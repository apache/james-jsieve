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

package org.apache.jsieve;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.jsieve.parser.generated.ASTstring;

public class SieveParserVisitorImplQuoteTest extends TestCase {

    SieveParserVisitorImpl visitor;
    List data;
    ASTstring node;
    
    protected void setUp() throws Exception {
        super.setUp();
        visitor = new SieveParserVisitorImpl(new BaseSieveContext(CommandManager.getInstance()));
        data = new ArrayList();
        node = new ASTstring(100);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testVisitASTstringObjectQuoted() {

        node.setValue("\"value\"");
        visitor.visit(node, data);
        assertEquals("Data value added after quotes stripped", 1, data.size());
        assertEquals("Data value added after quotes stripped", "value", data.get(0));
    }

    public void testVisitASTstringObjectQuoteInQuoted() {

        node.setValue("\"val\\\"ue\"");
        visitor.visit(node, data);
        assertEquals("Data value added after quotes stripped", 1, data.size());
        assertEquals("Data value added after quotes stripped", "val\"ue", data.get(0));
    }
    
    public void testVisitASTstringObjectDoubleSlashQuoted() {

        node.setValue("\"val\\\\ue\"");
        visitor.visit(node, data);
        assertEquals("Data value added after quotes stripped", 1, data.size());
        assertEquals("Data value added after quotes stripped", "val\\ue", data.get(0));
    }
    
    
    public void testVisitASTstringObjectSlashQuoted() {

        node.setValue("\"val\\ue\"");
        visitor.visit(node, data);
        assertEquals("Data value added after quotes stripped", 1, data.size());
        assertEquals("Data value added after quotes stripped", "value", data.get(0));
    }
    
    public void testVisitASTstringEmptyQuoted() {

        node.setValue("\"\"");
        visitor.visit(node, data);
        assertEquals("Data value added after quotes stripped", 1, data.size());
        assertEquals("Data value added after quotes stripped", "", data.get(0));
    }
    
    public void testVisitASTstringObjectMultiSlashQuoted() {

        node.setValue("\"v\\\\al\\\\u\\e\\\\\"");
        visitor.visit(node, data);
        assertEquals("Data value added after quotes stripped", 1, data.size());
        assertEquals("Data value added after quotes stripped", "v\\al\\ue\\", data.get(0));
    }
}
