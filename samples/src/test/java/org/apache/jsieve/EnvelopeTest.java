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

import junit.framework.TestCase;

import org.apache.jsieve.commands.ThrowTestException;
import org.apache.jsieve.exception.SieveException;
import org.apache.jsieve.parser.generated.ParseException;
import org.apache.jsieve.utils.JUnitUtils;
import org.apache.jsieve.utils.SieveEnvelopeMailAdapter;

/**
 * Class EnvelopeTest
 */
public class EnvelopeTest extends TestCase {

    /**
     * Test for Test 'envelope'
     */
    public void testIfEnvelopeAllIsTrue() {
        boolean isTestPassed = false;
        String script = "if envelope :all :is \"From\" \"user@domain\" {throwTestException;}";
        try {
            SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("user@domain");
            JUnitUtils.interpret(mail, script);
        } catch (ThrowTestException.TestException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    public void testCaseInsensitiveEnvelopeName() {
        boolean isTestPassed = false;
        String script = "if envelope :all :is \"from\" \"user@domain\" {throwTestException;}";
        try {
            SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("user@domain");
            JUnitUtils.interpret(mail, script);
        } catch (ThrowTestException.TestException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    public void testOctetComparatorTrue() {
        boolean isTestPassed = false;
        String script = "if envelope :comparator \"i;octet\" :all :is \"From\" \"uSeR@dOmAiN\" {throwTestException;}";
        try {
            SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("uSeR@dOmAiN");
            JUnitUtils.interpret(mail, script);
        } catch (ThrowTestException.TestException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    public void testOctetComparatorFalse() {
        boolean isTestPassed = false;
        String script = "if envelope :comparator \"i;octet\" :all :is \"From\" \"uSeR@dOmAiN\" {throwTestException;}";
        try {
            SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("user@domain");
            JUnitUtils.interpret(mail, script);
            isTestPassed = true;
        } catch (ThrowTestException.TestException e) {
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    public void testAsciiComparatorTrue() {
        boolean isTestPassed = false;
        String script = "if envelope :comparator \"i;ascii-casemap\" :all :is \"From\" \"uSeR@dOmAiN\" {throwTestException;}";
        try {
            SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("user@domain");
            JUnitUtils.interpret(mail, script);
        } catch (ThrowTestException.TestException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    public void testAsciiComparatorFalse() {
        boolean isTestPassed = false;
        String script = "if envelope :comparator \"i;ascii-casemap\" :all :is \"From\" \"uSeR@dOmAiN\" {throwTestException;}";
        try {
            SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("user@domain1");
            JUnitUtils.interpret(mail, script);
            isTestPassed = true;
        } catch (ThrowTestException.TestException e) {
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    public void testIfEnvelopeAllIsMultiTrue1() {
        boolean isTestPassed = false;
        String script = "if envelope :all :is [\"From\", \"To\"] \"user@domain\" {throwTestException;}";
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
        assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    public void testIfEnvelopeAllIsMultiTrue2() {
        boolean isTestPassed = false;
        String script = "if envelope :all :is [\"From\", \"To\"] [\"user@domain\", \"tweety@pie\"] {throwTestException;}";
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
        assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    public void testIfEnvelopeAllIsMultiTrue3() {
        boolean isTestPassed = false;
        String script = "if envelope :all :is [\"From\", \"To\"] [\"user@domain\", \"tweety@pie\"] {throwTestException;}";
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
        assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    public void testIfEnvelopeAllIsMultiTrue4() {
        boolean isTestPassed = false;
        String script = "if envelope :all :is [\"From\", \"To\"] [\"user@domain\", \"tweety@pie\"] {throwTestException;}";
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
        assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    public void testIfEnvelopeAllMatchesTrue() {
        boolean isTestPassed = false;
        String script = "if envelope :all :matches \"From\" \"*@domain\" {throwTestException;}";
        try {
            SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("user@domain");
            JUnitUtils.interpret(mail, script);
        } catch (ThrowTestException.TestException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    public void testIfEnvelopeAllContainsTrue() {
        boolean isTestPassed = false;
        String script = "if envelope :all :contains \"From\" \"r@dom\" {throwTestException;}";
        try {
            SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("user@domain");
            JUnitUtils.interpret(mail, script);
        } catch (ThrowTestException.TestException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    public void testIfEnvelopeLocalpartIsTrue() {
        boolean isTestPassed = false;
        String script = "if envelope :localpart :is \"From\" \"user\" {throwTestException;}";
        try {
            SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("user@domain");
            JUnitUtils.interpret(mail, script);
        } catch (ThrowTestException.TestException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    public void testIfEnvelopeLocalpartMatchesTrue() {
        boolean isTestPassed = false;
        String script = "if envelope :localpart :matches \"From\" \"*er\" {throwTestException;}";
        try {
            SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("user@domain");
            JUnitUtils.interpret(mail, script);
        } catch (ThrowTestException.TestException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    public void testIfEnvelopeLocalpartContainsTrue() {
        boolean isTestPassed = false;
        String script = "if envelope :localpart :contains \"From\" \"r\" {throwTestException;}";
        try {
            SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("user@domain");
            JUnitUtils.interpret(mail, script);
        } catch (ThrowTestException.TestException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    public void testIfEnvelopeDomainIsTrue() {
        boolean isTestPassed = false;
        String script = "if envelope :domain :is \"From\" \"domain\" {throwTestException;}";

        try {
            SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("user@domain");
            JUnitUtils.interpret(mail, script);
        } catch (ThrowTestException.TestException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    public void testIfEnvelopeDomainMatchesTrue() {
        boolean isTestPassed = false;
        String script = "if envelope :domain :matches \"From\" \"*main\" {throwTestException;}";
        try {
            SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("user@domain");
            JUnitUtils.interpret(mail, script);
        } catch (ThrowTestException.TestException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    public void testIfEnvelopeDomainContainsTrue() {
        boolean isTestPassed = false;
        String script = "if envelope :domain :contains \"From\" \"dom\" {throwTestException;}";
        try {
            SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("user@domain");
            JUnitUtils.interpret(mail, script);
        } catch (ThrowTestException.TestException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    public void testIfEnvelopeAllIsFalse() {
        boolean isTestPassed = false;
        String script = "if envelope :all :is \"From\" \"user@domain\" {throwTestException;}";
        try {
            SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("tweety@pie");
            JUnitUtils.interpret(mail, script);
            isTestPassed = true;
        } catch (ThrowTestException.TestException e) {
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    public void testIfEnvelopeAllMatchesFalse() {
        boolean isTestPassed = false;
        String script = "if envelope :all :matches \"From\" \"(.*)@domain\" {throwTestException;}";
        try {
            SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("bugs@bunny");
            JUnitUtils.interpret(mail, script);
            isTestPassed = true;
        } catch (ThrowTestException.TestException e) {
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    public void testIfEnvelopeAllContainsFalse() {
        boolean isTestPassed = false;
        String script = "if envelope :all :contains \"From\" \"r@dom\" {throwTestException;}";
        try {
            SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("tweety@pie");
            JUnitUtils.interpret(mail, script);
            isTestPassed = true;
        } catch (ThrowTestException.TestException e) {
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    public void testIfEnvelopeLocalpartIsFalse() {
        boolean isTestPassed = false;
        String script = "if envelope :localpart :is \"From\" \"user\" {throwTestException;}";
        try {
            SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("tweety@pie");
            JUnitUtils.interpret(mail, script);
            isTestPassed = true;
        } catch (ThrowTestException.TestException e) {
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    public void testIfEnvelopeLocalpartMatchesFalse() {
        boolean isTestPassed = false;
        String script = "if envelope :localpart :matches \"From\" \"(.*)er\" {throwTestException;}";
        try {
            SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("tweety@pie");
            JUnitUtils.interpret(mail, script);
            isTestPassed = true;
        } catch (ThrowTestException.TestException e) {
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    public void testIfEnvelopeLocalpartContainsFalse() {
        boolean isTestPassed = false;
        String script = "if envelope :localpart :contains \"From\" \"r\" {throwTestException;}";
        try {
            SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("tweety@pie");
            JUnitUtils.interpret(mail, script);
            isTestPassed = true;
        } catch (ThrowTestException.TestException e) {
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    public void testIfEnvelopeDomainIsFalse() {
        boolean isTestPassed = false;
        String script = "if envelope :domain :is \"From\" \"domain\" {throwTestException;}";

        try {
            SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("tweety@pie");
            JUnitUtils.interpret(mail, script);
            isTestPassed = true;
        } catch (ThrowTestException.TestException e) {
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    public void testIfEnvelopeDomainMatchesFalse() {
        boolean isTestPassed = false;
        String script = "if envelope :domain :matches \"From\" \"(.*)main\" {throwTestException;}";
        try {
            SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("tweety@pie");
            JUnitUtils.interpret(mail, script);
            isTestPassed = true;
        } catch (ThrowTestException.TestException e) {
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    public void testIfEnvelopeDomainContainsFalse() {
        boolean isTestPassed = false;
        String script = "if envelope :domain :contains \"From\" \"dom\" {throwTestException;}";
        try {
            SieveEnvelopeMailAdapter mail = JUnitUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("tweety@pie");
            JUnitUtils.interpret(mail, script);
            isTestPassed = true;
        } catch (ThrowTestException.TestException e) {
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    public void testIfEnvelopeAllIsMultiFalse1() {
        boolean isTestPassed = false;
        String script = "if envelope :all :is [\"From\", \"To\"] \"user@domain\" {throwTestException;}";
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
        assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'envelope'
     */
    public void testIfEnvelopeAllIsMultiFalse2() {
        boolean isTestPassed = false;
        String script = "if envelope :all :is [\"From\", \"To\"] [\"user@domain\", \"tweety@pie\"] {throwTestException;}";
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
        assertTrue(isTestPassed);
    }

}
