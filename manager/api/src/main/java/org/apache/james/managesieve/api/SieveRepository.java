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

package org.apache.james.managesieve.api;

import java.util.List;



/**
 * <code>SieveRepository</code>
 */
public interface SieveRepository {
    
    abstract public void haveSpace(String user, String name, long size) throws UserNotFoundException, QuotaExceededException;
    
    /**
     * PutScript.
     *
     * <p><strong>Note:</strong> It is the responsibility of the caller to validate the script to be put.
     *
     * @param user
     * @param name
     * @param content
     * @throws UserNotFoundException
     * @throws StorageException
     * @throws QuotaExceededException
     */
    abstract public void putScript(String user, String name, String content) throws UserNotFoundException, StorageException, QuotaExceededException;
    
    abstract public List<ScriptSummary> listScripts(String user) throws UserNotFoundException;
    
    abstract public String getActive(String user) throws UserNotFoundException, ScriptNotFoundException;
    
    abstract public void setActive(String user, String name) throws UserNotFoundException, ScriptNotFoundException, StorageException;
    
    abstract public String getScript(String user, String name) throws UserNotFoundException, ScriptNotFoundException;
    
    abstract public void deleteScript(String user, String name) throws UserNotFoundException, ScriptNotFoundException, IsActiveException, StorageException;
    
    abstract public void renameScript(String user, String oldName, String newName) throws UserNotFoundException, ScriptNotFoundException, DuplicateException, StorageException;
    
    abstract public boolean hasUser(String user);
    
    abstract public void addUser(String user) throws DuplicateUserException, StorageException;
    
    abstract public void removeUser(String user) throws UserNotFoundException, StorageException;

    abstract public boolean hasQuota();
    
    abstract public long getQuota() throws QuotaNotFoundException;
    
    abstract public void setQuota(long quota) throws StorageException;
    
    abstract public void removeQuota() throws QuotaNotFoundException, StorageException;
    
    abstract public boolean hasQuota(String user) throws UserNotFoundException;
    
    abstract public long getQuota(String user) throws UserNotFoundException, QuotaNotFoundException;
    
    abstract public void setQuota(String user, long quota) throws UserNotFoundException, StorageException;
    
    abstract public void removeQuota(String user) throws UserNotFoundException, QuotaNotFoundException, StorageException;

}
