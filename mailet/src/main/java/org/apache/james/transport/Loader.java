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


package org.apache.james.transport;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.DefaultServiceManager;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.james.services.FileSystem;
import org.apache.mailet.Mail;
import org.apache.mailet.MailAddress;
import org.apache.mailet.MailetContext;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 *
 * $Id$
 */
public abstract class Loader extends AbstractLogEnabled implements Serviceable, Configurable, Initializable {

    protected String baseDirectory = null;
    protected final String MAILET_PACKAGE = "mailetpackage";
    protected final String MATCHER_PACKAGE = "matcherpackage";
    /**
     * The list of packages that may contain Mailets or matchers
     */
    protected Vector packages;

    /**
     * System service manager
     */
    private ServiceManager serviceManager;

    /**
     * Mailet context
     */
    protected MailetContext mailetContext;

    /**
     * Set the MailetContext
     * 
     * @param mailetContext the MailetContext
     */
    public void setMailetContext(MailetContext mailetContext) {
        this.mailetContext = mailetContext;
    }

    protected void getPackages(Configuration conf, String packageType)
        throws ConfigurationException {
        packages = new Vector();
        packages.addElement("");
        final Configuration[] pkgConfs = conf.getChildren(packageType);
        for (int i = 0; i < pkgConfs.length; i++) {
            Configuration c = pkgConfs[i];
            String packageName = c.getValue();
            if (!packageName.endsWith(".")) {
                packageName += ".";
            }
            packages.addElement(packageName);
        }
    }

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager sm) throws ServiceException {
        serviceManager = new DefaultServiceManager(sm);
        try {
            baseDirectory = ((FileSystem) serviceManager.lookup(FileSystem.ROLE)).getBasedir().getCanonicalPath();
        } catch (FileNotFoundException e) {
            throw new ServiceException(FileSystem.ROLE, "Cannot find the base directory of the application", e);
        } catch (IOException e) {
            throw new ServiceException(FileSystem.ROLE, "Cannot find the base directory of the application", e);
        }
    }

    /**
     * @see org.apache.avalon.framework.activity.Initializable#initialize()
     */
    public void initialize() throws Exception {
        setMailetContext((MailetContext) serviceManager.lookup(MailetContext.class.getName()));
    }
        
    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public abstract void configure(Configuration arg0) throws ConfigurationException;

    /**
     * Wrapper fot a MailetContext that simply override the used logger.
     */
    protected final static class MailetContextWrapper implements MailetContext {
        
        /** the mailetContext */
        private MailetContext mailetContext;
        /** the logger */
        private Logger logger;

        /**
         * Create a mailetContext wrapper that use a different logger for the log
         * operations
         * 
         * @param mailetContext the mailet context to be wrapped
         * @param logger the logger to be used instead of the parent one. 
         */
        public MailetContextWrapper(MailetContext mailetContext, Logger logger) {
            this.mailetContext = mailetContext;
            this.logger = logger;
        }

        /**
         * @see org.apache.mailet.MailetContext#bounce(org.apache.mailet.Mail, java.lang.String)
         */
        public void bounce(Mail mail, String message) throws MessagingException {
            mailetContext.bounce(mail, message);
        }

        /**
         * @see org.apache.mailet.MailetContext#bounce(org.apache.mailet.Mail, java.lang.String, org.apache.mailet.MailAddress)
         */
        public void bounce(Mail mail, String message, MailAddress bouncer) throws MessagingException {
            mailetContext.bounce(mail, message, bouncer);
        }

        /**
         * @see org.apache.mailet.MailetContext#getAttribute(java.lang.String)
         */
        public Object getAttribute(String name) {
            return mailetContext.getAttribute(name);
        }

        /**
         * @see org.apache.mailet.MailetContext#getAttributeNames()
         */
        public Iterator getAttributeNames() {
            return mailetContext.getAttributeNames();
        }

        /**
         * @see org.apache.mailet.MailetContext#getMailServers(java.lang.String)
         */
        public Collection getMailServers(String host) {
            return mailetContext.getMailServers(host);
        }

        /**
         * @see org.apache.mailet.MailetContext#getMajorVersion()
         */
        public int getMajorVersion() {
            return mailetContext.getMajorVersion();
        }

        /**
         * @see org.apache.mailet.MailetContext#getMinorVersion()
         */
        public int getMinorVersion() {
            return mailetContext.getMinorVersion();
        }

        /**
         * @see org.apache.mailet.MailetContext#getPostmaster()
         */
        public MailAddress getPostmaster() {
            return mailetContext.getPostmaster();
        }

        /**
         * @see org.apache.mailet.MailetContext#getSMTPHostAddresses(java.lang.String)
         */
        public Iterator getSMTPHostAddresses(String domainName) {
            return mailetContext.getSMTPHostAddresses(domainName);
        }

        /**
         * @see org.apache.mailet.MailetContext#getServerInfo()
         */
        public String getServerInfo() {
            return mailetContext.getServerInfo();
        }

        /**
         * @see org.apache.mailet.MailetContext#isLocalEmail(org.apache.mailet.MailAddress)
         */
        public boolean isLocalEmail(MailAddress mailAddress) {
            return mailetContext.isLocalEmail(mailAddress);
        }

        /**
         * @see org.apache.mailet.MailetContext#isLocalServer(java.lang.String)
         */
        public boolean isLocalServer(String serverName) {
            return mailetContext.isLocalServer(serverName);
        }

        /**
         * @see org.apache.mailet.MailetContext#isLocalUser(java.lang.String)
         */
        public boolean isLocalUser(String userAccount) {
            return mailetContext.isLocalUser(userAccount);
        }

        /**
         * @see org.apache.mailet.MailetContext#log(java.lang.String)
         */
        public void log(String message) {
            logger.info(message);
        }

        /**
         * @see org.apache.mailet.MailetContext#log(java.lang.String, java.lang.Throwable)
         */
        public void log(String message, Throwable t) {
            logger.info(message, t);
        }

        /**
         * @see org.apache.mailet.MailetContext#removeAttribute(java.lang.String)
         */
        public void removeAttribute(String name) {
            mailetContext.removeAttribute(name);
        }

        /**
         * @see org.apache.mailet.MailetContext#sendMail(javax.mail.internet.MimeMessage)
         */
        public void sendMail(MimeMessage msg) throws MessagingException {
            mailetContext.sendMail(msg);
        }

        /**
         * @see org.apache.mailet.MailetContext#sendMail(org.apache.mailet.MailAddress, java.util.Collection, javax.mail.internet.MimeMessage)
         */
        public void sendMail(MailAddress sender, Collection recipients, MimeMessage msg) throws MessagingException {
            mailetContext.sendMail(sender, recipients, msg);
        }

        /**
         * @see org.apache.mailet.MailetContext#sendMail(org.apache.mailet.MailAddress, java.util.Collection, javax.mail.internet.MimeMessage, java.lang.String)
         */
        public void sendMail(MailAddress sender, Collection recipients, MimeMessage msg, String state) throws MessagingException {
            mailetContext.sendMail(sender, recipients, msg, state);
        }

        /**
         * @see org.apache.mailet.MailetContext#sendMail(org.apache.mailet.Mail)
         */
        public void sendMail(Mail mail) throws MessagingException {
            mailetContext.sendMail(mail);
        }

        /**
         * @see org.apache.mailet.MailetContext#setAttribute(java.lang.String, java.lang.Object)
         */
        public void setAttribute(String name, Object object) {
            mailetContext.setAttribute(name, object);
        }

        /**
         * @see org.apache.mailet.MailetContext#storeMail(MailAddress, MailAddress, MimeMessage)
         */
        public void storeMail(MailAddress sender, MailAddress recipient, MimeMessage msg) throws MessagingException {
            mailetContext.storeMail(sender, recipient, msg);
        }
    }

}
