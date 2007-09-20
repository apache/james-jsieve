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

package org.apache.jsieve.junit.commands;

import org.apache.jsieve.CommandStateManager;
import org.apache.jsieve.ConditionManager;

import junit.framework.TestCase;

public class ConcurrencyTest extends TestCase {
    
    public void testConditionManagerReset() throws Exception {
        ConditionManager instance = ConditionManager.getInstance();
        assertNotNull(instance);
        
        class ConcurrentThread extends Thread {
            ConditionManager one = null;
            ConditionManager two = null;
            public void run() {
                one = ConditionManager.getInstance();
                ConditionManager.resetInstance();
                two = ConditionManager.getInstance();
            }
        }
        
        ConcurrentThread otherThread = new ConcurrentThread();
        otherThread.start();
        otherThread.join();

        assertNotNull(otherThread.one);
        assertNotNull(otherThread.two);
        assertFalse(otherThread.one.equals(otherThread.two));
        assertEquals(instance, ConditionManager.getInstance());
    }
    
    public void testCommandStateManagerReset() throws Exception {
        CommandStateManager instance = CommandStateManager.getInstance();
        assertNotNull(instance);
        
        class ConcurrentThread extends Thread {
            CommandStateManager one = null;
            CommandStateManager two = null;
            public void run() {
                one = CommandStateManager.getInstance();
                CommandStateManager.resetInstance();
                two = CommandStateManager.getInstance();
            }
        }
        
        ConcurrentThread otherThread = new ConcurrentThread();
        otherThread.start();
        otherThread.join();

        assertNotNull(otherThread.one);
        assertNotNull(otherThread.two);
        assertFalse(otherThread.one.equals(otherThread.two));
        
        assertEquals(instance, CommandStateManager.getInstance());
    }
}
