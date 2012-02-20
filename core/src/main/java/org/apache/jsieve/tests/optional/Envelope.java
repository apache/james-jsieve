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
        String matchAddress = null;
        if (addressPart.equals(":all"))
            matchAddress = headerValue;
        else {
            int localStart = 0;
            int localEnd = 0;
            int domainStart = 0;
            int domainEnd = headerValue.length();
            int splitIndex = headerValue.indexOf('@');
            // If there is no domain part (-1), treat it as an empty String
            if (splitIndex == -1) {
                localEnd = domainEnd;
                domainStart = domainEnd;
            } else {
                localEnd = splitIndex;
                domainStart = splitIndex + 1;
            }
            matchAddress = (addressPart.equals(LOCALPART_TAG) ? headerValue
                    .substring(localStart, localEnd) : headerValue.substring(
                    domainStart, domainEnd));
        }

        // domain matches MUST ignore case, others should not
        String matchKey = null;
        if (addressPart.equals(DOMAIN_TAG)) {
            matchKey = key.toLowerCase();
            matchAddress = matchAddress.toLowerCase();
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
