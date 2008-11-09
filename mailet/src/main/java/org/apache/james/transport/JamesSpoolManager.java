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
import org.apache.james.services.SpoolRepository;
import org.apache.james.services.SpoolManager;
import org.apache.mailet.Mail;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

/**
 * Manages the mail spool.  This class is responsible for retrieving
 * messages from the spool, directing messages to the appropriate
 * processor, and removing them from the spool when processing is
 * complete.
 *
 * @version CVS $Revision$ $Date$
 */
public class JamesSpoolManager
    extends AbstractLogEnabled
    implements Serviceable, Configurable, Initializable, Runnable, Disposable, SpoolManager {

    /**
     * System component manager
     */
    private ServiceManager compMgr;

    /**
     * The spool that this manager will process
     */
    private SpoolRepository spool;

    /**
     * The number of threads used to move mail through the spool.
     */
    private int numThreads;

    /**
     * The ThreadPool containing worker threads.
     *
     * This used to be used, but for threads that lived the entire
     * lifespan of the application.  Currently commented out.  In
     * the future, we could use a thread pool to run short-lived
     * workers, so that we have a smaller number of readers that
     * accept a message from the spool, and dispatch to a pool of
     * worker threads that process the message.
     */
    // private ThreadPool workerPool;

    /**
     * The ThreadManager from which the thread pool is obtained.
     */
    // private ThreadManager threadManager;

    /**
     * Number of active threads
     */
    private int numActive;

    /**
     * Spool threads are active
     */
    private boolean active;

    /**
     * Spool threads
     */
    private Collection spoolThreads;

    /**
     * The mail processor 
     */
    private MailProcessor processorList;

    /**
     * Set the SpoolRepository
     * 
     * @param spool the SpoolRepository
     */
    public void setSpool(SpoolRepository spool) {
        this.spool = spool;
    }

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(ServiceManager)
     */
    public void service(ServiceManager comp) throws ServiceException {
        compMgr = comp;
        setSpool((SpoolRepository) compMgr.lookup(SpoolRepository.ROLE));
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(Configuration)
     */
    public void configure(Configuration conf) throws ConfigurationException {
        numThreads = conf.getChild("threads").getValueAsInteger(1);

        String processorClass = conf.getChild("processorClass").getValue("org.apache.james.transport.StateAwareProcessorList");
        try {
            processorList = (MailProcessor) Thread.currentThread().getContextClassLoader().loadClass(processorClass).newInstance();
        } catch (Exception e1) {
            getLogger().error("Unable to instantiate spoolmanager processor: "+processorClass, e1);
            throw new ConfigurationException("Instantiation exception: "+processorClass, e1);
        }

        try {
            ContainerUtil.enableLogging(processorList, getLogger());
            ContainerUtil.service(processorList, compMgr);
        } catch (ServiceException e) {
            getLogger().error(e.getMessage(), e);
            throw new ConfigurationException("Servicing failed with error: "+e.getMessage(),e);
        }

        ContainerUtil.configure(processorList, conf);
    }

    /**
     * @see org.apache.avalon.framework.activity.Initializable#initialize()
     */
    public void initialize() throws Exception {

        getLogger().info("JamesSpoolManager init...");

        ContainerUtil.initialize(processorList);

        if (getLogger().isInfoEnabled()) {
            StringBuffer infoBuffer =
                new StringBuffer(64)
                    .append("Spooler Manager uses ")
                    .append(numThreads)
                    .append(" Thread(s)");
            getLogger().info(infoBuffer.toString());
        }

        active = true;
        numActive = 0;
        spoolThreads = new java.util.ArrayList(numThreads);
        for ( int i = 0 ; i < numThreads ; i++ ) {
            Thread reader = new Thread(this, "Spool Thread #" + i);
            spoolThreads.add(reader);
            reader.start();
        }
    }

    /**
     * This routinely checks the message spool for messages, and processes
     * them as necessary
     */
    public void run() {

        if (getLogger().isInfoEnabled())
        {
            getLogger().info("Run JamesSpoolManager: "
                             + Thread.currentThread().getName());
            getLogger().info("Spool=" + spool.getClass().getName());
        }

        numActive++;
        while(active) {
            String key = null;
            try {
                Mail mail = (Mail)spool.accept();
                key = mail.getName();
                if (getLogger().isDebugEnabled()) {
                    StringBuffer debugBuffer =
                        new StringBuffer(64)
                                .append("==== Begin processing mail ")
                                .append(mail.getName())
                                .append("====");
                    getLogger().debug(debugBuffer.toString());
                }

                processorList.service(mail);

                // Only remove an email from the spool is processing is
                // complete, or if it has no recipients
                if ((Mail.GHOST.equals(mail.getState())) ||
                    (mail.getRecipients() == null) ||
                    (mail.getRecipients().size() == 0)) {
                    ContainerUtil.dispose(mail);
                    spool.remove(key);
                    if (getLogger().isDebugEnabled()) {
                        StringBuffer debugBuffer =
                            new StringBuffer(64)
                                    .append("==== Removed from spool mail ")
                                    .append(key)
                                    .append("====");
                        getLogger().debug(debugBuffer.toString());
                    }
                }
                else {
                    // spool.remove() has a side-effect!  It unlocks the
                    // message so that other threads can work on it!  If
                    // we don't remove it, we must unlock it!
                    spool.store(mail);
                    ContainerUtil.dispose(mail);
                    spool.unlock(key);
                    // Do not notify: we simply updated the current mail
                    // and we are able to reprocess it now.
                }
                mail = null;
            } catch (InterruptedException ie) {
                getLogger().info("Interrupted JamesSpoolManager: " + Thread.currentThread().getName());
            } catch (Throwable e) {
                if (getLogger().isErrorEnabled()) {
                    getLogger().error("Exception processing " + key + " in JamesSpoolManager.run "
                                      + e.getMessage(), e);
                }
                /* Move the mail to ERROR state?  If we do, it could be
                 * deleted if an error occurs in the ERROR processor.
                 * Perhaps the answer is to resolve that issue by
                 * having a special state for messages that are not to
                 * be processed, but aren't to be deleted?  The message
                 * would already be in the spool, but would not be
                 * touched again.
                if (mail != null) {
                    try {
                        mail.setState(Mail.ERROR);
                        spool.store(mail);
                    }
                }
                */
            }
        }
        if (getLogger().isInfoEnabled())
        {
            getLogger().info("Stop JamesSpoolManager: " + Thread.currentThread().getName());
        }
        numActive--;
    }

    /**
     * The dispose operation is called at the end of a components lifecycle.
     * Instances of this class use this method to release and destroy any
     * resources that they own.
     *
     * This implementation shuts down the LinearProcessors managed by this
     * JamesSpoolManager
     * 
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose() {
        getLogger().info("JamesSpoolManager dispose...");
        active = false; // shutdown the threads
        for (Iterator it = spoolThreads.iterator(); it.hasNext(); ) {
            ((Thread) it.next()).interrupt(); // interrupt any waiting accept() calls.
        }

        long stop = System.currentTimeMillis() + 60000;
        // give the spooler threads one minute to terminate gracefully
        while (numActive != 0 && stop > System.currentTimeMillis()) {
            try {
                Thread.sleep(1000);
            } catch (Exception ignored) {}
        }
        getLogger().info("JamesSpoolManager thread shutdown completed.");

        ContainerUtil.dispose(processorList);
    }

    public String[] getProcessorNames() {
        if (!(processorList instanceof ProcessorList)) {
            return new String[0];  
        }
        String[] processorNames = ((ProcessorList) processorList).getProcessorNames();
        return processorNames;
    }

    public List getMailetConfigs(String processorName) {
        MailetContainer mailetContainer = getMailetContainerByName(processorName);
        if (mailetContainer == null) return new ArrayList();
        return mailetContainer.getMailetConfigs();
    }

    public List getMatcherConfigs(String processorName) {
        MailetContainer mailetContainer = getMailetContainerByName(processorName);
        if (mailetContainer == null) return new ArrayList();
        return mailetContainer.getMatcherConfigs();
    }

    private MailetContainer getMailetContainerByName(String processorName) {
        if (!(processorList instanceof ProcessorList)) return null;
        
        MailProcessor processor = ((ProcessorList) processorList).getProcessor(processorName);
        if (!(processor instanceof MailetContainer)) return null;
        // TODO: decide, if we have to visit all sub-processors for being ProcessorLists 
        // on their very own and collecting the processor names deeply.
        return (MailetContainer)processor;
    }
}
