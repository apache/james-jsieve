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
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.jsieve.comparators.AsciiCasemap;
import org.apache.jsieve.comparators.Contains;
import org.apache.jsieve.comparators.Equals;
import org.apache.jsieve.comparators.Matches;
import org.apache.jsieve.exception.InternetAddressException;
import org.apache.jsieve.exception.SieveException;
import org.apache.jsieve.mail.ActionDiscard;
import org.apache.jsieve.mail.ActionKeep;
import org.apache.jsieve.mail.MailUtils;
import org.apache.jsieve.mail.SieveMailException;
import org.apache.jsieve.utils.JUnitUtils;
import org.apache.jsieve.utils.SieveMailAdapter;
import org.junit.Test;

/**
 * The comparisons of an evaluation are bounded: a script cannot keep the
 * evaluating thread busy for hours on a large mail.
 */
public class EvaluationBudgetTest {

    private static final String CONTAINS_SCRIPT = "if header :contains \"Subject\" \"holidays\" { discard; }";

    @Test(timeout = 10_000)
    public void aPathologicalMatchOnALargeHeaderShouldFailOnceTheBudgetIsSpent() throws Exception {
        SieveMailAdapter mail = mailWithSubject("a".repeat(2_000_000));
        String script = "if header :matches \"Subject\" \"*" + "a".repeat(10_000) + "b*\" { discard; }";

        assertThatThrownBy(() -> factoryWithBudget(100_000_000).interpret(mail, script(script)))
            .isInstanceOf(SieveException.class)
            .hasMessageContaining("budget");
        assertThat(mail.getActions()).isEmpty();
    }

    @Test(timeout = 10_000)
    public void aPathologicalContainsOnALargeHeaderShouldBeEvaluatedInLinearTime() throws Exception {
        SieveMailAdapter mail = mailWithSubject("a".repeat(2_000_000));
        String script = "if header :contains \"Subject\" \"" + "a".repeat(10_000) + "b\" { discard; }";

        JUnitUtils.interpret(mail, script);

        assertThat(mail.getActions()).singleElement().isInstanceOf(ActionKeep.class);
    }

    @Test(timeout = 30_000)
    public void manyContainsKeysOnAMultiMegabyteHeaderShouldBeEvaluated() throws Exception {
        SieveMailAdapter mail = mailWithSubject("lorem ipsum dolor sit amet ".repeat(170_000) + "holidays");
        String script = "if header :contains \"Subject\" [" + keys(100) + ", \"holidays\"] { discard; }";

        JUnitUtils.interpret(mail, script);

        assertThat(mail.getActions()).singleElement().isInstanceOf(ActionDiscard.class);
    }

    @Test(timeout = 30_000)
    public void manyMatchesKeysOnAMultiMegabyteHeaderShouldBeEvaluated() throws Exception {
        SieveMailAdapter mail = mailWithSubject("lorem ipsum dolor sit amet ".repeat(170_000) + "holidays");
        String script = "if header :matches \"Subject\" [" + keys(100).replace("\"key", "\"*key") + ", \"*holidays\"] { discard; }";

        JUnitUtils.interpret(mail, script);

        assertThat(mail.getActions()).singleElement().isInstanceOf(ActionDiscard.class);
    }

    @Test(timeout = 30_000)
    public void manyAsciiNumericKeysOnAMegabyteOfDigitsShouldBeEvaluatedInLinearTime() throws Exception {
        String digits = "7".repeat(1_000_000);
        SieveMailAdapter mail = mailWithSubject(digits);
        String keys = IntStream.range(0, 100)
            .mapToObj(i -> "\"" + "7".repeat(1_000) + i + "\"")
            .collect(Collectors.joining(", "));
        String script = "require \"comparator-i;ascii-numeric\";"
            + " if header :comparator \"i;ascii-numeric\" :is \"Subject\" [" + keys + ", \"000" + digits + "\"] { discard; }";

        JUnitUtils.interpret(mail, script);

        assertThat(mail.getActions()).singleElement().isInstanceOf(ActionDiscard.class);
    }

    @Test
    public void theSearchOfTheBodyByTheMailAdapterShouldBeCharged() throws Exception {
        SieveMailAdapter mail = mailWithBody("lorem ipsum dolor sit amet ".repeat(40_000));
        String test = "if body :contains \"absent\" { discard; }";
        String script = "require \"body\"; " + test.repeat(100);

        assertThatThrownBy(() -> factoryWithBudget(10_000_000).interpret(mail, script(script)))
            .isInstanceOf(SieveException.class)
            .hasMessageContaining("budget");
        assertThat(mail.getActions()).isEmpty();
    }

    @Test
    public void aSearchOfTheBodyWithinTheBudgetShouldBeEvaluated() throws Exception {
        SieveMailAdapter mail = mailWithBody("Gone for holidays");
        String script = "require \"body\"; if body :contains [\"absent\", \"HOLIDAYS\"] { discard; }";

        factoryWithBudget(10_000).interpret(mail, script(script));

        assertThat(mail.getActions()).singleElement().isInstanceOf(ActionDiscard.class);
    }

    @Test
    public void theParseOfAnAddressHeaderShouldBeCharged() throws Exception {
        SieveMailAdapter mail = mailWithFrom("(" + "a".repeat(1_000_000) + ") <");
        String script = "if address :all \"From\" \"key\" { discard; }";

        assertThatThrownBy(() -> factoryWithBudget(1_000_000).interpret(mail, script(script)))
            .isInstanceOf(SieveException.class)
            .hasMessageContaining("budget");
    }

    @Test
    public void anAddressHeaderShouldBeParsedOnceWhateverTheNumberOfKeys() throws Exception {
        SieveMailAdapter mail = mailWithFrom("(" + "a".repeat(100_000) + ") <");
        String script = "if address :all \"From\" [" + keys(10_000) + "] { discard; }";

        factoryWithBudget(1_000_000).interpret(mail, script(script));

        assertThat(mail.getActions()).singleElement().isInstanceOf(ActionKeep.class);
    }

    @Test
    public void aNameMatchingNoHeaderShouldNotBeParsed() throws Exception {
        ParseCountingMailAdapter mail = new ParseCountingMailAdapter();
        mail.getMessage().setHeader("Subject", "(" + "a".repeat(1_000_000) + ") <");
        // U+017F folds to s under String#equalsIgnoreCase, not under i;ascii-casemap
        String script = "if address :all [\"\u017Fubject\", \"\u017FUBJECT\", \"X-Absent\"] \"key\" { discard; }";

        factoryWithBudget(100_000).interpret(mail, script(script));

        assertThat(mail.parses).isZero();
        assertThat(mail.getActions()).singleElement().isInstanceOf(ActionKeep.class);
    }

    @Test
    public void theAsciiCaseSpellingsOfANameShouldBeParsedOnce() throws Exception {
        ParseCountingMailAdapter mail = new ParseCountingMailAdapter();
        mail.getMessage().setHeader("From", "user@example.com");
        String script = "if address :all [\"From\", \"FROM\", \"from\", \"fRoM\"] \"key\" { discard; }";

        factoryWithBudget(100_000).interpret(mail, script(script));

        assertThat(mail.parses).isEqualTo(1);
    }

    @Test
    public void theHeaderLookupsOfExistsShouldBeCharged() throws Exception {
        SieveMailAdapter mail = mailWithHeaders(10_000);
        String script = "if exists [" + headerNames(10_000) + "] { discard; }";

        assertThatThrownBy(() -> factoryWithBudget(10_000_000).interpret(mail, script(script)))
            .isInstanceOf(SieveException.class)
            .hasMessageContaining("budget");
    }

    @Test
    public void theHeaderLookupsOfHeaderShouldBeCharged() throws Exception {
        SieveMailAdapter mail = mailWithHeaders(10_000);
        String script = "if header :is [" + headerNames(10_000).replace("X-Header-", "X-Absent-") + "] \"key\" { discard; }";

        assertThatThrownBy(() -> factoryWithBudget(10_000_000).interpret(mail, script(script)))
            .isInstanceOf(SieveException.class)
            .hasMessageContaining("budget");
    }

    @Test(timeout = 10_000)
    public void theSpellingsOfANameShouldBeLookedUpLinearlyWhenTheAdapterIgnoresCase() throws Exception {
        SieveMailAdapter mail = mailWithSpellings(new CaseInsensitiveMailAdapter(), "XXXXXXXXXXXXXX", 10_000);

        assertThat(MailUtils.getMatchingHeader(mail, "xxxxxxxxxxxxxx")).hasSize(10_000);
        factoryWithBudget(10_000_000).interpret(mail, script("if exists \"XXXXXXXXXXXXXX\" { discard; }"));
        assertThat(mail.getActions()).singleElement().isInstanceOf(ActionDiscard.class);
    }

    @Test
    public void theSpellingsOfANameShouldEachBeLookedUpWhenTheAdapterMatchesTheExactName() throws Exception {
        SieveMailAdapter mail = mailWithSpellings((SieveMailAdapter) JUnitUtils.createMail(), "XXXXXXXXXXXXXX", 100);

        assertThat(MailUtils.getMatchingHeader(mail, "xxxxxxxxxxxxxx")).hasSize(100).doesNotHaveDuplicates();
    }

    @Test
    public void spellingsSharingAValueShouldEachBeLookedUpWhenTheAdapterMatchesTheExactName() throws Exception {
        SieveMailAdapter mail = (SieveMailAdapter) JUnitUtils.createMail();
        mail.getMessage().addHeader("X-Spam", "yes");
        mail.getMessage().addHeader("x-spam", "yes");
        mail.getMessage().addHeader("X-SPAM", "no");

        assertThat(MailUtils.getMatchingHeader(mail, "x-spam")).containsExactlyInAnyOrder("yes", "yes", "no");
    }

    @Test
    public void spellingsSharingAValueShouldBeLookedUpOnceWhenTheAdapterIgnoresCase() throws Exception {
        SieveMailAdapter mail = new CaseInsensitiveMailAdapter();
        mail.getMessage().addHeader("X-Spam", "yes");
        mail.getMessage().addHeader("x-spam", "yes");
        mail.getMessage().addHeader("X-SPAM", "no");

        assertThat(MailUtils.getMatchingHeader(mail, "x-spam")).containsExactlyInAnyOrder("yes", "yes", "no");
    }

    @Test
    public void aHeaderNameShouldBeLookedUpOncePerEvaluation() throws Exception {
        SieveMailAdapter mail = mailWithHeaders(10_000);
        String script = "if exists [" + String.join(", ", Collections.nCopies(10_000, "\"X-Header-1\"")) + "] { discard; }";

        factoryWithBudget(10_000_000).interpret(mail, script(script));

        assertThat(mail.getActions()).singleElement().isInstanceOf(ActionDiscard.class);
    }

    @Test
    public void theGlobMatcherShouldBeChargedTheCharactersItComparesAgain() throws Exception {
        // Each of the ~1000 attempts compares up to 100 characters before failing
        String string = "a".repeat(1_000);
        String glob = "*" + "a".repeat(99) + "b*";

        assertThatThrownBy(() -> new AsciiCasemap().matches(string, glob, contextWithBudget(50_000)))
            .isInstanceOf(SieveException.class)
            .hasMessageContaining("budget");
        assertThat(new AsciiCasemap().matches(string, glob, contextWithBudget(150_000))).isFalse();
    }

    @Test
    public void aStarScanningAValueShouldBeChargedLinearly() throws Exception {
        String string = "a".repeat(100_000) + "b";

        // The value up front, then one step per character the star swallows
        assertThat(new AsciiCasemap().matches(string, "*b", contextWithBudget(200_010))).isTrue();
    }

    @Test
    public void theEndAfterTheLastStarShouldBeComparedOnce() throws Exception {
        String string = "a".repeat(100_000);
        String glob = "*" + "a".repeat(999) + "b";

        // The lengths up front only: the end is not searched for
        assertThat(new AsciiCasemap().matches(string, glob, contextWithBudget(101_003))).isFalse();
        assertThat(new AsciiCasemap().matches(string + "B", glob, contextWithBudget(101_004))).isTrue();
    }

    private static SieveContext contextWithBudget(long budget) throws Exception {
        ConfigurationManager configurationManager = new ConfigurationManager();
        return new BaseSieveContext(configurationManager.getCommandManager(),
            configurationManager.getComparatorManager(), configurationManager.getTestManager(), budget);
    }

    @Test
    public void aComparatorWithoutCostShouldBeChargedTheWorstCase() throws Exception {
        ConfigurationManager configurationManager = new ConfigurationManager();
        SieveContext context = new BaseSieveContext(configurationManager.getCommandManager(),
            configurationManager.getComparatorManager(), configurationManager.getTestManager(), 1_000);
        Contains naiveContains = String::contains;
        Matches naiveMatches = (string, glob) -> string.equals(glob);
        Equals naiveEquals = String::equals;

        assertThatThrownBy(() -> naiveContains.contains("a".repeat(100), "b".repeat(10), context))
            .isInstanceOf(SieveException.class)
            .hasMessageContaining("budget");
        assertThatThrownBy(() -> naiveMatches.matches("a".repeat(100), "b".repeat(10), context))
            .isInstanceOf(SieveException.class)
            .hasMessageContaining("budget");
        assertThatThrownBy(() -> naiveEquals.equals("a".repeat(100), "b".repeat(10), context))
            .isInstanceOf(SieveException.class)
            .hasMessageContaining("budget");
    }

    @Test
    public void comparisonsWithinTheBudgetShouldBeEvaluated() throws Exception {
        SieveMailAdapter mail = mailWithSubject("Gone for holidays");

        factoryWithBudget(70).interpret(mail, script(CONTAINS_SCRIPT));

        assertThat(mail.getActions()).singleElement().isInstanceOf(ActionDiscard.class);
    }

    @Test
    public void comparisonsBeyondTheBudgetShouldFail() throws Exception {
        SieveMailAdapter mail = mailWithSubject("Gone for holidays");

        assertThatThrownBy(() -> factoryWithBudget(20).interpret(mail, script(CONTAINS_SCRIPT)))
            .isInstanceOf(SieveException.class)
            .hasMessageContaining("budget");
    }

    @Test
    public void theBudgetShouldBeSpentAcrossTheTestsOfAnEvaluation() throws Exception {
        SieveMailAdapter mail = mailWithSubject("Gone for holidays");
        String script = CONTAINS_SCRIPT + CONTAINS_SCRIPT;

        assertThatThrownBy(() -> factoryWithBudget(70).interpret(mail, script(script)))
            .isInstanceOf(SieveException.class)
            .hasMessageContaining("budget");
    }

    @Test
    public void theBudgetShouldBeRenewedForEachEvaluation() throws Exception {
        SieveFactory factory = factoryWithBudget(70);

        for (int i = 0; i < 3; i++) {
            SieveMailAdapter mail = mailWithSubject("Gone for holidays");
            factory.interpret(mail, script(CONTAINS_SCRIPT));
            assertThat(mail.getActions()).singleElement().isInstanceOf(ActionDiscard.class);
        }
    }

    @Test
    public void aNegativeBudgetShouldBeRejected() {
        assertThatThrownBy(() -> new ConfigurationManager().setComparisonBudget(-1))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void aContextBuiltWithoutBudgetShouldNotLimitEvaluation() throws Exception {
        ConfigurationManager configurationManager = new ConfigurationManager();
        SieveContext context = new BaseSieveContext(configurationManager.getCommandManager(),
            configurationManager.getComparatorManager(), configurationManager.getTestManager());

        assertThatCode(() -> context.chargeComparison(Long.MAX_VALUE)).doesNotThrowAnyException();
    }

    private static SieveFactory factoryWithBudget(long budget) throws Exception {
        ConfigurationManager configurationManager = new ConfigurationManager();
        configurationManager.setComparisonBudget(budget);
        return configurationManager.build();
    }

    private static String keys(int count) {
        return IntStream.range(0, count)
            .mapToObj(i -> "\"key" + i + "\"")
            .collect(Collectors.joining(", "));
    }

    private static ByteArrayInputStream script(String script) {
        return new ByteArrayInputStream(script.getBytes(StandardCharsets.UTF_8));
    }

    private static SieveMailAdapter mailWithBody(String body) throws Exception {
        SieveMailAdapter mail = (SieveMailAdapter) JUnitUtils.createMail();
        mail.getMessage().setText(body);
        mail.getMessage().saveChanges();
        // copied so that JavaMail reports the size of the whole message
        return (SieveMailAdapter) JUnitUtils.copyMail(mail);
    }

    private static String headerNames(int count) {
        return IntStream.range(0, count)
            .mapToObj(i -> "\"X-Header-" + i + "\"")
            .collect(Collectors.joining(", "));
    }

    private static SieveMailAdapter mailWithFrom(String from) throws Exception {
        SieveMailAdapter mail = (SieveMailAdapter) JUnitUtils.createMail();
        mail.getMessage().setHeader("From", from);
        return mail;
    }

    private static SieveMailAdapter mailWithHeaders(int count) throws Exception {
        SieveMailAdapter mail = (SieveMailAdapter) JUnitUtils.createMail();
        for (int i = 0; i < count; i++) {
            mail.getMessage().addHeader("X-Header-" + i, "value");
        }
        return mail;
    }

    /**
     * Adds the given number of spellings of the name, differing in the case
     * of their letters, each with its own value.
     */
    private static SieveMailAdapter mailWithSpellings(SieveMailAdapter mail, String name, int count) throws Exception {
        for (int i = 0; i < count; i++) {
            StringBuilder spelling = new StringBuilder(name);
            for (int bit = 0; bit < name.length(); bit++) {
                if ((i & (1 << bit)) != 0) {
                    spelling.setCharAt(bit, Character.toLowerCase(name.charAt(bit)));
                }
            }
            mail.getMessage().addHeader(spelling.toString(), "value" + i);
        }
        return mail;
    }

    private static class ParseCountingMailAdapter extends SieveMailAdapter {
        int parses;

        ParseCountingMailAdapter() {
            super(((SieveMailAdapter) JUnitUtils.createMail()).getMessage());
        }

        @Override
        public Address[] parseAddresses(String headerName) throws SieveMailException, InternetAddressException {
            parses++;
            return super.parseAddresses(headerName);
        }
    }

    /**
     * Answers the fields of every case of the name, as JavaMail does and as
     * James's adapter relies on.
     */
    private static class CaseInsensitiveMailAdapter extends SieveMailAdapter {
        CaseInsensitiveMailAdapter() {
            super(((SieveMailAdapter) JUnitUtils.createMail()).getMessage());
        }

        @Override
        public List<String> getHeader(String name) throws SieveMailException {
            try {
                String[] values = getMessage().getHeader(name);
                return values == null ? Collections.emptyList() : Arrays.asList(values);
            } catch (javax.mail.MessagingException e) {
                throw new SieveMailException(e);
            }
        }
    }

    private static SieveMailAdapter mailWithSubject(String subject) throws Exception {
        SieveMailAdapter mail = (SieveMailAdapter) JUnitUtils.createMail();
        mail.getMessage().setHeader("Subject", subject);
        return mail;
    }
}
