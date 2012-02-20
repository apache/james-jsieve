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

import org.apache.jsieve.mail.MailAdapter;
import org.apache.jsieve.utils.JUnitUtils;

public class MultipleRequireTest extends TestCase {

    private static final String MINIMAL_SIEVE = "require [\"fileinto\", \"reject\"];\n";

    private static final String MULTIPLE_REQUIRED_SIEVE = "# \n"
            + "# Start with some comments\n" + "# Whatever\n" + "#\n" + "\n"
            + MINIMAL_SIEVE + "\n" + "#\n" + "# Lets have some more comments\n"
            + "#\n" + "\n";

    MailAdapter mail;

    protected void setUp() throws Exception {
        super.setUp();
        mail = JUnitUtils.createMail();
    }

    public void testMinimalScriptMultipleRequiredParsing() throws Exception {
        JUnitUtils.interpret(mail, MINIMAL_SIEVE);
    }

    public void testScriptMultipleRequiredParsing() throws Exception {
        JUnitUtils.interpret(mail, MULTIPLE_REQUIRED_SIEVE);
    }
}
