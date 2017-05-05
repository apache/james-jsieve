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
import org.apache.jsieve.exception.SieveException;
import org.apache.jsieve.parser.generated.ParseException;
import org.apache.jsieve.utils.JUnitUtils;
import org.apache.jsieve.utils.SieveMailAdapter;
import org.junit.Assert;
import org.junit.Test;

import javax.mail.MessagingException;

/**
 * Class HeaderTest
 */
public class HeaderTest {

    /**
     * Test for Test 'header'
     */
    @Test
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
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'header'
     */
    @Test(expected = ThrowTestException.TestException.class)
    public void testFoldedEncodedHeader() throws Exception {
        String script = "if header :is \"To\" \"Benoît TELLIER <tellier@linagora.com>\" {throwTestException;}";


        SieveMailAdapter mail = (SieveMailAdapter) JUnitUtils.createMail();
        mail.getMessage().addHeader("To", "=?UTF-8?Q?Beno=c3=aet_TELLIER?=\r\n" +
            " <tellier@linagora.com>");

        JUnitUtils.interpret(mail, script);
    }

    /**
     * Test for Test 'header'
     */
    @Test
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
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'header'
     */
    @Test
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
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'header'
     */
    @Test
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
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'header'
     */
    @Test
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
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'header'
     */
    @Test
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
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'header'
     */
    @Test
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
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'header'
     */
    @Test
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
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'header'
     */
    @Test
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
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'header'
     */
    @Test
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
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'header'
     */
    @Test
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
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'header'
     */
    @Test
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
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'header'
     */
    @Test
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
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'header'
     */
    @Test
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
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for square brackets in matching headers When the "[" is in the first
     * char of the pattern it does not matches.
     * <p/>
     * See http://issues.apache.org/jira/browse/JSIEVE-19
     */
    @Test
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
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for special char escaping: \\? is a ? and \\* is an *
     */
    @Test
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
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for special char escaping: \\? is a ? and \\* is an *
     */
    @Test
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
        Assert.assertTrue(isTestPassed);
    }

}
