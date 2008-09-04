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

package org.apache.jsieve.util.check;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.jsieve.ConfigurationManager;
import org.apache.jsieve.exception.SieveException;
import org.apache.jsieve.mail.ActionFileInto;
import org.apache.jsieve.mail.ActionKeep;
import org.apache.jsieve.mail.ActionRedirect;
import org.apache.jsieve.mail.ActionReject;
import org.apache.jsieve.parser.generated.ParseException;

/**
 * Checks a <code>sieve</code> script by executing it against a given mail and
 * reporting the results.
 */
public class ScriptChecker {

    private final ScriptCheckMailAdapter adapter;

    public ScriptChecker() {
        adapter = new ScriptCheckMailAdapter();
    }

    /**
     * Checks the <code>sieve</code> script contained in the given file by
     * executing it against the given message.
     * 
     * @param message
     *            <code>File</code> containing the mail message to be fed to
     *            the script, not null
     * @param script
     *            <code>File</code> containing the script to be checked
     * @return <code>Results</code> of that execution
     * @throws IOException
     * @throws MessageException
     */
    public Results check(final File message, final File script)
            throws IOException, MessagingException {
        final FileInputStream messageStream = new FileInputStream(message);
        final FileInputStream scriptStream = new FileInputStream(script);
        final Results results = check(messageStream, scriptStream);
        return results;
    }

    /**
     * Checks the <code>sieve</code> script contained in the given file by
     * executing it against the given message.
     * 
     * @param script
     *            <code>InputStream</code>, not null
     * @return <code>Results</code> of the check, not null
     * @throws IOException
     * @throws MessagingException
     */
    public Results check(final InputStream message, final InputStream script)
            throws IOException, MessagingException {
        MimeMessage mimeMessage = new MimeMessage(null, message);
        adapter.setMail(mimeMessage);
        Results results;
        try {
            new ConfigurationManager().build().interpret(adapter, script);
            final List executedActions = adapter.getExecutedActions();
            results = new Results(executedActions);
        } catch (ParseException e) {
            e.printStackTrace();
            results = new Results(e);
        } catch (SieveException e) {
            e.printStackTrace();
            results = new Results(e);
        }
        return results;
    }

    /**
     * Contains results of script execution.
     */
    public final static class Results {
        private final boolean pass;

        private final Exception exception;

        private final List actionsExecuted;

        public Results(final Exception ex) {
            this.exception = ex;
            pass = false;
            actionsExecuted = null;
        }

        public Results(final List actions) {
            this.pass = true;
            exception = null;
            this.actionsExecuted = actions;
        }

        /**
         * Is this a pass?
         * 
         * @return true if the script executed without error, false if errors
         *         were encountered
         */
        public boolean isPass() {
            return pass;
        }

        /**
         * Gets the exception which was thrown during execution.
         * 
         * @return <code>Exception</code> or null if no exception was thrown
         */
        public Exception getException() {
            return exception;
        }

        /**
         * Gets the actions executed by the script.
         * 
         * @return <code>List</code> of actions or null if the script failed
         */
        public List getActionsExecuted() {
            return actionsExecuted;
        }

        /**
         * Is the <code>n<code>'th action a <code>FileInto</code>
         * with given destination?
         * @param destination <code>String</code> destination for the file into
         * @param n index to check
         * @return  true if the <code>n<code>'th action is a <code>FileInto</code>
         * with given destination
         */
        public boolean isActionFileInto(String destination, int n) {
            boolean result = false;
            Object action = actionsExecuted.get(n);
            if (action != null && action instanceof ActionFileInto) {
                ActionFileInto actionFileInto = (ActionFileInto) action;
                result = destination.equals(actionFileInto.getDestination());
            }
            return result;
        }

        /**
         * Is the <code>n<code>'th action a <code>Redirect</code>
         * with given address?
         * @param address <code>String</code> redirect address
         * @param n index to check
         * @return  true if the <code>n<code>'th action is a <code>Redirect</code>
         * with given redirect address
         */
        public boolean isActionRedirect(String address, int n) {
            boolean result = false;
            Object action = actionsExecuted.get(n);
            if (action != null && action instanceof ActionRedirect) {
                ActionRedirect actionRedirect = (ActionRedirect) action;
                result = address.equals(actionRedirect.getAddress());
            }
            return result;
        }

        /**
         * Is the <code>n<code>'th action a <code>Reject</code>
         * with given message?
         * @param message <code>String</code> rejection message
         * @param n index to check
         * @return  true if the <code>n<code>'th action is a <code>Reject</code>
         * with given reject message
         */
        public boolean isActionReject(String message, int n) {
            boolean result = false;
            Object action = actionsExecuted.get(n);
            if (action != null && action instanceof ActionReject) {
                ActionReject actionReject = (ActionReject) action;
                result = message.equals(actionReject.getMessage());
            }
            return result;
        }

        /**
         * Is the <code>n<code>'th action a <code>Keep</code>?
         * @param n index to check
         * @return  true if the <code>n<code>'th action is a <code>Keep</code>
         */
        public boolean isActionKeep(int n) {
            boolean result = false;
            Object action = actionsExecuted.get(n);
            if (action != null && action instanceof ActionKeep) {
                result = true;
            }
            return result;
        }

        /**
         * Prints out details of results.
         */
        public String toString() {
            StringBuffer buffer = new StringBuffer("Results: ");
            if (pass) {
                buffer.append("PASS");
            } else {
                buffer.append("FAIL: ");
                if (exception != null) {
                    if (exception instanceof ParseException) {
                        buffer.append("Cannot parse script");
                    } else {
                        buffer.append("Cannot excute script");
                    }
                    buffer.append(exception.getMessage());
                }
            }
            return buffer.toString();
        }

    }
}
