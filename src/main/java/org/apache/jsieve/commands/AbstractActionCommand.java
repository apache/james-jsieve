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

import org.apache.jsieve.CommandException;

/**
 * Abstract class AbstractActionCommand defines the common state validation and state
 * update behavior for Action Commands as per RFC 3028, section 8. 
 */
public abstract class AbstractActionCommand extends AbstractBodyCommand
{

    /**
     * Constructor for AbstractActionCommand.
     */
    public AbstractActionCommand()
    {
        super();
    }
    
    /**
     * <p>Method updateState() updates the CommandStateManager to indicate an Action
     * Command has been processed and to cancel implicit keep.</p>
     * 
     * <p>Also, @see org.apache.jsieve.commands.AbstractCommand#updateState()</p>
     */
    protected void updateState()
    {
        super.updateState();
        CommandStateManager.getInstance().setHasActions(true);
        CommandStateManager.getInstance().setImplicitKeep(false);        
    }

    /**
     * <p>Method validateState() validates via the CommandStateManager that an
     * Action Command is legal at this time.</p>
     * 
     * <p>Also, @see org.apache.jsieve.commands.AbstractCommand#validateState()
     */
    protected void validateState() throws CommandException
    {
        if (CommandStateManager.getInstance().isRejected())
            throw new CommandException("Cannot perform Actions on a rejected message.");
    }    
    
}
