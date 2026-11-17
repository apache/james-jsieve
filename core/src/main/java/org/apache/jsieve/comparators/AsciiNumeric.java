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

package org.apache.jsieve.comparators;

import org.apache.jsieve.SieveContext;
import org.apache.jsieve.exception.FeatureException;
import org.apache.jsieve.exception.SieveException;

/**
 * Class AsciiNumeric implements the EQUALITY operation of the i;ascii-numeric
 * comparator as defined by RFC2244, section 3.4.
 */
public class AsciiNumeric implements Comparator {

    /**
     * Constructor for AsciiNumeric.
     */
    public AsciiNumeric() {
        super();
    }

    /**
     * Compares the leading digits of the values as numbers, without parsing
     * them: linear in their length, whatever the number of digits.
     *
     * @see org.apache.jsieve.comparators.Equals#equals(String, String)
     */
    public boolean equals(String string1, String string2) {
        if (isPositiveInfinity(string1) || isPositiveInfinity(string2)) {
            return isPositiveInfinity(string1) && isPositiveInfinity(string2);
        }
        final int end1 = digitEnd(string1);
        final int end2 = digitEnd(string2);
        final int start1 = firstSignificantDigit(string1, end1);
        final int start2 = firstSignificantDigit(string2, end2);
        final int length = end1 - start1;
        return length == end2 - start2
                && string1.regionMatches(start1, string2, start2, length);
    }

    /**
     * The comparison being linear, charges the lengths.
     * @see org.apache.jsieve.comparators.Equals#equals(String, String, SieveContext)
     */
    @Override
    public boolean equals(String string1, String string2, SieveContext context)
            throws SieveException {
        context.chargeComparison(ComparatorUtils.linearCost(string1, string2));
        return equals(string1, string2);
    }

    /**
     * Index of the first character that is not a digit, the length of the
     * value when all are.
     */
    private int digitEnd(final String value) {
        int i = 0;
        while (i < value.length() && isDigit(value.charAt(i))) {
            i++;
        }
        return i;
    }

    /**
     * Index of the first digit that is not a leading zero, the last digit
     * standing for zero when all are.
     */
    private int firstSignificantDigit(final String value, final int digitEnd) {
        int i = 0;
        while (i < digitEnd - 1 && value.charAt(i) == '0') {
            i++;
        }
        return i;
    }

    /**
     * Does the given string to be handled as positive infinity?
     * See <a href='http://tools.ietf.org/html/rfc4790#section-9.1.1'>RFC4790</a>
     *
     * @param value not null
     * @return true when the value should represent positive infinity,
     *         false otherwise
     */
    private boolean isPositiveInfinity(final String value) {
        return value.isEmpty() || !isDigit(value.charAt(0));
    }

    /**
     * Is the given character an ASCII digit?
     *
     * @param character character to be tested
     * @return true when the given character is an ASCII digit,
     *         false otherwise
     */
    private boolean isDigit(final char character) {
        return character >= 0x30 && character <= 0x39;
    }

    /**
     * Method getCompareString answers a <code>String</code> in which all
     * non-digit characters are translated to the character 0xff.
     *
     * @param string
     * @return String
     */
    protected String computeCompareString(String string) {
        char[] chars = string.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (!isDigit(chars[i]))
                chars[i] = 0xff;
        }
        return new String(chars);
    }

    /**
     * i;ascii-numeric has no substring operation (RFC 4790 section 9.1.1).
     *
     * @see org.apache.jsieve.comparators.Comparator#isSubstringSupported()
     */
    @Override
    public boolean isSubstringSupported() {
        return false;
    }

    /**
     * Unsupported, see <a href='http://tools.ietf.org/html/rfc4790#section-9.1.1'>RFC4790</a>.
     *
     * @see org.apache.jsieve.comparators.Contains#contains(String, String)
     */
    @Override
    public boolean contains(String container, String content) throws FeatureException {
        // TODO: Consider using finer grained exception
        throw new FeatureException("Substring match unsupported by ascii-numeric");
    }

    /**
     * Unsupported operation.
     * <a href='http://tools.ietf.org/html/rfc5228#section-2.7.1'>RFC5228</a> limits
     * support to comparators that support <code>:contains</code>.
     * <a href='http://tools.ietf.org/html/rfc4790#section-9.1.1'>RFC4790</a> states
     * that substring matches are not supported.
     *
     * @see org.apache.jsieve.comparators.Matches#matches(String, String)
     */
    @Override
    public boolean matches(String string, String glob)
            throws FeatureException {
        // TODO: Consider using finer grained exception
        throw new FeatureException("Substring match unsupported by ascii-numeric");
    }

}
