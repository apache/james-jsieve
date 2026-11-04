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

import org.junit.Test;

public class SieveMailAdapterTest {

    @Test
    public void getMatchingHeaderShouldReturnEachFieldOnceWhenNamesDifferByCase() throws Exception {
        SieveMailAdapter mail = (SieveMailAdapter) JUnitUtils.createMail();
        mail.getMessage().addHeader("From", "a@example.com");
        mail.getMessage().addHeader("FROM", "b@example.com");

        assertThat(mail.getMatchingHeader("from")).containsExactlyInAnyOrder("a@example.com", "b@example.com");
    }

    @Test
    public void getMatchingHeaderShouldIgnoreOnlyTheAsciiCase() throws Exception {
        SieveMailAdapter mail = (SieveMailAdapter) JUnitUtils.createMail();
        mail.getMessage().addHeader("\u00c4", "upper");
        mail.getMessage().addHeader("\u00e4", "lower");

        assertThat(mail.getMatchingHeader("\u00c4")).containsExactly("upper");
    }

}
