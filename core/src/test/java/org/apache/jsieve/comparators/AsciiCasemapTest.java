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
}
