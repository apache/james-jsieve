/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2004 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache", "Jakarta", "JAMES", "JSieve" and 
 *    "Apache Software Foundation" must not be used to endorse or promote
 *    products derived from this software without prior written permission.
 *    For written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * Portions of this software are based upon public domain software
 * originally written at the National Center for Supercomputing Applications,
 * University of Illinois, Urbana-Champaign.
 */
package org.apache.jsieve;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;
import org.xml.sax.SAXException;

/**
 * <p>Singleton class <code>ConfigurationManager</code> parses the XML statements in
 * the Sieve configuration file and translates them to Java objects.</p>
 * 
 * <p>The Sieve configuration file is named <code>sieveConfig.xml</code>. It is
 * located by searching the classpath of the current ClassLoader.</p>
 */
public class ConfigurationManager
{
    /**
     * The sole instance of the receiver.
     */ 
    static private ConfigurationManager fieldInstance;
    
    /**
     * The Digester used to process the Sieve configuration XML.
     */ 
    private Digester fieldDigester;
    
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

    /**
     * Constructor for ConfigurationManager.
     * 
     * @throws SieveConfigurationException
     */
    private ConfigurationManager() throws SieveConfigurationException
    {
        super();
        Log log = Logger.getLog();
        try
        {
            parse();
        }
        catch (SAXException e)
        {
            if (log.isErrorEnabled())
                log.error("Exception processing Configuration: ", e);
            throw new SieveConfigurationException(e);
        }
        catch (IOException e)
        {
            if (log.isErrorEnabled())
                log.error("Exception processing Configuration: ", e);
            throw new SieveConfigurationException(e);
        }
    }

    /**
     * Returns the sole instance of the receiver, lazily initialised if required.
     * @return ConfigurationManager
     */
    static public synchronized ConfigurationManager getInstance()
        throws SieveConfigurationException
    {
        ConfigurationManager instance = null;
        if (null == (instance = getInstanceBasic()))
        {
            updateInstance();
            return getInstance();
        }
        return instance;
    }
    
    /**
     * Returns the sole instance of the receiver.
     * @return ConfigurationManager
     */
    static private ConfigurationManager getInstanceBasic()
    {
        return fieldInstance;
    }
    
    /**
     * Returns a new instance of the receiver.
     * @return ConfigurationManager
     */
    static protected ConfigurationManager computeInstance()
        throws SieveConfigurationException
    {
        return new ConfigurationManager();
    }        

    /**
     * Sets the instance.
     * @param instance The instance to set
     */
    static protected void setInstance(ConfigurationManager instance)
    {
        fieldInstance = instance;
    }
    
    /**
     * Updates the instance.
     */
    static protected void updateInstance() throws SieveConfigurationException
    {
        setInstance(computeInstance());
    }
    
    /**
     * Method getConfigName answers the name of the Sieve configuration file.
     * @return String
     */
    static protected String getConfigName()
    {
        return "sieveConfig.xml";
    }    

    /**
     * Method getConfigStream answers an InputStream over the Sieve configuration 
     * file. It is located by searching the classpath of the current ClassLoader.
     * 
     * @return InputStream
     */
    static protected InputStream getConfigStream()
    {    
        return ClassLoader.getSystemResourceAsStream(getConfigName());
    }    
    
    /**
     * Method getCommandMap answers a Map of Command names and their associated
     * class names, lazily initialized if required.
     * 
     * @return Map
     */
    public synchronized Map getCommandMap()
    {
        Map commandMap = null;
        if (null == (commandMap = getCommandMapBasic()))
        {
            updateCommandMap();
            return getCommandMap();
        }    
        return commandMap;
    }
    
    /**
     * Method getTestMap answers a Map of Test names and their associated
     * class names, lazily initialized if required.
     * 
     * @return Map
     */
    public synchronized Map getTestMap()
    {
        Map testMap = null;
        if (null == (testMap = getTestMapBasic()))
        {
            updateTestMap();
            return getTestMap();
        }    
        return testMap;
    }
    
    /**
     * Method getComparatorMap answers a Map of Comparator names and their associated
     * class names, lazily initialized if required.
     * 
     * @return Map
     */
    public synchronized Map getComparatorMap()
    {
        Map comparatorMap = null;
        if (null == (comparatorMap = getComparatorMapBasic()))
        {
            updateComparatorMap();
            return getComparatorMap();
        }    
        return comparatorMap;
    }        
    
    /**
     * Method getCommandMapBasic answers a Map of Command names and their associated
     * class names.
     * 
     * @return Map
     */
    private Map getCommandMapBasic()
    {
        return fieldCommandMap;
    }
    
    /**
     * Method getTestMapBasic answers a Map of Test names and their associated
     * class names.
     * 
     * @return Map
     */
    private Map getTestMapBasic()
    {
        return fieldTestMap;
    }
    
    /**
     * Method getComparatorMapBasic answers a Map of Comparator names and their a
     * ssociated class names.
     * 
     * @return Map
     */
    private Map getComparatorMapBasic()
    {
        return fieldComparatorMap;
    }            
    
    /**
     * Method computeCommandMap answers a new CommandMap.
     * @return Map
     */
    protected Map computeCommandMap()
    {
        return new HashMap();
    }
    
    /**
     * Method computeTestMap answers a new TestMap.
     * @return Map
     */
    protected Map computeTestMap()
    {
        return new HashMap();
    } 
    
    /**
     * Method computeComparatorMap answers a new ComparatorMap.
     * @return Map
     */
    protected Map computeComparatorMap()
    {
        return new HashMap();
    }       
    
    /**
     * Method putCommandMapEntry adds an association between a Command name and an
     * implementation class to the Command Map.
     * 
     * @param name
     * @param className
     */
    public void putCommandMapEntry(String name, String className)
    {
        getCommandMap().put(name, className);
    }
    
    /**
     * Method putTestMapEntry adds an association between a Test name and an
     * implementation class to the Test Map.
     * 
     * @param name
     * @param className
     */
    public void putTestMapEntry(String name, String className)
    {
        getTestMap().put(name, className);
    }
    
    /**
     * Method putComparatorMapEntry adds an association between a Comparator name and
     * an implementation class to the Comparator Map.
     * 
     * @param name
     * @param className
     */
    public void putComparatorMapEntry(String name, String className)
    {
        getComparatorMap().put(name, className);
    }                        

    /**
     * Returns the digester, lazily initialised if required.
     * @return Digester
     */
    protected synchronized Digester getDigester()
    {
        Digester digester = null;
        if (null == (digester = getDigesterBasic()))
        {
            updateDigester();
            return getDigester();    
        }    
        return digester;
    }
    
    /**
     * Returns the digester.
     * @return Digester
     */
    private Digester getDigesterBasic()
    {
        return fieldDigester;
    }
    
    /**
     * Method computeDigester answers a new digester intialised with the rules to
     * parse the Sieve configuration file.
     * 
     * @return Digester
     */
    protected Digester computeDigester()
    {
        Digester digester = new Digester();
        digester.push(this);
        digester.setValidating(false);

        // CommandMap rules
        digester.addCallMethod(
            "sieve/commandMap/entry",
            "putCommandMapEntry",
            2,
            new Class[] { String.class, String.class });
        digester.addCallParam("sieve/commandMap/entry/name", 0);
        digester.addCallParam("sieve/commandMap/entry/class", 1);
        
        // TestMap rules
        digester.addCallMethod(
            "sieve/testMap/entry",
            "putTestMapEntry",
            2,
            new Class[] { String.class, String.class });
        digester.addCallParam("sieve/testMap/entry/name", 0);
        digester.addCallParam("sieve/testMap/entry/class", 1);
        
        // ComparatorMap rules
        digester.addCallMethod(
            "sieve/comparatorMap/entry",
            "putComparatorMapEntry",
            2,
            new Class[] { String.class, String.class });
        digester.addCallParam("sieve/comparatorMap/entry/name", 0);
        digester.addCallParam("sieve/comparatorMap/entry/class", 1);                  

        return digester;
    }       

    /**
     * Sets the digester.
     * @param digester The digester to set
     */
    protected void setDigester(Digester digester)
    {
        fieldDigester = digester;
    }
    
    /**
     * Updates the digester.
     */
    protected void updateDigester()
    {
        setDigester(computeDigester());
    }
    
    /**
     * Method parse uses the Digester to parse the XML statements in the Sieve 
     * configuration file into Java objects.
     * 
     * @return Object
     * @throws SAXException
     * @throws IOException
     */
    protected Object parse() throws SAXException, IOException
    {
        return getDigester().parse(getConfigStream());
    }        

    /**
     * Sets the commandMap.
     * @param commandMap The commandMap to set
     */
    protected void setCommandMap(Map commandMap)
    {
        fieldCommandMap = commandMap;
    }
    
    /**
     * Sets the testMap.
     * @param testMap The testMap to set
     */
    protected void setTestMap(Map testMap)
    {
        fieldTestMap = testMap;
    }
    
    /**
     * Sets the comparatorMap.
     * @param comparatorMap The comparatorMap to set
     */
    protected void setComparatorMap(Map comparatorMap)
    {
        fieldComparatorMap = comparatorMap;
    }        
    
    /**
     * Updates the commandMap.
     */
    protected void updateCommandMap()
    {
        setCommandMap(computeCommandMap());
    }
    
    /**
     * Updates the testMap.
     */
    protected void updateTestMap()
    {
        setTestMap(computeTestMap());
    }
    
    /**
     * Updates the comparatorMap.
     */
    protected void updateComparatorMap()
    {
        setComparatorMap(computeComparatorMap());
    }            

}
