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

import org.apache.jsieve.Argument;
import org.apache.jsieve.NumberArgument;
import org.apache.jsieve.StringListArgument;
import org.apache.jsieve.TagArgument;
import org.apache.jsieve.exception.SyntaxException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ArgumentParser {

    private List<Argument> remainingArguments;
    private Set<String> singleTags;
    private Map<String, Argument> tagsWithValues;

    public ArgumentParser(List<Argument> arguments) {
        this.remainingArguments = new ArrayList<Argument>();
        this.singleTags = new HashSet<String>();
        this.tagsWithValues = new HashMap<String, Argument>();
        initialize(arguments);
    }

    public void initialize(List<Argument> arguments) {
        TagArgument previousSeenTagArgument = null;
        for (Argument argument : arguments) {
            if (argument instanceof TagArgument) {
                TagArgument tagArgument = (TagArgument) argument;
                handlePreviousTagArgument(previousSeenTagArgument);
                previousSeenTagArgument = tagArgument;
            } else {
                handleOtherArguments(previousSeenTagArgument, argument);
                previousSeenTagArgument = null;
            }
        }
        if (previousSeenTagArgument != null) {
            singleTags.add(previousSeenTagArgument.getTag());
        }
    }

    private void handlePreviousTagArgument(TagArgument previousSeenTagArgument) {
        if (previousSeenTagArgument != null) {
            singleTags.add(previousSeenTagArgument.getTag());
        }
    }

    private void handleOtherArguments(TagArgument lastSeenTagArgument, Argument argument) {
        if (lastSeenTagArgument == null) {
            remainingArguments.add(argument);
        } else {
            tagsWithValues.put(lastSeenTagArgument.getTag(), argument);
        }
    }

    public String getStringValueForTag(String tag, String exceptionMessage) throws SyntaxException {
        Argument argument = retrieveArgumentIfExists(tag, exceptionMessage);
        if (argument == null) {
            return null;
        }
        return retrieveSingleStringValue(argument, exceptionMessage);
    }

    public Integer getNumericValueForTag(String tag, String exceptionMessage) throws SyntaxException {
        Argument argument = retrieveArgumentIfExists(tag, exceptionMessage);
        if (argument == null) {
            return null;
        }
        return retrieveNumericValue(argument, exceptionMessage);
    }

    public List<String> getStringListForTag(String tag, String exceptionMessage) throws SyntaxException {
        Argument argument = retrieveArgumentIfExists(tag, exceptionMessage);
        if (argument == null) {
            return null;
        }
        return retrieveStringValues(argument, exceptionMessage);
    }

    public Set<String> getSingleTags() {
        return new HashSet<String>(singleTags);
    }

    public String getRemainingStringValue(String exceptionMessage) throws SyntaxException {
        if (remainingArguments.size() > 1) {
            throw new SyntaxException(exceptionMessage);
        }
        if (remainingArguments.size() == 0) {
            return null;
        }
        return retrieveSingleStringValue(remainingArguments.get(0), exceptionMessage);
    }

    public void validateSingleTags(String... validTags) throws SyntaxException {
        validateTagCollectionWithExpectations(singleTags, validTags);
    }

    public void validateTagsWithValue(String... validTags) throws SyntaxException {
        validateTagCollectionWithExpectations(tagsWithValues.keySet(), validTags);
    }

    private void validateTagCollectionWithExpectations(Set<String> seenTags, String[] expectations) throws SyntaxException {
        Set<String> validTagList = getSetFromStringArray(expectations);
        if (!validTagList.containsAll(seenTags)) {
            throw new SyntaxException(buildUnwantedTagsErrorMessage(retrieveUnwantedTags(seenTags, validTagList)));
        }
    }

    private Set<String> getSetFromStringArray(String[] validTags) {
        Set<String> validTagList = new HashSet<String>();
        for(String validTag : validTags) {
            validTagList.add(validTag);
        }
        return validTagList;
    }

    private Set<String> retrieveUnwantedTags(Set<String> seenTags, Set<String> validTagList) {
        Set<String> unwantedTags = new HashSet<String>(seenTags);
        unwantedTags.removeAll(validTagList);
        return unwantedTags;
    }

    private String buildUnwantedTagsErrorMessage(Set<String> unwantedTags) {
        String errorMessage;
        StringBuilder errorMessageBuilder = new StringBuilder().append("Unexpected tags : [");
        for (String unwantedTag : unwantedTags) {
            errorMessageBuilder.append("\"").append(unwantedTag).append("\"");
        }
        errorMessageBuilder.append("]");
        errorMessage = errorMessageBuilder.toString();
        return errorMessage;
    }

    private Argument retrieveArgumentIfExists(String tag, String exceptionMessage) throws SyntaxException {
        Argument argument = tagsWithValues.get(tag);
        if (argument == null) {
            if (singleTags.contains(tag)) {
                throw new SyntaxException(exceptionMessage);
            } else {
                return null;
            }
        }
        return argument;
    }

    private List<String> retrieveStringValues(Argument argument, String exceptionMessage) throws SyntaxException {
        if (! (argument instanceof StringListArgument)) {
            throw new SyntaxException(exceptionMessage);
        }
        StringListArgument stringListArgument = (StringListArgument) argument;
        return stringListArgument.getList();
    }

    private String retrieveSingleStringValue(Argument argument, String exceptionMessage) throws SyntaxException {
        List<String> stringsValue = retrieveStringValues(argument, exceptionMessage);
        if (stringsValue.size() != 1) {
            throw new SyntaxException(exceptionMessage);
        }
        return stringsValue.get(0);
    }

    private Integer retrieveNumericValue(Argument argument, String exceptionMessage) throws SyntaxException {
        if (! (argument instanceof NumberArgument)) {
            throw new SyntaxException(exceptionMessage);
        }
        NumberArgument numberArgument = (NumberArgument) argument;
        return numberArgument.getInteger();
    }

}
