/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */

package org.apache.james.managesieve.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.james.managesieve.api.AuthenticationRequiredException;
import org.apache.james.managesieve.api.DuplicateException;
import org.apache.james.managesieve.api.IsActiveException;
import org.apache.james.managesieve.api.QuotaExceededException;
import org.apache.james.managesieve.api.ScriptNotFoundException;
import org.apache.james.managesieve.api.ScriptSummary;
import org.apache.james.managesieve.api.SieveRepository;
import org.apache.james.managesieve.api.StorageException;
import org.apache.james.managesieve.api.SyntaxException;
import org.apache.james.managesieve.api.UserNotFoundException;
import org.apache.james.managesieve.api.commands.CoreCommands;
import org.apache.james.managesieve.api.commands.Capability.Capabilities;
import org.apache.james.managesieve.mock.MockSession;
import org.apache.james.managesieve.mock.MockSieveParser;
import org.apache.james.managesieve.mock.MockSieveRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * <code>CoreProcessorTestCase</code>
 */
public class CoreProcessorTestCase {

    /**
     * setUp.
     *
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * tearDown.
     *
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for {@link org.apache.james.managesieve.core.CoreProcessor#CoreProcessor(org.apache.james.managesieve.api.Session, org.apache.james.managesieve.api.SieveRepository, org.apache.james.managesieve.api.SieveParser)}.
     */
    @Test
    public final void testCoreProcessor() {
        CoreProcessor core = new CoreProcessor(new MockSession(), new MockSieveRepository(), new MockSieveParser());
        assertTrue(core instanceof CoreCommands);
    }

    /**
     * Test method for {@link org.apache.james.managesieve.core.CoreProcessor#capability()}.
     */
    @Test
    public final void testCapability() {
        MockSession session = new MockSession();
        MockSieveParser parser = new MockSieveParser();
        CoreProcessor core = new CoreProcessor(session, new MockSieveRepository(), parser);

        // Unauthenticated
        session.setAuthentication(false);
        parser.setExtensions(Arrays.asList(new String[]{"a","b","c"}));
        Map<Capabilities, String> capabilities = core.capability();
        assertEquals(CoreProcessor.IMPLEMENTATION_DESCRIPTION, capabilities.get(Capabilities.IMPLEMENTATION));
        assertEquals(CoreProcessor.MANAGE_SIEVE_VERSION, capabilities.get(Capabilities.VERSION));
        assertEquals("a b c", capabilities.get(Capabilities.SIEVE));
        assertFalse(capabilities.containsKey(Capabilities.OWNER));
        assertTrue(capabilities.containsKey(Capabilities.GETACTIVE));
        
        // Authenticated
        session.setAuthentication(true);
        parser.setExtensions(Arrays.asList(new String[]{"a","b","c"}));
        session.setUser("test");
        capabilities = core.capability();
        assertEquals(CoreProcessor.IMPLEMENTATION_DESCRIPTION, capabilities.get(Capabilities.IMPLEMENTATION));
        assertEquals(CoreProcessor.MANAGE_SIEVE_VERSION, capabilities.get(Capabilities.VERSION));
        assertEquals("a b c", capabilities.get(Capabilities.SIEVE));
        assertEquals("test", capabilities.get(Capabilities.OWNER));
        assertTrue(capabilities.containsKey(Capabilities.GETACTIVE));        
    }

    /**
     * Test method for {@link org.apache.james.managesieve.core.CoreProcessor#checkScript(java.lang.String)}.
     * @throws SyntaxException 
     * @throws AuthenticationRequiredException 
     */
    @Test
    public final void testCheckScript() throws AuthenticationRequiredException, SyntaxException {
        MockSession session = new MockSession();
        CoreProcessor core = new CoreProcessor(session, new MockSieveRepository(), new MockSieveParser());

        // Unauthorised
        boolean success = false;
        session.setAuthentication(false);
        try {
            core.checkScript("warning");
        } catch (AuthenticationRequiredException ex) {
            success = true;
        }
        assertTrue("Expected AuthenticationRequiredException", success);

        // Authorised
        session.setAuthentication(true);
        session.setUser("test");
        List<String> warnings = core.checkScript("warning");
        assertEquals(2, warnings.size());
        assertEquals("warning1", warnings.get(0));
        assertEquals("warning2", warnings.get(1));

        // Syntax
        success = false;
        session.setAuthentication(true);
        session.setUser("test");
        try {
            core.checkScript("SyntaxException");
        } catch (SyntaxException ex) {
            success = true;
        }
        assertTrue("Expected SyntaxException", success);
    }

    /**
     * Test method for {@link org.apache.james.managesieve.core.CoreProcessor#deleteScript(java.lang.String)}.
     * @throws IsActiveException 
     * @throws ScriptNotFoundException 
     * @throws AuthenticationRequiredException 
     * @throws QuotaExceededException 
     * @throws StorageException 
     * @throws UserNotFoundException 
     */
    @Test
    public final void testDeleteScript() throws ScriptNotFoundException, IsActiveException, AuthenticationRequiredException, UserNotFoundException, StorageException, QuotaExceededException {
        MockSession session = new MockSession();
        SieveRepository repository = new MockSieveRepository();
        CoreProcessor core = new CoreProcessor(session, repository, new MockSieveParser());

        // Unauthorised
        boolean success = false;
        session.setAuthentication(false);
        try {
            core.deleteScript("script");
        } catch (AuthenticationRequiredException ex) {
            success = true;
        }
        assertTrue("Expected AuthenticationRequiredException", success);

        // Authorised - non-existent script
        success = false;
        session.setAuthentication(true);
        session.setUser("test");
        try {
            core.deleteScript("script");
        } catch (ScriptNotFoundException ex) {
            success = true;
        }
        assertTrue("Expected ScriptNotFoundException", success);
        
        // Authorised - existent script
        session.setAuthentication(true);
        session.setUser("test");
        repository.putScript("test", "script", "content");
        core.deleteScript("script");
        success = false;
        try {
            repository.getScript("test", "script");
        } catch (ScriptNotFoundException ex) {
            success = true;
        }
        assertTrue("Expected ScriptNotFoundException", success);
        
        // Authorised - active script
        success = false;
        session.setAuthentication(true);
        session.setUser("test");
        repository.putScript("test", "script", "content");
        repository.setActive("test", "script");
        try {
            core.deleteScript("script");
        } catch (IsActiveException ex) {
            success = true;
        }
        assertTrue("Expected IsActiveException", success);
    }

    /**
     * Test method for {@link org.apache.james.managesieve.core.CoreProcessor#getScript(java.lang.String)}.
     * @throws ScriptNotFoundException 
     * @throws AuthenticationRequiredException 
     * @throws QuotaExceededException 
     * @throws StorageException 
     * @throws UserNotFoundException 
     */
    @Test
    public final void testGetScript() throws ScriptNotFoundException, AuthenticationRequiredException, UserNotFoundException, StorageException, QuotaExceededException {
        MockSession session = new MockSession();
        SieveRepository repository = new MockSieveRepository();
        CoreProcessor core = new CoreProcessor(session, repository, new MockSieveParser());

        // Unauthorised
        boolean success = false;
        session.setAuthentication(false);
        try {
            core.getScript("script");
        } catch (AuthenticationRequiredException ex) {
            success = true;
        }
        assertTrue("Expected AuthenticationRequiredException", success);

        // Authorised - non-existent script
        success = false;
        session.setAuthentication(true);
        session.setUser("test");
        try {
            core.getScript("script");
        } catch (ScriptNotFoundException ex) {
            success = true;
        }
        assertTrue("Expected ScriptNotFoundException", success);
        
        // Authorised - existent script
        session.setAuthentication(true);
        session.setUser("test");
        repository.putScript("test", "script", "content");
        core.getScript("script");
    }

    /**
     * Test method for {@link org.apache.james.managesieve.core.CoreProcessor#haveSpace(java.lang.String, long)}.
     * @throws QuotaExceededException 
     * @throws AuthenticationRequiredException 
     */
    @Test
    public final void testHaveSpace() throws QuotaExceededException, AuthenticationRequiredException {
        MockSession session = new MockSession();
        SieveRepository repository = new MockSieveRepository();
        CoreProcessor core = new CoreProcessor(session, repository, new MockSieveParser());
        
        // Unauthorised
        boolean success = false;
        session.setAuthentication(false);
        try {
            core.haveSpace("script", Long.MAX_VALUE);
        } catch (AuthenticationRequiredException ex) {
            success = true;
        }
        assertTrue("Expected AuthenticationRequiredException", success);
        
        // Authorised - existent script
        session.setAuthentication(true);
        session.setUser("test");
        core.haveSpace("script", Long.MAX_VALUE);
    }

    /**
     * Test method for {@link org.apache.james.managesieve.core.CoreProcessor#listScripts()}.
     * @throws AuthenticationRequiredException 
     * @throws QuotaExceededException 
     * @throws StorageException 
     * @throws UserNotFoundException 
     */
    @Test
    public final void testListScripts() throws AuthenticationRequiredException, UserNotFoundException, StorageException, QuotaExceededException {
        MockSession session = new MockSession();
        SieveRepository repository = new MockSieveRepository();
        CoreProcessor core = new CoreProcessor(session, repository, new MockSieveParser());

        // Unauthorised
        boolean success = false;
        session.setAuthentication(false);
        try {
            core.listScripts();
        } catch (AuthenticationRequiredException ex) {
            success = true;
        }
        assertTrue("Expected AuthenticationRequiredException", success);

        // Authorised - non-existent script
        success = false;
        session.setAuthentication(true);
        session.setUser("test");
        List<ScriptSummary> summaries = core.listScripts();
        assertTrue(summaries.isEmpty());
        
        // Authorised - existent script
        session.setAuthentication(true);
        session.setUser("test");
        repository.putScript("test", "script", "content");
        summaries = core.listScripts();
        assertEquals(1, summaries.size());
    }

    /**
     * Test method for {@link org.apache.james.managesieve.core.CoreProcessor#putScript(java.lang.String, java.lang.String)}.
     * @throws QuotaExceededException 
     * @throws SyntaxException 
     * @throws AuthenticationRequiredException 
     * @throws ScriptNotFoundException 
     * @throws UserNotFoundException 
     */
    @Test
    public final void testPutScript() throws SyntaxException, QuotaExceededException, AuthenticationRequiredException, UserNotFoundException, ScriptNotFoundException {
        MockSession session = new MockSession();
        SieveRepository repository = new MockSieveRepository();
        CoreProcessor core = new CoreProcessor(session, repository, new MockSieveParser());

        // Unauthorised
        boolean success = false;
        session.setAuthentication(false);
        try {
            core.putScript("script", "content");
        } catch (AuthenticationRequiredException ex) {
            success = true;
        }
        assertTrue("Expected AuthenticationRequiredException", success);

        // Authorised
        success = false;
        session.setAuthentication(true);
        session.setUser("test");
        core.putScript("script", "content");
        assertEquals("content", repository.getScript("test", "script"));
        
        // Syntax
        success = false;
        session.setAuthentication(true);
        session.setUser("test");
        try {
            core.putScript("script", "SyntaxException");
        } catch (SyntaxException ex) {
            success = true;
        }
        assertTrue("Expected SyntaxException", success);
    }

    /**
     * Test method for {@link org.apache.james.managesieve.core.CoreProcessor#renameScript(java.lang.String, java.lang.String)}.
     * @throws DuplicateException 
     * @throws IsActiveException 
     * @throws ScriptNotFoundException 
     * @throws QuotaExceededException 
     * @throws SyntaxException 
     * @throws AuthenticationRequiredException 
     * @throws StorageException 
     * @throws UserNotFoundException 
     */
    @Test
    public final void testRenameScript() throws ScriptNotFoundException, IsActiveException, DuplicateException, AuthenticationRequiredException, SyntaxException, QuotaExceededException, UserNotFoundException, StorageException {
        MockSession session = new MockSession();
        SieveRepository repository = new MockSieveRepository();
        CoreProcessor core = new CoreProcessor(session, repository, new MockSieveParser());

        // Unauthorised
        boolean success = false;
        session.setAuthentication(false);
        try {
            core.renameScript("oldName", "newName");
        } catch (AuthenticationRequiredException ex) {
            success = true;
        }
        assertTrue("Expected AuthenticationRequiredException", success);

        // Authorised
        success = false;
        session.setAuthentication(true);
        session.setUser("test");
        repository.putScript("test", "oldName", "content");
        core.renameScript("oldName", "newName");
        assertEquals("content", repository.getScript("test", "oldName"));
    }

    /**
     * Test method for {@link org.apache.james.managesieve.core.CoreProcessor#setActive(java.lang.String)}.
     * @throws ScriptNotFoundException 
     * @throws AuthenticationRequiredException 
     * @throws QuotaExceededException 
     * @throws StorageException 
     * @throws UserNotFoundException 
     */
    @Test
    public final void testSetActive() throws ScriptNotFoundException, AuthenticationRequiredException, UserNotFoundException, StorageException, QuotaExceededException {
        MockSession session = new MockSession();
        SieveRepository repository = new MockSieveRepository();
        CoreProcessor core = new CoreProcessor(session, repository, new MockSieveParser());

        // Unauthorised
        boolean success = false;
        session.setAuthentication(false);
        try {
            core.setActive("script");
        } catch (AuthenticationRequiredException ex) {
            success = true;
        }
        assertTrue("Expected AuthenticationRequiredException", success);

        // Authorised
        success = false;
        session.setAuthentication(true);
        session.setUser("test");
        repository.putScript("test", "script", "content");
        core.setActive("script");
        assertEquals("content", repository.getActive("test"));
    }
    
    /**
     * testGetActive.
     *
     * @throws ScriptNotFoundException
     * @throws AuthenticationRequiredException
     * @throws UserNotFoundException
     * @throws StorageException
     * @throws QuotaExceededException
     */
    @Test
    public final void testGetActive() throws ScriptNotFoundException,
            AuthenticationRequiredException, UserNotFoundException, StorageException,
            QuotaExceededException {
        MockSession session = new MockSession();
        SieveRepository repository = new MockSieveRepository();
        CoreProcessor core = new CoreProcessor(session, repository, new MockSieveParser());

        // Unauthorised
        boolean success = false;
        session.setAuthentication(false);
        try {
            core.getActive();
        } catch (AuthenticationRequiredException ex) {
            success = true;
        }
        assertTrue("Expected AuthenticationRequiredException", success);

        // Authorised - non-existent script
        success = false;
        session.setAuthentication(true);
        session.setUser("test");
        try {
            core.getActive();
        } catch (ScriptNotFoundException ex) {
            success = true;
        }
        assertTrue("Expected ScriptNotFoundException", success);

        // Authorised - existent script, inactive
        session.setAuthentication(true);
        session.setUser("test");
        repository.putScript("test", "script", "content");
        try {
            core.getActive();
        } catch (ScriptNotFoundException ex) {
            success = true;
        }
        assertTrue("Expected ScriptNotFoundException", success);

        // Authorised - existent script, active
        session.setAuthentication(true);
        session.setUser("test");
        repository.setActive("test", "script");
        core.getActive();
    }

}
