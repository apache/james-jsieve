/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2004 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache", "Jakarta", "JAMES", "JSieve" and 
 *    "Apache Software Foundation" must not be used to endorse or promote
 *    products derived from this software without prior written permission.
 *    For written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * Portions of this software are based upon public domain software
 * originally written at the National Center for Supercomputing Applications,
 * University of Illinois, Urbana-Champaign.
 */
package org.apache.jsieve.comparators;

import org.apache.oro.text.GlobCompiler;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Matcher;

import org.apache.jsieve.*;
import org.apache.jsieve.LookupException;
import org.apache.jsieve.SieveException;
import org.apache.jsieve.SievePatternException;
import org.apache.jsieve.tests.MatchTypeTags;

/**
 * Class ComparatorUtils implements utility methods used by Comparators.
 */
public class ComparatorUtils implements MatchTypeTags
{
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
    private ComparatorUtils()
    {
        super();
    }
    
    /**
     * Method <code>match</code> answers a boolean indicating if the parameter 
     * <code>matchTarget</code> compares to parameter <code>matchArgument</code> 
     * is a match of <code>matchType</code> using the comparator 
     * <code>comparatorName</code>.
     * 
     * @param comparatorName
     * @param matchType
     * @param matchArgument
     * @param matchTarget
     * @return boolean
     */
    public static boolean match(
        String comparatorName,
        String matchType,
        String matchTarget,
        String matchArgument) throws SieveException
    {
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
     * <p>Method <code>matches</code> answers a boolean indicating if the parameter 
     * <code>string</code> matches the glob pattern described by parameter 
     * <code>glob</code>.
     * @param string
     * @param glob
     * @return boolean
     * @throws SievePatternException
     */
    static public boolean matches(String string, String glob)
        throws SievePatternException
    {
        // This requires optimization
        // 1) DONE - Keep one compiler and one matcher in thread variables
        // 2) Is there a way to re-use the compiled pattern?
        Pattern pattern = null;
        try
        {
            pattern = getGlobCompiler().compile(glob);
        }
        catch (MalformedPatternException e)
        {
            throw new SievePatternException(e.getMessage());
        }
        PatternMatcher matcher = getPerl5Matcher();
        return matcher.matches(string, pattern);
    }
    
    /**
     * <p>Method <code>contains</code> answers a boolean indicating if the parameter 
     * <code>container</code> contains the parameter <code>contents</code>.
     * </p>
     * 
     * @param container
     * @param contents
     * @return boolean
     */
    static public boolean contains(String container, String contents)
    {
        return container.indexOf(contents) > -1;     
    }
    
    /**
     * <p>Method <code>equals</code> answers a boolean indicating if the parameter 
     * <code>string1</code> is equal to the parameter <code>string2</code>.
     * </p>
     * 
     * @param string1
     * @param string2
     * @return boolean
     */
    static public boolean equals(String string1, String string2)
    {
        return string1.equals(string2);     
    }            

    /**
     * <p>Returns the globCompiler, intialises if required.</p>
     * 
     * <p>Note that this must be synchronized to prevent another thread 
     * detecting the null state while this thread is initialising.</p>
     * 
     * @return GlobCompiler
     */
    static synchronized protected GlobCompiler getGlobCompiler()
    {
        GlobCompiler globCompiler = null;
        if (null == (globCompiler = getGlobCompilerBasic()))
        {
            updateGlobCompiler();
            return getGlobCompiler();
        }
        return globCompiler;
    }

    /**
     * Returns the globCompiler.
     * @return GlobCompiler
     */
    private static GlobCompiler getGlobCompilerBasic()
    {
        if (null == fieldGlobCompiler)
            return null;
        return (GlobCompiler)fieldGlobCompiler.get();
    }
    
    /**
     * Updates the current GlobCompiler.
     */
    static protected void updateGlobCompiler()
    {
        setGlobCompiler(computeGlobCompiler());
    }
    
    /**
     * Answers a new GlobCompiler.
     * @return GlobCompiler
     */
    static protected GlobCompiler computeGlobCompiler()
    {
        return new GlobCompiler();
    }        

    /**
     * <p>Returns the perl5Matcher, intialises if required.</p>
     * 
     * <p>Note that this must be synchronized to prevent another thread 
     * detecting the null state while this thread is initialising.</p>
     * 
     * @return Perl5Matcher
     */
    static synchronized protected Perl5Matcher getPerl5Matcher()
    {
        Perl5Matcher perl5Matcher = null;
        if (null == (perl5Matcher = getPerl5MatcherBasic()))
        {
            updatePerl5Matcher();
            return getPerl5Matcher();
        }
        return perl5Matcher;
    }
    
    /**
     * Returns the perl5Matcher.
     * @return Perl5Matcher
     */    
    private static Perl5Matcher getPerl5MatcherBasic()
    {
        if (null == fieldPerl5Matcher)
            return null;
        return (Perl5Matcher)fieldPerl5Matcher.get();
    }
    
    /**
     * Updates the current Perl5Matcher.
     */
    static protected void updatePerl5Matcher()
    {
        setPerl5Matcher(computePerl5Matcher());
    }        

    /**
     * Sets the globCompiler.
     * @param globCompiler The globCompiler to set
     */
    protected static void setGlobCompiler(GlobCompiler globCompiler)
    {
        if (null == fieldGlobCompiler)
            fieldGlobCompiler = new ThreadLocal();
        fieldGlobCompiler.set(globCompiler);
    }

    /**
     * Sets the perl5Matcher.
     * @param perl5Matcher The perl5Matcher to set
     */
    protected static void setPerl5Matcher(Perl5Matcher perl5Matcher)
    {
        if (null == fieldPerl5Matcher)
            fieldPerl5Matcher = new ThreadLocal();
        fieldPerl5Matcher.set(perl5Matcher);
    }
    
    /**
     * Answers a new perl5Matcher.
     * @return Perl5Matcher
     */
    static protected Perl5Matcher computePerl5Matcher()
    {
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
    public static boolean contains(
        String comparatorName,
        String container,
        String contents) throws LookupException
    {
        Contains comparatorObj =
            ComparatorManager.getInstance().newInstance(comparatorName);
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
    public static boolean is(
        String comparatorName,
        String string1,
        String string2) throws LookupException
    {
        Equals comparatorObj =
            ComparatorManager.getInstance().newInstance(comparatorName);
        return comparatorObj.equals(string1, string2);
    }








    /**
     * Method <code>matches</code> answers a boolean indicating if the parameter 
     * <code>string/code> is matched by the patterm <code>glob</code> using an
     * instance of <code>comparatorName</code>.
     * @param comparatorName
     * @param string
     * @param glob
     * @return boolean
     */
    public static boolean matches(
        String comparatorName,
        String string,
        String glob) throws SieveException
    {
        Matches comparatorObj =
            ComparatorManager.getInstance().newInstance(comparatorName);
        return comparatorObj.matches(string, glob);        
    }



        

}
