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
    
    /*
     * Is an Else Condition allowed
     */ 
    private boolean fieldElseAllowed;
    
    /*
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
