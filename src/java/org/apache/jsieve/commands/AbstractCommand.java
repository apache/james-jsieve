/***********************************************************************
 * Copyright (c) 2003-2004 The Apache Software Foundation.             *
 * All rights reserved.                                                *
 * ------------------------------------------------------------------- *
 * Licensed under the Apache License, Version 2.0 (the "License"); you *
 * may not use this file except in compliance with the License. You    *
 * may obtain a copy of the License at:                                *
 *                                                                     *
 *     http://www.apache.org/licenses/LICENSE-2.0                      *
 *                                                                     *
 * Unless required by applicable law or agreed to in writing, software *
 * distributed under the License is distributed on an "AS IS" BASIS,   *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or     *
 * implied.  See the License for the specific language governing       *
 * permissions and limitations under the License.                      *
 ***********************************************************************/

package org.apache.jsieve.commands;

import org.apache.jsieve.Arguments;
import org.apache.jsieve.Block;
import org.apache.jsieve.CommandException;
import org.apache.jsieve.SieveException;
import org.apache.jsieve.SyntaxException;
import org.apache.jsieve.mail.MailAdapter;

/**
 * Abstract class AbstractCommand defines a framework of common behavior for 
 * Sieve Commands. 
 */
public abstract class AbstractCommand implements ExecutableCommand
{

    /**
     * Constructor for AbstractCommand.
     */
    public AbstractCommand()
    {
        super();
    }
    
    /**
     * Framework method validateState is invoked before a Sieve Command is 
     * executed to validate its state. Subclass methods are expected to override
     * or extend this method to perform their own validation as appropriate.
     * 
     * @throws CommandException
     */
    protected void validateState()
            throws CommandException
    {
    }
    
    /**
     * Framework method updateState is invoked after a Sieve Command has executed to
     * update the Sieve state. Subclass methods are expected to override or extend
     * this method to update state as appropriate.
     */
    protected void updateState()
    {
    }    
    
    /**
     * Framework method validateArguments is invoked before a Sieve Command is 
     * executed to validate its arguments. Subclass methods are expected to override
     * or extend this method to perform their own validation as appropriate.
     * 
     * @param arguments
     * @throws SieveException
     */
    protected void validateArguments(Arguments arguments) throws SieveException
    {
        if (!arguments.getArgumentList().isEmpty())
            throw new SyntaxException("Found unexpected arguments");
    }
    
    /**
     * Framework method validateBlock is invoked before a Sieve Command is 
     * executed to validate its Block. Subclass methods are expected to override
     * or extend this method to perform their own validation as appropriate.
     * 
     * @param block
     * @throws SieveException
     */
    protected void validateBlock(Block block)
            throws SieveException
    {           
        if (null != block)
            throw new SyntaxException("Found unexpected Block");         
    }        
    
    /**
     * <p>Method execute executes a basic Sieve Command after first invoking framework
     * methods to validate that Sieve is in a legal state to invoke the Command and
     * that the Command arguments are legal. After invocation, a framework method is
     * invoked to update the state.</p>
     * 
     * <p>Also, @see org.apache.jsieve.Executable#execute()</p>
     */
    public Object execute(MailAdapter mail, Arguments arguments, Block block)
        throws SieveException
    {
        validateState();
        validateArguments(arguments);        
        validateBlock(block);         
        Object result = executeBasic( mail, arguments, block);
        updateState();
        return result;
    } 
    
    /**
     * Abstract method executeBasic invokes a Sieve Command.
     * @param mail
     * @param arguments
     * @param block
     * @return Object
     * @throws SieveException
     */
    abstract protected Object executeBasic(MailAdapter mail, Arguments arguments, Block block)
        throws SieveException;           

}
