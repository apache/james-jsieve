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

package org.apache.jsieve;

import org.apache.commons.logging.Log;
import org.apache.jsieve.exception.LookupException;
import org.apache.jsieve.exception.SieveException;
import org.apache.jsieve.mail.MailAdapter;
import org.apache.jsieve.tests.ExecutableTest;

/**
 * <p>
 * A parsed representation of an RFC3028 test argument...
 * </p>
 * 
 * <code>test = identifier arguments</code>
 */
public class Test implements Executable {

    /** The name of this Test */
    private String fieldName;

    /** The arguments for this Test */
    private Arguments fieldArguments;

    /**
     * @see org.apache.jsieve.Executable#execute(MailAdapter, SieveContext)
     */
    public Object execute(MailAdapter mail, SieveContext context)
            throws SieveException {
        return new Boolean(isTestPassed(mail, context));
    }

    /**
     * Is this test passed for the given mail?
     * @param mail not null
     * @param context not null
     * @return true when the test passes, false otherwise
     * @throws LookupException
     * @throws SieveException
     */
    public boolean isTestPassed(MailAdapter mail, SieveContext context) throws LookupException, SieveException {
        Log log = context.getLog();
        if (log.isDebugEnabled()) {
            log.debug(toString());
        }
        final String name = getName();
        final ExecutableTest test = context.getTestManager().getTest(name);
        return test.execute(mail, getArguments(), context);
    }

    /**
     * Constructor for Test.
     */
    private Test() {
        super();
    }

    /**
     * Constructor for Test.
     * 
     * @param name
     * @param arguments
     */
    public Test(String name, Arguments arguments) {
        this();
        setName(name);
        setArguments(arguments);
    }

    /**
     * Returns the arguments.
     * 
     * @return Arguments
     */
    public Arguments getArguments() {
        return fieldArguments;
    }

    /**
     * Returns the name.
     * 
     * @return String
     */
    public String getName() {
        return fieldName;
    }

    /**
     * Sets the arguments.
     * 
     * @param arguments
     *            The arguments to set
     */
    protected void setArguments(Arguments arguments) {
        fieldArguments = arguments;
    }

    /**
     * Sets the name.
     * 
     * @param name
     *            The name to set
     */
    protected void setName(String name) {
        fieldName = name;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Test name: " + getName() + " "
                + (getArguments() == null ? "null" : getArguments().toString());
    }

}
