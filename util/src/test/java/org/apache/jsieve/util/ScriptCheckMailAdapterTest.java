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

import java.io.ByteArrayInputStream;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.apache.jsieve.ConfigurationManager;
import org.apache.jsieve.SieveFactory;
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
}
