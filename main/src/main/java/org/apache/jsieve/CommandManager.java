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

import java.util.List;

import org.apache.jsieve.exception.LookupException;

/**
 * <p>Maps Command names to configured Command implementation classes.</p>
 * <h4>Thread Safety</h4>
 * <p>
 * Implementation dependent. {@link CommandManagerImpl} is a thread safe implementation.
 * </p>
 */
public interface CommandManager {

    /**
     * <p>Gets an instance of a command by name.</p>
     * 
     * @param name -
     *            The name of the Command
     * @return command, not null
     * @throws LookupException
     */
    public ExecutableCommand getCommand(String name) throws LookupException;

    /**
     * Method isSupported answers a boolean indicating if a Command name is
     * configured.
     * 
     * @param name -
     *            The Command name
     * @return boolean - True if the Command name is configured.
     */
    public boolean isCommandSupported(String name);
    
    /**
     * Answer a List of the names of supported Sieve extensions.
     *
     * @return
     */
    public List<String> getExtensions();
}
