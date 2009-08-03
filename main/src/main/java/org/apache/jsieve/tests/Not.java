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

package org.apache.jsieve.tests;

import java.util.List;

import org.apache.jsieve.Arguments;
import org.apache.jsieve.SieveContext;
import org.apache.jsieve.Test;
import org.apache.jsieve.exception.SieveException;
import org.apache.jsieve.mail.MailAdapter;

/**
 * Class Not implements the Not Test as defined in RFC 3028, section 5.8.
 */
public class Not extends AbstractTest {

    /**
     * Constructor for Not.
     */
    public Not() {
        super();
    }

    /**
     * @see org.apache.jsieve.tests.AbstractTest#executeBasic(MailAdapter,
     *      Arguments, SieveContext)
     */
    protected boolean executeBasic(MailAdapter mail, Arguments arguments,
            SieveContext context) throws SieveException {
        boolean result = true;
        List<Test> tests = arguments.getTestList().getTests();
        if (tests.size() != 1)
            throw context.getCoordinate().syntaxException(
                    new StringBuilder("Exactly 1 test permitted. Found ").append(tests.size()));
        for (Test test: tests) {
            result = result && test.isTestPassed(mail, context);
        }
        return !result;
    }

}
