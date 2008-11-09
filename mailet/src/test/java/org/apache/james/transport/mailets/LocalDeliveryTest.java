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

package org.apache.james.transport.mailets;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.james.Constants;
import org.apache.james.api.user.UsersRepository;
import org.apache.james.core.MailImpl;
import org.apache.james.impl.user.DefaultJamesUser;
import org.apache.james.services.MailRepository;
import org.apache.james.services.MailServer;
import org.apache.james.test.mock.avalon.MockServiceManager;
import org.apache.james.test.mock.james.InMemorySpoolRepository;
import org.apache.james.test.mock.james.MockMailServer;
import org.apache.james.test.mock.javaxmail.MockMimeMessage;
import org.apache.james.test.mock.mailet.MockMail;
import org.apache.james.test.mock.mailet.MockMailContext;
import org.apache.james.test.mock.mailet.MockMailetConfig;
import org.apache.james.test.mock.util.MailUtil;
import org.apache.james.userrepository.MockUsersRepository;
import org.apache.mailet.Mail;
import org.apache.mailet.MailAddress;
import org.apache.mailet.Mailet;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.ParseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;

/**
 * Test LocalDelivery Mailet
 */
public class LocalDeliveryTest extends TestCase {

    private HashMap mailboxes;
    private MockMailContext mockMailetContext;
    private MockServiceManager mockServiceManager;
    private MockMailServer mockMailServer;
    private MockUsersRepository mockUsersRepository;

    public void testUnknownUser() throws MessagingException {
        //mockMailetConfig.setProperty(key, value)
        Mailet m = getMailet(null);
        
        Mail mail = createMail(new String[] {"unknownuser@ignoreddomain"});
        m.service(mail);
        
        HashMap expectedMails = new HashMap();
        expectedMails.put("errors", new String[] {"unknownuser@ignoreddomain"});
        
        assertDeliveryWorked(mail, expectedMails);
    }


    public void testSimpleDelivery() throws MessagingException {
        //mockMailetConfig.setProperty(key, value)
        Mailet m = getMailet(null);
        
        Mail mail = createMail(new String[] {"localuser@ignoreddomain"});
        m.service(mail);
        
        HashMap expectedMails = new HashMap();
        expectedMails.put("localuser", new String[] {"localuser@ignoreddomain"});
        
        assertDeliveryWorked(mail, expectedMails);
    }

    public void testSimpleDeliveryCaseSensitiveNoMatch() throws MessagingException {
        //mockMailetConfig.setProperty(key, value)
        Mailet m = getMailet(null);
        
        Mail mail = createMail(new String[] {"localUser@ignoreddomain"});
        m.service(mail);
        
        HashMap expectedMails = new HashMap();
        expectedMails.put("errors", new String[] {"localUser@ignoreddomain"});
        
        assertDeliveryWorked(mail, expectedMails);
    }

    public void testSimpleDeliveryCaseSensitiveMatch() throws MessagingException {
        //mockMailetConfig.setProperty(key, value)
        mockUsersRepository.setIgnoreCase(true);
        Mailet m = getMailet(null);
        
        Mail mail = createMail(new String[] {"localUser@ignoreddomain"});
        m.service(mail);
        
        HashMap expectedMails = new HashMap();
        expectedMails.put("localuser", new String[] {"localuser@ignoreddomain"});
        
        assertDeliveryWorked(mail, expectedMails);
    }

    public void testSimpleAliasDelivery() throws MessagingException {
        mockUsersRepository.setEnableAliases(true);
        //mockMailetConfig.setProperty(key, value)
        Mailet m = getMailet(null);
        
        Mail mail = createMail(new String[] {"aliasedUser@ignoreddomain"});
        m.service(mail);
        
        HashMap expectedMails = new HashMap();
        expectedMails.put("localuser", new String[] {"localuser@ignoreddomain"});
        
        assertDeliveryWorked(mail, expectedMails);
    }

    public void testSimpleAliasWithDisabledAlias() throws MessagingException {
        //mockMailetConfig.setProperty(key, value)
        Mailet m = getMailet(null);
        
        Mail mail = createMail(new String[] {"aliasedUser@ignoreddomain"});
        m.service(mail);
        
        HashMap expectedMails = new HashMap();
        expectedMails.put("aliasedUser", new String[] {"aliasedUser@ignoreddomain"});
        
        assertDeliveryWorked(mail, expectedMails);
    }


    public void testForwardingWithForwardingDisabled() throws MessagingException {
        //mockMailetConfig.setProperty(key, value)
        Mailet m = getMailet(null);
        
        Mail mail = createMail(new String[] {"forwardingUser@ignoreddomain"});
        m.service(mail);
        
        HashMap expectedMails = new HashMap();
        expectedMails.put("errors", new String[] {"forwardingUser@ignoreddomain"});
        
        assertDeliveryWorked(mail, expectedMails);
    }


    public void testForwarding() throws MessagingException {
        mockUsersRepository.setEnableForwarding(true);
        //mockMailetConfig.setProperty(key, value)
        Mailet m = getMailet(null);
        
        Mail mail = createMail(new String[] {"forwardingUser@ignoreddomain"});
        m.service(mail);
        
        HashMap expectedMails = new HashMap();
        expectedMails.put("resent", new String[] {"remoteuser@remotedomain"});
        
        assertDeliveryWorked(mail, expectedMails);
    }

    public void testAliasingForwarding() throws MessagingException {
        mockUsersRepository.setEnableAliases(true);
        mockUsersRepository.setEnableForwarding(true);
        //mockMailetConfig.setProperty(key, value)
        Mailet m = getMailet(null);
        
        Mail mail = createMail(new String[] {"aliasForwardUser@ignoreddomain"});
        m.service(mail);
        
        HashMap expectedMails = new HashMap();
        expectedMails.put("resent", new String[] {"remoteuser@remotedomain"});
        
        assertDeliveryWorked(mail, expectedMails);
    }

    public void testAliasingForwardingWithLocallyOverriddenForwarding() throws MessagingException {
        mockUsersRepository.setEnableAliases(true);
        mockUsersRepository.setEnableForwarding(false);
        Mailet m = getMailet(null);

        Mail mail = createMail(new String[] {"aliasForwardUser@ignoreddomain"});
        m.service(mail);
        
        HashMap expectedMails = new HashMap();
        expectedMails.put("localuser", new String[] {"localuser@ignoreddomain"});
        
        assertDeliveryWorked(mail, expectedMails);
    }

    /* Commented out because "forwarding" to local is no more available
     * under the new structure.
     * If we'll ever change VirtualUserTable to return different collections
     * for local and remote addresses this will be enabled again.
    public void testForwardingToLocal() throws MessagingException {
        mockUsersRepository.setEnableAliases(true);
        mockUsersRepository.setEnableForwarding(true);
        //mockMailetConfig.setProperty(key, value)
        Mailet m = getMailet(null);
        
        Mail mail = createMail(new String[] {"forwardToLocal@ignoreddomain"});
        m.service(mail);
        
        HashMap expectedMails = new HashMap();
        expectedMails.put("resent", new String[] {"localuser@ignoreddomain"});
        
        assertDeliveryWorked(mail, expectedMails);
    }
    */
    
    public void testSimpleDeliveryVirtualHosting() throws MessagingException {
        //enable virtual hosting
        mockMailServer.setVirtualHosting(true);
        Mailet m = getMailet(null);
           
        Mail mail = createMail(new String[] {"virtual@hosting"});
        m.service(mail);
            
        HashMap expectedMails = new HashMap();
        expectedMails.put("virtual@hosting", new String[] {"virtual@hosting"});
           
        assertDeliveryWorked(mail, expectedMails);
    }

    /**
     * @throws ParseException 
     * 
     */
    public void setUp() throws ParseException {
        mockServiceManager = new MockServiceManager();
        mockUsersRepository = new MockUsersRepository();
        mockUsersRepository.setForceUseJamesUser();
        mockUsersRepository.addUser("localuser", "password");
        mockUsersRepository.addUser("aliasedUser", "pass2");
        
        // VirtualHosting
        mockUsersRepository.addUser("virtual@hosting","pass");

        DefaultJamesUser u = (DefaultJamesUser) mockUsersRepository.getUserByName("aliasedUser");
        u.setAliasing(true);
        u.setAlias("localuser");
        mockUsersRepository.addUser("forwardingUser", "pass2");
        u = (DefaultJamesUser) mockUsersRepository.getUserByName("forwardingUser");
        u.setForwarding(true);
        u.setForwardingDestination(new MailAddress("remoteuser@remotedomain"));
        mockUsersRepository.addUser("aliasForwardUser", "pass2");
        u = (DefaultJamesUser) mockUsersRepository.getUserByName("aliasForwardUser");
        u.setAliasing(true);
        u.setAlias("localuser");
        u.setForwarding(true);
        u.setForwardingDestination(new MailAddress("remoteuser@remotedomain"));
        mockUsersRepository.addUser("forwardToLocal", "pass2");
        u = (DefaultJamesUser) mockUsersRepository.getUserByName("forwardToLocal");
        u.setForwarding(true);
        u.setForwardingDestination(new MailAddress("localuser@ignoreddomain"));
        mockServiceManager.put(UsersRepository.ROLE,mockUsersRepository);
        mockMailServer = new MockMailServer(mockUsersRepository);
        mailboxes = new HashMap();
        mailboxes.put("localuser", new InMemorySpoolRepository());
        mailboxes.put("aliasedUser", new InMemorySpoolRepository());
        mailboxes.put("virtual@hosting",new InMemorySpoolRepository());
        Iterator mbi = mailboxes.keySet().iterator();
        while (mbi.hasNext()) {
            String mboxName = (String) mbi.next();
            mockMailServer.setUserInbox(mboxName, (MailRepository) mailboxes.get(mboxName));
        }
        mockServiceManager.put(MailServer.ROLE, mockMailServer);

        mockMailetContext = new MockMailContext() {

            public void sendMail(MailAddress sender, Collection recipients, MimeMessage msg) throws MessagingException {
                mockMailServer.sendMail(sender, recipients, msg);
            }

            public void sendMail(MailAddress sender, Collection recipients, MimeMessage msg, String state) throws MessagingException {
                MailImpl m = new MailImpl(MailUtil.newId(), sender, recipients, msg);
                m.setState(state);
                mockMailServer.sendMail(m);
                m.dispose();
            }

            public boolean isLocalServer(String serverName) {
                if ("ignoreddomain".equals(serverName)) {
                    return true;
                }
                return super.isLocalServer(serverName);
            }
            
            
        };
        mockUsersRepository.setEnableAliases(false);
        mockUsersRepository.setEnableForwarding(false);
        mockUsersRepository.setIgnoreCase(false);
        mockMailetContext.setAttribute(Constants.AVALON_COMPONENT_MANAGER, mockServiceManager);
    }
    
    public void tearDown() {
        mockMailetContext = null;
        mailboxes = null;
        mockServiceManager = null;
    }

    /**
     * @param mail
     * @param expectedMails
     * @throws MessagingException
     */
    private void assertDeliveryWorked(Mail mail, HashMap expectedMails) throws MessagingException {
        assertEquals(Mail.GHOST, mail.getState());
        Iterator mboxes = mailboxes.keySet().iterator();
        while (mboxes.hasNext()) {
            String mboxName = (String) mboxes.next();
            MailRepository inMemorySpoolRepository = (MailRepository) mailboxes.get(mboxName);
            assertExpectedMailsInRepository(mail, (String[]) expectedMails.get(mboxName), inMemorySpoolRepository);
            
        }
        
        MailRepository sentMailsRepository = mockMailServer.getSentMailsRepository();
        MailRepository errorsMailRepository = new InMemorySpoolRepository();
        
        Iterator keys = sentMailsRepository.list();
        while (keys.hasNext()) {
            String nextKey = (String) keys.next();
            Mail m = sentMailsRepository.retrieve(nextKey);
            if (Mail.ERROR.equals(m.getState())) {
                errorsMailRepository.store(m);
                sentMailsRepository.remove(nextKey);
            } else {
                assertEquals("Found a mail in outgoing having a state different from ERROR or ROOT", Mail.DEFAULT, m.getState());
            }
            
            ContainerUtil.dispose(m);
        }
        
        assertExpectedMailsInRepository(mail, (String[]) expectedMails.get("resent"), sentMailsRepository);
        assertExpectedMailsInRepository(mail, (String[]) expectedMails.get("errors"), errorsMailRepository);
        
        ContainerUtil.dispose(errorsMailRepository);
    }


    /**
     * @param mail
     * @param expectedMails
     * @param mboxName
     * @param inMemorySpoolRepository
     * @throws MessagingException
     */
    private void assertExpectedMailsInRepository(Mail mail, String[] expectedDeliveries, MailRepository inMemorySpoolRepository) throws MessagingException {
        List c = expectedDeliveries != null ? new ArrayList(Arrays.asList(expectedDeliveries)) : new ArrayList();
        Iterator i = inMemorySpoolRepository.list();
        System.out.println("check: "+c.size()+"|"+inMemorySpoolRepository);
        for (int j = 0; j < c.size(); j++) {
            assertTrue("No mails have been found in the repository", i.hasNext());
            String next = (String) i.next();
            assertNotNull("Mail has not been stored", next);
            Mail storedMail = inMemorySpoolRepository.retrieve(next);
            assertNotNull("Mail cannot be retrieved", storedMail);
            String recipient = storedMail.getRecipients().iterator().next().toString();
            assertTrue("Message has been delivered to the wrong user", c.contains(recipient));
            c.remove(recipient);
        }
        assertEquals("Message has not been delivered to all expected recipients", c.size(), 0);
        if (i.hasNext()) {
            String tooMuch = (String) i.next();
            Mail m = inMemorySpoolRepository.retrieve(tooMuch);
            fail("Found unexpected mail for recipient "+m.getRecipients().iterator().next().toString()+"!");
        }
    }

    /**
     * @return
     * @throws MessagingException 
     */
    private Mail createMail(String[] recipients) throws MessagingException {
        Mail mail = new MockMail();
        ArrayList a = new ArrayList(recipients.length);
        for (int i = 0; i < recipients.length; i++) {
            a.add(new MailAddress(recipients[i]));
        }
        mail.setRecipients(a);
        mail.setMessage(new MockMimeMessage());
        return mail;
    }
    
    public Mailet getMailet(Properties p) throws MessagingException {
        MockMailetConfig mockMailetConfig = new MockMailetConfig("TestedLocalDelivery", mockMailetContext, p);
        Mailet m = new LocalDelivery();
        m.init(mockMailetConfig);
        return m;
    }
}
