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

import javax.mail.MessagingException;

import junit.framework.TestCase;

import org.apache.jsieve.commands.ThrowTestException;
import org.apache.jsieve.exception.SieveException;
import org.apache.jsieve.parser.generated.ParseException;
import org.apache.jsieve.utils.JUnitUtils;
import org.apache.jsieve.utils.SieveMailAdapter;

/**
 * Class HeaderTest
 */
public class HeaderTest extends TestCase {

    /**
     * Test for Test 'header'
     */
    public void testHeaderIsTrue() {
        boolean isTestPassed = false;
        String script = "if header :is \"X-Caffeine\" \"C8H10N4O2\" {throwTestException;}";
        try {
            SieveMailAdapter mail = (SieveMailAdapter) JUnitUtils.createMail();
            mail.getMessage().addHeader("X-Caffeine", "C8H10N4O2");
            JUnitUtils.interpret(mail, script);
        } catch (MessagingException e) {
        } catch (ThrowTestException.TestException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'header'
     */
    public void testHeaderCaseInsensitivity() {
        boolean isTestPassed = false;
        String script = "if header :is \"X-Caffeine\" \"C8H10N4O2\" {throwTestException;}";
        try {
            SieveMailAdapter mail = (SieveMailAdapter) JUnitUtils.createMail();
            mail.getMessage().addHeader("x-caffeine", "C8H10N4O2");
            JUnitUtils.interpret(mail, script);
        } catch (MessagingException e) {
        } catch (ThrowTestException.TestException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'header'
     */
    public void testHeaderIsTrueMulti1() {
        boolean isTestPassed = false;
        String script = "if header :is [\"X-Decaf\", \"X-Caffeine\"] \"C8H10N4O2\" {throwTestException;}";
        try {
            SieveMailAdapter mail = (SieveMailAdapter) JUnitUtils.createMail();
            mail.getMessage().addHeader("X-Caffeine", "C8H10N4O2");
            JUnitUtils.interpret(mail, script);
        } catch (MessagingException e) {
        } catch (ThrowTestException.TestException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'header'
     */
    public void testHeaderIsFalseMulti1() {
        boolean isTestPassed = false;
        String script = "if header :is [\"X-Decaf\", \"X-Caffeine\"] \"C8H10N4O2\" {throwTestException;}";
        try {
            SieveMailAdapter mail = (SieveMailAdapter) JUnitUtils.createMail();
            mail.getMessage().addHeader("X-Caffeine", "stuff");
            mail.getMessage().addHeader("X-Decaf", "more stuff");
            JUnitUtils.interpret(mail, script);
            isTestPassed = true;
        } catch (MessagingException e) {
        } catch (ThrowTestException.TestException e) {
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'header'
     */
    public void testHeaderIsTrueMulti2() {
        boolean isTestPassed = false;
        String script = "if header :is \"X-Caffeine\" [\"absent\", \"C8H10N4O2\"] {throwTestException;}";
        try {
            SieveMailAdapter mail = (SieveMailAdapter) JUnitUtils.createMail();
            mail.getMessage().addHeader("X-Caffeine", "C8H10N4O2");
            JUnitUtils.interpret(mail, script);
        } catch (MessagingException e) {
        } catch (ThrowTestException.TestException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'header'
     */
    public void testHeaderIsTrueMulti3() {
        boolean isTestPassed = false;
        String script = "if header :is [\"X-Decaf\", \"X-Caffeine\"] [\"absent\", \"C8H10N4O2\"] {throwTestException;}";
        try {
            SieveMailAdapter mail = (SieveMailAdapter) JUnitUtils.createMail();
            mail.getMessage().addHeader("X-Caffeine", "stuff");
            mail.getMessage().addHeader("X-Decaf", "C8H10N4O2");
            JUnitUtils.interpret(mail, script);
        } catch (MessagingException e) {
        } catch (ThrowTestException.TestException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'header'
     */
    public void testHeaderIsFalseValue() {
        boolean isTestPassed = false;
        String script = "if header :is \"X-Caffeine\" \"C8H10N4O2\" {throwTestException;}";
        try {
            SieveMailAdapter mail = (SieveMailAdapter) JUnitUtils.createMail();
            mail.getMessage().addHeader("X-Caffeine", "C8H10N4O");
            JUnitUtils.interpret(mail, script);
            isTestPassed = true;
        } catch (MessagingException e) {
        } catch (ThrowTestException.TestException e) {
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'header'
     */
    public void testHeaderIsFalseHeader() {
        boolean isTestPassed = false;
        String script = "if header :is \"X-Caffeine\" \"C8H10N4O2\" {throwTestException;}";
        try {
            SieveMailAdapter mail = (SieveMailAdapter) JUnitUtils.createMail();
            mail.getMessage().addHeader("X-Caffein", "C8H10N4O2");
            JUnitUtils.interpret(mail, script);
            isTestPassed = true;
        } catch (MessagingException e) {
        } catch (ThrowTestException.TestException e) {
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'header'
     */
    public void testHeaderContainsTrue() {
        boolean isTestPassed = false;
        String script = "if header :contains \"X-Caffeine\" \"C8H10\" {throwTestException;}";
        try {
            SieveMailAdapter mail = (SieveMailAdapter) JUnitUtils.createMail();
            mail.getMessage().addHeader("X-Caffeine", "C8H10N4O2");
            JUnitUtils.interpret(mail, script);
        } catch (MessagingException e) {
        } catch (ThrowTestException.TestException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'header'
     */
    public void testHeaderContainsFalse() {
        boolean isTestPassed = false;
        String script = "if header :is \"X-Caffeine\" \"C8H10N4O2\" {throwTestException;}";
        try {
            SieveMailAdapter mail = (SieveMailAdapter) JUnitUtils.createMail();
            mail.getMessage().addHeader("X-Caffeine", "izzy");
            JUnitUtils.interpret(mail, script);
            isTestPassed = true;
        } catch (MessagingException e) {
        } catch (ThrowTestException.TestException e) {
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'header'
     */
    public void testHeaderContainsNullTrue() {
        boolean isTestPassed = false;
        String script = "if header :contains \"X-Caffeine\" \"\" {throwTestException;}";
        try {
            SieveMailAdapter mail = (SieveMailAdapter) JUnitUtils.createMail();
            mail.getMessage().addHeader("X-Caffeine", null);
            JUnitUtils.interpret(mail, script);
        } catch (MessagingException e) {
        } catch (ThrowTestException.TestException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'header'
     */
    public void testHeaderIsNullFalse() {
        boolean isTestPassed = false;
        String script = "if header :is \"X-Caffeine\" \"\" {throwTestException;}";
        try {
            SieveMailAdapter mail = (SieveMailAdapter) JUnitUtils.createMail();
            mail.getMessage().addHeader("X-Caffeine", null);
            JUnitUtils.interpret(mail, script);
            isTestPassed = true;
        } catch (MessagingException e) {
        } catch (ThrowTestException.TestException e) {
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'header'
     */
    public void testHeaderMatchesTrue() {
        boolean isTestPassed = false;
        String script = "if header :matches \"X-Caffeine\" \"*10N?O2\" {throwTestException;}";
        try {
            SieveMailAdapter mail = (SieveMailAdapter) JUnitUtils.createMail();
            mail.getMessage().addHeader("X-Caffeine", "C8H10N4O2");
            JUnitUtils.interpret(mail, script);
        } catch (MessagingException e) {
        } catch (ThrowTestException.TestException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'header'
     */
    public void testHeaderMatchesFalse() {
        boolean isTestPassed = false;
        String script = "if header :matches \"X-Caffeine\" \"*10N?O2\" {throwTestException;}";
        try {
            SieveMailAdapter mail = (SieveMailAdapter) JUnitUtils.createMail();
            mail.getMessage().addHeader("X-Caffeine", "C8H10N4O3");
            JUnitUtils.interpret(mail, script);
            isTestPassed = true;
        } catch (MessagingException e) {
        } catch (ThrowTestException.TestException e) {
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        assertTrue(isTestPassed);
    }

    /**
     * Test for square brackets in matching headers When the "[" is in the first
     * char of the pattern it does not matches.
     * 
     * See http://issues.apache.org/jira/browse/JSIEVE-19
     */
    public void testSquareBracketsInMatch() {
        boolean isTestPassed = false;
        String script = "if header :matches \"X-Caffeine\" \"[test*\" {throwTestException;}";
        try {
            SieveMailAdapter mail = (SieveMailAdapter) JUnitUtils.createMail();
            mail.getMessage().addHeader("X-Caffeine", "[test] my subject");
            JUnitUtils.interpret(mail, script);
        } catch (MessagingException e) {
        } catch (ThrowTestException.TestException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        assertTrue(isTestPassed);
    }

    /**
     * Test for special char escaping: \\? is a ? and \\* is an *
     */
    public void testSpecialCharsEscapingInMatch() {
        boolean isTestPassed = false;
        String script = "if header :matches \"X-Caffeine\" \"my,\\\\,?,\\\\?,*,\\\\*,pattern\" {throwTestException;}";
        try {
            SieveMailAdapter mail = (SieveMailAdapter) JUnitUtils.createMail();
            mail.getMessage().addHeader("X-Caffeine",
                    "my,\\,x,?,foo,*,pattern");
            JUnitUtils.interpret(mail, script);
        } catch (MessagingException e) {
        } catch (ThrowTestException.TestException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        assertTrue(isTestPassed);
    }

    /**
     * Test for special char escaping: \\? is a ? and \\* is an *
     */
    public void testSpecialCharsEscapingInMatchFalse() {
        boolean isTestPassed = false;
        String script = "if header :matches \"X-Caffeine\" \"my,?,\\\\?,*,\\\\*,pattern\" {throwTestException;}";
        try {
            SieveMailAdapter mail = (SieveMailAdapter) JUnitUtils.createMail();
            mail.getMessage().addHeader("X-Caffeine",
                    "my,x,q,foo,*,pattern");
            JUnitUtils.interpret(mail, script);
            isTestPassed = true;
        } catch (MessagingException e) {
        } catch (ThrowTestException.TestException e) {
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        assertTrue(isTestPassed);
    }

}
