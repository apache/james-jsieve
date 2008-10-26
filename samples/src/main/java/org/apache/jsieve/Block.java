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

import org.apache.jsieve.exception.SieveException;
import org.apache.jsieve.mail.MailAdapter;

/**
 * <p>
 * A parsed representation of the RFC3028 BNF...
 * </p>
 * 
 * <code>block = "{" commands "}"</code>
 * 
 */
public class Block implements Executable {
    /**
     * The chilren of the Block
     */
    private Commands fieldChildren;

    /**
     * Constructor for Block.
     */
    private Block() {
        super();
    }

    /**
     * Constructor for Block.
     * 
     * @param commands
     */
    public Block(Commands commands) {
        this();
        setChildren(commands);
    }

    /**
     * Returns the commands.
     * 
     * @return Commands
     */
    public Commands getChildren() {
        return fieldChildren;
    }

    /**
     * Sets the commands.
     * 
     * @param commands
     *            The commands to set
     */
    protected void setChildren(Commands commands) {
        fieldChildren = commands;
    }

    /**
     * @see org.apache.jsieve.Executable#execute(MailAdapter, SieveContext)
     */
    public Object execute(MailAdapter mail, SieveContext context)
            throws SieveException {
        return getChildren().execute(mail, context);
    }

    public String toString() {
        return "BLOCK: " + getChildren();
    }

}
