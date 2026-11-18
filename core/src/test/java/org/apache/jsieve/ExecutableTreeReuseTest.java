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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.jsieve.exception.SieveException;
import org.apache.jsieve.mail.ActionDiscard;
import org.apache.jsieve.mail.ActionFileInto;
import org.apache.jsieve.mail.ActionKeep;
import org.apache.jsieve.parser.SieveNode;
import org.apache.jsieve.parser.generated.Node;
import org.apache.jsieve.utils.JUnitUtils;
import org.apache.jsieve.utils.SieveMailAdapter;
import org.junit.Test;

/**
 * A parsed script is evaluated for many mails, possibly at once: what
 * evaluation runs is built once and shared.
 */
public class ExecutableTreeReuseTest {

    private static final String SCRIPT = "require \"fileinto\";"
        + " if header :contains \"Subject\" \"holidays\" { discard; }"
        + " elsif anyof (header :is \"X-List\" \"dev\", address :domain \"From\" \"apache.org\") { fileinto \"dev\"; }";

    @Test
    public void theTreeShouldBeBuiltOnceAtParse() throws Exception {
        SieveFactory factory = new ConfigurationManager().build();
        Node node = factory.parse(script(SCRIPT));
        Object executable = ((SieveNode) node).getExecutable();

        factory.evaluate(mail("Gone for holidays", "dev"), node);
        factory.evaluate(mail("Hello", "dev"), node);

        assertThat(executable).isInstanceOf(Block.class);
        assertThat(((SieveNode) node).getExecutable()).isSameAs(executable);
    }

    @Test
    public void theSharedTreeShouldNotBeModifiable() throws Exception {
        SieveFactory factory = new ConfigurationManager().build();
        Block block = (Block) ((SieveNode) factory.parse(script(SCRIPT))).getExecutable();
        Command command = block.getChildren().getChildren().get(1);

        assertThatThrownBy(() -> block.getChildren().getChildren().clear())
            .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> command.getArguments().getArgumentList().clear())
            .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> command.getArguments().getTestList().getTests().clear())
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void eachEvaluationShouldGetItsOwnResult() throws Exception {
        SieveFactory factory = new ConfigurationManager().build();
        Node node = factory.parse(script(SCRIPT));

        SieveMailAdapter holidays = mail("Gone for holidays", "none");
        SieveMailAdapter dev = mail("Hello", "dev");
        SieveMailAdapter other = mail("Hello", "none");
        factory.evaluate(holidays, node);
        factory.evaluate(dev, node);
        factory.evaluate(other, node);

        assertThat(holidays.getActions()).singleElement().isInstanceOf(ActionDiscard.class);
        assertThat(dev.getActions()).singleElement().isInstanceOf(ActionFileInto.class);
        assertThat(other.getActions()).singleElement().isInstanceOf(ActionKeep.class);
    }

    @Test
    public void concurrentEvaluationsShouldNotInterfere() throws Exception {
        SieveFactory factory = new ConfigurationManager().build();
        Node node = factory.parse(script(SCRIPT));
        ExecutorService executor = Executors.newFixedThreadPool(8);
        try {
            List<Future<Class<?>>> results = new ArrayList<>();
            for (int i = 0; i < 2_000; i++) {
                final int kind = i % 3;
                results.add(executor.submit(() -> {
                    SieveMailAdapter mail = kind == 0 ? mail("Gone for holidays", "none")
                        : kind == 1 ? mail("Hello", "dev") : mail("Hello", "none");
                    factory.evaluate(mail, node);
                    assertThat(mail.getActions()).hasSize(1);
                    return mail.getActions().get(0).getClass();
                }));
            }
            for (int i = 0; i < results.size(); i++) {
                Class<?> expected = i % 3 == 0 ? ActionDiscard.class : i % 3 == 1 ? ActionFileInto.class : ActionKeep.class;
                assertThat(results.get(i).get()).isEqualTo(expected);
            }
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    public void aNodeParsedWithoutValidationShouldBuildItsTreeAtTheFirstEvaluation() throws Exception {
        Node node = new org.apache.jsieve.parser.generated.SieveParser(script("discard;"), "UTF-8").start();
        SieveFactory factory = new ConfigurationManager().build();
        SieveMailAdapter mail = mail("Hello", "none");

        factory.evaluate(mail, node);

        assertThat(((SieveNode) node).getExecutable()).isInstanceOf(Block.class);
        assertThat(mail.getActions()).singleElement().isInstanceOf(ActionDiscard.class);
    }

    private static SieveMailAdapter mail(String subject, String list) throws Exception {
        SieveMailAdapter mail = (SieveMailAdapter) JUnitUtils.createMail();
        mail.getMessage().setHeader("Subject", subject);
        mail.getMessage().setHeader("X-List", list);
        mail.getMessage().setHeader("From", "someone@example.com");
        return mail;
    }

    private static ByteArrayInputStream script(String script) {
        return new ByteArrayInputStream(script.getBytes(StandardCharsets.UTF_8));
    }
}
