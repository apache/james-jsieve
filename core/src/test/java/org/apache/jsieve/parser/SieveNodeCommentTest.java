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
package org.apache.jsieve.parser;

import java.util.List;

import junit.framework.TestCase;

import org.apache.jsieve.utils.JUnitUtils;

public class SieveNodeCommentTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGetNoCommentsBefore() throws Exception {
        SieveNode node = (SieveNode) JUnitUtils.parse("if address :contains [\"To\", \"From\"] \"Fish!\"{ }");
        List comments = node.getPrecedingComments();
        assertNotNull(comments);
        assertEquals(0, comments.size());
    }
    
    public void testGetBracketCommentsBefore() throws Exception {
        SieveNode node = (SieveNode) JUnitUtils.parse("/* A Comment *//* Another comment */if address :contains [\"To\", \"From\"] \"Fish!\"{ }");
        List comments = node.getPrecedingComments();
        assertNotNull(comments);
        assertEquals(2, comments.size());
        assertEquals(" A Comment ", comments.get(0));
        assertEquals(" Another comment ", comments.get(1));
    }
    
    public void testGetHashCommentsBefore() throws Exception {
        SieveNode node = (SieveNode) JUnitUtils.parse("/* A Comment */#A Line Comment\nif address :contains [\"To\", \"From\"] \"Fish!\"{ }");
        List comments = node.getPrecedingComments();
        assertNotNull(comments);
        assertEquals(2, comments.size());
        assertEquals(" A Comment ", comments.get(0));
        assertEquals("A Line Comment", comments.get(1));
    }
    
    public void testGetHashCommentsBeforeCRLF() throws Exception {
        SieveNode node = (SieveNode) JUnitUtils.parse("/* A Comment */#A Line Comment\r\nif address :contains [\"To\", \"From\"] \"Fish!\"{ }");
        List comments = node.getPrecedingComments();
        assertNotNull(comments);
        assertEquals(2, comments.size());
        assertEquals(" A Comment ", comments.get(0));
        assertEquals("A Line Comment", comments.get(1));
    }
    

    public void testGetLastCommentNoneBefore() throws Exception {
        SieveNode node = (SieveNode) JUnitUtils.parse("if address :contains [\"To\", \"From\"] \"Fish!\"{ }");
        assertNull(node.getLastComment());
    }
    
    public void testGetBracketLastCommentBefore() throws Exception {
        SieveNode node = (SieveNode) JUnitUtils.parse("/* A Comment *//* Another comment */if address :contains [\"To\", \"From\"] \"Fish!\"{ }");
        assertEquals(" Another comment ", node.getLastComment());
    }
    
    public void testGetHashLastCommentBefore() throws Exception {
        SieveNode node = (SieveNode) JUnitUtils.parse("/* A Comment */#A Line Comment\nif address :contains [\"To\", \"From\"] \"Fish!\"{ }");
        assertEquals("A Line Comment", node.getLastComment());
    }
    
    public void testGetHashLastCommentBeforeCRLF() throws Exception {
        SieveNode node = (SieveNode) JUnitUtils.parse("/* A Comment */#A Line Comment\r\nif address :contains [\"To\", \"From\"] \"Fish!\"{ }");;
        assertEquals("A Line Comment", node.getLastComment());
    }
}
