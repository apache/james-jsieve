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

/**
 * Abstract class AbstractBodyCommand defines the common state update behavior
 * for Body Commands. All Commands which are not PrologCommands are Body
 * Commands.
 */
public abstract class AbstractBodyCommand extends AbstractCommand {

    /**
     * Constructor for AbstractBodyCommand.
     */
    public AbstractBodyCommand() {
        super();
    }

    /**
     * <p>
     * Method updateState() updates the CommandStateManager to indicate a Body
     * Command has been processed.
     * </p>
     * 
     * <p>
     * Also,
     * 
     * @see org.apache.jsieve.commands.AbstractCommand#updateState(SieveContext)
     */
    protected void updateState(final SieveContext context) {
        context.getCommandStateManager().setInProlog(false);
    }

}
