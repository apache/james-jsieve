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

package org.apache.jsieve.samples.james.junit.utils;

import java.util.Collection;
import java.util.Iterator;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.mailet.Mail;
import org.apache.mailet.MailAddress;
import org.apache.mailet.MailetContext;

/**
 * Class <code>MockMailetContext</code>.
 */
public class MockMailetContext implements MailetContext
{

    /**
     * Constructor for MockMailetContext.
     */
    public MockMailetContext()
    {
        super();
    }

    /**
     * @see org.apache.mailet.MailetContext#bounce(Mail, String)
     */
    public void bounce(Mail mail, String message) throws MessagingException
    {
    }

    /**
     * @see org.apache.mailet.MailetContext#bounce(Mail, String, MailAddress)
     */
    public void bounce(Mail mail, String message, MailAddress bouncer)
        throws MessagingException
    {
    }

    /**
     * @see org.apache.mailet.MailetContext#getMailServers(String)
     */
    public Collection getMailServers(String host)
    {
        return null;
    }

    /**
     * @see org.apache.mailet.MailetContext#getPostmaster()
     */
    public MailAddress getPostmaster()
    {
        return null;
    }

    /**
     * @see org.apache.mailet.MailetContext#getAttribute(String)
     */
    public Object getAttribute(String name)
    {
        return null;
    }

    /**
     * @see org.apache.mailet.MailetContext#getAttributeNames()
     */
    public Iterator getAttributeNames()
    {
        return null;
    }

    /**
     * @see org.apache.mailet.MailetContext#getMajorVersion()
     */
    public int getMajorVersion()
    {
        return 0;
    }

    /**
     * @see org.apache.mailet.MailetContext#getMinorVersion()
     */
    public int getMinorVersion()
    {
        return 0;
    }

    /**
     * @see org.apache.mailet.MailetContext#getServerInfo()
     */
    public String getServerInfo()
    {
        return null;
    }

    /**
     * @see org.apache.mailet.MailetContext#isLocalServer(String)
     */
    public boolean isLocalServer(String serverName)
    {
        return false;
    }

    /**
     * @see org.apache.mailet.MailetContext#isLocalUser(String)
     */
    public boolean isLocalUser(String userAccount)
    {
        return false;
    }

    /**
     * @see org.apache.mailet.MailetContext#log(String)
     */
    public void log(String message)
    {
    }

    /**
     * @see org.apache.mailet.MailetContext#log(String, Throwable)
     */
    public void log(String message, Throwable t)
    {
    }

    /**
     * @see org.apache.mailet.MailetContext#removeAttribute(String)
     */
    public void removeAttribute(String name)
    {
    }

    /**
     * @see org.apache.mailet.MailetContext#sendMail(MimeMessage)
     */
    public void sendMail(MimeMessage msg) throws MessagingException
    {
    }

    /**
     * @see org.apache.mailet.MailetContext#sendMail(MailAddress, Collection, MimeMessage)
     */
    public void sendMail(
        MailAddress sender,
        Collection recipients,
        MimeMessage msg)
        throws MessagingException
    {
    }

    /**
     * @see org.apache.mailet.MailetContext#sendMail(MailAddress, Collection, MimeMessage, String)
     */
    public void sendMail(
        MailAddress sender,
        Collection recipients,
        MimeMessage msg,
        String state)
        throws MessagingException
    {
    }

    /**
     * @see org.apache.mailet.MailetContext#sendMail(Mail)
     */
    public void sendMail(Mail mail) throws MessagingException
    {
    }

    /**
     * @see org.apache.mailet.MailetContext#setAttribute(String, Object)
     */
    public void setAttribute(String name, Object object)
    {
    }

    /**
     * @see org.apache.mailet.MailetContext#storeMail(MailAddress, MailAddress, MimeMessage)
     */
    public void storeMail(
        MailAddress sender,
        MailAddress recipient,
        MimeMessage msg)
        throws MessagingException
    {
    }

    /**
     * @see org.apache.mailet.MailetContext#getSMTPHostAddresses(java.lang.String)
     */
    public Iterator getSMTPHostAddresses(String arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }
}
