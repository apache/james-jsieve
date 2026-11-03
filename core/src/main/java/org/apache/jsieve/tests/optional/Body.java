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
        if (args.hasTests()) {
            throw ctx.getCoordinate().syntaxException("Found unexpected tests");
        }
        // Tagged arguments come in any order before the key list (RFC 5228 section 2.6.2, RFC 5173 section 5)
        TagArgument transformationTag = null;
        StringListArgument contentTypesArgument = null;
        TagArgument matcherTag = null;
        Iterator<Argument> arguments = args.getArgumentList().iterator();
        Argument argument = nextArgument(arguments);
        while (argument instanceof TagArgument) {
            TagArgument tag = (TagArgument) argument;
            if (isTransformation(tag)) {
                if (transformationTag != null) {
                    throw new SyntaxException("Only one transformation is allowed. See RFC-5173 section 5.");
                }
                transformationTag = tag;
                if (tag.getTag().equals(CONTENT)) {
                    contentTypesArgument = retrieveContentTypes(arguments);
                }
            } else {
                TagArgument matcherCandidate = retrieveMatcher(tag);
                if (matcherTag != null) {
                    throw new SyntaxException("Only one matcher is allowed. See RFC-5173 section 5.");
                }
                matcherTag = matcherCandidate;
            }
            argument = nextArgument(arguments);
        }
        if (matcherTag == null && argument != null) {
            throw new SyntaxException("Expecting a matcher :contains and not a " + argument.getClass());
        }
        if (matcherTag == null) {
            throw new SyntaxException("Expecting a matcher :contains");
        }
        valuesToBeMatched = retrieveMatchValues(argument);
        assureNoMoreArguments(arguments);
        // by default transformation should be :text
        transformation = transformationTag != null ? transformationTag : new TagArgument(new Token(0, TEXT));
        contentTypes = contentTypesArgument;
        matcher = matcherTag;
    }

    private boolean isTransformation(TagArgument tag) {
        return tag.getTag().equals(TEXT) || tag.getTag().equals(RAW) || tag.getTag().equals(CONTENT);
    }

    private Argument nextArgument(Iterator<Argument> arguments) {
        if (arguments.hasNext()) {
            return arguments.next();
        }
        return null;
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

    private StringListArgument retrieveContentTypes(Iterator<Argument> matchingSpecifications) throws SyntaxException {
        if (!matchingSpecifications.hasNext()) {
            throw new SyntaxException("Expecting the list of content types following :content");
        }
        Argument contentTypesArgument = matchingSpecifications.next();
        if (! (contentTypesArgument instanceof StringListArgument)) {
            throw new SyntaxException("Expecting a String list to specify content types and not a" + contentTypesArgument.getClass());
        }
        return (StringListArgument) contentTypesArgument;
    }

    private TagArgument retrieveMatcher(TagArgument matcherArgument) throws SyntaxException {
        if (!matcherArgument.getTag().equals(":contains")) {
            throw new SyntaxException("Expecting a matcher :contains. Matcher " + matcherArgument.getTag() + " is currently not supported.");
        }
        return matcherArgument;
    }

    private StringListArgument retrieveMatchValues(Argument matchValues) throws SyntaxException {
        if (matchValues == null) {
            throw new SyntaxException("Matcher :contains should be followed by a StringList");
        }
        if (! (matchValues instanceof StringListArgument)) {
            throw new SyntaxException("Matcher :contains should be followed by a StringList and not a " + matchValues.getClass());
        }
        return (StringListArgument) matchValues;
    }

    private void assureNoMoreArguments(Iterator<Argument> matchingSpecifications) throws SyntaxException {
        if (matchingSpecifications.hasNext()) {
            throw new SyntaxException("Too many arguments for Body test");
        }
    }

}
