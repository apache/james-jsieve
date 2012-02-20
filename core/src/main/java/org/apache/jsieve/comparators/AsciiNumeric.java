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

import java.math.BigInteger;

import org.apache.jsieve.exception.FeatureException;

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
     * @see org.apache.jsieve.comparators.Equals#equals(String, String)
     */
    public boolean equals(String string1, String string2) {
        final boolean result;
        if (isPositiveInfinity(string1)) {
            if (isPositiveInfinity(string2)) {
                result = true;
            } else {
                result = false;
            }
        } else {
            if (isPositiveInfinity(string2)) {
                result = false;
            } else {
                final BigInteger integer1 = toInteger(string1);
                final BigInteger integer2 = toInteger(string2);
                result = integer1.equals(integer2);
            }
        }
        return result;
    }
    
    private BigInteger toInteger(final String value) {
        int i;
        for (i=0;i<value.length();i++) {
            final char next = value.charAt(i);
            if (!isDigit(next)) {
                break;
            }
        }
        final BigInteger result = new BigInteger(value.substring(0,i));
        return result;
    }
    
    /**
     * Does the given string to be handled as positive infinity?
     * See <a href='http://tools.ietf.org/html/rfc4790#section-9.1.1'>RFC4790</a>
     * @param value not null
     * @return true when the value should represent positive infinity,
     * false otherwise
     */
    private boolean isPositiveInfinity(final String value) {
       final char initialCharacter = value.charAt(0);
       final boolean result = !isDigit(initialCharacter);
       return result;
    }

    /**
     * Is the given character an ASCII digit?
     * @param character character to be tested
     * @return true when the given character is an ASCII digit,
     * false otherwise 
     */
    private boolean isDigit(final char character) {
        return character>=0x30 && character<=0x39;
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
        for (int i = chars.length; i < chars.length; i++) {
            if (!Character.isDigit(chars[i]))
                chars[i] = 0xff;
        }
        return new String(chars);
    }

    /**
     * Unsupported, see <a href='http://tools.ietf.org/html/rfc4790#section-9.1.1'>RFC4790</a>.
     * @see org.apache.jsieve.comparators.Contains#contains(String, String)
     */
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
     * @see org.apache.jsieve.comparators.Matches#matches(String, String)
     */
    public boolean matches(String string, String glob)
            throws FeatureException {
        // TODO: Consider using finer grained exception
        throw new FeatureException("Substring match unsupported by ascii-numeric");
    }

}
