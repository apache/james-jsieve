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

import java.util.List;

import org.apache.jsieve.exception.SieveException;
import org.apache.jsieve.mail.MailAdapter;

/**
 * <p>
 * A parsed representation of the RFC3028 BNF...
 * </p>
 * 
 * <code>commands = *command</code>
 * 
 */
public class Commands implements Executable {
    /**
     * A List of children of the receiver
     */
    private List<Command> fieldChildren;

    /**
     * Constructor for Commands.
     */
    private Commands() {
        super();
    }

    /**
     * Constructor for Commands.
     * 
     * @param commands
     */
    public Commands(List<Command> commands) {
        this();
        setChildren(commands);
    }

    /**
     * Returns the commands.
     * 
     * @return List
     */
    public List<Command> getChildren() {
        return fieldChildren;
    }

    /**
     * Sets the commands.
     * 
     * @param commands
     *            The commands to set
     */
    protected void setChildren(List<Command> commands) {
        fieldChildren = commands;
    }

    /**
     * @see org.apache.jsieve.Executable#execute(MailAdapter, SieveContext)
     */
    public Object execute(MailAdapter mail, SieveContext context)
            throws SieveException {
        for (Command command:fieldChildren) {
            command.execute(mail, context);
        };
        return null;
    }

    public String toString() {
        return "COMMANDS: " + fieldChildren;
    }

}
