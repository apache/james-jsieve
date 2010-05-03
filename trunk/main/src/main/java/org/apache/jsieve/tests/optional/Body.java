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

package org.apache.jsieve.tests.optional;

import java.util.List;

import org.apache.jsieve.Argument;
import org.apache.jsieve.Arguments;
import org.apache.jsieve.SieveContext;
import org.apache.jsieve.StringListArgument;
import org.apache.jsieve.TagArgument;
import org.apache.jsieve.exception.SieveException;
import org.apache.jsieve.exception.SyntaxException;
import org.apache.jsieve.mail.MailAdapter;
import org.apache.jsieve.mail.SieveMailException;
import org.apache.jsieve.tests.AbstractTest;

/**
 * Implementation of body extension defined in
 * <a href='http://tools.ietf.org/html/rfc5173'>RFC5173</a>.
 */
public class Body extends AbstractTest {
    private StringListArgument strings;

    public Body() {
        super();
        strings = null;
    }

    // TODO: Check how complete this is of the body specification
    // Validate (sorta); we're only implementing part of the spec
    protected void validateArguments(Arguments args, SieveContext ctx)
            throws SieveException {

        final List<Argument> arglist = args.getArgumentList();
        if (arglist.size() != 2) {
            throw new SyntaxException(
                    "Currently body-test can only two arguments");
        }

        // TODO: FIXME: As this is a limited implementation force the use of
        // ':contains'.
        Argument arg = arglist.get(0);
        if (!(arg instanceof TagArgument)) {
            throw new SyntaxException("Body expects a :contains tag");
        }

        if (!((TagArgument) arg).getTag().equals(":contains")) {
            throw new SyntaxException("Body expects a :contains tag");
        }

        // Get list of strings to search for
        arg = arglist.get(1);
        if (!(arg instanceof StringListArgument)) {
            throw new SyntaxException("Body expects a list of strings");
        }
        strings = (StringListArgument) args.getArgumentList().get(1);
    }

    // This implement body tests of the form
    // "body :contains ['string' 'string' ....]"
    protected boolean executeBasic(MailAdapter mail, Arguments args,
            SieveContext ctx) throws SieveException {
        // Attempt to fetch content as a string. If we can't do this it's
        // not a message we can handle.
        if (mail.getContentType().indexOf("text/") != 0) {
            throw new SieveMailException("Message is not of type 'text'");
        }

        // Compare each test string with body, ignoring case
        for (final String phrase:strings.getList()) {
            if (mail.isInBodyText(phrase)) {
                return true;
            }
        }
        return false;
    }

}
