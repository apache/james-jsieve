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
}
