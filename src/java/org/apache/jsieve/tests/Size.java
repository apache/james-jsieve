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
package org.apache.jsieve.tests;

import java.util.ListIterator;

import org.apache.jsieve.Arguments;
import org.apache.jsieve.NumberArgument;
import org.apache.jsieve.SieveException;
import org.apache.jsieve.SyntaxException;
import org.apache.jsieve.TagArgument;
import org.apache.jsieve.mail.MailAdapter;
import org.apache.jsieve.mail.SieveMailException;

/**
 * Class Size implements the Size Test as defined in RFC 3028, section 5.9.
 */
public class Size extends AbstractTest
{

    /**
     * Constructor for Size.
     */
    public Size()
    {
        super();
    }

    /**
     * @see org.apache.jsieve.tests.AbstractTest#executeBasic(MailAdapter, Arguments)
     * <p>From RFC 3028, Section 5.9... </p>
     * <code>  
     *    Syntax: size &lt;&quote;:over"&quote; / &quote;:under&quote;&gt; &lt;limit: number&gt;
     * </code>
     */
    protected boolean executeBasic(MailAdapter mail, Arguments arguments)
        throws SyntaxException, SieveMailException
    {
        String comparator = null;
        Integer size = null;
        ListIterator argumentsIter = arguments.getArgumentList().listIterator();

        // First argument MUST be a tag of ":under" or ":over"
        if (argumentsIter.hasNext())
        {
            Object argument = argumentsIter.next();
            if (argument instanceof TagArgument)
            {
                String tag = ((TagArgument) argument).getTag();
                if (tag.equals(":under") || tag.equals(":over"))
                    comparator = tag;
                else
                    throw new SyntaxException(
                        "Found unexpected TagArgument: \"" + tag + "\"");
            }
        }
        if (null == comparator)
            throw new SyntaxException("Expecting a Tag");

        // Second argument MUST be a number
        if (argumentsIter.hasNext())
        {
            Object argument = argumentsIter.next();
            if (argument instanceof NumberArgument)
                size = ((NumberArgument) argument).getInteger();
        }
        if (null == size)
            throw new SyntaxException("Expecting a Number");

        // There MUST NOT be any further arguments
        if (argumentsIter.hasNext())
            throw new SyntaxException("Found unexpected argument(s)");               

        return test(mail, comparator, size.intValue());
    }

    /**
     * Method test.
     * @param mail
     * @param comparator
     * @param size
     * @return boolean
     * @throws SieveMailException
     */
    protected boolean test(MailAdapter mail, String comparator, int size)
        throws SieveMailException
    {
        boolean isTestPassed = false;
        if (comparator.equals(":over"))
            isTestPassed = testOver(mail, size);
        else if (comparator.equals(":under"))
            isTestPassed = testUnder(mail, size);
        return isTestPassed;
    }

    /**
     * Method testUnder.
     * @param mail
     * @param size
     * @return boolean
     * @throws SieveMailException
     */
    protected boolean testUnder(MailAdapter mail, int size)
        throws SieveMailException
    {
        return mail.getSize() < size;
    }


    /**
     * Method testOver.
     * @param mail
     * @param size
     * @return boolean
     * @throws SieveMailException
     */
    protected boolean testOver(MailAdapter mail, int size)
        throws SieveMailException
    {
        return mail.getSize() > size;
    }



    /**
     * @see org.apache.jsieve.tests.AbstractTest#validateArguments(Arguments)
     */
    protected void validateArguments(Arguments arguments) throws SieveException
    {
        // All done in executeBasic()
    }

}
