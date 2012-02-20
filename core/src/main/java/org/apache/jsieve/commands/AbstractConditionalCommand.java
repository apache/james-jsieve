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

import org.apache.jsieve.Block;
import org.apache.jsieve.ConditionManager;
import org.apache.jsieve.SieveContext;
import org.apache.jsieve.exception.SieveException;
import org.apache.jsieve.mail.MailAdapter;

/**
 * Abstract class AbstractConditionalCommand defines a framework of common
 * behavior for conditional Commands (if, elsif, else). Conditional commands use
 * a ConditionManager to relate and validate Commands within their Blocks.
 */
public abstract class AbstractConditionalCommand extends AbstractControlCommand {

    /**
     * Constructor for AbstractConditionalCommand.
     */
    public AbstractConditionalCommand() {
        super();
    }

    /**
     * Method execute executes a Block within the context of a new
     * ConditionManager.
     * 
     * @param mail not null
     * @param block not null
     * @param context not null
     * @return Object 
     * @throws SieveException
     */
    protected Object execute(MailAdapter mail, Block block, SieveContext context)
            throws SieveException {
        // Switch to a new ConditionManager
        ConditionManager oldManager = context.getConditionManager();
        context.setConditionManager(new ConditionManager());

        try {
            // Execute the Block
            Object result = block.execute(mail, context);
            return result;
        } finally {
            // Always restore the old ConditionManager
            context.setConditionManager(oldManager);
        }
    }

    /**
     * @see org.apache.jsieve.commands.AbstractCommand#validateBlock(Block,
     *      SieveContext)
     */
    protected void validateBlock(Block block, SieveContext context)
            throws SieveException {
        if (null == block)
            throw context.getCoordinate().syntaxException("Expecting a Block.");
    }

}
