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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.jsieve.exception.SyntaxException;
import org.apache.jsieve.utils.JUnitUtils;
import org.apache.jsieve.utils.SieveMailAdapter;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BodyTest {

    private SieveMailAdapter sieveMailAdapter;

    @Before
    public void setUp() {
        sieveMailAdapter = mock(SieveMailAdapter.class);
    }

    @Test(expected = SyntaxException.class)
    public void NoArgumentsShouldThrow() throws Exception {
        String script = "if body {throwTestException;}";

        JUnitUtils.interpret(sieveMailAdapter, script);
    }

    @Test(expected = SyntaxException.class)
    public void invalidTransformationShouldThrow() throws Exception {
        String script = "if body :invalid :contains \"Wibble\" {throwTestException;}";

        JUnitUtils.interpret(sieveMailAdapter, script);
    }

    @Test(expected = SyntaxException.class)
    public void rawShouldThrowWithoutMatcher() throws Exception {
        String script = "if body :raw \"Wibble\" {throwTestException;}";

        JUnitUtils.interpret(sieveMailAdapter, script);
    }

    @Test(expected = SyntaxException.class)
    public void rawShouldThrowWithoutMatcherContains() throws Exception {
        String script = "if body :raw :is \"Wibble\" {throwTestException;}";

        JUnitUtils.interpret(sieveMailAdapter, script);
    }

    @Test(expected = SyntaxException.class)
    public void rawShouldThrowWithoutValuesToMatch() throws Exception {
        String script = "if body :raw :contains {throwTestException;}";

        JUnitUtils.interpret(sieveMailAdapter, script);
    }

    @Test(expected = SyntaxException.class)
    public void rawShouldThrowWithInvalidValuesToMatch() throws Exception {
        String script = "if body :raw :contains :fake {throwTestException;}";

        JUnitUtils.interpret(sieveMailAdapter, script);
    }

    @Test(expected = SyntaxException.class)
    public void rawShouldThrowWithExtraArguments() throws Exception {
        String script = "if body :raw :contains \"Wibble\" :hello {throwTestException;}";

        JUnitUtils.interpret(sieveMailAdapter, script);
    }

    @Test
    public void rawShouldWork() throws Exception {
        String script = "if body :raw :contains \"Wibble\" {throwTestException;}";
        List<String> containedText = Collections.singletonList("Wibble");
        when(sieveMailAdapter.isInBodyRaw(containedText)).thenReturn(false);

        JUnitUtils.interpret(sieveMailAdapter, script);

        verify(sieveMailAdapter).isInBodyRaw(containedText);
    }

    @Test
    public void rawShouldWorkWithMultipleValues() throws Exception {
        String script = "if body :raw :contains [\"Wibble\",\"other\"] {throwTestException;}";
        List<String> containedText = Arrays.asList("Wibble", "other");
        when(sieveMailAdapter.isInBodyRaw(containedText)).thenReturn(false);

        JUnitUtils.interpret(sieveMailAdapter, script);

        verify(sieveMailAdapter).isInBodyRaw(containedText);
    }

    @Test(expected = SyntaxException.class)
    public void textShouldThrowWithoutMatcher() throws Exception {
        String script = "if body :text \"Wibble\" {throwTestException;}";

        JUnitUtils.interpret(sieveMailAdapter, script);
    }

    @Test(expected = SyntaxException.class)
    public void textShouldThrowWithoutMatcherContains() throws Exception {
        String script = "if body :text :is \"Wibble\" {throwTestException;}";

        JUnitUtils.interpret(sieveMailAdapter, script);
    }

    @Test(expected = SyntaxException.class)
    public void textShouldThrowWithoutValuesToMatch() throws Exception {
        String script = "if body :text :contains {throwTestException;}";

        JUnitUtils.interpret(sieveMailAdapter, script);
    }

    @Test(expected = SyntaxException.class)
    public void textShouldThrowWithInvalidValuesToMatch() throws Exception {
        String script = "if body :text :contains :fake {throwTestException;}";

        JUnitUtils.interpret(sieveMailAdapter, script);
    }

    @Test(expected = SyntaxException.class)
    public void textShouldThrowWithExtraArguments() throws Exception {
        String script = "if body :text :contains \"Wibble\" :hello {throwTestException;}";

        JUnitUtils.interpret(sieveMailAdapter, script);
    }

    @Test
    public void textShouldWork() throws Exception {
        String script = "if body :text :contains \"Wibble\" {throwTestException;}";
        List<String> containedText = Collections.singletonList("Wibble");
        when(sieveMailAdapter.isInBodyText(containedText)).thenReturn(false);

        JUnitUtils.interpret(sieveMailAdapter, script);

        verify(sieveMailAdapter).isInBodyText(containedText);
    }

    @Test
    public void textShouldWorkWithMultipleValues() throws Exception {
        String script = "if body :text :contains [\"Wibble\",\"other\"] {throwTestException;}";
        List<String> containedText = Arrays.asList("Wibble", "other");
        when(sieveMailAdapter.isInBodyText(containedText)).thenReturn(false);

        JUnitUtils.interpret(sieveMailAdapter, script);

        verify(sieveMailAdapter).isInBodyText(containedText);
    }

    @Test(expected = SyntaxException.class)
    public void contentShouldThrowWithoutContentTypes() throws Exception {
        String script = "if body :content {throwTestException;}";

        JUnitUtils.interpret(sieveMailAdapter, script);
    }

    @Test(expected = SyntaxException.class)
    public void contentShouldThrowWithInvalidContentTypes() throws Exception {
        String script = "if body :content :fake {throwTestException;}";

        JUnitUtils.interpret(sieveMailAdapter, script);
    }

    @Test(expected = SyntaxException.class)
    public void contentShouldThrowWithoutMatcher() throws Exception {
        String script = "if body :content \"any\" {throwTestException;}";

        JUnitUtils.interpret(sieveMailAdapter, script);
    }

    @Test(expected = SyntaxException.class)
    public void contentShouldThrowWithInvalidMatcher() throws Exception {
        String script = "if body :content \"text/plain\" \"Wibble\" {throwTestException;}";

        JUnitUtils.interpret(sieveMailAdapter, script);
    }

    @Test(expected = SyntaxException.class)
    public void contentShouldThrowWithoutMatcherContains() throws Exception {
        String script = "if body :content \"text/plain\" :is \"Wibble\" {throwTestException;}";

        JUnitUtils.interpret(sieveMailAdapter, script);
    }

    @Test(expected = SyntaxException.class)
    public void contentShouldThrowWithoutValuesToMatch() throws Exception {
        String script = "if body :content \"text/plain\" :contains {throwTestException;}";

        JUnitUtils.interpret(sieveMailAdapter, script);
    }

    @Test(expected = SyntaxException.class)
    public void contentShouldThrowWithInvalidValuesToMatch() throws Exception {
        String script = "if body :content \"text/plain\" :contains :fake {throwTestException;}";

        JUnitUtils.interpret(sieveMailAdapter, script);
    }

    @Test(expected = SyntaxException.class)
    public void contentShouldThrowWithExtraArguments() throws Exception {
        String script = "if body :content \"text/plain\" :contains \"Wibble\" :hello {throwTestException;}";

        JUnitUtils.interpret(sieveMailAdapter, script);
    }

    @Test
    public void contentShouldWork() throws Exception {
        String script = "if body :content \"text/plain\" :contains \"Wibble\" {throwTestException;}";
        List<String> contentTypes = Collections.singletonList("text/plain");
        List<String> containedText = Collections.singletonList("Wibble");
        when(sieveMailAdapter.isInBodyContent(contentTypes, containedText)).thenReturn(false);

        JUnitUtils.interpret(sieveMailAdapter, script);

        verify(sieveMailAdapter).isInBodyContent(contentTypes, containedText);
    }

    @Test
    public void contentShouldWorkWithMultipleValues() throws Exception {
        String script = "if body :content \"text/plain\" :contains [\"Wibble\",\"other\"] {throwTestException;}";
        List<String> containedText = Arrays.asList("Wibble", "other");
        List<String> contentTypes = Collections.singletonList("text/plain");
        when(sieveMailAdapter.isInBodyContent(contentTypes, containedText)).thenReturn(false);

        JUnitUtils.interpret(sieveMailAdapter, script);

        verify(sieveMailAdapter).isInBodyContent(contentTypes, containedText);
    }

    @Test
    public void contentShouldWorkWithMultipleContentTypes() throws Exception {
        String script = "if body :content [\"text/plain\",\"text/html\"] :contains \"Wibble\" {throwTestException;}";
        List<String> contentTypes = Arrays.asList("text/plain", "text/html");
        List<String> containedText = Collections.singletonList("Wibble");
        when(sieveMailAdapter.isInBodyContent(contentTypes, containedText)).thenReturn(false);

        JUnitUtils.interpret(sieveMailAdapter, script);

        verify(sieveMailAdapter).isInBodyContent(contentTypes, containedText);
    }

    @Test
    public void contentShouldWorkWithMultipleValuesAndMultipleContentTypes() throws Exception {
        String script = "if body :content [\"text/plain\",\"text/html\"] :contains [\"Wibble\",\"other\"] {throwTestException;}";
        List<String> containedText = Arrays.asList("Wibble", "other");
        List<String> contentTypes = Arrays.asList("text/plain", "text/html");
        when(sieveMailAdapter.isInBodyContent(contentTypes, containedText)).thenReturn(false);

        JUnitUtils.interpret(sieveMailAdapter, script);

        verify(sieveMailAdapter).isInBodyContent(contentTypes, containedText);
    }

    @Test(expected = SyntaxException.class)
    public void defaultShouldThrowWithoutMatcher() throws Exception {
        String script = "if body \"Wibble\" {throwTestException;}";

        JUnitUtils.interpret(sieveMailAdapter, script);
    }

    @Test(expected = SyntaxException.class)
    public void defaultShouldThrowWithoutValuesToMatch() throws Exception {
        String script = "if body :contains {throwTestException;}";

        JUnitUtils.interpret(sieveMailAdapter, script);
    }

    @Test(expected = SyntaxException.class)
    public void defaultShouldThrowWithInvalidValuesToMatch() throws Exception {
        String script = "if body :contains :fake {throwTestException;}";

        JUnitUtils.interpret(sieveMailAdapter, script);
    }

    @Test(expected = SyntaxException.class)
    public void defaultShouldThrowWithExtraArguments() throws Exception {
        String script = "if body :contains \"Wibble\" :hello {throwTestException;}";

        JUnitUtils.interpret(sieveMailAdapter, script);
    }

    @Test
    public void defaultShouldWork() throws Exception {
        String script = "if body :contains \"Wibble\" {throwTestException;}";
        List<String> containedText = Collections.singletonList("Wibble");
        when(sieveMailAdapter.isInBodyText(containedText)).thenReturn(false);

        JUnitUtils.interpret(sieveMailAdapter, script);

        verify(sieveMailAdapter).isInBodyText(containedText);
    }

    @Test
    public void defaultShouldWorkWithMultipleValues() throws Exception {
        String script = "if body :contains [\"Wibble\",\"other\"] {throwTestException;}";
        List<String> containedText = Arrays.asList("Wibble", "other");
        when(sieveMailAdapter.isInBodyText(containedText)).thenReturn(false);

        JUnitUtils.interpret(sieveMailAdapter, script);

        verify(sieveMailAdapter).isInBodyText(containedText);
    }

}
