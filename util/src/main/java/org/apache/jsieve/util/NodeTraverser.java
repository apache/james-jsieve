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
import org.apache.jsieve.parser.generated.Node;
import org.apache.jsieve.parser.generated.SieveParserVisitor;
import org.apache.jsieve.parser.generated.SimpleNode;

/**
 * Traverses nodes.
 * Once instance can be safely shared between threads.
 */
public class NodeTraverser {
    
    /**
     * Traverses the tree structure rooted at the given node.
     * The nodes contained are reported to the handler.
     * @param root not null
     * @param handler not null
     * @throws SieveException when traversal fails
     * @throws HaltTraversalException when traversal is halted by handler
     */
    public void traverse(final NodeHandler handler, final Node root) throws SieveException {
        final TraversalWorker worker = new TraversalWorker(handler);
        handler.start();
        root.jjtAccept(worker, null);
        handler.end();
    }
    
    /**
     * Traverses the tree structure rooted at the given node.
     * The nodes contained are reported to the handler.
     * @param root not null
     * @param handler not null
     * @throws SieveException when traversal fails
     * @throws HaltTraversalException when traversal is halted by handler
     */
    public void traverse(final SieveHandler handler, final Node root) throws SieveException {
        traverse(new NodeToSieveAdapter(handler), root);
    }
  

    /**
     * <p>Traverses a nodal tree structure.
     * An inner worker:
     * </p>
     * <ul>
     * <li>Allows a more fluent public API</li>
     * <li>Isolated the monotheaded code</li>
     * </ul>
     */
    private static final class TraversalWorker implements SieveParserVisitor {
        
        private final NodeHandler handler;
        
        /**
         * Constructs a traversal worker.
         * @param handler not null
         */
        public TraversalWorker(final NodeHandler handler) {
            super();
            this.handler = handler;
        }
        
        public Object visit(SimpleNode node, Object data) throws SieveException {
            handler.start(node);
            node.childrenAccept(this, null);
            handler.end(node);
            return null;
        }
    
        public Object visit(ASTstart node, Object data) throws SieveException {
            handler.start(node);
            node.childrenAccept(this, null);
            handler.end(node);
            return null;
        }
    
        public Object visit(ASTcommands node, Object data) throws SieveException {
            handler.start(node);
            node.childrenAccept(this, null);
            handler.end(node);
            return null;
        }
    
        public Object visit(ASTcommand node, Object data) throws SieveException {
            handler.start(node);
            node.childrenAccept(this, null);
            handler.end(node);
            return null;
        }
    
        public Object visit(ASTblock node, Object data) throws SieveException {
            handler.start(node);
            node.childrenAccept(this, null);
            handler.end(node);
            return null;
        }
    
        public Object visit(ASTarguments node, Object data) throws SieveException {
            handler.start(node);
            node.childrenAccept(this, null);
            handler.end(node);
            return null;
        }
    
        public Object visit(ASTargument node, Object data) throws SieveException {
            handler.start(node);
            node.childrenAccept(this, null);
            handler.end(node);
            return null;
        }
    
        public Object visit(ASTtest node, Object data) throws SieveException {
            handler.start(node);
            node.childrenAccept(this, null);
            handler.end(node);
            return null;
        }
    
        public Object visit(ASTtest_list node, Object data) throws SieveException {
            handler.start(node);
            node.childrenAccept(this, null);
            handler.end(node);
            return null;
        }
    
        public Object visit(ASTstring node, Object data) throws SieveException {
            handler.start(node);
            node.childrenAccept(this, null);
            handler.end(node);
            return null;
        }
    
        public Object visit(ASTstring_list node, Object data) throws SieveException {
            handler.start(node);
            node.childrenAccept(this, null);
            handler.end(node);
            return null;
        }
    }
}
