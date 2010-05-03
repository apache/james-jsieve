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
import org.apache.jsieve.ExecutableCommand;
import org.apache.jsieve.SieveContext;
import org.apache.jsieve.exception.CommandException;
import org.apache.jsieve.exception.SieveException;
import org.apache.jsieve.mail.MailAdapter;

/**
 * Abstract class AbstractCommand defines a framework of common behavior for
 * Sieve Commands.
 */
public abstract class AbstractCommand implements ExecutableCommand {

    /**
     * Constructor for AbstractCommand.
     */
    public AbstractCommand() {
        super();
    }

    /**
     * Framework method validateState is invoked before a Sieve Command is
     * executed to validate its state. Subclass methods are expected to override
     * or extend this method to perform their own validation as appropriate.
     * 
     * @param context
     *            <code>SieveContext</code> giving contextual information, not
     *            null
     * @throws CommandException
     */
    protected void validateState(SieveContext context) throws CommandException {
    }

    /**
     * Framework method updateState is invoked after a Sieve Command has
     * executed to update the Sieve state. Subclass methods are expected to
     * override or extend this method to update state as appropriate.
     * 
     * @param context not null
     */
    protected void updateState(SieveContext context) {
    }

    /**
     * Framework method validateArguments is invoked before a Sieve Command is
     * executed to validate its arguments. Subclass methods are expected to
     * override or extend this method to perform their own validation as
     * appropriate.
     * 
     * @param arguments
     * @param context
     *            <code>SieveContext</code> giving contextual information, not
     *            null
     * @throws SieveException
     */
    protected void validateArguments(Arguments arguments, SieveContext context)
            throws SieveException {
        if (!arguments.getArgumentList().isEmpty())
            throw context.getCoordinate().syntaxException(
                    "Found unexpected arguments");
    }

    /**
     * Framework method validateBlock is invoked before a Sieve Command is
     * executed to validate its Block. Subclass methods are expected to override
     * or extend this method to perform their own validation as appropriate.
     * 
     * @param block
     * @param context
     *            <code>ScriptCoordinate</code> giving positional information,
     *            not null
     * @throws SieveException
     */
    protected void validateBlock(Block block, SieveContext context)
            throws SieveException {
        if (null != block)
            throw context.getCoordinate().syntaxException(
                    "Found unexpected Block. Missing ';'?");
    }

    /**
     * <p>
     * Method execute executes a basic Sieve Command after first invoking
     * framework methods to validate that Sieve is in a legal state to invoke
     * the Command and that the Command arguments are legal. After invocation, a
     * framework method is invoked to update the state.
     * </p>
     * 
     * <p>
     * Also,
     * </p>
     * 
     * @see org.apache.jsieve.Executable#execute(MailAdapter, SieveContext)
     */
    public Object execute(MailAdapter mail, Arguments arguments, Block block,
            SieveContext context) throws SieveException {
        validateState(context);
        validateArguments(arguments, context);
        validateBlock(block, context);
        Object result = executeBasic(mail, arguments, block, context);
        updateState(context);
        return result;
    }

    /**
     * Abstract method executeBasic invokes a Sieve Command.
     * 
     * @param mail
     * @param arguments
     * @param block
     * @param context
     *            <code>SieveContext</code> giving contextual information, not
     *            null
     * @return Object
     * @throws SieveException
     */
    abstract protected Object executeBasic(MailAdapter mail,
            Arguments arguments, Block block, SieveContext context)
            throws SieveException;

}
