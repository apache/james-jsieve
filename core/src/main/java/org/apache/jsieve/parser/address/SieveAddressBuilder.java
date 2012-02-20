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

package org.apache.jsieve.parser.address;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.apache.jsieve.mail.MailAdapter;
import org.apache.jsieve.mail.MailAdapter.Address;
import org.apache.jsieve.parser.generated.address.ASTaddr_spec;
import org.apache.jsieve.parser.generated.address.ASTaddress_list;
import org.apache.jsieve.parser.generated.address.ASTdomain;
import org.apache.jsieve.parser.generated.address.ASTlocal_part;
import org.apache.jsieve.parser.generated.address.AddressListParser;
import org.apache.jsieve.parser.generated.address.ParseException;
import org.apache.jsieve.parser.generated.address.Token;

/**
 * Builds <code>MailAdapter.Address</code> from address lists. Note that
 * implementators of {@link MailAdapter} are recommended to use a fully featured
 * and maintained parser such as <a href='http://james.apache.org/mime4j'>Apache
 * Mime4J</a>. This implementation is based on Mime4J code but is intended only
 * for internal and demonstration purposes. It is not actively maintained.
 */
public class SieveAddressBuilder {

    private static final Address[] EMPTY_ADDRESSES = {};

    private final Collection<Address> addresses;

    private final Worker worker;

    public SieveAddressBuilder() {
        addresses = Collections.synchronizedCollection(new ArrayList<Address>());
        worker = new Worker();
    }

    /**
     * Clears the addresses currently accumulated.
     */
    public void reset() {
        addresses.clear();
    }

    /**
     * Adds addresses in the given list.
     * 
     * @param addressList
     *            RFC822 address list
     * @throws ParseException
     */
    public void addAddresses(String addressList) throws ParseException {
        final StringReader reader = new StringReader(addressList);
        worker.addAddressses(reader, addresses);
    }

    /**
     * Gets addresses currently accumulated by calls to
     * {@link #addAddresses(String)} since the last call to {@link #reset}.
     * 
     * @return addresses, not null
     */
    public Address[] getAddresses() {
        final Address[] results = (Address[]) addresses.toArray(EMPTY_ADDRESSES);
        return results;
    }

    /**
     * Performs the actual work. Factored into an inner class so that the build
     * interface is clean.
     */
    private final class Worker extends BaseAddressListVisitor {

        public void addAddressses(final Reader reader, final Collection results)
                throws ParseException {
            AddressListParser parser = new AddressListParser(reader);
            ASTaddress_list root = parser.parse();
            root.childrenAccept(this, results);
        }

        @SuppressWarnings("unchecked")
        public Object visit(ASTaddr_spec node, Object data) {
            final AddressBean address = new AddressBean();
            node.childrenAccept(this, address);
            if (data instanceof Collection) {
                final Collection collection = (Collection) data;
                collection.add(address);
            }
            return data;
        }

        public Object visit(final ASTdomain node, final Object data) {
            if (data instanceof AddressBean) {
                final AddressBean address = (AddressBean) data;
                final String domain = contents(node);
                address.setDomain(domain);
            }
            return data;
        }

        public Object visit(ASTlocal_part node, Object data) {
            if (data instanceof AddressBean) {
                final AddressBean address = (AddressBean) data;
                final String localPart = contents(node);
                address.setLocalPart(localPart);
            }
            return data;
        }

        private String contents(AddressNode node) {
            StringBuffer buffer = new StringBuffer(32);
            Token last = node.lastToken;
            Token next = node.firstToken;
            while (next != last) {
                buffer.append(next.image);
                next = next.next;
            }
            buffer.append(last.image);
            return buffer.toString();
        }
    }

    /**
     * Bean based address implementation.
     */
    private final class AddressBean implements Address {
        private String localPart;

        private String domain;

        public AddressBean() {
            localPart = "";
            domain = "";
        }

        public String getDomain() {
            return domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }

        public String getLocalPart() {
            return localPart;
        }

        public void setLocalPart(String localPart) {
            this.localPart = localPart;
        }
    }
}
