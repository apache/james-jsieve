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

package org.apache.jsieve.utils;

import java.io.ByteArrayInputStream;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.apache.jsieve.ConfigurationManager;
import org.apache.jsieve.exception.SieveException;
import org.apache.jsieve.mail.MailAdapter;
import org.apache.jsieve.parser.generated.Node;
import org.apache.jsieve.parser.generated.ParseException;

/**
 * Class JUnitUtils implements utility methods used during unit testing.
 */
public class JUnitUtils {
    /**
     * Method interpret parses a script and evaluates it against a MailAdapter.
     * 
     * @param mail
     * @param script
     * @throws SieveException
     * @throws ParseException
     */
    static public void interpret(MailAdapter mail, String script)
            throws SieveException, ParseException {
        new ConfigurationManager().build().interpret(mail,
                new ByteArrayInputStream(script.getBytes()));
    }

    /**
     * Method interpret parses a script and evaluates it against a MailAdapter.
     * 
     * @param mail
     * @param script
     * @throws SieveException
     * @throws ParseException
     */
    static public Node parse(String script) throws SieveException,
            ParseException {
        return new ConfigurationManager().build().parse(
                new ByteArrayInputStream(script.getBytes()));
    }

    /**
     * Method createMimeMessage answers an empty MimeMessage.
     * 
     * @return MimeMessage
     */
    static public MimeMessage createMimeMessage() {
        return new MimeMessage(Session.getDefaultInstance(System
                .getProperties()));
    }

    /**
     * Method createMail answers a SieveMailAdapter wrapping an empty
     * MimeMessage.
     * 
     * @return SieveEnvelopeMailAdapter
     */
    static public MailAdapter createMail() {
        return new SieveMailAdapter(createMimeMessage());
    }

    /**
     * Method createEnvelopeMail answers a SieveEnvelopeMailAdapter wrapping an
     * empty MimeMessage.
     * 
     * @return SieveEnvelopeMailAdapter
     */
    static public SieveEnvelopeMailAdapter createEnvelopeMail() {
        return new SieveEnvelopeMailAdapter(createMimeMessage());
    }

    /**
     * Method copyMail answers a copy of our mock MailAdapter.
     * 
     * @param mail
     * @return MailAdapter
     * @throws MessagingException
     */
    static public MailAdapter copyMail(SieveMailAdapter mail)
            throws MessagingException {
        MimeMessage message = new MimeMessage(mail.getMessage());
        return new SieveMailAdapter(message);
    }

    /**
     * Constructor for JUnitUtils.
     */
    private JUnitUtils() {
        super();
    }

}
