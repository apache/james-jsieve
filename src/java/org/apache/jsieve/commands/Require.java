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

import java.util.Iterator;
import java.util.List;

import org.apache.jsieve.Arguments;
import org.apache.jsieve.Block;
import org.apache.jsieve.CommandManager;
import org.apache.jsieve.FeatureException;
import org.apache.jsieve.LookupException;
import org.apache.jsieve.SieveException;
import org.apache.jsieve.StringListArgument;
import org.apache.jsieve.SyntaxException;
import org.apache.jsieve.TestManager;
import org.apache.jsieve.mail.MailAdapter;

/**
 * Class Require implements the Require Command as defined in RFC 3028, section 3.2.
 */
public class Require extends AbstractPrologCommand
{

    /**
     * Constructor for Require.
     * @param children
     */
    public Require()
    {
        super();
    }

    /**
     * <p>Ensure the required feature is configured.</p>
     * <p>Also,
     * @see org.apache.jsieve.commands.AbstractCommand#executeBasic(MailAdapter, Arguments, Block)
     * </p>
     */ 
    protected Object executeBasic(
        MailAdapter mail,
        Arguments arguments,
        Block block)
        throws SieveException
    {
        Iterator stringsIter =
            ((StringListArgument) arguments.getArgumentList().get(0))
                .getList()
                .iterator();

        while (stringsIter.hasNext())
        {
            validateFeature((String) stringsIter.next(), mail);
        }
        return null;
    }

    /**
     * Method validateFeature validates the required feature is configured as either
     * a Command or a Test.
     * @param name
     * @param mail
     * @throws FeatureException
     */
    protected void validateFeature(String name, MailAdapter mail)
        throws FeatureException
    {
        // Validate as a Command
        try
        {
            validateCommand(name);
            return;
        }
        catch (LookupException e)
        {
            // Not a command
        }       
        
        // Validate as a Test             
        try
        {
            validateTest(name);
        }
        catch (LookupException e)
        {
            throw new FeatureException(
                "Feature \"" + name + "\" is not supported.");
        }
    }
    
    /**
     * Method validateCommand.
     * @param name
     * @throws LookupException
     */
    protected void validateCommand(String name)
        throws LookupException
    {
        CommandManager.getInstance().lookup(name);
    }
    
    /**
     * Method validateTest.
     * @param name
     * @throws LookupException
     */
    protected void validateTest(String name)
        throws LookupException
    {
        TestManager.getInstance().lookup(name);
    }
    
    /**
     * @see org.apache.jsieve.commands.AbstractCommand#validateArguments(Arguments)
     */
    protected void validateArguments(Arguments arguments) throws SieveException
    {
        List args = arguments.getArgumentList();
        if (args.size() != 1)
            throw new SyntaxException(
                "Exactly 1 argument permitted. Found " + args.size());

        Object argument = args.get(0);
        if (!(argument instanceof StringListArgument))
            throw new SyntaxException("Expecting a string-list");
    }
    
}
