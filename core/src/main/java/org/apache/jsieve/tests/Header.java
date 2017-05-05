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

import static org.apache.jsieve.comparators.ComparatorNames.ASCII_CASEMAP_COMPARATOR;
import static org.apache.jsieve.comparators.MatchTypeTags.CONTAINS_TAG;
import static org.apache.jsieve.comparators.MatchTypeTags.IS_TAG;
import static org.apache.jsieve.comparators.MatchTypeTags.MATCHES_TAG;
import static org.apache.jsieve.tests.ComparatorTags.COMPARATOR_TAG;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.james.mime4j.codec.DecodeMonitor;
import org.apache.james.mime4j.codec.DecoderUtil;
import org.apache.james.mime4j.util.MimeUtil;
import org.apache.jsieve.Argument;
import org.apache.jsieve.Arguments;
import org.apache.jsieve.SieveContext;
import org.apache.jsieve.StringListArgument;
import org.apache.jsieve.TagArgument;
import org.apache.jsieve.comparators.ComparatorUtils;
import org.apache.jsieve.exception.SieveException;
import org.apache.jsieve.mail.MailAdapter;

/**
 * Class Header implements the Header Test as defined in RFC 3028, section 5.7.
 */
public class Header extends AbstractTest {

    /**
     * Constructor for Header.
     */
    public Header() {
        super();
    }

    /**
     * <p>
     * From RFC 3028, Section 5.7...
     * </p>
     * <code>  
     * Syntax: header [COMPARATOR] [MATCH-TYPE]
     *       &lt;header-names: string-list&gt; &lt;key-list: string-list&gt;
     * </code>
     * <p>
     * Note that the spec. then goes on to give an example where the order of
     * the optional parts is different, so I guess that the order is optional
     * too!
     * </p>
     * 
     * @see org.apache.jsieve.tests.AbstractTest#executeBasic(MailAdapter,
     *      Arguments, SieveContext)
     */
    protected boolean executeBasic(MailAdapter mail, Arguments arguments,
            SieveContext context) throws SieveException {
        String comparator = null;
        String matchType = null;
        List<String> headerNames = null;
        List<String> keys = null;

        ListIterator<Argument> argumentsIter = arguments.getArgumentList().listIterator();
        boolean stop = false;

        // Tag processing
        while (!stop && argumentsIter.hasNext()) {
            Argument argument = argumentsIter.next();
            if (argument instanceof TagArgument) {
                final String tag = ((TagArgument) argument).getTag();

                if (null == comparator && tag.equals(COMPARATOR_TAG)) {
                    // The next argument must be a stringlist
                    if (argumentsIter.hasNext()) {
                        argument = argumentsIter.next();
                        if (argument instanceof StringListArgument) {
                            List<String> stringList = ((StringListArgument) argument)
                                    .getList();
                            if (stringList.size() != 1)
                                throw context.getCoordinate().syntaxException(
                                        "Expecting exactly one String");
                            comparator = stringList.get(0);
                        } else
                            throw context.getCoordinate().syntaxException(
                                    "Expecting a StringList");
                    }
                }
                // [MATCH-TYPE]?
                else if (null == matchType
                        && (tag.equals(IS_TAG) || tag.equals(CONTAINS_TAG) || tag
                                .equals(MATCHES_TAG)))
                    matchType = tag;
                else
                    throw context.getCoordinate().syntaxException(
                            "Found unexpected TagArgument: \"" + tag + "\"");
            } else {
                // Stop when a non-tag argument is encountered
                argumentsIter.previous();
                stop = true;
            }
        }

        // The next argument MUST be a string-list of header names
        if (argumentsIter.hasNext()) {
            final Argument argument = argumentsIter.next();
            if (argument instanceof StringListArgument)
                headerNames = ((StringListArgument) argument).getList();
        }
        if (null == headerNames)
            throw context.getCoordinate().syntaxException(
                    "Expecting a StringListof header names");

        // The next argument MUST be a string-list of keys
        if (argumentsIter.hasNext()) {
            final Argument argument = argumentsIter.next();
            if (argument instanceof StringListArgument)
                keys = ((StringListArgument) argument).getList();
        }
        if (null == keys)
            throw context.getCoordinate().syntaxException(
                    "Expecting a StringList of keys");

        if (argumentsIter.hasNext())
            throw context.getCoordinate().syntaxException(
                    "Found unexpected arguments");

        return match(mail, (comparator == null ? ASCII_CASEMAP_COMPARATOR
                : comparator), (matchType == null ? IS_TAG : matchType),
                headerNames, keys, context);
    }

    /**
     * Method match.
     * 
     * @param mail
     * @param comparator
     * @param matchType
     * @param headerNames
     * @param keys
     * @param context not null
     * @return boolean
     * @throws SieveException
     */
    protected boolean match(MailAdapter mail, String comparator,
            String matchType, List<String> headerNames, List<String> keys, SieveContext context)
            throws SieveException {
        // Iterate over the header names looking for a match
        boolean isMatched = false;
        Iterator headerNamesIter = headerNames.iterator();
        while (!isMatched && headerNamesIter.hasNext()) {
            isMatched = match(comparator, matchType, mail
                    .getMatchingHeader((String) headerNamesIter.next()), keys,
                    context);
        }
        return isMatched;
    }

    /**
     * Method match.
     * 
     * @param comparator
     * @param matchType
     * @param headerValues
     * @param keys
     * @param context not null
     * @return boolean
     * @throws SieveException
     */
    protected boolean match(String comparator, String matchType,
            List<String> headerValues, List<String> keys, SieveContext context)
            throws SieveException {
        // Special case for empty values
        // If the matchType is :contains
        // add the headerValue of a null string
        // else
        // not matched
        if (headerValues.isEmpty())
            if (matchType.equals(CONTAINS_TAG)) {
                // header values may be immutable
                headerValues = new ArrayList<String>(headerValues);
                headerValues.add("");
            } else {
                return false;
            }
        // Iterate over the header values looking for a match
        boolean isMatched = false;
        Iterator<String> headerValuesIter = headerValues.iterator();
        while (!isMatched && headerValuesIter.hasNext()) {
            String headerValue = MimeUtil.unscrambleHeaderValue(headerValuesIter.next());
            isMatched = match(comparator, matchType, headerValue, keys, context);
        }
        return isMatched;
    }

    /**
     * Method match.
     * 
     * @param comparator
     * @param matchType
     * @param headerValue
     * @param keys
     * @param context not null
     * @return boolean
     * @throws SieveException
     */
    protected boolean match(String comparator, String matchType,
            String headerValue, List<String> keys, SieveContext context)
            throws SieveException {
        // Iterate over the keys looking for a match
        boolean isMatched = false;
        for (final String key: keys) {
            isMatched = ComparatorUtils.match(comparator, matchType,
                    headerValue, key, context);
            if (isMatched) {
                break;
            }
        }
        return isMatched;
    }

    /**
     * @see org.apache.jsieve.tests.AbstractTest#validateArguments(Arguments,
     *      SieveContext)
     */
    protected void validateArguments(Arguments arguments, SieveContext context)
            throws SieveException {
        if (arguments.hasTests())
            throw context.getCoordinate().syntaxException(
                    "Found unexpected tests");
    }

}
