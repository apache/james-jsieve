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
package org.apache.jsieve.tests;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.jsieve.Arguments;
import org.apache.jsieve.SieveException;
import org.apache.jsieve.StringListArgument;
import org.apache.jsieve.SyntaxException;
import org.apache.jsieve.TagArgument;
import org.apache.jsieve.comparators.ComparatorNames;
import org.apache.jsieve.comparators.ComparatorUtils;
import org.apache.jsieve.mail.MailAdapter;

/**
 * Class Header implements the Header Test as defined in RFC 3028, section 5.7.
 */
public class Header
    extends AbstractTest
    implements ComparatorTags, MatchTypeTags, ComparatorNames
{

    /**
     * Constructor for Header.
     */
    public Header()
    {
        super();
    }

    /**
     * <p>From RFC 3028, Section 5.7... </p>
     * <code>  
     * Syntax: header [COMPARATOR] [MATCH-TYPE]
     *       &lt;header-names: string-list&gt; &lt;key-list: string-list&gt;
     * </code>
     * <p>Note that the spec. then goes on to give an example where the
     * order of the optional parts is different, so I guess that the order is
     * optional too!</p>
     * 
     * @see org.apache.jsieve.tests.AbstractTest#executeBasic(MailAdapter, Arguments)
     */
    protected boolean executeBasic(MailAdapter mail, Arguments arguments)
        throws SieveException
    {
        String comparator = null;
        String matchType = null;
        List headerNames = null;
        List keys = null;

        ListIterator argumentsIter = arguments.getArgumentList().listIterator();
        boolean stop = false;

        // Tag processing
        while (!stop && argumentsIter.hasNext())
        {
            Object argument = argumentsIter.next();
            if (argument instanceof TagArgument)
            {
                String tag = ((TagArgument) argument).getTag();

                if (null == comparator && tag.equals(COMPARATOR_TAG))
                {
                    // The next argument must be a stringlist
                    if (argumentsIter.hasNext())
                    {
                        argument = argumentsIter.next();
                        if (argument instanceof StringListArgument)
                        {
                            List stringList =
                                ((StringListArgument) argument).getList();
                            if (stringList.size() != 1)
                                throw new SyntaxException("Expecting exactly one String");
                            comparator = (String) stringList.get(0);
                        }
                        else
                            throw new SyntaxException("Expecting a StringList");
                    }
                }
                // [MATCH-TYPE]?
                else if (
                    null == matchType
                        && (tag.equals(IS_TAG)
                            || tag.equals(CONTAINS_TAG)
                            || tag.equals(MATCHES_TAG)))
                    matchType = tag;
                else
                    throw new SyntaxException(
                        "Found unexpected TagArgument: \"" + tag + "\"");
            }
            else
            {
                // Stop when a non-tag argument is encountered
                argumentsIter.previous();
                stop = true;
            }
        }

        // The next argument MUST be a string-list of header names
        if (argumentsIter.hasNext())
        {
            Object argument = argumentsIter.next();
            if (argument instanceof StringListArgument)
                headerNames = ((StringListArgument) argument).getList();
        }
        if (null == headerNames)
            throw new SyntaxException("Expecting a StringListof header names");

        // The next argument MUST be a string-list of keys
        if (argumentsIter.hasNext())
        {
            Object argument = argumentsIter.next();
            if (argument instanceof StringListArgument)
                keys = ((StringListArgument) argument).getList();
        }
        if (null == keys)
            throw new SyntaxException("Expecting a StringList of keys");

        if (argumentsIter.hasNext())
            throw new SyntaxException("Found unexpected arguments");

        return match(
            mail,
            (comparator == null ? ASCII_CASEMAP_COMPARATOR : comparator),
            (matchType == null ? IS_TAG : matchType),
            headerNames,
            keys);
    }

    /**
     * Method match.
     * @param mail
     * @param comparator
     * @param matchType
     * @param headerNames
     * @param keys
     * @return boolean
     * @throws SieveException
     */
    protected boolean match(
        MailAdapter mail,
        String comparator,
        String matchType,
        List headerNames,
        List keys)
        throws SieveException
    {
        // Iterate over the header names looking for a match
        boolean isMatched = false;
        Iterator headerNamesIter = headerNames.iterator();
        while (!isMatched && headerNamesIter.hasNext())
        {
            isMatched =
                match(
                    comparator,
                    matchType,
                    mail.getMatchingHeader((String) headerNamesIter.next()),
                    keys);
        }
        return isMatched;
    }

    /**
     * Method match.
     * @param comparator
     * @param matchType
     * @param headerValues
     * @param keys
     * @return boolean
     * @throws SieveException
     */
    protected boolean match(
        String comparator,
        String matchType,
        List headerValues,
        List keys)
        throws SieveException
    {
        // Special case for empty values
        // If the matchType is :contains
        //     add the headerValue of a null string
        // else 
        //     not matched
        if (headerValues.isEmpty())
            if (matchType.equals(CONTAINS_TAG))
                headerValues.add("");
            else
                return false;
        // Iterate over the header values looking for a match
        boolean isMatched = false;
        Iterator headerValuesIter = headerValues.iterator();
        while (!isMatched && headerValuesIter.hasNext())
        {
            isMatched =
                match(
                    comparator,
                    matchType,
                    (String) headerValuesIter.next(),
                    keys);
        }
        return isMatched;
    }

    /**
     * Method match.
     * @param comparator
     * @param matchType
     * @param headerValue
     * @param keys
     * @return boolean
     * @throws SieveException
     */
    protected boolean match(
        String comparator,
        String matchType,
        String headerValue,
        List keys)
        throws SieveException
    {
        // Iterate over the keys looking for a match
        boolean isMatched = false;
        Iterator keysIter = keys.iterator();
        while (!isMatched && keysIter.hasNext())
        {
            isMatched =
                ComparatorUtils.match(
                    comparator,
                    matchType,
                    headerValue,
                    (String) keysIter.next());
        }
        return isMatched;
    }

    /**
     * @see org.apache.jsieve.tests.AbstractTest#validateArguments(Arguments)
     */
    protected void validateArguments(Arguments arguments) throws SieveException
    {
        if (arguments.hasTests())
            throw new SyntaxException("Found unexpected tests");
    }

}
