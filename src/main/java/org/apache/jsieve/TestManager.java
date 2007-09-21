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
import org.apache.jsieve.tests.ExecutableTest;

/**
 * Singleton class <code>TestManager</code> maps Test names to configured Test
 * implementation classes.
 */
public class TestManager {
    /**
     * The sole instance of the receiver.
     */
    static private TestManager fieldInstance;

    /**
     * Constructor for TestManager.
     */
    public TestManager() {
        super();
    }

    /**
     * Returns the sole instance of the reciever, lazily initialised if
     * required.
     * 
     * @return CommandManager
     */
    public static TestManager getInstance() {
        TestManager current = null;
        if (null == (current = getInstanceBasic())) {
            updateInstance();
            return getInstance();
        }
        return current;
    }

    /**
     * Returns the sole instance of the reciever.
     * 
     * @return CommandManager
     */
    private static TestManager getInstanceBasic() {
        return fieldInstance;
    }

    /**
     * Computes a new instance of the receiver.
     * 
     * @return CommandManager
     */
    protected static TestManager computeInstance() {
        return new TestManager();
    }

    /**
     * Sets the sole instance of the reciever.
     * 
     * @param instance
     *                The instance to set
     */
    protected static void setInstance(TestManager instance) {
        fieldInstance = instance;
    }

    /**
     * Resets the sole instance of the reciever.
     */
    public static void resetInstance() {
        setInstance(null);
    }

    /**
     * Updates the sole instance of the reciever.
     */
    protected static void updateInstance() {
        setInstance(computeInstance());
    }

    /**
     * <p>
     * Method lookup answers the class to which a Test name is mapped.
     * </p>
     * 
     * @param name -
     *                The name of the Test
     * @return Class - The class of the Test
     * @throws LookupException
     */
    public Class lookup(String name) throws LookupException {
        Class testClass = null;

        try {
            testClass = getClass().getClassLoader().loadClass(
                    getClassName(name));
        } catch (ClassNotFoundException e) {
            throw new LookupException("Test named '" + name + "' not found.");
        }
        if (!ExecutableTest.class.isAssignableFrom(testClass))
            throw new LookupException("Class " + testClass.getName()
                    + " must implement " + ExecutableTest.class.getName());
        return testClass;
    }

    /**
     * <p>
     * Method newInstance answers an instance of the class to which a Test name
     * is mapped.
     * </p>
     * 
     * @param name -
     *                The name of the Test
     * @return Class - The class of the Test
     * @throws LookupException
     */
    public ExecutableTest newInstance(String name) throws LookupException {
        try {
            return (ExecutableTest) lookup(name).newInstance();
        } catch (InstantiationException e) {
            throw new LookupException(e.getMessage());
        } catch (IllegalAccessException e) {
            throw new LookupException(e.getMessage());
        }
    }

    /**
     * Method isSupported answers a boolean indicating if a Test name is
     * configured.
     * 
     * @param name -
     *                The Test name
     * @return boolean - True if the Test name is configured.
     */
    public boolean isSupported(String name) {
        boolean isSupported = true;
        try {
            lookup(name);
        } catch (LookupException e) {
            isSupported = false;
        }
        return isSupported;
    }

    /**
     * <p>
     * Method getClassName answers the name of the class to which a Test name is
     * mapped.
     * </p>
     * 
     * @param name -
     *                The name of the Test
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
            throw new LookupException("Test named '" + name + "' not mapped.");
        return className;
    }

    /**
     * Method getClassNameMap answers a Map of Test names and their class names.
     * 
     * @return Map
     * @throws SieveConfigurationException
     */
    protected Map getClassNameMap() throws SieveConfigurationException {
        return ConfigurationManager.getInstance().getTestMap();
    }

}
