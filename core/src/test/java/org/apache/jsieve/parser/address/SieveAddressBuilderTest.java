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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

    private static int failedParses(String addressList, int times) {
        int failures = 0;
        for (int i = 0; i < times; i++) {
            try {
                SieveAddressBuilder builder = new SieveAddressBuilder();
                builder.addAddresses(addressList);
                if (builder.getAddresses().length != 1) {
                    failures++;
                }
            } catch (ParseException e) {
                failures++;
            }
        }
        return failures;
    }

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

    @Test
    public void displayNameShouldAcceptASeparateDot() throws Exception {
        builder.addAddresses("Wile . E . Coyote <" + COYOTE_ADDRESS + ">");

        Address[] addresses = builder.getAddresses();
        Assert.assertEquals(1, addresses.length);
        Assert.assertEquals(COYOTE, addresses[0].getLocalPart());
        Assert.assertEquals(DOMAIN, addresses[0].getDomain());
    }

    @Test
    public void displayNameShouldAcceptADotAfterAQuotedWord() throws Exception {
        builder.addAddresses("\"Wile\".E <" + COYOTE_ADDRESS + ">");

        Address[] addresses = builder.getAddresses();
        Assert.assertEquals(1, addresses.length);
        Assert.assertEquals(COYOTE, addresses[0].getLocalPart());
        Assert.assertEquals(DOMAIN, addresses[0].getDomain());
    }

    @Test
    public void groupDisplayNameShouldAcceptASeparateDot() throws Exception {
        builder.addAddresses("Acme .Inc: " + COYOTE_ADDRESS + ";, oj .a:;");

        Address[] addresses = builder.getAddresses();
        Assert.assertEquals(1, addresses.length);
        Assert.assertEquals(COYOTE, addresses[0].getLocalPart());
        Assert.assertEquals(DOMAIN, addresses[0].getDomain());
    }

    @Test(expected = ParseException.class)
    public void displayNameShouldNotStartWithADot() throws Exception {
        builder.addAddresses(". Wile <" + COYOTE_ADDRESS + ">");
    }

    @Test
    public void quotedDisplayNameShouldAcceptAnyNonAsciiCharacter() throws Exception {
        builder.addAddresses("\"Иван\" <" + COYOTE_ADDRESS + ">");

        Address[] addresses = builder.getAddresses();
        Assert.assertEquals(1, addresses.length);
        Assert.assertEquals(COYOTE, addresses[0].getLocalPart());
        Assert.assertEquals(DOMAIN, addresses[0].getDomain());
    }

    @Test
    public void commentShouldAcceptAnyNonAsciiCharacter() throws Exception {
        builder.addAddresses("(Ĉ) " + COYOTE_ADDRESS);

        Address[] addresses = builder.getAddresses();
        Assert.assertEquals(1, addresses.length);
        Assert.assertEquals(COYOTE, addresses[0].getLocalPart());
        Assert.assertEquals(DOMAIN, addresses[0].getDomain());
    }

    @Test
    public void domainLiteralShouldAcceptAnyNonAsciiCharacter() throws Exception {
        builder.addAddresses(COYOTE + "@[1Ȍ]");

        Address[] addresses = builder.getAddresses();
        Assert.assertEquals(1, addresses.length);
        Assert.assertEquals(COYOTE, addresses[0].getLocalPart());
        Assert.assertEquals("[1Ȍ]", addresses[0].getDomain());
    }

    @Test(timeout = 120000)
    public void concurrentParsesShouldEachCountTheirOwnNestedComments() throws Exception {
        int threads = 8;
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        try {
            List<Future<Integer>> failures = new ArrayList<>();
            for (int i = 0; i < threads; i++) {
                failures.add(executor.submit(() -> {
                    start.await();
                    return failedParses(COYOTE_ADDRESS + " (a (b (c (d))))", 5000);
                }));
            }

            start.countDown();
            int failed = 0;
            for (Future<Integer> future : failures) {
                failed += future.get();
            }

            Assert.assertEquals(0, failed);
        } finally {
            executor.shutdownNow();
        }
    }
}
