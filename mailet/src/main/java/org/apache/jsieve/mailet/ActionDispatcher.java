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

package org.apache.jsieve.mailet;

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
     * A Map keyed by the type of Action. The values are the methods to invoke to 
     * handle the Action.
     */ 
    private Map fieldMethodMap;

    /**
     * Constructor for ActionDispatcher.
     * @throws NoSuchMethodException 
     */
    public ActionDispatcher() throws MessagingException
    {
        super();
        setMethodMap(defaultMethodMap());
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
     * Returns the methodMap.
     * @return Map
     */
    public Map getMethodMap()
    {
        return fieldMethodMap;
    }    
    
    /**
     * Returns a new methodMap.
     * @return Map
     */
    private Map defaultMethodMap() throws MessagingException
    {
        try {
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
        } catch (NoSuchMethodException e) {
            throw new MessagingException("Require method missing from action.", e);
        }
    }    

    /**
     * Sets the methodMap.
     * @param methodMap The methodMap to set
     */
    protected void setMethodMap(Map methodMap)
    {
        fieldMethodMap = methodMap;
    }
}
