/***********************************************************************
 * Copyright (c) 2003-2004 The Apache Software Foundation.             *
 * All rights reserved.                                                *
 * ------------------------------------------------------------------- *
 * Licensed under the Apache License, Version 2.0 (the "License"); you *
 * may not use this file except in compliance with the License. You    *
 * may obtain a copy of the License at:                                *
 *                                                                     *
 *     http://www.apache.org/licenses/LICENSE-2.0                      *
 *                                                                     *
 * Unless required by applicable law or agreed to in writing, software *
 * distributed under the License is distributed on an "AS IS" BASIS,   *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or     *
 * implied.  See the License for the specific language governing       *
 * permissions and limitations under the License.                      *
 ***********************************************************************/

package org.apache.jsieve.tests;

import java.util.Iterator;
import java.util.List;

import org.apache.jsieve.Arguments;
import org.apache.jsieve.SieveException;
import org.apache.jsieve.SyntaxException;
import org.apache.jsieve.Test;
import org.apache.jsieve.mail.MailAdapter;

/**
 * Class Not implements the Not Test as defined in RFC 3028, section 5.8.
 */
public class Not extends AbstractTest
{

    /**
     * Constructor for Not.
     */
    public Not()
    {
        super();
    }

    /**
     * @see org.apache.jsieve.tests.AbstractTest#executeBasic(MailAdapter, Arguments)
     */
    protected boolean executeBasic(MailAdapter mail, Arguments arguments) throws SieveException
    {
        boolean result = true;
        List tests = arguments.getTestList().getTests();
        if (tests.size() != 1)
            throw new SyntaxException(
                "Exactly 1 test permitted. Found " + tests.size());
        Iterator testsIter = tests.iterator();
        while (testsIter.hasNext())
        {
            result =
                result
                    && ((Boolean) ((Test) testsIter.next()).execute(mail))
                        .booleanValue();
        }
        return !result;
    }

}
