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

import org.apache.jsieve.exception.CommandException;
import org.apache.jsieve.exception.SyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Specifies the positional extent of an element within the script being
 * executed. In other words, this gives the line and column at which the elment
 * starts and at which it ends.
 */
public final class ScriptCoordinate {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptCoordinate.class);

    private final int startLineNumber;

    private final int startColumnNumber;

    private final int endLineNumber;

    private final int endColumnNumber;

    public ScriptCoordinate(final int startLineNumber,
            final int startColumnNumber, final int endLineNumber,
            final int endColumnNumber) {
        super();
        this.startLineNumber = startLineNumber;
        this.startColumnNumber = startColumnNumber;
        this.endLineNumber = endLineNumber;
        this.endColumnNumber = endColumnNumber;
    }

    /**
     * Gets the number of the column where the elements ends.
     * 
     * @return column number
     */
    public int getEndColumnNumber() {
        return endColumnNumber;
    }

    /**
     * Gets the number of the line where the element ends.
     * 
     * @return line number
     */
    public int getEndLineNumber() {
        return endLineNumber;
    }

    /**
     * Gets the number of the column where the element start.
     * 
     * @return column number
     */
    public int getStartColumnNumber() {
        return startColumnNumber;
    }

    /**
     * Gets the number of the line where the element starts.
     * 
     * @return line number
     */
    public int getStartLineNumber() {
        return startLineNumber;
    }

    /**
     * Creates a syntax exception based on the given message containing details
     * of the script position. The message should end with a full stop.
     * 
     * @param message
     *            <code>String</code> containing the base message, not
     *            null
     * @return <code>SyntaxException</code> with details of the script
     *         position appended to the message, not null
     */
    public SyntaxException syntaxException(String message) {
        LOGGER.warn(message);
        logDiagnosticsInfo();
        final String fullMessage = addStartLineAndColumn(message);
        return new SyntaxException(fullMessage);
    }

    /**
     * Creates a command exception based on the given message containing details
     * of the script position. The message should end with a full stop.
     * 
     * @param message
     *            <code>String</code> containing the base message, not
     *            null
     * @return <code>CommandException</code> with details of the script
     *         position appended to the message, not null
     */
    public CommandException commandException(String message) {
        LOGGER.warn(message);
        logDiagnosticsInfo();
        final String fullMessage = addStartLineAndColumn(message);
        return new CommandException(fullMessage);
    }

    /**
     * Appends a standard position phrase to the given message. This message
     * should end with a full stop.
     * 
     * @param message
     *            <code>CharSequence</code> message, not null
     * @return <code>String</code> containing the original message with
     *         positional phrase appended, not null
     */
    public String addStartLineAndColumn(CharSequence message) {
        final StringBuilder buffer;
        if (message instanceof StringBuilder) {
            buffer = (StringBuilder) message;
        } else {
            buffer = new StringBuilder(message.toString());
        }
        buffer.append(" Line ");
        buffer.append(startLineNumber);
        buffer.append(" column ");
        buffer.append(startColumnNumber);
        buffer.append(".");
        return buffer.toString();
    }

    /**
     * Logs diagnotic information about the script coordinate.
     */
    public void logDiagnosticsInfo() {
        LOGGER.info("Expression starts line {} column {}", startLineNumber, startColumnNumber);
        LOGGER.info("Expression ends line {} column {}", endLineNumber, endColumnNumber);
    }

    /**
     * Logs diagnotic information about the script coordinate.
     */
    public void debugDiagnostics() {
        LOGGER.debug("Expression starts line {} column {}", startLineNumber, startColumnNumber);
        LOGGER.debug("Expression ends line {} column {}", endLineNumber, endColumnNumber);
    }
}
