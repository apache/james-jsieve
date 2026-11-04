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
     * The local part and the domain of an address, or null when it is not a
     * mailbox of RFC 5321 section 4.1.2 (with the non-ASCII text RFC 6531
     * section 3.3 adds): neither :localpart nor :domain matches such an
     * address (RFC 5228 section 2.7.4). An address without domain, such as
     * Postmaster, has the empty domain, and the null reverse-path, the empty
     * string, is the empty string for both parts (RFC 5228 section 5.4).
     */
    private static String[] mailboxParts(String address) {
        if (address.isEmpty()) {
            return new String[] {"", ""};
        }
        final int localEnd = localPartEnd(address);
        if (localEnd == 0) {
            return null;
        }
        if (localEnd == address.length()) {
            return new String[] {address, ""};
        }
        final String domain = address.substring(localEnd + 1);
        if (address.charAt(localEnd) != '@' || !isDomain(domain)) {
            return null;
        }
        return new String[] {address.substring(0, localEnd), domain};
    }

    /**
     * The end of the Dot-string or the Quoted-string the address starts with,
     * 0 when it starts with neither.
     */
    private static int localPartEnd(String address) {
        if (address.charAt(0) == '"') {
            int i = 1;
            while (i < address.length()) {
                final char c = address.charAt(i);
                if (c == '"') {
                    return i + 1;
                } else if (c == '\\' && i + 1 < address.length() && address.charAt(i + 1) >= 32
                        && address.charAt(i + 1) <= 126) {
                    i += 2;
                } else if (isQtext(c)) {
                    i++;
                } else {
                    return 0;
                }
            }
            return 0;
        }
        // Dot-string = Atom *("." Atom)
        int i = 0;
        boolean atomExpected = true;
        while (i < address.length()) {
            final char c = address.charAt(i);
            if (isAtext(c)) {
                atomExpected = false;
            } else if (c == '.' && !atomExpected) {
                atomExpected = true;
            } else {
                break;
            }
            i++;
        }
        return atomExpected ? 0 : i;
    }

    /**
     * Is it a Domain, dot-separated labels of letters, digits and hyphens
     * starting and ending with a letter or digit, or an address-literal
     * between brackets (RFC 5321 section 4.1.2)?
     */
    private static boolean isDomain(String domain) {
        if (domain.startsWith("[")) {
            return domain.length() >= 3 && domain.endsWith("]")
                    && isAddressLiteral(domain.substring(1, domain.length() - 1));
        }
        int labelStart = 0;
        for (int i = 0; i <= domain.length(); i++) {
            if (i == domain.length() || domain.charAt(i) == '.') {
                if (i == labelStart || domain.charAt(labelStart) == '-' || domain.charAt(i - 1) == '-') {
                    return false;
                }
                labelStart = i + 1;
            } else if (!isLetterOrDigit(domain.charAt(i)) && domain.charAt(i) != '-') {
                return false;
            }
        }
        return true;
    }

    /**
     * Is it, without its brackets, an IPv4 address, "IPv6:" and an IPv6
     * address, or a standardized tag, ":" and dcontent (RFC 5321 section
     * 4.1.3)?
     */
    private static boolean isAddressLiteral(String literal) {
        final int colon = literal.indexOf(':');
        if (colon < 0) {
            return isIpv4(literal);
        }
        final String tag = literal.substring(0, colon);
        final String content = literal.substring(colon + 1);
        if (AsciiCasemap.toAsciiUpperCase(tag).equals("IPV6")) {
            return isIpv6(content);
        }
        if (!isLdhStr(tag) || content.isEmpty()) {
            return false;
        }
        for (int i = 0; i < content.length(); i++) {
            final char c = content.charAt(i);
            if (c < 33 || c > 126 || c == '[' || c == '\\' || c == ']') {
                return false;
            }
        }
        return true;
    }

    /**
     * Is it four numbers from 0 to 255, of one to three digits, separated by
     * dots?
     */
    private static boolean isIpv4(String address) {
        final String[] numbers = address.split("\\.", -1);
        if (numbers.length != 4) {
            return false;
        }
        for (String number : numbers) {
            if (number.isEmpty() || number.length() > 3 || !isDigits(number) || Integer.parseInt(number) > 255) {
                return false;
            }
        }
        return true;
    }

    /**
     * Is it eight groups of one to four hexadecimal digits, the last two of
     * which may be an IPv4 address, or at most six such groups around a "::"
     * that stands for at least two groups of zeros?
     */
    private static boolean isIpv6(String address) {
        final int compression = address.indexOf("::");
        if (compression < 0) {
            return groupCount(address, true) == 8;
        }
        final String left = address.substring(0, compression);
        final String right = address.substring(compression + 2);
        if (right.contains("::")) {
            return false;
        }
        final int leftGroups = left.isEmpty() ? 0 : groupCount(left, false);
        final int rightGroups = right.isEmpty() ? 0 : groupCount(right, true);
        return leftGroups >= 0 && rightGroups >= 0 && leftGroups + rightGroups <= 6;
    }

    /**
     * The number of 16-bit groups in colon-separated hexadecimal groups, an
     * IPv4 address in last position counting for two when allowed, or -1 when
     * they are not such groups.
     */
    private static int groupCount(String groups, boolean ipv4Allowed) {
        final String[] parts = groups.split(":", -1);
        int count = 0;
        for (int i = 0; i < parts.length; i++) {
            final String part = parts[i];
            if (ipv4Allowed && i == parts.length - 1 && part.indexOf('.') >= 0) {
                if (!isIpv4(part)) {
                    return -1;
                }
                count += 2;
            } else if (part.isEmpty() || part.length() > 4 || !isHexDigits(part)) {
                return -1;
            } else {
                count++;
            }
        }
        return count;
    }

    /**
     * Is it an Ldh-str: letters, digits and hyphens ending with a letter or
     * digit (RFC 5321 section 4.1.2)?
     */
    private static boolean isLdhStr(String string) {
        if (string.isEmpty() || string.charAt(string.length() - 1) == '-') {
            return false;
        }
        for (int i = 0; i < string.length(); i++) {
            final char c = string.charAt(i);
            if (!(c >= 'a' && c <= 'z') && !(c >= 'A' && c <= 'Z') && !(c >= '0' && c <= '9') && c != '-') {
                return false;
            }
        }
        return true;
    }

    private static boolean isDigits(String string) {
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) < '0' || string.charAt(i) > '9') {
                return false;
            }
        }
        return true;
    }

    private static boolean isHexDigits(String string) {
        for (int i = 0; i < string.length(); i++) {
            final char c = string.charAt(i);
            if (!(c >= '0' && c <= '9') && !(c >= 'a' && c <= 'f') && !(c >= 'A' && c <= 'F')) {
                return false;
            }
        }
        return true;
    }

    private static boolean isLetterOrDigit(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c >= 0x80;
    }

    private static boolean isAtext(char c) {
        return isLetterOrDigit(c) || "!#$%&'*+-/=?^_`{|}~".indexOf(c) >= 0;
    }

    private static boolean isQtext(char c) {
        return (c >= 32 && c <= 126 && c != '"' && c != '\\') || c >= 0x80;
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
            final String[] parts = mailboxParts(address);
            if (parts == null) {
                return false;
            }
            matchAddress = addressPart.equals(LOCALPART_TAG) ? parts[0] : parts[1];
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
