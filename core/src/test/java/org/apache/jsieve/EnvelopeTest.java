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
import org.apache.jsieve.exception.SyntaxException;
import org.apache.jsieve.parser.generated.ParseException;
import org.apache.jsieve.utils.JUnitUtils;
import org.apache.jsieve.utils.SieveEnvelopeMailAdapter;
import org.junit.Assert;
import org.junit.Test;

/**
 * Class EnvelopeTest
 */
public class EnvelopeTest {

    /**
     * Test for Test 'envelope'
     */
    @org.junit.Test
    public void testIfEnvelopeAllIsTrue() {
        boolean isTestPassed = false;
        String script = "require \"envelope\"; if envelope :all :is \"From\" \"user@domain\" {throwTestException;}";
        try {
            SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("user@domain");
            JUnitUtils.interpret(mail, script);
        } catch (ThrowTestException.TestException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    @Test
    public void testCaseInsensitiveEnvelopeName() {
        boolean isTestPassed = false;
        String script = "require \"envelope\"; if envelope :all :is \"from\" \"user@domain\" {throwTestException;}";
        try {
            SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("user@domain");
            JUnitUtils.interpret(mail, script);
        } catch (ThrowTestException.TestException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    @Test
    public void testOctetComparatorTrue() {
        boolean isTestPassed = false;
        String script = "require \"envelope\"; if envelope :comparator \"i;octet\" :all :is \"From\" \"uSeR@dOmAiN\" {throwTestException;}";
        try {
            SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("uSeR@dOmAiN");
            JUnitUtils.interpret(mail, script);
        } catch (ThrowTestException.TestException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    @Test
    public void testOctetComparatorFalse() {
        boolean isTestPassed = false;
        String script = "require \"envelope\"; if envelope :comparator \"i;octet\" :all :is \"From\" \"uSeR@dOmAiN\" {throwTestException;}";
        try {
            SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("user@domain");
            JUnitUtils.interpret(mail, script);
            isTestPassed = true;
        } catch (ThrowTestException.TestException e) {
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    @Test
    public void testAsciiComparatorTrue() {
        boolean isTestPassed = false;
        String script = "require \"envelope\"; if envelope :comparator \"i;ascii-casemap\" :all :is \"From\" \"uSeR@dOmAiN\" {throwTestException;}";
        try {
            SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("user@domain");
            JUnitUtils.interpret(mail, script);
        } catch (ThrowTestException.TestException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    @Test
    public void testAsciiComparatorFalse() {
        boolean isTestPassed = false;
        String script = "require \"envelope\"; if envelope :comparator \"i;ascii-casemap\" :all :is \"From\" \"uSeR@dOmAiN\" {throwTestException;}";
        try {
            SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("user@domain1");
            JUnitUtils.interpret(mail, script);
            isTestPassed = true;
        } catch (ThrowTestException.TestException e) {
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    @Test
    public void testIfEnvelopeAllIsMultiTrue1() {
        boolean isTestPassed = false;
        String script = "require \"envelope\"; if envelope :all :is [\"From\", \"To\"] \"user@domain\" {throwTestException;}";
        try {
            SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("user@domain");
            mail.setEnvelopeTo("user@domain");
            JUnitUtils.interpret(mail, script);
        } catch (ThrowTestException.TestException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    @Test
    public void testIfEnvelopeAllIsMultiTrue2() {
        boolean isTestPassed = false;
        String script = "require \"envelope\"; if envelope :all :is [\"From\", \"To\"] [\"user@domain\", \"tweety@pie\"] {throwTestException;}";
        try {
            SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("user@domain");
            mail.setEnvelopeTo("user@domain");
            JUnitUtils.interpret(mail, script);
        } catch (ThrowTestException.TestException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    @Test
    public void testIfEnvelopeAllIsMultiTrue3() {
        boolean isTestPassed = false;
        String script = "require \"envelope\"; if envelope :all :is [\"From\", \"To\"] [\"user@domain\", \"tweety@pie\"] {throwTestException;}";
        try {
            SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("user@domain");
            mail.setEnvelopeTo("tweety@pie");
            JUnitUtils.interpret(mail, script);
        } catch (ThrowTestException.TestException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    @Test
    public void testIfEnvelopeAllIsMultiTrue4() {
        boolean isTestPassed = false;
        String script = "require \"envelope\"; if envelope :all :is [\"From\", \"To\"] [\"user@domain\", \"tweety@pie\"] {throwTestException;}";
        try {
            SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("tweety@pie");
            mail.setEnvelopeTo("tweety@pie");
            JUnitUtils.interpret(mail, script);
        } catch (ThrowTestException.TestException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    @Test
    public void testIfEnvelopeAllMatchesTrue() {
        boolean isTestPassed = false;
        String script = "require \"envelope\"; if envelope :all :matches \"From\" \"*@domain\" {throwTestException;}";
        try {
            SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("user@domain");
            JUnitUtils.interpret(mail, script);
        } catch (ThrowTestException.TestException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    @Test
    public void testIfEnvelopeAllContainsTrue() {
        boolean isTestPassed = false;
        String script = "require \"envelope\"; if envelope :all :contains \"From\" \"r@dom\" {throwTestException;}";
        try {
            SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("user@domain");
            JUnitUtils.interpret(mail, script);
        } catch (ThrowTestException.TestException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    @Test
    public void testIfEnvelopeLocalpartIsTrue() {
        boolean isTestPassed = false;
        String script = "require \"envelope\"; if envelope :localpart :is \"From\" \"user\" {throwTestException;}";
        try {
            SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("user@domain");
            JUnitUtils.interpret(mail, script);
        } catch (ThrowTestException.TestException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    @Test
    public void testIfEnvelopeLocalpartMatchesTrue() {
        boolean isTestPassed = false;
        String script = "require \"envelope\"; if envelope :localpart :matches \"From\" \"*er\" {throwTestException;}";
        try {
            SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("user@domain");
            JUnitUtils.interpret(mail, script);
        } catch (ThrowTestException.TestException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    @Test
    public void testIfEnvelopeLocalpartContainsTrue() {
        boolean isTestPassed = false;
        String script = "require \"envelope\"; if envelope :localpart :contains \"From\" \"r\" {throwTestException;}";
        try {
            SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("user@domain");
            JUnitUtils.interpret(mail, script);
        } catch (ThrowTestException.TestException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    @Test
    public void testIfEnvelopeDomainIsTrue() {
        boolean isTestPassed = false;
        String script = "require \"envelope\"; if envelope :domain :is \"From\" \"domain\" {throwTestException;}";

        try {
            SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("user@domain");
            JUnitUtils.interpret(mail, script);
        } catch (ThrowTestException.TestException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    @Test
    public void testIfEnvelopeDomainMatchesTrue() {
        boolean isTestPassed = false;
        String script = "require \"envelope\"; if envelope :domain :matches \"From\" \"*main\" {throwTestException;}";
        try {
            SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("user@domain");
            JUnitUtils.interpret(mail, script);
        } catch (ThrowTestException.TestException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    @Test
    public void testIfEnvelopeDomainContainsTrue() {
        boolean isTestPassed = false;
        String script = "require \"envelope\"; if envelope :domain :contains \"From\" \"dom\" {throwTestException;}";
        try {
            SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("user@domain");
            JUnitUtils.interpret(mail, script);
        } catch (ThrowTestException.TestException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    @Test
    public void testIfEnvelopeAllIsFalse() {
        boolean isTestPassed = false;
        String script = "require \"envelope\"; if envelope :all :is \"From\" \"user@domain\" {throwTestException;}";
        try {
            SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("tweety@pie");
            JUnitUtils.interpret(mail, script);
            isTestPassed = true;
        } catch (ThrowTestException.TestException e) {
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    @Test
    public void testIfEnvelopeAllMatchesFalse() {
        boolean isTestPassed = false;
        String script = "require \"envelope\"; if envelope :all :matches \"From\" \"(.*)@domain\" {throwTestException;}";
        try {
            SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("bugs@bunny");
            JUnitUtils.interpret(mail, script);
            isTestPassed = true;
        } catch (ThrowTestException.TestException e) {
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    @Test
    public void testIfEnvelopeAllContainsFalse() {
        boolean isTestPassed = false;
        String script = "require \"envelope\"; if envelope :all :contains \"From\" \"r@dom\" {throwTestException;}";
        try {
            SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("tweety@pie");
            JUnitUtils.interpret(mail, script);
            isTestPassed = true;
        } catch (ThrowTestException.TestException e) {
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    @Test
    public void testIfEnvelopeLocalpartIsFalse() {
        boolean isTestPassed = false;
        String script = "require \"envelope\"; if envelope :localpart :is \"From\" \"user\" {throwTestException;}";
        try {
            SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("tweety@pie");
            JUnitUtils.interpret(mail, script);
            isTestPassed = true;
        } catch (ThrowTestException.TestException e) {
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    @Test
    public void testIfEnvelopeLocalpartMatchesFalse() {
        boolean isTestPassed = false;
        String script = "require \"envelope\"; if envelope :localpart :matches \"From\" \"(.*)er\" {throwTestException;}";
        try {
            SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("tweety@pie");
            JUnitUtils.interpret(mail, script);
            isTestPassed = true;
        } catch (ThrowTestException.TestException e) {
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    @Test
    public void testIfEnvelopeLocalpartContainsFalse() {
        boolean isTestPassed = false;
        String script = "require \"envelope\"; if envelope :localpart :contains \"From\" \"r\" {throwTestException;}";
        try {
            SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("tweety@pie");
            JUnitUtils.interpret(mail, script);
            isTestPassed = true;
        } catch (ThrowTestException.TestException e) {
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    @Test
    public void testIfEnvelopeDomainIsFalse() {
        boolean isTestPassed = false;
        String script = "require \"envelope\"; if envelope :domain :is \"From\" \"domain\" {throwTestException;}";

        try {
            SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("tweety@pie");
            JUnitUtils.interpret(mail, script);
            isTestPassed = true;
        } catch (ThrowTestException.TestException e) {
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    @Test
    public void testIfEnvelopeDomainMatchesFalse() {
        boolean isTestPassed = false;
        String script = "require \"envelope\"; if envelope :domain :matches \"From\" \"(.*)main\" {throwTestException;}";
        try {
            SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("tweety@pie");
            JUnitUtils.interpret(mail, script);
            isTestPassed = true;
        } catch (ThrowTestException.TestException e) {
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    @Test
    public void testIfEnvelopeDomainContainsFalse() {
        boolean isTestPassed = false;
        String script = "require \"envelope\"; if envelope :domain :contains \"From\" \"dom\" {throwTestException;}";
        try {
            SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("tweety@pie");
            JUnitUtils.interpret(mail, script);
            isTestPassed = true;
        } catch (ThrowTestException.TestException e) {
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    @Test
    public void testIfEnvelopeAllIsMultiFalse1() {
        boolean isTestPassed = false;
        String script = "require \"envelope\"; if envelope :all :is [\"From\", \"To\"] \"user@domain\" {throwTestException;}";
        try {
            SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("bugs@bunny");
            mail.setEnvelopeTo("bugs@bunny");
            JUnitUtils.interpret(mail, script);
            isTestPassed = true;
        } catch (ThrowTestException.TestException e) {
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    @Test
    public void testIfEnvelopeAllIsMultiFalse2() {
        boolean isTestPassed = false;
        String script = "require \"envelope\"; if envelope :all :is [\"From\", \"To\"] [\"user@domain\", \"tweety@pie\"] {throwTestException;}";
        try {
            SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("bugs@bunny");
            mail.setEnvelopeTo("bugs@bunny");
            JUnitUtils.interpret(mail, script);
            isTestPassed = true;
        } catch (ThrowTestException.TestException e) {
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    @Test(expected = ParseException.class)
    public void numericKeyListShouldBeASyntaxException() throws Exception {
        SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
        mail.setEnvelopeFrom("user@domain");
        JUnitUtils.interpret(mail, "require \"envelope\"; if envelope :localpart :is \"from\" 0 {keep;}");
    }

    @Test
    public void domainShouldNotFoldNonAsciiLetters() throws Exception {
        SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
        mail.setEnvelopeFrom("user@k.it");
        // U+212A KELVIN SIGN lower cases to an ASCII k
        JUnitUtils.interpret(mail, "require \"envelope\";"
            + " if envelope :domain :comparator \"i;octet\" :is \"from\" \"\u212A.IT\" {throwTestException;}");
    }

    @Test(expected = ThrowTestException.TestException.class)
    public void domainShouldFollowTheLastAt() throws Exception {
        SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
        mail.setEnvelopeFrom("\"a@b\"@example.com");
        JUnitUtils.interpret(mail, "require \"envelope\";"
            + " if envelope :domain :is \"from\" \"example.com\" {throwTestException;}");
    }

    @Test(expected = ThrowTestException.TestException.class)
    public void localPartShouldHoldTheAtOfAQuotedLocalPart() throws Exception {
        SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
        mail.setEnvelopeFrom("\"a@b\"@example.com");
        JUnitUtils.interpret(mail, "require \"envelope\";"
            + " if envelope :localpart :contains \"from\" \"a@b\" {throwTestException;}");
    }

    @Test(expected = ThrowTestException.TestException.class)
    public void allShouldDropTheAngleBrackets() throws Exception {
        SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
        mail.setEnvelopeFrom("<user@example.com>");
        JUnitUtils.interpret(mail, "require \"envelope\";"
            + " if envelope :all :is \"from\" \"user@example.com\" {throwTestException;}");
    }

    @Test(expected = ThrowTestException.TestException.class)
    public void allShouldDropTheSourceRoute() throws Exception {
        SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
        mail.setEnvelopeFrom("<@relay1.org,@relay2.org:user@example.com>");
        JUnitUtils.interpret(mail, "require \"envelope\";"
            + " if envelope :all :is \"from\" \"user@example.com\" {throwTestException;}");
    }

    @Test(expected = ThrowTestException.TestException.class)
    public void domainShouldDropTheSourceRoute() throws Exception {
        SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
        mail.setEnvelopeFrom("<@relay.org:user@example.com>");
        JUnitUtils.interpret(mail, "require \"envelope\";"
            + " if envelope :domain :is \"from\" \"example.com\" {throwTestException;}");
    }

    @Test(expected = ThrowTestException.TestException.class)
    public void allOfTheNullReversePathShouldBeEmpty() throws Exception {
        SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
        mail.setEnvelopeFrom("<>");
        JUnitUtils.interpret(mail, "require \"envelope\";"
            + " if envelope :all :is \"from\" \"\" {throwTestException;}");
    }

    @Test(expected = ThrowTestException.TestException.class)
    public void localPartOfTheNullReversePathShouldBeEmpty() throws Exception {
        SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
        mail.setEnvelopeFrom("<>");
        JUnitUtils.interpret(mail, "require \"envelope\";"
            + " if envelope :localpart :is \"from\" \"\" {throwTestException;}");
    }

    @Test(expected = ThrowTestException.TestException.class)
    public void domainOfTheNullReversePathShouldBeEmpty() throws Exception {
        SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
        mail.setEnvelopeFrom("<>");
        JUnitUtils.interpret(mail, "require \"envelope\";"
            + " if envelope :domain :is \"from\" \"\" {throwTestException;}");
    }

    @Test(expected = ThrowTestException.TestException.class)
    public void domainShouldBeAnAddressLiteralHoldingAnAt() throws Exception {
        SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
        mail.setEnvelopeFrom("user@[x-tag:a@b]");
        JUnitUtils.interpret(mail, "require \"envelope\";"
            + " if envelope :domain :is \"from\" \"[x-tag:a@b]\" {throwTestException;}");
    }

    @Test
    public void localPartOfAnInvalidAddressShouldNotMatch() throws Exception {
        SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
        mail.setEnvelopeFrom("a@b@c");
        JUnitUtils.interpret(mail, "require \"envelope\";"
            + " if envelope :localpart :is \"from\" \"a@b\" {throwTestException;}");
    }

    @Test
    public void domainOfAnInvalidAddressShouldNotMatch() throws Exception {
        SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
        mail.setEnvelopeFrom("a b@example.com");
        JUnitUtils.interpret(mail, "require \"envelope\";"
            + " if envelope :domain :is \"from\" \"example.com\" {throwTestException;}");
    }

    @Test(expected = ThrowTestException.TestException.class)
    public void allOfAnInvalidAddressShouldStillMatch() throws Exception {
        SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
        mail.setEnvelopeFrom("a b@example.com");
        JUnitUtils.interpret(mail, "require \"envelope\";"
            + " if envelope :all :is \"from\" \"a b@example.com\" {throwTestException;}");
    }

    @Test(expected = ThrowTestException.TestException.class)
    public void localPartWithoutDomainShouldMatch() throws Exception {
        SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
        mail.setEnvelopeTo("Postmaster");
        JUnitUtils.interpret(mail, "require \"envelope\";"
            + " if envelope :localpart :is \"to\" \"Postmaster\" {throwTestException;}");
    }

    @Test
    public void addressLiteralThatIsNoAddressShouldNotMatch() throws Exception {
        SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
        mail.setEnvelopeFrom("user@[garbage]");
        JUnitUtils.interpret(mail, "require \"envelope\";"
            + " if envelope :localpart :is \"from\" \"user\" {throwTestException;}");
    }

    @Test
    public void ipv4LiteralOutOfRangeShouldNotMatch() throws Exception {
        SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
        mail.setEnvelopeFrom("user@[300.1.2.3]");
        JUnitUtils.interpret(mail, "require \"envelope\";"
            + " if envelope :localpart :is \"from\" \"user\" {throwTestException;}");
    }

    @Test
    public void ipv6LiteralThatIsNoAddressShouldNotMatch() throws Exception {
        SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
        mail.setEnvelopeFrom("user@[IPv6:not-an-ip]");
        JUnitUtils.interpret(mail, "require \"envelope\";"
            + " if envelope :localpart :is \"from\" \"user\" {throwTestException;}");
    }

    @Test
    public void generalLiteralWithoutContentShouldNotMatch() throws Exception {
        SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
        mail.setEnvelopeFrom("user@[x-tag:]");
        JUnitUtils.interpret(mail, "require \"envelope\";"
            + " if envelope :localpart :is \"from\" \"user\" {throwTestException;}");
    }

    @Test(expected = ThrowTestException.TestException.class)
    public void ipv4LiteralShouldBeTheDomain() throws Exception {
        SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
        mail.setEnvelopeFrom("user@[192.0.2.1]");
        JUnitUtils.interpret(mail, "require \"envelope\";"
            + " if envelope :domain :is \"from\" \"[192.0.2.1]\" {throwTestException;}");
    }

    @Test(expected = ThrowTestException.TestException.class)
    public void ipv6LiteralShouldBeTheDomain() throws Exception {
        SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
        mail.setEnvelopeFrom("user@[IPv6:2001:db8::1]");
        JUnitUtils.interpret(mail, "require \"envelope\";"
            + " if envelope :domain :is \"from\" \"[IPv6:2001:db8::1]\" {throwTestException;}");
    }

    @Test(expected = ThrowTestException.TestException.class)
    public void ipv6LiteralEndingWithIpv4ShouldBeTheDomain() throws Exception {
        SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
        mail.setEnvelopeFrom("user@[IPv6:::ffff:192.0.2.1]");
        JUnitUtils.interpret(mail, "require \"envelope\";"
            + " if envelope :domain :is \"from\" \"[IPv6:::ffff:192.0.2.1]\" {throwTestException;}");
    }

    @Test(expected = ParseException.class)
    public void unknownEnvelopePartShouldThrowAParseException() throws Exception {
        JUnitUtils.parse("require \"envelope\"; if envelope \"x-foo\" \"user@domain\" {keep;}");
    }

    @Test(expected = ParseException.class)
    public void unknownEnvelopePartInAListShouldThrowAParseException() throws Exception {
        JUnitUtils.parse("require \"envelope\"; if envelope [\"from\", \"auth\"] \"user@domain\" {keep;}");
    }

    @Test(expected = ParseException.class)
    public void envelopePartWithSpacesShouldThrowAParseException() throws Exception {
        JUnitUtils.parse("require \"envelope\"; if envelope \" from\" \"user@domain\" {keep;}");
    }

    @Test(expected = ThrowTestException.TestException.class)
    public void envelopePartsShouldIgnoreCase() throws Exception {
        SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
        mail.setEnvelopeTo("user@domain");
        JUnitUtils.interpret(mail, "require \"envelope\";"
            + " if envelope :all :is \"TO\" \"user@domain\" {throwTestException;}");
    }
}
