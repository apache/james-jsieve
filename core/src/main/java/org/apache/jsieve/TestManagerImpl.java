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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import org.apache.jsieve.exception.LookupException;
import org.apache.jsieve.tests.ExecutableTest;

/**
 * <p>Maps Test names to configured Test implementation classes.</p>
 * <h4>Thread Safety</h4>
 * <p>
 * Instances may safely be accessed concurrently by multiple threads.
 * </p>
 */
public class TestManagerImpl implements TestManager {
       
    private static List<String> IMPLICITLY_DECLARED = Arrays.asList("address",
            "allof", "anyof", "exists", "false", "header", "not", "size", "true");

    private static boolean isImplicitlyDeclared(String name) {
        return IMPLICITLY_DECLARED.contains(name);
    }

    private final ConcurrentMap<String, String> classNameMap;

    /**
     * TestManager is instanciated with getInstance
     */
    public TestManagerImpl(final ConcurrentMap<String, String> classNameMap) {
        super();
        this.classNameMap = classNameMap;
    }

    /**
     * <p>
     * Method lookup answers the class to which a Test name is mapped.
     * </p>
     * 
     * @param name -
     *            The name of the Test
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
     *            The name of the Test
     * @return Class - The class of the Test
     * @throws LookupException
     */
    public ExecutableTest getTest(String name) throws LookupException {
        try {
            return (ExecutableTest) lookup(name).newInstance();
        } catch (InstantiationException e) {
            throw new LookupException(e.getMessage());
        } catch (IllegalAccessException e) {
            throw new LookupException(e.getMessage());
        }
    }

    /**
     * <p>
     * Method getClassName answers the name of the class to which a Test name is
     * mapped.
     * </p>
     * 
     * @param name -
     *            The name of the Test
     * @return String - The name of the class
     * @throws LookupException
     */
    private String getClassName(String name) throws LookupException {
        final String className = classNameMap.get(name.toLowerCase());
        if (null == className)
            throw new LookupException("Test named '" + name + "' not mapped.");
        return className;
    }

    /**
     * @see org.apache.jsieve.TestManager#getExtensions()
     */
    public List<String> getExtensions() {
        List<String> extensions = new ArrayList<String>(classNameMap.size());
        for (String key : classNameMap.keySet())
        {
            if (!isImplicitlyDeclared(key))
            {
                extensions.add(key);
            }
        }
        return extensions;
    }

}
