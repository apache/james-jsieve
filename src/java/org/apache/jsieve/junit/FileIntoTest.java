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

import org.apache.jsieve.CommandManager;
import org.apache.jsieve.SieveException;
import org.apache.jsieve.SyntaxException;
import org.apache.jsieve.TestManager;
import org.apache.jsieve.junit.utils.*;
import org.apache.jsieve.mail.ActionFileInto;
import org.apache.jsieve.mail.MailAdapter;
import org.apache.jsieve.parser.generated.ParseException;

import junit.framework.TestCase;

/**
 * Class FileIntoTest
 */
public class FileIntoTest extends TestCase
{

    /**
     * Constructor for FileIntoTest.
     * @param arg0
     */
    public FileIntoTest(String arg0)
    {
        super(arg0);
    }

    public static void main(String[] args)
    {
        junit.swingui.TestRunner.run(FileIntoTest.class);
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
     * Test for Command 'fileinto'
     */
    public void testFileInto()
    {
        boolean isTestPassed = false;
        String script = "fileinto \"INBOX.test1\"; fileinto \"INBOX.test2\";";

        try
        {
            MailAdapter mail = TestUtils.createMail();
            TestUtils.interpret(mail, script);
            assertTrue(mail.getActions().size() == 2);
            assertTrue(mail.getActions().get(0) instanceof ActionFileInto);
            assertTrue(
                ((ActionFileInto) mail.getActions().get(0))
                    .getDestination()
                    .equals(
                    "INBOX.test1"));
            assertTrue(mail.getActions().get(1) instanceof ActionFileInto);
            assertTrue(
                ((ActionFileInto) mail.getActions().get(1))
                    .getDestination()
                    .equals(
                    "INBOX.test2"));
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
     * Test for Command 'fileinto' with duplicate destinations.
     * Only one ActionFileInto should result.
     */
    public void testDuplicateFileInto()
    {
        boolean isTestPassed = false;
        String script = "fileinto \"INBOX.test1\"; fileinto \"INBOX.test1\";";

        try
        {
            MailAdapter mail = TestUtils.createMail();
            TestUtils.interpret(mail, script);
            assertTrue(mail.getActions().size() == 1);
            assertTrue(mail.getActions().get(0) instanceof ActionFileInto);
            assertTrue(
                ((ActionFileInto) mail.getActions().get(0))
                    .getDestination()
                    .equals(
                    "INBOX.test1"));
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
     * Test for Command 'fileinto' with an invalid argument type
     */
    public void testInvalidArgumentType()
    {
        boolean isTestPassed = false;
        String script = "fileinto 1 ;";

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
     * Test for Command 'fileinto' with an invalid argument number
     */
    public void testInvalidArgumentNumber()
    {
        boolean isTestPassed = false;
        String script = "fileinto [\"INBOX.test\", \"elsewhere\"];";

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
     * Test for Command 'fileinto' with an invalid block
     */
    public void testInvalidBlock()
    {
        boolean isTestPassed = false;
        String script = "fileinto 1 {throwTestException;}";

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
