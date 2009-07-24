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

package org.apache.jsieve.commands.optional;

import org.apache.jsieve.Arguments;
import org.apache.jsieve.Block;
import org.apache.jsieve.SieveContext;
import org.apache.jsieve.StringListArgument;
import org.apache.jsieve.commands.AbstractActionCommand;
import org.apache.jsieve.exception.SieveException;
import org.apache.jsieve.mail.Action;
import org.apache.jsieve.mail.ActionFileInto;
import org.apache.jsieve.mail.MailAdapter;

/**
 * Class FileInto implements the FileInto Command as defined in RFC 3028,
 * section 4.2.
 */
public class FileInto extends AbstractActionCommand {

    /**
     * Constructor for Require.
     */
    public FileInto() {
        super();
    }

    /**
     * <p>
     * Add an ActionFileInto to the List of Actions to be performed passing the
     * sole StringList argument as the destination. RFC 3028 mandates that there
     * should be only one FileInto per destination. If this is a duplicate, this
     * Command is silently ignored.
     * </p>
     * <p>
     * Also,
     * 
     * @see org.apache.jsieve.commands.AbstractCommand#executeBasic(MailAdapter,
     *      Arguments, Block, SieveContext)
     *      </p>
     */
    protected Object executeBasic(MailAdapter mail, Arguments arguments,
            Block block, SieveContext context) throws SieveException {
        final String destination = ((StringListArgument) arguments
                .getArgumentList().get(0)).getList().get(0);

        // Only one fileinto per destination allowed, others should be
        // discarded
        boolean isDuplicate = false;
        for (final Action action: mail.getActions()) {
            isDuplicate = (action instanceof ActionFileInto)
                    && (((ActionFileInto) action).getDestination()
                            .equals(destination));
            if (isDuplicate) {
                break;
            }
        }

        if (!isDuplicate)
            mail.addAction(new ActionFileInto(destination));

        return null;
    }

    /**
     * @see org.apache.jsieve.commands.AbstractCommand#validateArguments(Arguments,
     *      SieveContext)
     */
    protected void validateArguments(Arguments arguments, SieveContext context)
            throws SieveException {
        validateSingleStringArguments(arguments, context);
    }

}
