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
import org.apache.jsieve.mail.MailAdapter;

/**
 * Interface ExecutableCommand defines the method signatures for Sieve Commands.
 */
public interface ExecutableCommand {
    /**
     * Method execute executes a Sieve Command.
     * 
     * @param mail -
     *            The mail against which the Command is executed.
     * @param arguments -
     *            The Command arguments
     * @param block -
     *            An optional Block to be evaluated
     * @param context
     *            <code>SieveContext</code> containing contextual information,
     *            not null
     * @return Object - The result of evaluating the Command
     * @throws SieveException
     */
    public Object execute(MailAdapter mail, Arguments arguments, Block block,
            SieveContext context) throws SieveException;

}
