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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeMessage;

import org.apache.jsieve.ConfigurationManager;
import org.apache.jsieve.SieveConfigurationException;
import org.apache.jsieve.SieveFactory;
import org.apache.jsieve.mail.MailAdapter;
import org.apache.mailet.base.GenericMailet;
import org.apache.mailet.Mail;
import org.apache.mailet.MailAddress;
import org.apache.mailet.MailetConfig;
import org.apache.mailet.MailetException;
import org.apache.mailet.base.RFC2822Headers;

/**
 * <p>Executes a <a href='http://www.rfc-editor.org/rfc/rfc3028.txt'>Sieve</a>
 * script against incoming mail. The script applied is based on the recipient.</p>
 */
public class SieveMailboxMailet extends GenericMailet {

    /**
     * The delivery header
     */
    private String deliveryHeader;

    /**
     * resetReturnPath
     */
    private boolean resetReturnPath;
    /** Experimental */
    private Poster poster;
    /** Experimental */
    private ResourceLocator locator;
    
    private SieveFactory factory;

    /**
     * For SDI
     */
    public SieveMailboxMailet() {}
    
    /**
     * CDI
     * @param poster not null
     */
    public SieveMailboxMailet(Poster poster, ResourceLocator locator) {
        this();
        this.poster = poster;
        this.locator = locator;
    }

    
    public ResourceLocator getLocator() {
        return locator;
    }

    /**
     * For SDI
     * @param locator not null
     */
    public void setLocator(ResourceLocator locator) {
        this.locator = locator;
    }

    public final Poster getPoster() {
        return poster;
    }
    
    /**
     * For SDI
     * @param poster not null
     */
    public final void setPoster(Poster poster) {
        this.poster = poster;
    }

    //@Override
    public void init(MailetConfig config) throws MessagingException {
        
        super.init(config);

        try {
            factory = new ConfigurationManager().build();
        } catch (SieveConfigurationException e) {
            throw new MessagingException("Failed to load standard Sieve configuration.", e);
        }
    }

    /**
     * Delivers a mail to a local mailbox.
     * 
     * @param mail
     *            the mail being processed
     * 
     * @throws MessagingException
     *             if an error occurs while storing the mail
     */
    public void service(Mail mail) throws MessagingException {
        Collection recipients = mail.getRecipients();
        Collection errors = new Vector();

        MimeMessage message = null;
        if (deliveryHeader != null || resetReturnPath) {
            message = mail.getMessage();
        }

        if (resetReturnPath) {
            // Set Return-Path and remove all other Return-Path headers from the
            // message
            // This only works because there is a placeholder inserted by
            // MimeMessageWrapper
            message.setHeader(RFC2822Headers.RETURN_PATH,
                    (mail.getSender() == null ? "<>" : "<" + mail.getSender()
                            + ">"));
        }

        Enumeration headers;
        InternetHeaders deliveredTo = new InternetHeaders();
        if (deliveryHeader != null) {
            // Copy any Delivered-To headers from the message
            headers = message
                    .getMatchingHeaders(new String[] { deliveryHeader });
            while (headers.hasMoreElements()) {
                Header header = (Header) headers.nextElement();
                deliveredTo.addHeader(header.getName(), header.getValue());
            }
        }

        for (Iterator i = recipients.iterator(); i.hasNext();) {
            MailAddress recipient = (MailAddress) i.next();
            try {
                if (deliveryHeader != null) {
                    // Add qmail's de facto standard Delivered-To header
                    message.addHeader(deliveryHeader, recipient.toString());
                }

                storeMail(mail.getSender(), recipient, mail);

                if (deliveryHeader != null) {
                    if (i.hasNext()) {
                        // Remove headers but leave all placeholders
                        message.removeHeader(deliveryHeader);
                        headers = deliveredTo.getAllHeaders();
                        // And restore any original Delivered-To headers
                        while (headers.hasMoreElements()) {
                            Header header = (Header) headers.nextElement();
                            message.addHeader(header.getName(), header
                                    .getValue());
                        }
                    }
                }
            } catch (Exception ex) {
                getMailetContext().log("Error while storing mail.", ex);
                errors.add(recipient);
            }
        }

        if (!errors.isEmpty()) {
            // If there were errors, we redirect the email to the ERROR
            // processor.
            // In order for this server to meet the requirements of the SMTP
            // specification, mails on the ERROR processor must be returned to
            // the sender. Note that this email doesn't include any details
            // regarding the details of the failure(s).
            // In the future we may wish to address this.
            getMailetContext().sendMail(mail.getSender(), errors,
                    mail.getMessage(), Mail.ERROR);
        }
        // We always consume this message
        mail.setState(Mail.GHOST);
    }

    /**
     * Return a string describing this mailet.
     * 
     * @return a string describing this mailet
     */
    public String getMailetInfo() {
        return "Sieve Mailbox Mailet";
    }

    /**
     * 
     * @param sender
     * @param recipient
     * @param mail
     * @throws MessagingException
     */
    public void storeMail(MailAddress sender, MailAddress recipient,
            Mail mail) throws MessagingException {
        String username;
        if (recipient == null) {
            throw new IllegalArgumentException(
                    "Recipient for mail to be spooled cannot be null.");
        }
        if (mail.getMessage() == null) {
            throw new IllegalArgumentException(
                    "Mail message to be spooled cannot be null.");
        }
        // recipient.toString was used here (JD)
        username = recipient.getUser();
        
        sieveMessage(username, mail);
 
    }
    
    void sieveMessage(String username, Mail aMail) throws MessagingException {
        // Evaluate the script against the mail
        String relativeUri = "//" + username + "@" + "localhost/sieve"; 
        try
        {
            final InputStream ins = locator.get(relativeUri);
            MailAdapter aMailAdapter = new SieveMailAdapter(aMail,
                    getMailetContext());
            log("Evaluating " + aMailAdapter.toString() + "against \""
                    + relativeUri + "\"");
            factory.evaluate(aMailAdapter, factory.parse(ins));
        }
        catch (Exception ex)
        {
            //
            // SLIEVE is a mail filtering protocol.
            // Rejecting the mail because it cannot be filtered
            // seems very unfriendly.
            // So just log and store in INBOX.
            //
            log("Cannot evaluate Sieve script. Storing mail in user INBOX.", ex);
            storeMessageInbox(username, aMail);
        }
    }
    
    void storeMessageInbox(String username, Mail mail) throws MessagingException {
        String url = "mailbox://" + username + "@localhost/";
        poster.post(url, mail.getMessage());
    }

    /**
     * @see org.apache.mailet.GenericMailet#init()
     */
    public void init() throws MessagingException {
        super.init();
        if (poster == null) {
            throw new MailetException("Not initialised. Please ensure that the mailet container supports either" +
                    " setter or constructor injection");
        }
        
        deliveryHeader = getInitParameter("addDeliveryHeader");
        String resetReturnPathString = getInitParameter("resetReturnPath");
        resetReturnPath = "true".equalsIgnoreCase(resetReturnPathString);
    }
}
