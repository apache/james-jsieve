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
 * Presents a low level reporting view of a Sieve node tree.
 * Familiarity with the 
 * <a href='http://james.apache.org/jsieve/'>JSieve</a> implementation is assumed.
 * Anyone requiring a high level view should see {@link SieveHandler}.
 * 
 * @see NodeTraverser
 * @see SieveHandler
 */
public interface NodeHandler {
    
    /**
     * Starts a tree traversal.
     * @throws HaltTraversalException
     */
    public void start() throws HaltTraversalException;
    
    /**
     * Ends a tree traveral.
     * @throws HaltTraversalException
     */
    public void end() throws HaltTraversalException;
    
    /**
     * Starts traversal of given node.
     * @param node not null
     * @throws HaltTraversalException
     */
    public void start(SimpleNode node) throws HaltTraversalException;
    
    /**
     * Ends traversal of given node.
     * @param node not null
     * @throws HaltTraversalException
     */
    public void end(SimpleNode node) throws HaltTraversalException;
    
    /**
     * Starts traversal of given node.
     * @param node not null
     * @throws HaltTraversalException
     */
    public void start(ASTstart node) throws HaltTraversalException;
    
    /**
     * Ends traversal of given node.
     * @param node not null
     * @throws HaltTraversalException
     */
    public void end(ASTstart node) throws HaltTraversalException;

    /**
     * Starts traversal of given node.
     * @param node not null
     * @throws HaltTraversalException
     */
    public void start(ASTcommands node) throws HaltTraversalException;
    
    /**
     * Ends traversal of given node.
     * @param node not null
     * @throws HaltTraversalException
     */
    public void end(ASTcommands node) throws HaltTraversalException;

    /**
     * Starts traversal of given node.
     * @param node not null
     * @throws HaltTraversalException
     */
    public void start(ASTcommand node) throws HaltTraversalException;
    
    /**
     * Ends traversal of given node.
     * @param node not null
     * @throws HaltTraversalException
     */
    public void end(ASTcommand node) throws HaltTraversalException;

    /**
     * Starts traversal of given node.
     * @param node not null
     * @throws HaltTraversalException
     */
    public void start(ASTblock node) throws HaltTraversalException;
    
    /**
     * Ends traversal of given node.
     * @param node not null
     * @throws HaltTraversalException
     */
    public void end(ASTblock node) throws HaltTraversalException;

    /**
     * Starts traversal of given node.
     * @param node not null
     * @throws HaltTraversalException
     */
    public void start(ASTarguments node) throws HaltTraversalException;
    
    /**
     * Ends traversal of given node.
     * @param node not null
     * @throws HaltTraversalException
     */
    public void end(ASTarguments node) throws HaltTraversalException;

    /**
     * Starts traversal of given node.
     * @param node not null
     * @throws HaltTraversalException
     */
    public void start(ASTargument node) throws HaltTraversalException;
    
    /**
     * Ends traversal of given node.
     * @param node not null
     * @throws HaltTraversalException
     */
    public void end(ASTargument node) throws HaltTraversalException;

    /**
     * Starts traversal of given node.
     * @param node not null
     * @throws HaltTraversalException
     */
    public void start(ASTtest node) throws HaltTraversalException;
    
    /**
     * Ends traversal of given node.
     * @param node not null
     * @throws HaltTraversalException
     */
    public void end(ASTtest node) throws HaltTraversalException;

    /**
     * Starts traversal of given node.
     * @param node not null
     * @throws HaltTraversalException
     */
    public void start(ASTtest_list node) throws HaltTraversalException;
    
    /**
     * Ends traversal of given node.
     * @param node not null
     * @throws HaltTraversalException
     */
    public void end(ASTtest_list node) throws HaltTraversalException;

    /**
     * Starts traversal of given node.
     * @param node not null
     * @throws HaltTraversalException
     */
    public void start(ASTstring node) throws HaltTraversalException;
    
    /**
     * Ends traversal of given node.
     * @param node not null
     * @throws HaltTraversalException
     */
    public void end(ASTstring node) throws HaltTraversalException;

    /**
     * Starts traversal of given node.
     * @param node not null
     * @throws HaltTraversalException
     */
    public void start(ASTstring_list node) throws HaltTraversalException;
    
    /**
     * Ends traversal of given node.
     * @param node not null
     * @throws HaltTraversalException
     */
    public void end(ASTstring_list node) throws HaltTraversalException;
}
