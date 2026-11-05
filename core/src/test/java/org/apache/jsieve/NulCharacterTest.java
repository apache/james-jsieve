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

import org.apache.jsieve.parser.generated.ParseException;
import org.apache.jsieve.utils.JUnitUtils;
import org.junit.Test;

/**
 * NUL is no Sieve character: the RFC 5228 section 8.1 grammar leaves it out of
 * quoted strings (octet-not-qspecial), multi-line strings (octet-not-crlf,
 * octet-not-period) and comments (octet-not-crlf, not-star, not-star-slash).
 */
public class NulCharacterTest {

    @Test(expected = ParseException.class)
    public void nulInAQuotedStringShouldThrowAParseException() throws Exception {
        JUnitUtils.parse("if header :is \"Subject\" \"a\u0000b\" {keep;}");
    }

    @Test(expected = ParseException.class)
    public void nulInAMultiLineStringShouldThrowAParseException() throws Exception {
        JUnitUtils.parse("require \"reject\"; reject text:\r\na\u0000b\r\n.\r\n;");
    }

    @Test(expected = ParseException.class)
    public void nulStartingAMultiLineLineShouldThrowAParseException() throws Exception {
        JUnitUtils.parse("require \"reject\"; reject text:\r\n\u0000\r\n.\r\n;");
    }

    @Test(expected = ParseException.class)
    public void nulInADotStuffedLineShouldThrowAParseException() throws Exception {
        JUnitUtils.parse("require \"reject\"; reject text:\r\n..a\u0000\r\n.\r\n;");
    }

    @Test(expected = ParseException.class)
    public void nulInTheCommentOfAMultiLineStartShouldThrowAParseException() throws Exception {
        JUnitUtils.parse("require \"reject\"; reject text: # a\u0000b\r\nabc\r\n.\r\n;");
    }

    @Test(expected = ParseException.class)
    public void nulInAHashCommentShouldThrowAParseException() throws Exception {
        JUnitUtils.parse("# a\u0000b\r\nkeep;");
    }

    @Test(expected = ParseException.class)
    public void nulInAHashCommentEndingTheScriptShouldThrowAParseException() throws Exception {
        JUnitUtils.parse("keep;\r\n# a\u0000b");
    }

    @Test(expected = ParseException.class)
    public void nulInABracketCommentShouldThrowAParseException() throws Exception {
        JUnitUtils.parse("/* a\u0000b */ keep;");
    }

    @Test(expected = ParseException.class)
    public void nulAfterAStarInABracketCommentShouldThrowAParseException() throws Exception {
        JUnitUtils.parse("/* a *\u0000 */ keep;");
    }

    @Test
    public void otherControlCharactersInAQuotedStringShouldStillParse() throws Exception {
        JUnitUtils.parse("if header :is \"Subject\" \"a\u0001\u001fb\" {keep;}");
    }

}