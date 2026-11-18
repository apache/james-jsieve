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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.jsieve.exception.SieveException;
import org.apache.jsieve.mail.ActionKeep;
import org.apache.jsieve.mail.ActionRedirect;
import org.apache.jsieve.utils.JUnitUtils;
import org.apache.jsieve.utils.SieveMailAdapter;
import org.junit.Test;

/**
 * Each redirect sends a copy of the mail: an evaluation redirects to a
 * bounded number of distinct addresses, and once to each.
 */
public class RedirectLimitTest {

    @Test
    public void redirectsUpToTheLimitShouldBeExecuted() throws Exception {
        RecordingMailAdapter mail = new RecordingMailAdapter();

        factoryWithMaxRedirects(3).interpret(mail, script(redirects(3)));

        assertThat(mail.executed).isTrue();
        assertThat(mail.getActions()).hasSize(3).allMatch(ActionRedirect.class::isInstance);
    }

    @Test
    public void redirectsBeyondTheLimitShouldFailTheEvaluationWithoutExecutingAnything() throws Exception {
        RecordingMailAdapter mail = new RecordingMailAdapter();

        assertThatThrownBy(() -> factoryWithMaxRedirects(3).interpret(mail, script(redirects(4))))
            .isInstanceOf(SieveException.class)
            .hasMessageContaining("Too many redirect");
        assertThat(mail.executed).isFalse();
    }

    @Test
    public void theDefaultLimitShouldApply() throws Exception {
        RecordingMailAdapter mail = new RecordingMailAdapter();
        String script = redirects(SieveFactory.DEFAULT_MAX_REDIRECTS + 1);

        assertThatThrownBy(() -> new ConfigurationManager().build().interpret(mail, script(script)))
            .isInstanceOf(SieveException.class)
            .hasMessageContaining("Too many redirect");
        assertThat(mail.executed).isFalse();
    }

    @Test
    public void aRepeatedAddressShouldBeRedirectedToOnce() throws Exception {
        SieveMailAdapter mail = (SieveMailAdapter) JUnitUtils.createMail();
        String script = "redirect \"target@example.org\";".repeat(1_000)
            + "redirect \"target@EXAMPLE.org\";";

        factoryWithMaxRedirects(1).interpret(mail, script(script));

        assertThat(mail.getActions()).singleElement().isInstanceOf(ActionRedirect.class);
        assertThat(((ActionRedirect) mail.getActions().get(0)).getAddress()).isEqualTo("target@example.org");
    }

    @Test
    public void localPartsDifferingByCaseShouldBeDistinctAddresses() throws Exception {
        SieveMailAdapter mail = (SieveMailAdapter) JUnitUtils.createMail();

        factoryWithMaxRedirects(2).interpret(mail,
            script("redirect \"target@example.org\"; redirect \"TARGET@example.org\";"));

        assertThat(mail.getActions()).hasSize(2).allMatch(ActionRedirect.class::isInstance);
    }

    @Test
    public void theLimitShouldBeCountedPerEvaluation() throws Exception {
        SieveFactory factory = factoryWithMaxRedirects(2);

        for (int i = 0; i < 3; i++) {
            SieveMailAdapter mail = (SieveMailAdapter) JUnitUtils.createMail();
            factory.interpret(mail, script(redirects(2)));
            assertThat(mail.getActions()).hasSize(2);
        }
    }

    @Test
    public void aZeroLimitShouldForbidRedirectsOnly() throws Exception {
        SieveMailAdapter mail = (SieveMailAdapter) JUnitUtils.createMail();

        factoryWithMaxRedirects(0).interpret(mail, script("keep;"));

        assertThat(mail.getActions()).singleElement().isInstanceOf(ActionKeep.class);
        assertThatThrownBy(() -> factoryWithMaxRedirects(0)
                .interpret(JUnitUtils.createMail(), script(redirects(1))))
            .isInstanceOf(SieveException.class);
    }

    @Test
    public void aNegativeLimitShouldBeRejected() {
        assertThatThrownBy(() -> new ConfigurationManager().setMaxRedirects(-1))
            .isInstanceOf(IllegalArgumentException.class);
    }

    private static class RecordingMailAdapter extends SieveMailAdapter {
        private boolean executed;

        RecordingMailAdapter() {
            super(((SieveMailAdapter) JUnitUtils.createMail()).getMessage());
        }

        @Override
        public void executeActions() throws SieveException {
            executed = true;
            super.executeActions();
        }
    }

    private static String redirects(int count) {
        return IntStream.range(0, count)
            .mapToObj(i -> "redirect \"target" + i + "@example.org\";")
            .collect(Collectors.joining(" "));
    }

    private static SieveFactory factoryWithMaxRedirects(int maxRedirects) throws Exception {
        ConfigurationManager configurationManager = new ConfigurationManager();
        configurationManager.setMaxRedirects(maxRedirects);
        return configurationManager.build();
    }

    private static ByteArrayInputStream script(String script) {
        return new ByteArrayInputStream(script.getBytes(StandardCharsets.UTF_8));
    }
}
