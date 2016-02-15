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

import org.apache.jsieve.exception.SieveException;
import org.apache.jsieve.exception.SyntaxException;
import org.apache.jsieve.mail.Action;

import java.util.ArrayList;
import java.util.List;

/**
 * Class ActionVacation encapsulates the information required to reject a mail.
 * See RFC 5230, Section 4.1.
 */
public class ActionVacation implements Action {

    public static class ActionVacationBuilder {

        private static final int SITE_DEFINED_DEFAULT_VACATION_DURATION = 7;
        private static final int MINIMUM_VACATION_DURATION = 1;

        private String subject;
        private String from;
        private List<String> addresses = new ArrayList<String>();
        private String handle;
        private String reason;
        private String mime;
        private Integer duration;

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
            if (addresses != null) {
                this.addresses = addresses;
            }
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

        public ActionVacationBuilder duration(Integer duration) {
            this.duration = duration;
            return this;
        }

        public ActionVacation build() throws SyntaxException {
            if (!hasOnlyOneReasonOrMime()) {
                throw new SyntaxException("vacation need you to set you either the reason string or a MIME message after tag :mime");
            }
            return new ActionVacation(subject, from, addresses, reason, computeDuration(duration), handle, mime);
        }

        private int computeDuration(Integer duration) {
            if (duration == null) {
                return SITE_DEFINED_DEFAULT_VACATION_DURATION;
            }
            if (duration < MINIMUM_VACATION_DURATION) {
                return MINIMUM_VACATION_DURATION;
            } else {
                return duration;
            }
        }

        private boolean hasOnlyOneReasonOrMime() {
            return (reason == null) != (mime == null);
        }
    }

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
        if (o == null || getClass() != o.getClass())  {
            return false;
        }

        ActionVacation that = (ActionVacation) o;

        return duration == that.duration
            && equalsNullProtected(this.subject, subject)
            && equalsNullProtected(this.from, from)
            && equalsNullProtected(this.handle, handle)
            && equalsNullProtected(this.reason, reason)
            && equalsNullProtected(this.mime, mime);
    }

    private boolean equalsNullProtected(Object object1, Object object2) {
        if (object1 == null) {
            return object2 == null;
        }
        return object1.equals(object2);
    }

    @Override
    public int hashCode() {
        int result = hashCodeNullProtected(subject);
        result = 31 * result + hashCodeNullProtected(from);
        result = 31 * result + hashCodeNullProtected(handle);
        result = 31 * result + hashCodeNullProtected(reason);
        result = 31 * result + hashCodeNullProtected(mime);
        result = 31 * result + hashCodeNullProtected(subject);
        result = 31 * result + duration;
        return result;
    }

    public int hashCodeNullProtected(Object object) {
        if (object == null) {
            return 0;
        }
        return object.hashCode();
    }

}
