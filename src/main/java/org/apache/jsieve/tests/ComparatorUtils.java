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

package org.apache.jsieve.tests;

import org.apache.oro.text.GlobCompiler;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Matcher;

import org.apache.jsieve.*;
import org.apache.jsieve.comparators.Contains;
import org.apache.jsieve.comparators.Equals;
import org.apache.jsieve.comparators.MatchTypeTags;
import org.apache.jsieve.comparators.Matches;
import org.apache.jsieve.exception.LookupException;
import org.apache.jsieve.exception.SieveException;
import org.apache.jsieve.exception.SievePatternException;

/**
 * Class ComparatorUtils implements utility methods used by Comparators.
 */
public class ComparatorUtils implements MatchTypeTags {
    /**
     * One per thread GlobCompiler.
     */
    static private ThreadLocal fieldGlobCompiler;

    /**
     * One per thread Perl5Matcher.
     */
    static private ThreadLocal fieldPerl5Matcher;

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
     * @param comparatorName
     * @param matchType
     * @param matchArgument
     * @param matchTarget
     * @return boolean
     */
    public static boolean match(String comparatorName, String matchType,
            String matchTarget, String matchArgument) throws SieveException {
        boolean isMatched = false;
        if (matchType.equals(IS_TAG))
            isMatched = is(comparatorName, matchTarget, matchArgument);
        else if (matchType.equals(CONTAINS_TAG))
            isMatched = contains(comparatorName, matchTarget, matchArgument);
        else if (matchType.equals(MATCHES_TAG))
            isMatched = matches(comparatorName, matchTarget, matchArgument);
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
        // This requires optimization
        // 1) DONE - Keep one compiler and one matcher in thread variables
        // 2) Is there a way to re-use the compiled pattern?
        Pattern pattern = null;
        try {
            pattern = getGlobCompiler().compile(glob);
        } catch (MalformedPatternException e) {
            throw new SievePatternException(e.getMessage());
        }
        PatternMatcher matcher = getPerl5Matcher();
        return matcher.matches(string, pattern);
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
     * <p>
     * Returns the globCompiler, intialises if required.
     * </p>
     * 
     * <p>
     * Note that this must be synchronized to prevent another thread detecting
     * the null state while this thread is initialising.
     * </p>
     * 
     * @return GlobCompiler
     */
    static synchronized protected GlobCompiler getGlobCompiler() {
        GlobCompiler globCompiler = null;
        if (null == (globCompiler = getGlobCompilerBasic())) {
            updateGlobCompiler();
            return getGlobCompiler();
        }
        return globCompiler;
    }

    /**
     * Returns the globCompiler.
     * 
     * @return GlobCompiler
     */
    private static GlobCompiler getGlobCompilerBasic() {
        if (null == fieldGlobCompiler)
            return null;
        return (GlobCompiler) fieldGlobCompiler.get();
    }

    /**
     * Updates the current GlobCompiler.
     */
    static protected void updateGlobCompiler() {
        setGlobCompiler(computeGlobCompiler());
    }

    /**
     * Answers a new GlobCompiler.
     * 
     * @return GlobCompiler
     */
    static protected GlobCompiler computeGlobCompiler() {
        return new GlobCompiler();
    }

    /**
     * <p>
     * Returns the perl5Matcher, intialises if required.
     * </p>
     * 
     * <p>
     * Note that this must be synchronized to prevent another thread detecting
     * the null state while this thread is initialising.
     * </p>
     * 
     * @return Perl5Matcher
     */
    static synchronized protected Perl5Matcher getPerl5Matcher() {
        Perl5Matcher perl5Matcher = null;
        if (null == (perl5Matcher = getPerl5MatcherBasic())) {
            updatePerl5Matcher();
            return getPerl5Matcher();
        }
        return perl5Matcher;
    }

    /**
     * Returns the perl5Matcher.
     * 
     * @return Perl5Matcher
     */
    private static Perl5Matcher getPerl5MatcherBasic() {
        if (null == fieldPerl5Matcher)
            return null;
        return (Perl5Matcher) fieldPerl5Matcher.get();
    }

    /**
     * Updates the current Perl5Matcher.
     */
    static protected void updatePerl5Matcher() {
        setPerl5Matcher(computePerl5Matcher());
    }

    /**
     * Sets the globCompiler.
     * 
     * @param globCompiler
     *                The globCompiler to set
     */
    protected static void setGlobCompiler(GlobCompiler globCompiler) {
        if (null == fieldGlobCompiler)
            fieldGlobCompiler = new ThreadLocal();
        fieldGlobCompiler.set(globCompiler);
    }

    /**
     * Sets the perl5Matcher.
     * 
     * @param perl5Matcher
     *                The perl5Matcher to set
     */
    protected static void setPerl5Matcher(Perl5Matcher perl5Matcher) {
        if (null == fieldPerl5Matcher)
            fieldPerl5Matcher = new ThreadLocal();
        fieldPerl5Matcher.set(perl5Matcher);
    }

    /**
     * Answers a new perl5Matcher.
     * 
     * @return Perl5Matcher
     */
    static protected Perl5Matcher computePerl5Matcher() {
        return new Perl5Matcher();
    }

    /**
     * Method <code>contains<code> answers a boolean indicating if the parameter 
     * <code>container</code> contains the parameter <code>contents</code> using an
     * instance of <code>comparatorName</code>.
     * @param comparatorName
     * @param container
     * @param contents
     * @return boolean
     */
    public static boolean contains(String comparatorName, String container,
            String contents) throws LookupException {
        Contains comparatorObj = ComparatorManager.getInstance().newInstance(
                comparatorName);
        return comparatorObj.contains(container, contents);
    }

    /**
     * Method <code>is<code> answers a boolean indicating if the parameter 
     * <code>container</code> is equal to the parameter <code>contents</code> using 
     * an instance of <code>comparatorName</code>.
     * @param comparatorName
     * @param string1
     * @param string2
     * @return boolean
     */
    public static boolean is(String comparatorName, String string1,
            String string2) throws LookupException {
        Equals comparatorObj = ComparatorManager.getInstance().newInstance(
                comparatorName);
        return comparatorObj.equals(string1, string2);
    }

    /**
     * Method <code>matches</code> answers a boolean indicating if the
     * parameter
     * <code>string/code> is matched by the patterm <code>glob</code> using an
     * instance of <code>comparatorName</code>.
     * @param comparatorName
     * @param string
     * @param glob
     * @return boolean
     */
    public static boolean matches(String comparatorName, String string,
            String glob) throws SieveException {
        Matches comparatorObj = ComparatorManager.getInstance().newInstance(
                comparatorName);
        return comparatorObj.matches(string, glob);
    }

}
