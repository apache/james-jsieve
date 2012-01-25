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

import java.util.List;

import org.apache.jsieve.comparators.Comparator;
import org.apache.jsieve.exception.LookupException;

/**
 * <p>Maps Comparator names to configured Comparator implementation classes.</p>
 * <h4>Thread Safety</h4>
 * <p>
 * Implementation dependent. {@link ComparatorManagerImpl} is a thread safe implementation.
 * </p>
 */
public interface ComparatorManager {
    
    /**
     * <p>Gets a comparator by name.</p>
     * 
     * @param name -
     *            The (logical) name of the Comparator
     * @return a comparator, not null
     * @throws LookupException
     */
    public Comparator getComparator(String name) throws LookupException;
    
    /**
     * Is an explicit declaration in a <code>require</code> statement
     * unnecessary for this comparator?
     * @param comparatorName not null
     * @return true when this comparator need not be declared by <core>require</code>,
     * false when any usage of this comparator must be declared in a <code>require</code> statement
     */
    public boolean isImplicitlyDeclared(final String comparatorName);
    
    /**
     * Is the comparator with the given name supported?
     * @param name not null
     * @return true when supported, false otherwise
     */
    public boolean isSupported(String name);
    
    /**
     * Answer a List of the names of supported Sieve extensions.
     *
     * @return
     */
    public List<String> getExtensions();
}
