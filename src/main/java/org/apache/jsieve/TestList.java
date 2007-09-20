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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.jsieve.exception.SieveException;
import org.apache.jsieve.mail.*;

/**
 * <p>A parsed representation of an RFC3028 testlist argument...</p>
 * 
 * <code>test-list = "(" test *("," test) ")"</code>
 */
public class TestList implements Executable
{
    /**
     * List of Tests
     */ 
    private List fieldTests;

    /**
     * Constructor for TestList.
     */
    private TestList()
    {
        super();
    }
    
    /**
     * Constructor for TestList.
     * @param children - A List of Tests
     */
    public TestList(List children)
    {
        this();
        setTests(children);
    }
    
    /**
     * Constructor for TestList.
     * @param child - A Test
     */
    public TestList(Test child)
    {
        this();
        List children = new ArrayList();
        children.add(child);
        setTests( children);
    }        

    /**
     * @see org.apache.jsieve.Executable#execute(MailAdapter)
     */
    public Object execute(MailAdapter mail) throws SieveException
    {
        boolean result = true;

        Iterator testsIter = getTests().iterator();
        while (result && testsIter.hasNext())
        {
            result =
                ((Boolean) ((Test) testsIter.next()).execute(mail)).booleanValue();
        }
        return new Boolean(result);
    }

    /**
     * Returns the children.
     * @return List
     */
    public List getTests()
    {
        return fieldTests;
    }

    /**
     * Sets the children.
     * @param children The children to set
     */
    protected void setTests(List children)
    {
        fieldTests = children;
    }

    public String toString() {
        return "TEST LIST: " + fieldTests;
    }

    
}
