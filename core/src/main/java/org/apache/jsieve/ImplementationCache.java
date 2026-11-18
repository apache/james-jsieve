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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.jsieve.exception.LookupException;

/**
 * <p>
 * Resolves the implementation classes a manager maps names to, and creates
 * their instances. A class is loaded and its constructor resolved once per
 * class name: evaluation asks for an instance for every command, test and
 * comparison, and {@link ClassLoader#loadClass(String)} is costly even for a
 * class already loaded.
 * </p>
 * <p>
 * The cache is keyed by class name, not by the name it is mapped from, so a
 * name mapped to another class resolves to that class. Failures are not
 * cached.
 * </p>
 * <h4>Thread Safety</h4>
 * <p>
 * Instances may safely be accessed concurrently by multiple threads.
 * </p>
 */
final class ImplementationCache<T> {

    private final Class<T> type;
    private final String kind;
    private final ClassLoader classLoader;
    private final ConcurrentMap<String, Constructor<? extends T>> constructors = new ConcurrentHashMap<>();

    /**
     * @param type the type implementations must have, not null
     * @param kind what the names name, for messages, not null
     * @param classLoader loads the classes, that of the manager so that a
     *            subclass of it loads from its own loader, not null
     */
    ImplementationCache(Class<T> type, String kind, ClassLoader classLoader) {
        this.type = type;
        this.kind = kind;
        this.classLoader = classLoader;
    }

    /**
     * Answers the class named, after checking it has the expected type.
     *
     * @param name the name mapped to the class, for messages, not null
     * @param className not null
     * @throws LookupException when the class cannot be loaded or has not the
     *             expected type
     */
    Class<? extends T> lookup(String name, String className) throws LookupException {
        final Constructor<? extends T> constructor = constructors.get(className);
        if (constructor != null) {
            return constructor.getDeclaringClass();
        }
        return load(name, className);
    }

    /**
     * Answers a new instance of the class named.
     *
     * @param name the name mapped to the class, for messages, not null
     * @param className not null
     * @throws LookupException when the class cannot be loaded or instantiated
     */
    T newInstance(String name, String className) throws LookupException {
        Constructor<? extends T> constructor = constructors.get(className);
        if (constructor == null) {
            try {
                constructor = load(name, className).getDeclaredConstructor();
            } catch (NoSuchMethodException e) {
                throw new LookupException(className + " has no constructor without argument");
            }
            final Constructor<? extends T> existing = constructors.putIfAbsent(className, constructor);
            if (existing != null) {
                constructor = existing;
            }
        }
        try {
            return constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new LookupException(e.getMessage());
        } catch (InvocationTargetException e) {
            throw new LookupException(e.getCause().getMessage(), e.getCause());
        }
    }

    private Class<? extends T> load(String name, String className) throws LookupException {
        final Class<?> loaded;
        try {
            loaded = classLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new LookupException(kind + " named '" + name + "' not found.");
        }
        if (!type.isAssignableFrom(loaded)) {
            throw new LookupException("Class " + loaded.getName()
                    + " must implement " + type.getName());
        }
        return loaded.asSubclass(type);
    }
}
