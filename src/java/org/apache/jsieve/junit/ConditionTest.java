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

import junit.framework.TestCase;

import org.apache.jsieve.CommandException;
import org.apache.jsieve.CommandManager;
import org.apache.jsieve.SieveException;
import org.apache.jsieve.SyntaxException;
import org.apache.jsieve.TestManager;
import org.apache.jsieve.junit.commands.ThrowTestException;
import org.apache.jsieve.junit.utils.TestUtils;
import org.apache.jsieve.parser.generated.ParseException;

/**
 * Class <code>ConditionTest</code> tests the conditional commands if, elsif and
 * else.
 */
public class ConditionTest extends TestCase
{

    /**
     * Constructor for ConditionTest.
     * @param arg0
     */
    public ConditionTest(String arg0)
    {
        super(arg0);
    }

    public static void main(String[] args)
    {
        junit.swingui.TestRunner.run(ConditionTest.class);
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
     * Test for Command 'if' with an argument of 'true'
     */
    public void testIfTrue()
    {
        boolean isTestPassed = false;
        String script = "if true {throwTestException;}";

        try
        {
            TestUtils.interpret(TestUtils.createMail(), script);
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
     * Test for Command 'if' with an argument of 'false'
     */
    public void testIfFalse()
    {
        boolean isTestPassed = false;
        String script = "if false {stop;} throwTestException;";

        try
        {
            TestUtils.interpret(TestUtils.createMail(), script);
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
     * Test for Command 'elsif' with an argument of 'true'
     */
    public void testElsifTrue()
    {
        boolean isTestPassed = false;
        String script = "if false {stop;} elsif true {throwTestException;}";

        try
        {
            TestUtils.interpret(TestUtils.createMail(), script);
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
     * Test for Command 'elsif' with an argument of 'false'
     */
    public void testElsifFalse()
    {
        boolean isTestPassed = false;
        String script = "if false {stop;} elsif false {stop;} throwTestException;";

        try
        {
            TestUtils.interpret(TestUtils.createMail(), script);
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
     * Test for nested Command 'elsif' with an argument of 'true'
     */
    public void testElsifFalseElsifTrue()
    {
        boolean isTestPassed = false;
        String script =
            "if false {stop;} elsif false {stop;} elsif true {throwTestException;}";

        try
        {
            TestUtils.interpret(TestUtils.createMail(), script);
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
     * Test for Command 'else' after 'elseif'
     */
    public void testElsifFalseElse()
    {
        boolean isTestPassed = false;
        String script = "if false {stop;} elsif false {stop;} else {throwTestException;}";

        try
        {
            TestUtils.interpret(TestUtils.createMail(), script);
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
     * Test for Command 'else'
     */
    public void testElse()
    {
        boolean isTestPassed = false;
        String script = "if false {stop;} else {throwTestException;}";

        try
        {
            TestUtils.interpret(TestUtils.createMail(), script);
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
     * Test for Command 'else' out of sequence
     */
    public void testOutOfSequenceElse()
    {
        boolean isTestPassed = false;
        String script = "else {stop;}";

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
     * Test for Command 'elsif' out of sequence
     */
    public void testOutOfSequenceElsif()
    {
        boolean isTestPassed = false;
        String script = "elsif {stop;}";

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
     * Test for Command 'if' without a corresponding Block
     */
    public void testIfMissingBlock()
    {
        boolean isTestPassed = false;
        String script = "if true stop;";

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
     * Test for Command 'if' without a test
     */
    public void testIfMissingTest()
    {
        boolean isTestPassed = false;
        String script = "if {stop;}";

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
     * Test for Command 'if' without a test
     */
    public void testElsifMissingTest()
    {
        boolean isTestPassed = false;
        String script = "if false {stop;} elsif {stop;}";

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
     * Test for Command 'elsif' without a corresponding Block
     */
    public void testElsifMissingBlock()
    {
        boolean isTestPassed = false;
        String script = "if false {stop;} elsif true stop;";

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
     * Test for Command 'else' without a corresponding Block
     */
    public void testElseMissingBlock()
    {
        boolean isTestPassed = false;
        String script = "if false {stop;} else stop;";

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
     * Test for Command 'if' nested in a block
     */
    public void testNestedIf()
    {
        boolean isTestPassed = false;
        String script = "if true {if true {throwTestException;}}";

        try
        {
            TestUtils.interpret(TestUtils.createMail(), script);
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
     * Test for Command 'else' out of sequence nested in a block
     */
    public void testNestedOutOfSequenceElse()
    {
        boolean isTestPassed = false;
        String script = "if true {else {stop;}}";

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
     * Test for Command 'elsif' out of sequence nested in a block
     */
    public void testNestedOutOfSequenceElsif()
    {
        boolean isTestPassed = false;
        String script = "if true {elsif true {stop;}}";

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

}
