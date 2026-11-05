package org.apache.jsieve;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.jsieve.exception.SieveException;
import org.apache.jsieve.parser.generated.ASTargument;
import org.apache.jsieve.parser.generated.ASTarguments;
import org.apache.jsieve.parser.generated.ASTblock;
import org.apache.jsieve.parser.generated.ASTcommand;
import org.apache.jsieve.parser.generated.ASTcommands;
import org.apache.jsieve.parser.generated.ASTstart;
import org.apache.jsieve.parser.generated.ASTstring;
import org.apache.jsieve.parser.generated.ASTstring_list;
import org.apache.jsieve.parser.generated.ASTtest;
import org.apache.jsieve.parser.generated.ASTtest_list;
import org.apache.jsieve.parser.generated.SieveParserVisitor;
import org.apache.jsieve.parser.generated.SimpleNode;

import static org.apache.jsieve.Constants.*;

/****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 *   http://www.apache.org/licenses/LICENSE-2.0                 *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 ****************************************************************/

/**
 * Validates nodes visited. Some checks are more conveniently carried out what
 * then tree has already been constructed.
 */
public class SieveValidationVisitor implements SieveParserVisitor {

    private static Set<String> lowerCased(List<String> names) {
        return names.stream().map(name -> name.toLowerCase(Locale.ROOT)).collect(Collectors.toSet());
    }

    private final CommandManager commandManager;
    private final TestManager testManager;
    private final ComparatorManager comparatorManager;

    private final Set<String> declaredComparators;

    /** The commands and tests not in RFC 5228 itself, as the managers list them, lower-cased. */
    private final Set<String> extensionCommands;
    private final Set<String> extensionTests;

    /** The extensions declared by a <code>require</code>. */
    private final Set<String> declaredExtensions;
    
    private boolean requireAllowed = true;
    /** Is the visitor within a <code>require</code>? */
    private boolean isInRequire = false;
    /** Is the next argument expected to be a comparator name? */
    private boolean nextArgumentIsComparatorName = false;
    /** Is the visitor within a comparator name argument? */
    private boolean isInComparatorNameArgument = false;
    
    protected SieveValidationVisitor(final CommandManager commandManager,
            final TestManager testManager, final ComparatorManager comparatorManager) {
        super();
        this.commandManager = commandManager;
        this.testManager = testManager;
        this.comparatorManager = comparatorManager;
        declaredComparators = new HashSet<String>();
        extensionCommands = lowerCased(commandManager.getExtensions());
        extensionTests = lowerCased(testManager.getExtensions());
        declaredExtensions = new HashSet<String>();
    }

    public Object visit(SimpleNode node, Object data) throws SieveException {
        return visitNode(node, data);
    }

    private Object visitNode(SimpleNode node, Object data)
            throws SieveException {
        List children = new ArrayList(node.jjtGetNumChildren());
        node.childrenAccept(this, children);
        return data;
    }

    public Object visit(ASTstart node, Object data) throws SieveException {
        return visitNode(node, data);
    }

    public Object visit(ASTcommands node, Object data) throws SieveException {
        return visitNode(node, data);
    }

    public Object visit(ASTcommand node, Object data) throws SieveException {
        final String name = node.getName();
        commandManager.getCommand(name);
        extensionDeclared(name, extensionCommands);
        if ("require".equalsIgnoreCase(name)) {
            if (requireAllowed) {
                isInRequire = true;
            } else {
                throw new SieveException(
                        "'require' is only allowed before other commands");
            }
        } else {
            requireAllowed = false;
            isInRequire = false;
        }
        return visitNode(node, data);
    }

    public Object visit(ASTblock node, Object data) throws SieveException {
        return visitNode(node, data);
    }

    public Object visit(ASTarguments node, Object data) throws SieveException {
        // Reset test for explicitly required comparator types
        nextArgumentIsComparatorName = false;
        return visitNode(node, data);
    }

    public Object visit(ASTargument node, Object data) throws SieveException {
        final Object value = node.getValue();
        if (value == null) { 
            if (nextArgumentIsComparatorName) {
                // Mark enclosed string for check against required list
                isInComparatorNameArgument = true;
            }
            nextArgumentIsComparatorName = false;
        } else {
            if (value instanceof TagArgument) {
                final TagArgument tag = (TagArgument) value;
                nextArgumentIsComparatorName = tag.isComparator();
            } else {
                nextArgumentIsComparatorName = false;
            }
        }
        final Object result = visitNode(node, data);
        isInComparatorNameArgument = false;
        return result;
    }

    public Object visit(ASTtest node, Object data) throws SieveException {
        final String name = node.getName();
        testManager.getTest(name);
        extensionDeclared(name, extensionTests);
        return visitNode(node, data);
    }

    /**
     * An extension, a command or test not in RFC 5228 itself, is used only
     * once a <code>require</code> declared it (RFC 5228 section 2.10.5).
     */
    private void extensionDeclared(String name, Set<String> extensions) throws SieveException {
        final String lowerCaseName = name.toLowerCase(Locale.ROOT);
        if (extensions.contains(lowerCaseName) && !declaredExtensions.contains(lowerCaseName)) {
            throw new SieveException("\"" + name + "\" must be explicitly declared in a require statement.");
        }
    }

    public Object visit(ASTtest_list node, Object data) throws SieveException {
        return visitNode(node, data);
    }

    public Object visit(ASTstring node, Object data) throws SieveException {
        if (isInRequire) {
            requirements(node);
        }
        if (isInComparatorNameArgument) {
            comparatorNameArgument(node);
        }
        return visitNode(node, data);
    }

    private void comparatorNameArgument(ASTstring node) throws SieveException {
        final Object value = node.getValue();
        if (value != null && value instanceof String) {
            final String name = (String) value;
            // Comparators must either be declared (either implicitly or explicitly)
            if (!comparatorManager.isImplicitlyDeclared(name)) {
                if (!declaredComparators.contains(name)) {
                    // An unknown comparator and an undeclared one are two errors (RFC 5228 section 2.7.3)
                    if (!comparatorManager.isSupported(name)) {
                        throw new SieveException("Comparator \"" + name + "\" is not supported.");
                    }
                    // TODO: replace with better exception
                    throw new SieveException("Comparator \"" + name + "\" must be explicitly declared in a require statement.");
                }
            }
        }
    }

    private void requirements(ASTstring node) throws SieveException {
        final Object value = node.getValue();
        if (value != null && value instanceof String) {
            final String name = (String) value;
            if (name.startsWith(COMPARATOR_PREFIX)) {
                final String comparatorName = name.substring(COMPARATOR_PREFIX_LENGTH);
                if (comparatorManager.isSupported(comparatorName)) {
                    declaredComparators.add(comparatorName);
                } else {
//                  TODO: Replace with more finely grained exception
                    throw new SieveException("Comparator \"" + comparatorName + "\" is not supported.");
                }
            } else if (extensionCommands.contains(name) || extensionTests.contains(name)) {
                // Capability strings are case-sensitive (RFC 5228 section 6)
                declaredExtensions.add(name);
            } else {
                throw new SieveException("Capability \"" + name + "\" is not supported.");
            }
        }
    }

    public Object visit(ASTstring_list node, Object data) throws SieveException {
        return visitNode(node, data);
    }

}
