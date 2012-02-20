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

import static org.apache.jsieve.Constants.COMPARATOR_ASCII_CASEMAP_NAME;
import static org.apache.jsieve.Constants.COMPARATOR_OCTET_NAME;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

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

    /** 
     * Constructs a set containing the names of those comparisons for which <code>require</code> 
     * is not necessary before usage, according to RFC5228.
     * See <a href='http://tools.ietf.org/html/rfc5228#section-2.7.3'>RFC5228, 2.7.3 Comparators</a>. 
     */
    public static CopyOnWriteArraySet<String> standardDefinedComparators() {
        final CopyOnWriteArraySet<String> results = new CopyOnWriteArraySet<String>();
        results.add(COMPARATOR_OCTET_NAME);
        results.add(COMPARATOR_ASCII_CASEMAP_NAME);
        return results;
    }
    
    private final ConcurrentMap<String, String> classNameMap;
    /** 
     * The names of those comparisons for which <code>require</code> is not necessary before usage.
     * See <a href='http://tools.ietf.org/html/rfc5228#section-2.7.3'>RFC5228, 2.7.3 Comparators</a>. 
     */
    private final CopyOnWriteArraySet<String> implicitlyDeclared;

    /**
     * Constructs a manager with the standard comparators implicitly defined.
     * @param classNameMap not null
     */
    public ComparatorManagerImpl(final ConcurrentMap<String, String> classNameMap) {
        this(classNameMap, standardDefinedComparators());
    }
    
    /**
     * Constructor for ComparatorManager.
     * @param classNameMap indexes names of implementation classes against logical names, not null
     * @param implicitlyDeclared names of those comparisons for which <code>require</code> is not necessary before usage
     */
    public ComparatorManagerImpl(final ConcurrentMap<String, String> classNameMap, final CopyOnWriteArraySet<String> implicitlyDeclared) {
        super();
        this.classNameMap = classNameMap;
        this.implicitlyDeclared = implicitlyDeclared;
    }
    
    /**
     * Is an explicit declaration in a <code>require</code> statement
     * unnecessary for this comparator?
     * @param comparatorName not null
     * @return true when this comparator need not be declared by <core>require</code>,
     * false when any usage of this comparator must be declared in a <code>require</code> statement
     */
    public boolean isImplicitlyDeclared(final String comparatorName) {
        return implicitlyDeclared.contains(comparatorName);
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
            throw new LookupException("Comparator named '" + name
                    + "' not mapped.");
        return className;
    }

    /**
     * @see ComparatorManager#isSupported(String)
     */
    public boolean isSupported(String name) {
        try {
            getComparator(name);
            return true;
        } catch (LookupException e) {
            return false;
        }
    }

    /**
     * @see org.apache.jsieve.ComparatorManager#getExtensions()
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
