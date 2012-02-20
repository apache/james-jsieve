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

package org.apache.jsieve.util.check;

import java.util.List;

import junit.framework.TestCase;

import org.apache.jsieve.javaxmail.MockMimeMessage;

public class ScriptCheckMailAdapterHeadersTest extends TestCase {
    private static final String BCC = "Bcc";

    private static final String TO = "To";

    private static final String FROM = "From";

    private static final int MESSAGE_SIZE = 100;

    private static final String BCC_ADDRESS_ONE = "bugs@toons.example.org";

    private static final String BCC_ADDRESS_TWO = "daffy@toons.example.org";

    private static final String TO_ADDRESS = "roadrunner@acme.example.com";

    private static final String X_HEADER_NAME = "X-Toon";

    private static final String X_HEADER_WITH_WS = "   "
            + X_HEADER_NAME.toLowerCase();

    private static final String X_HEADER_VALUE = "Road Runner";

    private static final String X_HEADER_VALUE_ALT = "Wile E. Coyote And Road Runner";

    private static final String FROM_ADDRESS = "coyote@desert.example.org";

    ScriptCheckMailAdapter adapter;

    MockMimeMessage message;

    protected void setUp() throws Exception {
        super.setUp();
        adapter = new ScriptCheckMailAdapter();
        message = new MockMimeMessage();
        message.addHeader(FROM, FROM_ADDRESS);
        message.addHeader(TO, TO_ADDRESS);
        message.addHeader(BCC, BCC_ADDRESS_ONE);
        message.addHeader(BCC, BCC_ADDRESS_TWO);
        message.addHeader(X_HEADER_NAME, X_HEADER_VALUE);
        message.addHeader(X_HEADER_NAME.toLowerCase(), X_HEADER_VALUE
                .toLowerCase());
        message.addHeader(X_HEADER_WITH_WS, X_HEADER_VALUE_ALT);
        message.setSize(MESSAGE_SIZE);
        adapter.setMail(message);
    }

    public void testGetHeader() throws Exception {
        List<String> headers = adapter.getHeader(FROM);
        assertNotNull(headers);
        assertEquals("From header", 1, headers.size());
        assertEquals("From header", FROM_ADDRESS, headers.get(0));
        headers = adapter.getHeader(BCC);
        assertEquals("Bcc headers", 2, headers.size());
        assertTrue("Bcc headers", headers.contains(BCC_ADDRESS_ONE));
        assertTrue("Bcc headers", headers.contains(BCC_ADDRESS_TWO));
        headers = adapter.getHeader(X_HEADER_NAME);
        assertEquals("Case and whitespace sensitive", 1, headers.size());
        assertEquals("Case and whitespace sensitive", X_HEADER_VALUE, headers
                .get(0));
        headers = adapter.getHeader(X_HEADER_NAME.toLowerCase());
        assertEquals("Case and whitespace sensitive", 1, headers.size());
        assertEquals("Case and whitespace sensitive", X_HEADER_VALUE
                .toLowerCase(), headers.get(0));
        headers = adapter.getHeader(X_HEADER_WITH_WS);
        assertEquals("Case and whitespace sensitive", 1, headers.size());
        assertEquals("Case and whitespace sensitive", X_HEADER_VALUE_ALT,
                headers.get(0));
    }

    public void testGetHeaderNames() throws Exception {
        List headers = adapter.getHeaderNames();
        assertNotNull(headers);
        assertEquals("All headers set returned", 6, headers.size());
        assertTrue("All headers set returned", headers.contains(BCC));
        assertTrue("All headers set returned", headers.contains(TO));
        assertTrue("All headers set returned", headers.contains(FROM));
        assertTrue("All headers set returned", headers.contains(X_HEADER_NAME));
        assertTrue("All headers set returned", headers.contains(X_HEADER_NAME
                .toLowerCase()));
        assertTrue("All headers set returned", headers
                .contains(X_HEADER_WITH_WS));
    }

    public void testGetMatchingHeader() throws Exception {
        List<String> headers = adapter.getMatchingHeader(FROM);
        assertNotNull(headers);
        assertEquals("From headers set returned", 1, headers.size());
        assertTrue("From headers set returned", headers.contains(FROM_ADDRESS));
        headers = adapter.getMatchingHeader(X_HEADER_NAME);
        assertNotNull(headers);
        assertEquals(
                "Matches ignoring whitespace and capitalisation headers set returned",
                3, headers.size());
        assertTrue(
                "Matches ignoring whitespace and capitalisation headers set returned",
                headers.contains(X_HEADER_VALUE));
        assertTrue(
                "Matches ignoring whitespace and capitalisation headers set returned",
                headers.contains(X_HEADER_VALUE_ALT));
        assertTrue(
                "Matches ignoring whitespace and capitalisation headers set returned",
                headers.contains(X_HEADER_VALUE.toLowerCase()));
    }

    public void testGetSize() throws Exception {
        int size = adapter.getSize();
        assertEquals("Message size set", MESSAGE_SIZE, size);
    }
}
