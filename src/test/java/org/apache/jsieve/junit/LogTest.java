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

import org.apache.jsieve.CommandManager;
import org.apache.jsieve.TestManager;
import org.apache.jsieve.exception.SieveException;
import org.apache.jsieve.junit.utils.JUnitUtils;
import org.apache.jsieve.parser.generated.ParseException;

import junit.framework.TestCase;

/**
 * Class LogTest
 */
public class LogTest extends TestCase
{

    /**
     * Constructor for LogTest.
     * @param arg0
     */
    public LogTest(String arg0)
    {
        super(arg0);
    }

    public static void main(String[] args)
    {
        junit.swingui.TestRunner.run(LogTest.class);
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
     * Test for Command 'log'.
     */
    public void testLogDebug()
    {
        boolean isTestPassed = false;
        String script = "log :debug \"Log a debug message.\";";

        try
        {
            JUnitUtils.interpret(JUnitUtils.createMail(), script);
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
     * Test for Command 'log'.
     */
    public void testLogError()
    {
        boolean isTestPassed = false;
        String script = "log :error \"Log an error message.\";";

        try
        {
            JUnitUtils.interpret(JUnitUtils.createMail(), script);
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
     * Test for Command 'log'.
     */
    public void testLogFatal()
    {
        boolean isTestPassed = false;
        String script = "log :fatal \"Log a fatal message.\";";

        try
        {
            JUnitUtils.interpret(JUnitUtils.createMail(), script);
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
     * Test for Command 'log'.
     */
    public void testLogInfo()
    {
        boolean isTestPassed = false;
        String script = "log :info \"Log an info message.\";";

        try
        {
            JUnitUtils.interpret(JUnitUtils.createMail(), script);
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
     * Test for Command 'log'.
     */
    public void testLogTrace()
    {
        boolean isTestPassed = false;
        String script = "log :trace \"Log a trace message.\";";

        try
        {
            JUnitUtils.interpret(JUnitUtils.createMail(), script);
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
     * Test for Command 'log'.
     */
    public void testLogWarn()
    {
        boolean isTestPassed = false;
        String script = "log :warn \"Log a warning message.\";";

        try
        {
            JUnitUtils.interpret(JUnitUtils.createMail(), script);
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
     * Test for Command 'log'.
     */
    public void testLogDefault()
    {
        boolean isTestPassed = false;
        String script = "log \"Log a default message.\";";

        try
        {
            JUnitUtils.interpret(JUnitUtils.createMail(), script);
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
