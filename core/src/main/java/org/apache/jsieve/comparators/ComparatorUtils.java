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
        return matches(string, glob, context, false);
    }

    /**
     * Same as {@link #matches(String, String, SieveContext)}, comparing the
     * characters after {@link #toAsciiUpperCase(int)} when
     * <code>ignoreAsciiCase</code>, without copying the string.
     */
    static boolean matches(String string, String glob, SieveContext context,
            boolean ignoreAsciiCase) throws SieveException {
        if (context != null) {
            context.chargeComparison(linearCost(string, glob));
        }
        final int[] pattern = compileGlob(glob, ignoreAsciiCase);
        // The string is walked by code point, without copying it: ? matches
        // a character and a match never splits a surrogate pair
        final int length = string.length();

        // Position, in chars, in the string
        int t = 0;
        int p = 0;
        // Position in the pattern right after the last star seen, -1 if none
        int afterStar = -1;
        // Position, in chars, in the string the last star is currently
        // matched up to
        int starMatchEnd = 0;
        // Steps of failed attempts not charged yet. The steps that are never
        // undone (stars, and characters matched before the last star or in
        // the last attempt) are at most the lengths charged up front.
        long uncharged = 0;
        boolean mismatch = false;
        // After the last star, the pattern can only match the end of the
        // string: it is compared there once instead of at each position
        int lastStar = pattern.length - 1;
        while (lastStar >= 0 && pattern[lastStar] != GLOB_STAR) {
            lastStar--;
        }
        while (t < length) {
            if (p < pattern.length && pattern[p] == GLOB_STAR) {
                if (p == lastStar) {
                    if (context != null) {
                        context.chargeComparison(uncharged);
                    }
                    return matchesEnd(string, t, pattern, p + 1, ignoreAsciiCase);
                }
                afterStar = ++p;
                starMatchEnd = t;
                continue;
            }
            // String#codePointAt costs several times charAt on this path,
            // taken for each step: a pair is only looked for at a high
            // surrogate
            final char ch = string.charAt(t);
            int c = ignoreAsciiCase ? toAsciiUpperCase(ch) : ch;
            int width = 1;
            if (Character.isHighSurrogate(ch) && t + 1 < length
                    && Character.isLowSurrogate(string.charAt(t + 1))) {
                c = Character.toCodePoint(ch, string.charAt(t + 1));
                width = 2;
            }
            if (p < pattern.length
                    && (pattern[p] == GLOB_ANY || pattern[p] == c)) {
                p++;
                t += width;
            } else if (afterStar >= 0) {
                // Mismatch: let the last star swallow one more character.
                // Earlier stars never need revisiting.
                if (context != null) {
                    // The characters matched since the star are compared again
                    uncharged += t - starMatchEnd + 1;
                    if (uncharged >= STEPS_CHARGED_AT_ONCE) {
                        context.chargeComparison(uncharged);
                        uncharged = 0;
                    }
                }
                p = afterStar;
                starMatchEnd += Character.charCount(string.codePointAt(starMatchEnd));
                t = starMatchEnd;
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

    /**
     * Whether the end of <code>string</code>, from <code>start</code> on,
     * ends with <code>pattern</code> from <code>from</code> on, a part
     * without star: each of its elements matches one character, so the
     * characters it matches are the last ones.
     */
    private static boolean matchesEnd(String string, int start, int[] pattern, int from,
            boolean ignoreAsciiCase) {
        int i = string.length();
        for (int k = pattern.length - 1; k >= from; k--) {
            if (i <= start) {
                return false;
            }
            final char ch = string.charAt(i - 1);
            int c = ignoreAsciiCase ? toAsciiUpperCase(ch) : ch;
            i--;
            if (Character.isLowSurrogate(ch) && i > start
                    && Character.isHighSurrogate(string.charAt(i - 1))) {
                c = Character.toCodePoint(string.charAt(i - 1), ch);
                i--;
            }
            if (pattern[k] != GLOB_ANY && pattern[k] != c) {
                return false;
            }
        }
        return true;
    }

    private static final int GLOB_STAR = -1;

    private static final int GLOB_ANY = -2;

    /**
     * Compiles a Sieve glob into code points, wildcards being replaced by
     * {@link #GLOB_STAR} and {@link #GLOB_ANY}. As per RFC 5228 sections
     * 2.7.1 and 2.4.2, a backslash makes the following character literal,
     * an undefined escape such as <code>\a</code> standing for <code>a</code>.
     * A trailing backslash stands for itself. Consecutive stars are merged.
     * Literals are upper-cased by {@link #toAsciiUpperCase(int)} when
     * <code>ignoreAsciiCase</code>.
     */
    private static int[] compileGlob(String glob, boolean ignoreAsciiCase) {
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
                result[length++] = ignoreAsciiCase ? toAsciiUpperCase(codePoints[++i]) : codePoints[++i];
            } else {
                result[length++] = ignoreAsciiCase ? toAsciiUpperCase(c) : c;
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
     * The search is linear whatever the values. A contents of at most
     * {@link #SHORT_CONTENTS} characters, the usual case of a script key, is
     * searched with {@link String#indexOf(String, int)}: at most
     * {@link #SHORT_CONTENTS} characters are compared per character of the
     * container, and the JIT runs it much faster than a scalar loop. A
     * longer contents, or one compared ignoring the ASCII case, is searched
     * with Knuth-Morris-Pratt, which compares at most
     * <code>2 * container length + contents length</code> characters.
     * </p>
     *
     * @param container
     * @param contents
     * @return boolean
     */
    static public boolean contains(String container, String contents) {
        return contains(container, contents, false);
    }

    /**
     * Same as {@link #contains(String, String)}, comparing the characters
     * after {@link #toAsciiUpperCase(int)} when <code>ignoreAsciiCase</code>,
     * without copying the container.
     */
    static boolean contains(String container, String contents, boolean ignoreAsciiCase) {
        final int length = contents.length();
        if (length == 0) {
            return true;
        }
        if (ignoreAsciiCase) {
            // The contents is short, usually a constant of the script
            contents = toAsciiUpperCase(contents);
            if (!hasAsciiLetter(contents)) {
                // A character of the container matches a non-letter only
                // when equal to it: the case is irrelevant
                ignoreAsciiCase = false;
            }
        }
        if (!ignoreAsciiCase && length <= SHORT_CONTENTS) {
            for (int i = container.indexOf(contents); i >= 0; i = container.indexOf(contents, i + 1)) {
                if (!splitsSurrogatePair(container, i)
                        && !splitsSurrogatePair(container, i + length)) {
                    return true;
                }
            }
            return false;
        }
        // Ignoring the case, no intrinsic applies: a naive search would compare
        // up to SHORT_CONTENTS characters per character of the container while
        // the budget is charged linearly
        final int[] border = borders(contents);
        // Number of characters of contents matched so far
        int matched = 0;
        for (int i = 0; i < container.length(); i++) {
            final char c = ignoreAsciiCase ? toAsciiUpperCase(container.charAt(i)) : container.charAt(i);
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
     * Maps a-z onto A-Z and leaves every other character untouched, as
     * defined by RFC 4790 section 9.2.
     */
    static int toAsciiUpperCase(int c) {
        return c >= 'a' && c <= 'z' ? c - ('a' - 'A') : c;
    }

    private static char toAsciiUpperCase(char c) {
        return c >= 'a' && c <= 'z' ? (char) (c - ('a' - 'A')) : c;
    }

    /**
     * Same as {@link #toAsciiUpperCase(int)} for each character of the
     * string, the string itself being returned when nothing changes.
     */
    static String toAsciiUpperCase(String string) {
        int i = 0;
        while (i < string.length() && toAsciiUpperCase(string.charAt(i)) == string.charAt(i)) {
            i++;
        }
        if (i == string.length()) {
            return string;
        }
        final char[] chars = string.toCharArray();
        for (; i < chars.length; i++) {
            chars[i] = toAsciiUpperCase(chars[i]);
        }
        return new String(chars);
    }

    private static boolean hasAsciiLetter(String upperCase) {
        for (int i = 0; i < upperCase.length(); i++) {
            final char c = upperCase.charAt(i);
            if (c >= 'A' && c <= 'Z') {
                return true;
            }
        }
        return false;
    }

    /**
     * Equality of the strings after {@link #toAsciiUpperCase(int)}, without
     * copying them.
     */
    static boolean equalsIgnoringAsciiCase(String string1, String string2) {
        final int length = string1.length();
        if (length != string2.length()) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            if (toAsciiUpperCase(string1.charAt(i)) != toAsciiUpperCase(string2.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Longest contents searched by {@link String#indexOf(String, int)}: its
     * worst case, quadratic, stays within a constant factor of a linear
     * search.
     */
    static final int SHORT_CONTENTS = 64;

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
            String string2, SieveContext context) throws SieveException {
        Equals comparatorObj = context.getComparatorManager().getComparator(comparatorName);
        return comparatorObj.equals(string1, string2, context);
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
