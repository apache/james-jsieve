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

package org.apache.jsieve.mailet;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.jsieve.mail.Action;
import org.apache.jsieve.mail.optional.ActionVacation;
import org.apache.mailet.Mail;
import org.apache.mailet.MailAddress;

import javax.activation.DataHandler;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.AddressException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class VacationAction implements MailAction {

    @Override
    public void execute(Action action, Mail mail, ActionContext context) throws MessagingException {
        ActionVacation actionVacation = (ActionVacation) action;
        long dayDifference = getDayDifference(context.getScriptInterpretationDate(), context.getScriptStorageDate());
        if (isStillInVacation(actionVacation, dayDifference)) {
            if (isValidForReply(mail, actionVacation, context)) {
                sendVacationNotification(mail, actionVacation, context);
            }
        }
    }

    private void sendVacationNotification(Mail mail, ActionVacation actionVacation, ActionContext context) throws MessagingException {
        MimeMessage reply = (MimeMessage) mail.getMessage().reply(false);
        reply.setSubject(generateNotificationSubject(actionVacation, context));
        try {
            reply.setContent(generateNotificationContent(actionVacation));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ActionUtils.detectAndHandleLocalLooping(mail, context, "vacation");
        context.post(generateNotificationFromHeaderField(actionVacation, context), Lists.newArrayList(mail.getSender()), reply);
    }

    private boolean isStillInVacation(ActionVacation actionVacation, long dayDifference) {
        return dayDifference >= 0 && dayDifference <= actionVacation.getDuration();
    }

    private boolean isValidForReply(final Mail mail, ActionVacation actionVacation, final ActionContext context) {
        Set<MailAddress> currentMailAddresses = Sets.newHashSet(mail.getRecipients());
        Set<MailAddress> allowedMailAddresses = Sets.newHashSet();
        allowedMailAddresses.add(context.getRecipient());
        List<String> validMailAddressFoMailReceiverAsString = actionVacation.getAddresses();
        for(String address : validMailAddressFoMailReceiverAsString) {
            allowedMailAddresses.add(retrieveAddressFromString(address, context));
        }
        return !Sets.intersection(currentMailAddresses, allowedMailAddresses).isEmpty();
    }

    private String generateNotificationSubject(ActionVacation actionVacation, ActionContext context) {
        return Optional.fromNullable(actionVacation.getSubject())
            .or(context.getRecipient() + " is currently in vacation");
    }

    private MailAddress generateNotificationFromHeaderField(ActionVacation actionVacation, final ActionContext context) throws AddressException {
        return Optional.fromNullable(actionVacation.getFrom()).transform(new Function<String, MailAddress>() {
            public MailAddress apply(String address) {
                return retrieveAddressFromString(address, context);
            }
        }).or(context.getRecipient());
    }

    private MailAddress retrieveAddressFromString(String address, ActionContext context) {
        try {
            return new MailAddress(address);
        } catch (AddressException e) {
            context.getLog().warn("Mail address " + address + " was not well formatted : " + e.getLocalizedMessage());
            return null;
        }
    }

    private Multipart generateNotificationContent(ActionVacation actionVacation) throws MessagingException, IOException {
        if (actionVacation.getReason() != null) {
            return generateNotificationContentFromReasonString(actionVacation);
        } else {
            return generateNotificationContentFromMime(actionVacation);
        }
    }

    private Multipart generateNotificationContentFromMime(ActionVacation actionVacation) throws MessagingException, IOException {
        Multipart multipart = new MimeMultipart(new ByteArrayDataSource(actionVacation.getMime(), "mixed"));
        MimeBodyPart reasonPart = new MimeBodyPart();
        try {
            reasonPart.setDataHandler(
                new DataHandler(
                    new ByteArrayDataSource(
                        actionVacation.getReason(),
                        "text/plain; charset=UTF-8")
                ));
        } catch (IOException e) {
            throw new MessagingException("Exception while executing data handler", e);
        }
        reasonPart.setDisposition(MimeBodyPart.INLINE);
        multipart.addBodyPart(reasonPart);
        return multipart;
    }

    private Multipart generateNotificationContentFromReasonString(ActionVacation actionVacation) throws MessagingException, IOException {
        Multipart multipart = new MimeMultipart("mixed");
        MimeBodyPart reasonPart = new MimeBodyPart();
        reasonPart.setDataHandler(
            new DataHandler(
                new ByteArrayDataSource(
                    actionVacation.getReason(),
                    "text/plain; charset=UTF-8")
            ));
        reasonPart.setDisposition(MimeBodyPart.INLINE);
        multipart.addBodyPart(reasonPart);
        return multipart;
    }

    public long getDayDifference(Date date1, Date date2) {
        long msDifference = date1.getTime() - date2.getTime();
        return TimeUnit.DAYS.convert(msDifference, TimeUnit.MILLISECONDS);
    }
}
