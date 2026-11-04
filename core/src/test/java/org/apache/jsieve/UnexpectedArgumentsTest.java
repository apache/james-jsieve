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

import org.apache.jsieve.exception.SyntaxException;
import org.apache.jsieve.utils.JUnitUtils;
import org.apache.jsieve.utils.SieveMailAdapter;
import org.junit.Test;

/**
 * An argument or a test that a command or a test does not take is a syntax
 * error when it runs (RFC 5228 section 2.10.6), not ignored.
 */
public class UnexpectedArgumentsTest {

    @Test(expected = SyntaxException.class)
    public void keepWithATestShouldThrow() throws Exception {
        String script = "keep true;";

        JUnitUtils.interpret(JUnitUtils.createMail(), script);
    }

    @Test(expected = SyntaxException.class)
    public void discardWithATestShouldThrow() throws Exception {
        String script = "discard true;";

        JUnitUtils.interpret(JUnitUtils.createMail(), script);
    }

    @Test(expected = SyntaxException.class)
    public void stopWithATestShouldThrow() throws Exception {
        String script = "stop true;";

        JUnitUtils.interpret(JUnitUtils.createMail(), script);
    }

    @Test(expected = SyntaxException.class)
    public void elseWithATestShouldThrow() throws Exception {
        String script = "if false {stop;} else true {keep;}";

        JUnitUtils.interpret(JUnitUtils.createMail(), script);
    }

    @Test(expected = SyntaxException.class)
    public void fileintoWithATestShouldThrow() throws Exception {
        String script = "require \"fileinto\"; fileinto \"INBOX.a\" true;";

        JUnitUtils.interpret(JUnitUtils.createMail(), script);
    }

    @Test(expected = SyntaxException.class)
    public void redirectWithATestShouldThrow() throws Exception {
        String script = "redirect \"user@example.com\" true;";

        JUnitUtils.interpret(JUnitUtils.createMail(), script);
    }

    @Test(expected = SyntaxException.class)
    public void rejectWithATestShouldThrow() throws Exception {
        String script = "require \"reject\"; reject \"no\" true;";

        JUnitUtils.interpret(JUnitUtils.createMail(), script);
    }

    @Test(expected = SyntaxException.class)
    public void vacationWithATestShouldThrow() throws Exception {
        String script = "require \"vacation\"; vacation \"away\" true;";

        JUnitUtils.interpret(JUnitUtils.createMail(), script);
    }

    @Test(expected = SyntaxException.class)
    public void logWithATestShouldThrow() throws Exception {
        String script = "require \"log\"; log \"x\" true;";

        JUnitUtils.interpret(JUnitUtils.createMail(), script);
    }

    @Test(expected = SyntaxException.class)
    public void requireWithATestShouldThrow() throws Exception {
        String script = "require \"fileinto\" true;";

        JUnitUtils.interpret(JUnitUtils.createMail(), script);
    }

    @Test(expected = SyntaxException.class)
    public void ifWithAnArgumentShouldThrow() throws Exception {
        String script = "if \"x\" true {keep;}";

        JUnitUtils.interpret(JUnitUtils.createMail(), script);
    }

    @Test(expected = SyntaxException.class)
    public void elsifWithAnArgumentShouldThrow() throws Exception {
        String script = "if false {stop;} elsif \"x\" true {keep;}";

        JUnitUtils.interpret(JUnitUtils.createMail(), script);
    }

    @Test(expected = SyntaxException.class)
    public void ifWithTwoTestsShouldThrow() throws Exception {
        String script = "if (true, false) {keep;}";

        JUnitUtils.interpret(JUnitUtils.createMail(), script);
    }

    @Test(expected = SyntaxException.class)
    public void elsifWithTwoTestsShouldThrow() throws Exception {
        String script = "if false {stop;} elsif (true, false) {keep;}";

        JUnitUtils.interpret(JUnitUtils.createMail(), script);
    }

    @Test(expected = SyntaxException.class)
    public void trueWithATestShouldThrow() throws Exception {
        String script = "if true false {keep;}";

        JUnitUtils.interpret(JUnitUtils.createMail(), script);
    }

    @Test(expected = SyntaxException.class)
    public void falseWithATestShouldThrow() throws Exception {
        String script = "if false true {keep;}";

        JUnitUtils.interpret(JUnitUtils.createMail(), script);
    }

    @Test(expected = SyntaxException.class)
    public void sizeWithATestShouldThrow() throws Exception {
        String script = "if size :over 0 true {keep;}";

        JUnitUtils.interpret(JUnitUtils.createMail(), script);
    }

    @Test(expected = SyntaxException.class)
    public void bodyWithATestShouldThrow() throws Exception {
        SieveMailAdapter mail = (SieveMailAdapter) JUnitUtils.createMail();
        mail.getMessage().setText("x");
        String script = "require \"body\"; if body :contains \"x\" true {keep;}";

        JUnitUtils.interpret(mail, script);
    }

}
