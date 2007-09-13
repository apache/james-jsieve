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

import java.util.Iterator;
import java.util.List;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.jsieve.InternetAddressException;
import org.apache.jsieve.SieveException;
import org.apache.jsieve.comparators.ComparatorUtils;
import org.apache.jsieve.mail.MailAdapter;
import org.apache.jsieve.mail.SieveMailException;
import org.apache.jsieve.mail.optional.EnvelopeAccessors;
import org.apache.jsieve.tests.AbstractCompatatorTest;
import org.apache.jsieve.tests.Address;

/**
 * Class Envelope implements the optional Envelope Test as defined in RFC 3028, 
 * section 5.4.
 */
public class Envelope extends AbstractCompatatorTest
{

    /**
     * Constructor for EnvelopeAccessors.
     */
    public Envelope()
    {
        super();
    }

    /**
     * @see org.apache.jsieve.tests.Address#getMatchingValues(MailAdapter, String)
     */
    protected List getMatchingValues(MailAdapter mail, String valueName)
        throws SieveMailException
    {
        return ((EnvelopeAccessors) mail).getMatchingEnvelope(valueName);
    }

    /**
     * Method match.
     * @param addressPart
     * @param comparator
     * @param matchType
     * @param headerValue
     * @param key
     * @return boolean
     * @throws SieveMailException
     */
    protected boolean match(String addressPart, String comparator, String matchType, String headerValue, String key) throws SieveException {
        // Attempt to create a new InternetAddress object from the headerValue
        // If this fails, the header either is not intended to contain a valid
        // Internet Address or is corrupt. Either way, its an Exception!
        String address = null;
        try
        {
            // address is a simple address; user@domain or user
            address = new InternetAddress(headerValue).getAddress();
        }
        catch (AddressException e)
        {
            throw new InternetAddressException(e.getMessage());
        }
    
        // Extract the part of the address we are matching on       
        String matchAddress = null;
        if (addressPart.equals(":all"))
            matchAddress = address;
        else
        {
            int localStart = 0;
            int localEnd = 0;
            int domainStart = 0;
            int domainEnd = address.length();
            int splitIndex = address.indexOf('@');
            // If there is no domain part (-1), treat it as an empty String
            if (splitIndex == -1)
            {
                localEnd = domainEnd;
                domainStart = domainEnd;
            }
            else
            {
                localEnd = splitIndex;
                domainStart = splitIndex + 1;
            }
            matchAddress =
                (addressPart.equals(LOCALPART_TAG)
                    ? address.substring(localStart, localEnd)
                    : address.substring(domainStart, domainEnd));
        }
    
        // domain matches MUST ignore case, others should not
        String matchKey = null;
        if (addressPart.equals(DOMAIN_TAG))
        {
            matchKey = key.toLowerCase();
            matchAddress = matchAddress.toLowerCase();
        }
        else
            matchKey = key;
    
        // Match using the specified comparator          
        return ComparatorUtils.match(
            comparator,
            matchType,
            matchAddress,
            matchKey);
    }

    protected boolean match(MailAdapter mail, String addressPart, String comparator, String matchType, String headerName, String key) throws SieveException {
        Iterator headerValuesIter =
            getMatchingValues(mail, headerName).iterator();
        boolean isMatched = false;
        while (!isMatched && headerValuesIter.hasNext())
        {
            isMatched =
                match(
                    addressPart,
                    comparator,
                    matchType,
                    ((String) headerValuesIter.next()),
                    key);
        }
        return isMatched;
    }    


    

}
