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

package org.apache.jsieve.mail.optional;

import org.apache.jsieve.mail.Action;

import java.util.ArrayList;
import java.util.List;

/**
 * Class ActionVacation encapsulates the information required to reject a mail.
 * See RFC 5230, Section 4.1.
 */
public class ActionVacation implements Action {

    public static class ActionVacationBuilder {
        private String subject;
        private String from;
        private List<String> addresses = new ArrayList<String>();
        private String handle;
        private String reason;
        private String mime;
        private int duration;

        public ActionVacationBuilder() {
            duration = SITE_DEFINED_DEFAULT_VACATION_DURATION;
        }

        public ActionVacationBuilder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public ActionVacationBuilder from(String from) {
            this.from = from;
            return this;
        }

        public ActionVacationBuilder addresses(List<String> addresses) {
            this.addresses = addresses;
            return this;
        }

        public ActionVacationBuilder handle(String handle) {
            this.handle = handle;
            return this;
        }

        public ActionVacationBuilder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public ActionVacationBuilder mime(String mime) {
            this.mime = mime;
            return this;
        }

        public ActionVacationBuilder duration(int duration) {
            if (duration < INTMINIMUM_VACATION_DURATION) {
                this.duration = INTMINIMUM_VACATION_DURATION;
            } else {
                this.duration = duration;
            }
            return this;
        }

        public ActionVacation build() {
            return new ActionVacation(subject, from, addresses, reason, duration, handle, mime);
        }
    }

    private static final int SITE_DEFINED_DEFAULT_VACATION_DURATION = 7;
    private static final int INTMINIMUM_VACATION_DURATION = 1;

    public static ActionVacationBuilder builder() {
        return new ActionVacationBuilder();
    }

    private final String subject;
    private final String from;
    private final List<String> addresses;
    private final String handle;
    private final String reason;
    private final String mime;
    private final int duration;

    private ActionVacation(String subject, String from, List<String> addresses, String reason, int duration, String handle, String mime) {
        this.subject = subject;
        this.from = from;
        this.addresses = addresses;
        this.reason = reason;
        this.duration = duration;
        this.handle = handle;
        this.mime = mime;
    }

    public String getSubject() {
        return subject;
    }

    public String getFrom() {
        return from;
    }

    public List<String> getAddresses() {
        return addresses;
    }

    public String getHandle() {
        return handle;
    }

    public String getReason() {
        return reason;
    }

    public int getDuration() {
        return duration;
    }

    public String getMime() {
        return mime;
    }

    public String toString() {
        return "Action: " + getClass().getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ActionVacation that = (ActionVacation) o;

        if (duration != that.duration) return false;
        if (subject != null ? !subject.equals(that.subject) : that.subject != null) return false;
        if (from != null ? !from.equals(that.from) : that.from != null) return false;
        if (handle != null ? !handle.equals(that.handle) : that.handle != null) return false;
        if (reason != null ? !reason.equals(that.reason) : that.reason != null) return false;
        return mime != null ? mime.equals(that.mime) : that.mime == null;

    }

    @Override
    public int hashCode() {
        int result = subject != null ? subject.hashCode() : 0;
        result = 31 * result + (from != null ? from.hashCode() : 0);
        result = 31 * result + (handle != null ? handle.hashCode() : 0);
        result = 31 * result + (reason != null ? reason.hashCode() : 0);
        result = 31 * result + (mime != null ? mime.hashCode() : 0);
        result = 31 * result + duration;
        return result;
    }
}
