/***********************************************************************
 * Copyright (c) 2003-2004 The Apache Software Foundation.             *
 * All rights reserved.                                                *
 * ------------------------------------------------------------------- *
 * Licensed under the Apache License, Version 2.0 (the "License"); you *
 * may not use this file except in compliance with the License. You    *
 * may obtain a copy of the License at:                                *
 *                                                                     *
 *     http://www.apache.org/licenses/LICENSE-2.0                      *
 *                                                                     *
 * Unless required by applicable law or agreed to in writing, software *
 * distributed under the License is distributed on an "AS IS" BASIS,   *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or     *
 * implied.  See the License for the specific language governing       *
 * permissions and limitations under the License.                      *
 ***********************************************************************/

package org.apache.jsieve.mail;

import java.util.List;
import java.util.ListIterator;

import org.apache.jsieve.SieveException;

/**
 * <p>Interface <code>MailAdapter</code> defines the minimum functionality required of
 * of a class implementing a mail message. This is the functionality neccesary to
 * implement the Commands and Tests that RFC32028 mandates MUST be implemented.
 * </p>
 * 
 * <p>Typically, implementations will wrap an application's pre-existing mail 
 * message implementation. It is expected that implementations will extend the
 * minimum level of functionality to provide support for Command and Test 
 * extensions that exploit the capabilities of a particular application.</p>
 */
public interface MailAdapter
{
    /**
     * Method getActions answers the List of Actions accumulated by the receiver.
     * @return Action
     */
    public List getActions();
    
    /**
     * Method getActionIteraror answers an Iterator over the List of Actions
     * accumulated by the receiver.
     * @return Action
     */
    public ListIterator getActionsIterator();
    
    /**
     * Method getHeader answers a List of all of the headers in the receiver whose 
     * name is equal to the passed name. If no headers are found an empty List is 
     * returned.
     * 
     * @param name
     * @return List
     * @throws SieveMailException
     */
    public List getHeader(String name) throws SieveMailException;
    
    /**
     * <p>Method getMatchingHeader answers a List of all of the headers in the 
     * receiver with the passed name. If no headers are found an empty List is 
     * returned.
     * </p>
     * 
     * <p>This method differs from getHeader(String) in that it ignores case and the 
     * whitespace prefixes and suffixes of a header name when performing the
     * match, as required by RFC 3028. Thus "From", "from ", " From" and " from "
     * are considered equal.
     * </p>
     * 
     * @param name
     * @return List
     * @throws SieveMailException
     */
    public List getMatchingHeader(String name)
        throws SieveMailException;
        
    /**
     * Method getHeaderNames answers a List of all of the headers in the receiver.
     * No duplicates are allowed.
     * @return List
     * @throws SieveMailException
     */
    public List getHeaderNames() throws SieveMailException;       
    
    /**
     * Method addAction adds an Action to the List of Actions to be performed by the
     * receiver.
     * @param action
     */
    public void addAction(Action action); 
    
    /**
     * Method executeActions. Applies the Actions accumulated by the receiver.
     */
    public void executeActions() throws SieveException;            



    /**
     * Method getSize answers the receiver's message size in octets.
     * @return int
     * @throws SieveMailException
     */
    int getSize() throws SieveMailException;

}
