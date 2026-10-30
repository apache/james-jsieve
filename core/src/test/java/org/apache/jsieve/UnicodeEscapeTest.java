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

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.jsieve.commands.ThrowTestException;
import org.apache.jsieve.parser.generated.ParseException;
import org.apache.jsieve.parser.generated.TokenMgrError;
import org.apache.jsieve.utils.JUnitUtils;
import org.apache.jsieve.utils.SieveMailAdapter;
import org.junit.Test;

/**
 * Sieve has no Java style unicode escape (backslash, 'u', four hexadecimal
 * digits; RFC 5228 section 2.4.2): the characters of a script are read as
 * they are.
 */
public class UnicodeEscapeTest {

    @Test
    public void incompleteUnicodeEscapeShouldBeALexicalError() {
        assertThatThrownBy(() -> JUnitUtils.parse("\\u3"))
            .isInstanceOfAny(ParseException.class, TokenMgrError.class);
        assertThatThrownBy(() -> JUnitUtils.parse("i\\u7"))
            .isInstanceOfAny(ParseException.class, TokenMgrError.class);
    }

    @Test
    public void unicodeEscapeShouldNotBeDecoded() {
        // was read as "keep;"
        assertThatThrownBy(() -> JUnitUtils.parse("\\u006beep;"))
            .isInstanceOfAny(ParseException.class, TokenMgrError.class);
    }

    @Test(expected = ThrowTestException.TestException.class)
    public void charactersAboveLatin1ShouldBeRead() throws Exception {
        SieveMailAdapter mail = (SieveMailAdapter) JUnitUtils.createMail();
        mail.getMessage().addHeader("X-Test", "\u65e5\u672c \ud83d\ude00");
        JUnitUtils.interpret(mail, "if header :is \"X-Test\" \"\u65e5\u672c \ud83d\ude00\" {throwTestException;}");
    }

}
