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

import org.apache.jsieve.Argument;
import org.apache.jsieve.Arguments;
import org.apache.jsieve.Block;
import org.apache.jsieve.NumberArgument;
import org.apache.jsieve.SieveContext;
import org.apache.jsieve.StringListArgument;
import org.apache.jsieve.TagArgument;
import org.apache.jsieve.commands.AbstractActionCommand;
import org.apache.jsieve.exception.CommandException;
import org.apache.jsieve.exception.SieveException;
import org.apache.jsieve.exception.SyntaxException;
import org.apache.jsieve.mail.MailAdapter;
import org.apache.jsieve.mail.optional.ActionVacation;

import java.util.Iterator;

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
        Iterator<Argument> argumentIterator = arguments.getArgumentList().iterator();
        ActionVacation.ActionVacationBuilder actionVacationBuilder = ActionVacation.builder();

        while (argumentIterator.hasNext()) {
            parseNextArguments(argumentIterator, actionVacationBuilder);
        }

        return actionVacationBuilder.build();
    }

    private void parseNextArguments(Iterator<Argument> argumentIterator, ActionVacation.ActionVacationBuilder actionVacationBuilder) throws SieveException {
        Argument argument = argumentIterator.next();
        if (!(argument instanceof TagArgument) && !(argument instanceof StringListArgument)) {
            throw new SyntaxException("Expecting one of these tags : :days :subject :from :mime :handle or the reason string");
        }
        if (argument instanceof StringListArgument) {
            handleReasonString(actionVacationBuilder, (StringListArgument) argument);
        }
        if (argument instanceof TagArgument) {
            handleTagArgument(argumentIterator, actionVacationBuilder, (TagArgument) argument);
        }
    }

    private void handleReasonString(ActionVacation.ActionVacationBuilder actionVacationBuilder, StringListArgument argument) throws SieveException {
        if (argument.getList().size() != 1) {
            throw new SyntaxException("Expecting only one reason for the vacation extension");
        }
        actionVacationBuilder.reason(argument.getList().get(0));
    }

    private void handleTagArgument(Iterator<Argument> argumentIterator, ActionVacation.ActionVacationBuilder actionVacationBuilder, TagArgument tagArgument) throws SieveException {
        if (tagArgument.is(DAYS)) {
            ensureHasTagParameter(argumentIterator, DAYS_EXCEPTION_MESSAGE);
            actionVacationBuilder.duration(retrieveNumberArgument(argumentIterator, DAYS_EXCEPTION_MESSAGE).getInteger());
        } else if (tagArgument.is(SUBJECT)) {
            ensureHasTagParameter(argumentIterator, SUBJECT_EXCEPTION_MESSAGE);
            String exceptionMessage = SUBJECT_EXCEPTION_MESSAGE;
            actionVacationBuilder.subject(retrieveStringArgument(argumentIterator, exceptionMessage));
        } else if (tagArgument.is(FROM)) {
            ensureHasTagParameter(argumentIterator, FROM_EXCEPTION_MESSAGE);
            actionVacationBuilder.from(retrieveStringArgument(argumentIterator, FROM_EXCEPTION_MESSAGE));
        } else if (tagArgument.is(ADDRESSES)) {
            ensureHasTagParameter(argumentIterator, ADDRESSES_EXCEPTION_MESSAGE);
            actionVacationBuilder.addresses(retrievingStringListArgument(argumentIterator, ADDRESSES_EXCEPTION_MESSAGE).getList());
        } else if (tagArgument.is(MIME)) {
            ensureHasTagParameter(argumentIterator, MIME_EXCEPTION_MESSAGE);
            actionVacationBuilder.mime(retrieveStringArgument(argumentIterator, MIME_EXCEPTION_MESSAGE));
        } else if (tagArgument.is(HANDLE)) {
            ensureHasTagParameter(argumentIterator, HANDLE_EXCEPTION_MESSAGE);
            actionVacationBuilder.handle(retrieveStringArgument(argumentIterator, HANDLE_EXCEPTION_MESSAGE));
        } else {
            throw new SyntaxException("Unextected tag " + tagArgument.getTag());
        }
    }

    private String retrieveStringArgument(Iterator<Argument> argumentIterator, String exceptionMessage) throws SieveException {
        StringListArgument stringListArgument = retrievingStringListArgument(argumentIterator, exceptionMessage);
        if (stringListArgument.getList().size() != 1) {
            throw new SyntaxException(exceptionMessage);
        }
        return stringListArgument.getList().get(0);
    }

    private StringListArgument retrievingStringListArgument(Iterator<Argument> argumentIterator, String exceptionMessage) throws SieveException {
        Argument subjectArgument = argumentIterator.next();
        if (!(subjectArgument instanceof StringListArgument)) {
            throw new SyntaxException(exceptionMessage);
        }
        return (StringListArgument) subjectArgument;
    }

    private NumberArgument retrieveNumberArgument(Iterator<Argument> argumentIterator, String exceptionMessage) throws SieveException {
        Argument daysArgument = argumentIterator.next();
        if (!(daysArgument instanceof NumberArgument)) {
            throw new SyntaxException(exceptionMessage);
        }
        return (NumberArgument) daysArgument;
    }

    private void ensureHasTagParameter(Iterator<Argument> argumentIterator, String message) throws SieveException {
        if (!argumentIterator.hasNext()) {
            throw new SyntaxException(message);
        }
    }
}
