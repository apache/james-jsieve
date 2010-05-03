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
package org.apache.jsieve.comparators;

import junit.framework.TestCase;

import org.apache.jsieve.parser.generated.ParseException;
import org.apache.jsieve.utils.JUnitUtils;

public class RequireComparatorTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void testAsciiCasemapShouldBeImplicitlyDeclared() throws Exception {
        String script = "if header :contains :comparator \"i;ascii-casemap\" \"Subject\" \"69\" {stop;}";
        JUnitUtils.interpret(JUnitUtils.createMail(), script);
    }
    
    public void testOctetShouldBeImplicitlyDeclared() throws Exception {
        String script = "if header :contains :comparator \"i;octet\" \"Subject\" \"69\" {stop;}";
        JUnitUtils.interpret(JUnitUtils.createMail(), script);
    }
    
    public void testBogusComparatorShouldFailAtParseTime() throws Exception {
        String script = "if header :contains :comparator \"i;bogus\" \"Subject\" \"69\" {stop;}";
        try {
            JUnitUtils.interpret(JUnitUtils.createMail(), script);
            fail("Bogus comparator should fail");
        } catch (ParseException e) {
            // TODO: catch more finely grained exception
            // Expected
        }
    }
}
