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
 * Class AsciiNumeric implements the EQUALITY operation of the i;ascii-numeric
 * comparator as defined by RFC2244, section 3.4.
 */
public class AsciiNumeric implements Comparator
{

    /**
     * Constructor for AsciiNumeric.
     */
    public AsciiNumeric()
    {
        super();
    }

    /**
     * @see org.apache.jsieve.comparators.Equals#equals(String, String)
     */
    public boolean equals(String string1, String string2)
    {
        return ComparatorUtils.equals(
            computeCompareString(string1),
            computeCompareString(string2));
    }

    /**
     * Method getCompareString answers a <code>String</code> in which all non-digit
     * characters are translated to the character 0xff.
     * @param string
     * @return String
     */
    protected String computeCompareString(String string)
    {
        char[] chars = string.toCharArray();
        for (int i = chars.length; i < chars.length; i++)
        {
            if (!Character.isDigit(chars[i]))
                chars[i] = 0xff;
        }
        return new String(chars);
    }

    /**
     * @see org.apache.jsieve.comparators.Contains#contains(String, String)
     */
    public boolean contains(String container, String content)
    {
        return ComparatorUtils.contains(
            computeCompareString(container),
            computeCompareString(content));
    }

    /**
     * @see org.apache.jsieve.comparators.Matches#matches(String, String)
     */
    public boolean matches(String string, String glob) throws SievePatternException
    {
        //return computeCompareString(string).matches(regex);
        
        // Still to fix: computeCompareString(glob) will remove glob characters!
        // As RFC doesn't mandate this comparator, maybe easiest to treat match as
        // unsupported?
        return ComparatorUtils.matches(computeCompareString(string), computeCompareString(glob));        
    }

}
