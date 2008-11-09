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
import org.apache.mailet.MailetException;
import org.apache.mailet.Matcher;
/**
 * Loads Matchers for use inside James.
 *
 */
public class JamesMatcherLoader extends Loader implements MatcherLoader {
    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(Configuration)
     */
    public void configure(Configuration conf) throws ConfigurationException {
           getPackages(conf,MATCHER_PACKAGE);
    }

    /* (non-Javadoc)
         * @see org.apache.james.transport.MatcherLoader#getMatcher(java.lang.String)
         */
    public Matcher getMatcher(String matchName) throws MessagingException {
        try {
            String condition = (String) null;
            int i = matchName.indexOf('=');
            if (i != -1) {
                condition = matchName.substring(i + 1);
                matchName = matchName.substring(0, i);
            }
            for (i = 0; i < packages.size(); i++) {
                String className = (String) packages.elementAt(i) + matchName;
                try {
                    Matcher matcher = (Matcher) Thread.currentThread().getContextClassLoader().loadClass(className).newInstance();
                    MatcherConfigImpl configImpl = new MatcherConfigImpl();
                    configImpl.setMatcherName(matchName);
                    configImpl.setCondition(condition);
                    configImpl.setMailetContext(new MailetContextWrapper(mailetContext, getLogger().getChildLogger(matchName)));
                    matcher.init(configImpl);
                    return matcher;
                } catch (ClassNotFoundException cnfe) {
                    //do this so we loop through all the packages
                }
            }
            StringBuffer exceptionBuffer =
                new StringBuffer(128)
                    .append("Requested matcher not found: ")
                    .append(matchName)
                    .append(".  looked in ")
                    .append(packages.toString());
            throw new ClassNotFoundException(exceptionBuffer.toString());
        } catch (MessagingException me) {
            throw me;
        } catch (Exception e) {
            StringBuffer exceptionBuffer =
                new StringBuffer(128).append("Could not load matcher (").append(matchName).append(
                    ")");
            throw new MailetException(exceptionBuffer.toString(), e);
        }
    }
}
