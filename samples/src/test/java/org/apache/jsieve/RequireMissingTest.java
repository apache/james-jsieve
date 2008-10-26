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

import junit.framework.TestCase;

import org.apache.jsieve.parser.generated.ParseException;
import org.apache.jsieve.utils.JUnitUtils;

/**
 * Class AddressTest
 */
public class RequireMissingTest extends TestCase {

    /**
     * Tests that unsupported requires are caught before script execution.
     */
    public void testUnsupportedRequireNoBrackets() throws Exception {
        String script = "require \"whatever\"; if address :contains [\"To\", \"From\"] \"Fish!\"{ fileinto \"aFolder\"; }";
        try {
            JUnitUtils.parse(script);
            fail("Expect exception to be throw during parse since command is unsupported");
        } catch (ParseException e) {
            // expected
        }
    }

    /**
     * Tests that unsupported requires are caught before script execution.
     */
    public void testUnsupportedRequireMultiple() throws Exception {
        String script = "require [\"fileinto\",\"whatever\"]; if address :contains [\"To\", \"From\"] \"Fish!\"{ fileinto \"aFolder\"; }";
        try {
            JUnitUtils.parse(script);
            fail("Expect exception to be throw during parse since command is unsupported");
        } catch (ParseException e) {
            // expected
        }
    }

    /**
     * Tests that unsupported requires are caught before script execution.
     */
    public void testUnsupportedRequire() throws Exception {
        String script = "require [\"whatever\"]; if address :contains [\"To\", \"From\"] \"Fish!\"{ fileinto \"aFolder\"; }";
        try {
            JUnitUtils.parse(script);
            fail("Expect exception to be throw during parse since command is unsupported");
        } catch (ParseException e) {
            // expected
        }
    }

    /**
     * Tests 2.10.5 Extensions and Optional Features: If an extension is not
     * enabled with "required" they must treat it as if they do not support it
     * at all.
     */
    public void testMissingRequire() throws Exception {
        String script = "if address :contains [\"To\", \"From\"] \"Fish!\"{ bogus \"aFolder\"; }";
        try {
            JUnitUtils.parse(script);
            fail("Expect exception to be throw during parse since command is missing");
        } catch (ParseException e) {
            // expected
        }
    }

    /**
     * Tests 3.2 Control Structure Require: Require MUST NOT be used after any
     * other command.
     */
    public void testRequireAfterOtherCommand() throws Exception {
        String script = "if address :contains [\"To\", \"From\"] \"Fish!\"{ fileinto \"aFolder\"; } require [\"whatever\"]; ";
        try {
            JUnitUtils.parse(script);
            fail("Expect exception to be throw during parse");
        } catch (ParseException e) {
            // expected
        }
    }
}
