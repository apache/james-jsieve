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

import org.apache.jsieve.mail.Action;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ScriptCheckMailAdapterMailTest {

    ScriptCheckMailAdapter adapter;

    Action action;

    Action anotherAction;

    @Before
    public void setUp() throws Exception {
        adapter = new ScriptCheckMailAdapter();
        action = new MockAction();
        anotherAction = new MockAction();
    }

    @Test
    public void testSetMail() throws Exception {
        adapter.addAction(action);
        adapter.addAction(anotherAction);
        adapter.executeActions();
        Assert.assertEquals("Two actions executed", 2, adapter.getExecutedActions()
                .size());
        Assert.assertEquals("Two actions", 2, adapter.getActions().size());
        adapter.setMail(null);
        Assert.assertEquals("Set mail resets", 0, adapter.getExecutedActions().size());
        Assert.assertEquals("Set mail resets", 0, adapter.getActions().size());
    }

}
