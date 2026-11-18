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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.jsieve.commands.Discard;
import org.apache.jsieve.commands.Keep;
import org.apache.jsieve.comparators.AsciiCasemap;
import org.apache.jsieve.comparators.Octet;
import org.apache.jsieve.exception.LookupException;
import org.apache.jsieve.tests.False;
import org.apache.jsieve.tests.True;
import org.junit.Test;

/**
 * The managers resolve a class once, yet follow the changes of their
 * mapping and hand out a new instance on each call.
 */
public class ImplementationCacheTest {

    @Test
    public void aComparatorShouldBeANewInstanceOnEachCall() throws Exception {
        ConcurrentMap<String, String> map = new ConcurrentHashMap<>();
        map.put("i;octet", Octet.class.getName());
        ComparatorManagerImpl manager = new ComparatorManagerImpl(map);

        assertThat(manager.getComparator("i;octet"))
            .isInstanceOf(Octet.class)
            .isNotSameAs(manager.getComparator("i;octet"));
    }

    @Test
    public void aRemappedComparatorShouldResolveToItsNewClass() throws Exception {
        ConcurrentMap<String, String> map = new ConcurrentHashMap<>();
        map.put("i;octet", Octet.class.getName());
        ComparatorManagerImpl manager = new ComparatorManagerImpl(map);
        manager.getComparator("i;octet");

        map.put("i;octet", AsciiCasemap.class.getName());

        assertThat(manager.getComparator("i;octet")).isInstanceOf(AsciiCasemap.class);
        assertThat(manager.lookup("i;octet")).isEqualTo(AsciiCasemap.class);
    }

    @Test
    public void aRemappedCommandShouldResolveToItsNewClass() throws Exception {
        ConcurrentMap<String, String> map = new ConcurrentHashMap<>();
        map.put("keep", Keep.class.getName());
        CommandManagerImpl manager = new CommandManagerImpl(map);
        assertThat(manager.getCommand("KEEP")).isInstanceOf(Keep.class);

        map.put("keep", Discard.class.getName());

        assertThat(manager.getCommand("keep")).isInstanceOf(Discard.class);
    }

    @Test
    public void aRemappedTestShouldResolveToItsNewClass() throws Exception {
        ConcurrentMap<String, String> map = new ConcurrentHashMap<>();
        map.put("true", True.class.getName());
        TestManagerImpl manager = new TestManagerImpl(map);
        assertThat(manager.getTest("true")).isInstanceOf(True.class);

        map.put("true", False.class.getName());

        assertThat(manager.getTest("true")).isInstanceOf(False.class);
    }

    @Test
    public void aMissingClassShouldFailOnEachCall() {
        ConcurrentMap<String, String> map = new ConcurrentHashMap<>();
        map.put("missing", "org.apache.jsieve.commands.Missing");
        CommandManagerImpl manager = new CommandManagerImpl(map);

        assertThatThrownBy(() -> manager.getCommand("missing"))
            .isInstanceOf(LookupException.class)
            .hasMessage("Command named 'missing' not found.");
        assertThatThrownBy(() -> manager.getCommand("missing"))
            .isInstanceOf(LookupException.class);
        assertThat(manager.isCommandSupported("missing")).isFalse();

        map.put("missing", Keep.class.getName());

        assertThat(manager.isCommandSupported("missing")).isTrue();
    }

    @Test
    public void aClassOfTheWrongTypeShouldBeRejected() {
        ConcurrentMap<String, String> map = new ConcurrentHashMap<>();
        map.put("keep", Keep.class.getName());
        ComparatorManagerImpl manager = new ComparatorManagerImpl(map);

        assertThatThrownBy(() -> manager.getComparator("keep"))
            .isInstanceOf(LookupException.class)
            .hasMessageContaining("must implement");
    }
}
