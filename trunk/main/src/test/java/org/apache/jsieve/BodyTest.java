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
import javax.mail.internet.MimeMultipart;

import junit.framework.TestCase;

import org.apache.jsieve.commands.ThrowTestException;
import org.apache.jsieve.exception.SieveException;
import org.apache.jsieve.parser.generated.ParseException;
import org.apache.jsieve.utils.JUnitUtils;
import org.apache.jsieve.utils.SieveMailAdapter;

/**
 * Class BodyTest
 */
public class BodyTest extends TestCase {

    protected SieveMailAdapter textMail() throws MessagingException {
        SieveMailAdapter mail = (SieveMailAdapter) JUnitUtils.createMail();
        mail.getMessage().setContent("Wibble\n\n" + "Wibble\n", "text/plain");
        return mail;
    }

    protected SieveMailAdapter nonTextMail() throws MessagingException,
            SieveException {
        SieveMailAdapter mail = (SieveMailAdapter) JUnitUtils.createMail();
        // FIXME: This doesn't work
        mail.getMessage().setContent(new MimeMultipart("image/png"));
        return mail;
    }

    /**
     * Test for Test 'header'
     */
    public void testBasic() {
        boolean isTestPassed = false;
        String script = "if body :contains [\"Wibble\"] {throwTestException;}";
        try {
            JUnitUtils.interpret(textMail(), script);
        } catch (MessagingException e) {
        } catch (ThrowTestException.TestException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'body'
     */
    public void testBodyCaseInsensitivity() {
        boolean isTestPassed = false;
        String script = "if body :contains [\"wibble\"] {throwTestException;}";
        try {
            JUnitUtils.interpret(textMail(), script);
        } catch (MessagingException e) {
        } catch (ThrowTestException.TestException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'body'
     */
    public void testBodyNoContains() {
        boolean isTestPassed = false;
        String script = "if body [\"wibble\"] {throwTestException;}";
        try {
            JUnitUtils.interpret(textMail(), script);
        } catch (MessagingException e) {
        } catch (ThrowTestException.TestException e) {
        } catch (ParseException e) {
        } catch (SieveException e) {
            isTestPassed = true;
        }
        assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'body'
     */
    // FIXME: I can't find a method of forcing the mime type, so this test
    // always fails ...
    // public void testBodyNonText()
    // {
    // boolean isTestPassed = false;
    // String script = "if body :contains [\"wibble\"] {throwTestException;}";
    // try
    // {
    // JUnitUtils.interpret(nonTextMail(), script);
    // }
    // catch (MessagingException e)
    // {
    // }
    // catch (ThrowTestException.TestException e)
    // {
    // }
    // catch (ParseException e)
    // {
    // }
    // catch (SieveException e)
    // {
    // isTestPassed = true;
    // }
    // assertTrue(isTestPassed);
    // }
}
