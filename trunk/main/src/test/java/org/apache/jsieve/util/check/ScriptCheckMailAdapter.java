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

package org.apache.jsieve.util.check;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;

import org.apache.jsieve.SieveContext;
import org.apache.jsieve.exception.SieveException;
import org.apache.jsieve.mail.Action;
import org.apache.jsieve.mail.MailAdapter;
import org.apache.jsieve.mail.MailUtils;
import org.apache.jsieve.mail.SieveMailException;
import org.apache.jsieve.parser.address.SieveAddressBuilder;

/**
 * Checks script execution for an email. The wrapped email is set by called
 * {@link #setMail}. Actions are recorded on {@link #executedActions} and can
 * be retrieved by {@link #getExecutedActions()}.
 */
public class ScriptCheckMailAdapter implements MailAdapter {

    private final List<Action> actions;

    private final List<Action> executedActions;

    private Message mail = null;

    public ScriptCheckMailAdapter() {
        actions = new ArrayList<Action>();
        executedActions = new ArrayList<Action>();
    }

    /**
     * Gets the wrapped email.
     * 
     * @return <code>Message</code>, possibly null
     */
    public Message getMail() {
        return mail;
    }

    /**
     * Sets the wrapped email and {@link #reset}s the adapter ready for another
     * execution.
     * 
     * @param mail
     *            <code>Message</code>, possibly null
     */
    public void setMail(Message mail) {
        this.mail = mail;
        reset();
    }

    /**
     * Method addAction adds an Action to the List of Actions to be performed by
     * the receiver.
     * 
     * @param action
     */
    public void addAction(final Action action) {
        actions.add(action);
    }

    /**
     * Method executeActions. Applies the Actions accumulated by the receiver.
     */
    public void executeActions() throws SieveException {
        executedActions.clear();
        executedActions.addAll(actions);
    }

    /**
     * Gets the actions accumulated when {@link #executedActions} was last
     * called.
     * 
     * @return <code>List</code> of {@link Action}s, not null. This list is a
     *         modifiable copy
     */
    public List<Action> getExecutedActions() {
        final ArrayList<Action> result = new ArrayList<Action>(executedActions);
        return result;
    }

    /**
     * Method getActions answers the List of Actions accumulated by the
     * receiver. Implementations may elect to supply an unmodifiable collection.
     * 
     * @return <code>List</code> of {@link Action}'s, not null, possibly
     *         unmodifiable
     */
    public List<Action> getActions() {
        final List<Action> result = Collections.unmodifiableList(actions);
        return result;
    }

    /**
     * Resets executed and accumlated actions. An instance may be safely reused
     * to check a script once this method has been called.
     */
    public void reset() {
        executedActions.clear();
        actions.clear();
    }

    /**
     * Method getHeader answers a List of all of the headers in the receiver
     * whose name is equal to the passed name. If no headers are found an empty
     * List is returned.
     * 
     * @param name
     * @return <code>List</code> not null, possibly empty
     * @throws SieveMailException
     */
    @SuppressWarnings("unchecked")
    public List<String> getHeader(String name) throws SieveMailException {
        List<String> result = Collections.EMPTY_LIST;
        if (mail != null) {
            try {
                String[] values = mail.getHeader(name);
                if (values != null) {
                    result = Arrays.asList(values);
                }
            } catch (MessagingException e) {
                throw new SieveMailException(e);
            }
        }
        return result;
    }

    /**
     * Method getHeaderNames answers a List of all of the headers in the
     * receiver. No duplicates are allowed.
     * 
     * @return <code>List</code>, not null possible empty, possible
     *         unmodifiable
     * @throws SieveMailException
     */
    @SuppressWarnings("unchecked")
    public List<String> getHeaderNames() throws SieveMailException {
        List<String> results = Collections.EMPTY_LIST;
        if (mail != null) {
            try {
                results = new ArrayList<String>();
                for (final Enumeration en = mail.getAllHeaders(); en
                        .hasMoreElements();) {
                    final Header header = (Header) en.nextElement();
                    final String name = header.getName();
                    if (!results.contains(name)) {
                        results.add(name);
                    }
                }
            } catch (MessagingException e) {
                throw new SieveMailException(e);
            }
        }
        return results;
    }

    /**
     * <p>
     * Method getMatchingHeader answers a List of all of the headers in the
     * receiver with the passed name. If no headers are found an empty List is
     * returned.
     * </p>
     * 
     * <p>
     * This method differs from getHeader(String) in that it ignores case and
     * the whitespace prefixes and suffixes of a header name when performing the
     * match, as required by RFC 3028. Thus "From", "from ", " From" and " from "
     * are considered equal.
     * </p>
     * 
     * @param name
     * @return <code>List</code>, not null possibly empty
     * @throws SieveMailException
     */
    @SuppressWarnings("unchecked")
    public List<String> getMatchingHeader(String name) throws SieveMailException {
        List<String> result = Collections.EMPTY_LIST;
        if (mail != null) {
            result = MailUtils.getMatchingHeader(this, name);
        }
        return result;
    }

    /**
     * Method getSize answers the receiver's message size in octets.
     * 
     * @return int
     * @throws SieveMailException
     */
    public int getSize() throws SieveMailException {
        int result = 0;
        if (mail != null) {
            try {
                result = mail.getSize();
            } catch (MessagingException e) {
                throw new SieveMailException(e);
            }
        }
        return result;
    }

    /**
     * Method getContentType returns string/mime representation of the message
     * type.
     * 
     * @return String
     * @throws SieveMailException
     */
    public String getContentType() throws SieveMailException {
        String result = null;
        if (mail != null) {
            try {
                result = mail.getContentType();
            } catch (MessagingException e) {
                throw new SieveMailException(e);
            }
        }
        return result;
    }

    public boolean isInBodyText(String phraseCaseInsensitive) throws SieveMailException {
        boolean result = false;
        if (mail != null) {
            try {
                result = mail.getContent().toString().toLowerCase().contains(phraseCaseInsensitive);
            } catch (MessagingException e) {
                throw new SieveMailException(e);
            } catch (IOException e) {
                throw new SieveMailException(e);
            }
        }
        return result;
    }

    public Address[] parseAddresses(String headerName)
            throws SieveMailException {
        return parseAddresses(headerName, mail);
    }

    /**
     * Parses the value from the given message into addresses.
     * 
     * @param headerName
     *            header name, to be matched case insensitively
     * @param message
     *            <code>Message</code>, not null
     * @return <code>Address</code> array, not null possibly empty
     * @throws SieveMailException
     */
    public Address[] parseAddresses(final String headerName,
            final Message message) throws SieveMailException {
        try {
            final SieveAddressBuilder builder = new SieveAddressBuilder();

            for (Enumeration en = message.getAllHeaders(); en.hasMoreElements();) {
                final Header header = (Header) en.nextElement();
                final String name = header.getName();
                if (name.trim().equalsIgnoreCase(headerName)) {
                    builder.addAddresses(header.getValue());
                }
            }

            final Address[] results = builder.getAddresses();
            return results;

        } catch (MessagingException ex) {
            throw new SieveMailException(ex);
        } catch (org.apache.jsieve.parser.generated.address.ParseException ex) {
            throw new SieveMailException(ex);
        }
    }

    public void setContext(SieveContext context) {}

}
