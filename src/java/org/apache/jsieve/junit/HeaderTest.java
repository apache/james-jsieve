/***********************************************************************
 * Copyright (c) 2003-2004 The Apache Software Foundation.             *
 * All rights reserved.                                                *
 * ------------------------------------------------------------------- *
 * Licensed under the Apache License, Version 2.0 (the "License"); you *
 * may not use this file except in compliance with the License. You    *
 * may obtain a copy of the License at:                                *
 *                                                                     *
 *     http://www.apache.org/licenses/LICENSE-2.0                      *
 *                                                                     *
 * Unless required by applicable law or agreed to in writing, software *
 * distributed under the License is distributed on an "AS IS" BASIS,   *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or     *
 * implied.  See the License for the specific language governing       *
 * permissions and limitations under the License.                      *
 ***********************************************************************/

package org.apache.jsieve.junit;

import javax.mail.MessagingException;
import junit.framework.TestCase;

import org.apache.jsieve.CommandManager;
import org.apache.jsieve.SieveException;
import org.apache.jsieve.TestManager;
import org.apache.jsieve.junit.commands.ThrowTestException;
import org.apache.jsieve.junit.utils.*;
import org.apache.jsieve.parser.generated.ParseException;


/**
 * Class HeaderTest
 */
public class HeaderTest extends TestCase
{

    /**
     * Constructor for HeaderTest.
     * @param arg0
     */
    public HeaderTest(String arg0)
    {
        super(arg0);
    }

    public static void main(String[] args)
    {
        junit.swingui.TestRunner.run(HeaderTest.class);
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
     * Test for Test 'header'
     */
    public void testHeaderIsTrue()
    {
        boolean isTestPassed = false;
        String script = "if header :is \"X-Caffeine\" \"C8H10N4O2\" {throwTestException;}";
        try
        {
            SieveMailAdapter mail = (SieveMailAdapter) TestUtils.createMail();
            mail.getMessage().addHeader("X-Caffeine", "C8H10N4O2");            
            TestUtils.interpret(mail, script);
        }
        catch (MessagingException e)
        {
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
     * Test for Test 'header'
     */
    public void testHeaderCaseInsensitivity()
    {
        boolean isTestPassed = false;
        String script = "if header :is \"X-Caffeine\" \"C8H10N4O2\" {throwTestException;}";
        try
        {
            SieveMailAdapter mail = (SieveMailAdapter) TestUtils.createMail();
            mail.getMessage().addHeader("x-caffeine", "C8H10N4O2");            
            TestUtils.interpret(mail, script);
        }
        catch (MessagingException e)
        {
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
     * Test for Test 'header'
     */
    public void testHeaderIsTrueMulti1()
    {
        boolean isTestPassed = false;
        String script = "if header :is [\"X-Decaf\", \"X-Caffeine\"] \"C8H10N4O2\" {throwTestException;}";
        try
        {
            SieveMailAdapter mail = (SieveMailAdapter) TestUtils.createMail();
            mail.getMessage().addHeader("X-Caffeine", "C8H10N4O2");            
            TestUtils.interpret(mail, script);
        }
        catch (MessagingException e)
        {
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
     * Test for Test 'header'
     */
    public void testHeaderIsFalseMulti1()
    {
        boolean isTestPassed = false;
        String script = "if header :is [\"X-Decaf\", \"X-Caffeine\"] \"C8H10N4O2\" {throwTestException;}";
        try
        {
            SieveMailAdapter mail = (SieveMailAdapter) TestUtils.createMail();
            mail.getMessage().addHeader("X-Caffeine", "stuff");
            mail.getMessage().addHeader("X-Decaf", "more stuff");                          
            TestUtils.interpret(mail, script);
            isTestPassed = true;            
        }
        catch (MessagingException e)
        {
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
     * Test for Test 'header'
     */
    public void testHeaderIsTrueMulti2()
    {
        boolean isTestPassed = false;
        String script = "if header :is \"X-Caffeine\" [\"absent\", \"C8H10N4O2\"] {throwTestException;}";
        try
        {
            SieveMailAdapter mail = (SieveMailAdapter) TestUtils.createMail();
            mail.getMessage().addHeader("X-Caffeine", "C8H10N4O2");            
            TestUtils.interpret(mail, script);
        }
        catch (MessagingException e)
        {
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
     * Test for Test 'header'
     */
    public void testHeaderIsTrueMulti3()
    {
        boolean isTestPassed = false;
        String script = "if header :is [\"X-Decaf\", \"X-Caffeine\"] [\"absent\", \"C8H10N4O2\"] {throwTestException;}";
        try
        {
            SieveMailAdapter mail = (SieveMailAdapter) TestUtils.createMail();
            mail.getMessage().addHeader("X-Caffeine", "stuff"); 
            mail.getMessage().addHeader("X-Decaf", "C8H10N4O2");                         
            TestUtils.interpret(mail, script);
        }
        catch (MessagingException e)
        {
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
     * Test for Test 'header'
     */
    public void testHeaderIsFalseValue()
    {
        boolean isTestPassed = false;
        String script = "if header :is \"X-Caffeine\" \"C8H10N4O2\" {throwTestException;}";
        try
        {
            SieveMailAdapter mail = (SieveMailAdapter) TestUtils.createMail();
            mail.getMessage().addHeader("X-Caffeine", "C8H10N4O");            
            TestUtils.interpret(mail, script);
            isTestPassed = true;            
        }
        catch (MessagingException e)
        {
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
     * Test for Test 'header'
     */
    public void testHeaderIsFalseHeader()
    {
        boolean isTestPassed = false;
        String script = "if header :is \"X-Caffeine\" \"C8H10N4O2\" {throwTestException;}";
        try
        {
            SieveMailAdapter mail = (SieveMailAdapter) TestUtils.createMail();
            mail.getMessage().addHeader("X-Caffein", "C8H10N4O2");            
            TestUtils.interpret(mail, script);
            isTestPassed = true;            
        }
        catch (MessagingException e)
        {
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
     * Test for Test 'header'
     */
    public void testHeaderContainsTrue()
    {
        boolean isTestPassed = false;
        String script = "if header :contains \"X-Caffeine\" \"C8H10\" {throwTestException;}";
        try
        {
            SieveMailAdapter mail = (SieveMailAdapter) TestUtils.createMail();
            mail.getMessage().addHeader("X-Caffeine", "C8H10N4O2");            
            TestUtils.interpret(mail, script);
        }
        catch (MessagingException e)
        {
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
     * Test for Test 'header'
     */
    public void testHeaderContainsFalse()
    {
        boolean isTestPassed = false;
        String script = "if header :is \"X-Caffeine\" \"C8H10N4O2\" {throwTestException;}";
        try
        {
            SieveMailAdapter mail = (SieveMailAdapter) TestUtils.createMail();
            mail.getMessage().addHeader("X-Caffeine", "izzy");            
            TestUtils.interpret(mail, script);
            isTestPassed = true;            
        }
        catch (MessagingException e)
        {
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
     * Test for Test 'header'
     */
    public void testHeaderContainsNullTrue()
    {
        boolean isTestPassed = false;
        String script = "if header :contains \"X-Caffeine\" \"\" {throwTestException;}";
        try
        {
            SieveMailAdapter mail = (SieveMailAdapter) TestUtils.createMail();
            mail.getMessage().addHeader("X-Caffeine", null);            
            TestUtils.interpret(mail, script);
        }
        catch (MessagingException e)
        {
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
     * Test for Test 'header'
     */
    public void testHeaderIsNullFalse()
    {
        boolean isTestPassed = false;
        String script = "if header :is \"X-Caffeine\" \"\" {throwTestException;}";
        try
        {
            SieveMailAdapter mail = (SieveMailAdapter) TestUtils.createMail();
            mail.getMessage().addHeader("X-Caffeine", null);            
            TestUtils.interpret(mail, script);
            isTestPassed = true;            
        }
        catch (MessagingException e)
        {
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
     * Test for Test 'header'
     */
    public void testHeaderMatchesTrue()
    {
        boolean isTestPassed = false;
        String script = "if header :matches \"X-Caffeine\" \"*10N?O2\" {throwTestException;}";
        try
        {
            SieveMailAdapter mail = (SieveMailAdapter) TestUtils.createMail();
            mail.getMessage().addHeader("X-Caffeine", "C8H10N4O2");            
            TestUtils.interpret(mail, script);
        }
        catch (MessagingException e)
        {
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
     * Test for Test 'header'
     */
    public void testHeaderMatchesFalse()
    {
        boolean isTestPassed = false;
        String script = "if header :matches \"X-Caffeine\" \"*10N?O2\" {throwTestException;}";
        try
        {
            SieveMailAdapter mail = (SieveMailAdapter) TestUtils.createMail();
            mail.getMessage().addHeader("X-Caffeine", "C8H10N4O3");            
            TestUtils.interpret(mail, script);
            isTestPassed = true;            
        }
        catch (MessagingException e)
        {
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
