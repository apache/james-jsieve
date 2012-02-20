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

import org.apache.commons.logging.LogFactory;
import org.apache.jsieve.parser.generated.ASTstring;
import org.apache.jsieve.parser.generated.Node;
import org.apache.jsieve.utils.JUnitUtils;

public class SieveParserVisitorImplQuoteTest extends TestCase {

    
    SieveParserVisitorImpl visitor;

    List data;

    ASTstring node;

    protected void setUp() throws Exception {
        super.setUp();
        final ConfigurationManager configurationManager = new ConfigurationManager();
        visitor = new SieveParserVisitorImpl(new BaseSieveContext(
                configurationManager.getCommandManager(), configurationManager
                        .getComparatorManager(), configurationManager
                        .getTestManager(), LogFactory
                        .getLog(SieveParserVisitorImplQuoteTest.class)));
        data = new ArrayList();

    }

    private ASTstring stringNode(String value) throws Exception {
        Node node = JUnitUtils.parse("fileinto " + value + ";");
        return (ASTstring) node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).jjtGetChild(0);
    }
    
    public void testVisitASTstringObjectQuoted() throws Exception {
        node = stringNode("\"value\"");
        visitor.visit(node, data);
        assertEquals("Data value added after quotes stripped", 1, data.size());
        assertEquals("Data value added after quotes stripped", "value", data
                .get(0));
    }

    public void testVisitASTstringObjectQuoteInQuoted() throws Exception {
        
        node = stringNode("\"val\\\"ue\"");
        visitor.visit(node, data);
        assertEquals("Data value added after quotes stripped", 1, data.size());
        assertEquals("Data value added after quotes stripped", "val\"ue", data
                .get(0));
    }

    public void testVisitASTstringObjectDoubleSlashQuoted() throws Exception {

        node = stringNode("\"val\\\\ue\"");
        visitor.visit(node, data);
        assertEquals("Data value added after quotes stripped", 1, data.size());
        assertEquals("Data value added after quotes stripped", "val\\ue", data
                .get(0));
    }

    public void testVisitASTstringObjectSlashQuoted() throws Exception {

        node = stringNode("\"value\"");
        visitor.visit(node, data);
        assertEquals("Data value added after quotes stripped", 1, data.size());
        assertEquals("Data value added after quotes stripped", "value", data
                .get(0));
    }

    public void testVisitASTstringEmptyQuoted() throws Exception {

        node = stringNode("\"\"");
        visitor.visit(node, data);
        assertEquals("Data value added after quotes stripped", 1, data.size());
        assertEquals("Data value added after quotes stripped", "", data.get(0));
    }

    public void testVisitASTstringObjectMultiSlashQuoted() throws Exception {

        node = stringNode("\"v\\\\al\\\\ue\\\\\"");
        visitor.visit(node, data);
        assertEquals("Data value added after quotes stripped", 1, data.size());
        assertEquals("Data value added after quotes stripped", "v\\al\\ue\\",
                data.get(0));
    }
}
