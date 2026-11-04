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

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.jsieve.commands.ThrowTestException;
import org.apache.jsieve.mail.MailAdapter;
import org.apache.jsieve.mail.optional.ActionVacation;
import org.apache.jsieve.utils.JUnitUtils;
import org.apache.jsieve.utils.SieveMailAdapter;
import org.junit.Test;

/**
 * Tags are case-insensitive (RFC 5228 section 8.1).
 */
public class TagCaseTest {

    @Test(expected = ThrowTestException.TestException.class)
    public void matchTypeShouldIgnoreCase() throws Exception {
        SieveMailAdapter mail = (SieveMailAdapter) JUnitUtils.createMail();
        mail.getMessage().addHeader("Subject", "x");

        JUnitUtils.interpret(mail, "if header :IS \"Subject\" \"x\" {throwTestException;}");
    }

    @Test(expected = ThrowTestException.TestException.class)
    public void sizeTagShouldIgnoreCase() throws Exception {
        JUnitUtils.interpret(JUnitUtils.createMail(), "if size :UNDER 1M {throwTestException;}");
    }

    @Test(expected = ThrowTestException.TestException.class)
    public void addressPartShouldIgnoreCase() throws Exception {
        SieveMailAdapter mail = (SieveMailAdapter) JUnitUtils.createMail();
        mail.getMessage().addHeader("From", "user@example.com");

        JUnitUtils.interpret(mail, "if address :Domain :Is \"From\" \"example.com\" {throwTestException;}");
    }

    @Test
    public void vacationTagShouldIgnoreCase() throws Exception {
        MailAdapter mail = JUnitUtils.createMail();

        JUnitUtils.interpret(mail, "require \"vacation\"; vacation :DAYS 3 \"away\";");

        assertThat(((ActionVacation) mail.getActions().get(0)).getDuration()).isEqualTo(3);
    }

}
