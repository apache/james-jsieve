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

import org.apache.jsieve.exception.SieveException;
import org.apache.jsieve.exception.SyntaxException;
import org.apache.jsieve.mail.ActionFileInto;
import org.apache.jsieve.mail.MailAdapter;
import org.apache.jsieve.parser.generated.ParseException;
import org.apache.jsieve.utils.JUnitUtils;

/**
 * Class FileIntoTest
 */
public class FileIntoTest extends TestCase {

    /**
     * Test for Command 'fileinto'
     */
    public void testFileInto() {
        boolean isTestPassed = false;
        String script = "fileinto \"INBOX.test1\"; fileinto \"INBOX.test2\";";

        try {
            MailAdapter mail = JUnitUtils.createMail();
            JUnitUtils.interpret(mail, script);
            assertTrue(mail.getActions().size() == 2);
            assertTrue(mail.getActions().get(0) instanceof ActionFileInto);
            assertTrue(((ActionFileInto) mail.getActions().get(0))
                    .getDestination().equals("INBOX.test1"));
            assertTrue(mail.getActions().get(1) instanceof ActionFileInto);
            assertTrue(((ActionFileInto) mail.getActions().get(1))
                    .getDestination().equals("INBOX.test2"));
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        assertTrue(isTestPassed);
    }

    /**
     * Test for Command 'fileinto' with duplicate destinations. Only one
     * ActionFileInto should result.
     */
    public void testDuplicateFileInto() {
        boolean isTestPassed = false;
        String script = "fileinto \"INBOX.test1\"; fileinto \"INBOX.test1\";";

        try {
            MailAdapter mail = JUnitUtils.createMail();
            JUnitUtils.interpret(mail, script);
            assertTrue(mail.getActions().size() == 1);
            assertTrue(mail.getActions().get(0) instanceof ActionFileInto);
            assertTrue(((ActionFileInto) mail.getActions().get(0))
                    .getDestination().equals("INBOX.test1"));
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        assertTrue(isTestPassed);
    }

    /**
     * Test for Command 'fileinto' with an invalid argument type
     */
    public void testInvalidArgumentType() {
        boolean isTestPassed = false;
        String script = "fileinto 1 ;";

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
     * Test for Command 'fileinto' with an invalid argument number
     */
    public void testInvalidArgumentNumber() {
        boolean isTestPassed = false;
        String script = "fileinto [\"INBOX.test\", \"elsewhere\"];";

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
     * Test for Command 'fileinto' with an invalid block
     */
    public void testInvalidBlock() {
        boolean isTestPassed = false;
        String script = "fileinto 1 {throwTestException;}";

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
