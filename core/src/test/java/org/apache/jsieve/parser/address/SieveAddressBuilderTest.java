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
package org.apache.jsieve.parser.address;

import junit.framework.TestCase;

import org.apache.jsieve.mail.MailAdapter.Address;
import org.apache.jsieve.parser.generated.address.ParseException;

public class SieveAddressBuilderTest extends TestCase {

    public static final String DOMAIN = "example.org";

    public static final String COYOTE = "coyote";

    public static final String COYOTE_ADDRESS = COYOTE + "@" + DOMAIN;

    public static final String ROADRUNNER = "roadrunner";

    public static final String ROADRUNNER_ADDRESS = ROADRUNNER + "@" + DOMAIN;

    public static final String BUGS = "bugs";

    public static final String BUGS_ADDRESS = BUGS + "@" + DOMAIN;

    public static final String HEROS = ROADRUNNER_ADDRESS + " , "
            + BUGS_ADDRESS;

    SieveAddressBuilder builder;

    protected void setUp() throws Exception {
        super.setUp();
        builder = new SieveAddressBuilder();
    }

    public void testNotAddress() throws Exception {
        try {
            builder
                    .addAddresses("What a load of rubbish - not an address in sight!");
            fail("Parsing should fail when the input is not an address");
        } catch (ParseException e) {
            // expected
        }
    }

    public void testAddAddresses() throws Exception {
        assertNotNull(builder.getAddresses());
        builder.addAddresses(COYOTE_ADDRESS);
        Address[] addresses = builder.getAddresses();
        assertNotNull(addresses);
        assertEquals(1, addresses.length);
        assertEquals(COYOTE, addresses[0].getLocalPart());
        assertEquals(DOMAIN, addresses[0].getDomain());
        builder.addAddresses(HEROS);
        addresses = builder.getAddresses();
        assertNotNull(addresses);
        assertEquals(3, addresses.length);
        assertEquals(COYOTE, addresses[0].getLocalPart());
        assertEquals(DOMAIN, addresses[0].getDomain());
        assertEquals(ROADRUNNER, addresses[1].getLocalPart());
        assertEquals(DOMAIN, addresses[1].getDomain());
        assertEquals(BUGS, addresses[2].getLocalPart());
        assertEquals(DOMAIN, addresses[2].getDomain());
    }

    public void testReset() throws Exception {
        assertNotNull(builder.getAddresses());
        builder.addAddresses(COYOTE_ADDRESS);
        Address[] addresses = builder.getAddresses();
        assertNotNull(addresses);
        assertEquals(1, addresses.length);
        assertEquals(COYOTE, addresses[0].getLocalPart());
        assertEquals(DOMAIN, addresses[0].getDomain());
        addresses = builder.getAddresses();
        assertNotNull(addresses);
        assertEquals(1, addresses.length);
        assertEquals(COYOTE, addresses[0].getLocalPart());
        assertEquals(DOMAIN, addresses[0].getDomain());
        builder.reset();
        addresses = builder.getAddresses();
        assertNotNull(addresses);
        assertEquals(0, addresses.length);
        builder.addAddresses(COYOTE_ADDRESS);
        addresses = builder.getAddresses();
        assertNotNull(addresses);
        assertEquals(1, addresses.length);
        assertEquals(COYOTE, addresses[0].getLocalPart());
        assertEquals(DOMAIN, addresses[0].getDomain());
    }
}
