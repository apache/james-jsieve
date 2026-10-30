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

    /**
     * The value of the digits, Long.MAX_VALUE when beyond.
     */
    private static long parseSaturated(String digits) {
        int start = 0;
        while (start < digits.length() - 1 && digits.charAt(start) == '0') {
            start++;
        }
        final String significant = digits.substring(start);
        if (significant.length() > 18) {
            return Long.MAX_VALUE;
        }
        return Long.parseLong(significant);
    }

    /**
     * The absolute value of the number after applying the quentifier.
     */
    private Integer fieldValue;

    /**
     * Whether the number exceeds Integer.MAX_VALUE, fieldValue then holding
     * Integer.MAX_VALUE.
     */
    private boolean aboveIntegerRange;

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
        return aboveIntegerRange;
    }

    /**
     * Sets the value of the receiver from a Token.
     * 
     * @param aToken
     *            The Token from which to extract the value to set
     */
    protected void setValue(Token aToken) {
        int endIndex = aToken.image.length();
        int magnitude = 1;
        if (aToken.image.endsWith("K")) {
            magnitude = 1024;
            endIndex--;
        } else if (aToken.image.endsWith("M")) {
            magnitude = 1048576;
            endIndex--;
        } else if (aToken.image.endsWith("G")) {
            magnitude = 1073741824;
            endIndex--;
        }

        final long number = parseSaturated(aToken.image.substring(0, endIndex));
        aboveIntegerRange = number > Integer.MAX_VALUE;
        setValue((int) Math.min(number, Integer.MAX_VALUE) * magnitude);
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return (getValue() == null) ? "null" : getValue().toString();
    }

}
