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

import java.util.ListIterator;

import junit.framework.TestCase;

import org.apache.jsieve.mail.Action;

public class ScriptCheckMailAdapterActionsTest extends TestCase {

    ScriptCheckMailAdapter adapter;

    Action action;

    Action anotherAction;

    protected void setUp() throws Exception {
        super.setUp();
        adapter = new ScriptCheckMailAdapter();
        action = new MockAction();
        anotherAction = new MockAction();
    }

    public void testAddAction() {
        adapter.addAction(action);
        assertEquals("Running total updated", 1, adapter.getActions().size());
        assertEquals("Running total updated", action, adapter.getActions().get(
                0));
        adapter.addAction(anotherAction);
        assertEquals("Order preserved", 2, adapter.getActions().size());
        assertEquals("Order preserved", anotherAction, adapter.getActions()
                .get(1));
    }

    public void testExecuteActions() throws Exception {
        assertNotNull(adapter.getExecutedActions());
        assertEquals("No actions executed", 0, adapter.getExecutedActions()
                .size());
        adapter.addAction(action);
        assertNotNull(adapter.getExecutedActions());
        assertEquals("No actions executed", 0, adapter.getExecutedActions()
                .size());
        adapter.executeActions();
        assertNotNull(adapter.getExecutedActions());
        assertEquals("One action executed", 1, adapter.getExecutedActions()
                .size());
    }

    public void testGetActions() {
        assertNotNull(adapter.getActions());
        try {
            adapter.getActions().add(new Action() {});
            fail("Should not be able to modify collection");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        adapter.addAction(action);
        assertNotNull(adapter.getActions());
        assertEquals("Running total updated", 1, adapter.getActions().size());
        assertEquals("Running total updated", action, adapter.getActions().get(
                0));
        adapter.addAction(anotherAction);
        assertNotNull(adapter.getActions());
        assertEquals("Order preserved", 2, adapter.getActions().size());
        assertEquals("Order preserved", anotherAction, adapter.getActions()
                .get(1));
    }

    public void testGetExecutedActions() throws Exception {
        assertNotNull(adapter.getExecutedActions());
        assertEquals("No actions executed", 0, adapter.getExecutedActions()
                .size());
        adapter.addAction(action);
        assertNotNull(adapter.getExecutedActions());
        assertEquals("No actions executed", 0, adapter.getExecutedActions()
                .size());
        adapter.executeActions();
        assertEquals("One action executed", 1, adapter.getExecutedActions()
                .size());
        assertEquals("One action executed", action, adapter
                .getExecutedActions().get(0));
        adapter.addAction(anotherAction);
        assertEquals("One action executed", 1, adapter.getExecutedActions()
                .size());
        assertEquals("One action executed", action, adapter
                .getExecutedActions().get(0));
        adapter.executeActions();
        assertEquals("Two actions executed", 2, adapter.getExecutedActions()
                .size());
        assertEquals("Two actions executed", action, adapter
                .getExecutedActions().get(0));
        assertEquals("Two actions executed", anotherAction, adapter
                .getExecutedActions().get(1));
        adapter.getExecutedActions().add(new Action(){});
        assertEquals("Two actions executed", 2, adapter.getExecutedActions()
                .size());
        assertEquals("Two actions executed", action, adapter
                .getExecutedActions().get(0));
        assertEquals("Two actions executed", anotherAction, adapter
                .getExecutedActions().get(1));
        adapter.executeActions();
        assertEquals("Two actions executed", 2, adapter.getExecutedActions()
                .size());
        assertEquals("Two actions executed", action, adapter
                .getExecutedActions().get(0));
        assertEquals("Two actions executed", anotherAction, adapter
                .getExecutedActions().get(1));
    }

    public void testReset() throws Exception {
        adapter.addAction(action);
        adapter.addAction(anotherAction);
        adapter.executeActions();
        assertEquals("Two actions executed", 2, adapter.getExecutedActions()
                .size());
        assertEquals("Two actions", 2, adapter.getActions().size());
        adapter.reset();
        assertEquals("Two actions executed", 0, adapter.getExecutedActions()
                .size());
        assertEquals("Two actions", 0, adapter.getActions().size());
    }
}
