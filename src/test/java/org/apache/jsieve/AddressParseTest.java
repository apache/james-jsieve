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

import org.apache.commons.logging.LogFactory;
import org.apache.jsieve.BaseSieveContext;
import org.apache.jsieve.ConfigurationManager;
import org.apache.jsieve.utils.JUnitUtils;
import org.apache.jsieve.utils.SieveMailAdapter;

public class AddressParseTest extends TestCase {

    private static final String MULTIPLE_ADDRESS_VALUES = "coyote@desert.example.org, bugs@example.org,  elmer@hunters.example.org";

    private static final String SOLO_ADDRESS_VALUES = "coyote@desert.example.org";

    BaseSieveContext context;

    SieveMailAdapter mail;

    OpenedAddress address;

    protected void setUp() throws Exception {
        super.setUp();
        ConfigurationManager configurationManager = new ConfigurationManager();
        context = new BaseSieveContext(
                configurationManager.getCommandManager(), configurationManager
                        .getComparatorManager(), configurationManager
                        .getTestManager(), LogFactory
                        .getLog(AddressParseTest.class));
        mail = (SieveMailAdapter) JUnitUtils.createMail();
        address = new OpenedAddress();
    }

    public void testSingleAddress() throws Exception {
        mail.getMessage().addHeader("From", SOLO_ADDRESS_VALUES);
        assertTrue(address.match(mail, ":all", "i;ascii-casemap", ":is",
                "from", "coyote@desert.example.org", context));
        assertFalse(address.match(mail, ":all", "i;ascii-casemap", ":is",
                "from", "elmer@hunters.example.org", context));
        assertFalse(address.match(mail, ":all", "i;ascii-casemap", ":is",
                "from", "bugs@example.org", context));
        assertFalse(address.match(mail, ":all", "i;ascii-casemap", ":is",
                "from", "roadrunner@example.org", context));
    }

    public void testMultipleAddresses() throws Exception {
        mail.getMessage().addHeader("From", MULTIPLE_ADDRESS_VALUES);
        assertTrue(address.match(mail, ":all", "i;ascii-casemap", ":is",
                "from", "coyote@desert.example.org", context));
        assertTrue(address.match(mail, ":all", "i;ascii-casemap", ":is",
                "from", "elmer@hunters.example.org", context));
        assertTrue(address.match(mail, ":all", "i;ascii-casemap", ":is",
                "from", "bugs@example.org", context));
        assertFalse(address.match(mail, ":all", "i;ascii-casemap", ":is",
                "from", "roadrunner@example.org", context));
    }
}
