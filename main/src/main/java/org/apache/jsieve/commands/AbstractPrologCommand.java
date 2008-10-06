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

import org.apache.jsieve.SieveContext;
import org.apache.jsieve.exception.CommandException;

/**
 * <p>
 * Abstract class AbstractPrologCommand defines the common state validation
 * behavior for Prolog Commands. In RFC 3028 the only Prolog Command is
 * 'requires', however, the specification may evolve.
 * </p>
 */
public abstract class AbstractPrologCommand extends AbstractCommand {

    /**
     * Constructor for AbstractPrologCommand.
     */
    public AbstractPrologCommand() {
        super();
    }

    /**
     * <p>
     * Method validateState() ensures, via the CommandStateManager, that a
     * Prolog Command is permissible.
     * </p>
     * 
     * <p>
     * Also,
     * </p>
     * 
     * @see org.apache.jsieve.commands.AbstractCommand#validateState(SieveContext)
     */
    protected void validateState(SieveContext context) throws CommandException {
        super.validateState(context);

        if (!(context.getCommandStateManager().isInProlog()))
            throw context.getCoordinate().commandException(
                    "Invalid state for a prolog command.");
    }

}
