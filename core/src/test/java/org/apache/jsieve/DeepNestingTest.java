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

import static org.apache.jsieve.parser.generated.SieveParser.MAX_NESTING;

import java.io.StringReader;

import org.apache.jsieve.commands.ThrowTestException;
import org.apache.jsieve.parser.generated.ParseException;
import org.apache.jsieve.parser.generated.SieveParser;
import org.apache.jsieve.utils.JUnitUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * A script nested deeper than the limit is a parse error, not a
 * StackOverflowError.
 */
public class DeepNestingTest {

    @Test(expected = ParseException.class)
    public void deeplyNestedTestsShouldThrowAParseException() throws Exception {
        String script = "if " + "not ".repeat(100000) + "true { discard; }";

        JUnitUtils.parse(script);
    }

    @Test(expected = ParseException.class)
    public void deeplyNestedTestListsShouldThrowAParseException() throws Exception {
        String script = "if " + "anyof(".repeat(100000) + "true" + ")".repeat(100000) + " { discard; }";

        JUnitUtils.parse(script);
    }

    @Test(expected = ParseException.class)
    public void deeplyNestedBlocksShouldThrowAParseException() throws Exception {
        String script = "if true { ".repeat(100000) + "discard;" + " }".repeat(100000);

        JUnitUtils.parse(script);
    }

    @Test(expected = ThrowTestException.TestException.class)
    public void nestingUpToTheLimitShouldRun() throws Exception {
        String script = "if " + "anyof(".repeat(MAX_NESTING - 1) + "true" + ")".repeat(MAX_NESTING - 1)
            + " { throwTestException; }";

        JUnitUtils.interpret(JUnitUtils.createMail(), script);
    }

    @Test(expected = ParseException.class)
    public void nestingAboveTheLimitShouldThrowAParseException() throws Exception {
        String script = "if " + "anyof(".repeat(MAX_NESTING) + "true" + ")".repeat(MAX_NESTING) + " { discard; }";

        JUnitUtils.parse(script);
    }

    @Test
    public void parserReusedAfterATooDeepScriptShouldParseAValidOne() throws Exception {
        SieveParser parser = new SieveParser(new StringReader("if " + "not ".repeat(MAX_NESTING) + "true { keep; }"));
        try {
            parser.start();
            Assert.fail("Expected a ParseException");
        } catch (ParseException e) {
            // nested too deep
        }

        parser.ReInit(new StringReader("if true { keep; }"));
        parser.start();
    }

}
