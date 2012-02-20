/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */

package org.apache.james.managesieve.mailet;

import java.util.Collection;
import java.util.Iterator;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.mailet.Mail;
import org.apache.mailet.MailAddress;
import org.apache.mailet.MailetContext;

/**
 * <code>MockMailetContext</code>
 */
public class MockMailetContext implements MailetContext {
    
    private MimeMessage _message = null;

    /**
     * @see org.apache.mailet.MailetContext#bounce(org.apache.mailet.Mail, java.lang.String)
     */
    public void bounce(Mail mail, String s) throws MessagingException {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.apache.mailet.MailetContext#bounce(org.apache.mailet.Mail, java.lang.String, org.apache.mailet.MailAddress)
     */
    public void bounce(Mail mail, String s, MailAddress mailaddress) throws MessagingException {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.apache.mailet.MailetContext#getAttribute(java.lang.String)
     */
    public Object getAttribute(String s) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.apache.mailet.MailetContext#getAttributeNames()
     */
    @SuppressWarnings("unchecked")
    public Iterator getAttributeNames() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.apache.mailet.MailetContext#getMailServers(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public Collection getMailServers(String s) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.apache.mailet.MailetContext#getMajorVersion()
     */
    public int getMajorVersion() {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * @see org.apache.mailet.MailetContext#getMinorVersion()
     */
    public int getMinorVersion() {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * @see org.apache.mailet.MailetContext#getPostmaster()
     */
    public MailAddress getPostmaster() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.apache.mailet.MailetContext#getSMTPHostAddresses(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public Iterator getSMTPHostAddresses(String s) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.apache.mailet.MailetContext#getServerInfo()
     */
    public String getServerInfo() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.apache.mailet.MailetContext#isLocalEmail(org.apache.mailet.MailAddress)
     */
    public boolean isLocalEmail(MailAddress mailaddress) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.apache.mailet.MailetContext#isLocalServer(java.lang.String)
     */
    public boolean isLocalServer(String s) {
        return s.equals("localhost");
    }

    /**
     * @see org.apache.mailet.MailetContext#isLocalUser(java.lang.String)
     */
    public boolean isLocalUser(String s) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.apache.mailet.MailetContext#log(java.lang.String)
     */
    public void log(String s) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.apache.mailet.MailetContext#log(java.lang.String, java.lang.Throwable)
     */
    public void log(String s, Throwable throwable) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.apache.mailet.MailetContext#removeAttribute(java.lang.String)
     */
    public void removeAttribute(String s) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.apache.mailet.MailetContext#sendMail(javax.mail.internet.MimeMessage)
     */
    public void sendMail(MimeMessage mimemessage) throws MessagingException {
        _message = mimemessage;

    }

    /**
     * @see org.apache.mailet.MailetContext#sendMail(org.apache.mailet.Mail)
     */
    public void sendMail(Mail mail) throws MessagingException {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.apache.mailet.MailetContext#sendMail(org.apache.mailet.MailAddress, java.util.Collection, javax.mail.internet.MimeMessage)
     */
    @SuppressWarnings("unchecked")
    public void sendMail(MailAddress mailaddress, Collection collection, MimeMessage mimemessage)
            throws MessagingException {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.apache.mailet.MailetContext#sendMail(org.apache.mailet.MailAddress, java.util.Collection, javax.mail.internet.MimeMessage, java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public void sendMail(MailAddress mailaddress, Collection collection, MimeMessage mimemessage,
            String s) throws MessagingException {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.apache.mailet.MailetContext#setAttribute(java.lang.String, java.lang.Object)
     */
    public void setAttribute(String s, Object obj) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.apache.mailet.MailetContext#storeMail(org.apache.mailet.MailAddress, org.apache.mailet.MailAddress, javax.mail.internet.MimeMessage)
     */
    public void storeMail(MailAddress mailaddress, MailAddress mailaddress1, MimeMessage mimemessage)
            throws MessagingException {
        // TODO Auto-generated method stub

    }

    /**
     * @return the message
     */
    public MimeMessage getMessage() {
        return _message;
    }

}
