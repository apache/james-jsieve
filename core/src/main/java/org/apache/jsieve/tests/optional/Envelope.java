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

package org.apache.jsieve.tests.optional;


import static org.apache.jsieve.tests.AddressPartTags.DOMAIN_TAG;
import static org.apache.jsieve.tests.AddressPartTags.LOCALPART_TAG;

import java.util.List;

import org.apache.jsieve.SieveContext;
import org.apache.jsieve.comparators.AsciiCasemap;
import org.apache.jsieve.comparators.ComparatorUtils;
import org.apache.jsieve.exception.SieveException;
import org.apache.jsieve.mail.MailAdapter;
import org.apache.jsieve.mail.SieveMailException;
import org.apache.jsieve.mail.optional.EnvelopeAccessors;
import org.apache.jsieve.tests.AbstractComparatorTest;

/**
 * Class Envelope implements the optional Envelope Test as defined in RFC 3028,
 * section 5.4.
 */
public class Envelope extends AbstractComparatorTest {

    /**
     * The address of an envelope value without the angle brackets and the
     * source route of an SMTP path (RFC 5321 section 4.1.2): RFC 5228 section
     * 5.4 says that all tests against envelopes MUST drop source routes, and
     * matches the null reverse-path as the empty string.
     */
    private static String address(String envelopeValue) {
        String path = envelopeValue;
        if (path.length() >= 2 && path.startsWith("<") && path.endsWith(">")) {
            path = path.substring(1, path.length() - 1);
        }
        // A local part never starts with '@', a source route always does
        final int routeEnd = path.startsWith("@") ? path.indexOf(':') : -1;
        return path.substring(routeEnd + 1);
    }

    /**
     * Constructor for EnvelopeAccessors.
     */
    public Envelope() {
        super();
    }

    protected List<String> getMatchingValues(MailAdapter mail, String valueName)
            throws SieveMailException {
        return ((EnvelopeAccessors) mail).getMatchingEnvelope(valueName);
    }

    /**
     * Method match.
     * 
     * @param addressPart
     * @param comparator
     * @param matchType
     * @param headerValue
     * @param key
     * @param context not null
     * @return boolean
     * @throws SieveMailException
     */
    protected boolean match(String addressPart, String comparator,
            String matchType, String headerValue, String key,
            SieveContext context) throws SieveException {

        // Extract the part of the address we are matching on
        final String address = address(headerValue);
        String matchAddress = null;
        if (addressPart.equals(":all"))
            matchAddress = address;
        else {
            int localStart = 0;
            int localEnd = 0;
            int domainStart = 0;
            int domainEnd = address.length();
            // A domain name holds no '@', a quoted local part may (RFC 5321 section 4.1.2)
            int splitIndex = address.lastIndexOf('@');
            // If there is no domain part (-1), treat it as an empty String
            if (splitIndex == -1) {
                localEnd = domainEnd;
                domainStart = domainEnd;
            } else {
                localEnd = splitIndex;
                domainStart = splitIndex + 1;
            }
            matchAddress = (addressPart.equals(LOCALPART_TAG) ? address
                    .substring(localStart, localEnd) : address.substring(
                    domainStart, domainEnd));
        }

        // domain matches MUST ignore case, others should not
        String matchKey = null;
        if (addressPart.equals(DOMAIN_TAG)) {
            matchKey = AsciiCasemap.toAsciiUpperCase(key);
            matchAddress = AsciiCasemap.toAsciiUpperCase(matchAddress);
        } else
            matchKey = key;

        // Match using the specified comparator
        return ComparatorUtils.match(comparator, matchType, matchAddress,
                matchKey, context);
    }

    protected boolean match(MailAdapter mail, String addressPart,
            String comparator, String matchType, String headerName, String key,
            SieveContext context) throws SieveException {
        final List<String> headerValues = getMatchingValues(mail, headerName);
        boolean isMatched = false;
        for (final String value:headerValues) {
            isMatched = match(addressPart, comparator, matchType, value, key, context);
            if (isMatched) {
                break;
            }
        }
        return isMatched;
    }

}
