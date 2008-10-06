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

import java.util.Arrays;

import junit.framework.TestCase;

import org.apache.jsieve.mail.Action;
import org.apache.jsieve.mail.ActionFileInto;
import org.apache.jsieve.mail.ActionKeep;
import org.apache.jsieve.mail.ActionRedirect;
import org.apache.jsieve.mail.ActionReject;

public class ScriptCheckerTestActionsTest extends TestCase {

    private static final String REDIRECT_ADDRESS_TWO = "roadrunner@acme.example.org";

    private static final String REDIRECT_ADDRESS_ONE = "coyote@desert.example.org";

    private static final String REJECT_MESSAGE_TWO = "Oh well";

    private static final String REJECT_MESSAGE_ONE = "Better luck next time";

    private static final String DESTINATION_TWO = "org.apache.jakarta";

    private static final String DESTINATION_ONE = "org.apache.james";

    static final Action[] ACTIONS = { new ActionFileInto(DESTINATION_ONE),
            new ActionKeep(), new ActionFileInto(DESTINATION_TWO),
            new ActionReject(REJECT_MESSAGE_ONE),
            new ActionRedirect(REDIRECT_ADDRESS_ONE),
            new ActionRedirect(REDIRECT_ADDRESS_TWO),
            new ActionReject(REJECT_MESSAGE_TWO), };

    ScriptChecker.Results result;

    protected void setUp() throws Exception {
        super.setUp();
        result = new ScriptChecker.Results(Arrays.asList(ACTIONS));
    }

    public void testFileInto() throws Exception {
        assertTrue("Check for file into action with right destination", result
                .isActionFileInto(DESTINATION_ONE, 0));
        assertFalse("Check for file into action with right destination", result
                .isActionFileInto(DESTINATION_ONE, 1));
        assertFalse("Check for file into action with right destination", result
                .isActionFileInto(DESTINATION_ONE, 2));
        assertFalse("Check for file into action with right destination", result
                .isActionFileInto(DESTINATION_ONE, 3));
        assertFalse("Check for file into action with right destination", result
                .isActionFileInto(DESTINATION_ONE, 4));
        assertFalse("Check for file into action with right destination", result
                .isActionFileInto(DESTINATION_ONE, 5));
        assertFalse("Check for file into action with right destination", result
                .isActionFileInto(DESTINATION_ONE, 6));
        assertFalse("Check for file into action with right destination", result
                .isActionFileInto(DESTINATION_TWO, 0));
        assertFalse("Check for file into action with right destination", result
                .isActionFileInto(DESTINATION_TWO, 1));
        assertTrue("Check for file into action with right destination", result
                .isActionFileInto(DESTINATION_TWO, 2));
        assertFalse("Check for file into action with right destination", result
                .isActionFileInto(DESTINATION_TWO, 3));
        assertFalse("Check for file into action with right destination", result
                .isActionFileInto(DESTINATION_TWO, 4));
        assertFalse("Check for file into action with right destination", result
                .isActionFileInto(DESTINATION_TWO, 5));
        assertFalse("Check for file into action with right destination", result
                .isActionFileInto(DESTINATION_TWO, 6));
    }

    public void testRedirect() throws Exception {
        assertFalse("Check for redirect action with right message", result
                .isActionRedirect(REDIRECT_ADDRESS_ONE, 0));
        assertFalse("Check for redirect action with right message", result
                .isActionRedirect(REDIRECT_ADDRESS_ONE, 1));
        assertFalse("Check for redirect action with right message", result
                .isActionRedirect(REDIRECT_ADDRESS_ONE, 2));
        assertFalse("Check for redirect action with right message", result
                .isActionRedirect(REDIRECT_ADDRESS_ONE, 3));
        assertTrue("Check for redirect action with right message", result
                .isActionRedirect(REDIRECT_ADDRESS_ONE, 4));
        assertFalse("Check for redirect action with right message", result
                .isActionRedirect(REDIRECT_ADDRESS_ONE, 5));
        assertFalse("Check for redirect action with right message", result
                .isActionRedirect(REDIRECT_ADDRESS_ONE, 6));
        assertFalse("Check for redirect action with right message", result
                .isActionRedirect(REDIRECT_ADDRESS_TWO, 0));
        assertFalse("Check for redirect action with right message", result
                .isActionRedirect(REDIRECT_ADDRESS_TWO, 1));
        assertFalse("Check for redirect action with right message", result
                .isActionRedirect(REDIRECT_ADDRESS_TWO, 2));
        assertFalse("Check for redirect action with right message", result
                .isActionRedirect(REDIRECT_ADDRESS_TWO, 3));
        assertFalse("Check for redirect action with right message", result
                .isActionRedirect(REDIRECT_ADDRESS_TWO, 4));
        assertTrue("Check for redirect action with right message", result
                .isActionRedirect(REDIRECT_ADDRESS_TWO, 5));
        assertFalse("Check for redirect action with right message", result
                .isActionRedirect(REDIRECT_ADDRESS_TWO, 6));
    }

    public void testReject() throws Exception {
        assertFalse("Check for reject action with right message", result
                .isActionReject(REJECT_MESSAGE_ONE, 0));
        assertFalse("Check for reject action with right message", result
                .isActionReject(REJECT_MESSAGE_ONE, 1));
        assertFalse("Check for reject action with right message", result
                .isActionReject(REJECT_MESSAGE_ONE, 2));
        assertTrue("Check for reject action with right message", result
                .isActionReject(REJECT_MESSAGE_ONE, 3));
        assertFalse("Check for reject action with right message", result
                .isActionReject(REJECT_MESSAGE_ONE, 4));
        assertFalse("Check for reject action with right message", result
                .isActionReject(REJECT_MESSAGE_ONE, 5));
        assertFalse("Check for reject action with right message", result
                .isActionReject(REJECT_MESSAGE_ONE, 6));
        assertFalse("Check for reject action with right message", result
                .isActionReject(REJECT_MESSAGE_TWO, 0));
        assertFalse("Check for reject action with right message", result
                .isActionReject(REJECT_MESSAGE_TWO, 1));
        assertFalse("Check for reject action with right message", result
                .isActionReject(REJECT_MESSAGE_TWO, 2));
        assertFalse("Check for reject action with right message", result
                .isActionReject(REJECT_MESSAGE_TWO, 3));
        assertFalse("Check for reject action with right message", result
                .isActionReject(REJECT_MESSAGE_TWO, 4));
        assertFalse("Check for reject action with right message", result
                .isActionReject(REJECT_MESSAGE_TWO, 5));
        assertTrue("Check for reject action with right message", result
                .isActionReject(REJECT_MESSAGE_TWO, 6));
    }

    public void testKeep() throws Exception {
        assertFalse("Check for keep action ", result.isActionKeep(0));
        assertTrue("Check for keep action ", result.isActionKeep(1));
        assertFalse("Check for keep action ", result.isActionKeep(2));
        assertFalse("Check for keep action ", result.isActionKeep(3));
        assertFalse("Check for keep action ", result.isActionKeep(4));
        assertFalse("Check for keep action ", result.isActionKeep(5));
        assertFalse("Check for keep action ", result.isActionKeep(6));
    }
}
