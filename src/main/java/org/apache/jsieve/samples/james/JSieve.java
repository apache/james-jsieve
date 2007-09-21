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

package org.apache.jsieve.samples.james;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.mail.MessagingException;

import org.apache.james.core.MailImpl;
import org.apache.jsieve.SieveFactory;
import org.apache.jsieve.exception.SieveException;
import org.apache.jsieve.mail.MailAdapter;
import org.apache.jsieve.parser.generated.Node;
import org.apache.jsieve.parser.generated.ParseException;
import org.apache.mailet.GenericMailet;
import org.apache.mailet.Mail;
import org.apache.mailet.MailetException;

/**
 * <p>
 * Class JSieve is a DEMONSTRATION Mailet that invokes JSieve to perform mail
 * processing. There is a single configuration parameter, <code>scriptURL</code>.
 * This a URL pointing to the resource containing the Sieve script to run.
 * </p>
 * 
 * <p>
 * This Mailet is intended to replace the <code>LocalDelivery</code> Mailet in
 * James. Sieve's Keep Action is functionally equivalent to the
 * <code>LocalDelivery</code> Mailet's processing. The Sieve script may
 * explicitily or implicitly invoke this Action, and/or any other configured
 * Action. Actions are configured in class <code>ActionDispatcher</code>.
 * </p>
 * 
 * <p>
 * IMPORTANT NOTES
 * </p>
 * 
 * <p>
 * This is NOT production quality code! It is a test harness for exercising
 * jSieve. At least the feutures listed below would be required to consider this
 * of production quality.
 * </p>
 * 
 * <p>
 * REQUIRED FEATURES
 * </p>
 * 
 * <p>
 * To be truly useful, this mailet needs to be configurable to invoke user
 * specific Sieve scripts so that indivual users have control of their mail
 * processing.
 * </p>
 * 
 * <p>
 * In a Mailet environment, a generic Sieve command to invoke a Mailet would be
 * extremely powerful as jSieve could then leverage the abilities of all
 * available Mailets. Currently, Sieve must wastefully duplicate the same
 * behaviour as Sieve commands.
 * </p>
 * 
 * <p>
 * The converse also applies. Provision should be made for a Mailet to reuse
 * jSieve commands. As the primary difference between the two is that Sieve
 * deals with a single recipient while Mailets deal with multiple recipients, a
 * Mailet could simply iterate over all of its recipients invoking the Sieve
 * command for each recipient.
 * </p>
 */
public class JSieve extends GenericMailet {
    private static final Random random = new Random();

    private URL fieldScriptURL;

    private Node fieldStartNode;

    /**
     * Constructor for JSieve.
     */
    public JSieve() {
        super();
    }

    /**
     * @see org.apache.mailet.Mailet#service(Mail)
     */
    public void service(Mail mail) throws MessagingException {
        // If the mail has no recipients, do nothing
        if (mail.getRecipients().isEmpty())
            return;
        // Sieve expects a single recipient. If the mail has more we need to
        // clone the mail, with each mail having a single recipient and
        // resend them.
        if (mail.getRecipients().size() == 1) {
            // Evaluate the mail against the script.
            // The default state for the mail is GHOST.
            // Actions executed as a result of evaluating the script may
            // change this.
            mail.setState(Mail.GHOST);
            evaluate(mail);
        } else {
            Iterator recipientsIter = mail.getRecipients().iterator();
            List recipients = new ArrayList(1);
            while (recipientsIter.hasNext()) {
                // MailImpl mailClone = duplicate((MailImpl) mail);
                recipients.clear();
                recipients.add(recipientsIter.next());
                // mailClone.setRecipients(recipients);
                // getMailetContext().sendMail(mailClone);
                getMailetContext().sendMail(mail.getSender(), recipients,
                        mail.getMessage(), mail.getState());
            }
            // Kill the original message
            mail.setState(Mail.GHOST);
        }
    }

    protected MailImpl duplicate(MailImpl aMail) throws MessagingException {
        // duplicates the Mail object, to be able to modify the new mail
        // keeping
        // the original untouched
        MailImpl newMail = (MailImpl) aMail.duplicate(newName(aMail));
        // We don't need to use the original Remote Address and Host,
        // and doing so would likely cause a loop with spam detecting
        // matchers.
        try {
            newMail.setRemoteAddr(java.net.InetAddress.getLocalHost()
                    .getHostAddress());
            newMail.setRemoteHost(java.net.InetAddress.getLocalHost()
                    .getHostName());
        } catch (java.net.UnknownHostException _) {
            newMail.setRemoteAddr("127.0.0.1");
            newMail.setRemoteHost("localhost");
        }
        return newMail;
    }

    /**
     * Create a unique new primary key name.
     * 
     * Borrowed from org.apache.james.transport.mailets.AbstractRedirect
     * 
     * @param mail
     *                the mail to use as the basis for the new mail name
     * @return a new name
     */
    private String newName(MailImpl mail) throws MessagingException {
        String oldName = mail.getName();
        // Checking if the original mail name is too long, perhaps because of a
        // loop caused by a configuration error.
        // it could cause a "null pointer exception" in AvalonMailRepository
        // much
        // harder to understand.
        if (oldName.length() > 76) {
            int count = 0;
            int index = 0;
            while ((index = oldName.indexOf('!', index + 1)) >= 0) {
                count++;
            }
            // It looks like a configuration loop. It's better to stop.
            if (count > 7) {
                throw new MessagingException(
                        "Unable to create a new message name: too long."
                                + " Possible loop in config.xml.");
            } else {
                oldName = oldName.substring(0, 76);
            }
        }
        StringBuffer nameBuffer = new StringBuffer(64).append(oldName).append(
                "-!").append(random.nextInt(1048576));
        return nameBuffer.toString();
    }

    /**
     * Method evaluate evaluates the receivers script against aMail.
     * 
     * @param aMail
     * @throws MessagingException
     */
    protected void evaluate(Mail aMail) throws MessagingException {
        // Evaluate the script against the mail
        try {
            MailAdapter aMailAdapter = new SieveMailAdapter(aMail,
                    getMailetContext());
            log("Evaluating " + aMailAdapter.toString() + "against \""
                    + getScriptURL().toExternalForm() + "\"");
            SieveFactory.getInstance().evaluate(aMailAdapter, getStartNode());
        } catch (SieveException ex) {
            log("Exception evaluating Sieve script", ex);
            // If there were errors, we redirect the email to the ERROR
            // processor.
            // In order for this server to meet the requirements of the SMTP
            // specification,
            // mails on the ERROR processor must be returned to the sender.
            // Note that this
            // email doesn't include any details regarding the details of the
            // failure(s).
            // In the future we may wish to address this.
            getMailetContext().sendMail(aMail.getSender(),
                    aMail.getRecipients(), aMail.getMessage(), Mail.ERROR);
            throw new MessagingException("Exception evaluating Sieve script",
                    ex);
        }
    }

    /**
     * Returns the scriptURL.
     * 
     * @return URL
     */
    public URL getScriptURL() {
        return fieldScriptURL;
    }

    /**
     * Sets the scriptURL.
     * 
     * @param scriptURL
     *                The scriptURL to set
     */
    protected void setScriptURL(URL scriptURL) {
        fieldScriptURL = scriptURL;
    }

    /**
     * Returns the startNode, lazy initialised if required.
     * 
     * @return Node
     * @throws MessagingException
     */
    public Node getStartNode() throws MessagingException {
        Node node = null;
        if (null == (node = getStartNodeBasic())) {
            updateStartNode();
            return getStartNode();
        }
        return node;
    }

    /**
     * Returns the startNode.
     * 
     * @return Node
     */
    private Node getStartNodeBasic() {
        return fieldStartNode;
    }

    /**
     * Parses the script and returns its startNode.
     * 
     * @return Node
     * @throws MessagingException
     */
    protected Node computeStartNode() throws MessagingException {
        Object content = null;
        try {
            content = getScriptURL().getContent();
            if (!(content instanceof InputStream)) {
                String msg = "Cannot get an InputStream for "
                        + getScriptURL().toExternalForm();
                log(msg);
                throw new MessagingException(msg);
            }
            return SieveFactory.getInstance().parse(
                    new BufferedInputStream((InputStream) content));
        } catch (IOException ex) {
            String msg = "Exception getting contents of script URL: "
                    + getScriptURL().toExternalForm();
            log(msg, ex);
            throw new MessagingException(msg, ex);
        } catch (ParseException ex) {
            String msg = "Exception parsing Sieve script: "
                    + getScriptURL().toExternalForm();
            log(msg, ex);
            throw new MessagingException(msg, ex);
        }
    }

    /**
     * Sets the startNode.
     * 
     * @param startNode
     *                The startNode to set
     */
    protected void setStartNode(Node startNode) {
        fieldStartNode = startNode;
    }

    /**
     * Updates the startNode.
     * 
     * @throws MessagingException
     */
    protected void updateStartNode() throws MessagingException {
        setStartNode(computeStartNode());
    }

    /**
     * @see org.apache.mailet.GenericMailet#init()
     */
    public void init() throws MessagingException {
        super.init();
        try {
            setScriptURL(new URL(getInitParameter("scriptURL")));
        } catch (MalformedURLException e) {
            throw new MailetException(
                    "Error in configuration parameter \"scriptURL\"", e);
        }
    }
}
