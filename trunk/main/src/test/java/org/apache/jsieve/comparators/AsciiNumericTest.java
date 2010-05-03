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

import org.apache.jsieve.exception.FeatureException;
import org.apache.jsieve.parser.generated.ParseException;
import org.apache.jsieve.utils.JUnitUtils;

public class AsciiNumericTest extends TestCase {
    
    AsciiNumeric subject;

    protected void setUp() throws Exception {
        super.setUp();
        subject = new AsciiNumeric();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testVerificationFailsWhenAsciiNumericIsNotRequired() throws Exception {
        String script = "if header :contains :comparator \"i;ascii-numeric\" \"Subject\" \"69\" {stop;}";
        try {
            JUnitUtils.interpret(JUnitUtils.createMail(), script);
            fail("Comparator must be declared in require statement");
        } catch (ParseException e) {
            // Expected
        }
    }
    
    public void testVerificationPassesWhenAsciiNumericIsRequired() throws Exception {
        String script = "require [\"comparator-i;ascii-numeric\"]; if header :is :comparator \"i;ascii-numeric\" \"Subject\" \"69\" {stop;}";
        JUnitUtils.interpret(JUnitUtils.createMail(), script);
    }
    
    public void testBasicNumbericEquality() throws Exception {
        assertFalse(subject.equals("1", "2"));
        assertTrue(subject.equals("1", "1"));
    }
    
    public void testEqualityShouldIgnoreTrailingCharacters() throws Exception {
        assertTrue(subject.equals("01", "1A"));
        assertTrue(subject.equals("1", "00000000000000001A"));
        assertTrue(subject.equals("234S", "234YTGSDBBSD"));
    }
    
    public void testEqualityShouldIgnoreLeadingZeros() throws Exception {
        assertTrue(subject.equals("01", "1"));
        assertTrue(subject.equals("000001", "1"));
        assertFalse(subject.equals("000001", "10"));
    }
    
    public void testStingsThatDoNotStartWithADigitRepresentPositiveInfinityWhenUsedInEquality() throws Exception {
        assertFalse(subject.equals("1", "A4"));
        assertFalse(subject.equals("x", "4"));
        assertTrue(subject.equals("GT", "A4"));
    }
    
    public void testSubstringIsNotSupported() throws Exception {
        try {
            subject.contains("234234", "34");
            fail("Substring is unsupported");
        } catch (FeatureException e) {
            // Expected
        }
    }
    
    public void testMatchNotSupported() throws Exception {
        try {
            subject.matches("234234", "34");
            fail("Substring is unsupported");
        } catch (FeatureException e) {
            // Expected
        }
    }
}
