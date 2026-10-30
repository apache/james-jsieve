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

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * <p>Builds sieve handlers that convert nodes to a Sieve script.</p>
 * <h4>Usage</h4>
 * <ul>
 * <li>Create an instance</li>
 * <li>Configure using setters</li>
 * <li>{@link #build(Writer)} configured {@link SieveHandler}</li>
 * <li>Supply handler to {@link NodeTraverser}</li>
 * </ul>
 * <p>
 * Handler instances are fully configured when built. 
 * Changes to the factory configuration will not effect 
 * them.
 * </p>
 */
public class ToSieveHandlerFactory {

    /**
     * Builds a configured handler.
     * @return not null
     * @see NodeToSieveAdapter
     * @see NodeTraverser
     */
    public SieveHandler build(final Writer writer) {
        return new ToSieveHandler(writer);
    }
    
    /**
     * Thread safe worker.
     */
    private static final class ToSieveHandler extends SieveHandler.Base {
        /** An open test list: the test depth it was opened at, and how many tests it holds yet */
        private static final class TestList {
            private final int depth;
            private int tests;

            private TestList(int depth) {
                this.depth = depth;
            }
        }

        private final Writer writer;
        private final Deque<TestList> testLists;
        
        private int testDepth;
        private boolean firstTestInList;
        private boolean firstMemberInList;
        private boolean commandUsedBlock;
        private boolean commandAfterEndCommand;
     
        public ToSieveHandler(final Writer writer) {
            this.writer = writer;
            testLists = new ArrayDeque<TestList>();
            testDepth = 0;
            firstTestInList = false;
            firstMemberInList = true;
            commandUsedBlock = false;
            commandAfterEndCommand = false;
        }
        
        /** @see SieveHandler#endBlock() */
        @Override
        public SieveHandler endBlock() throws HaltTraversalException {
            commandUsedBlock = true;
            return append('}');
        }

        /** @see SieveHandler#startBlock() */
        @Override
        public SieveHandler startBlock() throws HaltTraversalException {
            space();
            return append('{');
        }
        
        /** */
        @Override
        public SieveHandler startCommand(String commandName) throws HaltTraversalException {
            if (commandAfterEndCommand) {
                space();
                commandAfterEndCommand = false;
            }
            return append(commandName);
        }
        
        /** */
        @Override
        public SieveHandler endCommand(String commandName) throws HaltTraversalException {
            if (!commandUsedBlock) {
                append(';');
            }
            commandAfterEndCommand = true;
            commandUsedBlock = false;
            return this;
        }
        
        /** @see SieveHandler#argument(String) */
        @Override
        public SieveHandler argument(String tag) throws HaltTraversalException {
            space();
            append(':');
            return append(tag);
        }

        /**
         * Appends white space.
         * @throws HaltTraversalException
         */
        private void space() throws HaltTraversalException {
            append(' ');
        }

        /** @see SieveHandler#argument(int) */
        @Override
        public SieveHandler argument(int number) throws HaltTraversalException {
            space();
            return append(Integer.toString(number));
        }

        /** @see SieveHandler#argument(long) */
        @Override
        public SieveHandler argument(long number) throws HaltTraversalException {
            space();
            return append(Long.toString(number));
        }
        
        /** @see SieveHandler#startStringListArgument() */
        @Override
        public SieveHandler startStringListArgument() throws HaltTraversalException {
            firstMemberInList = true;
            space();
            return append('[');
        }
        
        /** @see SieveHandler#endStringListArgument() */
        @Override
        public SieveHandler endStringListArgument() throws HaltTraversalException {
            return append(']');
        }
        
        /** @see SieveHandler#listMember(String) */
        @Override
        public SieveHandler listMember(String string) throws HaltTraversalException {
            if (!firstMemberInList) {
                append(", ");
            }
            firstMemberInList = false;
            append('"');
            for (int i=0;i<string.length();i++) {
                char next = string.charAt(i);
                if (next == '"' || next == '\\' || next =='\r' || next =='\f') {
                    append('\\');
                } 
                append(next);
            }
            return append('"');
        }
        
        
        /** @see SieveHandler#startTestList() */
        @Override
        public SieveHandler startTestList() throws HaltTraversalException {
            firstTestInList = true;
            testLists.push(new TestList(testDepth));
            return append('(');
        }
        
        /** @see SieveHandler#endTestList() */
        @Override
        public SieveHandler endTestList() throws HaltTraversalException {
            testLists.poll();
            return append(')');
        }

        /** @see SieveHandler#startTest(String) */
        @Override
        public SieveHandler startTest(String testName) throws HaltTraversalException {
            // a comma only between the tests of a test list, not before the test argument of a test
            final TestList testList = testLists.peek();
            if (testList != null && testList.depth == testDepth) {
                if (testList.tests > 0) {
                    append(",");
                }
                testList.tests++;
            }
            if (!firstTestInList) {
                space();
            }
            firstTestInList = false;
            testDepth++;
            return append(testName);
        }

        /** @see SieveHandler#endTest(String) */
        @Override
        public SieveHandler endTest(String testName) throws HaltTraversalException {
            testDepth--;
            return this;
        }

        /**
         * Appends the given sequence.
         * @return this
         * @throws HaltTraversalException when character cannot be written
         */
        private SieveHandler append(CharSequence characterSequence) throws HaltTraversalException {
            try {
                writer.append(characterSequence);
                return this;
            } catch (IOException e) {
                throw new HaltTraversalException(e);
            }
        }
        
        /**
         * Appends the given character.
         * @param character to be appended
         * @return this
         * @throws HaltTraversalException when character cannot be written
         */
        private SieveHandler append(char character) throws HaltTraversalException {
            try {
                writer.append(character);
                return this;
            } catch (IOException e) {
                throw new HaltTraversalException(e);
            }
        }
    }
}
