/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2004 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache", "Jakarta", "JAMES", "JSieve" and 
 *    "Apache Software Foundation" must not be used to endorse or promote
 *    products derived from this software without prior written permission.
 *    For written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * Portions of this software are based upon public domain software
 * originally written at the National Center for Supercomputing Applications,
 * University of Illinois, Urbana-Champaign.
 */
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
