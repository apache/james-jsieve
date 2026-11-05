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
package org.apache.jsieve.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.apache.jsieve.ConfigurationManager;
import org.apache.jsieve.SieveFactory;
import org.apache.jsieve.exception.InternetAddressException;
import org.apache.jsieve.mail.ActionRedirect;
import org.apache.jsieve.parser.generated.Node;
import org.apache.jsieve.util.check.ScriptCheckMailAdapter;
import org.junit.Before;
import org.junit.Test;

public class ScriptCheckMailAdapterTest {

    private static final String FOLDED_SUBJECT_MATCH = "if header :is \"subject\" \"Test with long long long long long long long long long "
            + "long long long long long long long long long long long very very long folded subject email\" { "
            + "redirect \"test@test.test\";" + "}";
    
    private static final String ENCODED_SUBJECT_MATCH = "if header :is \"subject\" \"Test però £ ù €\" { "
            + "redirect \"test@test.test\";" + "}";

    private static ScriptCheckMailAdapter adapterFor(String message) throws Exception {
        ScriptCheckMailAdapter adapter = new ScriptCheckMailAdapter();
        adapter.setMail(new MimeMessage(Session.getDefaultInstance(new Properties()),
                new ByteArrayInputStream(message.getBytes(StandardCharsets.US_ASCII))));
        return adapter;
    }

    private SieveFactory sieveFactory;


    @Before
    public void setUp() throws Exception {
        this.sieveFactory = new ConfigurationManager().build();
    }

    @Test
    public void askedActionMustBeExecutedWhenFoldedHeaderMatchs() throws Exception {

        //Given
        // Load test message file
        MimeMessage message = new MimeMessage(Session.getDefaultInstance(System.getProperties()),
                ClassLoader.getSystemResourceAsStream("org/apache/jsieve/util/FoldedSubjectEmail.eml"));

        // Associate it to the adapter
        ScriptCheckMailAdapter testee = new ScriptCheckMailAdapter();
        testee.setMail(message);

        //When
        // Parse sieve script
        Node parsedSieve = sieveFactory.parse(new ByteArrayInputStream(FOLDED_SUBJECT_MATCH.getBytes("UTF-8")));

        // Evaluate the script against the message
        sieveFactory.evaluate(testee, parsedSieve);

        //Then
        // Test is OK if we have the "redirect" action
        assertThat(testee.getActions()).hasSize(1);
        assertThat(testee.getActions().get(0)).isInstanceOf(ActionRedirect.class);
    }

    @Test
    public void askedActionMustBeExecutedWhenEncodedHeaderMatchs() throws Exception {

        //Given
        // Load test message file
        MimeMessage message = new MimeMessage(Session.getDefaultInstance(System.getProperties()),
                ClassLoader.getSystemResourceAsStream("org/apache/jsieve/util/EncodedSubjectEmail.eml"));

        // Associate it to the adapter
        ScriptCheckMailAdapter testee = new ScriptCheckMailAdapter();
        testee.setMail(message);

        //When
        // Parse sieve script
        Node parsedSieve = sieveFactory.parse(new ByteArrayInputStream(ENCODED_SUBJECT_MATCH.getBytes("UTF-8")));

        // Evaluate the script against the message
        sieveFactory.evaluate(testee, parsedSieve);

        //Then
        // Test is OK if we have the "redirect" action
        assertThat(testee.getActions()).hasSize(1);
        assertThat(testee.getActions().get(0)).isInstanceOf(ActionRedirect.class);
    }

    @Test
    public void getMatchingHeaderShouldReturnEachFieldOnceWhenNamesDifferByCase() throws Exception {
        ScriptCheckMailAdapter testee = adapterFor("From: a@example.com\r\nFROM: b@example.com\r\n\r\nbody\r\n");

        assertThat(testee.getMatchingHeader("from")).containsExactly("a@example.com", "b@example.com");
    }

    @Test
    public void getHeaderShouldReturnOnlyTheFieldsWithTheExactName() throws Exception {
        ScriptCheckMailAdapter testee = adapterFor("From: a@example.com\r\nFROM: b@example.com\r\n\r\nbody\r\n");

        assertThat(testee.getHeader("FROM")).containsExactly("b@example.com");
    }

    @Test
    public void getHeaderShouldUnfoldALineBreakFollowingABackslash() throws Exception {
        ScriptCheckMailAdapter testee = adapterFor("X-Path: C:\\\r\n dir\r\n\r\nbody\r\n");

        assertThat(testee.getMatchingHeader("X-Path")).containsExactly("C:\\ dir");
    }

    @Test
    public void getHeaderShouldKeepAnEncodedWordOfAnUnknownCharsetAsWritten() throws Exception {
        ScriptCheckMailAdapter testee = adapterFor("Subject: =?x-unknown?q?a?= b\r\n\r\nbody\r\n");

        assertThat(testee.getMatchingHeader("Subject")).containsExactly("=?x-unknown?q?a?= b");
    }

    @Test
    public void parseAddressesShouldNotReadAnAddressOutOfAnEncodedWord() throws Exception {
        ScriptCheckMailAdapter testee = adapterFor("To: =?utf-8?q?evil=40attacker=2Ecom=2C?= <user@example.com>\r\n\r\nbody\r\n");

        assertThat(testee.parseAddresses("To")).extracting(address -> address.getLocalPart()).containsExactly("user");
    }

    @Test
    public void parseAddressesShouldReadAnAddressAfterAnEncodedSpecial() throws Exception {
        ScriptCheckMailAdapter testee = adapterFor("To: =?utf-8?q?a=3Cb?= <user@example.com>\r\n\r\nbody\r\n");

        assertThat(testee.parseAddresses("To")).extracting(address -> address.getLocalPart()).containsExactly("user");
    }

    @Test
    public void parseAddressesShouldReadAnAddressBesideAnEncodedWordOfAnUnknownCharset() throws Exception {
        ScriptCheckMailAdapter testee = adapterFor("To: =?x-unknown?q?a?= <a@example.com>\r\n\r\nbody\r\n");

        assertThat(testee.parseAddresses("To")).extracting(address -> address.getLocalPart()).containsExactly("a");
    }

    @Test
    public void parseAddressesShouldUnfoldALineBreakFollowingABackslash() throws Exception {
        ScriptCheckMailAdapter testee = adapterFor("To: \"a\\\r\n b\"@example.com\r\n\r\nbody\r\n");

        assertThat(testee.parseAddresses("To")).extracting(address -> address.getLocalPart()).containsExactly("a b");
    }

    @Test
    public void parseAddressesShouldIgnoreOnlyTheAsciiCaseOfTheName() throws Exception {
        ScriptCheckMailAdapter testee = adapterFor("Subject: test\r\n\r\nbody\r\n");
        ((MimeMessage) testee.getMail()).addHeader("T\u00f6", "a@example.com");
        ((MimeMessage) testee.getMail()).addHeader("T\u00d6", "b@example.com");

        assertThat(testee.parseAddresses("T\u00f6")).extracting(address -> address.getLocalPart()).containsExactly("a");
    }

    @Test
    public void parseAddressesShouldThrowAnInternetAddressExceptionOnAMalformedValue() throws Exception {
        ScriptCheckMailAdapter testee = adapterFor("To: [x] <a@example.com>\r\n\r\nbody\r\n");

        assertThatThrownBy(() -> testee.parseAddresses("To")).isInstanceOf(InternetAddressException.class);
    }

    @Test
    public void parseAddressesShouldKeepTheValidFieldsBesideAMalformedOne() throws Exception {
        ScriptCheckMailAdapter testee = adapterFor("To: [x] <a@example.com>\r\nTo: b@example.com\r\n\r\nbody\r\n");

        assertThat(testee.parseAddresses("To")).extracting(address -> address.getLocalPart()).containsExactly("b");
    }

    @Test
    public void addressTestShouldNotMatchAMalformedValue() throws Exception {
        ScriptCheckMailAdapter testee = adapterFor("To: [x] <a@example.com>\r\n\r\nbody\r\n");
        Node script = sieveFactory.parse(new ByteArrayInputStream(
            "if not address :localpart :is \"to\" \"a\" { redirect \"test@test.test\"; }".getBytes(StandardCharsets.UTF_8)));

        sieveFactory.evaluate(testee, script);

        assertThat(testee.getActions()).hasSize(1).first().isInstanceOf(ActionRedirect.class);
    }
}
