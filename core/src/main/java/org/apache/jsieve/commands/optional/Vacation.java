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
import org.apache.jsieve.commands.AbstractActionCommand;
import org.apache.jsieve.exception.CommandException;
import org.apache.jsieve.exception.SieveException;
import org.apache.jsieve.mail.MailAdapter;
import org.apache.jsieve.mail.optional.ActionVacation;
import org.apache.jsieve.utils.ArgumentParser;

/**
 * See https://tools.ietf.org/html/rfc5230
 */
public class Vacation extends AbstractActionCommand {

    public static final String DAYS = ":days";
    public static final String DAYS_EXCEPTION_MESSAGE = "Expecting a number argument setting the number of days after tag " + DAYS;
    public static final String SUBJECT = ":subject";
    public static final String SUBJECT_EXCEPTION_MESSAGE = "Expecting a string argument setting the subject after tag " + SUBJECT;
    public static final String FROM = ":from";
    public static final String FROM_EXCEPTION_MESSAGE = "Expecting a string argument setting the from field of sent mails after tag " + FROM;
    public static final String ADDRESSES = ":addresses";
    public static final String ADDRESSES_EXCEPTION_MESSAGE = "Expecting a string list argument setting the additional addresses this script is authorized to respond to after tag " + ADDRESSES;
    public static final String MIME = ":mime";
    public static final String MIME_EXCEPTION_MESSAGE = "Expecting a string argument setting a mime message instead of the reason string after tag " + MIME;
    public static final String HANDLE = ":handle";
    public static final String HANDLE_EXCEPTION_MESSAGE = "Expecting a string argument setting handle string after tag " + HANDLE;

    @Override
    protected Object executeBasic(MailAdapter mail, Arguments arguments, Block block, SieveContext context) throws SieveException {
        mail.addAction(retrieveAction(arguments));
        return null;
    }

    @Override
    protected void validateState(SieveContext context) throws CommandException {
        // RFC-5230 Section 4.7 : The vacation action is incompatible with the Sieve reject and refuse actions
        super.validateState(context);
        // RFC-5230 Section 4.7 : Vacation can only be executed once per script
        if (context.getCommandStateManager().getVacationProcessed())
            throw context.getCoordinate()
                .commandException("The \"vacation\" command is not allowed to be executed after other vacation commands");
    }

    @Override
    protected void updateState(SieveContext context) {
        context.getCommandStateManager().setHasActions(true);
        context.getCommandStateManager().setInProlog(false);
        // RFC-5230 Section 4.7 : Vacation does not affect Sieve's implicit keep action.
        context.getCommandStateManager().setVacationProcessed(true);
        context.getCommandStateManager().setImplicitKeep(context.getCommandStateManager().isImplicitKeep());
    }

    @Override
    protected void validateArguments(Arguments arguments, SieveContext context) throws SieveException {
        retrieveAction(arguments);
    }

    private ActionVacation retrieveAction(Arguments arguments) throws SieveException {
        ArgumentParser argumentParser = new ArgumentParser(arguments.getArgumentList());
        argumentParser.throwOnUnvalidSeenSingleTag();
        argumentParser.throwOnUnvalidSeenTagWithValue(FROM, SUBJECT, HANDLE, MIME, DAYS, ADDRESSES);

        return ActionVacation.builder()
            .addresses(argumentParser.getStringListForTag(ADDRESSES, ADDRESSES_EXCEPTION_MESSAGE))
            .duration(argumentParser.getNumericValueForTag(DAYS, DAYS_EXCEPTION_MESSAGE))
            .handle(argumentParser.getStringValueForTag(HANDLE, HANDLE_EXCEPTION_MESSAGE))
            .mime(argumentParser.getStringValueForTag(MIME, MIME_EXCEPTION_MESSAGE))
            .subject(argumentParser.getStringValueForTag(SUBJECT, SUBJECT_EXCEPTION_MESSAGE))
            .from(argumentParser.getStringValueForTag(FROM, FROM_EXCEPTION_MESSAGE))
            .reason(argumentParser.getRemainingStringValue("Expecting a single String value as a reason"))
            .build();
    }

}
