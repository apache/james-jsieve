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

package org.apache.jsieve.comparator;

import java.util.Random;

import org.apache.jsieve.comparators.ComparatorUtils;
import org.apache.jsieve.exception.SievePatternException;
import org.junit.Assert;
import org.junit.Test;

public class ComparatorUtilsTest {

    @Test
    public void testMatchesStringString() throws SievePatternException {
        String sievematch = "[test] ?\\\\?\\?*\\\\*\\*\\";
        Assert.assertTrue(ComparatorUtils.matches("[test] a\\x?foo\\bar*\\",
                sievematch));
        Assert.assertFalse(ComparatorUtils.matches("[test] ab\\x?foo\\bar*\\",
                sievematch));
        Assert.assertFalse(ComparatorUtils.matches("[test]a\\x?foo\\bar*\\",
                sievematch));
        Assert.assertFalse(ComparatorUtils.matches("[tst] a\\x?foo\\bar*\\",
                sievematch));
        Assert.assertFalse(ComparatorUtils.matches("[test] a\\\\x?foo\\bar*\\",
                sievematch));
        Assert.assertFalse(ComparatorUtils.matches("[test] a\\?foo\\bar*\\",
                sievematch));
        Assert.assertFalse(ComparatorUtils.matches("[test] a\\xafoo\\bar*\\",
                sievematch));
        Assert.assertTrue(ComparatorUtils.matches("[test] a\\x?\\bar*\\", sievematch));
        Assert.assertTrue(ComparatorUtils.matches("[test] a\\x?foo\\\\bar*\\",
                sievematch));
        Assert.assertFalse(ComparatorUtils
                .matches("[test] a\\x?foobar*\\", sievematch));
        Assert.assertFalse(ComparatorUtils.matches("[test] a\\x?foo\\bar.\\",
                sievematch));
        Assert.assertFalse(ComparatorUtils.matches("[test] a\\x?foo\\bar*\\\\",
                sievematch));
        Assert.assertFalse(ComparatorUtils
                .matches("[test] a\\x?foo\\bar*", sievematch));
    }

    /**
     * The ":matches" version specifies a wildcard match using the characters
     * "*" and "?". "*" matches zero or more characters, and "?" matches a
     * single character. "?" and "*" may be escaped as "\\?" and "\\*" in
     * strings to match against themselves. The first backslash escapes the
     * second backslash; together, they escape the "*". This is awkward, but it
     * is commonplace in several programming languages that use globs and
     * regular expressions.
     */
    @Test
    public void testSieveToJavaRegex() {
        String sievematch = "[test] ?\\\\?\\?*\\\\*\\*\\";
        String res = ComparatorUtils.sieveToJavaRegex(sievematch);
        String expected = "\\[test\\] .\\\\.\\?.*\\\\.*\\*\\\\";
        Assert.assertEquals(expected, res);
    }

    @Test(timeout = 2000)
    public void multiStarGlobShouldMatchInLinearishTime() throws Exception {
        StringBuilder target = new StringBuilder();
        for (int i = 0; i < 2000; i++) {
            target.append('a');
        }
        Assert.assertFalse(ComparatorUtils.matches(target.toString(), "*a*a*a*a*a*a*a*a*b"));
    }

    @Test(timeout = 2000)
    public void fuzzedGlobShouldNotBacktrack() throws Exception {
        StringBuilder target = new StringBuilder("2\u00e9^\u00e9");
        for (int i = 0; i < 180; i++) {
            target.append(i % 3 == 0 ? 'e' : 'a');
        }
        Assert.assertFalse(ComparatorUtils.matches(target.toString(),
                "*2\u00e9*^\u00e9*a*a*e*a*a*a*a\u00df*a****\\\\a?*"));
    }

    @Test
    public void wildcardsShouldMatchLineTerminators() throws Exception {
        Assert.assertTrue(ComparatorUtils.matches("foo\r\n bar", "foo*bar"));
        Assert.assertTrue(ComparatorUtils.matches("foo\nbar", "foo?bar"));
    }

    @Test
    public void questionMarkShouldMatchASupplementaryCharacter() throws Exception {
        Assert.assertTrue(ComparatorUtils.matches("a\uD83D\uDE00b", "a?b"));
    }

    @Test
    public void supplementaryCharacterShouldNotBeAWildcard() throws Exception {
        // U+1002A must not be truncated into '*'
        Assert.assertFalse(ComparatorUtils.matches("x", "\uD800\uDC2A"));
        Assert.assertTrue(ComparatorUtils.matches("\uD800\uDC2A", "\\\uD800\uDC2A"));
    }

    @Test
    public void undefinedEscapeShouldStandForTheEscapedCharacter() throws Exception {
        Assert.assertTrue(ComparatorUtils.matches("a", "\\a"));
        Assert.assertFalse(ComparatorUtils.matches("\\a", "\\a"));
        Assert.assertTrue(ComparatorUtils.matches("x.y", "x\\.y"));
    }

    @Test
    public void trailingBackslashShouldStandForItself() throws Exception {
        Assert.assertTrue(ComparatorUtils.matches("foo\\", "foo\\"));
        Assert.assertFalse(ComparatorUtils.matches("foo", "foo\\"));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void regexTranslationShouldHandleUndefinedEscapes() {
        Assert.assertEquals("a\\.b", ComparatorUtils.sieveToJavaRegex("\\a\\.b"));
    }

    @Test
    public void edgeCases() throws Exception {
        assertMatch("", "");
        assertMatch("", "*");
        assertMatch("", "***");
        assertNoMatch("", "?");
        assertNoMatch("", "a");
        assertNoMatch("a", "");
        assertMatch("abc", "***");
        assertMatch("abc", "a*");
        assertMatch("abc", "*c");
        assertMatch("abc", "*b*");
        assertNoMatch("abc", "*b");
        assertNoMatch("abc", "b*");
        assertMatch("abc", "???");
        assertNoMatch("abc", "??");
        assertNoMatch("abc", "????");
        assertMatch("abc", "?*?");
        assertMatch("ab", "?*?");
        assertNoMatch("a", "?*?");
        assertMatch("*", "\\*");
        assertNoMatch("a", "\\*");
        assertMatch("?", "\\?");
        assertNoMatch("a", "\\?");
        assertMatch("a*b", "*\\**");
        assertNoMatch("ab", "*\\**");
        assertMatch("\\", "\\\\");
        assertNoMatch("\\\\", "\\\\");
    }

    @Test
    public void starShouldBacktrackPastAPartialMatch() throws Exception {
        assertMatch("abcabd", "*abd");
        assertMatch("ababab", "*ab*ab");
        assertMatch("aaa", "a*a*a");
        assertNoMatch("aa", "a*a*a");
        assertMatch("mississippi", "*sip*");
        assertMatch("mississippi", "m*iss*ppi");
        assertNoMatch("mississippi", "m*iss*ppix");
        assertMatch("xaaab", "x*a?b");
        assertNoMatch("xaab", "x*a??b");
    }

    @Test
    public void caseShouldBeSignificant() throws Exception {
        assertNoMatch("ABC", "abc");
        assertNoMatch("ABC", "a*");
    }

    @Test(timeout = 5000)
    public void adversarialInputsShouldMatchInBoundedTime() throws Exception {
        String as = repeat("a", 10000);
        // Many stars, the last literal never occurs
        assertNoMatch(as, repeat("*a", 200) + "b");
        // Many stars, and a match only at the very end
        assertMatch(as + "b", repeat("*a", 200) + "*b");
        // Repeated multi-character literal between stars
        assertNoMatch(repeat("ab", 5000), repeat("*ab", 100) + "c");
        // Question marks after a star
        assertNoMatch(as, "*" + repeat("?", 5000) + "b");
        assertMatch(as, "*" + repeat("?", 5000));
        // Long header with a single star
        assertNoMatch(repeat("x", 500000), "*y*");
        // Stars and question marks interleaved
        assertNoMatch(as, repeat("*?", 200) + "b");
    }

    @Test
    public void matchesShouldAgreeWithReferenceImplementationOnRandomGlobs() throws Exception {
        Random random = new Random(42);
        for (int run = 0; run < 50000; run++) {
            String glob = randomString(random, 12);
            String target = randomString(random, 20);
            assertAgreesWithReference(target, glob);
        }
    }

    @Test
    public void matchesShouldAgreeWithReferenceImplementationOnGlobsDerivedFromTarget() throws Exception {
        Random random = new Random(43);
        for (int run = 0; run < 50000; run++) {
            String target = randomString(random, 30);
            // Without mutation, the derived glob must match its target
            String exact = deriveGlob(random, target, false);
            assertMatch(target, exact);
            assertAgreesWithReference(target, exact);
            assertAgreesWithReference(target, deriveGlob(random, target, true));
        }
    }

    private static final String[] ALPHABET = {"a", "b", "A", "*", "?", "\\", ".", "\n", "\r",
            "ß", "😀"};

    private static String randomString(Random random, int maxLength) {
        int length = random.nextInt(maxLength + 1);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(ALPHABET[random.nextInt(ALPHABET.length)]);
        }
        return builder.toString();
    }

    private static String repeat(String s, int count) {
        StringBuilder builder = new StringBuilder(s.length() * count);
        for (int i = 0; i < count; i++) {
            builder.append(s);
        }
        return builder.toString();
    }

    /**
     * Builds a glob matching the given target: each character is either kept
     * (escaped if needed), replaced by a ?, or a run of characters is replaced
     * by a *. When mutating, undefined escapes and a trailing random character
     * may be added, the result being left to the reference implementation to
     * judge.
     */
    private static String deriveGlob(Random random, String target, boolean mutate) {
        int[] codePoints = target.codePoints().toArray();
        StringBuilder glob = new StringBuilder();
        int i = 0;
        while (i < codePoints.length) {
            int choice = random.nextInt(10);
            if (choice < 2) {
                glob.append('*');
                i += random.nextInt(Math.min(4, codePoints.length - i) + 1);
            } else if (choice < 4) {
                glob.append('?');
                i++;
            } else {
                int c = codePoints[i++];
                if (c == '*' || c == '?' || c == '\\' || (mutate && random.nextInt(20) == 0)) {
                    glob.append('\\');
                }
                glob.appendCodePoint(c);
            }
        }
        if (mutate && random.nextInt(4) == 0) {
            glob.append(ALPHABET[random.nextInt(ALPHABET.length)]);
        }
        return glob.toString();
    }

    private static void assertAgreesWithReference(String target, String glob) throws Exception {
        Assert.assertEquals("glob=" + glob + " target=" + target,
                referenceMatches(target, glob),
                ComparatorUtils.matches(target, glob));
    }

    /**
     * Textbook dynamic programming glob matcher, written straight from RFC
     * 5228: escapes are resolved first, then reachable[i][j] tells whether
     * the first i characters of the target match the first j glob tokens.
     */
    private static boolean referenceMatches(String target, String glob) {
        int[] t = target.codePoints().toArray();
        int[] g = glob.codePoints().toArray();
        int[] tokens = new int[g.length];
        boolean[] literal = new boolean[g.length];
        int n = 0;
        for (int i = 0; i < g.length; i++, n++) {
            if (g[i] == '\\' && i + 1 < g.length) {
                tokens[n] = g[++i];
                literal[n] = true;
            } else {
                tokens[n] = g[i];
                literal[n] = g[i] != '*' && g[i] != '?';
            }
        }
        boolean[][] reachable = new boolean[t.length + 1][n + 1];
        reachable[0][0] = true;
        for (int i = 0; i <= t.length; i++) {
            for (int j = 1; j <= n; j++) {
                boolean star = !literal[j - 1] && tokens[j - 1] == '*';
                if (star) {
                    reachable[i][j] = reachable[i][j - 1] || (i > 0 && reachable[i - 1][j]);
                } else if (i > 0) {
                    boolean any = !literal[j - 1] && tokens[j - 1] == '?';
                    reachable[i][j] = reachable[i - 1][j - 1] && (any || tokens[j - 1] == t[i - 1]);
                }
            }
        }
        return reachable[t.length][n];
    }

    private static void assertMatch(String target, String glob) throws Exception {
        Assert.assertTrue("glob=" + glob + " target=" + target, ComparatorUtils.matches(target, glob));
    }

    private static void assertNoMatch(String target, String glob) throws Exception {
        Assert.assertFalse("glob=" + glob + " target=" + target, ComparatorUtils.matches(target, glob));
    }
}
