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

import java.util.Collection;
import java.util.Date;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.mailet.MailAddress;
import org.joda.time.DateTime;

/**
 * Provides context for action execution.
 */
public interface ActionContext {

    /**
     * @return Date the script was activated
     */
    DateTime getScriptActivationDate();

    /**
     * @return Date the script is currently interpreted
     */
    DateTime getScriptInterpretationDate();

    /**
     * @return Recipient receiving the given eMail
     */
    MailAddress getRecipient();

    /**
     * Gets the log.
     * @return not null
     */
    public Log getLog();
    
    /**
     * Experimental mail delivery. 
     * POST verb indicate that mail should be attached to the collection
     * indicated by the given URI.
     * 
     * @param uri indicates the destination to which the mail to added. ATM 
     * the value should be mailbox://<user>@localhost/<mailbox-path>
     * @param mail not null
     */
    public void post(String uri, MimeMessage mail) throws MessagingException;

    /**
     * Posts the given mail.
     * @param sender possibly null
     * @param recipients not null
     * @param mail not null
     * @throws MessagingException when mail cannot be posted
     */
    public void post(MailAddress sender, Collection<MailAddress> recipients, MimeMessage mail) throws MessagingException;

    /**
     * Gets name (including version) of this server.
     * @return not nul
     */
    public String getServerInfo();
}
