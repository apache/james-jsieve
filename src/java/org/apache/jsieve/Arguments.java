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

import java.util.ArrayList;
import java.util.List;

/**
 * <p>A parsed representation of the RFC3028 BNF...</p>
 * 
 * <code>arguments = *argument [test / test-list]</code>
 * 
 * <p>Note that a test is represented as a test-list with a single element.</p>
 * 
 */
public class Arguments
{
    /**
     * A List of the parsed Arguments
     */ 
    private List fieldArgumentList;
    
    /**
     * The parsed tests
     */     
    private TestList fieldTestList;

    /**
     * Constructor for Arguments.
     */
    private Arguments()
    {
        super();
    }
    
    /**
     * Constructor for Arguments.
     * @param arguments
     * @param testList
     */
    public Arguments(List arguments, TestList testList)
    {
        this();
        setArgumentList(arguments);
        setTestList(testList);
    }    

    /**
     * Returns the arguments.
     * @return List
     */
    public List getArgumentList()
    {
        return fieldArgumentList;
    }

    /**
     * Returns the testList, lazily initialised if required.
     * @return TestList
     */
    public TestList getTestList()
    {
        TestList testList = null;
        if (null == (testList = getTestListBasic()))
        {
            updateTestList();
            return getTestList();
        }
        return testList;
    }
    
    /**
     * Returns true if there is a TestList and it has Tests.
     * Saves triggering lazy initialisation.
     * @return boolean
     */
    public boolean hasTests()
    {
        TestList testList = getTestListBasic();
        return null != testList && testList.getTests().size() == 0;
    }    
    
    /**
     * Returns the testList.
     * @return TestList
     */
    private TestList getTestListBasic()
    {
        return fieldTestList;
    }
    
    /**
     * Computes the testList.
     * @return TestList
     */
    protected TestList computeTestList()
    {
        return new TestList(new ArrayList());
    }        

    /**
     * Sets the arguments.
     * @param arguments The arguments to set
     */
    protected void setArgumentList(List arguments)
    {
        fieldArgumentList = arguments;
    }

    /**
     * Sets the testList.
     * @param testList The testList to set
     */
    protected void setTestList(TestList testList)
    {
        fieldTestList = testList;
    }
    
    /**
     * Updates the TestList
     */
    protected void updateTestList()
    {
        setTestList(computeTestList());
    }    

    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return "Arguments: "
            + getArgumentList().toString()
            + " Tests: "
            + getTestList().toString();
    }

}
