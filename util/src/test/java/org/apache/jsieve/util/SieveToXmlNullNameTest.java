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

public class SieveToXmlNullNameTest extends MockObjectTestCase {

    SieveToXml subject;
    Mock mockOut;
    SieveHandler instance;
    
    protected void setUp() throws Exception {
        super.setUp();
        subject = new SieveToXml();
        subject.setNameAttributeName(null);
        mockOut = mock(SieveToXml.Out.class);
        instance = subject.build((SieveToXml.Out) mockOut.proxy());
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private void assertElementIsOutput(final String commandName, final String elementName) throws HaltTraversalException {
        mockOut.expects(once()).method("openElement").with(eq(elementName), 
                eq(SieveToXml.DEFAULT_NAMESPACE), eq(SieveToXml.DEFAULT_PREFIX)).id("1");
        mockOut.expects(once()).method("closeElement").after("1");
        assertBuilderIsReturned(instance.startCommand(commandName));
        assertBuilderIsReturned(instance.endCommand(commandName));
    }

    private void assertBuilderIsReturned(SieveHandler handler) {
        assertEquals("Builder pattern so instance should be returned", instance, handler);
    }
    
    public void testBuildIsNotNull() {
        assertNotNull(instance);
    }
        
    public void testControlCommandShouldOutputElement() throws Exception {
        assertElementIsOutput("if", SieveToXml.DEFAULT_NAME_CONTROL_COMMAND);
    }

    public void testActionCommandShouldOutputElement() throws Exception {
        assertElementIsOutput("other", SieveToXml.DEFAULT_NAME_ACTION_COMMAND);
    }
    
    public void testTestShouldOutputElement() throws Exception {
        final String testName = "is";
        mockOut.expects(once()).method("openElement").with(eq(SieveToXml.DEFAULT_NAME_TEST), 
                eq(SieveToXml.DEFAULT_NAMESPACE), eq(SieveToXml.DEFAULT_PREFIX)).id("1");
        mockOut.expects(once()).method("closeElement").after("1");
        assertBuilderIsReturned(instance.startTest(testName));
        assertBuilderIsReturned(instance.endTest(testName));
    }
   
}
