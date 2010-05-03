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
package org.apache.jsieve.commands.optional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.logging.LogFactory;
import org.apache.jsieve.Argument;
import org.apache.jsieve.Arguments;
import org.apache.jsieve.BaseSieveContext;
import org.apache.jsieve.ConfigurationManager;
import org.apache.jsieve.ScriptCoordinate;
import org.apache.jsieve.SieveContext;
import org.apache.jsieve.StringListArgument;
import org.apache.jsieve.TestList;
import org.apache.jsieve.mail.ActionFileInto;
import org.apache.jsieve.util.check.ScriptCheckMailAdapter;

public class FileIntoTest extends TestCase {

    FileInto subject;
    
    ScriptCheckMailAdapter mockAdapter;
    Arguments dummyArguments;
    SieveContext dummyContext;
    
    @SuppressWarnings("unchecked")
    protected void setUp() throws Exception {
        super.setUp();
        mockAdapter = new ScriptCheckMailAdapter();
        List<String> stringList = new ArrayList<String>();
        stringList.add("Whatever");
        List<Argument> argumentList = new ArrayList<Argument>();
        argumentList.add(new StringListArgument(stringList));
        dummyArguments = new Arguments(argumentList, new TestList(Collections.EMPTY_LIST));
        ConfigurationManager configurationManager = new ConfigurationManager();
        dummyContext = new BaseSieveContext(
                configurationManager.getCommandManager(), configurationManager
                        .getComparatorManager(), configurationManager
                        .getTestManager(), LogFactory
                        .getLog(this.getClass()));
        dummyContext.setCoordinate(new ScriptCoordinate(0, 0, 0, 0));
        subject = new FileInto();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testFileIntoShouldNotAllowMultipleFileIntoActions() throws Exception {
        subject.execute(mockAdapter, dummyArguments, null, dummyContext);
        assertEquals(1, mockAdapter.getActions().size());
        assertTrue(mockAdapter.getActions().get(0) instanceof ActionFileInto);
        
        subject.execute(mockAdapter, dummyArguments, null, dummyContext);
        assertEquals(1, mockAdapter.getActions().size());
        assertTrue(mockAdapter.getActions().get(0) instanceof ActionFileInto);
    }
}
