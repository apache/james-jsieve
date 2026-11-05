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
import static org.apache.jsieve.tests.AddressPartTags.ALL_TAG;
import static org.apache.jsieve.tests.AddressPartTags.DOMAIN_TAG;
import static org.apache.jsieve.tests.AddressPartTags.LOCALPART_TAG;
import static org.apache.jsieve.tests.ComparatorTags.COMPARATOR_TAG;

import java.util.List;
import java.util.ListIterator;

import org.apache.jsieve.Argument;
import org.apache.jsieve.Arguments;
import org.apache.jsieve.SieveContext;
import org.apache.jsieve.StringListArgument;
import org.apache.jsieve.TagArgument;
import org.apache.jsieve.comparators.ComparatorUtils;
import org.apache.jsieve.exception.SieveException;
import org.apache.jsieve.exception.SyntaxException;
import org.apache.jsieve.mail.MailAdapter;
import org.apache.jsieve.mail.SieveMailException;


public abstract class AbstractComparatorTest extends AbstractTest {

    /** The arguments of an address or envelope test, checked, with their defaults. */
    private static final class ComparatorTestArguments {
        private final String addressPart;
        private final String comparator;
        private final String matchType;
        private final List<String> headerNames;
        private final List<String> keys;

        private ComparatorTestArguments(String addressPart, String comparator, String matchType,
                List<String> headerNames, List<String> keys) {
            this.addressPart = addressPart;
            this.comparator = comparator;
            this.matchType = matchType;
            this.headerNames = headerNames;
            this.keys = keys;
        }
    }

    /**
     * The address that :all compares, written as RFC 5322 section 3.4.1 does:
     * the local part as it is when the address parsers read it without quotes,
     * else as a quoted string (RFC 5322 section 3.2.4), then "@" and the domain
     * when there is one.
     *
     * @param localPart the content of the local part, not null
     * @param domain the domain, empty when there is none, not null
     * @return the address, not null
     */
    protected static String canonicalAddress(String localPart, String domain) {
        final String local = isUnquotedLocalPart(localPart) ? localPart : quoted(localPart);
        return domain.isEmpty() ? local : local + "@" + domain;
    }

    /**
     * Is it a local part the address parsers read without quotes: an atext
     * character, then atext characters or dots? A dot-atom (RFC 5322 section
     * 3.2.3) is one, and so is a local part with consecutive or trailing dots,
     * such as taro..yamada, which some providers give and the parsers accept
     * unquoted.
     */
    private static boolean isUnquotedLocalPart(String text) {
        if (text.isEmpty() || !isAtext(text.charAt(0))) {
            return false;
        }
        for (int i = 1; i < text.length(); i++) {
            if (text.charAt(i) != '.' && !isAtext(text.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /** Is it an atext character (RFC 5322 section 3.2.3), any non-ASCII one included (RFC 6532 section 3.2)? */
    private static boolean isAtext(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c >= 0x80
                || "!#$%&'*+-/=?^_`{|}~".indexOf(c) >= 0;
    }

    /** The text as a quoted string, its quotes and backslashes as quoted pairs. */
    private static String quoted(String text) {
        final StringBuilder result = new StringBuilder(text.length() + 2).append('"');
        for (int i = 0; i < text.length(); i++) {
            final char c = text.charAt(i);
            if (c == '"' || c == '\\') {
                result.append('\\');
            }
            result.append(c);
        }
        return result.append('"').toString();
    }

    /** The arguments validateArguments checked, for executeBasic. */
    private ComparatorTestArguments checkedArguments;

    /** The arguments validateArguments read checkedArguments from. */
    private Arguments checkedFrom;

    public AbstractComparatorTest() {
        super();
    }

    /**
     * @see org.apache.jsieve.tests.AbstractTest#executeBasic(MailAdapter,
     *      Arguments, SieveContext)
     */
    protected boolean executeBasic(MailAdapter mail, Arguments arguments,
            SieveContext context) throws SieveException {
        final ComparatorTestArguments test = arguments == checkedFrom ? checkedArguments : parse(arguments, context);
        return match(mail, test.addressPart, test.comparator, test.matchType, test.headerNames, test.keys,
                context);
    }

    /**
     * <p>
     * From RFC 3028, Section 5.1...
     * </p>
     * <code>  
     * Syntax: address [ADDRESS-PART] [COMPARATOR] [MATCH-TYPE]
     *         &lt;header-list: string-list&gt; &lt;key-list: string-list&gt;
     * </code>
     * <p>
     * Note that the spec. then goes on to give an example where the order of
     * the optional parts is different, so I am assuming that the order of the
     * optional parts is optional too!
     * </p>
     * <p>
     * Checks the arguments and answers them with their defaults.
     * </p>
     */
    private ComparatorTestArguments parse(Arguments arguments, SieveContext context)
            throws SieveException {
        String addressPart = null;
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
                String tag = ((TagArgument) argument).getTag();

                // [ADDRESS-PART]?
                if (null == addressPart
                        && (tag.equals(LOCALPART_TAG) || tag.equals(DOMAIN_TAG) || tag
                                .equals(ALL_TAG)))
                    addressPart = tag;
                // [COMPARATOR]?
                else if (null == comparator && tag.equals(COMPARATOR_TAG)) {
                    // The next argument must be a stringlist
                    if (argumentsIter.hasNext()) {
                        argument = argumentsIter.next();
                        if (argument instanceof StringListArgument) {
                            List stringList = ((StringListArgument) argument)
                                    .getList();
                            if (stringList.size() != 1)
                                throw new SyntaxException(
                                        "Expecting exactly one String");
                            comparator = (String) stringList.get(0);
                        } else
                            throw new SyntaxException("Expecting a StringList");
                    }
                }
                // [MATCH-TYPE]?
                else if (null == matchType
                        && (tag.equals(IS_TAG) || tag.equals(CONTAINS_TAG) || tag
                                .equals(MATCHES_TAG)))
                    matchType = tag;
                else
                    throw context.getCoordinate().syntaxException(
                            "Found unexpected TagArgument");
            } else {
                // Stop when a non-tag argument is encountered
                argumentsIter.previous();
                stop = true;
            }
        }

        // The next argument MUST be a string-list of header names
        if (argumentsIter.hasNext()) {
            Argument argument = argumentsIter.next();
            if (argument instanceof StringListArgument)
                headerNames = ((StringListArgument) argument).getList();
        }
        if (null == headerNames)
            throw context.getCoordinate().syntaxException(
                    "Expecting a StringList of header names");
        validateHeaderNames(headerNames, context);

        // The next argument MUST be a string-list of keys
        if (argumentsIter.hasNext()) {
            Argument argument = argumentsIter.next();
            if (argument instanceof StringListArgument)
                keys = ((StringListArgument) argument).getList();
        }
        if (null == keys)
            throw context.getCoordinate().syntaxException(
                    "Expecting a StringList of keys");

        if (argumentsIter.hasNext())
            throw context.getCoordinate().syntaxException(
                    "Found unexpected arguments");

        final ComparatorTestArguments test = new ComparatorTestArguments((addressPart == null ? ALL_TAG : addressPart),
                (comparator == null ? ASCII_CASEMAP_COMPARATOR : comparator),
                (matchType == null ? IS_TAG : matchType), headerNames, keys);
        ComparatorUtils.checkMatchType(test.comparator, test.matchType, context);
        return test;
    }

    /**
     * Method match.
     * 
     * @param mail
     * @param addressPart
     * @param comparator
     * @param matchType
     * @param headerNames
     * @param keys
     * @param context not null
     * @return boolean
     * @throws SieveMailException
     */
    protected boolean match(MailAdapter mail, String addressPart,
            String comparator, String matchType, List<String> headerNames, List<String> keys,
            SieveContext context) throws SieveException {
        // Iterate over the header names looking for a match
        boolean isMatched = false;
        for (final String headerName: headerNames) {
            isMatched = match(mail, addressPart, comparator, matchType,
                    headerName, keys, context); 
            if (isMatched) {
                break;
            }
        }
        return isMatched;
    }

    /**
     * Method match.
     * 
     * @param mail
     * @param addressPart
     * @param comparator
     * @param matchType
     * @param headerName
     * @param keys
     * @param context not null
     * @return boolean
     * @throws SieveMailException
     */
    protected boolean match(MailAdapter mail, String addressPart,
            String comparator, String matchType, String headerName, List<String> keys,
            SieveContext context) throws SieveException {
        // Iterate over the keys looking for a match
        boolean isMatched = false;
        for (final String key:keys) {
            isMatched = match(mail, addressPart, comparator, matchType,
                    headerName, key, context);
            if (isMatched) {
                break;
            }
        }
        return isMatched;
    }

    /**
     * Method match.
     * 
     * @param mail
     * @param addressPart
     * @param comparator
     * @param matchType
     * @param headerName
     * @param key
     * @param context not null
     * @return boolean
     * @throws SieveMailException
     */
    protected abstract boolean match(MailAdapter mail, String addressPart,
            String comparator, String matchType, String headerName, String key,
            SieveContext context) throws SieveException;

    /**
     * Checks the header names, or the envelope parts, the test compares. Every
     * name is accepted here.
     *
     * @param headerNames not null
     * @param context not null
     * @throws SieveException when a name is not accepted
     */
    protected void validateHeaderNames(List<String> headerNames, SieveContext context)
            throws SieveException {
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
        checkedArguments = parse(arguments, context);
        checkedFrom = arguments;
    }

}
