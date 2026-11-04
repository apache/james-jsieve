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

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.apache.jsieve.utils.JUnitUtils;
import org.junit.Assert;
import org.slf4j.LoggerFactory;

/**
 * The description of a test or a command, which covers its whole subtree,
 * is built only when debug logging is on.
 */
public class DebugLoggingTest {

    @org.junit.Test
    public void testShouldNotBuildItsDescriptionWhenDebugIsOff() throws Exception {
        AtomicInteger descriptions = new AtomicInteger();
        Test test = new Test("true", new Arguments(new ArrayList<Argument>(), null)) {
            @Override
            public String toString() {
                descriptions.incrementAndGet();
                return super.toString();
            }
        };
        ConfigurationManager configurationManager = new ConfigurationManager();
        SieveContext context = new BaseSieveContext(configurationManager.getCommandManager(),
            configurationManager.getComparatorManager(), configurationManager.getTestManager());
        Logger logger = (Logger) LoggerFactory.getLogger(Test.class);
        Level level = logger.getLevel();
        logger.setLevel(Level.INFO);
        try {
            test.isTestPassed(JUnitUtils.createMail(), context);
        } finally {
            logger.setLevel(level);
        }

        Assert.assertEquals(0, descriptions.get());
    }

}
