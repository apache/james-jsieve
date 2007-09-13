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

package org.apache.jsieve.samples.james;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.apache.jsieve.SieveException;
import org.apache.jsieve.mail.Action;
import org.apache.jsieve.mail.MailAdapter;
import org.apache.jsieve.mail.MailUtils;
import org.apache.jsieve.mail.SieveMailException;
import org.apache.jsieve.mail.optional.EnvelopeAccessors;
import org.apache.jsieve.parser.address.SieveAddressBuilder;
import org.apache.mailet.Mail;
import org.apache.mailet.MailAddress;
import org.apache.mailet.MailetContext;
/**
 * <p>
 * Class <code>SieveMailAdapter</code> implements a <code>MailAdapter</code>
 * for use in a Mailet environment.
 * </p>
 */
public class SieveMailAdapter implements MailAdapter, EnvelopeAccessors
{
    /**
     * The Mail being adapted.
     */
    private Mail fieldMail;
    /**
     * The MailetContext.
     */
    private MailetContext fieldMailetContext;
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
     * 
     * @param aMail
     * @param aMailetContext
     */
    public SieveMailAdapter(Mail aMail, MailetContext aMailetContext)
    {
        this();
        setMail(aMail);
        setMailetContext(aMailetContext);
    }
    /**
     * Returns the message.
     * 
     * @return MimeMessage
     */
    protected MimeMessage getMessage() throws MessagingException
    {
        return getMail().getMessage();
    }
    /**
     * Returns the List of actions.
     * 
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
     * 
     * @return List
     */
    protected List computeActions()
    {
        return new ArrayList();
    }
    /**
     * Returns the List of actions.
     * 
     * @return List
     */
    private List getActionsBasic()
    {
        return fieldActions;
    }
    /**
     * Adds an Action.
     * 
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
        //        Log log = Logger.getLog();
        //        boolean isDebugEnabled = log.isDebugEnabled();
        ListIterator actionsIter = getActionsIterator();
        while (actionsIter.hasNext())
        {
            Action action = (Action) actionsIter.next();
            getMailetContext().log("Executing action: " + action.toString());
            try
            {
                ActionDispatcher.getInstance().execute(action, getMail(),
                        getMailetContext());
            }
            catch (NoSuchMethodException e)
            {
                throw new SieveException(e.getMessage());
            }
            catch (IllegalAccessException e)
            {
                throw new SieveException(e.getMessage());
            }
            catch (InvocationTargetException e)
            {
                throw new SieveException(e.getMessage());
            }
            catch (MessagingException e)
            {
                throw new SieveException(e.getMessage());
            }
        }
    }
    /**
     * Sets the actions.
     * 
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
            return (headers == null ? new ArrayList(0) : Arrays.asList(headers));
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
    public List getMatchingHeader(String name) throws SieveMailException
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
     * Method getEnvelopes.
     * 
     * @return Map
     */
    protected Map getEnvelopes()
    {
        Map envelopes = new HashMap(2);
        if (null != getEnvelopeFrom())
            envelopes.put("From", getEnvelopeFrom());
        if (null != getEnvelopeTo())
            envelopes.put("To", getEnvelopeTo());
        return envelopes;
    }
    /**
     * @see org.apache.jsieve.mail.optional.EnvelopeAccessors#getEnvelope(String)
     */
    public List getEnvelope(String name) throws SieveMailException
    {
        List values = new ArrayList(1);
        Object value = getEnvelopes().get(name);
        if (null != value)
            values.add(value);
        return values;
    }
    /**
     * @see org.apache.jsieve.mail.optional.EnvelopeAccessors#getEnvelopeNames()
     */
    public List getEnvelopeNames() throws SieveMailException
    {
        return new ArrayList(getEnvelopes().keySet());
    }
    /**
     * @see org.apache.jsieve.mail.optional.EnvelopeAccessors#getMatchingEnvelope(String)
     */
    public List getMatchingEnvelope(String name) throws SieveMailException
    {
        Iterator envelopeNamesIter = getEnvelopeNames().iterator();
        List matchedEnvelopeValues = new ArrayList(32);
        while (envelopeNamesIter.hasNext())
        {
            String envelopeName = (String) envelopeNamesIter.next();
            if (envelopeName.trim().equalsIgnoreCase(name))
                matchedEnvelopeValues.addAll(getEnvelope(envelopeName));
        }
        return matchedEnvelopeValues;
    }
    /**
     * Returns the from.
     * 
     * @return String
     */
    public String getEnvelopeFrom()
    {
        MailAddress sender = getMail().getSender(); 
        return (null == sender ? "" : sender.toString());
    }
    /**
     * Returns the sole recipient or null if there isn't one.
     * 
     * @return String
     */
    public String getEnvelopeTo()
    {
        String recipient = null;
        Iterator recipientIter = getMail().getRecipients().iterator();
        if (recipientIter.hasNext())
            recipient = (String) recipientIter.next().toString();
        return recipient;
    }
    /**
     * Returns the mail.
     * 
     * @return Mail
     */
    public Mail getMail()
    {
        return fieldMail;
    }
    /**
     * Sets the mail.
     * 
     * @param mail The mail to set
     */
    protected void setMail(Mail mail)
    {
        fieldMail = mail;
    }
    /**
     * Returns the mailetContext.
     * 
     * @return MailetContext
     */
    public MailetContext getMailetContext()
    {
        return fieldMailetContext;
    }
    /**
     * Sets the mailetContext.
     * 
     * @param mailetContext The mailetContext to set
     */
    protected void setMailetContext(MailetContext mailetContext)
    {
        fieldMailetContext = mailetContext;
    }
    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        String messageID = null;
        try
        {
            messageID = getMail().getMessage().getMessageID();
        }
        catch (MessagingException e)
        {
            messageID = "<" + e.getMessage() + ">";
        }
        return getClass().getName() + " Envelope From: "
                + (null == getEnvelopeFrom() ? "null" : getEnvelopeFrom())
                + " Envelope To: "
                + (null == getEnvelopeTo() ? "null" : getEnvelopeTo())
                + " Message ID: " + (null == messageID ? "null" : messageID);
    }
    public Object getContent() throws SieveMailException {
        // TODO Auto-generated method stub
        return null;
    }
    public String getContentType() throws SieveMailException {
        // TODO Auto-generated method stub
        return null;
    }
    public Address[] parseAddresses(String headerName) throws SieveMailException {
        try {
            return SieveAddressBuilder.parseAddresses(headerName, getMail().getMessage());
        } catch (MessagingException e) {
            throw new SieveMailException(e);
        }
    }
}
