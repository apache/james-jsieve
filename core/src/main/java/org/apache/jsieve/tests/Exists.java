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

import java.util.List;

import org.apache.jsieve.Argument;
import org.apache.jsieve.Arguments;
import org.apache.jsieve.SieveContext;
import org.apache.jsieve.StringListArgument;
import org.apache.jsieve.exception.SieveException;
import org.apache.jsieve.mail.MailAdapter;

/**
 * Class Exists implements the Exists Test as defined in RFC 3028, section 5.5.
 */
public class Exists extends AbstractTest {

    /**
     * Constructor for Exists.
     */
    public Exists() {
        super();
    }

    /**
     * @see org.apache.jsieve.tests.AbstractTest#executeBasic(MailAdapter,
     *      Arguments, SieveContext)
     */
    protected boolean executeBasic(MailAdapter mail, Arguments arguments,
            SieveContext context) throws SieveException {

        final List<String> argumentList = ((StringListArgument) arguments
                                .getArgumentList().get(0)).getList();
        
        boolean found = true;
        for (final String arg:argumentList) {
            List<String> headers = mail.getMatchingHeader(arg);
            found = found && !headers.isEmpty();
            if (!found) {
                break;
            }
        }
        return found;
    }

    /**
     * @see org.apache.jsieve.tests.AbstractTest#validateArguments(Arguments,
     *      SieveContext)
     */
    protected void validateArguments(Arguments arguments, SieveContext context)
            throws SieveException {
        List<Argument> argumentsList = arguments.getArgumentList();
        if (1 != argumentsList.size())
            throw context.getCoordinate().syntaxException(
                    "Expecting exactly one argument");

        if (!(argumentsList.get(0) instanceof StringListArgument))
            throw context.getCoordinate().syntaxException(
                    "Expecting a StringList");

        if (arguments.hasTests())
            throw context.getCoordinate().syntaxException(
                    "Found unexpected tests");
    }

}
