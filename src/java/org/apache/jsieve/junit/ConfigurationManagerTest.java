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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.jsieve.ConfigurationManager;
import org.apache.jsieve.SieveConfigurationException;

import junit.framework.TestCase;

/**
 * Class ConfigurationManagerTest
 */
public class ConfigurationManagerTest extends TestCase
{

    /**
     * Constructor for ConfigurationManagerTest.
     * @param arg0
     */
    public ConfigurationManagerTest(String arg0)
    {
        super(arg0);
    }

    public static void main(String[] args)
    {
        junit.swingui.TestRunner.run(ConfigurationManagerTest.class);
    }

    /**
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    /**
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }
    
    /**
     * Test the CommandMap maps 'MUST' and 'SHOULD' be supported commands to the 
     * correct classes.
     */
    public void testCommandMap()
    {
        Map map = new HashMap();
        // Condition Commands
        // RFC3082 - Implementations MUST support these:
        map.put("if", "org.apache.jsieve.commands.If");
        map.put("else", "org.apache.jsieve.commands.Else");
        map.put("elsif", "org.apache.jsieve.commands.Elsif");
        map.put("require", "org.apache.jsieve.commands.Require");
        map.put("stop", "org.apache.jsieve.commands.Stop");

        // Action Commands        
        // RFC3082 - Implementations MUST support these:
        map.put("keep", "org.apache.jsieve.commands.Keep");
        map.put("discard", "org.apache.jsieve.commands.Discard");
        map.put("redirect", "org.apache.jsieve.commands.Redirect");
        // RFC3082 - Implementations SHOULD support these:       
        map.put("reject", "org.apache.jsieve.commands.optional.Reject");
        map.put("fileinto", "org.apache.jsieve.commands.optional.FileInto");

        boolean isTestPassed = false;
        try
        {
            Map commandMap = ConfigurationManager.getInstance().getCommandMap();

            Iterator mapIter = map.entrySet().iterator();
            while (mapIter.hasNext())
            {
                Map.Entry entry = (Map.Entry) mapIter.next();
                assertTrue(
                    "Key: " + entry.getKey(),
                    commandMap.containsKey(entry.getKey()));
                assertTrue(
                    "Value: " + entry.getValue(),
                    commandMap.get(entry.getKey()).equals(entry.getValue()));
            }
            isTestPassed = true;
        }
        catch (SieveConfigurationException e)
        {
        }
        assertTrue(isTestPassed);
    }
    
    /**
     * Test the TestMap maps 'MUST' and 'SHOULD' be supported tests to the 
     * correct classes.
     */
    public void testTestMap()
    {
        Map map = new HashMap();

        // RFC3082 - Implementations MUST support these tests:
        map.put("address", "org.apache.jsieve.tests.Address");
        map.put("allof", "org.apache.jsieve.tests.AllOf");
        map.put("anyof", "org.apache.jsieve.tests.AnyOf");
        map.put("exists", "org.apache.jsieve.tests.Exists");
        map.put("false", "org.apache.jsieve.tests.False");
        map.put("header", "org.apache.jsieve.tests.Header");
        map.put("not", "org.apache.jsieve.tests.Not");
        map.put("size", "org.apache.jsieve.tests.Size");
        map.put("true", "org.apache.jsieve.tests.True");

        // RFC3082 - Implementations SHOULD support the "envelope" test.
        map.put("envelope", "org.apache.jsieve.tests.optional.Envelope");

        boolean isTestPassed = false;
        try
        {
            Map testMap = ConfigurationManager.getInstance().getTestMap();

            Iterator mapIter = map.entrySet().iterator();
            while (mapIter.hasNext())
            {
                Map.Entry entry = (Map.Entry) mapIter.next();
                assertTrue(
                    "Key: " + entry.getKey(),
                    testMap.containsKey(entry.getKey()));
                assertTrue(
                    "Value: " + entry.getValue(),
                    testMap.get(entry.getKey()).equals(entry.getValue()));
            }
            isTestPassed = true;
        }
        catch (SieveConfigurationException e)
        {
        }
        assertTrue(isTestPassed);
    } 
    
    /**
     * Test the CommparatorMap maps 'MUST' and 'SHOULD' be supported comparators to
     * the correct classes.
     */
    public void testComparatorMap()
    {
        Map map = new HashMap();

        // RFC3082 - Required Comparators
        map.put("i;octet", "org.apache.jsieve.comparators.Octet");
        map.put("i;ascii-casemap", "org.apache.jsieve.comparators.AsciiCasemap");

        boolean isTestPassed = false;
        try
        {
            Map comparatorMap = ConfigurationManager.getInstance().getComparatorMap();

            Iterator mapIter = map.entrySet().iterator();
            while (mapIter.hasNext())
            {
                Map.Entry entry = (Map.Entry) mapIter.next();
                assertTrue(
                    "Key: " + entry.getKey(),
                    comparatorMap.containsKey(entry.getKey()));
                assertTrue(
                    "Value: " + entry.getValue(),
                    comparatorMap.get(entry.getKey()).equals(entry.getValue()));
            }
            isTestPassed = true;
        }
        catch (SieveConfigurationException e)
        {
        }
        assertTrue(isTestPassed);
    }    
       


        

}
