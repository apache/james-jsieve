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

import java.io.ByteArrayInputStream;
import java.io.StringWriter;

import org.apache.jsieve.ConfigurationManager;
import org.apache.jsieve.parser.generated.Node;

import junit.framework.TestCase;

public class XmlGenerationTest extends TestCase {

    //@Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    //@Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testShouldGenerateXmlFromSimpleScript() throws Exception {
        // Set up
        final String script = "if address :all :is \"from\" \"user@domain\" {stop;}";
        final Node node = new ConfigurationManager().build().parse(new ByteArrayInputStream(script.getBytes()));
        final StringWriter monitor = new StringWriter();
        
        // Exercise
        OutputUtils.toXml(node, monitor);
        
        // Verify
        assertEquals("<sieve:control xmlns:sieve='urn:ietf:params:xml:ns:sieve' sieve:name='if'>" +
                "<sieve:test sieve:name='address'>" +
                "<sieve:tag>all</sieve:tag>" +
                "<sieve:tag>is</sieve:tag>" +
                "<sieve:str>from</sieve:str>" +
                "<sieve:str>user@domain</sieve:str>" +
                "</sieve:test>" +
                "<sieve:control sieve:name='stop'/>" +
                "</sieve:control>", monitor.toString());
    }
}
