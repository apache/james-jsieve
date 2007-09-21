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

package org.apache.jsieve.samples.james.junit;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import junit.framework.TestCase;

import org.apache.james.core.MailImpl;
import org.apache.jsieve.mail.Action;
import org.apache.jsieve.mail.ActionKeep;
import org.apache.jsieve.samples.james.ActionDispatcher;
import org.apache.jsieve.samples.james.junit.utils.ActionAbsent;
import org.apache.jsieve.samples.james.junit.utils.MockMailetContext;
import org.apache.mailet.Mail;
import org.apache.mailet.MailAddress;
import org.apache.mailet.MailetContext;

/**
 * Class <code>ActionDispatcherTest</code>.
 */
public class ActionDispatcherTest extends TestCase {

    /**
     * Constructor for ActionDispatcherTest.
     * 
     * @param arg0
     */
    public ActionDispatcherTest(String arg0) {
        super(arg0);
    }

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(ActionDispatcherTest.class);
    }

    /**
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        ActionDispatcher.resetInstance();
    }

    /**
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test execute of ActionKeep
     * 
     * @throws MessagingException
     */
    public void testExecuteActionKeep() throws MessagingException {
        boolean isTestPassed = false;
        Mail aMail = new MailImpl();
        aMail.setRecipients(Arrays.asList(new MailAddress[] { new MailAddress(
                "a", "a.com") }));
        MimeMessage mimeMessage = new MimeMessage(Session
                .getDefaultInstance(new Properties()));
        mimeMessage.setText("TEST");
        aMail.setMessage(mimeMessage);
        MailetContext aMailetContext = new MockMailetContext();
        Action action = new ActionKeep();
        try {
            ActionDispatcher.getInstance().execute(action, aMail,
                    aMailetContext);
            isTestPassed = true;
        } catch (NoSuchMethodException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        } catch (MessagingException e) {
        }

        assertTrue(isTestPassed);
    }

    /**
     * Test execute of ActionAbsent. Should throw a NoSuchMethodException.
     */
    public void testExecuteActionAbsent() {
        boolean isTestPassed = false;
        Mail aMail = new MailImpl();
        MailetContext aMailetContext = new MockMailetContext();
        Action action = new ActionAbsent();
        try {
            ActionDispatcher.getInstance().execute(action, aMail,
                    aMailetContext);
        } catch (NoSuchMethodException e) {
            isTestPassed = true;
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        } catch (MessagingException e) {
        }
        assertTrue(isTestPassed);
    }

}
