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

import java.util.Locale;

import org.apache.jsieve.parser.generated.Token;

/**
 * <p>
 * A parsed representation of the RFC3028 BNF...
 * </p>
 * 
 * <code>1*DIGIT [QUANTIFIER]</code>
 * 
 * <p>
 * Note that the stored value is the absolute value after applying the
 * quantifier.
 * </p>
 * 
 */
public class NumberArgument implements Argument {

    private static final String LONG_MAX_DIGITS = Long.toString(Long.MAX_VALUE);

    /**
     * The value of the digits, Long.MAX_VALUE when beyond.
     */
    private static long parseSaturated(String digits) {
        int start = 0;
        while (start < digits.length() - 1 && digits.charAt(start) == '0') {
            start++;
        }
        final String significant = digits.substring(start);
        // digit strings of the same length compare as their numbers
        if (significant.length() > LONG_MAX_DIGITS.length()
                || (significant.length() == LONG_MAX_DIGITS.length()
                    && significant.compareTo(LONG_MAX_DIGITS) > 0)) {
            return Long.MAX_VALUE;
        }
        return Long.parseLong(significant);
    }

    /**
     * The absolute value of the number after applying the quentifier.
     */
    private Integer fieldValue;

    /**
     * The value in full, Long.MAX_VALUE when beyond: fieldValue holds at most
     * Integer.MAX_VALUE.
     */
    private long longValue;

    /**
     * Constructor for NumberArgument.
     */
    private NumberArgument() {
        super();
    }

    /**
     * Constructor for NumberArgument.
     * 
     * @param token
     */
    public NumberArgument(Token token) {
        this();
        setValue(token);
    }

    /**
     * Sets the value of the reciver to an Integer.
     * 
     * @param number
     *            The value to set
     */
    protected void setValue(Integer number) {
        fieldValue = number;
        longValue = number == null ? 0 : number;
    }

    /**
     * @see org.apache.jsieve.Argument#getValue()
     */
    public Object getValue() {
        return fieldValue;
    }

    /**
     * Method getInteger answers the value of the receiver as an Integer.
     * 
     * @return Integer
     */
    public Integer getInteger() {
        return fieldValue;
    }

    /**
     * RFC 5228 section 2.4.1 only requires numbers up to 2^31 - 1: a larger
     * one is held as Integer.MAX_VALUE by {@link #getInteger()}. It still
     * exceeds every mail size, an int.
     * 
     * @return whether the number exceeds Integer.MAX_VALUE
     */
    public boolean isAboveIntegerRange() {
        return longValue > Integer.MAX_VALUE;
    }

    /**
     * Method getLong answers the value of the receiver in full, even above
     * Integer.MAX_VALUE, and Long.MAX_VALUE for a larger one.
     * 
     * @return long
     */
    public long getLong() {
        return longValue;
    }

    /**
     * Sets the value of the receiver from a Token.
     * 
     * @param aToken
     *            The Token from which to extract the value to set
     */
    protected void setValue(Token aToken) {
        // digits and an ASCII quantifier, which is case-insensitive (RFC 5228 section 8.1)
        final String image = aToken.image.toUpperCase(Locale.ROOT);
        int endIndex = image.length();
        int magnitude = 1;
        if (image.endsWith("K")) {
            magnitude = 1024;
            endIndex--;
        } else if (image.endsWith("M")) {
            magnitude = 1048576;
            endIndex--;
        } else if (image.endsWith("G")) {
            magnitude = 1073741824;
            endIndex--;
        }

        final long number = parseSaturated(image.substring(0, endIndex));
        final long value = number > Long.MAX_VALUE / magnitude ? Long.MAX_VALUE : number * magnitude;
        setValue((int) Math.min(value, Integer.MAX_VALUE));
        longValue = value;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return (getValue() == null) ? "null" : getValue().toString();
    }

}
