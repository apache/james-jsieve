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
package org.apache.jsieve.util;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

public class SieveToXmlTest extends MockObjectTestCase {

    SieveToXml subject;
    Mock mockOut;
    SieveHandler instance;
    
    protected void setUp() throws Exception {
        super.setUp();
        subject = new SieveToXml();
        mockOut = mock(SieveToXml.Out.class);
        instance = subject.build((SieveToXml.Out) mockOut.proxy());
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private void assertElementIsOutput(final String commandName, final String elementName) throws HaltTraversalException {
        mockOut.expects(once()).method("openElement").with(eq(elementName), 
                eq(SieveToXml.DEFAULT_NAMESPACE), eq(SieveToXml.DEFAULT_PREFIX)).id("1");
        mockOut.expects(once()).method("attribute").with(eq(SieveToXml.DEFAULT_NAME_ATTRIBUTE), eq(SieveToXml.DEFAULT_NAMESPACE), 
                eq(SieveToXml.DEFAULT_PREFIX), eq(commandName)).id("2");
        mockOut.expects(once()).method("closeElement").after("2");
        assertBuilderIsReturned(instance.startCommand(commandName));
        assertBuilderIsReturned(instance.endCommand(commandName));
    }

    private void assertBuilderIsReturned(SieveHandler handler) {
        assertEquals("Builder pattern so instance should be returned", instance, handler);
    }
    
    public void testBuildIsNotNull() {
        assertNotNull(instance);
    }
    
    public void testStartScriptShouldBeIgnored() throws Exception {
        assertBuilderIsReturned(instance.startScript());
    }
    
    public void testEndScriptShouldBeIgnored() throws Exception {
        assertBuilderIsReturned(instance.endScript());
    }
    
    public void testStartBlockShouldBeIgnored() throws Exception {
        assertBuilderIsReturned(instance.startBlock());
    }
    
    public void testEndBlockShouldBeIgnored() throws Exception {
        assertBuilderIsReturned(instance.endBlock());
    }
    
    public void testStartCommandsShouldBeIgnored() throws Exception {
        assertBuilderIsReturned(instance.startCommands());
    }
    
    public void testEndCommandsShouldBeIgnored() throws Exception {
        assertBuilderIsReturned(instance.endCommands());
    }
    
    public void testControlCommandShouldOutputElement() throws Exception {
        assertElementIsOutput("if", SieveToXml.DEFAULT_NAME_CONTROL_COMMAND);
    }

    public void testActionCommand() throws Exception {
        assertElementIsOutput("other", SieveToXml.DEFAULT_NAME_ACTION_COMMAND);
    }
    
    public void testStringArgument() throws Exception {
        final String stringArgument = "A String Tag";
        mockOut.expects(once()).method("openElement").with(eq(SieveToXml.DEFAULT_NAME_STRING), 
                eq(SieveToXml.DEFAULT_NAMESPACE), eq(SieveToXml.DEFAULT_PREFIX)).id("1");
        mockOut.expects(once()).method("content").with(eq(stringArgument)).after("1").id("2");
        mockOut.expects(once()).method("closeElement").after("2");
        assertBuilderIsReturned(instance.listMember(stringArgument));
    }
    
    public void testNumericArgument()throws Exception {
        int number = 42;
        mockOut.expects(once()).method("openElement").with(eq(SieveToXml.DEFAULT_NAME_NUM), 
                eq(SieveToXml.DEFAULT_NAMESPACE), eq(SieveToXml.DEFAULT_PREFIX)).id("1");
        mockOut.expects(once()).method("content").with(eq(Integer.toString(number))).after("1").id("2");
        mockOut.expects(once()).method("closeElement").after("2");
        assertBuilderIsReturned(instance.argument(number));
    }
    
    public void testTagArgument() throws Exception {
        String tag = "A Tag";
        mockOut.expects(once()).method("openElement").with(eq(SieveToXml.DEFAULT_NAME_TAG), 
                eq(SieveToXml.DEFAULT_NAMESPACE), eq(SieveToXml.DEFAULT_PREFIX)).id("1");
        mockOut.expects(once()).method("content").with(eq(tag)).after("1").id("2");
        mockOut.expects(once()).method("closeElement").after("2");
        assertBuilderIsReturned(instance.argument(tag));
    }
    
    public void testStartTestList() throws Exception {
        mockOut.expects(once()).method("openElement").with(eq(SieveToXml.DEFAULT_NAME_LIST), 
                eq(SieveToXml.DEFAULT_NAMESPACE), eq(SieveToXml.DEFAULT_PREFIX));
        assertBuilderIsReturned(instance.startTestList());
    }
    
    public void testEndTestList() throws Exception {
        mockOut.expects(once()).method("closeElement");
        assertBuilderIsReturned(instance.endTestList());
    }
    
    public void testTest() throws Exception {
        final String testName = "is";
        mockOut.expects(once()).method("openElement").with(eq(SieveToXml.DEFAULT_NAME_TEST), 
                eq(SieveToXml.DEFAULT_NAMESPACE), eq(SieveToXml.DEFAULT_PREFIX)).id("1");
        mockOut.expects(once()).method("attribute").with(eq(SieveToXml.DEFAULT_NAME_ATTRIBUTE), eq(SieveToXml.DEFAULT_NAMESPACE), 
                eq(SieveToXml.DEFAULT_PREFIX), eq(testName)).id("2");
        mockOut.expects(once()).method("closeElement").after("2");
        assertBuilderIsReturned(instance.startTest(testName));
        assertBuilderIsReturned(instance.endTest(testName));
    }
   
}
