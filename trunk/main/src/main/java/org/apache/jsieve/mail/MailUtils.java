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

package org.apache.jsieve.mail;

import java.util.ArrayList;
import java.util.List;

/**
 * Class MailUtils implements utility methods that are useful when processing
 * Sieve mail.
 */
public class MailUtils {

    /**
     * Constructor for MailUtils.
     */
    protected MailUtils() {
        super();
    }

    /**
     * <p>
     * Method getMatchingHeader answers a List of all of the headers in the mail
     * with the passed name. If no headers are found an empty List is returned.
     * </p>
     * 
     * <p>
     * This method differs from MailAdapter.getHeader(String) in that it ignores
     * case and whitespace prefixes and suffixes to a header name when
     * performing the match, as required by RFC 3028. Thus "From", "from ", "
     * From" and " from " are considered equal.
     * </p>
     * 
     * @param name
     * @return List
     * @throws SieveMailException
     */
    static public List<String> getMatchingHeader(MailAdapter mail, String name)
            throws SieveMailException {;
        final List<String> matchedHeaderValues = new ArrayList<String>(32);
        for (String headerName: mail.getHeaderNames()) {
            if (headerName.trim().equalsIgnoreCase(name))
                matchedHeaderValues.addAll(mail.getHeader(headerName));
        }
        return matchedHeaderValues;
    }
}
