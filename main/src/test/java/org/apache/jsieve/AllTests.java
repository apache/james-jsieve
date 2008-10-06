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

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Class AllTests
 */
public class AllTests {

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(AllTests.class);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for org.apache.jsieve.junit");
        // $JUnit-BEGIN$
        suite.addTest(new TestSuite(ConfigurationManagerTest.class));
        suite.addTest(new TestSuite(ConditionTest.class));
        suite.addTest(new TestSuite(RequireTest.class));
        suite.addTest(new TestSuite(StopTest.class));
        suite.addTest(new TestSuite(KeepTest.class));
        suite.addTest(new TestSuite(DiscardTest.class));
        suite.addTest(new TestSuite(FileIntoTest.class));
        suite.addTest(new TestSuite(RejectTest.class));
        suite.addTest(new TestSuite(TrueTest.class));
        suite.addTest(new TestSuite(FalseTest.class));
        suite.addTest(new TestSuite(NotTest.class));
        suite.addTest(new TestSuite(AnyOfTest.class));
        suite.addTest(new TestSuite(AllOfTest.class));
        suite.addTest(new TestSuite(ExistsTest.class));
        suite.addTest(new TestSuite(AddressTest.class));
        suite.addTest(new TestSuite(HeaderTest.class));
        suite.addTest(new TestSuite(SizeTest.class));
        suite.addTest(new TestSuite(EnvelopeTest.class));
        suite.addTest(new TestSuite(LogTest.class));
        // $JUnit-END$
        return suite;
    }
}
