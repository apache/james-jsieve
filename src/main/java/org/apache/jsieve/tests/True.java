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

import org.apache.jsieve.Arguments;
import org.apache.jsieve.SieveContext;
import org.apache.jsieve.mail.MailAdapter;

/**
 * Class True implements the True Test as defined in RFC 3028, section 5.10.
 */
public class True extends AbstractTest {

    /**
     * Constructor for True.
     */
    public True() {
        super();
    }

    /**
     * @see org.apache.jsieve.tests.AbstractTest#executeBasic(MailAdapter,
     *      Arguments, SieveContext)
     */
    protected boolean executeBasic(MailAdapter mail, Arguments arguments,
            SieveContext context) {
        return true;
    }

}
