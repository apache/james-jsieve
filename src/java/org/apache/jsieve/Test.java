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
package org.apache.jsieve;

import org.apache.commons.logging.Log;

import org.apache.jsieve.mail.*;

/**
 * <p>A parsed representation of an RFC3028 test argument...</p>
 * 
 * <code>test = identifier arguments</code>
 */
public class Test implements Executable
{
    // The name of this Test
    private String fieldName;
    
    // The arguments for this Test
    private Arguments fieldArguments;
        
    /**
     * @see org.apache.jsieve.Executable#execute(MailAdapter)
     */
    public Object execute(MailAdapter mail) throws SieveException
    {
        Log log = Logger.getLog();
        if (log.isDebugEnabled())
            log.debug("Test: " + getName());
        return new Boolean(
            TestManager.getInstance().newInstance(getName()).execute(
                mail,
                getArguments()));
    }

    /**
     * Constructor for Test.
     */
    private Test()
    {
        super();
    }
    
    /**
     * Constructor for Test.
     * @param name
     * @param arguments
     */
    public Test(String name, Arguments arguments)
    {
        this();
        setName(name);
        setArguments(arguments);
    }    

    /**
     * Returns the arguments.
     * @return Arguments
     */
    public Arguments getArguments()
    {
        return fieldArguments;
    }

    /**
     * Returns the name.
     * @return String
     */
    public String getName()
    {
        return fieldName;
    }

    /**
     * Sets the arguments.
     * @param children The arguments to set
     */
    protected void setArguments(Arguments arguments)
    {
        fieldArguments = arguments;
    }

    /**
     * Sets the name.
     * @param name The name to set
     */
    protected void setName(String name)
    {
        fieldName = name;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return "Test name: "
            + getName()
            + " Arguments: "
            + (getArguments() == null ? "null" : getArguments().toString());
    }

}
