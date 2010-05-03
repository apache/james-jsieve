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

package org.apache.jsieve.javaxmail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeMessage;
import javax.mail.search.SearchTerm;

/**
 * Forked from JAMES server.
 */
public class MockMimeMessage extends MimeMessage {

    private final List m_fromAddresses = new ArrayList();

    private Address m_senderAddress;

    private final List m_toRecepients = new ArrayList();

    private final List m_ccRecepients = new ArrayList();

    private final List m_bccRecepients = new ArrayList();

    private final List m_replyToAddresses = new ArrayList();

    private String m_subject;

    private int m_iMessageNumber;

    private boolean m_bIsExpunged;

    private Object m_content;

    private Date m_sentDate;

    private String[] m_contentLanguage;

    private String m_fileName;

    private DataHandler m_dataHandler;

    private HashMap m_contentHeaders = new HashMap();

    private Flags m_setFlags = new Flags();

    private boolean m_doMatch;

    private int m_size = -1;

    public MockMimeMessage() throws MessagingException {
        super((Session) null);
    }

    public MockMimeMessage(int messageNumber) throws MessagingException {
        super((Session) null);
        m_iMessageNumber = messageNumber;
    }

    public MockMimeMessage(MimeMessage mimeMessage) throws MessagingException {
        super(mimeMessage); // trivial implementation
    }

    public Address[] getFrom() throws MessagingException {
        return (Address[]) m_fromAddresses.toArray();
    }

    public void setFrom(Address address) throws MessagingException {
        m_fromAddresses.clear();
        m_fromAddresses.add(address);
    }

    public void setFrom() throws MessagingException {
        m_fromAddresses.clear();
        m_fromAddresses.add(InternetAddress.getLocalAddress(null));
    }

    public void addFrom(Address[] addresses) throws MessagingException {
        m_fromAddresses.add(addresses);
    }

    public Address getSender() throws MessagingException {
        return m_senderAddress;
    }

    public void setSender(Address address) throws MessagingException {
        m_senderAddress = address;
    }

    public Address[] getRecipients(Message.RecipientType recipientType)
            throws MessagingException {
        List recipientsList = getRecipientsList(recipientType);
        List recipientAddresses = new ArrayList();
        for (Iterator iterator = recipientsList.iterator(); iterator.hasNext();) {
            String recipient = (String) iterator.next();
            recipientAddresses.add(new InternetAddress(recipient));
        }
        return (Address[]) (recipientAddresses.toArray(new Address[] {}));
    }

    private List getRecipientsList(Message.RecipientType recipientType) {
        if (Message.RecipientType.TO.equals(recipientType))
            return m_toRecepients;
        if (Message.RecipientType.CC.equals(recipientType))
            return m_ccRecepients;
        if (Message.RecipientType.BCC.equals(recipientType))
            return m_bccRecepients;
        return null;
    }

    public Address[] getAllRecipients() throws MessagingException {
        List allRecipients = new ArrayList();
        allRecipients.addAll(m_toRecepients);
        allRecipients.addAll(m_ccRecepients);
        allRecipients.addAll(m_bccRecepients);
        return (Address[]) allRecipients.toArray();
    }

    public void setRecipients(Message.RecipientType recipientType,
            Address[] addresses) throws MessagingException {
        getRecipientsList(recipientType).addAll(Arrays.asList(addresses));
    }

    public void setRecipients(Message.RecipientType recipientType,
            String recipient) throws MessagingException {
        getRecipientsList(recipientType).add(recipient);
    }

    public void addRecipients(Message.RecipientType recipientType,
            Address[] addresses) throws MessagingException {
        getRecipientsList(recipientType).addAll(Arrays.asList(addresses));
    }

    public void addRecipients(Message.RecipientType recipientType,
            String recipient) throws MessagingException {
        getRecipientsList(recipientType).add(recipient);
    }

    public Address[] getReplyTo() throws MessagingException {
        return (Address[]) m_replyToAddresses.toArray();
    }

    public void setReplyTo(Address[] addresses) throws MessagingException {
        m_replyToAddresses.addAll(Arrays.asList(addresses));
    }

    public String getSubject() throws MessagingException {
        return m_subject;
    }

    public void setSubject(String subject) throws MessagingException {
        m_subject = subject;
    }

    public void setSubject(String subject, String charset)
            throws MessagingException {
        if (subject == null) {
            m_subject = null;
            return;
        }
        try {
            m_subject = new String(subject.getBytes(charset));
        } catch (UnsupportedEncodingException e) {
            throw new MessagingException("setting subject failed", e);
        }
    }

    public Date getSentDate() throws MessagingException {
        return m_sentDate;
    }

    public void setSentDate(Date date) throws MessagingException {
        m_sentDate = date;
    }

    public Date getReceivedDate() throws MessagingException {
        return null; // trivial implementation
    }

    public int getSize() throws MessagingException {
        return m_size; // trivial implementation
    }

    public int getLineCount() throws MessagingException {
        return -1; // trivial implementation
    }

    public String getContentType() throws MessagingException {
        return getHeader("Content-Type", null);
    }

    public boolean isMimeType(String mimeType) throws MessagingException {
        return mimeType.startsWith(getContentType());
    }

    public String getDisposition() throws MessagingException {
        return getHeader("Content-Disposition", null);
    }

    public void setDisposition(String disposition) throws MessagingException {
        setHeader("Content-Disposition", disposition);
    }

    public String getEncoding() throws MessagingException {
        return getHeader("Content-Transfer-Encoding", null);
    }

    public String getContentID() throws MessagingException {
        return getHeader("Content-ID", null);
    }

    public void setContentID(String contentID) throws MessagingException {
        setHeader("Content-ID", contentID);
    }

    public String getContentMD5() throws MessagingException {
        return getHeader("Content-MD5", null);
    }

    public void setContentMD5(String value) throws MessagingException {
        setHeader("Content-MD5", value);
    }

    public String getDescription() throws MessagingException {
        return getHeader("Content-Description", null);
    }

    public void setDescription(String description) throws MessagingException {
        setHeader("Content-Description", description);
    }

    public void setDescription(String description, String charset)
            throws MessagingException {
        try {
            setDescription(new String(description.getBytes(charset)));
        } catch (UnsupportedEncodingException e) {
            throw new MessagingException("setting description failed", e);
        }
    }

    public String[] getContentLanguage() throws MessagingException {
        return m_contentLanguage;
    }

    public void setContentLanguage(String[] contentLanguage)
            throws MessagingException {
        m_contentLanguage = contentLanguage;
    }

    public String getMessageID() throws MessagingException {
        return "ID-" + m_iMessageNumber; // trivial implementation
    }

    public String getFileName() throws MessagingException {
        return m_fileName;
    }

    public void setFileName(String fileName) throws MessagingException {
        m_fileName = fileName;
    }

    public InputStream getInputStream() throws IOException, MessagingException {
        return null; // trivial implementation
    }

    protected InputStream getContentStream() throws MessagingException {
        return null; // trivial implementation
    }

    public InputStream getRawInputStream() throws MessagingException {
        if (m_content instanceof String) {
            return new ByteArrayInputStream(m_content.toString().getBytes());
        }
        throw new UnsupportedOperationException("Unimplementated method");
    }

    public synchronized DataHandler getDataHandler() throws MessagingException {
        return m_dataHandler;
    }

    public synchronized void setDataHandler(DataHandler dataHandler)
            throws MessagingException {
        m_dataHandler = dataHandler;
    }

    public Object getContent() throws IOException, MessagingException {
        return m_content;
    }

    public void setContent(Object object, String mimeType)
            throws MessagingException {
        m_content = object; // trivial implementation
    }

    public void setText(String string) throws MessagingException {
        setContent(string, "text/plain");
    }

    public void setText(String string, String charset)
            throws MessagingException {
        try {
            setContent(new String(string.getBytes(charset)), "text/plain");
        } catch (UnsupportedEncodingException e) {
            throw new MessagingException("setting text content failed", e);
        }
    }

    public void setContent(Multipart multipart) throws MessagingException {
        m_content = multipart;
    }

    public Message reply(boolean b) throws MessagingException {
        return new MockMimeMessage(this); // trivial implementation
    }

    public void writeTo(OutputStream outputStream) throws IOException,
            MessagingException {
        ; // trivial implementation
    }

    public void writeTo(OutputStream outputStream, String[] strings)
            throws IOException, MessagingException {
        ; // trivial implementation
    }

    public String[] getHeader(String name) throws MessagingException {
        Object value = m_contentHeaders.get(name);
        if (value == null)
            return null;
        if (value instanceof String) {
            String stringValue = (String) value;
            return new String[] { stringValue };
        } else {
            Collection values = (Collection) value;
            return (String[]) values.toArray(new String[values.size()]);
        }
    }

    public String getHeader(String name, String delimiter)
            throws MessagingException {
        String[] header = getHeader(name);
        if (header == null || header.length == 0)
            return null;
        return header[0];
    }

    public void setHeader(String name, String value) throws MessagingException {
        addHeader(name, value);
    }

    public void addHeader(String name, String value) throws MessagingException {
        Object newValue;
        Object existingValue = m_contentHeaders.get(name);
        if (existingValue == null) {
            newValue = value;
        } else if (existingValue instanceof String) {
            List values = new ArrayList();
            values.add(existingValue);
            values.add(value);
            newValue = values;
        } else {
            List values = (List) existingValue;
            values.add(value);
            newValue = values;
        }
        m_contentHeaders.put(name, newValue);
    }

    public void removeHeader(String name) throws MessagingException {
        m_contentHeaders.remove(name);
    }

    public Enumeration getAllHeaders() throws MessagingException {
        final Collection results = new ArrayList();
        final Collection entries = m_contentHeaders.entrySet();
        for (Iterator it = entries.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            String name = entry.getKey().toString();
            Object value = entry.getValue();
            if (value == null) {
                // ignore
            } else if (value instanceof Collection) {
                Collection values = (Collection) value;
                for (Iterator iterValues = values.iterator(); iterValues
                        .hasNext();) {
                    String stringValue = (String) iterValues.next();
                    results.add(new Header(name, stringValue));
                }
            } else {
                results.add(new Header(name, value.toString()));
            }
        }
        return Collections.enumeration(results);
    }

    public Enumeration getMatchingHeaders(String[] names)
            throws MessagingException {
        ArrayList matchingHeaders = new ArrayList();
        for (int i = 0; i < names.length; i++) {
            String name = names[i];
            String value = getHeader(name, null);
            if (value == null)
                continue;
            matchingHeaders.add(value);
        }
        return Collections.enumeration(matchingHeaders);
    }

    public Enumeration getNonMatchingHeaders(String[] names)
            throws MessagingException {
        List existingHeaders = Arrays.asList(names);

        ArrayList nonMatchingHeaders = new ArrayList();

        Iterator iterator = m_contentHeaders.keySet().iterator();
        while (iterator.hasNext()) {
            String name = (String) iterator.next();
            if (existingHeaders.contains(name))
                continue;
            String value = getHeader(name, null);
            if (value == null)
                continue;
            nonMatchingHeaders.add(value);
        }
        return Collections.enumeration(nonMatchingHeaders);
    }

    public void addHeaderLine(String headerLine) throws MessagingException {
        int separatorIndex = headerLine.indexOf(":");
        if (separatorIndex < 0)
            throw new MessagingException(
                    "header line does not conform to standard");

        addHeader(headerLine.substring(0, separatorIndex), headerLine
                .substring(separatorIndex, headerLine.length()));
    }

    public Enumeration getAllHeaderLines() throws MessagingException {
        return Collections.enumeration(getHeadersAsStrings(m_contentHeaders));
    }

    private ArrayList getHeadersAsStrings(HashMap contentHeaders) {
        ArrayList headerLines = new ArrayList();
        Iterator iterator = contentHeaders.keySet().iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            String value = (String) contentHeaders.get(key);
            headerLines.add(key + ":" + value);
        }
        return headerLines;
    }

    public Enumeration getMatchingHeaderLines(String[] names)
            throws MessagingException {
        ArrayList matchingHeaders = new ArrayList();
        for (int i = 0; i < names.length; i++) {
            String name = names[i];
            String value = getHeader(name, null);
            if (value == null)
                continue;
            matchingHeaders.add(name + ":" + value);
        }
        return Collections.enumeration(matchingHeaders);
    }

    public Enumeration getNonMatchingHeaderLines(String[] names)
            throws MessagingException {
        List existingHeaders = names != null ? Arrays.asList(names) : null;

        ArrayList nonMatchingHeaders = new ArrayList();

        Iterator iterator = m_contentHeaders.keySet().iterator();
        while (iterator.hasNext()) {
            String name = (String) iterator.next();
            if (existingHeaders != null && existingHeaders.contains(name))
                continue;
            String value = getHeader(name, null);
            if (value == null)
                continue;
            nonMatchingHeaders.add(name + ":" + value);
        }
        return Collections.enumeration(nonMatchingHeaders);
    }

    public synchronized Flags getFlags() throws MessagingException {
        return new Flags(m_setFlags);
    }

    public synchronized boolean isSet(Flags.Flag flag)
            throws MessagingException {
        return m_setFlags.contains(flag);
    }

    public synchronized void setFlags(Flags flags, boolean set)
            throws MessagingException {
        if (set)
            m_setFlags.add(flags);
        else
            m_setFlags.remove(flags);
    }

    public void saveChanges() throws MessagingException {
        ; // trivial implementation
    }

    protected void updateHeaders() throws MessagingException {
        ; // trivial implementation
    }

    protected InternetHeaders createInternetHeaders(InputStream inputStream)
            throws MessagingException {
        return new InternetHeaders();
    }

    public void setRecipient(Message.RecipientType recipientType,
            Address address) throws MessagingException {
        setRecipients(recipientType, new Address[] { address });
    }

    public void addRecipient(Message.RecipientType recipientType,
            Address address) throws MessagingException {
        setRecipients(recipientType, new Address[] { address });
    }

    public void setFlag(Flags.Flag flag, boolean set) throws MessagingException {
        if (set)
            m_setFlags.add(flag);
        else
            m_setFlags.remove(flag);
    }

    public int getMessageNumber() {
        return m_iMessageNumber;
    }

    protected void setMessageNumber(int i) {
        m_iMessageNumber = i;
    }

    public Folder getFolder() {
        return null;
    }

    public boolean isExpunged() {
        return m_bIsExpunged;
    }

    protected void setExpunged(boolean b) {
        m_bIsExpunged = b;
    }

    public void setShouldMatch(boolean doMatch) {
        m_doMatch = doMatch;
    }

    public boolean match(SearchTerm searchTerm) throws MessagingException {
        return m_doMatch;
    }

    public void setSize(int size) {
        this.m_size = size;
    }
}
