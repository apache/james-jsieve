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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * What XmlOut writes must be read back unchanged by an XML parser.
 */
public class XmlOutTest {

    private static final String LEGAL_CHARACTERS = "a퟿�😀\r\r\nb\t\n";

    private static Element write(String attribute, String content) throws Exception {
        StringWriter writer = new StringWriter();
        XmlOut out = new XmlOut(writer);
        out.openElement("e");
        out.attribute("a", attribute);
        out.content(content);
        out.closeElement();
        return DocumentBuilderFactory.newInstance().newDocumentBuilder()
            .parse(new InputSource(new StringReader(writer.toString())))
            .getDocumentElement();
    }

    @Test
    public void legalCharactersShouldBeReadBackInContent() throws Exception {
        assertThat(write("", LEGAL_CHARACTERS).getTextContent()).isEqualTo(LEGAL_CHARACTERS);
    }

    @Test
    public void legalCharactersShouldBeReadBackInAttributes() throws Exception {
        assertThat(write(LEGAL_CHARACTERS, "").getAttribute("a")).isEqualTo(LEGAL_CHARACTERS);
    }

    @Test
    public void loneSurrogatesShouldBeReplaced() throws Exception {
        assertThat(write("\uD83Dx", "x\uDE00").getTextContent()).isEqualTo("x?");
        assertThat(write("\uD83Dx", "").getAttribute("a")).isEqualTo("?x");
    }

}
