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

import org.apache.jsieve.CommandException;
import org.apache.jsieve.CommandManager;
import org.apache.jsieve.FeatureException;
import org.apache.jsieve.SieveException;
import org.apache.jsieve.SyntaxException;
import org.apache.jsieve.TestManager;
import org.apache.jsieve.junit.utils.*;
import org.apache.jsieve.parser.generated.ParseException;

/**
 * Class RequireTest
 */
public class RequireTest extends TestCase
{

    /**
     * Constructor for RequireTest.
     * @param arg0
     */
    public RequireTest(String arg0)
    {
        super(arg0);
    }

    public static void main(String[] args)
    {
        junit.swingui.TestRunner.run(RequireTest.class);
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
     * Test for Command 'require' with a single command that is present
     */
    public void testSingleCommandSatisfied()
    {
        boolean isTestPassed = false;
        String script = "require \"if\";";

        try
        {
            TestUtils.interpret(TestUtils.createMail(), script);
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
     * Test for Command 'require' with a single test that is present
     */
    public void testSingleTestSatisfied()
    {
        boolean isTestPassed = false;
        String script = "require \"true\";";

        try
        {
            TestUtils.interpret(TestUtils.createMail(), script);
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
     * Test for Command 'require' with multiple commands that are present
     */
    public void testMultipleCommandSatisfied()
    {
        boolean isTestPassed = false;
        String script = "require [\"if\", \"elsif\", \"else\"];";

        try
        {
            TestUtils.interpret(TestUtils.createMail(), script);
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
     * Test for Command 'require' with multiple tests that are present
     */
    public void testMultipleTestSatisfied()
    {
        boolean isTestPassed = false;
        String script = "require [\"true\", \"false\", \"not\"];";

        try
        {
            TestUtils.interpret(TestUtils.createMail(), script);
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
     * Test for Command 'require' with a single command that is absent
     */
    public void testSingleCommandUnsatisfied()
    {
        boolean isTestPassed = false;
        String script = "require \"absent\";";

        try
        {
            TestUtils.interpret(TestUtils.createMail(), script);
        }
        catch (FeatureException e)
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
     * Test for Command 'require' with a single test that is absent
     */
    public void testSingleTestUnsatisfied()
    {
        boolean isTestPassed = false;
        String script = "require \"absent\";";

        try
        {
            TestUtils.interpret(TestUtils.createMail(), script);
        }
        catch (FeatureException e)
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
     * Test for Command 'require' for missing argument
     */
    public void testMissingArgument()
    {
        boolean isTestPassed = false;
        String script = "require;";

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
     * Test for Command 'require' for extra argument
     */
    public void testExtraArgument()
    {
        boolean isTestPassed = false;
        String script = "require \"if\" 1;";

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
     * Test for Command 'require' rejecting Blocks
     */
    public void testRejectBlock()
    {
        boolean isTestPassed = false;
        String script = "require \"if\" {stop;}";

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
     * Test for Command 'require' after a Command
     */
    public void testInterveningCommand()
    {
        boolean isTestPassed = false;
        String script = "fileinto \"someplace\"; require \"fileinto\";";

        try
        {
            TestUtils.interpret(TestUtils.createMail(), script);
        }
        catch (CommandException e)
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
     * Test for Command 'require' rejecting invalid arguments
     */
    public void testRejectInvalidArgument()
    {
        boolean isTestPassed = false;
        String script = "require 1 ;";

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
     * Test for Command 'require' with a multiple commands of which one is absent
     */
    public void testMultipleCommandsUnsatisfied()
    {
        boolean isTestPassed = false;
        String script = "require [\"if\", \"elsif\", \"absent\"];";

        try
        {
            TestUtils.interpret(TestUtils.createMail(), script);
        }
        catch (FeatureException e)
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
     * Test for Command 'require' with a multiple tests of which one is absent
     */
    public void testMultipleTestsUnsatisfied()
    {
        boolean isTestPassed = false;
        String script = "require [\"true\", \"false\", \"absent\"];";

        try
        {
            TestUtils.interpret(TestUtils.createMail(), script);
        }
        catch (FeatureException e)
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
