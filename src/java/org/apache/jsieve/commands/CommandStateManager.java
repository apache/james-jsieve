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
     * @param CommandStateManager The CommandStateManager to set
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
        fieldInstance = null;
    }    
    

    /**
     * Updates the current CommandStateManager.
     * @param CommandStateManager - The CommandStateManager to set
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
