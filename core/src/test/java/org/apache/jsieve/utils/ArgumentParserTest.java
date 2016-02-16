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

package org.apache.jsieve.utils;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.Lists;
import org.apache.jsieve.Argument;
import org.apache.jsieve.NumberArgument;
import org.apache.jsieve.StringListArgument;
import org.apache.jsieve.TagArgument;
import org.apache.jsieve.exception.SyntaxException;
import org.apache.jsieve.parser.generated.Token;
import org.junit.Test;

public class ArgumentParserTest {
    public static final int KIND = 18;
    public static final int NUMERIC_VALUE = 42;
    public static final String ANY_TAG = ":any";
    public static final String OTHER_TAG = ":other";
    public static final String EXCEPTION_MESSAGE = "ExceptionMessage";
    public static final String STRING_VALUE = "string";
    public static final String STRING_VALUE_2 = "string2";
    public static final TagArgument ANY_TAG_ARGUMENT = new TagArgument(new Token(KIND, ANY_TAG));
    public static final TagArgument OTHER_TAG_ARGUMENT = new TagArgument(new Token(KIND, OTHER_TAG));
    public static final NumberArgument NUMBER_ARGUMENT = new NumberArgument(new Token(KIND, Integer.toString(NUMERIC_VALUE)));
    public static final StringListArgument SINGLE_STRING_ARGUMENT = new StringListArgument(Lists.newArrayList(STRING_VALUE));
    public static final StringListArgument STRING_LIST_ARGUMENT = new StringListArgument(Lists.newArrayList(STRING_VALUE, STRING_VALUE_2));

    @Test
    public void getRemainingStringValueShouldReturnNullByDefault() throws Exception {
        ArgumentParser argumentParser = new ArgumentParser(Lists.<Argument>newArrayList());
        assertThat(argumentParser.getRemainingStringValue(EXCEPTION_MESSAGE)).isNull();
    }

    @Test
    public void getNumericValueForTagShouldReturnNullByDefault() throws Exception {
        ArgumentParser argumentParser = new ArgumentParser(Lists.<Argument>newArrayList());
        assertThat(argumentParser.getNumericValueForTag(ANY_TAG, EXCEPTION_MESSAGE)).isNull();
    }

    @Test
    public void getStringListForTagShouldReturnNullByDefault() throws Exception {
        ArgumentParser argumentParser = new ArgumentParser(Lists.<Argument>newArrayList());
        assertThat(argumentParser.getStringListForTag(ANY_TAG, EXCEPTION_MESSAGE)).isNull();
    }


    @Test
    public void getStringValueForTagShouldReturnNullByDefault() throws Exception {
        ArgumentParser argumentParser = new ArgumentParser(Lists.<Argument>newArrayList());
        assertThat(argumentParser.getStringValueForTag(ANY_TAG, EXCEPTION_MESSAGE)).isNull();
    }

    @Test
    public void validateSingleTagsShouldNotThrowByDefault() throws Exception {
        ArgumentParser argumentParser = new ArgumentParser(Lists.<Argument>newArrayList());
        argumentParser.validateSingleTags();
    }

    @Test
    public void validateTagsWithValueShouldNotThrowByDefault() throws Exception {
        ArgumentParser argumentParser = new ArgumentParser(Lists.<Argument>newArrayList());
        argumentParser.validateTagsWithValue();
    }

    @Test
    public void validateSingleTagsShouldNotThrowWhenAllowedValuesAreNotPresent() throws Exception {
        ArgumentParser argumentParser = new ArgumentParser(Lists.<Argument>newArrayList());
        argumentParser.validateSingleTags(ANY_TAG);
    }

    @Test
    public void validateTagsWithValueShouldNotThrowWhenAllowedValuesAreNotPresent() throws Exception {
        ArgumentParser argumentParser = new ArgumentParser(Lists.<Argument>newArrayList());
        argumentParser.validateTagsWithValue(ANY_TAG);
    }

    @Test(expected = SyntaxException.class)
    public void validateSingleTagsShouldThrowOnUnexpectedSingleTag() throws Exception {
        ArgumentParser argumentParser = new ArgumentParser(Lists.<Argument>newArrayList(ANY_TAG_ARGUMENT));
        argumentParser.validateSingleTags();
    }

    @Test
    public void validateTagsWithValueShouldNotThrowOnSingleTag() throws Exception {
        ArgumentParser argumentParser = new ArgumentParser(Lists.<Argument>newArrayList(ANY_TAG_ARGUMENT));
        argumentParser.validateTagsWithValue();
    }

    @Test
    public void validateSingleTagsShouldNotThrowOnExpectedSingleTag() throws Exception {
        ArgumentParser argumentParser = new ArgumentParser(Lists.<Argument>newArrayList(ANY_TAG_ARGUMENT));
        argumentParser.validateSingleTags(ANY_TAG);
    }

    @Test
    public void validateSingleTagsShouldNotThrowOnTagWithValue() throws Exception {
        ArgumentParser argumentParser = new ArgumentParser(Lists.newArrayList(OTHER_TAG_ARGUMENT, NUMBER_ARGUMENT));
        argumentParser.validateSingleTags(ANY_TAG);
    }

    @Test(expected = SyntaxException.class)
    public void validateTagsWithValueShouldThrowOnUnexpectedTagWithValue() throws Exception {
        ArgumentParser argumentParser = new ArgumentParser(Lists.newArrayList(OTHER_TAG_ARGUMENT, NUMBER_ARGUMENT));
        argumentParser.validateTagsWithValue(ANY_TAG);
    }

    @Test(expected = SyntaxException.class)
    public void validateSingleTagsShouldThrowOnUnexpectedValue() throws Exception {
        ArgumentParser argumentParser = new ArgumentParser(Lists.<Argument>newArrayList(OTHER_TAG_ARGUMENT));
        argumentParser.validateSingleTags(ANY_TAG);
    }

    @Test(expected = SyntaxException.class)
    public void getRemainingStringValueShouldThrowOnWrongArgumentType() throws Exception {
        ArgumentParser argumentParser = new ArgumentParser(Lists.<Argument>newArrayList(NUMBER_ARGUMENT));
        argumentParser.getRemainingStringValue(EXCEPTION_MESSAGE);
    }

    @Test(expected = SyntaxException.class)
    public void getRemainingStringValueShouldThrowOnMultipleElementsInStringList() throws Exception {
        ArgumentParser argumentParser = new ArgumentParser(Lists.<Argument>newArrayList(STRING_LIST_ARGUMENT));
        argumentParser.getRemainingStringValue(EXCEPTION_MESSAGE);
    }

    @Test
    public void getRemainingStringValueShouldWork() throws Exception {
        ArgumentParser argumentParser = new ArgumentParser(Lists.<Argument>newArrayList(SINGLE_STRING_ARGUMENT));
        assertThat(argumentParser.getRemainingStringValue(EXCEPTION_MESSAGE)).isEqualTo(STRING_VALUE);
    }

    @Test
    public void getRemainingStringValueShouldReturnNullWhenCalledWithOnlyTagWithValue() throws Exception {
        ArgumentParser argumentParser = new ArgumentParser(Lists.newArrayList(ANY_TAG_ARGUMENT, STRING_LIST_ARGUMENT));
        assertThat(argumentParser.getRemainingStringValue(EXCEPTION_MESSAGE)).isNull();
    }

    @Test(expected = SyntaxException.class)
    public void getNumericValueForTagShouldThrowOnSingleStringArgument() throws Exception {
        ArgumentParser argumentParser = new ArgumentParser(Lists.newArrayList(ANY_TAG_ARGUMENT, SINGLE_STRING_ARGUMENT));
        argumentParser.getNumericValueForTag(ANY_TAG, EXCEPTION_MESSAGE);
    }

    @Test
    public void getStringListForTagShouldWorkWithSingleValue() throws Exception {
        ArgumentParser argumentParser = new ArgumentParser(Lists.newArrayList(ANY_TAG_ARGUMENT, SINGLE_STRING_ARGUMENT));
        assertThat(argumentParser.getStringListForTag(ANY_TAG, EXCEPTION_MESSAGE)).containsExactly(STRING_VALUE);
    }

    @Test
    public void getStringValueForTagShouldWork() throws Exception {
        ArgumentParser argumentParser = new ArgumentParser(Lists.newArrayList(ANY_TAG_ARGUMENT, SINGLE_STRING_ARGUMENT));
        assertThat(argumentParser.getStringValueForTag(ANY_TAG, EXCEPTION_MESSAGE)).isEqualTo(STRING_VALUE);
    }

    @Test
    public void getNumericValueForTagShouldWork() throws Exception {
        ArgumentParser argumentParser = new ArgumentParser(Lists.newArrayList(ANY_TAG_ARGUMENT, NUMBER_ARGUMENT));
        assertThat(argumentParser.getNumericValueForTag(ANY_TAG, EXCEPTION_MESSAGE)).isEqualTo(NUMERIC_VALUE);
    }

    @Test(expected = SyntaxException.class)
    public void getStringListForTagShouldThrowWhenUsedWithNumberArgument() throws Exception {
        ArgumentParser argumentParser = new ArgumentParser(Lists.newArrayList(ANY_TAG_ARGUMENT, NUMBER_ARGUMENT));
        argumentParser.getStringListForTag(ANY_TAG, EXCEPTION_MESSAGE);
    }

    @Test(expected = SyntaxException.class)
    public void getStringValueForTagShouldThrowWhenUsedWithNumberArgument() throws Exception {
        ArgumentParser argumentParser = new ArgumentParser(Lists.newArrayList(ANY_TAG_ARGUMENT, NUMBER_ARGUMENT));
        argumentParser.getStringValueForTag(ANY_TAG, EXCEPTION_MESSAGE);
    }

    @Test(expected = SyntaxException.class)
    public void getNumericValueForTagShouldThrowWhenUsedWithStringListArgument() throws Exception {
        ArgumentParser argumentParser = new ArgumentParser(Lists.newArrayList(ANY_TAG_ARGUMENT, STRING_LIST_ARGUMENT));
        argumentParser.getNumericValueForTag(ANY_TAG, EXCEPTION_MESSAGE);
    }

    @Test
    public void getStringListForTagShouldWork() throws Exception {
        ArgumentParser argumentParser = new ArgumentParser(Lists.newArrayList(ANY_TAG_ARGUMENT, STRING_LIST_ARGUMENT));
        assertThat(argumentParser.getStringListForTag(ANY_TAG, EXCEPTION_MESSAGE)).containsOnly(STRING_VALUE, STRING_VALUE_2);
    }

    @Test(expected = SyntaxException.class)
    public void getStringValueForTagShouldThrowWhenUsedWithMultipleValues() throws Exception {
        ArgumentParser argumentParser = new ArgumentParser(Lists.newArrayList(ANY_TAG_ARGUMENT, STRING_LIST_ARGUMENT));
        argumentParser.getStringValueForTag(ANY_TAG, EXCEPTION_MESSAGE);
    }

    @Test
    public void getNumericValueForTagShouldReturnNullWhenUsedOnAnOtherTag() throws Exception {
        ArgumentParser argumentParser = new ArgumentParser(Lists.newArrayList(OTHER_TAG_ARGUMENT, NUMBER_ARGUMENT));
        assertThat(argumentParser.getNumericValueForTag(ANY_TAG, EXCEPTION_MESSAGE)).isNull();
    }

    @Test
    public void getStringListForTagShouldReturnNullWhenUsedOnAnOtherTag() throws Exception {
        ArgumentParser argumentParser = new ArgumentParser(Lists.newArrayList(OTHER_TAG_ARGUMENT, STRING_LIST_ARGUMENT));
        assertThat(argumentParser.getStringListForTag(ANY_TAG, EXCEPTION_MESSAGE)).isNull();
    }

    @Test
    public void getStringValueForTagShouldReturnNullWhenUsedOnAnOtherTag() throws Exception {
        ArgumentParser argumentParser = new ArgumentParser(Lists.newArrayList(OTHER_TAG_ARGUMENT, SINGLE_STRING_ARGUMENT));
        assertThat(argumentParser.getStringValueForTag(ANY_TAG, EXCEPTION_MESSAGE)).isNull();
    }

    @Test
    public void argumentParserShouldHandleMultipleTagsWithValue() throws Exception {
        ArgumentParser argumentParser = new ArgumentParser(Lists.newArrayList(OTHER_TAG_ARGUMENT, SINGLE_STRING_ARGUMENT, ANY_TAG_ARGUMENT, NUMBER_ARGUMENT));
        assertThat(argumentParser.getNumericValueForTag(ANY_TAG, EXCEPTION_MESSAGE)).isEqualTo(NUMERIC_VALUE);
        assertThat(argumentParser.getStringValueForTag(OTHER_TAG, EXCEPTION_MESSAGE));
    }

    @Test
    public void argumentParserShouldHandleTagWithValueAndRemainingString() throws Exception {
        ArgumentParser argumentParser = new ArgumentParser(Lists.newArrayList(ANY_TAG_ARGUMENT, NUMBER_ARGUMENT, SINGLE_STRING_ARGUMENT));
        assertThat(argumentParser.getNumericValueForTag(ANY_TAG, EXCEPTION_MESSAGE)).isEqualTo(NUMERIC_VALUE);
        assertThat(argumentParser.getRemainingStringValue(EXCEPTION_MESSAGE)).isEqualTo(STRING_VALUE);
    }

    @Test
    public void argumentParserShouldHandleMultipleSingleTagArguments() throws Exception {
        ArgumentParser argumentParser = new ArgumentParser(Lists.<Argument>newArrayList(ANY_TAG_ARGUMENT, OTHER_TAG_ARGUMENT));
        assertThat(argumentParser.getSingleTags()).containsExactly(ANY_TAG, OTHER_TAG);
    }

}
