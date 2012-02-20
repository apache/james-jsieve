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
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

/**
 * <p>
 * <code>ConfigurationManager</code> parses the XML statements
 * in the Sieve configuration file and translates them to Java objects.
 * </p>
 * <p>
 * The Sieve configuration is read from 3 properties files
 * </p>
 * <ul>
 * <li><code>org/apache/jsieve/commandsmap.properties</code></li>
 * <li><code>org/apache/jsieve/testsmap.properties</code></li>
 * <li><code>org/apache/jsieve/comparatorsmap.properties</code></li>
 * </ul>
 * <p>They are located by searching the classpath of the current ClassLoader.
 * </p>
 * <h4>Thread Safety</h4>
 * <p>
 * Each configuration manager instance may be safely accessed by concurrent threads.
 * </p>
 * <p>
 * The managers constructed by 
 * </p>
 * <ul>
 * <li>{@link #getCommandManager()}</li> 
 * <li>{@link #getComparatorManager()}</li>
 * <li>{@link #getTestManager()}</li>
 * </ul>
 * <p>
 * and the {@link SieveFactory} built by
 * </p>
 * <ul>
 * <li>{@link #build()}</li>
 * </ul>
 * <p>
 * may be safely shared by multiple threads.
 * </p>
 */
public class ConfigurationManager {

    private static final int DEFAULT_INITIAL_CONCURRENCY_LEVEL = 8;

    private static final String COMMANDSMAP_PROPERTIES = "org/apache/jsieve/commandsmap.properties";

    private static final String TESTSMAP_PROPERTIES = "org/apache/jsieve/testsmap.properties";

    private static final String COMPARATORSMAP_PROPERTIES = "org/apache/jsieve/comparatorsmap.properties";

    /**
     * A Map of the Command names and their associated class names.
     */
    private ConcurrentMap<String, String> fieldCommandMap;

    /**
     * A Map of the Test names and their associated class names.
     */
    private ConcurrentMap<String, String> fieldTestMap;

    /**
     * A Map of the Comparator names and their associated class names.
     */
    private ConcurrentMap<String, String> fieldComparatorMap;
    
    /**
     * The initial size for the {@link ConcurrentHashMap} concurrency level.
     */
    private int initialConcurrencyLevel = DEFAULT_INITIAL_CONCURRENCY_LEVEL;

    private static final Log LOG = LogFactory.getLog("org.apache.jsieve");

    private Log log = LOG;
    
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
     * Gets the current initial size for the {@link ConcurrentHashMap} concurrency level.
     * @return number of concurrent threads estimated for initial sizing
     */
    public int getInitialConcurrencyLevel() {
        return initialConcurrencyLevel;
    }

    /**
     * Sets the current initial size for the {@link ConcurrentHashMap} concurrency level.
     * @param initialConcurrencyLevel number of concurrent threads estimated for initial sizing
     */
    public void setInitialConcurrencyLevel(int initialConcurrencyLevel) {
        this.initialConcurrencyLevel = initialConcurrencyLevel;
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
     * @return Map not null
     */
    public ConcurrentMap<String, String> getCommandMap() {
        if (null == fieldCommandMap) {
            fieldCommandMap = new ConcurrentHashMap<String, String>();
        }
        return fieldCommandMap;
    }

    /**
     * Method getTestMap answers a Map of Test names and their associated class
     * names, lazily initialized if required.
     * 
     * @return Map not null
     */
    public ConcurrentMap<String, String> getTestMap() {
        if (null == fieldTestMap) {
            fieldTestMap = new ConcurrentHashMap<String, String>();
        }
        return fieldTestMap;
    }

    /**
     * Method getComparatorMap answers a Map of Comparator names and their
     * associated class names, lazily initialized if required.
     * 
     * @return Map not null
     */
    public ConcurrentMap<String, String> getComparatorMap() {
        if (null == fieldComparatorMap) {
            fieldComparatorMap = new ConcurrentHashMap<String, String>();
        }
        return fieldComparatorMap;
    }

    /**
     * Method parse uses the Digester to parse the XML statements in the Sieve
     * configuration file into Java objects.
     * 
     * @throws SAXException
     * @throws IOException
     */
    private void parse() throws SAXException, IOException {
        setCommandMap(loadConfiguration(COMMANDSMAP_PROPERTIES));
        setTestMap(loadConfiguration(TESTSMAP_PROPERTIES));
        setComparatorMap(loadConfiguration(COMPARATORSMAP_PROPERTIES));
    }

    private ConcurrentMap<String,String> loadConfiguration(final String name) throws IOException {
        final Properties properties = loadProperties(name);
        final ConcurrentMap<String, String> result = 
            new ConcurrentHashMap<String, String>(properties.size(), 1.0f, initialConcurrencyLevel);
        for (final Map.Entry<Object, Object> entry: properties.entrySet()) {
            result.put(entry.getKey().toString(), entry.getValue().toString());
        }
        return result;
    }
    
    private Properties loadProperties(final String name) throws IOException {
        final InputStream is = getConfigStream(name);
        final Properties p = new Properties();
        p.load(is);
        return p;
    }

    /**
     * Sets the commandMap.
     * 
     * @param commandMap
     *            The commandMap to set
     */
    private void setCommandMap(ConcurrentMap<String, String> commandMap) {
        fieldCommandMap = commandMap;
    }

    /**
     * Sets the testMap.
     * 
     * @param testMap
     *            The testMap to set
     */
    private void setTestMap(ConcurrentMap<String, String> testMap) {
        fieldTestMap = testMap;
    }

    /**
     * Sets the comparatorMap.
     * 
     * @param comparatorMap
     *            The comparatorMap to set
     */
    private void setComparatorMap(ConcurrentMap<String, String> comparatorMap) {
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
    
    public void setLog(Log log) {
        this.log = log;
    }

    public SieveFactory build() {
        return new SieveFactory(getCommandManager(), getComparatorManager(),
                getTestManager(), getLog());
    }
}
