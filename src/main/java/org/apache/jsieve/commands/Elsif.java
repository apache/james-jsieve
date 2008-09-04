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

package org.apache.jsieve.commands;

import org.apache.jsieve.Arguments;
import org.apache.jsieve.Block;
import org.apache.jsieve.SieveContext;
import org.apache.jsieve.TestList;
import org.apache.jsieve.exception.SieveException;
import org.apache.jsieve.mail.MailAdapter;

/**
 * Class Elsif implements the Elsif Command as defined in RFC 3028, section 3.1.
 */
public class Elsif extends AbstractConditionalCommand {

    /**
     * Constructor for Elsif.
     */
    public Elsif() {
        super();
    }

    /**
     * <p>
     * Conditionally eexecute a Block if an Elsif Condition is allowed and
     * runnable.
     * </p>
     * <p>
     * Also,
     * 
     * @see org.apache.jsieve.commands.AbstractCommand#executeBasic(MailAdapter,
     *      Arguments, Block, SieveContext)
     *      </p>
     */
    protected Object executeBasic(MailAdapter mail, Arguments arguments,
            Block block, SieveContext context) throws SieveException {
        // Check Syntax
        if (!context.getConditionManager().isElsifAllowed())
            throw context.getCoordinate().commandException(
                    "Unexpected Command: \"elsif\".");

        // Check Runnable
        if (!context.getConditionManager().isElsifRunnable())
            return Boolean.FALSE;

        // Run the tests
        Boolean isTestPassed = (Boolean) arguments.getTestList().execute(mail,
                context);

        // If the tests answered TRUE, execute the Block
        if (isTestPassed.booleanValue())
            execute(mail, block, context);

        // Update the ConditionManager
        context.getConditionManager().setElsifTestResult(
                isTestPassed.booleanValue());

        // Return the result
        return isTestPassed;
    }

    /**
     * @see org.apache.jsieve.commands.AbstractCommand#validateArguments(Arguments,
     *      SieveContext)
     */
    protected void validateArguments(Arguments arguments, SieveContext context)
            throws SieveException {
        TestList testList = arguments.getTestList();
        if (null == testList || testList.getTests().isEmpty())
            throw context.getCoordinate().syntaxException("Expecting a Test");
    }

}
