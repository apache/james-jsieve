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

package org.apache.jsieve.tests.optional;

import java.util.Iterator;
import java.util.List;

import org.apache.jsieve.Argument;
import org.apache.jsieve.Arguments;
import org.apache.jsieve.SieveContext;
import org.apache.jsieve.StringListArgument;
import org.apache.jsieve.TagArgument;
import org.apache.jsieve.exception.SieveException;
import org.apache.jsieve.exception.SyntaxException;
import org.apache.jsieve.mail.MailAdapter;
import org.apache.jsieve.parser.generated.Token;
import org.apache.jsieve.tests.AbstractTest;

/**
 * Implementation of body extension defined in
 * <a href='http://tools.ietf.org/html/rfc5173'>RFC5173</a>.
 */
public class Body extends AbstractTest {

    public static final String TEXT = ":text";
    public static final String RAW = ":raw";
    public static final String CONTENT = ":content";
    private TagArgument transformation;
    private StringListArgument contentTypes;
    private TagArgument matcher;
    private StringListArgument valuesToBeMatched;

    protected void validateArguments(Arguments args, SieveContext ctx) throws SieveException {
        Iterator<Argument> matchingSpecifications = retrieveTransformationAndMatchingSpecificationIterator(args.getArgumentList());

        if (transformation.getTag().equals(TEXT)) {
            parseDefaultArguments(matchingSpecifications);
        } else if (transformation.getTag().equals(RAW)) {
            parseDefaultArguments(matchingSpecifications);
        } else if (transformation.getTag().equals(CONTENT)) {
            parseContentArguments(matchingSpecifications);
        } else {
            throw new SyntaxException("Unknown transformation " + transformation.getTag() + ". See RFC-5173 section 5.");
        }
    }

    private void parseContentArguments(Iterator<Argument> matchingSpecifications) throws SyntaxException {
        retrieveContentTypes(matchingSpecifications);
        retrieveMatcher(matchingSpecifications);
        retrieveMatchValues(matchingSpecifications);
        assureNoMoreArguments(matchingSpecifications);
    }

    private void parseDefaultArguments(Iterator<Argument> matchingSpecifications) throws SyntaxException {
        retrieveMatcher(matchingSpecifications);
        retrieveMatchValues(matchingSpecifications);
        assureNoMoreArguments(matchingSpecifications);
    }

    private Iterator<Argument> retrieveTransformationAndMatchingSpecificationIterator(List<Argument> arglist) throws SyntaxException {
        if (arglist.size() < 1 ) {
            throw new SyntaxException("Transformations should be specified. See RFC-5173 section 5.");
        }
        Argument arg = arglist.get(0);
        if (!(arg instanceof TagArgument)) {
            // by default transformation should be :text
            transformation = new TagArgument(new Token(0, TEXT));
            return arglist.iterator();
        } else {
            TagArgument transformationCandidate = (TagArgument) arg;
            if (transformationCandidate.getTag().equals(TEXT) ||
                    transformationCandidate.getTag().equals(RAW) ||
                    transformationCandidate.getTag().equals(CONTENT) ) {
                transformation = (TagArgument) arg;
                Iterator<Argument> matchingSpecifications = arglist.iterator();
                matchingSpecifications.next();
                return matchingSpecifications;
            } else {
                // by default transformation should be :text
                transformation = new TagArgument(new Token(0, TEXT));
                return arglist.iterator();
            }
        }
    }

    protected boolean executeBasic(MailAdapter mail, Arguments args, SieveContext ctx) throws SieveException {
        if (transformation.getTag().equals(RAW)) {
            return mail.isInBodyRaw(valuesToBeMatched.getList());
        } else if (transformation.getTag().equals(CONTENT)) {
            return mail.isInBodyContent(contentTypes.getList(), valuesToBeMatched.getList());
        } else if (transformation.getTag().equals(TEXT)) {
            return mail.isInBodyText(valuesToBeMatched.getList());
        } else {
            throw new RuntimeException("Invalid transformation caught. Is your argument parsing buggy ?");
        }
    }

    private void retrieveContentTypes(Iterator<Argument> matchingSpecifications) throws SyntaxException {
        if (!matchingSpecifications.hasNext()) {
            throw new SyntaxException("Expecting the list of content types following :content");
        }
        Argument contentTypesArgument = matchingSpecifications.next();
        if (! (contentTypesArgument instanceof StringListArgument)) {
            throw new SyntaxException("Expecting a String list to specify content types and not a" + contentTypesArgument.getClass());
        }
        contentTypes = (StringListArgument) contentTypesArgument;
    }

    private void retrieveMatcher(Iterator<Argument> matchingSpecifications) throws SyntaxException {
        if (!matchingSpecifications.hasNext()) {
            throw new SyntaxException("Expecting a matcher :contains");
        }
        Argument matcherArgument = matchingSpecifications.next();
        if (! (matcherArgument instanceof TagArgument)) {
            throw new SyntaxException("Expecting a matcher :contains and not a" + matcherArgument.getClass());
        }
        if (!((TagArgument)matcherArgument).getTag().equals(":contains")) {
            throw new SyntaxException("Expecting a matcher :contains. Matcher " + ((TagArgument) matcherArgument).getTag() + " is currently not supported.");
        }
        matcher = (TagArgument) matcherArgument;
    }

    private void retrieveMatchValues(Iterator<Argument> matchingSpecifications) throws SyntaxException {
        if (!matchingSpecifications.hasNext()) {
            throw new SyntaxException("Matcher :contains should be followed by a StringList");
        }
        Argument matchValues = matchingSpecifications.next();
        if (! (matchValues instanceof StringListArgument)) {
            throw new SyntaxException("Matcher :contains should be followed by a StringList and not a " + matchValues.getClass());
        }
        valuesToBeMatched = (StringListArgument) matchValues;
    }

    private void assureNoMoreArguments(Iterator<Argument> matchingSpecifications) throws SyntaxException {
        if (matchingSpecifications.hasNext()) {
            throw new SyntaxException("Too many arguments for Body test");
        }
    }

}
