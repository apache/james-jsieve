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

import org.apache.jsieve.mail.MailAdapter.Address;
import org.apache.jsieve.parser.generated.address.ParseException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SieveAddressBuilderTest {

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

    @Before
    public void setUp() throws Exception {
        builder = new SieveAddressBuilder();
    }

    @Test
    public void testNotAddress() throws Exception {
        try {
            builder
                    .addAddresses("What a load of rubbish - not an address in sight!");
            Assert.fail("Parsing should fail when the input is not an address");
        } catch (ParseException e) {
            // expected
        }
    }

    @Test
    public void testAddAddresses() throws Exception {
        Assert.assertNotNull(builder.getAddresses());
        builder.addAddresses(COYOTE_ADDRESS);
        Address[] addresses = builder.getAddresses();
        Assert.assertNotNull(addresses);
        Assert.assertEquals(1, addresses.length);
        Assert.assertEquals(COYOTE, addresses[0].getLocalPart());
        Assert.assertEquals(DOMAIN, addresses[0].getDomain());
        builder.addAddresses(HEROS);
        addresses = builder.getAddresses();
        Assert.assertNotNull(addresses);
        Assert.assertEquals(3, addresses.length);
        Assert.assertEquals(COYOTE, addresses[0].getLocalPart());
        Assert.assertEquals(DOMAIN, addresses[0].getDomain());
        Assert.assertEquals(ROADRUNNER, addresses[1].getLocalPart());
        Assert.assertEquals(DOMAIN, addresses[1].getDomain());
        Assert.assertEquals(BUGS, addresses[2].getLocalPart());
        Assert.assertEquals(DOMAIN, addresses[2].getDomain());
    }

    @Test
    public void testReset() throws Exception {
        Assert.assertNotNull(builder.getAddresses());
        builder.addAddresses(COYOTE_ADDRESS);
        Address[] addresses = builder.getAddresses();
        Assert.assertNotNull(addresses);
        Assert.assertEquals(1, addresses.length);
        Assert.assertEquals(COYOTE, addresses[0].getLocalPart());
        Assert.assertEquals(DOMAIN, addresses[0].getDomain());
        addresses = builder.getAddresses();
        Assert.assertNotNull(addresses);
        Assert.assertEquals(1, addresses.length);
        Assert.assertEquals(COYOTE, addresses[0].getLocalPart());
        Assert.assertEquals(DOMAIN, addresses[0].getDomain());
        builder.reset();
        addresses = builder.getAddresses();
        Assert.assertNotNull(addresses);
        Assert.assertEquals(0, addresses.length);
        builder.addAddresses(COYOTE_ADDRESS);
        addresses = builder.getAddresses();
        Assert.assertNotNull(addresses);
        Assert.assertEquals(1, addresses.length);
        Assert.assertEquals(COYOTE, addresses[0].getLocalPart());
        Assert.assertEquals(DOMAIN, addresses[0].getDomain());
    }
}
