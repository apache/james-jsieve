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

import org.apache.jsieve.CommandManager;
import org.apache.jsieve.SieveException;
import org.apache.jsieve.SyntaxException;
import org.apache.jsieve.TestManager;
import org.apache.jsieve.junit.commands.ThrowTestException;
import org.apache.jsieve.junit.utils.*;
import org.apache.jsieve.parser.generated.ParseException;

import junit.framework.TestCase;

/**
 * Class ExistsTest
 */
public class ExistsTest extends TestCase
{

    /**
     * Constructor for TrueTest.
     * @param arg0
     */
    public ExistsTest(String arg0)
    {
        super(arg0);
    }

    public static void main(String[] args)
    {
        junit.swingui.TestRunner.run(ExistsTest.class);
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
     * Test for Test 'exists'
     */
    public void testExistsTrue()
    {
        boolean isTestPassed = false;
        String script = "if exists \"From\" {throwTestException;}";

        try
        {
            SieveMailAdapter mail = (SieveMailAdapter) TestUtils.createMail();
            mail.getMessage().addHeader("From", "tweety@pie");
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
     * Test for Test 'exists'
     */
    public void testCaseInsensitivity()
    {
        boolean isTestPassed = false;
        String script = "if exists \"From\" {throwTestException;}";

        try
        {
            SieveMailAdapter mail = (SieveMailAdapter) TestUtils.createMail();
            mail.getMessage().addHeader("from", "tweety@pie");
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
     * Test for Test 'exists'
     */
    public void testExistsTrueTrue()
    {
        boolean isTestPassed = false;
        String script = "if exists [\"From\", \"X-Files\"] {throwTestException;}";

        try
        {
            SieveMailAdapter mail = (SieveMailAdapter) TestUtils.createMail();
            mail.getMessage().addHeader("From", "tweety@pie");
            mail.getMessage().addHeader("X-Files", "spooks@everywhere");            
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
     * Test for Test 'exists'
     */
    public void testExistsTrueFalse()
    {
        boolean isTestPassed = false;
        String script =
            "if exists [\"From\", \"X-Files\"] {stop;} throwTestException;";
        try
        {
            SieveMailAdapter mail = (SieveMailAdapter) TestUtils.createMail();
            mail.getMessage().addHeader("From", "tweety@pie");
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
     * Test for Test 'exists'
     */
    public void testExistsFalse()
    {
        boolean isTestPassed = false;
        String script = "if exists \"From\" {stop;} throwTestException;";

        try
        {
            SieveMailAdapter mail = (SieveMailAdapter) TestUtils.createMail();
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
     * Test for Test 'exists'
     */
    public void testExistsFalseFalse()
    {
        boolean isTestPassed = false;
        String script = "if exists [\"From\", \"X-Files\"] {stop;} throwTestException;";

        try
        {
            SieveMailAdapter mail = (SieveMailAdapter) TestUtils.createMail();
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
     * Test for Test 'exists' with invalid numeric argument
     */
    public void testInvalidNumericArgument()
    {
        boolean isTestPassed = false;
        String script = "if exists 1 {throwTestException;}";

        try
        {
            TestUtils.interpret(TestUtils.createMail(), script);
        }
        catch (SyntaxException e)
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
     * Test for Test 'exists' with invalid test argument
     */
    public void testInvalidTestArgument()
    {
        boolean isTestPassed = false;
        String script = "if exists not {throwTestException;}";

        try
        {
            TestUtils.interpret(TestUtils.createMail(), script);
        }
        catch (SyntaxException e)
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

}
