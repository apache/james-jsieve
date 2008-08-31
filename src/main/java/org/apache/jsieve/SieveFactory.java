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

import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.jsieve.exception.SieveException;
import org.apache.jsieve.exception.StopException;
import org.apache.jsieve.mail.ActionKeep;
import org.apache.jsieve.mail.MailAdapter;
import org.apache.jsieve.parser.generated.Node;
import org.apache.jsieve.parser.generated.ParseException;
import org.apache.jsieve.parser.generated.SieveParser;
import org.apache.jsieve.parser.generated.SieveParserVisitor;
import org.apache.jsieve.parser.generated.SimpleNode;

/**
 * <p>
 * Singleton class SieveFactory is the primary invocation point for all Sieve
 * operations. Theses are...
 * <dl>
 * <dt>parse</dt>
 * <dd> Parse a Sieve script into a hierarchy of parsed nodes. A succesful parse
 * means the script is lexically and gramatically valid according to RFC 3028,
 * section 8. The result is the start node of the parsed Sieve script. The start
 * node is resuable. Typically it is stored for reuse in all subsequent
 * evaluations of the script. </dd>
 * <dt>evaluate</dt>
 * <dd> Evaluate an RFC 822 compliant mail message wrapped in a MailAdapter
 * against the parse result referenced by the start node from the Parse
 * operation above. As evaluation proceeds a List of Actions is added to the
 * MailAdapter. At the end of evaluation, each Action in the List is executed in
 * the order they were added. </dd>
 * <dt>interpret/dt>
 * <dd>A concatenation of parse and evaluate. Useful for testing, but generally
 * the parse result should be stored for reuse in subsequent evaluations. </dd>
 * </dl>
 * </p>
 * 
 * 
 */
public class SieveFactory {

    /**
     * The sole instance of the receiver.
     */
    private static SieveFactory fieldInstance;

    /**
     * Constructor for SieveFactory.
     */
    public SieveFactory() {
        super();
    }

    /**
     * Method parse parses a Sieve script into a hierarchy of parsed nodes. A
     * successful parse means the script is lexically and grammatically valid
     * according to RFC 3028, section 8. The result is the start node of the
     * parsed Sieve script. The start node is reusable. Typically it is stored
     * for reuse in subsequent evaluations of the script.
     * 
     * @param inputStream
     * @return Node
     * @throws ParseException
     */
    public Node parse(InputStream inputStream) throws ParseException {
        try {
            final SimpleNode node = new SieveParser(inputStream, "UTF-8").start();
            final CommandManager commandManager = CommandManager.getInstance();
            SieveValidationVisitor visitor = new SieveValidationVisitor(commandManager);
            node.jjtAccept(visitor, null);
            return node;
        } catch (ParseException ex) {
            Log log = Logger.getLog();
            if (log.isErrorEnabled())
                log.error("Parse failed. Reason: " + ex.getMessage());
            if (log.isDebugEnabled())
                log.debug("Parse failed.", ex);
            throw ex;
        } catch (SieveException ex) {
            Log log = Logger.getLog();
            if (log.isErrorEnabled())
                log.error("Parse failed. Reason: " + ex.getMessage());
            if (log.isDebugEnabled())
                log.debug("Parse failed.", ex);
            throw new ParseException(ex.getMessage());
        }
    }

    /**
     * <p>
     * Method evaluate evaluates an RFC 822 compliant mail message wrapped in a
     * MailAdapter by visting each node of the parsed script beginning at the
     * passed start node. As evaluation proceeds a List of Actions is added to
     * the MailAdapter.
     * <p>
     * 
     * <p>
     * At the start of evaluation an 'implicitKeep' state is set. This can be
     * cancelled by a Command during evaluation. If 'implicitKeep' is still set
     * at the end of evaluation, a Keep Action is added to the List of Actions.
     * Finally, each Action in the List is executed in the order they were
     * added.
     * </p>
     * 
     * @param mail
     * @param startNode
     * @throws SieveException
     */
    public void evaluate(MailAdapter mail, Node startNode)
            throws SieveException {
        final CommandManager commandManager = CommandManager.getInstance();
        SieveContext context = new BaseSieveContext(commandManager);
        SieveParserVisitor visitor = new SieveParserVisitorImpl(context);
        try {
            // Evaluate the Nodes
            startNode.jjtAccept(visitor, mail);

        } catch (StopException ex) {
            // Stop is OK
        } catch (SieveException ex) {
            Log log = Logger.getLog();
            if (log.isErrorEnabled())
                log.error("Evaluation failed. Reason: " + ex.getMessage());
            if (log.isDebugEnabled())
                log.debug("Evaluation failed.", ex);
            throw ex;
        }

        // If after evaluating all of the nodes or stopping, implicitKeep is
        // still
        // in effect, add a Keep to the list of Actions.
        if (context.getCommandStateManager().isImplicitKeep())
            mail.addAction(new ActionKeep());

        // Execute the List of Actions
        try {
            mail.executeActions();
        } catch (SieveException ex) {
            Log log = Logger.getLog();
            if (log.isErrorEnabled())
                log.error("Evaluation failed. Reason: " + ex.getMessage());
            if (log.isDebugEnabled())
                log.debug("Evaluation failed.", ex);
            throw ex;
        }
    }

    /**
     * Method interpret parses a Sieve script and then evaluates the result
     * against a mail.
     * 
     * @param mail
     * @param inputStream
     * @throws ParseException
     * @throws SieveException
     */
    public void interpret(MailAdapter mail, InputStream inputStream)
            throws ParseException, SieveException {
        evaluate(mail, parse(inputStream));
    }

    /**
     * Returns the instance of the receiver, lazily initialised if required.
     * 
     * @return SieveFactory
     */
    public static synchronized SieveFactory getInstance() {
        SieveFactory instance = null;
        if (null == (instance = getInstanceBasic())) {
            updateInstance();
            return getInstance();
        }
        return instance;
    }

    /**
     * Computes an instance of the receiver.
     * 
     * @return SieveFactory
     */
    public static SieveFactory computeInstance() {
        return new SieveFactory();
    }

    /**
     * Returns the instance of the receiver.
     * 
     * @return SieveFactory
     */
    private static SieveFactory getInstanceBasic() {
        return fieldInstance;
    }

    /**
     * Sets the instance of the receiver.
     * 
     * @param instance
     *                The instance to set
     */
    protected static void setInstance(SieveFactory instance) {
        fieldInstance = instance;
    }

    /**
     * Updates the instance of the receiver.
     */
    protected static void updateInstance() {
        setInstance(computeInstance());
    }

}
