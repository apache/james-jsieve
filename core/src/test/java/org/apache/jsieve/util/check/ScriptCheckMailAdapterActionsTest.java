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

public class ScriptCheckMailAdapterActionsTest {

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
    public void testAddAction() {
        adapter.addAction(action);
        Assert.assertEquals("Running total updated", 1, adapter.getActions().size());
        Assert.assertEquals("Running total updated", action, adapter.getActions().get(
                0));
        adapter.addAction(anotherAction);
        Assert.assertEquals("Order preserved", 2, adapter.getActions().size());
        Assert.assertEquals("Order preserved", anotherAction, adapter.getActions()
                .get(1));
    }

    @Test
    public void testExecuteActions() throws Exception {
        Assert.assertNotNull(adapter.getExecutedActions());
        Assert.assertEquals("No actions executed", 0, adapter.getExecutedActions()
                .size());
        adapter.addAction(action);
        Assert.assertNotNull(adapter.getExecutedActions());
        Assert.assertEquals("No actions executed", 0, adapter.getExecutedActions()
                .size());
        adapter.executeActions();
        Assert.assertNotNull(adapter.getExecutedActions());
        Assert.assertEquals("One action executed", 1, adapter.getExecutedActions()
                .size());
    }

    @Test
    public void testGetActions() {
        Assert.assertNotNull(adapter.getActions());
        try {
            adapter.getActions().add(new Action() {
            });
            Assert.fail("Should not be able to modify collection");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        adapter.addAction(action);
        Assert.assertNotNull(adapter.getActions());
        Assert.assertEquals("Running total updated", 1, adapter.getActions().size());
        Assert.assertEquals("Running total updated", action, adapter.getActions().get(
                0));
        adapter.addAction(anotherAction);
        Assert.assertNotNull(adapter.getActions());
        Assert.assertEquals("Order preserved", 2, adapter.getActions().size());
        Assert.assertEquals("Order preserved", anotherAction, adapter.getActions()
                .get(1));
    }

    @Test
    public void testGetExecutedActions() throws Exception {
        Assert.assertNotNull(adapter.getExecutedActions());
        Assert.assertEquals("No actions executed", 0, adapter.getExecutedActions()
                .size());
        adapter.addAction(action);
        Assert.assertNotNull(adapter.getExecutedActions());
        Assert.assertEquals("No actions executed", 0, adapter.getExecutedActions()
                .size());
        adapter.executeActions();
        Assert.assertEquals("One action executed", 1, adapter.getExecutedActions()
                .size());
        Assert.assertEquals("One action executed", action, adapter
                .getExecutedActions().get(0));
        adapter.addAction(anotherAction);
        Assert.assertEquals("One action executed", 1, adapter.getExecutedActions()
                .size());
        Assert.assertEquals("One action executed", action, adapter
                .getExecutedActions().get(0));
        adapter.executeActions();
        Assert.assertEquals("Two actions executed", 2, adapter.getExecutedActions()
                .size());
        Assert.assertEquals("Two actions executed", action, adapter
                .getExecutedActions().get(0));
        Assert.assertEquals("Two actions executed", anotherAction, adapter
                .getExecutedActions().get(1));
        adapter.getExecutedActions().add(new Action() {
        });
        Assert.assertEquals("Two actions executed", 2, adapter.getExecutedActions()
                .size());
        Assert.assertEquals("Two actions executed", action, adapter
                .getExecutedActions().get(0));
        Assert.assertEquals("Two actions executed", anotherAction, adapter
                .getExecutedActions().get(1));
        adapter.executeActions();
        Assert.assertEquals("Two actions executed", 2, adapter.getExecutedActions()
                .size());
        Assert.assertEquals("Two actions executed", action, adapter
                .getExecutedActions().get(0));
        Assert.assertEquals("Two actions executed", anotherAction, adapter
                .getExecutedActions().get(1));
    }

    @Test
    public void testReset() throws Exception {
        adapter.addAction(action);
        adapter.addAction(anotherAction);
        adapter.executeActions();
        Assert.assertEquals("Two actions executed", 2, adapter.getExecutedActions()
                .size());
        Assert.assertEquals("Two actions", 2, adapter.getActions().size());
        adapter.reset();
        Assert.assertEquals("Two actions executed", 0, adapter.getExecutedActions()
                .size());
        Assert.assertEquals("Two actions", 0, adapter.getActions().size());
    }
}
