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
 * Class ActionRedirect encapsulates the information required to redirect a
 * mail. See RFC 3028, Section 4.3.
 */
public class ActionRedirect implements Action {
    private String fieldAddress;

    /**
     * Constructor for ActionRedirect.
     */
    private ActionRedirect() {
        super();
    }

    /**
     * Constructor for ActionRedirect.
     */
    public ActionRedirect(String address) {
        this();
        setAddress(address);
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Action: " + getClass().getName() + ", address: " + getAddress();
    }

    /**
     * Returns the address.
     * 
     * @return String
     */
    public String getAddress() {
        return fieldAddress;
    }

    /**
     * Sets the address.
     * 
     * @param address
     *            The address to set
     */
    protected void setAddress(String address) {
        fieldAddress = address;
    }

}
