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

package org.apache.jsieve.mail.optional;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.Test;

public class ActionVacationTest {

    private static ActionVacation.ActionVacationBuilder full() {
        return ActionVacation.builder()
            .subject("subject")
            .from("from@example.com")
            .addresses(Arrays.asList("me@example.com"))
            .handle("handle")
            .reason("reason")
            .duration(3);
    }

    @Test
    public void actionsWithTheSameFieldsShouldBeEqual() throws Exception {
        assertThat(full().build()).isEqualTo(full().build());
        assertThat(full().build().hashCode()).isEqualTo(full().build().hashCode());
    }

    @Test
    public void actionsWithADifferentFieldShouldNotBeEqual() throws Exception {
        ActionVacation action = full().build();

        assertThat(action).isNotEqualTo(full().subject("other").build());
        assertThat(action).isNotEqualTo(full().from("other@example.com").build());
        assertThat(action).isNotEqualTo(full().addresses(Arrays.asList("other@example.com")).build());
        assertThat(action).isNotEqualTo(full().handle("other").build());
        assertThat(action).isNotEqualTo(full().reason("other").build());
        assertThat(action).isNotEqualTo(full().reason(null).mime("mime").build());
        assertThat(action).isNotEqualTo(full().duration(4).build());
    }

}
