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

import org.apache.jsieve.comparators.Comparator;
import org.apache.jsieve.exception.LookupException;
import org.apache.jsieve.tests.ExecutableTest;

/**
 * Bean based implementation of context.
 * 
 */
public class BaseSieveContext extends SieveContext {

    private ScriptCoordinate coordinate;
    private ConditionManager conditionManager;
    
    private final CommandStateManager commandStateManager;
    private final CommandManager commandManager;
    private final ComparatorManager comparatorManager;
    private final TestManager testManager;
    
    public BaseSieveContext(final CommandManager commandManager, final ComparatorManager comparatorManager,
            final TestManager testManager) 
    {
        this.commandStateManager = new CommandStateManager();
        this.conditionManager = new ConditionManager();
        this.testManager = testManager;
        this.commandManager = commandManager;
        this.comparatorManager = comparatorManager;
    }
    
    /**
     * Gets the script position of the current operation.
     * 
     * @return <code>ScriptCoordinate</code>, not null
     */
    public ScriptCoordinate getCoordinate() {
        return coordinate;
    }

    /**
     * Sets the script position of the current operation.
     * 
     * @param coordinate
     *                <code>ScriptCoordinate</code>, not null
     */
    public void setCoordinate(ScriptCoordinate coordinate) {
        this.coordinate = coordinate;
    }
    
    public CommandStateManager getCommandStateManager()
    {
        return commandStateManager;
    }

    public ConditionManager getConditionManager() {
        return conditionManager;
    }

    public void setConditionManager(ConditionManager conditionManager) {
        this.conditionManager = conditionManager;
    }

    public ExecutableCommand getExecutableManager(String name) throws LookupException {
        return commandManager.newInstance(name);
    }

    public Comparator getComparator(String name) throws LookupException {
        return comparatorManager.newInstance(name);
    }

    public ExecutableTest getExecutableTest(String name) throws LookupException {
        return testManager.newInstance(name);
    }
}
