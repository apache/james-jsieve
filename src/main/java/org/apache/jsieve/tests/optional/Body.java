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

import java.lang.String;
import java.lang.ClassCastException;
import java.util.List;
import java.util.ListIterator;

import javax.mail.Message;
import javax.mail.MessagingException;

import org.apache.jsieve.SieveException;
import org.apache.jsieve.SyntaxException;
import org.apache.jsieve.SieveContext;
import org.apache.jsieve.Arguments;
import org.apache.jsieve.TagArgument;
import org.apache.jsieve.StringListArgument;
import org.apache.jsieve.tests.AbstractTest;
import org.apache.jsieve.mail.MailAdapter;
import org.apache.jsieve.mail.SieveMailException;

/**
 * This implements a single subset of the 'body' Sieve test as define here:
 *     http://tools.ietf.org/html/draft-ietf-sieve-body-00
 **/
public class Body extends AbstractTest
{
    private StringListArgument strings;

    public Body()
    {
        super();
        strings = null;
    }

    // Validate (sorta); we're only implementing part of the spec
    protected void validateArguments(Arguments args, SieveContext ctx)
        throws SieveException
    {

        List arglist = args.getArgumentList();
        if (arglist.size() != 2) {
            throw new SyntaxException("Currently body-test can only two arguments");
        }

        // FIXME: As this is a limited implementation force the use of
        // ':contains'.
        Object arg = arglist.get(0);
        if (! (arg instanceof TagArgument)) {
            throw new SyntaxException("Body expects a :contains tag");
        }

        if (! ((TagArgument) arg).getTag().equals(":contains")) {
            throw new SyntaxException("Body expects a :contains tag");
        }

        // Get list of strings to search for
        arg = arglist.get(1);
        if (! (arg instanceof StringListArgument)) {
            throw new SyntaxException("Body expects a list of strings");
        }
        strings = (StringListArgument) args.getArgumentList().get(1);
    }


    // This implement body tests of the form
    //   "body :contains ['string' 'string' ....]"
    protected boolean executeBasic(MailAdapter mail,
                                   Arguments args,
                                   SieveContext ctx)
        throws SieveException
    {
        // Attempt to fetch content as a string. If we can't do this it's
        // not a message we can handle.
        if (mail.getContentType().indexOf("text/") != 0) {
            throw new SieveMailException("Message is not of type 'text'");
        }
        String body = (String) mail.getContent();
        body = body.toLowerCase();


        // Compare each test string with body, ignoring case
        ListIterator iter = strings.getList().listIterator();
        while (iter.hasNext()) {
            String str = (String) iter.next();
            if (body.indexOf(str.toLowerCase()) != -1) {
                return true;
            }
        }
        return false;
    }



}
