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
import org.apache.jsieve.StringListArgument;
import org.apache.jsieve.SyntaxException;
import org.apache.jsieve.mail.MailAdapter;

/**
 * Class Exists implements the Exists Test as defined in RFC 3028, section 5.5.
 */
public class Exists extends AbstractTest
{

    /**
     * Constructor for Exists.
     */
    public Exists()
    {
        super();
    }

    /**
     * @see org.apache.jsieve.tests.AbstractTest#executeBasic(MailAdapter, Arguments)
     */
    protected boolean executeBasic(MailAdapter mail, Arguments arguments)
        throws SieveException
    {

        Iterator headerNamesIter =
            ((StringListArgument) arguments.getArgumentList().get(0))
                .getList()
                .iterator();
                
        boolean found = true;
        while (found && headerNamesIter.hasNext())
        {
            List headers = mail.getMatchingHeader((String) headerNamesIter.next());
            found = found && !headers.isEmpty();
        }
        return found;
    }

    /**
     * @see org.apache.jsieve.tests.AbstractTest#validateArguments(Arguments)
     */
    protected void validateArguments(Arguments arguments) throws SieveException
    {
        List argumentsList = arguments.getArgumentList();
        if (1 != argumentsList.size())
            throw new SyntaxException("Expecting exactly one argument");
            
        if (!(argumentsList.get(0) instanceof StringListArgument))
            throw new SyntaxException("Expecting a StringList");        
               
        if (arguments.hasTests())
            throw new SyntaxException("Found unexpected tests");
    }

}
