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

import org.apache.jsieve.commands.ThrowTestException;
import org.apache.jsieve.exception.CommandException;
import org.apache.jsieve.exception.SieveException;
import org.apache.jsieve.exception.SyntaxException;
import org.apache.jsieve.parser.generated.ParseException;
import org.apache.jsieve.utils.JUnitUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Class <code>ConditionTest</code> tests the conditional commands if, elsif
 * and else.
 */
public class ConditionTest {

    /**
     * Test for Command 'if' with an argument of 'true'
     */
    @org.junit.Test
    public void testIfTrue() {
        boolean isTestPassed = false;
        String script = "if true {throwTestException;}";

        try {
            JUnitUtils.interpret(JUnitUtils.createMail(), script);
        } catch (ThrowTestException.TestException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Command 'if' with an argument of 'false'
     */
    @Test
    public void testIfFalse() {
        boolean isTestPassed = false;
        String script = "if false {stop;} throwTestException;";

        try {
            JUnitUtils.interpret(JUnitUtils.createMail(), script);
        } catch (ThrowTestException.TestException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Command 'elsif' with an argument of 'true'
     */
    @Test
    public void testElsifTrue() {
        boolean isTestPassed = false;
        String script = "if false {stop;} elsif true {throwTestException;}";

        try {
            JUnitUtils.interpret(JUnitUtils.createMail(), script);
        } catch (ThrowTestException.TestException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Command 'elsif' with an argument of 'false'
     */
    @Test
    public void testElsifFalse() {
        boolean isTestPassed = false;
        String script = "if false {stop;} elsif false {stop;} throwTestException;";

        try {
            JUnitUtils.interpret(JUnitUtils.createMail(), script);
        } catch (ThrowTestException.TestException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for nested Command 'elsif' with an argument of 'true'
     */
    @Test
    public void testElsifFalseElsifTrue() {
        boolean isTestPassed = false;
        String script = "if false {stop;} elsif false {stop;} elsif true {throwTestException;}";

        try {
            JUnitUtils.interpret(JUnitUtils.createMail(), script);
        } catch (ThrowTestException.TestException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Command 'else' after 'elseif'
     */
    @Test
    public void testElsifFalseElse() {
        boolean isTestPassed = false;
        String script = "if false {stop;} elsif false {stop;} else {throwTestException;}";

        try {
            JUnitUtils.interpret(JUnitUtils.createMail(), script);
        } catch (ThrowTestException.TestException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Command 'else'
     */
    @Test
    public void testElse() {
        boolean isTestPassed = false;
        String script = "if false {stop;} else {throwTestException;}";

        try {
            JUnitUtils.interpret(JUnitUtils.createMail(), script);
        } catch (ThrowTestException.TestException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Command 'else' out of sequence
     */
    @Test
    public void testOutOfSequenceElse() {
        boolean isTestPassed = false;
        String script = "else {stop;}";

        try {
            JUnitUtils.interpret(JUnitUtils.createMail(), script);
        } catch (CommandException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Command 'elsif' out of sequence
     */
    @Test
    public void testOutOfSequenceElsif() {
        boolean isTestPassed = false;
        String script = "elsif {stop;}";

        try {
            JUnitUtils.interpret(JUnitUtils.createMail(), script);
        } catch (SyntaxException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Command 'if' without a corresponding Block
     */
    @Test
    public void testIfMissingBlock() {
        boolean isTestPassed = false;
        String script = "if true stop;";

        try {
            JUnitUtils.interpret(JUnitUtils.createMail(), script);
        } catch (SyntaxException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Command 'if' without a test
     */
    @Test
    public void testIfMissingTest() {
        boolean isTestPassed = false;
        String script = "if {stop;}";

        try {
            JUnitUtils.interpret(JUnitUtils.createMail(), script);
        } catch (SyntaxException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Command 'if' without a test
     */
    @Test
    public void testElsifMissingTest() {
        boolean isTestPassed = false;
        String script = "if false {stop;} elsif {stop;}";

        try {
            JUnitUtils.interpret(JUnitUtils.createMail(), script);
        } catch (SyntaxException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Command 'elsif' without a corresponding Block
     */
    @Test
    public void testElsifMissingBlock() {
        boolean isTestPassed = false;
        String script = "if false {stop;} elsif true stop;";

        try {
            JUnitUtils.interpret(JUnitUtils.createMail(), script);
        } catch (SyntaxException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Command 'else' without a corresponding Block
     */
    @Test
    public void testElseMissingBlock() {
        boolean isTestPassed = false;
        String script = "if false {stop;} else stop;";

        try {
            JUnitUtils.interpret(JUnitUtils.createMail(), script);
        } catch (SyntaxException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Command 'if' nested in a block
     */
    @Test
    public void testNestedIf() {
        boolean isTestPassed = false;
        String script = "if true {if true {throwTestException;}}";

        try {
            JUnitUtils.interpret(JUnitUtils.createMail(), script);
        } catch (ThrowTestException.TestException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Command 'else' out of sequence nested in a block
     */
    @Test
    public void testNestedOutOfSequenceElse() {
        boolean isTestPassed = false;
        String script = "if true {else {stop;}}";

        try {
            JUnitUtils.interpret(JUnitUtils.createMail(), script);
        } catch (CommandException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Command 'elsif' out of sequence nested in a block
     */
    @Test
    public void testNestedOutOfSequenceElsif() {
        boolean isTestPassed = false;
        String script = "if true {elsif true {stop;}}";

        try {
            JUnitUtils.interpret(JUnitUtils.createMail(), script);
        } catch (CommandException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

}
