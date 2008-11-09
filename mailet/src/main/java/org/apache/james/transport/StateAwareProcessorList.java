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

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.mailet.Mail;
import org.apache.mailet.MailetException;
import javax.mail.MessagingException;

import java.util.HashMap;
import java.util.Iterator;

/**
 * This class is responsible for creating a set of named processors and
 * directing messages to the appropriate processor (given the State of the mail)
 *
 * @version CVS $Revision: 405882 $ $Date: 2006-05-12 23:30:04 +0200 (ven, 12 mag 2006) $
 */
public class StateAwareProcessorList
    extends AbstractLogEnabled
    implements Serviceable, Configurable, Initializable, Disposable, MailProcessor, ProcessorList {

    /**
     * System component manager
     */
    private ServiceManager compMgr;

    /**
     * The configuration object used by this spool manager.
     */
    private Configuration conf;

    /**
     * The map of processor names to processors
     */
    private HashMap processors;

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(ServiceManager)
     */
    public void service(ServiceManager comp) throws ServiceException {
        // threadManager = (ThreadManager) comp.lookup(ThreadManager.ROLE);
        compMgr = comp;
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(Configuration)
     */
    public void configure(Configuration conf) throws ConfigurationException {
        this.conf = conf;
    }

    /**
     * @see org.apache.avalon.framework.activity.Initializable#initialize()
     */
    public void initialize() throws Exception {
        //A processor is a Collection of
        processors = new HashMap();

        final Configuration[] processorConfs = conf.getChildren( "processor" );
        for ( int i = 0; i < processorConfs.length; i++ )
        {
            Configuration processorConf = processorConfs[i];
            String processorName = processorConf.getAttribute("name");
            String processorClass = processorConf.getAttribute("class","org.apache.james.transport.LinearProcessor");

            try {
                MailProcessor processor = (MailProcessor) Thread.currentThread().getContextClassLoader().loadClass(processorClass).newInstance();
                processors.put(processorName, processor);
                
                setupLogger(processor, processorName);
                ContainerUtil.service(processor, compMgr);
                ContainerUtil.configure(processor, processorConf);
                
                if (getLogger().isInfoEnabled()) {
                    StringBuffer infoBuffer =
                        new StringBuffer(64)
                                .append("Processor ")
                                .append(processorName)
                                .append(" instantiated.");
                    getLogger().info(infoBuffer.toString());
                }
            } catch (Exception ex) {
                if (getLogger().isErrorEnabled()) {
                    StringBuffer errorBuffer =
                       new StringBuffer(256)
                               .append("Unable to init processor ")
                               .append(processorName)
                               .append(": ")
                               .append(ex.toString());
                    getLogger().error( errorBuffer.toString(), ex );
                }
                throw ex;
            }
        }
    }
    
    /**
     * Process this mail message by the appropriate processor as designated
     * in the state of the Mail object.
     *
     * @param mail the mail message to be processed
     *
     * @see org.apache.james.transport.MailProcessor#service(org.apache.mailet.Mail)
     */
    public void service(Mail mail) {
        while (true) {
            String processorName = mail.getState();
            if (processorName.equals(Mail.GHOST)) {
                //This message should disappear
                return;
            }
            try {
                MailProcessor processor
                    = (MailProcessor)processors.get(processorName);
                if (processor == null) {
                    StringBuffer exceptionMessageBuffer =
                        new StringBuffer(128)
                            .append("Unable to find processor ")
                            .append(processorName)
                            .append(" requested for processing of ")
                            .append(mail.getName());
                    String exceptionMessage = exceptionMessageBuffer.toString();
                    getLogger().debug(exceptionMessage);
                    mail.setState(Mail.ERROR);
                    throw new MailetException(exceptionMessage);
                }
                StringBuffer logMessageBuffer = null;
                if (getLogger().isDebugEnabled()) {
                    logMessageBuffer =
                        new StringBuffer(64)
                                .append("Processing ")
                                .append(mail.getName())
                                .append(" through ")
                                .append(processorName);
                    getLogger().debug(logMessageBuffer.toString());
                }
                processor.service(mail);
                if (getLogger().isDebugEnabled()) {
                    logMessageBuffer =
                        new StringBuffer(128)
                                .append("Processed ")
                                .append(mail.getName())
                                .append(" through ")
                                .append(processorName);
                    getLogger().debug(logMessageBuffer.toString());
                    getLogger().debug("Result was " + mail.getState());
                }
                return;
            } catch (Throwable e) {
                // This is a strange error situation that shouldn't ordinarily
                // happen
                StringBuffer exceptionBuffer = 
                    new StringBuffer(64)
                            .append("Exception in processor <")
                            .append(processorName)
                            .append(">");
                getLogger().error(exceptionBuffer.toString(), e);
                if (processorName.equals(Mail.ERROR)) {
                    // We got an error on the error processor...
                    // kill the message
                    mail.setState(Mail.GHOST);
                    mail.setErrorMessage(e.getMessage());
                } else {
                    //We got an error... send it to the requested processor
                    if (!(e instanceof MessagingException)) {
                        //We got an error... send it to the error processor
                        mail.setState(Mail.ERROR);
                    }
                    mail.setErrorMessage(e.getMessage());
                }
            }
            if (getLogger().isErrorEnabled()) {
                StringBuffer logMessageBuffer =
                    new StringBuffer(128)
                            .append("An error occurred processing ")
                            .append(mail.getName())
                            .append(" through ")
                            .append(processorName);
                getLogger().error(logMessageBuffer.toString());
                getLogger().error("Result was " + mail.getState());
            }
        }
    }

    /**
     * The dispose operation is called at the end of a components lifecycle.
     * Instances of this class use this method to release and destroy any
     * resources that they own.
     *
     * This implementation shuts down the Processors managed by this
     * Component
     *
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose() {
        Iterator it = processors.keySet().iterator();
        while (it.hasNext()) {
            String processorName = (String)it.next();
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Processor " + processorName);
            }
            Object processor = processors.get(processorName);
            ContainerUtil.dispose(processor);
            processors.remove(processor);
        }
    }

    /**
     * @return names of all configured processors
     */
    public String[] getProcessorNames() {
        return (String[]) processors.keySet().toArray(new String[]{});
    }

    public MailProcessor getProcessor(String name) {
        return (MailProcessor) processors.get(name);
    }

}
