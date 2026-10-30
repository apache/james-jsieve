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

import org.apache.jsieve.exception.SieveException;
import org.apache.jsieve.mail.ActionReject;
import org.apache.jsieve.mail.MailAdapter;
import org.apache.jsieve.parser.generated.ParseException;
import org.apache.jsieve.utils.JUnitUtils;
import org.junit.Assert;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Class RejectTest
 */
public class MultilineTextTest {

    /**
     * Tests that a multiline message is correctly passed
     */
    @org.junit.Test
    public void testRejectMultilineMessage() throws Exception {
        String message = "This is not a love song";
        String script = "reject text:\r\n" + message + "\r\n.\r\n;";
        ActionReject rejection = runRejectScript(script);
        Assert.assertEquals(message, rejection.getMessage());
    }

    /**
     * Tests that a multiline message is correctly passed when whitespace is inserted
     * between the command and the content.
     */
    @Test
    public void testRejectMultilineMessageWithWhitespace() throws Exception {
        String message = "This is not a love song";
        String script = "reject text: \t \t \r\n" + message + "\r\n.\r\n;";
        ActionReject rejection = runRejectScript(script);
        Assert.assertEquals(message, rejection.getMessage());
    }

    /**
     * Tests that a multiline message is correctly passed when dots within a line
     * between the command and the content.
     */
    @Test
    public void testRejectMultilineMessageWithDotsMidline() throws Exception {
        String message = "This is not.....a love song";
        String script = "reject text:\r\n" + message + "\r\n.\r\n;";
        ActionReject rejection = runRejectScript(script);
        Assert.assertEquals(message, rejection.getMessage());
    }

    /**
     * Tests that a multiline message with dot stuffing is correctly decoded.
     */
    @Test
    public void testRejectMultilineMessageWithDotStuffing() throws Exception {
        String lineOne = "This is not\n";
        String lineTwo = ".A Love Story";
        String script = "reject text:\r\n" + lineOne + '.' + lineTwo + "\r\n.\r\n;";
        ActionReject rejection = runRejectScript(script);
        Assert.assertEquals(lineOne + lineTwo, rejection.getMessage());
    }

    /**
     * Tests that a multiline message with missed dot stuffing is correctly decoded.
     */
    @Test
    public void testRejectMultilineMessageWithMissedDotStuffing() throws Exception {
        String lineOne = "This is not\n";
        String lineTwo = ".A Love Story";
        String script = "reject text:\r\n" + lineOne + lineTwo + "\r\n.\r\n;";
        ActionReject rejection = runRejectScript(script);
        Assert.assertEquals(lineOne + lineTwo, rejection.getMessage());
    }

    /**
     * Tests that a multiline message with many dots stuffed is correctly decoded.
     */
    @Test
    public void testNumberOfStuffedDotsInMultilineMessage() throws Exception {
        String lineOne = "This is line 1.\n";
        String lineTwo = "This is line 2.\n";
        String lineThree = "........ This is line 3.\n";
        String script = "reject text:\r\n" + lineOne + lineTwo + '.' + lineThree + "\r\n.\r\n;";
        ActionReject rejection = runRejectScript(script);
        Assert.assertEquals(lineOne + lineTwo + lineThree, rejection.getMessage());
    }

    /**
     * Tests that a multiline message with many dots stuffed is correctly decoded.
     */
    @Test
    public void testConsecutiveDotStuffedLineInMultilineMessage() throws Exception {
        String lineOne = "This is line 1.\n";
        String lineTwo = "This is line 2.\n";
        String lineThree = "........ This is line 3.\n";
        String lineFour = ".\n";
        String lineFive = ".\n";
        String script = "reject text:\r\n" + lineOne + lineTwo + '.' + lineThree + '.' + lineFour + '.' + lineFive + "\r\n.\r\n;";
        ActionReject rejection = runRejectScript(script);
        Assert.assertEquals(lineOne + lineTwo + lineThree + lineFour + lineFive, rejection.getMessage());
    }

    @Test
    public void emptyMultilineMessageShouldBeEmpty() throws Exception {
        ActionReject rejection = runRejectScript("reject text:\r\n.\r\n;");
        Assert.assertEquals("", rejection.getMessage());
    }

    @Test
    public void lastLineEndingWithLfShouldKeepItsCharacters() throws Exception {
        ActionReject rejection = runRejectScript("reject text:\r\nabc\n.\r\n;");
        Assert.assertEquals("abc", rejection.getMessage());
    }

    @Test
    public void linesEndingWithCrShouldKeepTheirBreaks() throws Exception {
        ActionReject rejection = runRejectScript("reject text:\r\n\r\r.\r\n;");
        Assert.assertEquals("\r", rejection.getMessage());
    }

    @Test
    public void hashCommentEndingWithCrShouldNotEatTheMessage() throws Exception {
        ActionReject rejection = runRejectScript("reject text: # reason\rabc\r\n.\r\n;");
        Assert.assertEquals("abc", rejection.getMessage());
    }

    @Test
    public void stuffedDotShouldBeRemovedAfterACr() throws Exception {
        ActionReject rejection = runRejectScript("reject text:\r\na\r..b\r\n.\r\n;");
        Assert.assertEquals("a\r.b", rejection.getMessage());
    }

    @Test
    public void backslashesShouldBeKept() throws Exception {
        ActionReject rejection = runRejectScript("reject text:\r\nFiles are in C:\\dir\\\\sub\\\r\n.\r\n;");
        Assert.assertEquals("Files are in C:\\dir\\\\sub\\", rejection.getMessage());
    }

    private ActionReject runRejectScript(String script) throws SieveException, ParseException {
        MailAdapter mail = JUnitUtils.createMail();
        JUnitUtils.interpret(mail, script);
        assertTrue(mail.getActions().size() == 1);
        Object action = mail.getActions().get(0);
        assertTrue(action instanceof ActionReject);
        return (ActionReject) action;
    }
}
