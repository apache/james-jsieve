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
import java.util.List;

/**
 * <p>
 * A parsed representation of the RFC3028 BNF...
 * </p>
 * 
 * <code>arguments = *argument [test / test-list]</code>
 * 
 * <p>
 * Note that a test is represented as a test-list with a single element.
 * </p>
 * 
 */
public class Arguments {
    /**
     * A List of the parsed Arguments
     */
    private List<Argument> fieldArgumentList;

    /**
     * The parsed tests
     */
    private TestList fieldTestList;

    /**
     * Constructor for Arguments.
     */
    private Arguments() {
        super();
    }

    /**
     * Constructor for Arguments.
     * 
     * @param arguments
     * @param testList
     */
    public Arguments(List<Argument> arguments, TestList testList) {
        this();
        setArgumentList(arguments);
        setTestList(testList);
    }

    /**
     * Returns the arguments.
     * 
     * @return List
     */
    public List<Argument> getArgumentList() {
        return fieldArgumentList;
    }

    /**
     * Returns the testList, lazily initialised if required.
     * 
     * @return TestList
     */
    public TestList getTestList() {
        TestList testList = null;
        if (null == (testList = getTestListBasic())) {
            updateTestList();
            return getTestList();
        }
        return testList;
    }

    /**
     * Returns true if there is a TestList and it has Tests. Saves triggering
     * lazy initialisation.
     * 
     * @return boolean
     */
    public boolean hasTests() {
        TestList testList = getTestListBasic();
        return null != testList && !testList.isEmpty();
    }

    /**
     * Returns the testList.
     * 
     * @return TestList
     */
    private TestList getTestListBasic() {
        return fieldTestList;
    }

    /**
     * Computes the testList.
     * 
     * @return TestList
     */
    protected TestList computeTestList() {
        return new TestList(new ArrayList<Test>());
    }

    /**
     * Sets the arguments.
     * 
     * @param arguments
     *            The arguments to set
     */
    protected void setArgumentList(List<Argument> arguments) {
        fieldArgumentList = arguments;
    }

    /**
     * Sets the testList.
     * 
     * @param testList
     *            The testList to set
     */
    protected void setTestList(TestList testList) {
        fieldTestList = testList;
    }

    /**
     * Updates the TestList
     */
    protected void updateTestList() {
        setTestList(computeTestList());
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Arguments: " + getArgumentList().toString() + " Tests: "
                + (hasTests() ? getTestList().toString() : "null");
    }

}
