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

package org.apache.jsieve;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.jsieve.exception.SieveException;
import org.apache.jsieve.mail.MailAdapter;
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

/**
 * <p>
 * Class SieveParserVisitorImpl defines the behaviour for each visited node in
 * the Sieve grammar. Each method corresponds to a node type and is invoked when
 * a node of that type is evaluated.
 * </p>
 * 
 * <p>
 * In essence, this class translates between the nodes operated on by the JavaCC
 * generated classes and the Sieve classes operated upon by the Commands, Tests
 * and Comparators. A visit to the start node, ASTstart, triggers evaluation of
 * all of its descendants.
 * </p>
 * 
 * <p>
 * See https://javacc.dev.java.net/doc/JJTree.html for indepth information about
 * Visitor support.
 * </p>
 * 
 * <p>
 * <strong>Note</strong> that this class is not thread safe. It's use should be
 * restricted to a single thread for the duration of a visit.
 * </p>
 */
public class SieveParserVisitorImpl implements SieveParserVisitor {
    private final SieveContext context;

    /**
     * Constructor for NodeVisitor.
     */
    public SieveParserVisitorImpl(final SieveContext context) {
        super();
        this.context = context;
    }

    /**
     * Method visitChildren adds the children of the node to the passed List.
     * 
     * @param node
     * @param data -
     *            Assumes a List
     * @return Object - A List
     * @throws SieveException
     */
    @SuppressWarnings("unchecked")
    protected Object visitChildren(SimpleNode node, Object data)
            throws SieveException {
        List children = new ArrayList(node.jjtGetNumChildren());
        node.childrenAccept(this, children);
        ((List) data).addAll(children);
        return data;
    }

    /**
     * @see SieveParserVisitor#visit(ASTargument, Object)
     */
    @SuppressWarnings("unchecked")
    public Object visit(ASTargument node, Object data) throws SieveException {
        List<String> children = new ArrayList<String>(node.jjtGetNumChildren());
        Argument argument = null;

        if (null != node.getValue()) {
            argument = (Argument) node.getValue();
        } else {
            argument = new StringListArgument(((List) node.childrenAccept(this,
                    children)));
        }
        ((List) data).add(argument);

        return data;
    }

    /**
     * @see SieveParserVisitor#visit(ASTarguments, Object)
     */
    @SuppressWarnings("unchecked")
    public Object visit(ASTarguments node, Object data) throws SieveException {
        List children = new ArrayList(node.jjtGetNumChildren());
        children = ((List) node.childrenAccept(this, children));

        // Extract Tests and TestList from the children
        Iterator childrenIter = children.iterator();
        TestList testList = null;
        List<Argument> argList = new ArrayList<Argument>(children.size());
        while (childrenIter.hasNext()) {
            Object next = childrenIter.next();
            if (next instanceof Test)
                testList = new TestList((Test) next);
            else if (next instanceof TestList)
                testList = (TestList) next;
            else if (next instanceof Argument) {
                argList.add((Argument)next);
            } else {
                context.getLog().error("Expected an 'Argument' but was " + next);
            }
        }

        Arguments arguments = new Arguments(argList, testList);
        ((List) data).add(arguments);
        return data;
    }

    /**
     * @see SieveParserVisitor#visit(ASTblock, Object)
     */
    @SuppressWarnings("unchecked")
    public Object visit(ASTblock node, Object data) throws SieveException {
        // if (node.jjtGetNumChildren() != 1)
        // throw new ParseException("Expecting exactly one 1 child");
        List children = new ArrayList(node.jjtGetNumChildren());
        Commands commands = (Commands) ((List) node.childrenAccept(this,
                children)).get(0);
        Block block = new Block(commands);
        ((List) data).add(block);
        return data;
    }

    /**
     * @see SieveParserVisitor#visit(ASTcommand, Object)
     */
    @SuppressWarnings("unchecked")
    public Object visit(ASTcommand node, Object data) throws SieveException {
        List children = new ArrayList(node.jjtGetNumChildren());
        children = ((List) node.childrenAccept(this, children));

        // Extract the Arguments and Block from the children
        Iterator childrenIter = children.iterator();
        Arguments arguments = null;
        Block block = null;
        while (childrenIter.hasNext()) {
            Object next = childrenIter.next();
            if (next instanceof Arguments)
                arguments = (Arguments) next;
            else if (next instanceof Block)
                block = (Block) next;
        }

        context.setCoordinate(node.getCoordinate());
        final ScriptCoordinate coordinate = context.getCoordinate();
        Command command = new Command(node.getName(), arguments, block,
                coordinate);
        ((List) data).add(command);
        return data;
    }

    /**
     * @see SieveParserVisitor#visit(ASTcommands, Object)
     */
    @SuppressWarnings("unchecked")
    public Object visit(ASTcommands node, Object data) throws SieveException {
        List<Command> children = new ArrayList<Command>(node.jjtGetNumChildren());
        Commands commands = new Commands(((List) node.childrenAccept(this,
                children)));
        ((List) data).add(commands);
        return data;
    }

    /**
     * @see SieveParserVisitor#visit(ASTstart, Object)
     */
    public Object visit(ASTstart node, Object data) throws SieveException {
        // The data object must be the MailAdapter to process
        if (!(data instanceof MailAdapter))
            throw new SieveException("Expecting an instance of "
                    + MailAdapter.class.getName()
                    + " as data, received an instance of "
                    + (data == null ? "<null>" : data.getClass().getName())
                    + ".");

        // Start is an implicit Block
        // There will be one child, an instance of Commands
        List children = new ArrayList(node.jjtGetNumChildren());
        Commands commands = (Commands) ((List) node.childrenAccept(this,
                children)).get(0);
        Block block = new Block(commands);
        context.setCoordinate(node.getCoordinate());
        // Answer the result of executing the Block
        return block.execute((MailAdapter) data, context);
    }

    /**
     * @see SieveParserVisitor#visit(ASTstring_list, Object)
     */
    public Object visit(ASTstring_list node, Object data) throws SieveException {
        return visitChildren(node, data);
    }

    /**
     * @see SieveParserVisitor#visit(ASTstring, Object)
     */
    @SuppressWarnings("unchecked")
    public Object visit(ASTstring node, Object data) {
        // Strings are always surround by double-quotes
        final String value = (String) node.getValue();
        // A String is terminal, add it
        ((List) data).add(value);
        return data;
    }

    /**
     * @see SieveParserVisitor#visit(ASTtest_list, Object)
     */
    @SuppressWarnings("unchecked")
    public Object visit(ASTtest_list node, Object data) throws SieveException {
        // return visitChildren(node, data);
        List<Test> children = new ArrayList<Test>(node.jjtGetNumChildren());
        TestList testList = new TestList(((List<Test>) node.childrenAccept(this,
                children)));
        ((List) data).add(testList);
        return data;
    }

    /**
     * @see SieveParserVisitor#visit(ASTtest, Object)
     */
    @SuppressWarnings("unchecked")
    public Object visit(ASTtest node, Object data) throws SieveException {
        List children = new ArrayList(node.jjtGetNumChildren());
        children = ((List) node.childrenAccept(this, children));

        // Extract the Arguments from the children
        Iterator childrenIter = children.iterator();
        Arguments arguments = null;
        while (childrenIter.hasNext()) {
            Object next = childrenIter.next();
            if (next instanceof Arguments)
                arguments = (Arguments) next;
        }

        context.setCoordinate(node.getCoordinate());
        Test test = new Test(node.getName(), arguments);
        ((List) data).add(test);
        return data;
    }

    /**
     * @see SieveParserVisitor#visit(SimpleNode, Object)
     */
    public Object visit(SimpleNode node, Object data) throws SieveException {
        return visitChildren(node, data);
    }

}
