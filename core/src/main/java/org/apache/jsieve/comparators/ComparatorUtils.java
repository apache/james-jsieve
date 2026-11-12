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
     * Checks that the comparator can be used with the match type: RFC 5228
     * section 2.7.3 makes it an error to use with :contains or :matches a
     * comparator that has no substring operation.
     * 
     * @param comparatorName not null
     * @param matchType not null
     * @param context not null
     * @throws SieveException when they cannot be used together
     */
    public static void checkMatchType(String comparatorName, String matchType,
            SieveContext context) throws SieveException {
        if (!matchType.equals(IS_TAG)
                && !context.getComparatorManager().getComparator(comparatorName).isSubstringSupported())
            throw context.getCoordinate().syntaxException(
                    "Comparator " + comparatorName + " cannot be used with " + matchType);
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
        if (matchType.equals(IS_TAG)) {
            context.chargeComparison(linearCost(matchTarget, matchArgument));
            isMatched = is(comparatorName, matchTarget, matchArgument, context);
        }
        else if (matchType.equals(CONTAINS_TAG))
            isMatched = contains(comparatorName, matchTarget, matchArgument,
                    context);
        else if (matchType.equals(MATCHES_TAG))
            isMatched = matches(comparatorName, matchTarget, matchArgument,
                    context);
        return isMatched;
    }

    /**
     * Cost, in characters compared, of a comparison linear in the length of
     * its values, such as <code>:is</code> or {@link #contains(String, String)}.
     */
    static long linearCost(String first, String second) {
        return first.length() + 1L + second.length() + 1L;
    }

    /**
     * Cost, in characters compared, of a comparison quadratic in the length
     * of its values: the worst case of a naive substring search or of a glob
     * matcher.
     */
    static long quadraticCost(String first, String second) {
        // Lengths are below 2^31: the product cannot overflow
        return (first.length() + 1L) * (second.length() + 1L);
    }

    /**
     * Number of steps of the glob matcher charged to the evaluation budget at
     * once: the budget is checked as the matcher runs, without charging it
     * at each step.
     */
    private static final int STEPS_CHARGED_AT_ONCE = 1 << 16;

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
        try {
            return matches(string, glob, null);
        } catch (SieveException e) {
            // Not reached: nothing is charged without context
            throw new IllegalStateException(e);
        }
    }

    /**
     * Same as {@link #matches(String, String)}, charging the evaluation
     * budget of the context with the steps the matcher really takes, so that
     * a pathological glob fails once the budget is exhausted while an
     * ordinary one on a large value is charged little more than its length.
     *
     * @param string not null
     * @param glob not null
     * @param context charged with the steps taken, nothing charged when null
     * @return boolean
     * @throws SieveException when the budget is exhausted
     */
    static public boolean matches(String string, String glob, SieveContext context)
            throws SieveException {
        if (context != null) {
            context.chargeComparison(linearCost(string, glob));
        }
        final int[] pattern = compileGlob(glob);
        final int[] target = string.codePoints().toArray();

        int t = 0;
        int p = 0;
        // Position in the pattern right after the last star seen, -1 if none
        int afterStar = -1;
        // Position in the target the last star is currently matched up to
        int starMatchEnd = 0;
        // Steps taken and not charged yet
        int uncharged = 0;
        boolean mismatch = false;
        while (t < target.length) {
            if (context != null && ++uncharged == STEPS_CHARGED_AT_ONCE) {
                context.chargeComparison(uncharged);
                uncharged = 0;
            }
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
                mismatch = true;
                break;
            }
        }
        if (context != null) {
            context.chargeComparison(uncharged);
        }
        if (mismatch) {
            return false;
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
     * <p>
     * Like <code>:matches</code>, it compares characters (RFC 5228 section
     * 2.7.1): a match never starts or ends inside a surrogate pair.
     * </p>
     * <p>
     * The search is linear (Knuth-Morris-Pratt): it compares at most
     * <code>2 * container length + contents length</code> characters,
     * whatever the values.
     * </p>
     *
     * @param container
     * @param contents
     * @return boolean
     */
    static public boolean contains(String container, String contents) {
        final int length = contents.length();
        if (length == 0) {
            return true;
        }
        final int[] border = borders(contents);
        // Number of characters of contents matched so far
        int matched = 0;
        for (int i = 0; i < container.length(); i++) {
            final char c = container.charAt(i);
            while (matched > 0 && contents.charAt(matched) != c) {
                matched = border[matched - 1];
            }
            if (contents.charAt(matched) == c) {
                matched++;
            }
            if (matched == length) {
                final int end = i + 1;
                if (!splitsSurrogatePair(container, end - length)
                        && !splitsSurrogatePair(container, end)) {
                    return true;
                }
                matched = border[matched - 1];
            }
        }
        return false;
    }

    /**
     * Knuth-Morris-Pratt failure function: for each prefix of the string,
     * the length of its longest proper prefix that is also a suffix.
     */
    private static int[] borders(String string) {
        final int[] border = new int[string.length()];
        int length = 0;
        for (int i = 1; i < string.length(); i++) {
            while (length > 0 && string.charAt(i) != string.charAt(length)) {
                length = border[length - 1];
            }
            if (string.charAt(i) == string.charAt(length)) {
                length++;
            }
            border[i] = length;
        }
        return border;
    }

    private static boolean splitsSurrogatePair(String string, int index) {
        return index > 0 && index < string.length()
                && Character.isHighSurrogate(string.charAt(index - 1))
                && Character.isLowSurrogate(string.charAt(index));
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
        return comparatorObj.contains(container, contents, context);
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
        return comparatorObj.matches(string, glob, context);
    }

}
