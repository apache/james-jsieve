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

package org.apache.jsieve.junit.commands;

import org.apache.jsieve.Arguments;
import org.apache.jsieve.Block;
import org.apache.jsieve.SieveException;
import org.apache.jsieve.commands.ExecutableCommand;
import org.apache.jsieve.mail.MailAdapter;

/**
 * Class ThrowTestException implements a Sieve Command to throw a TestException.
 */
public class ThrowTestException implements ExecutableCommand
{

    /**
     * Class TestException
     */
    public class TestException extends SieveException
    {

        /**
         * Constructor for TestException.
         */
        public TestException()
        {
            super();
        }

        /**
         * Constructor for TestException.
         * @param message
         */
        public TestException(String message)
        {
            super(message);
        }

        /**
         * Constructor for TestException.
         * @param message
         * @param cause
         */
        public TestException(String message, Throwable cause)
        {
            super(message, cause);
        }

        /**
         * Constructor for TestException.
         * @param cause
         */
        public TestException(Throwable cause)
        {
            super(cause);
        }

    }
    /**
     * Constructor for ThrowTestException.
     */
    public ThrowTestException()
    {
        super();
    }

    /**
     * @see org.apache.jsieve.commands.ExecutableCommand#execute(MailAdapter, Arguments, Block)
     */
    public Object execute(MailAdapter mail, Arguments arguments, Block block)
        throws SieveException
    {
        throw new TestException();
    }
    
}
