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

package org.apache.jsieve;

import org.apache.jsieve.parser.generated.ParseException;
import org.apache.jsieve.utils.JUnitUtils;
import org.junit.Test;

/**
 * SieveFactory#parse checks the arguments and blocks of every command and
 * test, also on code that is not reached (RFC 5228 section 2.10.6).
 */
public class ParseValidationTest {

    @Test(expected = ParseException.class)
    public void commandArgumentsShouldBeCheckedAtParse() throws Exception {
        JUnitUtils.parse("if false { keep \"x\"; }");
    }

    @Test(expected = ParseException.class)
    public void commandBlockShouldBeCheckedAtParse() throws Exception {
        JUnitUtils.parse("if false { discard { keep; } }");
    }

    @Test(expected = ParseException.class)
    public void testArgumentsShouldBeCheckedAtParse() throws Exception {
        JUnitUtils.parse("if false { if size 5 { keep; } }");
    }

    @Test(expected = ParseException.class)
    public void headerTagsShouldBeCheckedAtParse() throws Exception {
        JUnitUtils.parse("if false { if header :unknown \"Subject\" \"x\" { keep; } }");
    }

    @Test(expected = ParseException.class)
    public void addressArgumentsShouldBeCheckedAtParse() throws Exception {
        JUnitUtils.parse("if false { if address :all \"From\" { keep; } }");
    }

    @Test(expected = ParseException.class)
    public void notWithTwoTestsShouldBeCheckedAtParse() throws Exception {
        JUnitUtils.parse("if false { if not (true, false) { keep; } }");
    }

    @Test(expected = ParseException.class)
    public void logArgumentsShouldBeCheckedAtParse() throws Exception {
        JUnitUtils.parse("require \"log\"; if false { log; }");
    }

    @Test(expected = ParseException.class)
    public void vacationArgumentsShouldBeCheckedAtParse() throws Exception {
        JUnitUtils.parse("require \"vacation\"; if false { vacation :days \"x\" \"reason\"; }");
    }

    @Test(expected = ParseException.class)
    public void elseAfterAnotherCommandShouldBeRejectedAtParse() throws Exception {
        JUnitUtils.parse("if false { if true { keep; } stop; else { keep; } }");
    }

    @Test(expected = ParseException.class)
    public void elsifFirstInABlockShouldBeRejectedAtParse() throws Exception {
        JUnitUtils.parse("if false { elsif true { keep; } }");
    }

    @Test(expected = ParseException.class)
    public void elseAfterAnElseShouldBeRejectedAtParse() throws Exception {
        JUnitUtils.parse("if true { keep; } else { stop; } else { stop; }");
    }

    @Test
    public void validScriptShouldParse() throws Exception {
        JUnitUtils.parse("require [\"fileinto\", \"vacation\", \"envelope\"];"
            + " if anyof (header :contains \"Subject\" \"x\", address :domain :is \"From\" \"example.com\","
            + " envelope :localpart :matches \"to\" \"a*\", not exists \"X-A\", size :over 1K) {"
            + " fileinto \"INBOX.x\"; } elsif true { vacation :days 3 \"away\"; } else { keep; }");
    }

}
