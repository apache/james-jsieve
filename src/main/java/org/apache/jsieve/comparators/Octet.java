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
 * Class Octet implements the EQUALITY operation of the i;octet comparator as
 * defined by RFC2244, section 3.4 - "For the equality function, two strings are
 * equal if they are the same length and contain the same octets in the same
 * order. NIL is equal only to itself".
 */
public class Octet implements Comparator {

    /**
     * Constructor for Octet.
     */
    public Octet() {
        super();
    }

    /**
     * @see org.apache.jsieve.comparators.Equals#equals(String, String)
     */
    public boolean equals(String string1, String string2) {
        return ComparatorUtils.equals(string1, string2);
    }

    /**
     * @see org.apache.jsieve.comparators.Contains#contains(String, String)
     */
    public boolean contains(String container, String content) {
        return ComparatorUtils.contains(container, content);
    }

    /**
     * @see org.apache.jsieve.comparators.Matches#matches(String, String)
     */
    public boolean matches(String string, String glob)
            throws SievePatternException {
        return ComparatorUtils.matches(string, glob);
    }

}
