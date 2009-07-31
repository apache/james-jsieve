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

package org.apache.jsieve.mail;

import java.util.List;

import org.apache.jsieve.SieveContext;
import org.apache.jsieve.exception.InternetAddressException;
import org.apache.jsieve.exception.SieveException;

/**
 * <p>
 * Interface <code>MailAdapter</code> defines the minimum functionality
 * required of of a class implementing a mail message. This is the functionality
 * neccesary to implement the Commands and Tests that RFC32028 mandates MUST be
 * implemented.
 * </p>
 * 
 * <p>
 * Typically, implementations will wrap an application's pre-existing mail
 * message implementation. It is expected that implementations will extend the
 * minimum level of functionality to provide support for Command and Test
 * extensions that exploit the capabilities of a particular application.
 * </p>
 * 
 * <h4>Implementing parseAddresses</h4>
 * <p>
 * <a href='http://james.apache.org/mime4j'>Apache Mime4J</a> is a parser for
 * <abbr title='Multipurpose Internet Mail Extensions'> <a
 * href='http://www.faqs.org/rfcs/rfc2045.html'>MIME</a></abbr>. It can easily
 * be used to parse an address string into addresses. For example:
 * </p>
 * <code><pre>
 *       import org.apache.james.mime4j.field.address.AddressList;
 *       import org.apache.james.mime4j.field.address.Mailbox;
 *       import org.apache.james.mime4j.field.address.MailboxList;
 *       import org.apache.james.mime4j.field.address.parser.ParseException;
 *       ...
 *       public Address[] parseAddresses(String arg) throws SieveMailException, InternetAddressException {
 *           try {
 *               final MailboxList list = AddressList.parse(arg).flatten();
 *               final int size = list.size();
 *               final Address[] results = new Address[size];
 *               for (int i=0;i&lt;size;i++) {
 *                   final Mailbox mailbox = list.get(i);
 *                   results[i] = new AddressImpl(mailbox.getLocalPart(), mailbox.getDomain());
 *               }
 *               return null;
 *           } catch (ParseException e) {
 *               throw new InternetAddressException(e);
 *           }
 *       }
 * </pre></code>
 */
public interface MailAdapter {
    
    /**
     * <p>Sets the context for the current sieve script execution.</p>
     * <p>Sieve engines <code>MUST</code> set this property before any calls
     * related to the execution of a script are made.</p>
     * <p>Implementations intended to be shared between separate threads of
     * execution <code>MUST</code> ensure that they manage concurrency contexts,
     * for example by storage in a thread local variable. Engines <code>MUST</code>
     * - for a script execution - ensure that all calls are made within the
     * same thread of execution.</p>
     * @param context the current context, 
     * or null to clear the contest once the execution of a script has completed.
     */
    public void setContext(SieveContext context);
    
    /**
     * Method getActions answers the List of Actions accumulated by the
     * receiver. Implementations may elect to supply an unmodifiable collection.
     * 
     * @return <code>List</code> of {@link Action}'s, not null, possibly
     *         unmodifiable
     */
    public List<Action> getActions();
    
    /**
     * Method getHeader answers a List of all of the headers in the receiver
     * whose name is equal to the passed name. If no headers are found an empty
     * List is returned.
     * 
     * @param name
     * @return <code>List</code> not null, possibly empty, possible
     *         unmodifiable
     * @throws SieveMailException
     */
    public List<String> getHeader(String name) throws SieveMailException;

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
     * @return <code>List</code>, not null possibly empty, possible
     *         unmodifiable
     * @throws SieveMailException
     */
    public List<String> getMatchingHeader(String name) throws SieveMailException;

    /**
     * Method getHeaderNames answers a List of all of the headers in the
     * receiver. No duplicates are allowed.
     * 
     * @return <code>List</code>, not null possible empty, possible
     *         unmodifiable
     * @throws SieveMailException
     */
    public List<String> getHeaderNames() throws SieveMailException;

    /**
     * Method addAction adds an Action to the List of Actions to be performed by
     * the receiver.
     * 
     * @param action
     */
    public void addAction(Action action);

    /**
     * Method executeActions. Applies the Actions accumulated by the receiver.
     */
    public void executeActions() throws SieveException;

    /**
     * Method getSize answers the receiver's message size in octets.
     * 
     * @return int
     * @throws SieveMailException
     */
    int getSize() throws SieveMailException;

    /**
     * Method getContentType returns string/mime representation of the message
     * type.
     * 
     * @return String
     * @throws SieveMailException
     */
    public String getContentType() throws SieveMailException;

    /**
     * Is the given phrase found in the body text of this mail?
     * This search should be case insensitive.
     * @param phraseCaseInsensitive the phrase to search
     * @return true when the mail has a textual body and contains the phrase
     * (case insensitive), false otherwise
     * @throws SieveMailException when the search cannot be completed
     */
    public boolean isInBodyText(final String phraseCaseInsensitive) throws SieveMailException;
    
    /**
     * <p>
     * Parses the named header value into individual addresses.
     * </p>
     * 
     * <p>
     * Headers should be matched in a way that ignores case and the whitespace
     * prefixes and suffixes of a header name when performing the match, as
     * required by RFC 3028. Thus "From", "from ", " From" and " from " are
     * considered equal.
     * </p>
     * 
     * @param headerName
     *            name of the header whose value is to be split
     * @return addresses listed in the given header not null, possibly empty
     * @throws InternetAddressException
     *             when the header value is not an address or list of addresses.
     *             Implemetations may elect to support only standard headers
     *             known to containing one or more addresses rather than parsing
     *             the value content
     * @throws SieveMailException
     *             when the header value cannot be read
     */
    public Address[] parseAddresses(String headerName)
            throws SieveMailException, InternetAddressException;

    /**
     * Contains address data required for SIEVE processing.
     */
    public interface Address {

        /**
         * Gets the local part of the email address. Specified in <a
         * href='http://james.apache.org/server/rfclist/basic/rfc0822.txt'>RFC822</a>.
         * 
         * @return local part, not null
         */
        public String getLocalPart();

        /**
         * Gets the domain part of the email address. Specified in <a
         * href='http://james.apache.org/server/rfclist/basic/rfc0822.txt'>RFC822</a>.
         * 
         * @return domain, not null
         */
        public String getDomain();
    }
}
