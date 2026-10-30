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
import org.apache.jsieve.mail.MailAdapter;
import org.apache.jsieve.mail.SieveMailException;
import org.apache.jsieve.parser.generated.ParseException;
import org.apache.jsieve.utils.JUnitUtils;
import org.apache.jsieve.utils.SieveMailAdapter;
import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.mail.MessagingException;

/**
 * Class SizeTest
 */
public class SizeTest {

    private static MailAdapter largestMail() throws Exception {
        SieveMailAdapter mail = mock(SieveMailAdapter.class);
        when(mail.getSize()).thenReturn(Integer.MAX_VALUE);
        return mail;
    }

    private static MailAdapter smallMail() throws Exception {
        SieveMailAdapter mail = (SieveMailAdapter) JUnitUtils.createMail();
        mail.getMessage().setText("Hi!");
        mail.getMessage().saveChanges();
        // copied so that JavaMail reports the size of the whole message
        return JUnitUtils.copyMail(mail);
    }

    /**
     * Test for Test 'size'
     */
    @org.junit.Test
    public void testSizeIsOverTrue() {
        boolean isTestPassed = false;
        SieveMailAdapter mail = null;
        int size = 0;
        try {
            mail = (SieveMailAdapter) JUnitUtils.createMail();
            mail.getMessage().setText("Hi!");
            mail.getMessage().saveChanges();
            // Need to copy the mail to get JavaMail to report the message size
            // correctly (saveChanges() only saves the headers!)
            mail = (SieveMailAdapter) JUnitUtils.copyMail(mail);
            size = mail.getSize();
        } catch (SieveMailException e) {
        } catch (MessagingException e) {
        }

        String script = "if size :over " + Integer.toString(size - 1)
                + " {throwTestException;}";
        try {

            JUnitUtils.interpret(mail, script);
        } catch (ThrowTestException.TestException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'size'
     */
    @Test
    public void testSizeIsOverFalse() {
        boolean isTestPassed = false;
        SieveMailAdapter mail = null;
        int size = 0;
        try {
            mail = (SieveMailAdapter) JUnitUtils.createMail();
            mail.getMessage().setText("Hi!");
            mail.getMessage().saveChanges();
            // Need to copy the mail to get JavaMail to report the message size
            // correctly (saveChanges() only saves the headers!)
            mail = (SieveMailAdapter) JUnitUtils.copyMail(mail);
            size = mail.getSize();
        } catch (SieveMailException e) {
        } catch (MessagingException e) {
        }

        String script = "if size :over " + Integer.toString(size)
                + " {throwTestException;}";
        try {

            JUnitUtils.interpret(mail, script);
            isTestPassed = true;
        } catch (ThrowTestException.TestException e) {
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'size'
     */
    @Test
    public void testSizeIsUnderTrue() {
        boolean isTestPassed = false;
        SieveMailAdapter mail = null;
        int size = 0;
        try {
            mail = (SieveMailAdapter) JUnitUtils.createMail();
            mail.getMessage().setText("Hi!");
            mail.getMessage().saveChanges();
            // Need to copy the mail to get JavaMail to report the message size
            // correctly (saveChanges() only saves the headers!)
            mail = (SieveMailAdapter) JUnitUtils.copyMail(mail);
            size = mail.getSize();
        } catch (SieveMailException e) {
        } catch (MessagingException e) {
        }

        String script = "if size :under " + Integer.toString(size + 1)
                + " {throwTestException;}";
        try {

            JUnitUtils.interpret(mail, script);
        } catch (ThrowTestException.TestException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'size'
     */
    @Test
    public void testSizeIsUnderFalse() {
        boolean isTestPassed = false;
        SieveMailAdapter mail = null;
        int size = 0;
        try {
            mail = (SieveMailAdapter) JUnitUtils.createMail();
            mail.getMessage().setText("Hi!");
            mail.getMessage().saveChanges();
            // Need to copy the mail to get JavaMail to report the message size
            // correctly (saveChanges() only saves the headers!)
            mail = (SieveMailAdapter) JUnitUtils.copyMail(mail);
            size = mail.getSize();
        } catch (SieveMailException e) {
        } catch (MessagingException e) {
        }

        String script = "if size :over " + Integer.toString(size)
                + " {throwTestException;}";
        try {

            JUnitUtils.interpret(mail, script);
            isTestPassed = true;
        } catch (ThrowTestException.TestException e) {
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Test 'size' with quantifier
     */
    @Test
    public void testSizeIsWithQuantifier() throws Exception {
        boolean isTestPassed = false;
        SieveMailAdapter mail = (SieveMailAdapter) JUnitUtils.createMail();
        mail.getMessage().setText("Hi!");
        mail.getMessage().saveChanges();
        // Need to copy the mail to get JavaMail to report the message size
        // correctly (saveChanges() only saves the headers!)
        mail = (SieveMailAdapter) JUnitUtils.copyMail(mail);

        String script = "if size :over 1G {throwTestException;}";
        try {

            JUnitUtils.interpret(mail, script);
            isTestPassed = true;
        } catch (ThrowTestException.TestException e) {
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    @Test
    public void sizeOverANumberAboveIntRangeShouldNotMatch() throws Exception {
        JUnitUtils.interpret(smallMail(), "if size :over 3024438529 {throwTestException;}");
    }

    @Test(expected = ThrowTestException.TestException.class)
    public void sizeUnderANumberAboveIntRangeShouldMatch() throws Exception {
        JUnitUtils.interpret(smallMail(), "if size :under 3024438529 {throwTestException;}");
    }

    @Test
    public void sizeOverAQuantityAboveIntRangeShouldNotMatch() throws Exception {
        JUnitUtils.interpret(smallMail(), "if size :over 2G {throwTestException;}");
        JUnitUtils.interpret(smallMail(), "if size :over 2147483647M {throwTestException;}");
        JUnitUtils.interpret(smallMail(), "if size :over 2097152K {throwTestException;}");
    }

    @Test(expected = ThrowTestException.TestException.class)
    public void sizeUnderAQuantityAboveIntRangeShouldMatch() throws Exception {
        JUnitUtils.interpret(smallMail(), "if size :under 3G {throwTestException;}");
    }

    @Test
    public void numberOfManyDigitsShouldParse() throws Exception {
        JUnitUtils.parse("if size :over 000000000000099999999999999999999999 {discard;}");
    }

    @Test(expected = ThrowTestException.TestException.class)
    public void theLargestMailShouldBeUnderANumberAboveIntRange() throws Exception {
        JUnitUtils.interpret(largestMail(), "if size :under 2147483648 {throwTestException;}");
    }

    @Test(expected = ThrowTestException.TestException.class)
    public void theLargestMailShouldBeUnderAQuantityAboveIntRange() throws Exception {
        JUnitUtils.interpret(largestMail(), "if size :under 2G {throwTestException;}");
    }

    @Test
    public void theLargestMailShouldNotBeOverANumberAboveIntRange() throws Exception {
        JUnitUtils.interpret(largestMail(), "if size :over 2147483648 {throwTestException;}");
    }

}
