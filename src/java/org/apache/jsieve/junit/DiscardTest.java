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

import org.apache.jsieve.CommandManager;
import org.apache.jsieve.SieveException;
import org.apache.jsieve.SyntaxException;
import org.apache.jsieve.TestManager;
import org.apache.jsieve.junit.utils.*;
import org.apache.jsieve.mail.MailAdapter;
import org.apache.jsieve.parser.generated.ParseException;

import junit.framework.TestCase;

/**
 * Class DiscardTest
 */
public class DiscardTest extends TestCase
{

    /**
     * Constructor for DiscardTest.
     * @param arg0
     */
    public DiscardTest(String arg0)
    {
        super(arg0);
    }

    public static void main(String[] args)
    {
        junit.swingui.TestRunner.run(DiscardTest.class);
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
     * Test for Command 'discard' with invalid arguments
     */
    public void testInvalidArguments()
    {
        boolean isTestPassed = false;
        String script = "discard 1 ;";

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
     * Test for Command 'discard' with an invalid block
     */
    public void testInvalidBlock()
    {
        boolean isTestPassed = false;
        String script = "discard 1 {throwTestException;}";

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



    /*
     * Test for Command 'discard'
     */
    public void testDiscard()
    {
        boolean isTestPassed = false;
        String script = "discard;";

        try
        {
            MailAdapter mail = TestUtils.createMail();
            TestUtils.interpret(mail, script);
            assertTrue(mail.getActions().isEmpty());
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
