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
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
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

public class VacationReplyBuilder {

    private final Mail originalMail;
    private final ActionContext context;
    private String from;
    private String reason;
    private String mime;
    private String subject;

    public VacationReplyBuilder(Mail originalMail, ActionContext context) {
        this.originalMail = originalMail;
        this.context = context;
    }

    public VacationReplyBuilder from(String from) {
        this.from = from;
        return this;
    }

    public VacationReplyBuilder reason(String reason) {
        this.reason = reason;
        return this;
    }

    public VacationReplyBuilder mime(String mime) {
        this.mime = mime;
        return this;
    }

    public VacationReplyBuilder subject(String subject) {
        this.subject = subject;
        return this;
    }

    public void build() throws MessagingException {
        ActionUtils.detectAndHandleLocalLooping(originalMail, context, "vacation");

        Preconditions.checkState((reason == null) != (mime == null));
        MimeMessage reply = (MimeMessage) originalMail.getMessage().reply(false);
        reply.setSubject(generateNotificationSubject());
        reply.setContent(generateNotificationContent());

        context.post(retrieveOriginalSender(),
            Lists.newArrayList(originalMail.getSender()),
            reply);
    }

    private String generateNotificationSubject() {
        return Optional.fromNullable(subject)
            .or(context.getRecipient() + " is currently in vacation");
    }

    private Multipart generateNotificationContent() throws MessagingException {
        try {
            if (reason != null) {
                return generateNotificationContentFromReasonString();
            } else {
                return generateNotificationContentFromMime();
            }
        } catch (IOException e) {
            throw new MessagingException("Cannot read specified content", e);
        }
    }

    private Multipart generateNotificationContentFromMime() throws MessagingException, IOException {
        return new MimeMultipart(new ByteArrayDataSource(mime, "mixed"));
    }

    private Multipart generateNotificationContentFromReasonString() throws MessagingException, IOException {
        Multipart multipart = new MimeMultipart("mixed");
        MimeBodyPart reasonPart = new MimeBodyPart();
        reasonPart.setDataHandler(
            new DataHandler(
                new ByteArrayDataSource(
                    reason,
                    "text/plain; charset=UTF-8")));
        reasonPart.setDisposition(MimeBodyPart.INLINE);
        multipart.addBodyPart(reasonPart);
        return multipart;
    }

    private MailAddress retrieveOriginalSender() throws AddressException {
        return Optional.fromNullable(from).transform(new Function<String, MailAddress>() {
            public MailAddress apply(String address) {
                return retrieveAddressFromString(address, context);
            }
        }).or(context.getRecipient());
    }

    public static MailAddress retrieveAddressFromString(String address, ActionContext context) {
        try {
            return new MailAddress(address);
        } catch (AddressException e) {
            context.getLog().warn("Mail address " + address + " was not well formatted : " + e.getLocalizedMessage());
            return null;
        }
    }

}
