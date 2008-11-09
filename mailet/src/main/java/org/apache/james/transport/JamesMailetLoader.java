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
import javax.mail.MessagingException;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.mailet.Mailet;
import org.apache.mailet.MailetException;
/**
 * Loads Mailets for use inside James.
 *
 */
public class JamesMailetLoader extends Loader implements MailetLoader {
    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(Configuration)
     */
    public void configure(Configuration conf) throws ConfigurationException {
        getPackages(conf,MAILET_PACKAGE);
    }

    /**
     * @see org.apache.james.transport.MailetLoader#getMailet(java.lang.String, org.apache.avalon.framework.configuration.Configuration)
     */
    public Mailet getMailet(String mailetName, Configuration configuration)
        throws MessagingException {
        try {
            for (int i = 0; i < packages.size(); i++) {
                String className = (String) packages.elementAt(i) + mailetName;
                try {
                    Mailet mailet = (Mailet) Thread.currentThread().getContextClassLoader().loadClass(className).newInstance();
                    MailetConfigImpl configImpl = new MailetConfigImpl();
                    configImpl.setMailetName(mailetName);
                    configImpl.setConfiguration(configuration);
                    configImpl.setMailetContext(new MailetContextWrapper(mailetContext, getLogger().getChildLogger(mailetName))); 
                    mailet.init(configImpl);
                    return mailet;
                } catch (ClassNotFoundException cnfe) {
                    //do this so we loop through all the packages
                }
            }
            StringBuffer exceptionBuffer =
                new StringBuffer(128)
                    .append("Requested mailet not found: ")
                    .append(mailetName)
                    .append(".  looked in ")
                    .append(packages.toString());
            throw new ClassNotFoundException(exceptionBuffer.toString());
        } catch (MessagingException me) {
            throw me;
        } catch (Exception e) {
            StringBuffer exceptionBuffer =
                new StringBuffer(128).append("Could not load mailet (").append(mailetName).append(
                    ")");
            throw new MailetException(exceptionBuffer.toString(), e);
        }
    }
}
