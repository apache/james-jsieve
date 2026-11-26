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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jsieve.comparators.AsciiCasemap;

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
            throws SieveMailException {
        // Header names are compared with i;ascii-casemap (RFC 5228 section 2.7)
        final String asciiUpperCaseName = AsciiCasemap.toAsciiUpperCase(name);
        final List<String> headerNames = mail.getHeaderNames();
        final List<String> matchingNames = new ArrayList<String>();
        for (String headerName: headerNames) {
            if (AsciiCasemap.toAsciiUpperCase(headerName.trim()).equals(asciiUpperCaseName))
                matchingNames.add(headerName);
        }
        final List<String> matchedHeaderValues = new ArrayList<String>(32);
        if (matchingNames.isEmpty()) {
            return matchedHeaderValues;
        }
        final boolean ignoresCase = matchingNames.size() > 1
            && getHeaderIgnoresCase(mail, headerNames, matchingNames.get(0));
        // Spellings differing in their whitespace are asked each, whatever the adapter
        final Set<String> askedSpellings = new HashSet<String>();
        for (String headerName : matchingNames) {
            if (!ignoresCase || askedSpellings.add(AsciiCasemap.toAsciiUpperCase(headerName))) {
                matchedHeaderValues.addAll(mail.getHeader(headerName));
            }
        }
        return matchedHeaderValues;
    }

    /**
     * Whether the getHeader of the adapter ignores the case of the name, as
     * the one of JavaMail does: then each spelling of a name answers the
     * values of all of them, and asking each of k spellings would answer k
     * times k values. The adapter is asked a case spelling of a header name
     * that the mail does not hold: an adapter matching the exact name answers
     * no value for it. The spellings of the given name are tried first, then
     * those of the other names, each family of spellings at most as many
     * times as it has members, so that this is linear in the header names.
     * When the mail holds every case spelling of every name, which no mail
     * but a crafted one does, the adapter is deemed to ignore case: only the
     * values of one spelling are looked up.
     */
    private static boolean getHeaderIgnoresCase(MailAdapter mail, List<String> headerNames, String name)
            throws SieveMailException {
        final Set<String> names = new HashSet<String>(headerNames);
        final Map<String, Integer> families = new LinkedHashMap<String, Integer>();
        families.put(AsciiCasemap.toAsciiUpperCase(name), 0);
        for (String headerName : headerNames) {
            families.merge(AsciiCasemap.toAsciiUpperCase(headerName), 1, Integer::sum);
        }
        for (Map.Entry<String, Integer> family : families.entrySet()) {
            final String absent = absentSpelling(family.getKey(), family.getValue(), names);
            if (absent != null) {
                return !mail.getHeader(absent).isEmpty();
            }
        }
        return true;
    }

    /**
     * A spelling of the given upper case name, differing in the case of its
     * ASCII letters, that is not among the names, or null when the given
     * number of members of the family already holds all of them.
     */
    private static String absentSpelling(String upperCaseName, int members, Set<String> names) {
        final List<Integer> letters = new ArrayList<Integer>();
        for (int i = 0; i < upperCaseName.length(); i++) {
            final char c = upperCaseName.charAt(i);
            if (c >= 'A' && c <= 'Z') {
                letters.add(i);
            }
        }
        if (letters.size() < 31 && (1 << letters.size()) <= members) {
            return null;
        }
        // members + 1 distinct spellings: at least one of them is absent
        final char[] spelling = upperCaseName.toCharArray();
        for (int mask = 0; mask <= members; mask++) {
            for (int bit = 0; bit < letters.size(); bit++) {
                final int index = letters.get(bit);
                final boolean lower = bit < 31 && (mask & (1 << bit)) != 0;
                spelling[index] = lower ? Character.toLowerCase(upperCaseName.charAt(index)) : upperCaseName.charAt(index);
            }
            final String candidate = new String(spelling);
            if (!names.contains(candidate)) {
                return candidate;
            }
        }
        return null;
    }
}
