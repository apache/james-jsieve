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

/**
 * Thread singleton class ConditionManager manages Conditional Commands during a
 * Sieve evaluation.
 */
public class ConditionManager {

    /**
     * Is an Else Condition allowed
     */
    private boolean fieldElseAllowed;

    /**
     * The result of the last Test
     */
    private boolean fieldTestResult;

    /**
     * Constructor for ConditionManager.
     */
    public ConditionManager() {
        super();
        initialize();
    }

    /**
     * Initialize the receiver.
     */
    protected void initialize() {
        setElseAllowed(false);
        setTestResult(true);
    }

    /**
     * Method setIfTestResult enables a following Else Command and records the
     * test result.
     * 
     * @param result
     */
    public void setIfTestResult(boolean result) {
        setElseAllowed(true);
        setTestResult(result);
    }

    /**
     * Method setElsifTestResult enables a following Else Command and records
     * the test result.
     * 
     * @param result
     */
    public void setElsifTestResult(boolean result) {
        setElseAllowed(true);
        setTestResult(result);
    }

    /**
     * Method setElseTestResult disables a following Else Command and records
     * the test result.
     * 
     * @param result
     */
    public void setElseTestResult(boolean result) {
        setElseAllowed(false);
        setTestResult(result);
    }

    /**
     * Method isIfAllowed answers a boolean indicating if an If Command is
     * allowed.
     * 
     * @return boolean
     */
    public boolean isIfAllowed() {
        return true;
    }

    /**
     * Method isElsifAllowed answers a boolean indicating if an Elsif Command is
     * allowed.
     * 
     * @return boolean
     */
    public boolean isElsifAllowed() {
        return isElseAllowed();
    }

    /**
     * Method isElseAllowed answers a boolean indicating if an Else Command is
     * allowed.
     * 
     * @return boolean
     */
    public boolean isElseAllowed() {
        return fieldElseAllowed;
    }

    /**
     * Method isIfRunnable answers a boolean indicating if an If Command is
     * runnable based upon the current evaluation state.
     * 
     * @return boolean
     */
    public boolean isIfRunnable() {
        return true;
    }

    /**
     * Method isElsifRunnable answers a boolean indicating if an Elsif Command
     * is runnable based upon the current evaluation state.
     * 
     * @return boolean
     */
    public boolean isElsifRunnable() {
        return isElseRunnable();
    }

    /**
     * Method isElseRunnable answers a boolean indicating if an Else Command is
     * runnable based upon the current evaluation state.
     * 
     * @return boolean
     */
    public boolean isElseRunnable() {
        return !isTestResult();
    }

    /**
     * Returns the testResult.
     * 
     * @return boolean
     */
    protected boolean isTestResult() {
        return fieldTestResult;
    }

    /**
     * Sets the elseAllowed.
     * 
     * @param elseAllowed
     *            The elseAllowed to set
     */
    protected void setElseAllowed(boolean elseAllowed) {
        fieldElseAllowed = elseAllowed;
    }

    /**
     * Sets the testResult.
     * 
     * @param testResult
     *            The testResult to set
     */
    protected void setTestResult(boolean testResult) {
        fieldTestResult = testResult;
    }

}
