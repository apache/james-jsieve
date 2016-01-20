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

import org.apache.jsieve.exception.SyntaxException;
import org.apache.jsieve.mail.ActionKeep;
import org.apache.jsieve.mail.optional.ActionVacation;
import org.apache.jsieve.utils.JUnitUtils;
import org.apache.jsieve.utils.SieveMailAdapter;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class VacationTest {

    private SieveMailAdapter sieveMailAdapter;

    @Before
    public void setUp() {
        sieveMailAdapter = mock(SieveMailAdapter.class);
    }

    @Test(expected = SyntaxException.class)
    public void NoArgumentsShouldThrow() throws Exception {
        String script = "vacation;";

        JUnitUtils.interpret(sieveMailAdapter, script);
    }

    @Test(expected = SyntaxException.class)
    public void unknownTagsShouldThrow() throws Exception {
        String script = "vacation :unknown;";

        JUnitUtils.interpret(sieveMailAdapter, script);
    }

    @Test(expected = SyntaxException.class)
    public void uncompletedDaysTagShouldThrow() throws Exception {
        String script = "vacation \"reason\" :days;";

        JUnitUtils.interpret(sieveMailAdapter, script);
    }

    @Test(expected = SyntaxException.class)
    public void daysTagWithStringShouldThrow() throws Exception {
        String script = "vacation \"reason\" :days \"string\";";

        JUnitUtils.interpret(sieveMailAdapter, script);
    }

    @Test(expected = SyntaxException.class)
    public void uncompletedSubjectTagShouldThrow() throws Exception {
        String script = "vacation \"reason\" :subject;";

        JUnitUtils.interpret(sieveMailAdapter, script);
    }

    @Test(expected = SyntaxException.class)
    public void uncompletedFromTagShouldThrow() throws Exception {
        String script = "vacation \"reason\" :from;";

        JUnitUtils.interpret(sieveMailAdapter, script);
    }

    @Test(expected = SyntaxException.class)
    public void uncompletedAddressesTagShouldThrow() throws Exception {
        String script = "vacation \"reason\" :addresses;";

        JUnitUtils.interpret(sieveMailAdapter, script);
    }

    @Test(expected = SyntaxException.class)
    public void uncompletedHandleTagShouldThrow() throws Exception {
        String script = "vacation \"reason\" :handle;";

        JUnitUtils.interpret(sieveMailAdapter, script);
    }

    @Test(expected = SyntaxException.class)
    public void uncompletedMimeTagShouldThrow() throws Exception {
        String script = "vacation :mime;";

        JUnitUtils.interpret(sieveMailAdapter, script);
    }

    @Test
    public void vacationShouldWork() throws Exception {
        String script = "vacation \"reason\";";

        JUnitUtils.interpret(sieveMailAdapter, script);

        verify(sieveMailAdapter, times(2)).setContext(any(SieveContext.class));
        verify(sieveMailAdapter).addAction(ActionVacation.builder().reason("reason").build());
        verify(sieveMailAdapter, times(2)).addAction(any(ActionKeep.class));
        verify(sieveMailAdapter).executeActions();
        verifyNoMoreInteractions(sieveMailAdapter);
    }

    @Test
    public void daysShouldWork() throws Exception {
        String script = "vacation \"reason\" :days 3;";

        JUnitUtils.interpret(sieveMailAdapter, script);

        verify(sieveMailAdapter, times(2)).setContext(any(SieveContext.class));
        verify(sieveMailAdapter).addAction(ActionVacation.builder().reason("reason").duration(3).build());
        verify(sieveMailAdapter, times(2)).addAction(any(ActionKeep.class));
        verify(sieveMailAdapter).executeActions();
        verifyNoMoreInteractions(sieveMailAdapter);
    }

    @Test
    public void subjectShouldWork() throws Exception {
        String script = "vacation \"reason\" :subject \"any\";";

        JUnitUtils.interpret(sieveMailAdapter, script);

        verify(sieveMailAdapter, times(2)).setContext(any(SieveContext.class));
        verify(sieveMailAdapter).addAction(ActionVacation.builder().reason("reason").subject("any").build());
        verify(sieveMailAdapter, times(2)).addAction(any(ActionKeep.class));
        verify(sieveMailAdapter).executeActions();
        verifyNoMoreInteractions(sieveMailAdapter);
    }

    @Test
    public void fromShouldWork() throws Exception {
        String script = "vacation \"reason\" :from \"benwa@apache.org\";";

        JUnitUtils.interpret(sieveMailAdapter, script);

        verify(sieveMailAdapter, times(2)).setContext(any(SieveContext.class));
        verify(sieveMailAdapter).addAction(ActionVacation.builder().reason("reason").from("benwa@apache.org").build());
        verify(sieveMailAdapter, times(2)).addAction(any(ActionKeep.class));
        verify(sieveMailAdapter).executeActions();
        verifyNoMoreInteractions(sieveMailAdapter);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void addressesShouldWork() throws Exception {
        String script = "vacation \"reason\" :addresses \"benwa@apache.org\";";

        JUnitUtils.interpret(sieveMailAdapter, script);

        verify(sieveMailAdapter, times(2)).setContext(any(SieveContext.class));
        verify(sieveMailAdapter).addAction(ActionVacation.builder().reason("reason").build());
        verify(sieveMailAdapter, times(2)).addAction(any(ActionKeep.class));
        verify(sieveMailAdapter).executeActions();
        verifyNoMoreInteractions(sieveMailAdapter);
    }

    @Test
    public void mimeShouldWork() throws Exception {
        String script = "vacation :mime \"reason\";";

        JUnitUtils.interpret(sieveMailAdapter, script);

        verify(sieveMailAdapter, times(2)).setContext(any(SieveContext.class));
        verify(sieveMailAdapter).addAction(ActionVacation.builder().mime("reason").build());
        verify(sieveMailAdapter, times(2)).addAction(any(ActionKeep.class));
        verify(sieveMailAdapter).executeActions();
        verifyNoMoreInteractions(sieveMailAdapter);
    }

    @Test
    public void handleShouldWork() throws Exception {
        String script = "vacation \"reason\" :handle \"plop\";";

        JUnitUtils.interpret(sieveMailAdapter, script);

        verify(sieveMailAdapter, times(2)).setContext(any(SieveContext.class));
        verify(sieveMailAdapter).addAction(ActionVacation.builder().reason("reason").handle("plop").build());
        verify(sieveMailAdapter, times(2)).addAction(any(ActionKeep.class));
        verify(sieveMailAdapter).executeActions();
        verifyNoMoreInteractions(sieveMailAdapter);
    }

    @Test(expected = SyntaxException.class)
    public void reasonAndMimeTogetherShouldThrow() throws Exception {
        String script = "vacation \"reason\" :mime \"plop\";";

        JUnitUtils.interpret(sieveMailAdapter, script);
    }

    @Test(expected = SyntaxException.class)
    public void reasonAsStringListWithMultipleEntriesShouldThrow() throws Exception {
        String script = "vacation [\"reason\",\"too much\"];";

        JUnitUtils.interpret(sieveMailAdapter, script);
    }


    @Test(expected = SyntaxException.class)
    public void aNumberAsReasonShouldThrow() throws Exception {
        String script = "vacation 3;";

        JUnitUtils.interpret(sieveMailAdapter, script);
    }

    @Test
    public void reasonAsStringListWithOneEntryShouldWork() throws Exception {
        String script = "vacation [\"reason\"];";

        JUnitUtils.interpret(sieveMailAdapter, script);

        verify(sieveMailAdapter, times(2)).setContext(any(SieveContext.class));
        verify(sieveMailAdapter).addAction(ActionVacation.builder().reason("reason").build());
        verify(sieveMailAdapter, times(2)).addAction(any(ActionKeep.class));
        verify(sieveMailAdapter).executeActions();
        verifyNoMoreInteractions(sieveMailAdapter);
    }

}
