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

import org.apache.commons.logging.Log;
import org.apache.jsieve.Arguments;
import org.apache.jsieve.SieveContext;
import org.apache.jsieve.exception.SieveException;
import org.apache.jsieve.exception.SyntaxException;
import org.apache.jsieve.mail.MailAdapter;

/**
 * Abstract class AbstractTest defines a framework of common behavior for Sieve
 * Tests.
 */
public abstract class AbstractTest implements ExecutableTest {

    /**
     * Constructor for AbstractTest.
     */
    public AbstractTest() {
        super();
    }

    /**
     * <p>
     * Method execute executes a basic Sieve Test after first invoking framework
     * methods to validate the Command arguments.
     * </p>
     * 
     * <p>
     * Also,
     * 
     * @see org.apache.jsieve.tests.ExecutableTest#execute(MailAdapter,
     *      Arguments, SieveContext)
     */
    public boolean execute(MailAdapter mail, Arguments arguments,
            SieveContext context) throws SieveException {
        validateArguments(arguments, context);
        return executeBasic(mail, arguments, context);
    }

    /**
     * Abstract method executeBasic invokes a Sieve Test.
     * 
     * @param mail
     * @param arguments
     * @param context
     *            <code>SieveContext</code> giving contextual information, not
     *            null
     * @return boolean
     * @throws SieveException
     */
    protected abstract boolean executeBasic(MailAdapter mail,
            Arguments arguments, SieveContext context) throws SieveException;

    /**
     * Framework method validateArguments is invoked before a Sieve Test is
     * executed to validate its arguments. Subclass methods are expected to
     * override or extend this method to perform their own validation as
     * appropriate.
     * 
     * @param arguments
     * @param context
     *            <code>SieveContext</code> giving comntextual information,
     *            not null
     * @throws SieveException
     */
    protected void validateArguments(Arguments arguments, SieveContext context)
            throws SieveException {
        if (!arguments.getArgumentList().isEmpty()) {
            final Log logger = context.getLog();
            if (logger.isWarnEnabled()) {
                logger.warn("Unexpected arguments for " + getClass().getName());
            }
            context.getCoordinate().logDiagnosticsInfo(logger);
            logger.debug(arguments);
            final String message = context.getCoordinate()
                    .addStartLineAndColumn("Found unexpected arguments.");
            throw new SyntaxException(message);
        }
    }
}
