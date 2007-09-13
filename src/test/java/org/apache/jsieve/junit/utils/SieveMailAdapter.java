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

 
package org.apache.jsieve.junit.utils;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;

import org.apache.jsieve.Logger;
import org.apache.jsieve.SieveException;
import org.apache.jsieve.mail.Action;
import org.apache.jsieve.mail.MailAdapter;
import org.apache.jsieve.mail.MailUtils;
import org.apache.jsieve.mail.SieveMailException;
import org.apache.jsieve.parser.address.SieveAddressBuilder;
import org.apache.jsieve.parser.generated.address.ParseException;

/**
 * <p>Class SieveMailAdapter implements a mock MailAdapter for testing purposes.</p>
 * 
 * <p>Being a mock object, Actions are not performed against a mail server, but in 
 * most other respects it behaves as would expect a MailAdapter wrapping a JavaMail 
 * message should. To this extent, it is a useful demonstration of how to create an
 * implementation of a MailAdapter.
 */
public class SieveMailAdapter implements MailAdapter
{
    /*
     * The message being adapted.
     */ 
    private MimeMessage fieldMessage;
    
    /**
     * List of Actions to perform.
     */ 
    private List fieldActions;

    /**
     * Constructor for SieveMailAdapter.
     */
    private SieveMailAdapter()
    {
        super();
    }
    
    /**
     * Constructor for SieveMailAdapter.
     * @param message
     */
    public SieveMailAdapter(MimeMessage message)
    {
        this();
        setMessage(message);
    }    

    /**
     * Returns the message.
     * @return MimeMessage
     */
    public MimeMessage getMessage()
    {
        return fieldMessage;
    }

    /**
     * Sets the message.
     * @param message The message to set
     */
    protected void setMessage(MimeMessage message)
    {
        fieldMessage = message;
    }

    /**
     * Returns the List of actions.
     * @return List
     */
    public List getActions()
    {
        List actions = null;
        if (null == (actions = getActionsBasic()))
        {
            updateActions();
            return getActions();
        }    
        return actions;
    }
    
    /**
     * Returns a new List of actions.
     * @return List
     */
    protected List computeActions()
    {
        return new ArrayList();
    }    
    
    /**
     * Returns the List of actions.
     * @return List
     */
    private List getActionsBasic()
    {
        return fieldActions;
    }    

    /**
     * Adds an Action.
     * @param action The action to set
     */
    public void addAction(Action action)
    {
        getActions().add(action);
    }
    
    /**
     * @see org.apache.jsieve.mail.MailAdapter#executeActions()
     */
    public void executeActions() throws SieveException
    {
        Log log = Logger.getLog();
        boolean isDebugEnabled = log.isDebugEnabled();
        ListIterator actionsIter = getActionsIterator();
        while (actionsIter.hasNext())
        {
            Action action = (Action) actionsIter.next();
            if (isDebugEnabled)
                log.debug("Executing " + action.toString());
        }

    }

    /**
     * Sets the actions.
     * @param actions The actions to set
     */
    protected void setActions(List actions)
    {
        fieldActions = actions;
    }
    
    /**
     * Updates the actions.
     */
    protected void updateActions()
    {
        setActions(computeActions());
    }    

    /**
     * @see org.apache.jsieve.mail.MailAdapter#getActionsIterator()
     */
    public ListIterator getActionsIterator()
    {
        return getActions().listIterator();
    }

    /**
     * @see org.apache.jsieve.mail.MailAdapter#getHeader(String)
     */
    public List getHeader(String name) throws SieveMailException
    {
        try
        {
            String[] headers = getMessage().getHeader(name);
            return (
                headers == null ? new ArrayList(0) : Arrays.asList(headers));
        }
        catch (MessagingException ex)
        {
            throw new SieveMailException(ex);
        }
    }

    /**
     * @see org.apache.jsieve.mail.MailAdapter#getHeaderNames()
     */
    public List getHeaderNames() throws SieveMailException
    {
        Set headerNames = new HashSet();
        try
        {
            Enumeration allHeaders = getMessage().getAllHeaders();
            while (allHeaders.hasMoreElements())
            {
                headerNames.add(((Header) allHeaders.nextElement()).getName());
            }
            return new ArrayList(headerNames);
        }
        catch (MessagingException ex)
        {
            throw new SieveMailException(ex);
        }
    }

    /**
     * @see org.apache.jsieve.mail.MailAdapter#getMatchingHeader(String)
     */
    public List getMatchingHeader(String name)
        throws SieveMailException
    {
        return MailUtils.getMatchingHeader(this, name);
    }

    /**
     * @see org.apache.jsieve.mail.MailAdapter#getSize()
     */
    public int getSize() throws SieveMailException
    {
        try
        {
            return getMessage().getSize();
        }
        catch (MessagingException ex)
        {
            throw new SieveMailException(ex);
        }
    }

    /**
     * @see org.apache.jsieve.mail.MailAdapter#getContentType()
     */
    public String getContentType() throws SieveMailException
    {
        try
        {
            return getMessage().getContentType();
        }
        catch (MessagingException ex)
        {
            throw new SieveMailException(ex);
        }
    }

    /**
     * @see org.apache.jsieve.mail.MailAdapter#getContent()
     */
    public Object getContent() throws SieveMailException
    {
        try
        {
            return getMessage().getContent();
        }
        catch (MessagingException ex)
        {
            throw new SieveMailException(ex);
        }
        catch (IOException ex)
        {
            throw new SieveMailException(ex);
        }
    }

    public Address[] parseAddresses(final String headerName) throws SieveMailException {
         return SieveAddressBuilder.parseAddresses(headerName, getMessage());
    }



}
