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

import org.apache.jsieve.CommandManager;
import org.apache.jsieve.SieveException;
import org.apache.jsieve.TestManager;
import org.apache.jsieve.junit.commands.ThrowTestException;
import org.apache.jsieve.junit.utils.SieveEnvelopeMailAdapter;
import org.apache.jsieve.junit.utils.TestUtils;
import org.apache.jsieve.parser.generated.ParseException;

/**
 * Class EnvelopeTest
 */
public class EnvelopeTest extends TestCase
{

    /**
     * Constructor for AddressTest.
     * @param arg0
     */
    public EnvelopeTest(String arg0)
    {
        super(arg0);
    }

    public static void main(String[] args)
    {
        junit.swingui.TestRunner.run(EnvelopeTest.class);
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
     * Test for Test 'envelope'
     */
    public void testIfEnvelopeAllIsTrue()
    {
        boolean isTestPassed = false;
        String script = "if envelope :all :is \"From\" \"user@domain\" {throwTestException;}";
        try
        {
            SieveEnvelopeMailAdapter mail = TestUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("user@domain");            
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
     * Test for Test 'envelope'
     */
    public void testCaseInsensitiveEnvelopeName()
    {
        boolean isTestPassed = false;
        String script = "if envelope :all :is \"from\" \"user@domain\" {throwTestException;}";
        try
        {
            SieveEnvelopeMailAdapter mail = TestUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("user@domain");            
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
     * Test for Test 'envelope'
     */
    public void testOctetComparatorTrue()
    {
        boolean isTestPassed = false;
        String script = "if envelope :comparator \"i;octet\" :all :is \"From\" \"uSeR@dOmAiN\" {throwTestException;}";
        try
        {
            SieveEnvelopeMailAdapter mail = TestUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("uSeR@dOmAiN");            
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
     * Test for Test 'envelope'
     */
    public void testOctetComparatorFalse()
    {
        boolean isTestPassed = false;
        String script = "if envelope :comparator \"i;octet\" :all :is \"From\" \"uSeR@dOmAiN\" {throwTestException;}";
        try
        {
            SieveEnvelopeMailAdapter mail = TestUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("user@domain");           
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
     * Test for Test 'envelope'
     */
    public void testAsciiComparatorTrue()
    {
        boolean isTestPassed = false;
        String script = "if envelope :comparator \"i;ascii-casemap\" :all :is \"From\" \"uSeR@dOmAiN\" {throwTestException;}";
        try
        {
            SieveEnvelopeMailAdapter mail = TestUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("user@domain");              
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
     * Test for Test 'envelope'
     */
    public void testAsciiComparatorFalse()
    {
        boolean isTestPassed = false;
        String script = "if envelope :comparator \"i;ascii-casemap\" :all :is \"From\" \"uSeR@dOmAiN\" {throwTestException;}";
        try
        {
            SieveEnvelopeMailAdapter mail = TestUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("user@domain1");
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
     * Test for Test 'envelope'
     */
    public void testIfEnvelopeAllIsMultiTrue1()
    {
        boolean isTestPassed = false;
        String script = "if envelope :all :is [\"From\", \"To\"] \"user@domain\" {throwTestException;}";
        try
        {
            SieveEnvelopeMailAdapter mail = TestUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("user@domain");
            mail.setEnvelopeTo("user@domain");                       
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
     * Test for Test 'envelope'
     */
    public void testIfEnvelopeAllIsMultiTrue2()
    {
        boolean isTestPassed = false;
        String script = "if envelope :all :is [\"From\", \"To\"] [\"user@domain\", \"tweety@pie\"] {throwTestException;}";
        try
        {
            SieveEnvelopeMailAdapter mail = TestUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("user@domain");
            mail.setEnvelopeTo("user@domain");           
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
     * Test for Test 'envelope'
     */
    public void testIfEnvelopeAllIsMultiTrue3()
    {
        boolean isTestPassed = false;
        String script = "if envelope :all :is [\"From\", \"To\"] [\"user@domain\", \"tweety@pie\"] {throwTestException;}";
        try
        {
            SieveEnvelopeMailAdapter mail = TestUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("user@domain");
            mail.setEnvelopeTo("tweety@pie");          
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
     * Test for Test 'envelope'
     */
    public void testIfEnvelopeAllIsMultiTrue4()
    {
        boolean isTestPassed = false;
        String script = "if envelope :all :is [\"From\", \"To\"] [\"user@domain\", \"tweety@pie\"] {throwTestException;}";
        try
        {
            SieveEnvelopeMailAdapter mail = TestUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("tweety@pie");
            mail.setEnvelopeTo("tweety@pie");           
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
     * Test for Test 'envelope'
     */
    public void testIfEnvelopeAllMatchesTrue()
    {
        boolean isTestPassed = false;
        String script = "if envelope :all :matches \"From\" \"*@domain\" {throwTestException;}";
        try
        {
            SieveEnvelopeMailAdapter mail = TestUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("user@domain");          
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
     * Test for Test 'envelope'
     */
    public void testIfEnvelopeAllContainsTrue()
    {
        boolean isTestPassed = false;
        String script = "if envelope :all :contains \"From\" \"r@dom\" {throwTestException;}";
        try
        {
            SieveEnvelopeMailAdapter mail = TestUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("user@domain");              
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
     * Test for Test 'envelope'
     */
    public void testIfEnvelopeLocalpartIsTrue()
    {
        boolean isTestPassed = false;
        String script = "if envelope :localpart :is \"From\" \"user\" {throwTestException;}";
        try
        {
            SieveEnvelopeMailAdapter mail = TestUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("user@domain");           
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
     * Test for Test 'envelope'
     */
    public void testIfEnvelopeLocalpartMatchesTrue()
    {
        boolean isTestPassed = false;
        String script = "if envelope :localpart :matches \"From\" \"*er\" {throwTestException;}";
        try
        {
            SieveEnvelopeMailAdapter mail = TestUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("user@domain");            
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
     * Test for Test 'envelope'
     */
    public void testIfEnvelopeLocalpartContainsTrue()
    {
        boolean isTestPassed = false;
        String script = "if envelope :localpart :contains \"From\" \"r\" {throwTestException;}";
        try
        {
            SieveEnvelopeMailAdapter mail = TestUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("user@domain");            
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
     * Test for Test 'envelope'
     */
    public void testIfEnvelopeDomainIsTrue()
    {
        boolean isTestPassed = false;
        String script = "if envelope :domain :is \"From\" \"domain\" {throwTestException;}";

        try
        {
            SieveEnvelopeMailAdapter mail = TestUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("user@domain");            
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
     * Test for Test 'envelope'
     */
    public void testIfEnvelopeDomainMatchesTrue()
    {
        boolean isTestPassed = false;
        String script = "if envelope :domain :matches \"From\" \"*main\" {throwTestException;}";
        try
        {
            SieveEnvelopeMailAdapter mail = TestUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("user@domain");           
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
     * Test for Test 'envelope'
     */
    public void testIfEnvelopeDomainContainsTrue()
    {
        boolean isTestPassed = false;
        String script = "if envelope :domain :contains \"From\" \"dom\" {throwTestException;}";
        try
        {
            SieveEnvelopeMailAdapter mail = TestUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("user@domain");            
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
     * Test for Test 'envelope'
     */
    public void testIfEnvelopeAllIsFalse()
    {
        boolean isTestPassed = false;
        String script = "if envelope :all :is \"From\" \"user@domain\" {throwTestException;}";
        try
        {
            SieveEnvelopeMailAdapter mail = TestUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("tweety@pie");             
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
     * Test for Test 'envelope'
     */
    public void testIfEnvelopeAllMatchesFalse()
    {
        boolean isTestPassed = false;
        String script = "if envelope :all :matches \"From\" \"(.*)@domain\" {throwTestException;}";
        try
        {
            SieveEnvelopeMailAdapter mail = TestUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("bugs@bunny");          
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
     * Test for Test 'envelope'
     */
    public void testIfEnvelopeAllContainsFalse()
    {
        boolean isTestPassed = false;
        String script = "if envelope :all :contains \"From\" \"r@dom\" {throwTestException;}";
        try
        {
            SieveEnvelopeMailAdapter mail = TestUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("tweety@pie");             
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
     * Test for Test 'envelope'
     */
    public void testIfEnvelopeLocalpartIsFalse()
    {
        boolean isTestPassed = false;
        String script = "if envelope :localpart :is \"From\" \"user\" {throwTestException;}";
        try
        {
            SieveEnvelopeMailAdapter mail = TestUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("tweety@pie");            
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
     * Test for Test 'envelope'
     */
    public void testIfEnvelopeLocalpartMatchesFalse()
    {
        boolean isTestPassed = false;
        String script = "if envelope :localpart :matches \"From\" \"(.*)er\" {throwTestException;}";
        try
        {
            SieveEnvelopeMailAdapter mail = TestUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("tweety@pie");            
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
     * Test for Test 'envelope'
     */
    public void testIfEnvelopeLocalpartContainsFalse()
    {
        boolean isTestPassed = false;
        String script = "if envelope :localpart :contains \"From\" \"r\" {throwTestException;}";
        try
        {
            SieveEnvelopeMailAdapter mail = TestUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("tweety@pie");            
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
     * Test for Test 'envelope'
     */
    public void testIfEnvelopeDomainIsFalse()
    {
        boolean isTestPassed = false;
        String script = "if envelope :domain :is \"From\" \"domain\" {throwTestException;}";

        try
        {
            SieveEnvelopeMailAdapter mail = TestUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("tweety@pie");            
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
     * Test for Test 'envelope'
     */
    public void testIfEnvelopeDomainMatchesFalse()
    {
        boolean isTestPassed = false;
        String script = "if envelope :domain :matches \"From\" \"(.*)main\" {throwTestException;}";
        try
        {
            SieveEnvelopeMailAdapter mail = TestUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("tweety@pie");             
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
     * Test for Test 'envelope'
     */
    public void testIfEnvelopeDomainContainsFalse()
    {
        boolean isTestPassed = false;
        String script = "if envelope :domain :contains \"From\" \"dom\" {throwTestException;}";
        try
        {
            SieveEnvelopeMailAdapter mail = TestUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("tweety@pie");          
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
     * Test for Test 'envelope'
     */
    public void testIfEnvelopeAllIsMultiFalse1()
    {
        boolean isTestPassed = false;
        String script = "if envelope :all :is [\"From\", \"To\"] \"user@domain\" {throwTestException;}";
        try
        {
            SieveEnvelopeMailAdapter mail = TestUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("bugs@bunny");
            mail.setEnvelopeTo("bugs@bunny");            
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
     * Test for Test 'envelope'
     */
    public void testIfEnvelopeAllIsMultiFalse2()
    {
        boolean isTestPassed = false;
        String script = "if envelope :all :is [\"From\", \"To\"] [\"user@domain\", \"tweety@pie\"] {throwTestException;}";
        try
        {
            SieveEnvelopeMailAdapter mail = TestUtils.createEnvelopeMail();
            mail.setEnvelopeFrom("bugs@bunny");
            mail.setEnvelopeTo("bugs@bunny");              
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
