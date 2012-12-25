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

package org.apache.jsieve.util.check;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class ScriptCheckMailAdapterNoMessageSetTest {

    ScriptCheckMailAdapter adapter;

    @Before
    public void setUp() throws Exception {
        adapter = new ScriptCheckMailAdapter();
    }

    @Test
    public void testGetHeader() throws Exception {
        List<String> headers = adapter.getHeader("From");
        Assert.assertNotNull(headers);
    }

    @Test
    public void testGetHeaderNames() throws Exception {
        List headers = adapter.getHeaderNames();
        Assert.assertNotNull(headers);
    }

    @Test
    public void testGetMatchingHeader() throws Exception {
        List headers = adapter.getMatchingHeader("From");
        Assert.assertNotNull(headers);
    }

    @Test
    public void testGetSize() throws Exception {
        int size = adapter.getSize();
        Assert.assertEquals("When mail not set, size is zero", 0, size);
    }
}
