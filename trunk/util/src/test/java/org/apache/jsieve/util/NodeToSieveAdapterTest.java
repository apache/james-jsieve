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

import org.apache.jsieve.NumberArgument;
import org.apache.jsieve.TagArgument;
import org.apache.jsieve.parser.generated.ASTargument;
import org.apache.jsieve.parser.generated.ASTarguments;
import org.apache.jsieve.parser.generated.ASTblock;
import org.apache.jsieve.parser.generated.ASTcommand;
import org.apache.jsieve.parser.generated.ASTcommands;
import org.apache.jsieve.parser.generated.ASTstart;
import org.apache.jsieve.parser.generated.ASTstring;
import org.apache.jsieve.parser.generated.ASTstring_list;
import org.apache.jsieve.parser.generated.ASTtest;
import org.apache.jsieve.parser.generated.ASTtest_list;
import org.apache.jsieve.parser.generated.SieveParserTreeConstants;
import org.apache.jsieve.parser.generated.SimpleNode;
import org.apache.jsieve.parser.generated.Token;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

public class NodeToSieveAdapterTest extends MockObjectTestCase {

    Mock mock;
    NodeToSieveAdapter subject;
    
    protected void setUp() throws Exception {
        super.setUp();
        mock = mock(SieveHandler.class);
        subject = new NodeToSieveAdapter((SieveHandler)mock.proxy());
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void testStart() throws Exception {
        // Not propergated
        subject.start();
    }
    
    public void testEnd() throws Exception {
        // Not propergated
        subject.end();
    }

    public void testEndSimpleNode() throws Exception {
        // Not propergated
        subject.end(new SimpleNode(SieveParserTreeConstants.JJTBLOCK));
    }

    public void testEndASTstart() throws Exception {
        mock.expects(once()).method("endScript");
        subject.end(new ASTstart(SieveParserTreeConstants.JJTSTART));
    }

    public void testEndASTcommands() throws Exception {
        mock.expects(once()).method("endCommands");
        subject.end(new ASTcommands(SieveParserTreeConstants.JJTCOMMANDS));
    }

    public void testEndASTcommand() throws Exception {
        String name = "CommandName";
        mock.expects(once()).method("endCommand").with(eq(name));
        ASTcommand node = new ASTcommand(SieveParserTreeConstants.JJTCOMMAND);
        node.setName(name);
        subject.end(node);
    }

    public void testEndASTblock() throws Exception {
        mock.expects(once()).method("endBlock");
        subject.end(new ASTblock(SieveParserTreeConstants.JJTBLOCK)); 
    }

    public void testEndASTarguments() throws Exception {
        mock.expects(once()).method("endArguments");
        subject.end(new ASTarguments(SieveParserTreeConstants.JJTARGUMENTS)); 
    }

    public void testASTargumentTag() throws Exception {
        String tag = "Hugo";
        mock.expects(once()).method("argument").with(eq(tag));
        ASTargument argument = new ASTargument(SieveParserTreeConstants.JJTARGUMENTS);
        argument.setValue(new TagArgument(new Token(0, tag)));
        subject.start(argument); 
        subject.end(argument); 
    }
    
    public void testASTargumentNumber() throws Exception {
        int number = 17;
        mock.expects(once()).method("argument").with(eq(number));
        ASTargument argument = new ASTargument(SieveParserTreeConstants.JJTARGUMENTS);
        argument.setValue(new NumberArgument(new Token(0, "17")));
        subject.start(argument); 
        subject.end(argument); 
    }

    public void testEndASTtest() throws Exception {
        String name = "ATestName";
        mock.expects(once()).method("endTest").with(eq(name));
        ASTtest node = new ASTtest(SieveParserTreeConstants.JJTTEST);
        node.setName(name);
        subject.end(node);
    }

    public void testEndASTtest_list() throws Exception {
        mock.expects(once()).method("endTestList");
        subject.end(new ASTtest_list(SieveParserTreeConstants.JJTTEST_LIST)); 
    }

    public void testEndASTstring_list() throws Exception {
        mock.expects(once()).method("endStringListArgument");
        subject.end(new ASTstring_list(SieveParserTreeConstants.JJTSTRING_LIST)); 
    }

    public void testStartSimpleNode() throws Exception {
        // Not propergated
        subject.end(new SimpleNode(SieveParserTreeConstants.JJTBLOCK));
    }

    public void testStartASTstart() throws Exception {
        mock.expects(once()).method("startScript");
        subject.start(new ASTstart(SieveParserTreeConstants.JJTSTART));
    }

    public void testStartASTcommands() throws Exception {
        mock.expects(once()).method("startCommands");
        subject.start(new ASTcommands(SieveParserTreeConstants.JJTCOMMANDS));
    }

    public void testStartASTcommand() throws Exception {
        String name = "CommandName";
        mock.expects(once()).method("startCommand").with(eq(name));
        ASTcommand node = new ASTcommand(SieveParserTreeConstants.JJTCOMMAND);
        node.setName(name);
        subject.start(node);
    }

    public void testStartASTblock() throws Exception {
        mock.expects(once()).method("startBlock");
        subject.start(new ASTblock(SieveParserTreeConstants.JJTBLOCK)); 
    }

    public void testStartASTarguments() throws Exception {
        mock.expects(once()).method("startArguments");
        subject.start(new ASTarguments(SieveParserTreeConstants.JJTARGUMENTS)); 
    }

    public void testStartASTtest() throws Exception {
        String name = "ATestName";
        mock.expects(once()).method("startTest").with(eq(name));
        ASTtest node = new ASTtest(SieveParserTreeConstants.JJTTEST);
        node.setName(name);
        subject.start(node);
    }

    public void testStartASTtest_list() throws Exception {
        mock.expects(once()).method("startTestList");
        subject.start(new ASTtest_list(SieveParserTreeConstants.JJTTEST_LIST));
    }

    public void testASTstring() throws Exception {
        String string = "A Value";
        mock.expects(once()).method("listMember").with(eq(string));
        ASTstring node = new ASTstring(SieveParserTreeConstants.JJTSTRING);
        node.setValue(string);
        subject.start(node); 
        subject.end(node); 
    }

    public void testStartASTstring_list() throws Exception {
        mock.expects(once()).method("startStringListArgument");
        subject.start(new ASTstring_list(SieveParserTreeConstants.JJTSTRING_LIST)); 
    }

}
