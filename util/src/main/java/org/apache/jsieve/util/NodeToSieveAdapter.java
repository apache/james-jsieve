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
package org.apache.jsieve.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jsieve.NumberArgument;
import org.apache.jsieve.TagArgument;
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
import org.apache.jsieve.parser.generated.SimpleNode;

/**
 * Adapters low level {@link NodeHandler} output into a
 * high level {@link SieveHandler}.
 */
public class NodeToSieveAdapter implements NodeHandler {

    private static final Log LOG = LogFactory.getLog(NodeToSieveAdapter.class);
    
    private final SieveHandler handler;
    
    /**
     * Constructs an adapter to the given {@link SieveHandler}.
     * @param handler not null
     * @throws NullPointerException when handler is null
     */
    public NodeToSieveAdapter(final SieveHandler handler) {
        super();
        // Hard to debug a null pointer during parsing
        if (handler == null) {
            throw new NullPointerException("Handler must not be null");
        }
        this.handler = handler;
    }


    public void start() throws HaltTraversalException {
//      Ignore
    }
    
    public void end() throws HaltTraversalException {
//      Ignore
    }

    public void end(SimpleNode node) throws HaltTraversalException {
//      Ignore
    }

    public void end(ASTstart node) throws HaltTraversalException {
        handler.endScript();
    }

    public void end(ASTcommands node) throws HaltTraversalException {
        handler.endCommands();
    }

    public void end(ASTcommand node) throws HaltTraversalException {
        handler.endCommand(node.getName());
    }

    public void end(ASTblock node) throws HaltTraversalException {
        handler.endBlock();
    }

    public void end(ASTarguments node) throws HaltTraversalException {
        handler.endArguments();
    }

    public void end(ASTargument node) throws HaltTraversalException {
        // Processed in start
    }

    public void end(ASTtest node) throws HaltTraversalException {
        final String name = node.getName();
        handler.endTest(name);
    }

    public void end(ASTtest_list node) throws HaltTraversalException {
        handler.endTestList();
    }

    public void end(ASTstring node) throws HaltTraversalException {
        // Process ASTstring on start
    }

    public void end(ASTstring_list node) throws HaltTraversalException {
        handler.endStringListArgument();
    }


    public void start(SimpleNode node) throws HaltTraversalException {
        // Ignore
    }

    public void start(ASTstart node) throws HaltTraversalException {
        handler.startScript();
    }

    public void start(ASTcommands node) throws HaltTraversalException {
        handler.startCommands();
    }

    public void start(ASTcommand node) throws HaltTraversalException {
        handler.startCommand(node.getName());
    }

    public void start(ASTblock node) throws HaltTraversalException {
        handler.startBlock();
    }

    public void start(ASTarguments node) throws HaltTraversalException {
        handler.startArguments();
    }

    public void start(ASTargument node) throws HaltTraversalException {
        final Object value = node.getValue();
        if (value == null) {
            LOG.debug("Ignoring null argument");
        } else if (value instanceof NumberArgument) {
            final NumberArgument numberArgument = (NumberArgument) value;
            Integer integer = numberArgument.getInteger();
            if (integer == null) {
                LOG.debug("Ignoring null numeric argument");
            } else {
                final int number = integer.intValue();
                handler.argument(number);
            }
        } else if (value instanceof TagArgument) {
            final TagArgument tagArgument = (TagArgument) value;
            final String tag = tagArgument.getTag();
            // tag = ":" identifier
            // handlers are only interesting in the identifier for the tag
            final String identifier;
            if (tag.charAt(0) == ':') {
                identifier = tag.substring(1);
            } else {
                identifier = tag;
            }
            handler.argument(identifier);
        }
    }

    public void start(ASTtest node) throws HaltTraversalException {
        final String name = node.getName();
        handler.startTest(name);
    }

    public void start(ASTtest_list node) throws HaltTraversalException {
        handler.startTestList();
    }

    public void start(ASTstring node) throws HaltTraversalException {
        final String string =(String) node.getValue();
        handler.listMember(string);
    }

    public void start(ASTstring_list node) throws HaltTraversalException {
        handler.startStringListArgument();
    }

}
