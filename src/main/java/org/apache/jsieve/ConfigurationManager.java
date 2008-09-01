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

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

/**
 * <p>
 * Singleton class <code>ConfigurationManager</code> parses the XML statements
 * in the Sieve configuration file and translates them to Java objects.
 * </p>
 * 
 * <p>
 * The Sieve configuration is read from 3 properties file. They are
 * located by searching the classpath of the current ClassLoader.
 * org/apache/jsieve/commandsmap.properties
 * org/apache/jsieve/testsmap.properties
 * org/apache/jsieve/comparatorsmap.properties
 * </p>
 */
public class ConfigurationManager {
    
    private static final String COMMANDSMAP_PROPERTIES = "org/apache/jsieve/commandsmap.properties";

    private static final String TESTSMAP_PROPERTIES = "org/apache/jsieve/testsmap.properties";

    private static final String COMPARATORSMAP_PROPERTIES = "org/apache/jsieve/comparatorsmap.properties";

    /**
     * A Map of the Command names and their associated class names.
     */
    private Map fieldCommandMap;

    /**
     * A Map of the Test names and their associated class names.
     */
    private Map fieldTestMap;

    /**
     * A Map of the Comparator names and their associated class names.
     */
    private Map fieldComparatorMap;

    private static final Log log = LogFactory.getLog("org.apache.jsieve");
    
    /**
     * Constructor for ConfigurationManager.
     * 
     * @throws SieveConfigurationException
     */
    public ConfigurationManager() throws SieveConfigurationException {
        super();
        try {
            parse();
        } catch (SAXException e) {
            if (log.isErrorEnabled())
                log.error("Exception processing Configuration: ", e);
            throw new SieveConfigurationException(e);
        } catch (IOException e) {
            if (log.isErrorEnabled())
                log.error("Exception processing Configuration: ", e);
            throw new SieveConfigurationException(e);
        }
    }
    
    /**
     * <p>
     * Method getConfigStream answers an InputStream over the Sieve
     * configuration file. It is located by searching the classpath of the
     * current ClassLoader.
     * </p>
     * <p>
     * The context classloader is searched first. If a suitably named resource
     * is found then this is returned. Otherwise, the classloader used to load
     * this class is searched for the resource.
     * </p>
     * 
     * @return InputStream
     * @throws IOException
     */
    private InputStream getConfigStream(String configName) throws IOException {
        InputStream stream = null;
        // Context classloader is usually right in a JEE evironment
        final ClassLoader contextClassLoader = Thread.currentThread()
                .getContextClassLoader();
        if (contextClassLoader != null) {
            stream = contextClassLoader.getResourceAsStream(configName);
        }

        // Sometimes context classloader will not be set conventionally
        // So, try class classloader
        if (null == stream) {
            stream = ConfigurationManager.class.getClassLoader()
                    .getResourceAsStream(configName);
        }

        if (null == stream)
            throw new IOException("Resource \"" + configName + "\" not found");
        return stream;
    }

    /**
     * Method getCommandMap answers a Map of Command names and their associated
     * class names, lazily initialized if required.
     * 
     * @return Map
     */
    public Map getCommandMap() {
        if (null == fieldCommandMap) {
            fieldCommandMap = new HashMap();
        }
        return Collections.synchronizedMap(fieldCommandMap);
    }

    /**
     * Method getTestMap answers a Map of Test names and their associated class
     * names, lazily initialized if required.
     * 
     * @return Map
     */
    public Map getTestMap() {
        if (null == fieldTestMap) {
            fieldTestMap = new HashMap();
        }
        return Collections.synchronizedMap(fieldTestMap);
    }

    /**
     * Method getComparatorMap answers a Map of Comparator names and their
     * associated class names, lazily initialized if required.
     * 
     * @return Map
     */
    public Map getComparatorMap() {
        if (null == fieldComparatorMap) {
            fieldComparatorMap = new HashMap();
        }
        return Collections.synchronizedMap(fieldComparatorMap);
    }

    /**
     * Method parse uses the Digester to parse the XML statements in the Sieve
     * configuration file into Java objects.
     * 
     * @throws SAXException
     * @throws IOException
     */
    private void parse() throws SAXException, IOException {
        InputStream is;
        Properties p;
        is = getConfigStream(COMMANDSMAP_PROPERTIES);
        p = new Properties();
        p.load(is);
        setCommandMap(p);
        is = getConfigStream(TESTSMAP_PROPERTIES);
        p = new Properties();
        p.load(is);
        setTestMap(p);
        is = getConfigStream(COMPARATORSMAP_PROPERTIES);
        p = new Properties();
        p.load(is);
        setComparatorMap(p);
    }

    /**
     * Sets the commandMap.
     * 
     * @param commandMap
     *                The commandMap to set
     */
    private void setCommandMap(Map commandMap) {
        fieldCommandMap = commandMap;
    }

    /**
     * Sets the testMap.
     * 
     * @param testMap
     *                The testMap to set
     */
    private void setTestMap(Map testMap) {
        fieldTestMap = testMap;
    }

    /**
     * Sets the comparatorMap.
     * 
     * @param comparatorMap
     *                The comparatorMap to set
     */
    private void setComparatorMap(Map comparatorMap) {
        fieldComparatorMap = comparatorMap;
    }
    
    public ComparatorManager getComparatorManager() {
        return new ComparatorManagerImpl(fieldComparatorMap);
    }

    public CommandManager getCommandManager() {
        return new CommandManagerImpl(fieldCommandMap);
    }
    
    public TestManager getTestManager() {
        return new TestManagerImpl(fieldTestMap);
    }
    
    public Log getLog() {
        return log;
    }
}
