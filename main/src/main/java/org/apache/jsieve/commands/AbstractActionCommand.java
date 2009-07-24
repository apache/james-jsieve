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

import java.util.List;

import org.apache.jsieve.Argument;
import org.apache.jsieve.Arguments;
import org.apache.jsieve.SieveContext;
import org.apache.jsieve.StringListArgument;
import org.apache.jsieve.exception.CommandException;
import org.apache.jsieve.exception.SieveException;

/**
 * Abstract class AbstractActionCommand defines the common state validation and
 * state update behavior for Action Commands as per RFC 3028, section 8.
 */
public abstract class AbstractActionCommand extends AbstractBodyCommand {

    /**
     * Constructor for AbstractActionCommand.
     */
    public AbstractActionCommand() {
        super();
    }

    /**
     * <p>
     * Method updateState() updates the CommandStateManager to indicate an
     * Action Command has been processed and to cancel implicit keep.
     * </p>
     * 
     * <p>
     * And also
     * </p>
     * 
     * @see org.apache.jsieve.commands.AbstractCommand#updateState(SieveContext)
     */
    protected void updateState(SieveContext context) {
        super.updateState(context);
        context.getCommandStateManager().setHasActions(true);
        context.getCommandStateManager().setImplicitKeep(false);
    }

    /**
     * <p>
     * Method validateState() validates via the CommandStateManager that an
     * Action Command is legal at this time.
     * </p>
     * 
     * <p>
     * Also,
     * </p>
     * 
     * @see org.apache.jsieve.commands.AbstractCommand#validateState(SieveContext)
     */
    protected void validateState(SieveContext context) throws CommandException {
        if (context.getCommandStateManager().isRejected())
            throw context.getCoordinate().commandException(
                    "Cannot perform Actions on a rejected message.");
    }

    /**
     * This is an utility method for subclasses
     * 
     * @see org.apache.jsieve.commands.Redirect
     * @see org.apache.jsieve.commands.optional.FileInto
     * @see org.apache.jsieve.commands.optional.Reject
     */
    protected void validateSingleStringArguments(Arguments arguments,
            SieveContext context) throws SieveException {
        List<Argument> args = arguments.getArgumentList();
        if (args.size() != 1)
            throw context.getCoordinate().syntaxException(
                    "Exactly 1 argument permitted. Found " + args.size());

        Argument argument = args.get(0);
        if (!(argument instanceof StringListArgument))
            throw context.getCoordinate().syntaxException(
                    "Expecting a string-list");

        if (1 != ((StringListArgument) argument).getList().size())
            throw context.getCoordinate().syntaxException(
                    "Expecting exactly one argument");
    }

}
