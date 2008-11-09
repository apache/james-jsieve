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



package org.apache.james.transport;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.mailet.MailetConfig;
import org.apache.mailet.MailetContext;

import java.util.Iterator;

/**
 * Implements the configuration object for a Mailet.
 *
 * @version CVS $Revision$ $Date$
 */
public class MailetConfigImpl implements MailetConfig {

    /**
     * The mailet MailetContext
     */
    private MailetContext mailetContext;

    /**
     * The mailet name
     */
    private String name;

    //This would probably be better.
    //Properties params = new Properties();
    //Instead, we're tied to the Configuration object
    /**
     * The mailet Avalon Configuration
     */
    private Configuration configuration;

    /**
     * No argument constructor for this object.
     */
    public MailetConfigImpl() {
    }

    /**
     * Get the value of an parameter stored in this MailetConfig.  Multi-valued
     * parameters are returned as a comma-delineated string.
     *
     * @param name the name of the parameter whose value is to be retrieved.
     *
     * @return the parameter value
     */
    public String getInitParameter(String name) {
        try {
            String result = null;

            final Configuration[] values = configuration.getChildren( name );
            for ( int i = 0; i < values.length; i++ ) {
                if (result == null) {
                    result = "";
                } else {
                    result += ",";
                }
                Configuration conf = values[i];
                result += conf.getValue();
            }
            return result;
        } catch (ConfigurationException ce) {
            throw new RuntimeException("Embedded configuration exception was: " + ce.getMessage());
        }

    }

    /**
     * Returns an iterator over the set of configuration parameter names.
     *
     * @return an iterator over the set of configuration parameter names.
     */
    public Iterator getInitParameterNames() {
        return new Iterator () {
            Configuration[] children;
            int count = 0;
            {
                children = configuration.getChildren();
            }

            public boolean hasNext() {
                return count < children.length;
            }

            public Object next() {
                return children[count++].getName();
            }

            public void remove() {
                throw new UnsupportedOperationException ("remove not supported");
            }
        };
    }

    /**
     * Get the value of an (XML) attribute stored in this MailetConfig.
     *
     * @param name the name of the attribute whose value is to be retrieved.
     *
     * @return the attribute value or null if missing
     */
    public String getInitAttribute(String name) {
        return configuration.getAttribute(name, null);
    }

    /**
     * Get the mailet's MailetContext object.
     *
     * @return the MailetContext for the mailet
     */
    public MailetContext getMailetContext() {
        return mailetContext;
    }

    /**
     * Set the mailet's Avalon Configuration object.
     *
     * @param newContext the MailetContext for the mailet
     */
    public void setMailetContext(MailetContext newContext) {
        mailetContext = newContext;
    }

    /**
     * Set the Avalon Configuration object for the mailet.
     *
     * @param newConfiguration the new Configuration for the mailet
     */
    public void setConfiguration(Configuration newConfiguration) {
        configuration = newConfiguration;
    }

    /**
     * Get the name of the mailet.
     *
     * @return the name of the mailet
     */
    public String getMailetName() {
        return name;
    }

    /**
     * Set the name for the mailet.
     *
     * @param newName the new name for the mailet
     */
    public void setMailetName(String newName) {
        name = newName;
    }
}
