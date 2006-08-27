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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.james.util.mail.mdn.ActionModeAutomatic;
import org.apache.james.util.mail.mdn.Disposition;
import org.apache.james.util.mail.mdn.DispositionModifier;
import org.apache.james.util.mail.mdn.MDNFactory;
import org.apache.james.util.mail.mdn.ModifierError;
import org.apache.james.util.mail.mdn.SendingModeAutomatic;
import org.apache.james.util.mail.mdn.TypeDeleted;
import org.apache.jsieve.mail.ActionFileInto;
import org.apache.jsieve.mail.ActionKeep;
import org.apache.jsieve.mail.ActionRedirect;
import org.apache.jsieve.mail.ActionReject;
import org.apache.mailet.Mail;
import org.apache.mailet.MailAddress;
import org.apache.mailet.MailetContext;

/**
 * Singleton Class <code>Actions</code> implements <code>execute()</code>
 * methods for each of the supported Actions.
 */
public class Actions
{
    static private String fieldAttributePrefix;

    /**
     * Constructor for Actions.
     */
    private Actions()
    {
        super();
    }

    /**
     * <p>
     * Executes the passed ActionFileInto.
     * </p>
     * 
     * <p>
     * This implementation accepts any destination with the root of <code>INBOX</code>.
     * </p>
     * 
     * <p>
     * As the current POP3 server does not support sub-folders, the mail is
     * stored in the INBOX for the recipient of the mail and the full intended
     * destination added as a prefix to the message's subject.
     * </p>
     * 
     * <p>
     * When IMAP support is added to James, it will be possible to support
     * sub-folders of <code>INBOX</code> fully.
     * </p>
     * 
     * @param anAction
     * @param aMail
     * @param aMailetContext
     * @throws MessagingException
     */
    static public void execute(ActionFileInto anAction, Mail aMail,
            MailetContext aMailetContext) throws MessagingException
    {
        StringBuffer repositoryDestinationBuffer = null;
        MailAddress recipient = null;
        boolean delivered = false;
        try
        {
            recipient = getSoleRecipient(aMail);
            // Validate and translate the destination
            // Note that we do not check that the repository implementation
            // supports the destination or that it exists. That is left
            // to the repository implementation
            int endOfRootDestination = anAction.getDestination().indexOf('/');
            String rootDestination = (endOfRootDestination > -1
                    ? anAction.getDestination().substring(0,
                            endOfRootDestination)
                    : anAction.getDestination());
            if (!rootDestination.equalsIgnoreCase("INBOX"))
                throw new DestinationException("Folder: \""
                        + anAction.getDestination() + "\"");
            repositoryDestinationBuffer = new StringBuffer(recipient.toString());
            if (endOfRootDestination > -1)
                repositoryDestinationBuffer.append(anAction.getDestination()
                        .substring(endOfRootDestination).replace('/', '.'));
            // Adapted from LocalDelivery Mailet
            // Add qmail's de facto standard Delivered-To header
            MimeMessage localMessage = new MimeMessage(aMail.getMessage())
            {
                protected void updateHeaders() throws MessagingException
                {
                    if (getMessageID() == null)
                        super.updateHeaders();
                    else
                        modified = false;
                }
            };
            localMessage.addHeader("Delivered-To", recipient.toString());
            // For now, a prefix "[destination]" is added to the
            // header so that we can see what the true destination
            // would have been with support for sub-folders
            localMessage.setSubject("["
                    + repositoryDestinationBuffer.toString() + "] "
                    + localMessage.getSubject());
            localMessage.saveChanges();
            // NOTE: recipient will have to change to a MailAddress built
            // from the repositoryDestinationBuffer
            aMailetContext
                    .storeMail(aMail.getSender(), recipient, localMessage);
            delivered = true;
        }
        catch (MessagingException ex)
        {
            aMailetContext.log("Error while storing mail.", ex);
            throw ex;
        }
        finally
        {
            // Ensure the mail is always ghosted
            aMail.setState(Mail.GHOST);
        }
        if (delivered)
        {
            aMailetContext.log("Filed Message ID: "
                    + aMail.getMessage().getMessageID()
                    + " into destination: \""
                    + repositoryDestinationBuffer.toString() + "\"");
        }
    }

    /**
     * <p>
     * Executes the passed ActionKeep.
     * </p>
     * 
     * <p>
     * In this implementation, "keep" is equivalent to "fileinto" with a
     * destination of "INBOX".
     * </p>
     * 
     * @param anAction
     * @param aMail
     * @param aMailetContext
     * @throws MessagingException
     */
    public static void execute(ActionKeep anAction, Mail aMail,
            MailetContext aMailetContext) throws MessagingException
    {
        ActionFileInto action = new ActionFileInto("INBOX");
        execute(action, aMail, aMailetContext);
    }

    /**
     * Method execute executes the passed ActionRedirect.
     * 
     * @param anAction
     * @param aMail
     * @param aMailetContext
     * @throws MessagingException
     */
    public static void execute(ActionRedirect anAction, Mail aMail,
            MailetContext aMailetContext) throws MessagingException
    {
        detectAndHandleLocalLooping(aMail, aMailetContext, "redirect");
        Collection recipients = new ArrayList(1);
        recipients.add(new InternetAddress(anAction.getAddress()));
        aMailetContext.sendMail(aMail.getSender(), recipients, aMail
                .getMessage());
        aMail.setState(Mail.GHOST);
        aMailetContext.log("Redirected Message ID: "
                + aMail.getMessage().getMessageID() + " to \""
                + anAction.getAddress() + "\"");
    }

    /**
     * <p>
     * Method execute executes the passed ActionReject. It sends an RFC 2098
     * compliant reject MDN back to the sender.
     * </p>
     * <p>
     * NOTE: The Mimecontent type should be 'report', but as we do not yet have
     * a DataHandler for this yet, its currently 'text'!
     * 
     * @param anAction
     * @param aMail
     * @param aMailetContext
     * @throws MessagingException
     */
    public static void execute(ActionReject anAction, Mail aMail,
            MailetContext aMailetContext) throws MessagingException
    {
        detectAndHandleLocalLooping(aMail, aMailetContext, "reject");

        // Create the MDN part
        StringBuffer humanText = new StringBuffer(128);
        humanText
                .append("This message was refused by the recipient's mail filtering program.");
        humanText.append("\r\n");
        humanText.append("The reason given was:");
        humanText.append("\r\n");
        humanText.append("\r\n");
        humanText.append(anAction.getMessage());

        String reporting_UA_name = null;
        try
        {
            reporting_UA_name = InetAddress.getLocalHost()
                    .getCanonicalHostName();
        }
        catch (UnknownHostException ex)
        {
            reporting_UA_name = "localhost";
        }

        String reporting_UA_product = aMailetContext.getServerInfo();

        String[] originalRecipients = aMail.getMessage().getHeader(
                "Original-Recipient");
        String original_recipient = null;
        if (null != originalRecipients && originalRecipients.length > 0)
        {
            original_recipient = originalRecipients[0];
        }

        MailAddress soleRecipient = getSoleRecipient(aMail);
        String final_recipient = soleRecipient.toString();

        String original_message_id = aMail.getMessage().getMessageID();

        DispositionModifier modifiers[] = {new ModifierError()};
        Disposition disposition = new Disposition(new ActionModeAutomatic(),
                new SendingModeAutomatic(), new TypeDeleted(), modifiers);

        MimeMultipart multiPart = MDNFactory.create(humanText.toString(),
                reporting_UA_name, reporting_UA_product, original_recipient,
                final_recipient, original_message_id, disposition);

        // Send the message
        MimeMessage reply = (MimeMessage) aMail.getMessage().reply(false);
        reply.setFrom(soleRecipient.toInternetAddress());
        reply.setContent(multiPart);
        reply.saveChanges();
        Address[] recipientAddresses = reply.getAllRecipients();
        if (null != recipientAddresses)
        {
            Collection recipients = new ArrayList(recipientAddresses.length);
            for (int i = 0; i < recipientAddresses.length; i++)
            {
                recipients.add(new MailAddress(
                        (InternetAddress) recipientAddresses[i]));
            }
            aMailetContext.sendMail(null, recipients, reply);
        }
        else
        {
            aMailetContext
                    .log("Unable to send reject MDN. Could not determine the recipient.");
        }
        // Ghost the original mail
        aMail.setState(Mail.GHOST);
    }

    /**
     * Answers the sole intended recipient for aMail.
     * 
     * @param aMail
     * @return String
     * @throws MessagingException
     */
    protected static MailAddress getSoleRecipient(Mail aMail)
            throws MessagingException
    {
    	  if (aMail.getRecipients() == null) {
          throw new MessagingException("Invalid number of recipients - 0"
              + ". Exactly 1 recipient is expected.");
    	  } else if (1 != aMail.getRecipients().size())
            throw new MessagingException("Invalid number of recipients - "
                    + new Integer(aMail.getRecipients().size()).toString()
                    + ". Exactly 1 recipient is expected.");
        return (MailAddress) aMail.getRecipients().iterator().next();
    }

    /**
     * Returns a lazy initialised attributePrefix.
     * 
     * @return String
     */
    protected static String getAttributePrefix()
    {
        String value = null;
        if (null == (value = getAttributePrefixBasic()))
        {
            updateAttributePrefix();
            return getAttributePrefix();
        }
        return value;
    }

    /**
     * Returns the attributePrefix.
     * 
     * @return String
     */
    private static String getAttributePrefixBasic()
    {
        return fieldAttributePrefix;
    }

    /**
     * Returns the computed attributePrefix.
     * 
     * @return String
     */
    protected static String computeAttributePrefix()
    {
        return Actions.class.getPackage().getName() + ".";
    }

    /**
     * Sets the attributePrefix.
     * 
     * @param attributePrefix The attributePrefix to set
     */
    protected static void setAttributePrefix(String attributePrefix)
    {
        fieldAttributePrefix = attributePrefix;
    }

    /**
     * Updates the attributePrefix.
     */
    protected static void updateAttributePrefix()
    {
        setAttributePrefix(computeAttributePrefix());
    }

    /**
     * Detect and handle locally looping mail. External loop detection is left
     * to the MTA.
     * 
     * @param aMail
     * @param aMailetContext
     * @param anAttributeSuffix
     * @throws MessagingException
     */
    protected static void detectAndHandleLocalLooping(Mail aMail,
            MailetContext aMailetContext, String anAttributeSuffix)
            throws MessagingException
    {
        MailAddress thisRecipient = getSoleRecipient(aMail);
        MailAddress lastRecipient = (MailAddress) aMail
                .getAttribute(getAttributePrefix() + anAttributeSuffix);
        if (null != lastRecipient && lastRecipient.equals(thisRecipient))
        {
            MessagingException ex = new MessagingException(
                    "This message is looping! Message ID: "
                            + aMail.getMessage().getMessageID());
            aMailetContext.log(ex.getMessage(), ex);
            throw ex;
        }
        aMail.setAttribute(getAttributePrefix() + anAttributeSuffix,
                thisRecipient);
    }
}
