/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2004 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache", "Jakarta", "JAMES", "JSieve" and 
 *    "Apache Software Foundation" must not be used to endorse or promote
 *    products derived from this software without prior written permission.
 *    For written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * Portions of this software are based upon public domain software
 * originally written at the National Center for Supercomputing Applications,
 * University of Illinois, Urbana-Champaign.
 */
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
