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
package org.apache.jsieve.junit.utils;

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

}
