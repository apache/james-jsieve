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

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.jsieve.ConfigurationManager;
import org.apache.jsieve.exception.LookupException;
import org.apache.jsieve.parser.generated.ParseException;
import org.apache.jsieve.utils.JUnitUtils;
import org.junit.Assert;
import org.junit.Test;

public class RequireComparatorTest {

    @Test
    public void testAsciiCasemapShouldBeImplicitlyDeclared() throws Exception {
        String script = "if header :contains :comparator \"i;ascii-casemap\" \"Subject\" \"69\" {stop;}";
        JUnitUtils.interpret(JUnitUtils.createMail(), script);
    }

    @Test
    public void testOctetShouldBeImplicitlyDeclared() throws Exception {
        String script = "if header :contains :comparator \"i;octet\" \"Subject\" \"69\" {stop;}";
        JUnitUtils.interpret(JUnitUtils.createMail(), script);
    }

    @Test
    public void testBogusComparatorShouldFailAtParseTime() throws Exception {
        String script = "if header :contains :comparator \"i;bogus\" \"Subject\" \"69\" {stop;}";
        try {
            JUnitUtils.interpret(JUnitUtils.createMail(), script);
            Assert.fail("Bogus comparator should fail");
        } catch (ParseException e) {
            // TODO: catch more finely grained exception
            // Expected
        }
    }

    @Test
    public void requiredComparatorShouldBeDeclaredInsideABlock() throws Exception {
        String script = "require \"comparator-i;ascii-numeric\";\r\n"
                + "if true { if header :comparator \"i;ascii-numeric\" :is \"X-N\" \"5\" { keep; } }";

        JUnitUtils.interpret(JUnitUtils.createMail(), script);
    }

    @Test(expected = ParseException.class)
    public void undeclaredComparatorShouldBeRejectedInsideABlock() throws Exception {
        String script = "if true { if header :comparator \"i;ascii-numeric\" :is \"X-N\" \"5\" { keep; } }";

        JUnitUtils.interpret(JUnitUtils.createMail(), script);
    }

    @Test
    public void requiredComparatorShouldBeDeclaredAfterABlock() throws Exception {
        String script = "require \"comparator-i;ascii-numeric\";\r\n"
                + "if true { }\r\n"
                + "if header :comparator \"i;ascii-numeric\" :is \"X-N\" \"5\" { keep; }";

        JUnitUtils.interpret(JUnitUtils.createMail(), script);
    }

    @Test(expected = ParseException.class)
    public void comparatorCapabilityInAnotherCaseShouldThrow() throws Exception {
        String script = "require \"comparator-I;ASCII-NUMERIC\";";

        JUnitUtils.parse(script);
    }

    @Test(expected = LookupException.class)
    public void comparatorNameInAnotherCaseShouldNotBeFound() throws Exception {
        new ConfigurationManager().getComparatorManager().getComparator("I;OCTET");
    }

    @Test
    public void unknownComparatorShouldBeReportedAsNotSupported() {
        String script = "if header :comparator \"I;OCTET\" :is \"Subject\" \"hello\" { }";

        assertThatThrownBy(() -> JUnitUtils.parse(script))
            .isInstanceOf(ParseException.class)
            .hasMessageContaining("Comparator \"I;OCTET\" is not supported.");
    }

    @Test
    public void undeclaredComparatorShouldBeNamedInTheError() {
        String script = "if header :comparator \"i;ascii-numeric\" :is \"Subject\" \"5\" { }";

        assertThatThrownBy(() -> JUnitUtils.parse(script))
            .isInstanceOf(ParseException.class)
            .hasMessageContaining("Comparator \"i;ascii-numeric\" must be explicitly declared in a require statement.");
    }

    @Test
    public void requiredUnknownComparatorShouldBeNamedInTheError() {
        String script = "require \"comparator-I;ASCII-NUMERIC\";";

        assertThatThrownBy(() -> JUnitUtils.parse(script))
            .isInstanceOf(ParseException.class)
            .hasMessageContaining("Comparator \"I;ASCII-NUMERIC\" is not supported.");
    }
}
