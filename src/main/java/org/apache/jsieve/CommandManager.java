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
 * Singleton class <code>CommandManager</code> maps Command names to
 * configured Command implementation classes.
 */
public class CommandManager {
    /**
     * The sole instance of the receiver.
     */
    static private CommandManager fieldInstance;

    /**
     * Constructor for CommandManager.
     */
    private CommandManager() {
        super();
    }

    /**
     * Returns the sole instance of the receiver, lazily initialised if
     * required.
     * 
     * @return CommandManager
     */
    public static synchronized CommandManager getInstance() {
        CommandManager instance = null;
        if (null == (instance = getInstanceBasic())) {
            updateInstance();
            return getInstance();
        }
        return instance;
    }

    /**
     * Returns the sole instance of the receiver.
     * 
     * @return CommandManager
     */
    private static CommandManager getInstanceBasic() {
        return fieldInstance;
    }

    /**
     * Computes a new instance of the receiver.
     * 
     * @return CommandManager
     */
    protected static CommandManager computeInstance() {
        return new CommandManager();
    }

    /**
     * Sets the sole instance of the receiver.
     * 
     * @param instance
     *                The current instance to set
     */
    protected static void setInstance(CommandManager instance) {
        fieldInstance = instance;
    }

    /**
     * Resets the sole instance.
     */
    public static void resetInstance() {
        setInstance(null);
    }

    /**
     * Updates the sole instance.
     */
    protected static void updateInstance() {
        setInstance(computeInstance());
    }

    /**
     * <p>
     * Method lookup answers the class to which a Command name is mapped.
     * </p>
     * 
     * @param name -
     *                The name of the Command
     * @return Class - The class of the Command
     * @throws LookupException
     */
    public Class lookup(String name) throws LookupException {
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
     *                The name of the Command
     * @return Class - The class of the Command
     * @throws LookupException
     */
    public ExecutableCommand newInstance(String name) throws LookupException {
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
     *                The Command name
     * @return boolean - True if the Command name is configured.
     */
    public boolean isSupported(String name) {
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
     *                The name of the Command
     * @return String - The name of the class
     * @throws LookupException
     */
    protected String getClassName(String name) throws LookupException {
        String className;
        try {
            className = (String) getClassNameMap().get(name.toLowerCase());
        } catch (SieveConfigurationException e) {
            throw new LookupException(
                    "Lookup failed due to a Configuration Exception: "
                            + e.getMessage());
        }
        if (null == className)
            throw new LookupException("Command named '" + name
                    + "' not mapped.");
        return className;
    }

    /**
     * Method getClassNameMap answers a Map of Command names and their class
     * names.
     * 
     * @return Map
     * @throws SieveConfigurationException
     */
    protected Map getClassNameMap() throws SieveConfigurationException {
        return ConfigurationManager.getInstance().getCommandMap();
    }

}
