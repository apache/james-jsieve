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

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.jsieve.Arguments;
import org.apache.jsieve.ComparatorManager;
import org.apache.jsieve.InternetAddressException;
import org.apache.jsieve.LookupException;
import org.apache.jsieve.SieveException;
import org.apache.jsieve.StringListArgument;
import org.apache.jsieve.SyntaxException;
import org.apache.jsieve.TagArgument;
import org.apache.jsieve.comparators.ComparatorNames;
import org.apache.jsieve.comparators.ComparatorUtils;
import org.apache.jsieve.comparators.Contains;
import org.apache.jsieve.comparators.Equals;
import org.apache.jsieve.comparators.Matches;
import org.apache.jsieve.mail.MailAdapter;
import org.apache.jsieve.mail.SieveMailException;

/**
 * Class Address implements the Addresss Test as defined in RFC 3028, section 5.1.
 */
public class Address
    extends AbstractTest
    implements AddressPartTags, ComparatorTags, MatchTypeTags, ComparatorNames
{
    /**
     * Constructor for Address.
     */
    public Address()
    {
        super();
    }

    /**
     * <p>From RFC 3028, Section 5.1... </p>
     * <code>  
     * Syntax: address [ADDRESS-PART] [COMPARATOR] [MATCH-TYPE]
     *         &lt;header-list: string-list&gt; &lt;key-list: string-list&gt;
     * </code>
     * <p>Note that the spec. then goes on to give an example where the
     * order of the optional parts is different, so I am assuming that the order of
     * the optional parts is optional too!</p>
     * 
     * @see org.apache.jsieve.tests.AbstractTest#executeBasic(MailAdapter, Arguments)
     */
    protected boolean executeBasic(MailAdapter mail, Arguments arguments)
        throws SieveException
    {
        String addressPart = null;
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

                // [ADDRESS-PART]?
                if (null == addressPart
                    && (tag.equals(LOCALPART_TAG)
                        || tag.equals(DOMAIN_TAG)
                        || tag.equals(ALL_TAG)))
                    addressPart = tag;
                // [COMPARATOR]?    
                else if (null == comparator && tag.equals(COMPARATOR_TAG))
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
                    throw new SyntaxException("Found unexpected TagArgument");
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
            throw new SyntaxException("Expecting a StringList of header names");

        // The next argument MUST be a string-list of keys
        if (argumentsIter.hasNext())
        {
            Object argument = argumentsIter.next();
            if (argument instanceof StringListArgument)
                keys = ((StringListArgument) argument).getList();
        }
        else if (null == keys)
            throw new SyntaxException("Expecting a StringList of keys");

        if (argumentsIter.hasNext())
            throw new SyntaxException("Found unexpected arguments");

        return match(
            mail,
            (addressPart == null ? ALL_TAG : addressPart),
            (comparator == null ? ASCII_CASEMAP_COMPARATOR : comparator),
            (matchType == null ? IS_TAG : matchType),
            headerNames,
            keys);
    }

    /**
     * Method match.
     * @param mail
     * @param addressPart
     * @param comparator
     * @param matchType
     * @param headerNames
     * @param keys
     * @return boolean
     * @throws SieveMailException
     */
    protected boolean match(
        MailAdapter mail,
        String addressPart,
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
                    mail,
                    addressPart,
                    comparator,
                    matchType,
                    (String) headerNamesIter.next(),
                    keys);
        }
        return isMatched;
    }

    /**
     * Method match.
     * @param mail
     * @param addressPart
     * @param comparator
     * @param matchType
     * @param headerNames
     * @param string
     * @return boolean
     * @throws SieveMailException
     */
    protected boolean match(
        MailAdapter mail,
        String addressPart,
        String comparator,
        String matchType,
        String headerName,
        List keys)
        throws SieveException
    {
        // Iterate over the keys looking for a match
        boolean isMatched = false;
        Iterator keysIter = keys.iterator();
        while (!isMatched && keysIter.hasNext())
        {
            isMatched =
                match(
                    mail,
                    addressPart,
                    comparator,
                    matchType,
                    headerName,
                    (String) keysIter.next());
        }
        return isMatched;
    }

    /**
     * Method match.
     * @param mail
     * @param addressPart
     * @param comparator
     * @param matchType
     * @param headerName
     * @param key
     * @return boolean
     * @throws SieveMailException
     */
    protected boolean match(
        MailAdapter mail,
        String addressPart,
        String comparator,
        String matchType,
        String headerName,
        String key)
        throws SieveException
    {
        Iterator headerValuesIter =
            getMatchingValues(mail, headerName).iterator();
        boolean isMatched = false;
        while (!isMatched && headerValuesIter.hasNext())
        {
            isMatched =
                match(
                    addressPart,
                    comparator,
                    matchType,
                    ((String) headerValuesIter.next()),
                    key);
        }
        return isMatched;
    }

    /**
     * Method getMatchingValues.
     * @param mail
     * @param valueName
     * @return List
     * @throws SieveMailException
     */
    protected List getMatchingValues(MailAdapter mail, String valueName)
        throws SieveMailException
    {
        return mail.getMatchingHeader(valueName);
    }

    /**
     * Method match.
     * @param addressPart
     * @param comparator
     * @param matchType
     * @param headerValue
     * @param key
     * @return boolean
     * @throws SieveMailException
     */
    protected boolean match(
        String addressPart,
        String comparator,
        String matchType,
        String headerValue,
        String key)
        throws SieveException
    {
        // Attempt to create a new InternetAddress object from the headerValue
        // If this fails, the header either is not intended to contain a valid
        // Internet Address or is corrupt. Either way, its an Exception!
        String address = null;
        try
        {
            // address is a simple address; user@domain or user
            address = new InternetAddress(headerValue).getAddress();
        }
        catch (AddressException e)
        {
            throw new InternetAddressException(e.getMessage());
        }

        // Extract the part of the address we are matching on       
        String matchAddress = null;
        if (addressPart.equals(":all"))
            matchAddress = address;
        else
        {
            int localStart = 0;
            int localEnd = 0;
            int domainStart = 0;
            int domainEnd = address.length();
            int splitIndex = address.indexOf('@');
            // If there is no domain part (-1), treat it as an empty String
            if (splitIndex == -1)
            {
                localEnd = domainEnd;
                domainStart = domainEnd;
            }
            else
            {
                localEnd = splitIndex;
                domainStart = splitIndex + 1;
            }
            matchAddress =
                (addressPart.equals(LOCALPART_TAG)
                    ? address.substring(localStart, localEnd)
                    : address.substring(domainStart, domainEnd));
        }

        // domain matches MUST ignore case, others should not
        String matchKey = null;
        if (addressPart.equals(DOMAIN_TAG))
        {
            matchKey = key.toLowerCase();
            matchAddress = matchAddress.toLowerCase();
        }
        else
            matchKey = key;

        // Match using the specified comparator          
        return ComparatorUtils.match(
            comparator,
            matchType,
            matchAddress,
            matchKey);
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
