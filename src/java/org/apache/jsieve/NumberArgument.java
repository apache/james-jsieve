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

import org.apache.jsieve.parser.generated.Token;

/**
 * <p>A parsed representation of the RFC3028 BNF...</p>
 * 
 * <code>1*DIGIT [QUANTIFIER]</code>
 * 
 * <p>Note that the stored value is the absolute value after applying the quantifier.
 * </p>
 * 
 */
public class NumberArgument implements Argument
{
    
    /**
     * The absolute value of the number after applying the quentifier. 
     */ 
    private Integer fieldValue;

    /**
     * Constructor for NumberArgument.
     */
    private NumberArgument()
    {
        super();
    }
    
    /**
     * Constructor for NumberArgument.
     * @param token
     */
    public NumberArgument(Token token)
    {
        this();
        setValue(token);
    }

    /**
     * Sets the value of the reciver to an Integer.
     * @param number The value to set
     */
    protected void setValue(Integer number)
    {
        fieldValue = number;
    }

    /**
     * @see org.apache.jsieve.Argument#getValue()
     */
    public Object getValue()
    {
        return fieldValue;
    }
    
    /**
     * Method getInteger answers the value of the receiver as an Integer.
     * @return Integer
     */
    public Integer getInteger()
    {
        return fieldValue;
    }    

    /**
     * Sets the value of the receiver from a Token.
     * @param aToken The Token from which to extract the value to set
     */
    protected void setValue(Token aToken)
    {
        int endIndex = aToken.image.length();
        int magnitude = 1;
        if (aToken.image.endsWith("K"))
        {
            magnitude = 1024;
            endIndex--;
        }
        else if (aToken.image.endsWith("M"))
        {
            magnitude = 1048576;
            endIndex--;
        }
        else if (aToken.image.endsWith("G"))
        {
            magnitude = 1073741824;
            endIndex--;
        }

        setValue(
            new Integer(
                Integer.parseInt(aToken.image.substring(0, endIndex))
                    * magnitude));
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return (getValue() == null) ? "null" : getValue().toString();
    }

}
