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

import org.apache.jsieve.Arguments;
import org.apache.jsieve.SieveException;
import org.apache.jsieve.SyntaxException;
import org.apache.jsieve.mail.MailAdapter;

/**
 * Class | Interface Enter description here
 * 
 * Creation Date: 13-Jan-04
 * @author sbrewin
 * 
 * Copyright 2003, Synergy Systems Limited
 */
/**
 * Abstract class AbstractTest defines a framework of common behavior for 
 * Sieve Tests. 
 */
public abstract class AbstractTest implements ExecutableTest
{

    /**
     * Constructor for AbstractTest.
     */
    public AbstractTest()
    {
        super();
    }

    /**
     * <p>Method execute executes a basic Sieve Test after first invoking framework
     * methods to validate the Command arguments.</p>
     * 
     * <p>Also, @see org.apache.jsieve.tests.ExecutableTest#execute(MailAdapter, Arguments)
     */
    public boolean execute(MailAdapter mail, Arguments arguments)
        throws SieveException
    {
        validateArguments(arguments);
        return executeBasic(mail, arguments);
    }
    
    /**
     * Abstract method executeBasic invokes a Sieve Test.
     * @param mail
     * @param arguments
     * @return boolean
     * @throws SieveException
     */
    protected abstract boolean executeBasic(MailAdapter mail, Arguments arguments)
        throws SieveException;
        
    /**
     * Framework method validateArguments is invoked before a Sieve Test is 
     * executed to validate its arguments. Subclass methods are expected to override
     * or extend this method to perform their own validation as appropriate.
     * @param arguments
     * @throws SieveException
     */
    protected void validateArguments(Arguments arguments) throws SieveException
    {
        if (!arguments.getArgumentList().isEmpty())
            throw new SyntaxException("Found unexpected arguments");
    }
    

        
   

}
