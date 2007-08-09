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
 * Thread singleton class CommandStateManager records the state of a Sieve evaluation.
 */
public class CommandStateManager
{
    
    /**
     * The evaluated script is processing Prolog Commands
     */ 
    private boolean fieldInProlog = true;
    
    /**
     * The evaluated script has rejected the mail
     */ 
    private boolean fieldRejected = false;
    
    /**
     * The evaluated script must keep the mail
     */ 
    private boolean fieldImplicitKeep = true;    
    
    /**
     * The evaluation has processed Action Commands  
     */ 
    private boolean fieldHasActions = false;
    
    /**
     * The instance of the reciever for the current thread
     */     
    static private ThreadLocal fieldInstance;
    
    /**
     * Constructor for CommandStateManager.
     */
    private CommandStateManager()
    {
        super();
        initialize();
    }
    
    /**
     * Initialize the receiver.
     */
    protected void initialize()
    {
        setInProlog(true);
        setRejected(false);
        setHasActions(false);
        setImplicitKeep(true);         
    }    
    
    /**
     * Answers a new instance of the receiver.
     * @return ConditionManager
     */
    static protected CommandStateManager computeInstance()
    {
        return new CommandStateManager();
    }    


    /**
     * <p>Returns an instance of the receiver for the current thread, lazily 
     * intialised if required.</p>
     * 
     * <p>Note that this must be synchronized to prevent another thread 
     * detecting the null state while this thread is initialising.</p>
     * 
     * @return ConditionManager
     */
    static synchronized public CommandStateManager getInstance()
    {
        CommandStateManager instance = null;
        if (null == (instance = getInstanceBasic()))
        {
            updateInstance();
            return getInstance();
        }
        return instance;
    }
    

    /**
     * Returns the current CommandStateManager for the current thread.
     * @return CommandStateManager
     */
    static private CommandStateManager getInstanceBasic()
    {
        if (null == fieldInstance)
            return null;
        return (CommandStateManager)fieldInstance.get();
    }    
    

    /**
     * Sets the CommandStateManager for the current thread.
     * @param conditionManager The CommandStateManager to set
     */
    static protected void setInstance(CommandStateManager conditionManager)
    {
        if (null == fieldInstance)
            fieldInstance = new ThreadLocal();
        fieldInstance.set(conditionManager);
    }
    
    /**
     * resets the current CommandStateManager.
     */
    static public void resetInstance()
    {
        setInstance(null);
    }    
    

    /**
     * Updates the current CommandStateManager.
     */
    static protected void updateInstance()
    {
        setInstance(computeInstance());
    }    


    

    /**
     * Returns the hasActions.
     * @return boolean
     */
    public boolean isHasActions()
    {
        return fieldHasActions;
    }

    /**
     * Returns the inProlog.
     * @return boolean
     */
    public boolean isInProlog()
    {
        return fieldInProlog;
    }

    /**
     * Returns the isRejected.
     * @return boolean
     */
    public boolean isRejected()
    {
        return fieldRejected;
    }

    /**
     * Sets the hasActions.
     * @param hasActions The hasActions to set
     */
    protected void setHasActions(boolean hasActions)
    {
        fieldHasActions = hasActions;
    }

    /**
     * Sets the inProlog.
     * @param inProlog The inProlog to set
     */
    protected void setInProlog(boolean inProlog)
    {
        fieldInProlog = inProlog;
    }

    /**
     * Sets the isRejected.
     * @param isRejected The isRejected to set
     */
    public void setRejected(boolean isRejected)
    {
        fieldRejected = isRejected;
    }

    /**
     * Returns the implicitKeep.
     * @return boolean
     */
    public boolean isImplicitKeep()
    {
        return fieldImplicitKeep;
    }

    /**
     * Sets the implicitKeep.
     * @param implicitKeep The implicitKeep to set
     */
    protected void setImplicitKeep(boolean implicitKeep)
    {
        fieldImplicitKeep = implicitKeep;
    }

}
