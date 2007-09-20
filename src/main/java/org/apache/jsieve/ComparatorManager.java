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

import org.apache.jsieve.comparators.Comparator;
import org.apache.jsieve.exception.LookupException;

/**
 * Singleton class <code>ComparatorManager</code> maps Comparator names to configured
 * Comparator implementation classes.
 */
public class ComparatorManager
{
    /**
     * The sole instance of the receiver.
     */ 
    static private ComparatorManager fieldInstance;

    /**
     * Constructor for ComparatorManager.
     */
    private ComparatorManager()
    {
        super();
    }

    /**
     * Returns the sole instance of the receiver, lazily initialised if required.
     * @return ComparatorManager
     */
    public static synchronized ComparatorManager getInstance()
    {
        ComparatorManager current = null;
        if (null == (current = getInstanceBasic()))
        {
            updateInstance();
            return getInstance();
        }    
        return current;
    }
    
    /**
     * Returns the sole instance of the receiver.
     * @return ComparatorManager
     */
    private static ComparatorManager getInstanceBasic()
    {
        return fieldInstance;
    }
    
    /**
     * Computes a new instance of the receiver.
     * @return ComparatorManager
     */
    protected static ComparatorManager computeInstance()
    {
        return new ComparatorManager();
    }        

    /**
     * Sets the sole instance.
     * @param instance The current instance to set
     */
    protected static void setInstance(ComparatorManager instance)
    {
        fieldInstance = instance;
    }
    
    /**
     * Resets the sole instance.
     */
    public static void resetInstance()
    {
        setInstance(null);
    }    
    
    /**
     * Updates the sole instance.
     */
    protected static void updateInstance()
    {
        setInstance(computeInstance());
    }
    
    /**
     * <p>Method lookup answers the class to which a Comparator name is mapped.</p>
     * 
     * @param name - The name of the Comparator
     * @return Class - The class of the Comparator
     * @throws LookupException
     */
    public Class lookup(String name) throws LookupException
    {
        Class comparatorClass = null;
        try
        {
            comparatorClass =
                getClass().getClassLoader().loadClass(getClassName(name));
        }
        catch (ClassNotFoundException e)
        {
            throw new LookupException(
                "Comparator named '" + name + "' not found.");
        }
        if (!Comparator.class.isAssignableFrom(comparatorClass))
            throw new LookupException(
                "Class "
                    + comparatorClass.getName()
                    + " must implement "
                    + Comparator.class.getName());
        return comparatorClass;
    }
    
    /**
     * <p>Method newInstance answers an instance of the class to which a Comparator
     * name is mapped.</p>
     * 
     * @param name - The name of the Comparator
     * @return Class - The class of the Comparator
     * @throws LookupException
     */
    public Comparator newInstance(String name) throws LookupException
    {
        try
        {
            return (Comparator) lookup(name).newInstance();
        }
        catch (InstantiationException e)
        {
            throw new LookupException(e.getMessage());
        }
        catch (IllegalAccessException e)
        {
            throw new LookupException(e.getMessage());
        }
    }    


    /**
     * Method isSupported answers a boolean indicating if a Comparator name is 
     * configured.
     * @param name - The Comparator name
     * @return boolean - True if the Comparator name is configured.
     */
    public boolean isSupported(String name)
    {
        boolean isSupported = true;
        try
        {
            lookup(name);
        }
        catch (LookupException e)
        {
            isSupported = false;
        }
        return isSupported;
    }
    
    /**
     * <p>Method getClassName answers the name of the class to which a Comparator
     * name is mapped.</p>
     * 
     * @param name - The name of the Comparator
     * @return String - The name of the class
     * @throws LookupException
     */
    protected String getClassName(String name) throws LookupException
    {
        String className;
        try
        {
            className = (String) getClassNameMap().get(name.toLowerCase());
        }
        catch (SieveConfigurationException e)
        {
            throw new LookupException(
                "Lookup failed due to a Configuration Exception: "
                    + e.getMessage());
        }
        if (null == className)
            throw new LookupException(
                "Command named '" + name + "' not mapped.");
        return className;
    }
    
    /**
     * Method getClassNameMap answers a Map of Comparator names and their class 
     * names.
     * @return Map
     * @throws SieveConfigurationException
     */
    protected Map getClassNameMap() throws SieveConfigurationException
    {   
        return ConfigurationManager.getInstance().getComparatorMap();        
    }    
    
        

}
