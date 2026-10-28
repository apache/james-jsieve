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

import org.apache.jsieve.exception.SievePatternException;

/**
 * Class AsciiCasemap implements the EQUALITY operation of the i;ascii-casemap
 * comparator as defined by RFC2244, section 3.4 - "With this function the
 * values "hello" and "HELLO" have the same ordinal value and are considered
 * equal".
 */
public class AsciiCasemap implements Comparator {

    /**
     * Constructor for AsciiCasemap.
     */
    public AsciiCasemap() {
        super();
    }

    /**
     * @see org.apache.jsieve.comparators.Equals#equals(String, String)
     */
    public boolean equals(String string1, String string2) {
        return ComparatorUtils.equals(toAsciiUpperCase(string1),
                toAsciiUpperCase(string2));
    }

    /**
     * @see org.apache.jsieve.comparators.Contains#contains(String, String)
     */
    public boolean contains(String container, String content) {
        return ComparatorUtils.contains(toAsciiUpperCase(container),
                toAsciiUpperCase(content));
    }

    /**
     * @see org.apache.jsieve.comparators.Matches#matches(String, String)
     */
    public boolean matches(String string, String glob)
            throws SievePatternException {
        return ComparatorUtils.matches(toAsciiUpperCase(string),
                toAsciiUpperCase(glob));
    }

    /**
     * Maps a-z onto A-Z and leaves every other character untouched, as
     * defined by RFC 4790 section 9.2. Unlike {@link String#toUpperCase()},
     * this neither depends on the default locale nor changes the length of
     * the string.
     */
    static String toAsciiUpperCase(String string) {
        final char[] chars = string.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] >= 'a' && chars[i] <= 'z') {
                chars[i] = (char) (chars[i] - ('a' - 'A'));
            }
        }
        return new String(chars);
    }

}
