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

/**
 * Presents a high level reporting view of a Sieve node tree.
 * Familiarity with 
 * <a href='http://www.ietf.org/rfc/rfc3028.txt'>Sieve</a> is assumed 
 * (but not of the internals of the 
 * <a href='http://james.apache.org/jsieve/'>JSieve</a> implementation).
 * Anyone who requires a low level, <code>JSieve</code> specific view
 * should see {@link NodeHandler}
 * 
 * @see NodeTraverser
 * @see NodeHandler
 */
public interface SieveHandler {
    
    /**
     * Handles the start of a Sieve script.
     * @throws HaltTraversalException
     * @return this
     */
    public SieveHandler startScript() throws HaltTraversalException;
    
    /**
     * Handles the end of a Sieve script.
     * @throws HaltTraversalException
     * @return this
     */
    public SieveHandler endScript() throws HaltTraversalException;
    
    /**
     * Handles the start of a block.
     * @throws HaltTraversalException
     * @return this
     */
    public SieveHandler startBlock() throws HaltTraversalException;
    
    /**
     * Handles the end of a block.
     * @throws HaltTraversalException
     * @return this
     */
    public SieveHandler endBlock() throws HaltTraversalException;
    
    /**
     * Handles the start of a block of commands.
     * @throws HaltTraversalException
     * @return this
     */
    public SieveHandler startCommands() throws HaltTraversalException;
    
    /**
     * Handles the end of a block of commands.
     * @throws HaltTraversalException
     * @return this
     */
    public SieveHandler endCommands() throws HaltTraversalException;

    /**
     * Handles the start of a command.
     * @param commandName name identifying the command
     * @throws HaltTraversalException
     * @return this
     */
    public SieveHandler startCommand(String commandName) throws HaltTraversalException;
    
    /**
     * Handles the end of a command.
     * @param commandName name identifying the command
     * @throws HaltTraversalException
     * @return this
     */
    public SieveHandler endCommand(String commandName) throws HaltTraversalException;
    
    
    /**
     * Handles the start of a block of arguments.
     * @throws HaltTraversalException
     * @return this
     */
    public SieveHandler startArguments() throws HaltTraversalException;
    
    /**
     * Handles the end of a block of arguments.
     * @throws HaltTraversalException
     * @return this
     */
    public SieveHandler endArguments() throws HaltTraversalException;
    
    /**
     * Handles a tag argument.
     * Note that this supplies the identifier for the tag
     * (after the leading ':' has been stripped).
     * @param identifier not null
     * @throws HaltTraversalException
     * @return this
     */
    public SieveHandler argument(String identifier) throws HaltTraversalException;
    
    /**
     * Handler a numeric argument.
     * @param number not null
     * @throws HaltTraversalException
     * @return this
     */
    public SieveHandler argument(int number) throws HaltTraversalException;
    
    /**
     * Handles the start of an argument which is a list of strings.
     * @throws HaltTraversalException
     * @return this
     */
    public SieveHandler startStringListArgument() throws HaltTraversalException;
    
    /**
     * Handles the end of an argument which is a list of strings.
     * @throws HaltTraversalException
     * @return this
     */
    public SieveHandler endStringListArgument() throws HaltTraversalException;
    
    /**
     * One string from a list.
     * @param string not null
     * @throws HaltTraversalException
     * @return this
     */
    public SieveHandler listMember(String string) throws HaltTraversalException;
    
    /**
     * Handles the start of a list of tests.
     * @throws HaltTraversalException
     * @return this
     */
    public SieveHandler startTestList() throws HaltTraversalException;
    
    /**
     * Handles the end of a list of tests.
     * @throws HaltTraversalException
     * @return this
     */
    public SieveHandler endTestList() throws HaltTraversalException;
    
    /**
     * Handles the start of a test.
     * @param testName name identifying the test
     * @throws HaltTraversalException
     * @return this
     */
    public SieveHandler startTest(String testName) throws HaltTraversalException;
    
    /**
     * Handles the end of a test.
     * @param testName name identifying the test
     * @throws HaltTraversalException
     * @return this
     */
    public SieveHandler endTest(String testName) throws HaltTraversalException;

    /**
     * Convenience basic implementation.
     * All methods do nothing.
     */
    public abstract static class Base implements SieveHandler {

        public SieveHandler argument(int number) throws HaltTraversalException {
            return this;
        }

        public SieveHandler argument(String tag) throws HaltTraversalException {
            return this;
        }

        public SieveHandler endArguments() throws HaltTraversalException {
            return this;
        }

        public SieveHandler endBlock() throws HaltTraversalException {
            return this;
        }

        public SieveHandler endCommand(String commandName) throws HaltTraversalException {
            return this;
        }

        public SieveHandler endCommands() throws HaltTraversalException {
            return this;
        }

        public SieveHandler endScript() throws HaltTraversalException {
            return this;
        }

        public SieveHandler endStringListArgument() throws HaltTraversalException {
            return this;
        }

        public SieveHandler endTest(String testName) throws HaltTraversalException {
            return this;
        }

        public SieveHandler endTestList() throws HaltTraversalException {
            return this;
        }

        public SieveHandler listMember(String string) throws HaltTraversalException {
            return this;
        }

        public SieveHandler startArguments() throws HaltTraversalException {
            return this;
        }

        public SieveHandler startBlock() throws HaltTraversalException {
            return this;
        }

        public SieveHandler startCommand(String commandName) throws HaltTraversalException {
            return this;
        }

        public SieveHandler startCommands() throws HaltTraversalException {
            return this;
        }

        public SieveHandler startScript() throws HaltTraversalException {
            return this;
        }

        public SieveHandler startStringListArgument() throws HaltTraversalException {
            return this;
        }

        public SieveHandler startTest(String testName) throws HaltTraversalException {
            return this;
        }

        public SieveHandler startTestList() throws HaltTraversalException {
            return this;
        }
    }
}
