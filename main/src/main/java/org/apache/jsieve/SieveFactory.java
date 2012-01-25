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
import java.util.ArrayList;
import java.util.List;

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
 * SieveFactory is the primary invocation point for all Sieve
 * operations. These are...
 * <dl>
 * <dt>{@link #parse(InputStream)}</dt>
 * <dd> Parse a Sieve script into a hierarchy of parsed nodes. A succesful parse
 * means the script is lexically and gramatically valid according to RFC 3028,
 * section 8. The result is the start node of the parsed Sieve script. The start
 * node is resuable. Typically it is stored for reuse in all subsequent
 * evaluations of the script. </dd>
 * <dt>{@link #evaluate(MailAdapter, Node)}</dt>
 * <dd> Evaluate an RFC 822 compliant mail message wrapped in a {@link MailAdapter}
 * against the parse result referenced by the start node from the Parse
 * operation above. As evaluation proceeds a List of {@link org.apache.jsieve.mail.Action}s 
 * is added to the MailAdapter. At the end of evaluation, each Action in the List is executed in
 * the order they were added. </dd>
 * <dt>{@link #interpret(MailAdapter, InputStream)}</dt>
 * <dd>A concatenation of parse and evaluate. Useful for testing, but generally
 * the parse result should be stored for reuse in subsequent evaluations. </dd>
 * </dl>
 * </p>
 * <h4>Thread Safety</h4>
 * <p>
 * An instance can be safely accessed concurrently by multiple threads
 * provided that the managers used to construct the instance 
 * (when {@link #SieveFactory(CommandManager, ComparatorManager, TestManager, Log)} 
 * is called) are thread safe.
 * </p>
 */
public class SieveFactory {

    private final CommandManager commandManager;

    private final ComparatorManager comparatorManager;

    private final TestManager testManager;

    private final Log log;

    /**
     * Constructor for SieveFactory.
     */
    public SieveFactory(final CommandManager commandManager,
            final ComparatorManager comparatorManager,
            final TestManager testManager, final Log log) {
        super();
        this.commandManager = commandManager;
        this.comparatorManager = comparatorManager;
        this.testManager = testManager;
        this.log = log;
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
            final SimpleNode node = new SieveParser(inputStream, "UTF-8")
                    .start();
            SieveValidationVisitor visitor = new SieveValidationVisitor(
                    commandManager, testManager, comparatorManager);
            node.jjtAccept(visitor, null);
            return node;
        } catch (ParseException ex) {
            if (log.isErrorEnabled())
                log.error("Parse failed. Reason: " + ex.getMessage());
            if (log.isDebugEnabled())
                log.debug("Parse failed.", ex);
            throw ex;
        } catch (SieveException ex) {
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
        final SieveContext context = new BaseSieveContext(commandManager,
                comparatorManager, testManager, log);
        try {
            // Ensure that the context is set on the mail
            mail.setContext(context);
            
            SieveParserVisitor visitor = new SieveParserVisitorImpl(context);
            try {
                // Evaluate the Nodes
                startNode.jjtAccept(visitor, mail);
    
            } catch (StopException ex) {
                // Stop is OK
            } catch (SieveException ex) {
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
                if (log.isErrorEnabled())
                    log.error("Evaluation failed. Reason: " + ex.getMessage());
                if (log.isDebugEnabled())
                    log.debug("Evaluation failed.", ex);
                throw ex;
            }
        } finally {
            // Tidy up by ensuring that a reference to the context is not held by the adapter.
            // This prevents leaks when the adapter stores the context in a thread local variable.
            mail.setContext(null);
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
     * <p>Answer a List of supported Sieve extensions. This depends on the configured Command, Comparator
     * and Test managers.
     *
     * @return a List of supported Sieve extensions
     */
    public List<String> getExtensions() {
        List<String> commands = commandManager.getExtensions();
        List<String> comparators = comparatorManager.getExtensions();
        List<String> tests = testManager.getExtensions();
        List<String> extensions = new ArrayList<String>(commands.size() + comparators.size()
                + tests.size());
        extensions.addAll(commands);
        extensions.addAll(comparators);
        extensions.addAll(tests);
        return extensions;
    }
}
