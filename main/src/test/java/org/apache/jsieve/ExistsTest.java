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
import org.apache.jsieve.exception.SyntaxException;
import org.apache.jsieve.parser.generated.ParseException;
import org.apache.jsieve.utils.JUnitUtils;
import org.apache.jsieve.utils.SieveMailAdapter;

/**
 * Class ExistsTest
 */
public class ExistsTest extends TestCase {

    /**
     * Test for Test 'exists'
     */
    public void testExistsTrue() {
        boolean isTestPassed = false;
        String script = "if exists \"From\" {throwTestException;}";

        try {
            SieveMailAdapter mail = (SieveMailAdapter) JUnitUtils.createMail();
            mail.getMessage().addHeader("From", "tweety@pie");
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
     * Test for Test 'exists'
     */
    public void testCaseInsensitivity() {
        boolean isTestPassed = false;
        String script = "if exists \"From\" {throwTestException;}";

        try {
            SieveMailAdapter mail = (SieveMailAdapter) JUnitUtils.createMail();
            mail.getMessage().addHeader("from", "tweety@pie");
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
     * Test for Test 'exists'
     */
    public void testExistsTrueTrue() {
        boolean isTestPassed = false;
        String script = "if exists [\"From\", \"X-Files\"] {throwTestException;}";

        try {
            SieveMailAdapter mail = (SieveMailAdapter) JUnitUtils.createMail();
            mail.getMessage().addHeader("From", "tweety@pie");
            mail.getMessage().addHeader("X-Files", "spooks@everywhere");
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
     * Test for Test 'exists'
     */
    public void testExistsTrueFalse() {
        boolean isTestPassed = false;
        String script = "if exists [\"From\", \"X-Files\"] {stop;} throwTestException;";
        try {
            SieveMailAdapter mail = (SieveMailAdapter) JUnitUtils.createMail();
            mail.getMessage().addHeader("From", "tweety@pie");
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
     * Test for Test 'exists'
     */
    public void testExistsFalse() {
        boolean isTestPassed = false;
        String script = "if exists \"From\" {stop;} throwTestException;";

        try {
            SieveMailAdapter mail = (SieveMailAdapter) JUnitUtils.createMail();
            JUnitUtils.interpret(mail, script);
        } catch (ThrowTestException.TestException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'exists'
     */
    public void testExistsFalseFalse() {
        boolean isTestPassed = false;
        String script = "if exists [\"From\", \"X-Files\"] {stop;} throwTestException;";

        try {
            SieveMailAdapter mail = (SieveMailAdapter) JUnitUtils.createMail();
            JUnitUtils.interpret(mail, script);
        } catch (ThrowTestException.TestException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'exists' with invalid numeric argument
     */
    public void testInvalidNumericArgument() {
        boolean isTestPassed = false;
        String script = "if exists 1 {throwTestException;}";

        try {
            JUnitUtils.interpret(JUnitUtils.createMail(), script);
        } catch (SyntaxException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'exists' with invalid test argument
     */
    public void testInvalidTestArgument() {
        boolean isTestPassed = false;
        String script = "if exists not {throwTestException;}";

        try {
            JUnitUtils.interpret(JUnitUtils.createMail(), script);
        } catch (SyntaxException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        assertTrue(isTestPassed);
    }

}
