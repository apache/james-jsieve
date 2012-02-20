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

package org.apache.jsieve.mail.optional;

import java.util.List;

import org.apache.jsieve.mail.SieveMailException;

/**
 * Interface EnvelopeAccessors specifies the method signatures required to
 * support the Envelope Test.
 */
public interface EnvelopeAccessors {
    /**
     * Method getEnvelope answers a List of all of the envelope values in the
     * receiver whose name is equal to the passed name. If no values are found
     * an empty List is returned.
     * 
     * @param name
     * @return List
     * @throws SieveMailException
     */
    public List<String> getEnvelope(String name) throws SieveMailException;

    /**
     * Method getEnvelopeNames answers a List of the names of the envelope
     * values in the receiver. No duplicates are allowed.
     * 
     * @return List
     * @throws SieveMailException
     */
    public List<String> getEnvelopeNames() throws SieveMailException;

    /**
     * <p>
     * Method getMatchingEnvelope answers a List of all of the envelope values
     * in the receiver with the passed name. If no matching names are found an
     * empty List is returned.
     * </p>
     * 
     * <p>
     * This method differs from getEnvelope(String) in that it ignores case and
     * the whitespace prefixes and suffixes of an envelope value name when
     * performing the match, as required by RFC 3028. Thus "From", "from ", "
     * From" and " from " are considered equal.
     * </p>
     * 
     * @param name
     * @return List
     * @throws SieveMailException
     */
    public List<String> getMatchingEnvelope(String name) throws SieveMailException;

}
