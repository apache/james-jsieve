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

package org.apache.jsieve.junit;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import junit.framework.TestCase;

import org.apache.jsieve.util.check.ScriptChecker;

public class MultipleToTest extends TestCase {

    private static final String SOLO_TO_EMAIL = "Date: Sun, 1 Apr 2007 1100:00:00 +0100 (BST)\r\f"
            + "From: roadrunner@acme.example.com\r\f"
            + "To: coyote@desert.example.org\r\f"
            + "Subject: Who's The Fool?\r\f"
            + "\r\f"
            + "Beep-Beep\r\f";

    private static final String MULTIPLE_TO_EMAIL = "Date: Sun, 1 Apr 2007 1100:00:00 +0100 (BST)\r\f"
            + "From: roadrunner@acme.example.com\r\f"
            + "To: coyote@desert.example.org, bugs@example.org, "
            + "    elmer@hunters.example.org,\r\f"
            + "Subject: Who's The Fool?\r\f"
            + "\r\f"
            + "Beep-Beep\r\f";

    private static final String FILTER_SCRIPT = "require \"fileinto\";\r\f"
            + "if address :is :all \"to\" \"coyote@desert.example.org\" {\r\f"
            + "  fileinto \"coyote\";\r\f}\r\f"
            + "if address :is :all \"to\" \"bugs@example.org\" {\r\f"
            + "  fileinto \"bugs\";\r\f}\r\f"
            + "if address :is :all \"to\" \"roadrunneracme.@example.org\" {\r\f"
            + "  fileinto \"rr\";\r\f}\r\f"
            + "if address :is :all \"to\" \"elmer@hunters.example.org\" {\r\f"
            + "  fileinto \"elmer\";\r\f}\r\f";

    public void testSingleTo() throws Exception {
        ScriptChecker checker = new ScriptChecker();
        ScriptChecker.Results results = checker.check(toStream(SOLO_TO_EMAIL),
                toStream(FILTER_SCRIPT));
        if (results.getException() != null) {
            fail(results.getException().toString());
        }
        final List actionsExecuted = results.getActionsExecuted();
        assertEquals(1, actionsExecuted.size());
        assertTrue(results.isActionFileInto("coyote", 0));
    }

    public void testMultipleTo() throws Exception {
        ScriptChecker checker = new ScriptChecker();
        ScriptChecker.Results results = checker.check(
                toStream(MULTIPLE_TO_EMAIL), toStream(FILTER_SCRIPT));
        if (results.getException() != null) {
            fail(results.getException().toString());
        }
        final List actionsExecuted = results.getActionsExecuted();
        assertEquals(3, actionsExecuted.size());
        assertTrue(results.isActionFileInto("coyote", 0));
        assertTrue(results.isActionFileInto("bugs", 1));
        assertTrue(results.isActionFileInto("elmer", 2));
    }

    private InputStream toStream(String in) throws Exception {
        byte[] bytes = in.getBytes("US-ASCII");
        ByteArrayInputStream result = new ByteArrayInputStream(bytes);
        return result;
    }
}
