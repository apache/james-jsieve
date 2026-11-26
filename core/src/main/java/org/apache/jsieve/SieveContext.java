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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.james.mime4j.util.MimeUtil;
import org.apache.jsieve.comparators.AsciiCasemap;
import org.apache.jsieve.exception.InternetAddressException;
import org.apache.jsieve.exception.SieveException;
import org.apache.jsieve.mail.MailAdapter;

/**
 * Context for sieve operations.
 * 
 */
public abstract class SieveContext {

    /**
     * Gets the script position of the current operation.
     * 
     * @return <code>ScriptCoordinate</code>, not null
     */
    public abstract ScriptCoordinate getCoordinate();

    /**
     * Sets the script position of the current operation.
     * 
     * @param coordinate
     *            <code>ScriptCoordinate</code>, not null
     */
    public abstract void setCoordinate(ScriptCoordinate coordinate);

    /**
     * Gets the command state manager.
     * @return command state manage, not null
     */
    public abstract CommandStateManager getCommandStateManager();

    /**
     * Gets the condition manager.
     * @return condition manager, not null
     */
    public abstract ConditionManager getConditionManager();

    /**
     * Sets the condition manager.
     * @param manager not null
     */
    public abstract void setConditionManager(final ConditionManager manager);

    /**
     * Gets the command manager.
     * @return command manager, not null
     */
    public abstract CommandManager getCommandManager();
    
    /**
     * Gets the comparator manager.
     * @return not null
     */
    public abstract ComparatorManager getComparatorManager();
    
    /**
     * Gets the test manager.
     * @return test manager, not null
     */
    public abstract TestManager getTestManager();

    /**
     * Charges the evaluation budget with a comparison of the given cost,
     * in characters compared.
     * By default, evaluation is not limited.
     *
     * @param cost not negative
     * @throws SieveException when the budget is exhausted
     */
    public void chargeComparison(long cost) throws SieveException {
    }

    /**
     * Gets the number of distinct addresses an evaluation may redirect the
     * mail to. By default, it is not limited.
     *
     * @return not negative
     */
    public int getMaxRedirects() {
        return Integer.MAX_VALUE;
    }

    /**
     * Raw header values already decoded, with their decoded value. A context
     * lives for one evaluation: this holds at most the header values of the
     * mail evaluated.
     */
    private Map<String, String> decodedHeaderValues;

    /**
     * Decodes a raw header value, unfolding it and decoding its RFC 2047
     * encoded words. A value is decoded once per context, that is per
     * evaluation, whatever the number of tests reading it. The decoded values
     * are kept as long as the context: a context must not be reused across
     * evaluations, nor shared between threads.
     *
     * @param rawValue not null
     * @return the decoded value, not null
     */
    public String decodeHeaderValue(String rawValue) {
        if (decodedHeaderValues == null) {
            decodedHeaderValues = new HashMap<>();
        }
        return decodedHeaderValues.computeIfAbsent(rawValue, MimeUtil::unscrambleHeaderValue);
    }

    /**
     * Header values already looked up, by the header name of the script.
     */
    private Map<String, List<String>> matchingHeaders;

    /**
     * Addresses already parsed, by the header name of the script.
     */
    private Map<String, MailAdapter.Address[]> parsedAddresses;

    /**
     * The characters of the header names of the mail, plus one per name, or
     * -1 before they are counted.
     */
    private long headerNamesLength = -1;

    /**
     * Looks up the values of the headers of the mail with the given name, see
     * {@link MailAdapter#getMatchingHeader(String)}. A name is looked up once
     * per context, that is per evaluation. A lookup is charged to the
     * evaluation budget: the header names of the mail it walks, the name, and
     * the values it returns.
     *
     * @param mail not null
     * @param name not null
     * @return the values, not null
     * @throws SieveException when the budget is exhausted or the mail cannot be read
     */
    public List<String> getMatchingHeader(MailAdapter mail, String name) throws SieveException {
        if (matchingHeaders == null) {
            matchingHeaders = new HashMap<>();
        }
        List<String> values = matchingHeaders.get(name);
        if (values == null) {
            chargeComparison(headerNamesLength(mail) + name.length() + 1L);
            values = mail.getMatchingHeader(name);
            chargeComparison(length(values));
            matchingHeaders.put(name, values);
        }
        return values;
    }

    /**
     * Parses the addresses of the headers of the mail with the given name,
     * see {@link MailAdapter#parseAddresses(String)}: none when they do not
     * parse as address lists. A name is parsed once per context, that is per
     * evaluation, whatever its ASCII case. A parse is charged to the
     * evaluation budget: the header names of the mail it walks and the values
     * it parses, as {@link #getMatchingHeader(MailAdapter, String)} looks
     * them up. A name with no such value, or outside ASCII, is not parsed.
     *
     * @param mail not null
     * @param headerName not null
     * @return the addresses, not null
     * @throws SieveException when the budget is exhausted or the mail cannot be read
     */
    public MailAdapter.Address[] parseAddresses(MailAdapter mail, String headerName) throws SieveException {
        if (parsedAddresses == null) {
            parsedAddresses = new HashMap<>();
        }
        // The spellings of a name that differ in ASCII case are one name
        final String key = AsciiCasemap.toAsciiUpperCase(headerName);
        MailAdapter.Address[] addresses = parsedAddresses.get(key);
        if (addresses == null) {
            final List<String> values = getMatchingHeader(mail, headerName);
            if (values.isEmpty() || !isAscii(headerName)) {
                // The adapter parses the fields it looks up itself, and may
                // match more names than the ASCII ones of getMatchingHeader
                // (String#equalsIgnoreCase folds U+017F to s): only a parse of
                // the values charged here is charged right. A field name is
                // ASCII (RFC 5322 section 3.6.8), so a name outside ASCII
                // holds no address.
                addresses = new MailAdapter.Address[0];
            } else {
                chargeComparison(headerNamesLength(mail) + length(values));
                try {
                    addresses = mail.parseAddresses(headerName);
                } catch (InternetAddressException e) {
                    addresses = new MailAdapter.Address[0];
                }
            }
            parsedAddresses.put(key, addresses);
        }
        return addresses;
    }

    private static boolean isAscii(String string) {
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) > 0x7F) {
                return false;
            }
        }
        return true;
    }

    private long headerNamesLength(MailAdapter mail) throws SieveException {
        if (headerNamesLength < 0) {
            final List<String> headerNames = mail.getHeaderNames();
            final long length = length(headerNames);
            chargeComparison(length);
            headerNamesLength = length;
        }
        return headerNamesLength;
    }

    private static long length(List<String> strings) {
        long length = 0;
        for (String string : strings) {
            length += string.length() + 1L;
        }
        return length;
    }
}
