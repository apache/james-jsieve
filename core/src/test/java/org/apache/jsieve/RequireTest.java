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

import org.apache.jsieve.exception.SieveException;
import org.apache.jsieve.exception.SyntaxException;
import org.apache.jsieve.parser.generated.ParseException;
import org.apache.jsieve.utils.JUnitUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Class RequireTest
 */
public class RequireTest {

    /**
     * Test for Command 'require' with a single command that is present
     */
    @org.junit.Test
    public void testSingleCommandSatisfied() {
        boolean isTestPassed = false;
        String script = "require \"if\";";

        try {
            JUnitUtils.interpret(JUnitUtils.createMail(), script);
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Command 'require' with a single test that is present
     */
    @Test
    public void testSingleTestSatisfied() throws Exception {
        String script = "require \"true\";";
        JUnitUtils.interpret(JUnitUtils.createMail(), script);
    }

    /**
     * Test for Command 'require' with multiple commands that are present
     */
    @Test
    public void testMultipleCommandSatisfied() throws Exception {
        String script = "require [\"if\", \"elsif\", \"else\"];";
        JUnitUtils.interpret(JUnitUtils.createMail(), script);
    }

    /**
     * Test for Command 'require' with multiple tests that are present
     */
    @Test
    public void testMultipleTestSatisfied() throws Exception {
        String script = "require [\"true\", \"false\", \"not\"];";
        JUnitUtils.interpret(JUnitUtils.createMail(), script);
    }

    /**
     * Test for Command 'require' with a single command that is absent
     */
    @Test
    public void testSingleCommandUnsatisfied() throws Exception {
        boolean isTestPassed = false;
        String script = "require \"absent\";";

        try {
            JUnitUtils.interpret(JUnitUtils.createMail(), script);
        } catch (ParseException e) {
            isTestPassed = true;
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Command 'require' with a single test that is absent
     */
    @Test
    public void testSingleTestUnsatisfied() throws Exception {
        boolean isTestPassed = false;
        String script = "require \"absent\";";

        try {
            JUnitUtils.interpret(JUnitUtils.createMail(), script);
        } catch (ParseException e) {
            isTestPassed = true;
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Command 'require' for missing argument
     */
    @Test
    public void testMissingArgument() throws Exception {
        boolean isTestPassed = false;
        String script = "require;";

        try {
            JUnitUtils.interpret(JUnitUtils.createMail(), script);
        } catch (SyntaxException e) {
            isTestPassed = true;
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Command 'require' for extra argument
     */
    @Test
    public void testExtraArgument() throws Exception {
        boolean isTestPassed = false;
        String script = "require \"if\" 1;";

        try {
            JUnitUtils.interpret(JUnitUtils.createMail(), script);
        } catch (SyntaxException e) {
            isTestPassed = true;
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Command 'require' rejecting Blocks
     */
    @Test
    public void testRejectBlock() throws Exception {
        boolean isTestPassed = false;
        String script = "require \"if\" {stop;}";

        try {
            JUnitUtils.interpret(JUnitUtils.createMail(), script);
        } catch (SyntaxException e) {
            isTestPassed = true;
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Command 'require' after a Command
     */
    @Test
    public void testInterveningCommand() throws Exception {
        boolean isTestPassed = false;
        String script = "fileinto \"someplace\"; require \"fileinto\";";

        try {
            JUnitUtils.interpret(JUnitUtils.createMail(), script);
        } catch (ParseException e) {
            isTestPassed = true;
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Command 'require' rejecting invalid arguments
     */
    @Test
    public void testRejectInvalidArgument() throws Exception {
        boolean isTestPassed = false;
        String script = "require 1 ;";

        try {
            JUnitUtils.interpret(JUnitUtils.createMail(), script);
        } catch (SyntaxException e) {
            isTestPassed = true;
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Command 'require' with a multiple commands of which one is
     * absent
     */
    @Test
    public void testMultipleCommandsUnsatisfied() throws Exception {
        boolean isTestPassed = false;
        String script = "require [\"if\", \"elsif\", \"absent\"];";

        try {
            JUnitUtils.interpret(JUnitUtils.createMail(), script);
        } catch (ParseException e) {
            isTestPassed = true;
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Command 'require' with a multiple tests of which one is absent
     */
    @Test
    public void testMultipleTestsUnsatisfied() throws Exception {
        boolean isTestPassed = false;
        String script = "require [\"true\", \"false\", \"absent\"];";

        try {
            JUnitUtils.interpret(JUnitUtils.createMail(), script);
        } catch (ParseException e) {
            isTestPassed = true;
        }
        Assert.assertTrue(isTestPassed);
    }

}
