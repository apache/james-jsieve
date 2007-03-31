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


package org.apache.jsieve.commands;

/**
 * Thread singleton class ConditionManager manages Conditional Commands during a
 * Sieve evaluation.
 */
public class ConditionManager
{
    /**
     * The Condition Manager instance for the current thread
     */     
    static private ThreadLocal fieldInstance;
    
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
    private ConditionManager()
    {
        super();
        initialize();
    }
    
    /**
     * Initialize the receiver.
     */
    protected void initialize()
    {
        setElseAllowed(false);
        setTestResult(true);        
    }    
    
    /**
     * Method setIfTestResult enables a following Else Command and records the test 
     * result.
     * @param result
     */
    public void setIfTestResult(boolean result)
    {
        setElseAllowed(true);
        setTestResult(result);    
    } 

    /**
     * Method setElsifTestResult enables a following Else Command and records the 
     * test result.
     * @param result
     */    
    public void setElsifTestResult(boolean result)
    {
        setElseAllowed(true);
        setTestResult(result);                   
    } 

    /**
     * Method setElseTestResult disables a following Else Command and records the 
     * test result.
     * @param result
     */     
    public void setElseTestResult(boolean result)
    {
        setElseAllowed(false);
        setTestResult(result);                   
    }         
    
    /**
     * Method isIfAllowed answers a boolean indicating if an If Command is allowed.
     * @return boolean
     */
    public boolean isIfAllowed()
    {
        return true;
    } 
    
    /**
     * Method isElsifAllowed answers a boolean indicating if an Elsif Command is
     * allowed.
     * @return boolean
     */
    public boolean isElsifAllowed()
    {
        return isElseAllowed();        
    } 
    
    /**
     * Method isElseAllowed answers a boolean indicating if an Else Command is
     * allowed.
     * @return boolean
     */
    public boolean isElseAllowed()
    {
        return fieldElseAllowed;          
    }

    /**
     * Method isIfRunnable answers a boolean indicating if an If Command is
     * runnable based upon the current evaluation state.
     * @return boolean
     */    
    public boolean isIfRunnable()
    {
        return true;
    }

    /**
     * Method isElsifRunnable answers a boolean indicating if an Elsif Command is
     * runnable based upon the current evaluation state.
     * @return boolean
     */    
    public boolean isElsifRunnable()
    {
        return isElseRunnable();
    }

    /**
     * Method isElseRunnable answers a boolean indicating if an Else Command is
     * runnable based upon the current evaluation state.
     * @return boolean
     */     
    public boolean isElseRunnable()
    {
        return !isTestResult();        
    }                             
     

    /**
     * Answers a new instance of the reciver.
     * @return ConditionManager
     */
    static protected ConditionManager computeInstance()
    {
        return new ConditionManager();
    }    


    /**
     * <p>Returns the conditionManager, lazily intialised if required.</p>
     * 
     * <p>Note that this must be synchronized to prevent another thread 
     * detecting the null state while this thread is initialising.</p>
     * 
     * @return ConditionManager
     */
    static synchronized public ConditionManager getInstance()
    {
        ConditionManager instance = null;
        if (null == (instance = getInstanceBasic()))
        {
            updateInstance();
            return getInstance();
        }
        return instance;
    }
    

    /**
     * Returns the current conditionManager.
     * @return ConditionManager
     */
    static private ConditionManager getInstanceBasic()
    {
        if (null == fieldInstance)
            return null;
        return (ConditionManager)fieldInstance.get();
    }    
    

    /**
     * Sets the current conditionManager.
     * @param conditionManager The conditionManager to set
     */
    static protected void setInstance(ConditionManager conditionManager)
    {
        if (null == fieldInstance)
            fieldInstance = new ThreadLocal();
        fieldInstance.set(conditionManager);
    }
    
    /**
     * resets the current conditionManager.
     */
    static public void resetInstance()
    {
        fieldInstance = null;
    }    
    

    /**
     * Updates the current conditionManager.
     */
    static protected void updateInstance()
    {
        setInstance(computeInstance());
    }      

    /**
     * Returns the testResult.
     * @return boolean
     */
    protected boolean isTestResult()
    {
        return fieldTestResult;
    }

    /**
     * Sets the elseAllowed.
     * @param elseAllowed The elseAllowed to set
     */
    protected void setElseAllowed(boolean elseAllowed)
    {
        fieldElseAllowed = elseAllowed;
    }

    /**
     * Sets the testResult.
     * @param testResult The testResult to set
     */
    protected void setTestResult(boolean testResult)
    {
        fieldTestResult = testResult;
    }

}
