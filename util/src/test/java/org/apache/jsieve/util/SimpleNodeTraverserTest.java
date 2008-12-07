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
package org.apache.jsieve.util;

import java.io.ByteArrayInputStream;

import org.apache.jsieve.ConfigurationManager;
import org.apache.jsieve.parser.generated.ASTarguments;
import org.apache.jsieve.parser.generated.ASTcommand;
import org.apache.jsieve.parser.generated.ASTcommands;
import org.apache.jsieve.parser.generated.ASTstart;
import org.apache.jsieve.parser.generated.Node;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

public class SimpleNodeTraverserTest extends MockObjectTestCase {

    Mock mock;
    NodeHandler handler;
    
    NodeTraverser subject;
    
    protected void setUp() throws Exception {
        super.setUp();
        mock = mock(NodeHandler.class);
        handler = (NodeHandler) mock.proxy();
        
        subject = new NodeTraverser();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void testTraverseSimpleScript() throws Exception {
        mock.expects(once()).method("start").id("1");
        mock.expects(once()).method("start").with(isA(ASTstart.class)).after("1").id("2");
        mock.expects(once()).method("start").with(isA(ASTcommands.class)).after("2").id("3");
        mock.expects(once()).method("start").with(isA(ASTcommand.class)).after("3").id("4");
        mock.expects(once()).method("start").with(isA(ASTarguments.class)).after("4").id("5");
        mock.expects(once()).method("end").with(isA(ASTarguments.class)).after("5").id("6");
        mock.expects(once()).method("end").with(isA(ASTcommand.class)).after("6").id("7");
        mock.expects(once()).method("end").with(isA(ASTcommands.class)).after("7").id("8");
        mock.expects(once()).method("end").with(isA(ASTstart.class)).after("8").id("9");
        mock.expects(once()).method("end").after("9");
        traverse("Keep;");
    }
    
    private void traverse(String script) throws Exception {
        subject.traverse(handler, parse(script));
    }

    private Node parse(String script) throws Exception {
        return new ConfigurationManager().build().parse(
                new ByteArrayInputStream(script.getBytes()));
    }
}
