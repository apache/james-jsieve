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

import org.apache.jsieve.exception.FeatureException;
import org.apache.jsieve.parser.generated.ParseException;
import org.apache.jsieve.utils.JUnitUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AsciiNumericTest {

    AsciiNumeric subject;

    @Before
    public void setUp() throws Exception {
        subject = new AsciiNumeric();
    }

    @Test
    public void testVerificationFailsWhenAsciiNumericIsNotRequired() throws Exception {
        String script = "if header :contains :comparator \"i;ascii-numeric\" \"Subject\" \"69\" {stop;}";
        try {
            JUnitUtils.interpret(JUnitUtils.createMail(), script);
            Assert.fail("Comparator must be declared in require statement");
        } catch (ParseException e) {
            // Expected
        }
    }

    @Test
    public void testVerificationPassesWhenAsciiNumericIsRequired() throws Exception {
        String script = "require [\"comparator-i;ascii-numeric\"]; if header :is :comparator \"i;ascii-numeric\" \"Subject\" \"69\" {stop;}";
        JUnitUtils.interpret(JUnitUtils.createMail(), script);
    }

    @Test
    public void testBasicNumbericEquality() throws Exception {
        Assert.assertFalse(subject.equals("1", "2"));
        Assert.assertTrue(subject.equals("1", "1"));
    }

    @Test
    public void testEqualityShouldIgnoreTrailingCharacters() throws Exception {
        Assert.assertTrue(subject.equals("01", "1A"));
        Assert.assertTrue(subject.equals("1", "00000000000000001A"));
        Assert.assertTrue(subject.equals("234S", "234YTGSDBBSD"));
    }

    @Test
    public void testEqualityShouldIgnoreLeadingZeros() throws Exception {
        Assert.assertTrue(subject.equals("01", "1"));
        Assert.assertTrue(subject.equals("000001", "1"));
        Assert.assertFalse(subject.equals("000001", "10"));
    }

    @Test
    public void testStingsThatDoNotStartWithADigitRepresentPositiveInfinityWhenUsedInEquality() throws Exception {
        Assert.assertFalse(subject.equals("1", "A4"));
        Assert.assertFalse(subject.equals("x", "4"));
        Assert.assertTrue(subject.equals("GT", "A4"));
    }

    @Test
    public void testSubstringIsNotSupported() throws Exception {
        try {
            subject.contains("234234", "34");
            Assert.fail("Substring is unsupported");
        } catch (FeatureException e) {
            // Expected
        }
    }

    @Test
    public void testMatchNotSupported() throws Exception {
        try {
            subject.matches("234234", "34");
            Assert.fail("Substring is unsupported");
        } catch (FeatureException e) {
            // Expected
        }
    }
}
