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

package org.apache.james.managesieve.mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.james.managesieve.api.DuplicateException;
import org.apache.james.managesieve.api.DuplicateUserException;
import org.apache.james.managesieve.api.IsActiveException;
import org.apache.james.managesieve.api.QuotaExceededException;
import org.apache.james.managesieve.api.QuotaNotFoundException;
import org.apache.james.managesieve.api.ScriptNotFoundException;
import org.apache.james.managesieve.api.ScriptSummary;
import org.apache.james.managesieve.api.SieveRepository;
import org.apache.james.managesieve.api.StorageException;
import org.apache.james.managesieve.api.UserNotFoundException;

/**
 * <code>MockSieveRepository</code>
 */
public class MockSieveRepository implements SieveRepository {
    
    public class SieveScript
    {
        private String _name = null;
        private String _content = null;
        private boolean _isActive = false;
     
        /**
         * Creates a new instance of SieveScript.
         *
         */
        private SieveScript() {
            super();
        }
        
        /**
         * Creates a new instance of SieveScript.
         *
         */
        public SieveScript(String content, boolean isActive) {
            this();
            setContent(content);
            setActive(isActive);
        }
        
        /**
         * @return the name
         */
        public String getName() {
            return _name;
        }
        
        /**
         * @param name the name to set
         */
        public void setName(String name) {
            _name = name;
        }
        
        /**
         * @return the content
         */
        public String getContent() {
            return _content;
        }
        
        /**
         * @param content the content to set
         */
        public void setContent(String content) {
            _content = content;
        }
        
        /**
         * @return the isActive
         */
        public boolean isActive() {
            return _isActive;
        }
        
        /**
         * @param isActive the isActive to set
         */
        public void setActive(boolean isActive) {
            _isActive = isActive;
        }
    }
    
    Map<String,Map<String, SieveScript>> _repository = null;

    /**
     * Creates a new instance of MockSieveRepository.
     *
     */
    public MockSieveRepository() {
        _repository = new HashMap<String,Map<String, SieveScript>>();
    }

    /**
     * @see org.apache.james.managesieve.api.SieveRepository#addUser(java.lang.String)
     */
    public void addUser(String user) throws DuplicateUserException, StorageException {
        if (_repository.containsKey(user))
        {
            throw new DuplicateUserException(user);
        }
        _repository.put(user, new HashMap<String, SieveScript>());               
    }

    /**
     * @see org.apache.james.managesieve.api.SieveRepository#deleteScript(java.lang.String, java.lang.String)
     */
    public void deleteScript(String user, String name) throws UserNotFoundException,
            ScriptNotFoundException, IsActiveException, StorageException {
        if (!_repository.containsKey(user))
        {
            throw new UserNotFoundException(user);
        }
        SieveScript script = _repository.get(user).get(name);
        if (null == script)
        {
            throw new ScriptNotFoundException(name);
        }
        if (script.isActive())
        {
            throw new IsActiveException(name);
        }
        _repository.get(user).remove(name);
    }

    /**
     * @see org.apache.james.managesieve.api.SieveRepository#getActive(java.lang.String)
     */
    public String getActive(String user) throws UserNotFoundException, ScriptNotFoundException {
        if (!_repository.containsKey(user))
        {
            throw new UserNotFoundException(user);
        }
        Set<Entry<String, SieveScript>> scripts = _repository.get(user).entrySet();
        String content = null;
        for (final Entry<String, SieveScript> entry : scripts)
        {
            if (entry.getValue().isActive())
            {
                content = entry.getValue().getContent();
                break;
            }
        }
        if (null == content)
        {
            throw new ScriptNotFoundException();
        }
        return content;
    }

    /**
     * @see org.apache.james.managesieve.api.SieveRepository#getQuota()
     */
    public long getQuota() throws QuotaNotFoundException {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * @see org.apache.james.managesieve.api.SieveRepository#getQuota(java.lang.String)
     */
    public long getQuota(String user) throws UserNotFoundException, QuotaNotFoundException {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * @see org.apache.james.managesieve.api.SieveRepository#getScript(java.lang.String, java.lang.String)
     */
    public String getScript(String user, String name) throws UserNotFoundException,
            ScriptNotFoundException {
        if (!_repository.containsKey(user))
        {
            throw new UserNotFoundException(user);
        }
        SieveScript script = _repository.get(user).get(name);
        if (null == script)
        {
            throw new ScriptNotFoundException(name);
        }
        return script.getContent();
    }

    /**
     * @see org.apache.james.managesieve.api.SieveRepository#hasQuota()
     */
    public boolean hasQuota() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.apache.james.managesieve.api.SieveRepository#hasQuota(java.lang.String)
     */
    public boolean hasQuota(String user) throws UserNotFoundException {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.apache.james.managesieve.api.SieveRepository#hasUser(java.lang.String)
     */
    public boolean hasUser(String user) {
        return _repository.containsKey(user);
    }

    /**
     * @see org.apache.james.managesieve.api.SieveRepository#haveSpace(java.lang.String, java.lang.String, long)
     */
    public void haveSpace(String user, String name, long size) throws UserNotFoundException,
            QuotaExceededException {
        if (!_repository.containsKey(user))
        {
            throw new UserNotFoundException(user);
        }
    }

    /**
     * @see org.apache.james.managesieve.api.SieveRepository#listScripts(java.lang.String)
     */
    public List<ScriptSummary> listScripts(String user) throws UserNotFoundException {
        if (!_repository.containsKey(user))
        {
            throw new UserNotFoundException(user);
        }
        Set<Entry<String, SieveScript>> scripts = _repository.get(user).entrySet();
        List<ScriptSummary> summaries = new ArrayList<ScriptSummary>(scripts.size());
        for (final Entry<String, SieveScript> entry : scripts)
        {
            summaries.add(new ScriptSummary(){

                public String getName() {
                    return entry.getKey();
                }

                public boolean isActive() {
                    return entry.getValue().isActive();
                }});
        }
        return summaries;
    }

    /**
     * @see org.apache.james.managesieve.api.SieveRepository#putScript(java.lang.String, java.lang.String, java.lang.String)
     */
    public void putScript(String user, String name, String content) throws UserNotFoundException,
            StorageException, QuotaExceededException {
        if (!_repository.containsKey(user))
        {
            throw new UserNotFoundException(user);
        }
        Map<String,SieveScript> scripts = _repository.get(user);
        scripts.put(name, new SieveScript(content, false));
    }

    /**
     * @see org.apache.james.managesieve.api.SieveRepository#removeQuota()
     */
    public void removeQuota() throws QuotaNotFoundException, StorageException {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.apache.james.managesieve.api.SieveRepository#removeQuota(java.lang.String)
     */
    public void removeQuota(String user) throws UserNotFoundException, QuotaNotFoundException,
            StorageException {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.apache.james.managesieve.api.SieveRepository#removeUser(java.lang.String)
     */
    public void removeUser(String user) throws UserNotFoundException, StorageException {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.apache.james.managesieve.api.SieveRepository#renameScript(java.lang.String, java.lang.String, java.lang.String)
     */
    public void renameScript(String user, String oldName, String newName)
            throws UserNotFoundException, ScriptNotFoundException,
            DuplicateException, StorageException {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.apache.james.managesieve.api.SieveRepository#setActive(java.lang.String, java.lang.String)
     */
    public void setActive(String user, String name) throws UserNotFoundException,
            ScriptNotFoundException, StorageException {

        // Turn off currently active script, if any
        Entry<String, SieveScript> oldActive = null;
        oldActive = getActiveEntry(user);
        if (null != oldActive) {
            oldActive.getValue().setActive(false);
        }

        // Turn on the new active script if not an empty name
        if ((null != name) && (!name.trim().isEmpty())) {
            if (_repository.get(user).containsKey(name)) {
                _repository.get(user).get(name).setActive(true);
            } else {
                if (null != oldActive) {
                    oldActive.getValue().setActive(true);
                }
                throw new ScriptNotFoundException();
            }
        }
    }
    
    protected Entry<String, SieveScript> getActiveEntry(String user)
    {
        Set<Entry<String, SieveScript>> scripts = _repository.get(user).entrySet();
        Entry<String, SieveScript> activeEntry = null;
        for (final Entry<String, SieveScript> entry : scripts)
        {
            if (entry.getValue().isActive())
            {
                activeEntry = entry;
                break;
            }
        }
        return activeEntry;
    }

    /**
     * @see org.apache.james.managesieve.api.SieveRepository#setQuota(long)
     */
    public void setQuota(long quota) throws StorageException {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.apache.james.managesieve.api.SieveRepository#setQuota(java.lang.String, long)
     */
    public void setQuota(String user, long quota) throws UserNotFoundException, StorageException {
        // TODO Auto-generated method stub

    }

}
