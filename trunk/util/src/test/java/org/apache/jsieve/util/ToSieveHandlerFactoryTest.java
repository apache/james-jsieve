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

import java.io.StringWriter;

import junit.framework.TestCase;

public class ToSieveHandlerFactoryTest extends TestCase {

    ToSieveHandlerFactory factory;
    StringWriter monitor;
    
    protected void setUp() throws Exception {
        super.setUp();
        factory = new ToSieveHandlerFactory();
        monitor = new StringWriter();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testDefaultConfigurationShouldBuildNotNullHandler() throws Exception {
        assertNotNull(factory.build(monitor));
    }
    
    public void testStartScriptShouldBeIgnored() throws Exception {
        // Setup
        SieveHandler handler = factory.build(monitor);
        
        // Exercise
        handler.startScript();
        
        // Verify
        assertEquals("", monitor.toString());
    }
    
    public void testEndScriptShouldBeIgnored() throws Exception {
        // Setup
        SieveHandler handler = factory.build(monitor);
        
        // Exercise
        handler.endScript();
        
        // Verify
        assertEquals("", monitor.toString());
    }
    
    public void testStartBlockShouldOpenBracket() throws Exception {
        // Setup
        SieveHandler handler = factory.build(monitor);
        
        // Exercise
        handler.startBlock();
        
        // Verify
        assertEquals(" {", monitor.toString());
    }
    
    public void testEndBlockShouldCloseBracket() throws Exception {
        // Setup
        SieveHandler handler = factory.build(monitor);
        
        // Exercise
        handler.endBlock();
        
        // Verify
        assertEquals("}", monitor.toString());
    }
    
    public void testStartCommandsShouldBeIgnored() throws Exception {
        // Setup
        SieveHandler handler = factory.build(monitor);
        
        // Exercise
        handler.startCommands();
        
        // Verify
        assertEquals("", monitor.toString());
    }
    
    public void testEndCommandsShouldBeIgnored() throws Exception {
        // Setup
        SieveHandler handler = factory.build(monitor);
        
        // Exercise
        handler.endCommands();
        
        // Verify
        assertEquals("", monitor.toString());
    }
    
    public void testStartCommandShouldPrintIdentifier() throws Exception {
        // Setup
        SieveHandler handler = factory.build(monitor);
        String commandName = "SomeCommand";
        
        // Exercise
        handler.startCommand(commandName);
        
        // Verify
        assertEquals("SomeCommand", monitor.toString());
    }
    
    public void testEndCommandShouldPrintColon() throws Exception {
        // Setup
        SieveHandler handler = factory.build(monitor);
        String commandName = "SomeCommand";
        
        // Exercise
        handler.endCommand(commandName);
        
        // Verify
        assertEquals(";", monitor.toString());
    }
    
    public void testStartArgumentsShouldBeIgnored() throws Exception {
        // Setup
        SieveHandler handler = factory.build(monitor);
        
        // Exercise
        handler.startArguments();
        
        // Verify
        assertEquals("", monitor.toString());
    }
    
    public void testEndArgumentsShouldBeIgnored() throws Exception {
        // Setup
        SieveHandler handler = factory.build(monitor);
        
        // Exercise
        handler.endArguments();
        
        // Verify
        assertEquals("", monitor.toString());
    }
    
    public void testArgumentShouldPrintTag() throws Exception {
        // Setup
        SieveHandler handler = factory.build(monitor);
        String identifier = "AnIdentifier";
        
        // Exercise
        handler.argument(identifier);
        
        // Verify
        assertEquals(" :" + identifier, monitor.toString());
    }
    
    public void testArgumentShouldPrintNumber() throws Exception {
        // Setup
        SieveHandler handler = factory.build(monitor);
        int number = 99;
        
        // Exercise
        handler.argument(number);
        
        // Verify
        assertEquals(" " + Integer.toString(number), monitor.toString());
    }
    
    public void testStartStringListShouldOpenBracket() throws Exception {
        // Setup
        SieveHandler handler = factory.build(monitor);
        
        // Exercise
        handler.startStringListArgument();
        
        // Verify
        assertEquals(" [", monitor.toString());
    }
    
    public void testEndStringListShouldCloseBracket() throws Exception {
        // Setup
        SieveHandler handler = factory.build(monitor);
        
        // Exercise
        handler.endStringListArgument();
        
        // Verify
        assertEquals("]", monitor.toString());
    }
    
    public void testListMemberShouldQuoteString() throws Exception {
        // Setup
        SieveHandler handler = factory.build(monitor);
        String member = "A List Member";
        
        // Exercise
        handler.listMember(member);
        
        // Verify
        assertEquals('"' + member + '"', monitor.toString());
    }
    
    public void testListMemberShouldEscapeDoubleQuote() throws Exception {
        // Setup
        SieveHandler handler = factory.build(monitor);
        String prefix = "A Prefix";
        String suffix = "A Suffix";
        
        // Exercise
        handler.listMember(prefix + '"' + suffix);
        
        // Verify
        assertEquals('"' + prefix + "\\\"" + suffix + '"', monitor.toString());
    }
    
    public void testListMemberShouldEscapeBackSlash() throws Exception {
        // Setup
        SieveHandler handler = factory.build(monitor);
        String prefix = "A Prefix";
        String suffix = "A Suffix";
        
        // Exercise
        handler.listMember(prefix + '\\' + suffix);
        
        // Verify
        assertEquals('"' + prefix + "\\\\" + suffix + '"', monitor.toString());
    }
    
    public void testListMemberShouldEscapeCR() throws Exception {
        // Setup
        SieveHandler handler = factory.build(monitor);
        String prefix = "A Prefix";
        String suffix = "A Suffix";
        
        // Exercise
        handler.listMember(prefix + '\r' + suffix);
        
        // Verify
        assertEquals('"' + prefix + "\\\r" + suffix + '"', monitor.toString());
    }
    
    public void testListMemberShouldEscapeLF() throws Exception {
        // Setup
        SieveHandler handler = factory.build(monitor);
        String prefix = "A Prefix";
        String suffix = "A Suffix";
        
        // Exercise
        handler.listMember(prefix + '\f' + suffix);
        
        // Verify
        assertEquals('"' + prefix + "\\\f" + suffix + '"', monitor.toString());
    }
    
    public void testStartTestListShouldOpenBracket() throws Exception {
        // Setup
        SieveHandler handler = factory.build(monitor);
        
        // Exercise
        handler.startTestList();
        
        // Verify
        assertEquals("(", monitor.toString());
    }
    
    public void testEndTestListShouldCloseBracket() throws Exception {
        // Setup
        SieveHandler handler = factory.build(monitor);
        
        // Exercise
        handler.endTestList();
        
        // Verify
        assertEquals(")", monitor.toString());
    }
    
    public void testStartTestShouldPrintIdentifier() throws Exception {
        // Setup
        SieveHandler handler = factory.build(monitor);
        String identifier = "AnIdentifier";
        
        // Exercise
        handler.startTest(identifier);
        
        // Verify
        assertEquals(" " + identifier, monitor.toString());
    }
    
    public void testStartSecondTestShouldPrefixComma() throws Exception {
        // Setup
        SieveHandler handler = factory.build(monitor);
        String firstIdentifier = "FirstIdentifier";
        String secondIdentifier = "SecondIdentifier";
        
        // Exercise
        handler.startTest(firstIdentifier);
        handler.endTest(firstIdentifier);
        handler.startTest(secondIdentifier);
        
        // Verify
        assertEquals(" " +firstIdentifier + ", " + secondIdentifier, monitor.toString());
    }
    
    public void testAfterEndTestListShouldNotNextPrefixTestWithComma() throws Exception {
        // Setup
        SieveHandler handler = factory.build(monitor);
        String firstIdentifier = "FirstIdentifier";
        String secondIdentifier = "SecondIdentifier";
        
        // Exercise
        handler.startTest(firstIdentifier);
        handler.endTest(firstIdentifier);
        handler.endTestList();
        handler.startTestList();
        handler.startTest(secondIdentifier);
        
        // Verify
        assertEquals(" " + firstIdentifier + ")(" + secondIdentifier, monitor.toString());
    }
    
    public void testEndTestShouldBeIgnored() throws Exception {
        // Setup
        SieveHandler handler = factory.build(monitor);
        String identifier = "AnIdentifier";
        
        // Exercise
        handler.endTest(identifier);
        
        // Verify
        assertEquals("", monitor.toString());
    }
    
    public void testEndCommandShouldNotPrintSemiColonAfterBlock() throws Exception {
        // Setup
        SieveHandler handler = factory.build(monitor);
        String commandName = "SomeCommand";
        
        // Exercise
        handler.endBlock();
        handler.endCommand(commandName);
        
        // Verify
        assertEquals("}", monitor.toString());
    }
    
    public void testAfterEndCommandNextShouldPrintSpace() throws Exception {
        // Setup
        SieveHandler handler = factory.build(monitor);
        String firstCommandName = "FirstCommand";
        String secondCommandName = "SecondCommand";
        
        // Exercise
        handler.endCommand(firstCommandName);
        handler.startCommand(secondCommandName);
        
        // Verify
        assertEquals("; " + secondCommandName, monitor.toString());
    }
}
