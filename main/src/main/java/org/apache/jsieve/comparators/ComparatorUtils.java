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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.jsieve.SieveContext;
import org.apache.jsieve.exception.LookupException;
import org.apache.jsieve.exception.SieveException;
import org.apache.jsieve.exception.SievePatternException;

import static  org.apache.jsieve.comparators.MatchTypeTags.*;

/**
 * Class ComparatorUtils implements utility methods used by Comparators.
 */
public class ComparatorUtils {

    /**
     * Constructor for ComparatorUtils.
     */
    private ComparatorUtils() {
        super();
    }

    /**
     * Method <code>match</code> answers a boolean indicating if the parameter
     * <code>matchTarget</code> compares to parameter
     * <code>matchArgument</code> is a match of <code>matchType</code> using
     * the comparator <code>comparatorName</code>.
     * 
     * @param comparatorName not null
     * @param matchType not null
     * @param matchTarget not null
     * @param matchArgument not null
     * @param context not null
     * @return boolean
     */
    public static boolean match(String comparatorName, String matchType,
            String matchTarget, String matchArgument, SieveContext context)
            throws SieveException {
        boolean isMatched = false;
        if (matchType.equals(IS_TAG))
            isMatched = is(comparatorName, matchTarget, matchArgument, context);
        else if (matchType.equals(CONTAINS_TAG))
            isMatched = contains(comparatorName, matchTarget, matchArgument,
                    context);
        else if (matchType.equals(MATCHES_TAG))
            isMatched = matches(comparatorName, matchTarget, matchArgument,
                    context);
        return isMatched;
    }

    /**
     * <p>
     * Method <code>matches</code> answers a boolean indicating if the
     * parameter <code>string</code> matches the glob pattern described by
     * parameter <code>glob</code>.
     * 
     * @param string
     * @param glob
     * @return boolean
     * @throws SievePatternException
     */
    static public boolean matches(String string, String glob)
            throws SievePatternException {
        try {
            String regex = sieveToJavaRegex(glob);
            final Matcher matcher = Pattern.compile(regex).matcher(string);
            return matcher.matches();
        } catch (PatternSyntaxException e) {
            throw new SievePatternException(e.getMessage());
        }
    }

    /**
     * <p>
     * Method <code>contains</code> answers a boolean indicating if the
     * parameter <code>container</code> contains the parameter
     * <code>contents</code>.
     * </p>
     * 
     * @param container
     * @param contents
     * @return boolean
     */
    static public boolean contains(String container, String contents) {
        return container.indexOf(contents) > -1;
    }

    /**
     * <p>
     * Method <code>equals</code> answers a boolean indicating if the
     * parameter <code>string1</code> is equal to the parameter
     * <code>string2</code>.
     * </p>
     * 
     * @param string1
     * @param string2
     * @return boolean
     */
    static public boolean equals(String string1, String string2) {
        return string1.equals(string2);
    }

    /**
     * Returns true if the char is a special char for regex
     */
    private static boolean isRegexSpecialChar(char ch) {
        return (ch == '*' || ch == '?' || ch == '+' || ch == '[' || ch == ']'
                || ch == '(' || ch == ')' || ch == '|' || ch == '^'
                || ch == '$' || ch == '.' || ch == '{' || ch == '}' || ch == '\\');
    }

    /**
     * Returns true if the char is a special char for sieve matching
     */
    private static boolean isSieveMatcherSpecialChar(char ch) {
        return (ch == '*' || ch == '?' || ch == '\\');
    }

    /**
     * Converts a Sieve pattern in a java regex pattern
     */
    public static String sieveToJavaRegex(String pattern) {
        int ch;
        StringBuffer buffer = new StringBuffer(2 * pattern.length());
        boolean lastCharWasStar = false;
        for (ch = 0; ch < pattern.length(); ch++) {
            final char nextChar = pattern.charAt(ch);
            switch (nextChar) {
            case '*':
                //
                // Java Matcher has issues with repeated stars
                //
                if (!lastCharWasStar) {
                    buffer.append(".*");
                }
                break;
            case '?':
                buffer.append('.');
                break;
            case '\\':
                buffer.append('\\');
                if (ch == pattern.length() - 1)
                    buffer.append('\\');
                else if (isSieveMatcherSpecialChar(pattern.charAt(ch + 1)))
                    buffer.append(pattern.charAt(++ch));
                else
                    buffer.append('\\');
                break;
            default:
                if (isRegexSpecialChar(nextChar))
                    buffer.append('\\');
                buffer.append(nextChar);
                break;
            }
            // Workaround for issue with Java Matcher
            lastCharWasStar = '*' == nextChar;
        }
        return buffer.toString();
    }

    /**
     * Method <code>contains<code> answers a boolean indicating if the parameter 
     * <code>container</code> contains the parameter <code>contents</code> using an
     * instance of <code>comparatorName</code>.
     * @param comparatorName not null
     * @param container not null
     * @param contents not null
     * @param context not null
     * @return boolean
     */
    public static boolean contains(String comparatorName, String container,
            String contents, SieveContext context) throws SieveException {
        Contains comparatorObj = context.getComparatorManager().getComparator(comparatorName);
        return comparatorObj.contains(container, contents);
    }

    /**
     * Method <code>is<code> answers a boolean indicating if the parameter 
     * <code>container</code> is equal to the parameter <code>contents</code> using 
     * an instance of <code>comparatorName</code>.
     * @param comparatorName
     * @param string1 not null
     * @param string2 not null
     * @param context not null
     * @return boolean
     */
    public static boolean is(String comparatorName, String string1,
            String string2, SieveContext context) throws LookupException {
        Equals comparatorObj = context.getComparatorManager().getComparator(comparatorName);
        return comparatorObj.equals(string1, string2);
    }

    /**
     * Method <code>matches</code> answers a boolean indicating if the
     * parameter
     * <code>string/code> is matched by the patterm <code>glob</code> using an
     * instance of <code>comparatorName</code>.
     * @param comparatorName not null
     * @param string not null
     * @param glob not null
     * @param context not null
     * @return boolean
     */
    public static boolean matches(String comparatorName, String string,
            String glob, SieveContext context) throws SieveException {
        Matches comparatorObj = context.getComparatorManager().getComparator(comparatorName);
        return comparatorObj.matches(string, glob);
    }

}
