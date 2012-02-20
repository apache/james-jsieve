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

import java.util.ListIterator;

import org.apache.jsieve.Argument;
import org.apache.jsieve.Arguments;
import org.apache.jsieve.NumberArgument;
import org.apache.jsieve.SieveContext;
import org.apache.jsieve.TagArgument;
import org.apache.jsieve.exception.SieveException;
import org.apache.jsieve.exception.SyntaxException;
import org.apache.jsieve.mail.MailAdapter;
import org.apache.jsieve.mail.SieveMailException;

/**
 * Class Size implements the Size Test as defined in RFC 3028, section 5.9.
 */
public class Size extends AbstractTest {

    /**
     * Constructor for Size.
     */
    public Size() {
        super();
    }

    /**
     * @see org.apache.jsieve.tests.AbstractTest#executeBasic(MailAdapter,
     *      Arguments, SieveContext)
     *      <p>
     *      From RFC 3028, Section 5.9...
     *      </p>
     *      <code>  
     *    Syntax: size &lt;&quote;:over"&quote; / &quote;:under&quote;&gt; &lt;limit: number&gt;
     * </code>
     */
    protected boolean executeBasic(MailAdapter mail, Arguments arguments,
            SieveContext context) throws SyntaxException, SieveMailException {
        String comparator = null;
        Integer size = null;
        ListIterator<Argument> argumentsIter = arguments.getArgumentList().listIterator();

        // First argument MUST be a tag of ":under" or ":over"
        if (argumentsIter.hasNext()) {
            Argument argument = argumentsIter.next();
            if (argument instanceof TagArgument) {
                final String tag = ((TagArgument) argument).getTag();
                if (tag.equals(":under") || tag.equals(":over"))
                    comparator = tag;
                else
                    throw context.getCoordinate().syntaxException(
                            new StringBuilder("Found unexpected TagArgument: \"").append(tag).append("\""));
            }
        }
        if (null == comparator)
            throw context.getCoordinate().syntaxException("Expecting a Tag");

        // Second argument MUST be a number
        if (argumentsIter.hasNext()) {
            final Argument argument = argumentsIter.next();
            if (argument instanceof NumberArgument)
                size = ((NumberArgument) argument).getInteger();
        }
        if (null == size)
            throw context.getCoordinate().syntaxException("Expecting a Number");

        // There MUST NOT be any further arguments
        if (argumentsIter.hasNext())
            throw context.getCoordinate().syntaxException(
                    "Found unexpected argument(s)");

        return test(mail, comparator, size.intValue());
    }

    /**
     * Method test.
     * 
     * @param mail
     * @param comparator
     * @param size
     * @return boolean
     * @throws SieveMailException
     */
    protected boolean test(MailAdapter mail, String comparator, int size)
            throws SieveMailException {
        boolean isTestPassed = false;
        if (comparator.equals(":over"))
            isTestPassed = testOver(mail, size);
        else if (comparator.equals(":under"))
            isTestPassed = testUnder(mail, size);
        return isTestPassed;
    }

    /**
     * Method testUnder.
     * 
     * @param mail
     * @param size
     * @return boolean
     * @throws SieveMailException
     */
    protected boolean testUnder(MailAdapter mail, int size)
            throws SieveMailException {
        return mail.getSize() < size;
    }

    /**
     * Method testOver.
     * 
     * @param mail
     * @param size
     * @return boolean
     * @throws SieveMailException
     */
    protected boolean testOver(MailAdapter mail, int size)
            throws SieveMailException {
        return mail.getSize() > size;
    }

    /**
     * @see org.apache.jsieve.tests.AbstractTest#validateArguments(Arguments,
     *      SieveContext)
     */
    protected void validateArguments(Arguments arguments, SieveContext context)
            throws SieveException {
        // All done in executeBasic()
    }

}
