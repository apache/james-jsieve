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
 * Class ActionFileInto encapsulates the information required to file a mail
 * into a location. See RFC 3028, Section 4.2.
 */
public class ActionFileInto implements Action {
    /**
     * A String representation of the location to which the message should be
     * moved.
     */
    private String fieldDestination;

    /**
     * Constructor for ActionFileInto.
     */
    private ActionFileInto() {
        super();
    }

    /**
     * Constructor for ActionFileInto.
     */
    public ActionFileInto(String destination) {
        this();
        setDestination(destination);
    }

    /**
     * Returns the destination.
     * 
     * @return String
     */
    public String getDestination() {
        return fieldDestination;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Action: " + getClass().getName() + ", destination: "
                + getDestination();
    }

    /**
     * Sets the destination.
     * 
     * @param destination
     *            The destination to set
     */
    protected void setDestination(String destination) {
        fieldDestination = destination;
    }

}
