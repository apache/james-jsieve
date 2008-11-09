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



package org.apache.james.transport.mailets;

import org.apache.avalon.cornerstone.services.store.Store;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.james.Constants;
import org.apache.james.core.MailImpl;
import org.apache.james.services.MailRepository;
import org.apache.james.services.MailServer;
import org.apache.mailet.GenericMailet;
import org.apache.mailet.Mail;
import org.apache.mailet.MailAddress;
import org.apache.mailet.RFC2822Headers;

import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeMessage;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

/**
 * Receives a Mail from JamesSpoolManager and takes care of delivery of the
 * message to local inboxes or a specific repository.
 * 
 * Differently from LocalDelivery this does not lookup the UserRepository This
 * simply store the message in a repository named like the local part of the
 * recipient address or the full recipient address, depending on the configuration
 * (repositorySelector).
 * 
 * If no repository is specified then this fallback to MailServer.getUserInbox.
 * Otherwise you can add your own configuration for the repository
 * 
 * e.g: <repositoryUrl>file://var/spool/userspools/</repositoryUrl>
 * <repositoryType>SPOOL</repositoryType>
 * 
 * <repositoryUrl>file://var/mail/inboxes/</repositoryUrl> <repositoryType>MAIL</repositoryType>
 * 
 * Header "Delivered-To" can be added to every message adding the
 * <addDeliveryHeader>Delivered-To</addDeliveryHeader>
 * 
 * <repositorySelector> defaults to "localpart" and can be changed to "full" if you
 * prefer to use full recipient emails as repository names.
 */
public class ToMultiRepository extends GenericMailet {
    /**
     * The number of mails generated. Access needs to be synchronized for thread
     * safety and to ensure that all threads see the latest value.
     */
    private static int count = 0;
    private static final Object countLock = new Object();

    /**
     * The mailserver reference
     */
    private MailServer mailServer;

    /**
     * The mailstore
     */
    private Store store;

    /**
     * The optional repositoryUrl
     */
    private String repositoryUrl;

    /**
     * The optional repositoryType
     */
    private String repositoryType;
    
    private final static String SELECTOR_LOCALPART = "localpart";
    private final static String SELECTOR_FULL = "full";
    
    /**
     * The optional repositorySelector
     */
    private String repositorySelector;

    /**
     * The delivery header
     */
    private String deliveryHeader;

    /**
     * resetReturnPath
     */
    private boolean resetReturnPath;

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

        MimeMessage message = mail.getMessage();

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

                storeMail(mail.getSender(), recipient, message);

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
            getMailetContext().sendMail(mail.getSender(), errors, mail.getMessage(),
                    Mail.ERROR);
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
        return "ToMultiRepository Mailet";
    }

    /**
     * 
     * @param sender
     * @param recipient
     * @param message
     * @throws MessagingException
     */
    public void storeMail(MailAddress sender, MailAddress recipient,
            MimeMessage message) throws MessagingException {
        String username;
        if (recipient == null) {
            throw new IllegalArgumentException(
                    "Recipient for mail to be spooled cannot be null.");
        }
        if (message == null) {
            throw new IllegalArgumentException(
                    "Mail message to be spooled cannot be null.");
        }
        username = recipient.toString();

        Collection recipients = new HashSet();
        recipients.add(recipient);
        MailImpl mail = new MailImpl(getId(), sender, recipients, message);
        try {
            MailRepository userInbox = getRepository(username);
            if (userInbox == null) {
                StringBuffer errorBuffer = new StringBuffer(128).append(
                        "The repository for user ").append(username).append(
                        " was not found on this server.");
                throw new MessagingException(errorBuffer.toString());
            }
            userInbox.store(mail);
        } finally {
            mail.dispose();
        }
    }

    /**
     * Return a new mail id.
     * 
     * @return a new mail id
     */
    public String getId() {
        final long localCount;
        synchronized (countLock) {
            localCount = count++;
        }
        StringBuffer idBuffer = new StringBuffer(64).append("Mail").append(
                System.currentTimeMillis()).append("-").append(localCount).append('L');
        return idBuffer.toString();
    }

    /**
     * @see org.apache.mailet.GenericMailet#init()
     */
    public void init() throws MessagingException {
        super.init();
        ServiceManager compMgr = (ServiceManager) getMailetContext()
                .getAttribute(Constants.AVALON_COMPONENT_MANAGER);

        try {
            // Instantiate the a MailRepository for outgoing mails
            store = (Store) compMgr.lookup(Store.ROLE);
        } catch (ServiceException cnfe) {
            log("Failed to retrieve Store component:" + cnfe.getMessage());
        } catch (Exception e) {
            log("Failed to retrieve Store component:" + e.getMessage());
        }

        repositoryUrl = getInitParameter("repositoryUrl");
        if (repositoryUrl != null) {
            if (!repositoryUrl.endsWith("/"))
                repositoryUrl += "/";
            repositoryType = getInitParameter("repositoryType");
            if (repositoryType == null)
                repositoryType = "MAIL";
            repositorySelector = getInitParameter("repositorySelector");
            if (repositorySelector == null)
                repositorySelector = SELECTOR_LOCALPART;
            if (!SELECTOR_LOCALPART.equals(repositorySelector) && !SELECTOR_FULL.equals(repositorySelector)) {
                throw new MessagingException("repositorySelector valid options are "+SELECTOR_FULL+" or "+SELECTOR_LOCALPART);
            }
        } else {

            try {
                // Instantiate the a MailRepository for outgoing mails
                mailServer = (MailServer) compMgr.lookup(MailServer.ROLE);
            } catch (ServiceException cnfe) {
                log("Failed to retrieve MailServer component:" + cnfe.getMessage());
            } catch (Exception e) {
                log("Failed to retrieve MailServer component:" + e.getMessage());
            }
            
        }

        deliveryHeader = getInitParameter("addDeliveryHeader");
        String resetReturnPathString = getInitParameter("resetReturnPath");
        resetReturnPath = "true".equalsIgnoreCase(resetReturnPathString);
    }

    /**
     * Get the user inbox: if the repositoryUrl is null then get the userinbox
     * from the mailserver, otherwise lookup the store with the given 
     * repositoryurl/type
     *   
     * @param userName
     */
    private MailRepository getRepository(String userName) {
        MailRepository userInbox;
        if (repositoryUrl == null) {
            userInbox = mailServer.getUserInbox(userName);
        } else {
            if (SELECTOR_LOCALPART.equals(repositorySelector)) {
                // find the username for delivery to that user - localname, ignore the rest
                String[] addressParts = userName.split("@");
                userName = addressParts[0];
            }
                        
            StringBuffer destinationBuffer = new StringBuffer(192).append(
            repositoryUrl).append(userName).append("/");
            String destination = destinationBuffer.toString();
            DefaultConfiguration mboxConf = new DefaultConfiguration(
                    "repository", "generated:ToMultiRepository.getUserInbox()");
            mboxConf.setAttribute("destinationURL", destination);
            mboxConf.setAttribute("type", repositoryType);
            try {
                userInbox = (MailRepository) store.select(mboxConf);
            } catch (Exception e) {
                log("Cannot open repository " + e);
                userInbox = null;
            }
        }
        return userInbox;
    }

}
