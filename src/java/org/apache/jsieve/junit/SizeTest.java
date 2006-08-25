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


package org.apache.jsieve.junit;

import javax.mail.MessagingException;

import junit.framework.TestCase;

import org.apache.jsieve.CommandManager;
import org.apache.jsieve.SieveException;
import org.apache.jsieve.TestManager;
import org.apache.jsieve.junit.commands.ThrowTestException;
import org.apache.jsieve.junit.utils.SieveMailAdapter;
import org.apache.jsieve.junit.utils.TestUtils;
import org.apache.jsieve.mail.SieveMailException;
import org.apache.jsieve.parser.generated.ParseException;

/**
 * Class SizeTest
 */
public class SizeTest extends TestCase
{

    /**
     * Constructor for SizeTest.
     * @param arg0
     */
    public SizeTest(String arg0)
    {
        super(arg0);
    }

    public static void main(String[] args)
    {
        junit.swingui.TestRunner.run(SizeTest.class);
    }

    /**
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        CommandManager.resetInstance();
        TestManager.resetInstance();          
    }

    /**
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }
    
    /**
     * Test for Test 'size'
     */
    public void testSizeIsOverTrue()
    {
        boolean isTestPassed = false;
        SieveMailAdapter mail = null;
        int size = 0;
        try
        {
            mail = (SieveMailAdapter) TestUtils.createMail();
            mail.getMessage().setText("Hi!");           
            mail.getMessage().saveChanges();
            // Need to copy the mail to get JavaMail to report the message size
            // correctly (saveChanges() only saves the headers!) 
            mail = (SieveMailAdapter) TestUtils.copyMail(mail);
            size = mail.getSize();
        }
        catch (SieveMailException e)
        {
        }
        catch (MessagingException e)
        {
        }

        String script =
            "if size :over "
                + new Integer(size - 1).toString()
                + " {throwTestException;}";
        try
        {

            TestUtils.interpret(mail, script);
        }

        catch (ThrowTestException.TestException e)
        {
            isTestPassed = true;
        }
        catch (ParseException e)
        {
        }
        catch (SieveException e)
        {
        }
        assertTrue(isTestPassed);
    }
    
    /**
     * Test for Test 'size'
     */
    public void testSizeIsOverFalse()
    {
        boolean isTestPassed = false;
        SieveMailAdapter mail = null;
        int size = 0;
        try
        {
            mail = (SieveMailAdapter) TestUtils.createMail();
            mail.getMessage().setText("Hi!");           
            mail.getMessage().saveChanges();
            // Need to copy the mail to get JavaMail to report the message size
            // correctly (saveChanges() only saves the headers!) 
            mail = (SieveMailAdapter) TestUtils.copyMail(mail);
            size = mail.getSize();
        }
        catch (SieveMailException e)
        {
        }
        catch (MessagingException e)
        {
        }

        String script =
            "if size :over "
                + new Integer(size).toString()
                + " {throwTestException;}";
        try
        {

            TestUtils.interpret(mail, script);
            isTestPassed = true;            
        }

        catch (ThrowTestException.TestException e)
        {
        }
        catch (ParseException e)
        {
        }
        catch (SieveException e)
        {
        }
        assertTrue(isTestPassed);
    }    
    
    /**
     * Test for Test 'size'
     */
    public void testSizeIsUnderTrue()
    {
        boolean isTestPassed = false;
        SieveMailAdapter mail = null;
        int size = 0;
        try
        {
            mail = (SieveMailAdapter) TestUtils.createMail();
            mail.getMessage().setText("Hi!");
            mail.getMessage().saveChanges();
            // Need to copy the mail to get JavaMail to report the message size
            // correctly (saveChanges() only saves the headers!) 
            mail = (SieveMailAdapter) TestUtils.copyMail(mail);
            size = mail.getSize();
        }
        catch (SieveMailException e)
        {
        }
        catch (MessagingException e)
        {
        }

        String script =
            "if size :under "
                + new Integer(size + 1).toString()
                + " {throwTestException;}";
        try
        {

            TestUtils.interpret(mail, script);
        }

        catch (ThrowTestException.TestException e)
        {
            isTestPassed = true;
        }
        catch (ParseException e)
        {
        }
        catch (SieveException e)
        {
        }
        assertTrue(isTestPassed);
    }
    /**
     * Test for Test 'size'
     */
    public void testSizeIsUnderFalse()
    {
        boolean isTestPassed = false;
        SieveMailAdapter mail = null;
        int size = 0;
        try
        {
            mail = (SieveMailAdapter) TestUtils.createMail();
            mail.getMessage().setText("Hi!");
            mail.getMessage().saveChanges();
            // Need to copy the mail to get JavaMail to report the message size
            // correctly (saveChanges() only saves the headers!) 
            mail = (SieveMailAdapter) TestUtils.copyMail(mail);
            size = mail.getSize();
        }
        catch (SieveMailException e)
        {
        }
        catch (MessagingException e)
        {
        }

        String script =
            "if size :over "
                + new Integer(size).toString()
                + " {throwTestException;}";
        try
        {

            TestUtils.interpret(mail, script);
            isTestPassed = true;
        }

        catch (ThrowTestException.TestException e)
        {
        }
        catch (ParseException e)
        {
        }
        catch (SieveException e)
        {
        }
        assertTrue(isTestPassed);
    }        

}
