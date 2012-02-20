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

package org.apache.jsieve;

import org.apache.jsieve.exception.SieveException;

/**
 * Class <code>SieveConfigurationException</code> indicates an exceptional
 * condition encountered while evaluating the Sieve configuration.
 * 
 */
@SuppressWarnings("serial")
public class SieveConfigurationException extends SieveException {

    /**
     * Constructor for SieveConfigurationException.
     */
    public SieveConfigurationException() {
        super();
    }

    /**
     * Constructor for SieveConfigurationException.
     * 
     * @param message
     */
    public SieveConfigurationException(String message) {
        super(message);
    }

    /**
     * Constructor for SieveConfigurationException.
     * 
     * @param message
     * @param cause
     */
    public SieveConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor for SieveConfigurationException.
     * 
     * @param cause
     */
    public SieveConfigurationException(Throwable cause) {
        super(cause);
    }

}
