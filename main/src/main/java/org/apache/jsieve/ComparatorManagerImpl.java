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

import java.util.concurrent.ConcurrentMap;

import org.apache.jsieve.comparators.Comparator;
import org.apache.jsieve.exception.LookupException;

/**
 * <p>Maps Comparator names to configured Comparator implementation classes.</p>
 * <h4>Thread Safety</h4>
 * <p>
 * Instances may safely be accessed concurrently by multiple threads.
 * </p>
 */
public class ComparatorManagerImpl implements ComparatorManager {

    private final ConcurrentMap<String, String> classNameMap;

    /**
     * Constructor for ComparatorManager.
     */
    public ComparatorManagerImpl(final ConcurrentMap<String, String> classNameMap) {
        super();
        this.classNameMap = classNameMap;
    }

    /**
     * <p>
     * Method lookup answers the class to which a Comparator name is mapped.
     * </p>
     * 
     * @param name -
     *            The name of the Comparator
     * @return Class - The class of the Comparator
     * @throws LookupException
     */
    public Class lookup(String name) throws LookupException {
        Class comparatorClass = null;
        try {
            comparatorClass = getClass().getClassLoader().loadClass(
                    getClassName(name));
        } catch (ClassNotFoundException e) {
            throw new LookupException("Comparator named '" + name
                    + "' not found.");
        }
        if (!Comparator.class.isAssignableFrom(comparatorClass))
            throw new LookupException("Class " + comparatorClass.getName()
                    + " must implement " + Comparator.class.getName());
        return comparatorClass;
    }

    /**
     * <p>
     * Method newInstance answers an instance of the class to which a Comparator
     * name is mapped.
     * </p>
     * 
     * @param name -
     *            The name of the Comparator
     * @return Class - The class of the Comparator
     * @throws LookupException
     */
    public Comparator getComparator(String name) throws LookupException {
        try {
            return (Comparator) lookup(name).newInstance();
        } catch (InstantiationException e) {
            throw new LookupException(e.getMessage());
        } catch (IllegalAccessException e) {
            throw new LookupException(e.getMessage());
        }
    }

    /**
     * <p>
     * Method getClassName answers the name of the class to which a Comparator
     * name is mapped.
     * </p>
     * 
     * @param name -
     *            The name of the Comparator
     * @return String - The name of the class
     * @throws LookupException
     */
    private String getClassName(String name) throws LookupException {
        String className = classNameMap.get(name.toLowerCase());
        if (null == className)
            throw new LookupException("Command named '" + name
                    + "' not mapped.");
        return className;
    }
}
