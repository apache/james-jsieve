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

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.jsieve.exception.SyntaxException;
import org.apache.jsieve.mail.Action;
import org.apache.jsieve.mail.ActionDiscard;
import org.apache.jsieve.mail.MailAdapter;
import org.apache.jsieve.utils.JUnitUtils;
import org.assertj.core.api.Condition;
import org.junit.Test;

public class DiscardTest {

    private static final Condition<Action> INSTANCE_OF_ACTION_DISCARDED = new Condition<Action>() {
        @Override
        public boolean matches(Action action) {
            return action instanceof ActionDiscard;
        }
    };

    @Test(expected = SyntaxException.class)
    public void discardShouldThrowOnInvalidArguments() throws Exception {
        String script = "discard 1 ;";

        JUnitUtils.interpret(JUnitUtils.createMail(), script);
    }

    @Test(expected = SyntaxException.class)
    public void discardShouldThrowOnInvalidFollowingBlock() throws Exception {
        String script = "discard {throwTestException;}";

        JUnitUtils.interpret(JUnitUtils.createMail(), script);
    }

    @Test
    public void discardShouldAddOnlyActionDiscard() throws Exception {
        String script = "discard;";

        MailAdapter mail = JUnitUtils.createMail();
        JUnitUtils.interpret(mail, script);

        assertThat(mail.getActions())
            .hasSize(1)
            .are(INSTANCE_OF_ACTION_DISCARDED);
    }

}
