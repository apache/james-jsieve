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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

/**
 * SieveFactory#getExtensions lists the capability strings a require accepts,
 * as the ManageSieve SIEVE capability gives them (RFC 5804 section 1.7).
 */
public class ExtensionsTest {

    @Test
    public void comparatorShouldBeListedWithTheComparatorPrefix() throws Exception {
        SieveFactory factory = new ConfigurationManager().build();

        assertThat(factory.getExtensions())
            .contains("comparator-i;ascii-numeric")
            .doesNotContain("i;ascii-numeric");
    }

    @Test
    public void everyListedExtensionShouldBeAcceptedByRequire() throws Exception {
        SieveFactory factory = new ConfigurationManager().build();

        for (String capability : factory.getExtensions()) {
            String script = "require \"" + capability + "\";";
            factory.parse(new ByteArrayInputStream(script.getBytes(StandardCharsets.UTF_8)));
        }
    }

}