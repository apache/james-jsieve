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

import org.apache.commons.logging.Log;

/**
 * Context for sieve operations.
 * 
 */
public abstract class SieveContext {

    /**
     * Gets the script position of the current operation.
     * 
     * @return <code>ScriptCoordinate</code>, not null
     */
    public abstract ScriptCoordinate getCoordinate();

    /**
     * Sets the script position of the current operation.
     * 
     * @param coordinate
     *            <code>ScriptCoordinate</code>, not null
     */
    public abstract void setCoordinate(ScriptCoordinate coordinate);

    /**
     * Gets the command state manager.
     * @return command state manage, not null
     */
    public abstract CommandStateManager getCommandStateManager();

    /**
     * Gets the condition manager.
     * @return condition manager, not null
     */
    public abstract ConditionManager getConditionManager();

    /**
     * Sets the condition manager.
     * @param manager not null
     */
    public abstract void setConditionManager(final ConditionManager manager);

    /**
     * Gets the command manager.
     * @return command manager, not null
     */
    public abstract CommandManager getCommandManager();
    
    /**
     * Gets the comparator manager.
     * @return not null
     */
    public abstract ComparatorManager getComparatorManager();
    
    /**
     * Gets the test manager.
     * @return test manager, not null
     */
    public abstract TestManager getTestManager();

    /**
     * Gets the log.
     * @return log, not null
     */
    public abstract Log getLog();
}
