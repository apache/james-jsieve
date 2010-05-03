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

import org.apache.commons.logging.Log;
import org.apache.jsieve.exception.SieveException;
import org.apache.jsieve.mail.MailAdapter;

/**
 * <p>
 * A parsed representation of the RFC3028 BNF...
 * </p>
 * 
 * <code>command = identifier arguments ( ";" / block )</code>
 */
public class Command implements Executable {

    /** The name of this Command */
    private String fieldName;

    /** The Arguments for this Command */
    private Arguments fieldArguments;

    /** The Block for this Command */
    private Block fieldBlock;

    /**
     * Script coordinate for this command. Commands are executed after the
     * document has been parse. So this must be recorded on construction and
     * stored for later use.
     */
    private ScriptCoordinate coordinate;

    /**
     * Constructor for Test.
     */
    private Command() {
        super();
    }

    /**
     * Constructor for Command.
     * 
     * @param name
     * @param arguments
     * @param block
     */
    public Command(String name, Arguments arguments, Block block,
            ScriptCoordinate coordinate) {
        this();
        this.coordinate = coordinate;
        setName(name);
        setArguments(arguments);
        setBlock(block);
    }

    /**
     * Returns the name.
     * 
     * @return String
     */
    public String getName() {
        return fieldName;
    }

    /**
     * Sets the name.
     * 
     * @param name
     *            The name to set
     */
    protected void setName(String name) {
        fieldName = name;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Command name: "
                + getName()
                + " "
                + ((getArguments() == null) ? "null" : getArguments()
                        .toString()) + " Block: "
                + ((getBlock() == null) ? "null" : getBlock().toString());
    }

    /**
     * Returns the arguments.
     * 
     * @return Arguments
     */
    public Arguments getArguments() {
        return fieldArguments;
    }

    /**
     * Returns the block.
     * 
     * @return Block
     */
    public Block getBlock() {
        return fieldBlock;
    }

    /**
     * Sets the arguments.
     * 
     * @param arguments
     *            The arguments to set
     */
    protected void setArguments(Arguments arguments) {
        fieldArguments = arguments;
    }

    /**
     * Sets the block.
     * 
     * @param block
     *            The block to set
     */
    protected void setBlock(Block block) {
        fieldBlock = block;
    }

    /**
     * @see org.apache.jsieve.Executable#execute(MailAdapter, SieveContext)
     */
    public Object execute(MailAdapter mail, SieveContext context)
            throws SieveException {
        Log log = context.getLog();
        if (log.isDebugEnabled()) {
            log.debug(toString());
            coordinate.debugDiagnostics(log);
        }
        // commands are executed after the parsing phase
        // recursively from the top level block
        // so need to use the coordinate recorded from the parse
        context.setCoordinate(coordinate);
        final ExecutableCommand executable = context.getCommandManager().getCommand(getName());
        final Object result = executable.execute(mail, getArguments(),
                getBlock(), context);
        return result;
    }
}
