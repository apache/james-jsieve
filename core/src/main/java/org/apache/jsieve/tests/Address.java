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

package org.apache.jsieve.tests;

import static org.apache.jsieve.tests.AddressPartTags.DOMAIN_TAG;
import static org.apache.jsieve.tests.AddressPartTags.LOCALPART_TAG;

import org.apache.jsieve.SieveContext;
import org.apache.jsieve.comparators.ComparatorUtils;
import org.apache.jsieve.exception.InternetAddressException;
import org.apache.jsieve.exception.SieveException;
import org.apache.jsieve.mail.MailAdapter;
import org.apache.jsieve.mail.SieveMailException;

/**
 * Class Address implements the Addresss Test as defined in RFC 3028, section
 * 5.1.
 */
public class Address extends AbstractComparatorTest {
    /**
     * Constructor for Address.
     */
    public Address() {
        super();
    }

    protected boolean match(MailAdapter mail, String addressPart,
            String comparator, String matchType, String headerName, String key,
            SieveContext context) throws SieveException {
        final MailAdapter.Address[] addresses = getMatchingValues(mail, headerName);
        final int length = addresses.length;
        int i = 0;
        boolean isMatched = false;
        while (!isMatched && i < length) {
            isMatched = match(addressPart, comparator, matchType,
                    addresses[i++], key, context);
        }
        return isMatched;
    }

    private MailAdapter.Address[] getMatchingValues(MailAdapter mail,
            String valueName) throws SieveMailException,
            InternetAddressException {
        return mail.parseAddresses(valueName);
    }

    protected boolean match(String addressPart, String comparator,
            String matchType, MailAdapter.Address address, String key,
            SieveContext context) throws SieveException {
        final String localPart = address.getLocalPart();
        final String domain = address.getDomain();

        // Extract the part of the address we are matching on
        final String matchAddress;
        if (addressPart.equals(":all"))
            matchAddress = localPart + "@" + domain;
        else {
            matchAddress = (addressPart.equals(LOCALPART_TAG) ? localPart
                    : domain.toLowerCase());
        }

        // domain matches MUST ignore case, others should not
        String matchKey = null;
        if (addressPart.equals(DOMAIN_TAG)) {
            matchKey = key.toLowerCase();
        } else {
            matchKey = key;
        }

        // Match using the specified comparator
        return ComparatorUtils.match(comparator, matchType, matchAddress,
                matchKey, context);
    }
}
