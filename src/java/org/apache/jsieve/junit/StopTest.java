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

import junit.framework.TestCase;

import org.apache.jsieve.CommandManager;
import org.apache.jsieve.SieveException;
import org.apache.jsieve.SyntaxException;
import org.apache.jsieve.TestManager;
import org.apache.jsieve.junit.commands.ThrowTestException;
import org.apache.jsieve.junit.utils.TestUtils;
import org.apache.jsieve.mail.ActionKeep;
import org.apache.jsieve.mail.MailAdapter;
import org.apache.jsieve.parser.generated.ParseException;

/**
 * Class StopTest
 */
public class StopTest extends TestCase
{

    /**
     * Constructor for StopTest.
     * @param arg0
     */
    public StopTest(String arg0)
    {
        super(arg0);
    }

    public static void main(String[] args)
    {
        junit.swingui.TestRunner.run(StopTest.class);
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
     * Test for Command 'stop'. This has an implicit Keep.
     */
    public void testStop()
    {
        boolean isTestPassed = false;
        String script = "stop; throwTestException;";

        try
        {          
            MailAdapter mail = TestUtils.createMail();
            TestUtils.interpret(mail, script);
            assertTrue(mail.getActions().size() == 1);
            assertTrue(mail.getActions().get(0) instanceof ActionKeep);
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
     * Test for Command 'stop' with invalid arguments
     */
    public void testInvalidArguments()
    {
        boolean isTestPassed = false;
        String script = "stop 1 ;";

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
     * Test for Command 'stop' with an invalid block
     */
    public void testInvalidBlock()
    {
        boolean isTestPassed = false;
        String script = "stop 1 {throwTestException;}";

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
