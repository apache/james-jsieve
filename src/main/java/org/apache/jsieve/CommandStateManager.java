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

/**
 * Thread singleton class CommandStateManager records the state of a Sieve
 * evaluation.
 */
public class CommandStateManager {

    /**
     * The evaluated script is processing Prolog Commands
     */
    private boolean fieldInProlog = true;

    /**
     * The evaluated script has rejected the mail
     */
    private boolean fieldRejected = false;

    /**
     * The evaluated script must keep the mail
     */
    private boolean fieldImplicitKeep = true;

    /**
     * The evaluation has processed Action Commands
     */
    private boolean fieldHasActions = false;

    /**
     * Constructor for CommandStateManager.
     */
    public CommandStateManager() {
        super();
        initialize();
    }

    /**
     * Initialize the receiver.
     */
    protected void initialize() {
        setInProlog(true);
        setRejected(false);
        setHasActions(false);
        setImplicitKeep(true);
    }

    /**
     * Returns the hasActions.
     * 
     * @return boolean
     */
    public boolean isHasActions() {
        return fieldHasActions;
    }

    /**
     * Returns the inProlog.
     * 
     * @return boolean
     */
    public boolean isInProlog() {
        return fieldInProlog;
    }

    /**
     * Returns the isRejected.
     * 
     * @return boolean
     */
    public boolean isRejected() {
        return fieldRejected;
    }

    /**
     * Sets the hasActions.
     * 
     * @param hasActions
     *            The hasActions to set
     */
    public void setHasActions(boolean hasActions) {
        fieldHasActions = hasActions;
    }

    /**
     * Sets the inProlog.
     * 
     * @param inProlog
     *            The inProlog to set
     */
    public void setInProlog(boolean inProlog) {
        fieldInProlog = inProlog;
    }

    /**
     * Sets the isRejected.
     * 
     * @param isRejected
     *            The isRejected to set
     */
    public void setRejected(boolean isRejected) {
        fieldRejected = isRejected;
    }

    /**
     * Returns the implicitKeep.
     * 
     * @return boolean
     */
    public boolean isImplicitKeep() {
        return fieldImplicitKeep;
    }

    /**
     * Sets the implicitKeep.
     * 
     * @param implicitKeep
     *            The implicitKeep to set
     */
    public void setImplicitKeep(boolean implicitKeep) {
        fieldImplicitKeep = implicitKeep;
    }

}
