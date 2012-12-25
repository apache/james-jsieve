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

import org.apache.jsieve.javaxmail.MockMimeMessage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class ScriptCheckMailAdapterHeadersTest {
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

    @Before
    public void setUp() throws Exception {
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

    @Test
    public void testGetHeader() throws Exception {
        List<String> headers = adapter.getHeader(FROM);
        Assert.assertNotNull(headers);
        Assert.assertEquals("From header", 1, headers.size());
        Assert.assertEquals("From header", FROM_ADDRESS, headers.get(0));
        headers = adapter.getHeader(BCC);
        Assert.assertEquals("Bcc headers", 2, headers.size());
        Assert.assertTrue("Bcc headers", headers.contains(BCC_ADDRESS_ONE));
        Assert.assertTrue("Bcc headers", headers.contains(BCC_ADDRESS_TWO));
        headers = adapter.getHeader(X_HEADER_NAME);
        Assert.assertEquals("Case and whitespace sensitive", 1, headers.size());
        Assert.assertEquals("Case and whitespace sensitive", X_HEADER_VALUE, headers
                .get(0));
        headers = adapter.getHeader(X_HEADER_NAME.toLowerCase());
        Assert.assertEquals("Case and whitespace sensitive", 1, headers.size());
        Assert.assertEquals("Case and whitespace sensitive", X_HEADER_VALUE
                .toLowerCase(), headers.get(0));
        headers = adapter.getHeader(X_HEADER_WITH_WS);
        Assert.assertEquals("Case and whitespace sensitive", 1, headers.size());
        Assert.assertEquals("Case and whitespace sensitive", X_HEADER_VALUE_ALT,
                headers.get(0));
    }

    @Test
    public void testGetHeaderNames() throws Exception {
        List headers = adapter.getHeaderNames();
        Assert.assertNotNull(headers);
        Assert.assertEquals("All headers set returned", 6, headers.size());
        Assert.assertTrue("All headers set returned", headers.contains(BCC));
        Assert.assertTrue("All headers set returned", headers.contains(TO));
        Assert.assertTrue("All headers set returned", headers.contains(FROM));
        Assert.assertTrue("All headers set returned", headers.contains(X_HEADER_NAME));
        Assert.assertTrue("All headers set returned", headers.contains(X_HEADER_NAME
                .toLowerCase()));
        Assert.assertTrue("All headers set returned", headers
                .contains(X_HEADER_WITH_WS));
    }

    @Test
    public void testGetMatchingHeader() throws Exception {
        List<String> headers = adapter.getMatchingHeader(FROM);
        Assert.assertNotNull(headers);
        Assert.assertEquals("From headers set returned", 1, headers.size());
        Assert.assertTrue("From headers set returned", headers.contains(FROM_ADDRESS));
        headers = adapter.getMatchingHeader(X_HEADER_NAME);
        Assert.assertNotNull(headers);
        Assert.assertEquals(
                "Matches ignoring whitespace and capitalisation headers set returned",
                3, headers.size());
        Assert.assertTrue(
                "Matches ignoring whitespace and capitalisation headers set returned",
                headers.contains(X_HEADER_VALUE));
        Assert.assertTrue(
                "Matches ignoring whitespace and capitalisation headers set returned",
                headers.contains(X_HEADER_VALUE_ALT));
        Assert.assertTrue(
                "Matches ignoring whitespace and capitalisation headers set returned",
                headers.contains(X_HEADER_VALUE.toLowerCase()));
    }

    @Test
    public void testGetSize() throws Exception {
        int size = adapter.getSize();
        Assert.assertEquals("Message size set", MESSAGE_SIZE, size);
    }
}
