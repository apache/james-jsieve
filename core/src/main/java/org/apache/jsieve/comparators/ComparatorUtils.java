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

import java.util.Arrays;

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
     * </p>
     * <p>
     * The glob is matched by a dedicated iterative matcher rather than being
     * translated to a backtracking regular expression: worst case is
     * O(string length * glob length) whatever the number of <code>*</code>.
     * As mandated by RFC 5228 section 2.7.1, <code>*</code> and
     * <code>?</code> match any character, line terminators included.
     * </p>
     * 
     * @param string
     * @param glob
     * @return boolean
     * @throws SievePatternException
     */
    static public boolean matches(String string, String glob)
            throws SievePatternException {
        final int[] pattern = compileGlob(glob);
        final int[] target = string.codePoints().toArray();

        int t = 0;
        int p = 0;
        // Position in the pattern right after the last star seen, -1 if none
        int afterStar = -1;
        // Position in the target the last star is currently matched up to
        int starMatchEnd = 0;
        while (t < target.length) {
            if (p < pattern.length && pattern[p] == GLOB_STAR) {
                afterStar = ++p;
                starMatchEnd = t;
            } else if (p < pattern.length
                    && (pattern[p] == GLOB_ANY || pattern[p] == target[t])) {
                p++;
                t++;
            } else if (afterStar >= 0) {
                // Mismatch: let the last star swallow one more character.
                // Earlier stars never need revisiting.
                p = afterStar;
                t = ++starMatchEnd;
            } else {
                return false;
            }
        }
        while (p < pattern.length && pattern[p] == GLOB_STAR) {
            p++;
        }
        return p == pattern.length;
    }

    private static final int GLOB_STAR = -1;

    private static final int GLOB_ANY = -2;

    /**
     * Compiles a Sieve glob into code points, wildcards being replaced by
     * {@link #GLOB_STAR} and {@link #GLOB_ANY}. As per RFC 5228 sections
     * 2.7.1 and 2.4.2, a backslash makes the following character literal,
     * an undefined escape such as <code>\a</code> standing for <code>a</code>.
     * A trailing backslash stands for itself. Consecutive stars are merged.
     */
    private static int[] compileGlob(String glob) {
        final int[] codePoints = glob.codePoints().toArray();
        final int[] result = new int[codePoints.length];
        int length = 0;
        for (int i = 0; i < codePoints.length; i++) {
            final int c = codePoints[i];
            if (c == '*') {
                if (length == 0 || result[length - 1] != GLOB_STAR) {
                    result[length++] = GLOB_STAR;
                }
            } else if (c == '?') {
                result[length++] = GLOB_ANY;
            } else if (c == '\\' && i + 1 < codePoints.length) {
                result[length++] = codePoints[++i];
            } else {
                result[length++] = c;
            }
        }
        return Arrays.copyOf(result, length);
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
        return container.contains(contents);
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
