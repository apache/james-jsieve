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

import junit.framework.TestCase;

import org.apache.jsieve.commands.ThrowTestException;
import org.apache.jsieve.utils.JUnitUtils;
import org.apache.jsieve.utils.SieveMailAdapter;

public class QuotingTest extends TestCase {

    public void testQuoteInQuotedString() throws Exception {
        String script = "if header :is \"X-Test\" \"Before\\\"After\" {throwTestException;}";

        final SieveMailAdapter mail = (SieveMailAdapter) JUnitUtils
                .createMail();
        mail.getMessage().addHeader("X-Test", "Before\"After");
        try {
            JUnitUtils.interpret(mail, script);
            fail("Expected header to be matched");
        } catch (ThrowTestException.TestException e) {
            // expected
        }
    }
}
