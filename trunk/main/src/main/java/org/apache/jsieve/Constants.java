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

public class Constants {

    public static final String TAG_COMPARATOR = ":comparator";
    
    /** Name of the standard ascii casemap comparator. See <a href='http://tools.ietf.org/html/rfc4790'>RFC4790</a>. */
    public static final String COMPARATOR_ASCII_CASEMAP_NAME = "i;ascii-casemap";
    /** Name of the standard octet comparator. See <a href='http://tools.ietf.org/html/rfc4790'>RFC4790</a>. */
    public static final String COMPARATOR_OCTET_NAME = "i;octet";
    /** Prefix used to identify extension groups. See <a href='http://tools.ietf.org/html/rfc5228#section-6.1'>RFC5228, 6.1. Capability String</a> */
    public static final char REQUIRE_EXTENSION_PREFIX = '-';
    /** Prefix used to identify comparator extensions. See <a href='http://tools.ietf.org/html/rfc5228#section-6.1'>RFC5228, 6.1. Capability String</a> */
    public static final String COMPARATOR_PREFIX = "comparator" + REQUIRE_EXTENSION_PREFIX;
    /** Number of characters in {@link #COMPARATOR_PREFIX} */
    public static final int COMPARATOR_PREFIX_LENGTH = COMPARATOR_PREFIX.length();
}
