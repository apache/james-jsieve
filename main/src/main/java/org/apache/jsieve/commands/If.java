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
import org.apache.jsieve.exception.SieveException;
import org.apache.jsieve.mail.MailAdapter;

/**
 * Class If implements the If Command as defined in RFC 3028, section 3.1.
 */
public class If extends AbstractConditionalCommand {
    /**
     * Constructor for If.
     */
    public If() {
        super();
    }

    /**
     * <p>
     * Conditionally eexecute a Block if an If Condition is allowed and
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
        if (!context.getConditionManager().isIfAllowed())
            throw context.getCoordinate().commandException(
                    "Unexpected Command: \"if\".");

        // Check Runnable
        if (!context.getConditionManager().isIfRunnable())
            return Boolean.FALSE;

        // Run the tests
        final boolean isTestPassed = arguments.getTestList().allTestsPass(mail,context);

        // If the tests answered TRUE, execute the Block
        if (isTestPassed)
            execute(mail, block, context);

        // Update the ConditionManager
        context.getConditionManager().setIfTestResult(isTestPassed);

        // Return the result
        return isTestPassed;
    }

    /**
     * @see org.apache.jsieve.commands.AbstractCommand#validateArguments(Arguments,
     *      SieveContext)
     */
    protected void validateArguments(Arguments arguments, SieveContext context)
            throws SieveException {
        if (!arguments.hasTests())
            throw context.getCoordinate().syntaxException("Expecting a Test");
    }

}
