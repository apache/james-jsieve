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

public class SieveGenerationTest extends TestCase {
    
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testShouldGenerateSoloToEmailScriptFromNode() throws Exception {
        assertShouldRoundtripScript(
                "if address :is :all [\"to\"] [\"coyote@desert.example.org\"] {"
                + "fileinto [\"coyote\"];} "
                + "if address :is :all [\"to\"] [\"bugs@example.org\"] {"
                + "fileinto [\"bugs\"];} "
                + "if address :is :all [\"to\"] [\"roadrunneracme.@example.org\"] {"
                + "fileinto [\"rr\"];} "
                + "if address :is :all [\"to\"] [\"elmer@hunters.example.org\"] {"
                + "fileinto [\"elmer\"];}");
    }
    
    public void testShouldGenerateSimpleScriptFromNode() throws Exception {
        assertShouldRoundtripScript("if address :all :is [\"from\"] [\"user@domain\"] {stop;}");
    }
    
    public void testShouldGenerateFileintoFromNode() throws Exception {
        assertShouldRoundtripScript("fileinto [\"INBOX.test1\"]; fileinto [\"INBOX.test1\"];");
    }
    
    public void testShouldGenerateNumbersAboveIntegerRangeInFull() throws Exception {
        assertShouldRoundtripScript("if size :under 2147483648 {discard;}");
        assertShouldRoundtripScript("if size :over 9223372036854775807 {discard;}");
        assertShouldRoundtripScript("if size :over 1000000000000000000 {discard;}");
        assertShouldRoundtripScript("if size :over 9223372036854775806 {discard;}");

        final Node node = new ConfigurationManager().build().parse(new ByteArrayInputStream("if size :under 2G {discard;}".getBytes()));
        final StringWriter monitor = new StringWriter();
        OutputUtils.toSieve(node, monitor);
        assertEquals("if size :under 2147483648 {discard;}", monitor.toString());
    }
    
    public void testShouldGenerateNestedTestFromNode() throws Exception {
        assertShouldRoundtripScript("if not exists [\"x\"] {stop;}");
    }
    
    public void testShouldGenerateNestedTestsInTestListFromNode() throws Exception {
        assertShouldRoundtripScript("if anyof(not exists [\"a\"], allof(true, false)) {stop;}");
    }
    
    private void assertShouldRoundtripScript(final String script) throws Exception {
        // Set up
        final Node node = new ConfigurationManager().build().parse(new ByteArrayInputStream(script.getBytes()));
        final StringWriter monitor = new StringWriter();
        
        // Exercise
        OutputUtils.toSieve(node, monitor);
        
        // Verify
        assertEquals(script, monitor.toString());
    }

}
