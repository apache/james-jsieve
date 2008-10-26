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

/**
 * Simple immutable address implementation.
 */
public final class AddressImpl implements MailAdapter.Address {

    private final String localPart;

    private final String domain;

    /**
     * Constructs an address.
     * 
     * @param localPart
     *            the local part of the address
     * @param domain
     *            the domain part of the address
     */
    public AddressImpl(final String localPart, final String domain) {
        super();
        this.localPart = localPart;
        this.domain = domain;
    }

    /**
     * Gets the domain of the address.
     * 
     * @return domain
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Gets the local part of the address.
     */
    public String getLocalPart() {
        return localPart;
    }

}
