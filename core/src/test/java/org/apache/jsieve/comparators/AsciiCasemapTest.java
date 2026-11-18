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

import java.util.Locale;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

/**
 * i;ascii-casemap only folds a-z onto A-Z, other characters being compared
 * as is (RFC 4790 section 9.2).
 */
public class AsciiCasemapTest {

    private final AsciiCasemap subject = new AsciiCasemap();

    @Test
    public void matchesShouldIgnoreAsciiCase() throws Exception {
        Assert.assertTrue(subject.matches("Hello World", "*hello w*"));
        Assert.assertTrue(subject.matches("hello world", "HELLO?WORLD"));
        Assert.assertFalse(subject.matches("hello world", "HELLO?WORLDS"));
    }

    @Test(timeout = 2000)
    public void multiStarGlobShouldMatchInBoundedTime() throws Exception {
        StringBuilder target = new StringBuilder();
        for (int i = 0; i < 2000; i++) {
            target.append(i % 2 == 0 ? 'a' : 'A');
        }
        Assert.assertFalse(subject.matches(target.toString(), "*a*a*a*a*a*a*a*a*b"));
    }

    @Test
    public void questionMarkShouldMatchSharpS() throws Exception {
        // "ß".toUpperCase() is "SS": two characters
        Assert.assertTrue(subject.matches("ß", "?"));
        Assert.assertFalse(subject.matches("ß", "??"));
    }

    @Test
    public void sharpSShouldNotEqualSS() throws Exception {
        Assert.assertFalse(subject.matches("SS", "ß"));
        Assert.assertFalse(subject.equals("SS", "ß"));
        Assert.assertFalse(subject.contains("SS", "ß"));
    }

    @Test
    public void nonAsciiLettersShouldNotBeFolded() throws Exception {
        Assert.assertFalse(subject.matches("é", "É"));
        Assert.assertFalse(subject.equals("é", "É"));
        Assert.assertFalse(subject.contains("été", "É"));
    }

    @Test
    public void foldingShouldNotDependOnTheDefaultLocale() throws Exception {
        Locale defaultLocale = Locale.getDefault();
        Locale.setDefault(new Locale("tr", "TR"));
        try {
            // In Turkish, "i".toUpperCase() is a dotted capital I
            Assert.assertTrue(subject.matches("title", "TITLE"));
            Assert.assertTrue(subject.equals("title", "TITLE"));
            Assert.assertTrue(subject.contains("title", "IT"));
        } finally {
            Locale.setDefault(defaultLocale);
        }
    }

    @Test
    public void unicodeCaseFoldingsShouldNotApply() throws Exception {
        // equalsIgnoreCase or regionMatches would fold these
        Assert.assertFalse(subject.equals("\u017F", "S"));
        Assert.assertFalse(subject.equals("\u212A", "k"));
        Assert.assertFalse(subject.contains("x\u212Ax", "K"));
        Assert.assertFalse(subject.contains("x\u017Fx" + "y".repeat(70), "S" + "y".repeat(70)));
        Assert.assertFalse(subject.matches("\u212A", "k"));
    }

    @Test
    public void longContentsShouldIgnoreAsciiCase() throws Exception {
        String key = "Lorem Ipsum ".repeat(10);

        Assert.assertTrue(subject.contains("xx" + key.toUpperCase(Locale.ROOT) + "yy", key));
        Assert.assertTrue(subject.contains("xx" + key.toLowerCase(Locale.ROOT) + "yy", key.toUpperCase(Locale.ROOT)));
        Assert.assertFalse(subject.contains("xx" + key.substring(1) + "yy", key));
    }

    @Test(timeout = 3000)
    public void shortContentsShouldBeSearchedInLinearTime() throws Exception {
        // A naive search compares 64 characters at each position: ~7 seconds
        String container = "a".repeat(10_000_000);
        String key = "A".repeat(63) + "B";

        for (int i = 0; i < 10; i++) {
            Assert.assertFalse(subject.contains(container, key));
        }
    }

    @Test
    public void contentsWithoutLetterShouldBeFound() throws Exception {
        Assert.assertTrue(subject.contains("Price: 12.50 EUR", "12.50"));
        Assert.assertFalse(subject.contains("Price: 12.50 EUR", "12,50"));
    }

    @Test
    public void comparisonsShouldAgreeWithUpperCasedCopiesOnRandomStrings() throws Exception {
        Random random = new Random(42);
        for (int i = 0; i < 50_000; i++) {
            String first = randomString(random, random.nextInt(12));
            String second = randomString(random, random.nextInt(5));
            assertAgreesWithUpperCasedCopies(first, second);
        }
        for (int i = 0; i < 2_000; i++) {
            // Beyond 64 characters, contents is no longer searched by the short path
            String second = randomString(random, 65 + random.nextInt(5));
            String first = randomString(random, random.nextInt(8)) + flipCase(random, second)
                + randomString(random, random.nextInt(8));
            assertAgreesWithUpperCasedCopies(first, second);
            assertAgreesWithUpperCasedCopies(first.substring(1), second);
        }
    }

    private void assertAgreesWithUpperCasedCopies(String first, String second) throws Exception {
        String upperFirst = upperCaseCopy(first);
        String upperSecond = upperCaseCopy(second);
        String message = "first=" + first + " second=" + second;
        Assert.assertEquals(message, ComparatorUtils.contains(upperFirst, upperSecond), subject.contains(first, second));
        Assert.assertEquals(message, ComparatorUtils.matches(upperFirst, upperSecond), subject.matches(first, second));
        Assert.assertEquals(message, upperFirst.equals(upperSecond), subject.equals(first, second));
        Assert.assertEquals(message, upperFirst.equals(upperSecond), subject.equals(first, flipCase(new Random(0), second)));
    }

    /**
     * The definition of i;ascii-casemap, as implemented before the
     * comparisons folded the case in their loops.
     */
    private static String upperCaseCopy(String string) {
        char[] chars = string.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] >= 'a' && chars[i] <= 'z') {
                chars[i] = (char) (chars[i] - ('a' - 'A'));
            }
        }
        return new String(chars);
    }

    private static final String[] ALPHABET = {"a", "A", "b", "B", "z", "Z", "*", "?", "\\", "@", "[", "`", "{",
        "\u00E9", "\u00C9", "\u00DF", "\u017F", "\u212A", "\uD83D\uDE00", "\uD83D", "\uDE00"};

    private static String randomString(Random random, int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(ALPHABET[random.nextInt(ALPHABET.length)]);
        }
        return builder.toString();
    }

    private static String flipCase(Random random, String string) {
        char[] chars = string.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (random.nextBoolean() && chars[i] < 128 && Character.isLetter(chars[i])) {
                chars[i] = Character.isUpperCase(chars[i]) ? Character.toLowerCase(chars[i]) : Character.toUpperCase(chars[i]);
            }
        }
        return new String(chars);
    }
}
