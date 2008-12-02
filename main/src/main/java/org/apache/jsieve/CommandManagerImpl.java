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

import java.util.Map;

import org.apache.jsieve.exception.LookupException;

/**
 * Maps command names to comman implementations.
 */
public class CommandManagerImpl implements CommandManager {

    private final Map classNameMap;

    /**
     * Constructor for CommandManager.
     */
    public CommandManagerImpl(final Map classNameMap) {
        super();
        this.classNameMap = classNameMap;
    }

    /**
     * <p>
     * Method lookup answers the class to which a Command name is mapped.
     * </p>
     * 
     * @param name -
     *            The name of the Command
     * @return Class - The class of the Command
     * @throws LookupException
     */
    private Class lookup(String name) throws LookupException {
        Class cmdClass = null;
        try {
            cmdClass = getClass().getClassLoader()
                    .loadClass(getClassName(name));
        } catch (ClassNotFoundException e) {
            throw new LookupException("Command named '" + name + "' not found.");
        }
        if (!ExecutableCommand.class.isAssignableFrom(cmdClass))
            throw new LookupException("Class " + cmdClass.getName()
                    + " must implement " + ExecutableCommand.class.getName());
        return cmdClass;
    }

    /**
     * <p>
     * Method newInstance answers an instance of the class to which a Command
     * name is mapped.
     * </p>
     * 
     * @param name -
     *            The name of the Command
     * @return Class - The class of the Command
     * @throws LookupException
     */
    public ExecutableCommand getCommand(String name) throws LookupException {
        try {
            return (ExecutableCommand) lookup(name).newInstance();
        } catch (InstantiationException e) {
            throw new LookupException(e.getMessage());
        } catch (IllegalAccessException e) {
            throw new LookupException(e.getMessage());
        }
    }

    /**
     * Method isSupported answers a boolean indicating if a Command name is
     * configured.
     * 
     * @param name -
     *            The Command name
     * @return boolean - True if the Command name is configured.
     */
    public boolean isCommandSupported(String name) {
        boolean isSupported = false;
        try {
            lookup(name);
            isSupported = true;
        } catch (LookupException e) {
        }
        return isSupported;
    }

    /**
     * <p>
     * Method getClassName answers the name of the class to which a Command name
     * is mapped.
     * </p>
     * 
     * @param name -
     *            The name of the Command
     * @return String - The name of the class
     * @throws LookupException
     */
    protected String getClassName(String name) throws LookupException {
        final String className = (String) classNameMap.get(name.toLowerCase());
        if (null == className)
            throw new LookupException("Command named '" + name
                    + "' not mapped.");
        return className;
    }
}
