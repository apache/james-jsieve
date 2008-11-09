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

package org.apache.james.transport.mailets.sieve;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;

import org.apache.jsieve.mail.Action;
import org.apache.jsieve.mail.ActionFileInto;
import org.apache.jsieve.mail.ActionKeep;
import org.apache.jsieve.mail.ActionRedirect;
import org.apache.jsieve.mail.ActionReject;
import org.apache.mailet.Mail;
import org.apache.mailet.MailetContext;

/**
 * Singleton Class <code>ActionDispatcher</code> dynamically dispatches 
 * an Action depending on the type of Action received at runtime. 
 */
public class ActionDispatcher
{
    /**
     * The sole instance of the receiver. 
     */
    static private ActionDispatcher fieldInstance;
    
    /**
     * A Map keyed by the type of Action. The values are the methods to invoke to 
     * handle the Action.
     */ 
    private Map fieldMethodMap;

    /**
     * Constructor for ActionDispatcher.
     */
    private ActionDispatcher()
    {
        super();
    }

    /**
     * Returns the sole instance of the receiver, lazily initialised.
     * @return ActionDispatcher
     */
    public static synchronized ActionDispatcher getInstance()
    {
        ActionDispatcher instance = null;
        if (null == (instance = getInstanceBasic()))
        {
            updateInstance();
            return getInstance();
        }    
        return instance;
    }
    
    /**
     * Returns the sole instance of the receiver.
     * @return ActionDispatcher
     */
    private static ActionDispatcher getInstanceBasic()
    {
        return fieldInstance;
    }    
    
    /**
     * Returns a new instance of the receiver.
     * @return ActionDispatcher
     */
    protected static ActionDispatcher computeInstance()
    {
        return new ActionDispatcher();
    }    

    /**
     * Sets the instance.
     * @param instance The instance to set
     */
    protected static void setInstance(ActionDispatcher instance)
    {
        fieldInstance = instance;
    }
  
    
    /**
     * Resets the instance.
     */
    public static void resetInstance()
    {
        setInstance(null);
    }    
    
    /**
     * Updates the instance.
     */
    protected static void updateInstance()
    {
        setInstance(computeInstance());
    }
    
    /**
     * Method execute executes the passed Action by invoking the method mapped by the
     * receiver with a parameter of the EXACT type of Action.
     * @param anAction
     * @param aMail
     * @param aMailetContext
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws MessagingException
     */
    public void execute(
        Action anAction,
        Mail aMail,
        MailetContext aMailetContext)
        throws
            NoSuchMethodException,
            IllegalAccessException,
            InvocationTargetException,
            MessagingException
    {
        Method actionMethod = (Method) getMethodMap().get(anAction.getClass());
        if (null == actionMethod)
            throw new NoSuchMethodException(
                "Method accepting parameters ("
                    + anAction.getClass().getName()
                    + ", "
                    + aMail.getClass().getName()
                    + ", "
                    + aMailetContext.getClass().getName()
                    + ") not mapped.");
        actionMethod.invoke(
            null,
            new Object[] { anAction, aMail, aMailetContext });
    }
    
    /**
     * Returns the methodMap, lazily initialised.
     * @return Map
     * @throws NoSuchMethodException
     */
    protected synchronized Map getMethodMap() throws NoSuchMethodException
    {
        Map methodMap = null;
        if (null == (methodMap = getMethodMapBasic()))
        {
            updateMethodMap();
            return getMethodMap();
        }    
        return methodMap;
    }
    
    /**
     * Returns the methodMap.
     * @return Map
     */
    private Map getMethodMapBasic()
    {
        return fieldMethodMap;
    }    
    
    /**
     * Returns a new methodMap.
     * @return Map
     */
    protected Map computeMethodMap() throws NoSuchMethodException
    {
        Map methodNameMap = new HashMap();
        methodNameMap.put(
            ActionFileInto.class,
            Actions.class.getMethod(
                "execute",
                new Class[] {
                    ActionFileInto.class,
                    Mail.class,
                    MailetContext.class }));
        methodNameMap.put(
            ActionKeep.class,
            Actions.class.getMethod(
                "execute",
                new Class[] {
                    ActionKeep.class,
                    Mail.class,
                    MailetContext.class }));
        methodNameMap.put(
            ActionRedirect.class,
            Actions.class.getMethod(
                "execute",
                new Class[] {
                    ActionRedirect.class,
                    Mail.class,
                    MailetContext.class }));
        methodNameMap.put(
            ActionReject.class,
            Actions.class.getMethod(
                "execute",
                new Class[] {
                    ActionReject.class,
                    Mail.class,
                    MailetContext.class }));
        return methodNameMap;
    }    

    /**
     * Sets the methodMap.
     * @param methodMap The methodMap to set
     */
    protected void setMethodMap(Map methodMap)
    {
        fieldMethodMap = methodMap;
    }
    
    /**
     * Updates the methodMap.
     * @throws NoSuchMethodException
     */
    protected void updateMethodMap() throws NoSuchMethodException
    {
        setMethodMap(computeMethodMap());
    }

}
