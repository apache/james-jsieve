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



package org.apache.james.transport;

/**
 * A set of constants used inside the James transport.
 *
 * @version 1.0.0, 24/04/1999
 */
public class Resources {

    //Already defined in Constants
    //public static final String SERVER_NAMES = "SERVER_NAMES";

    /**
     * Don't know what this is supposed to be.
     *
     * @deprecated this is unused
     */
    public static final String USERS_MANAGER = "USERS_MANAGER";

    //Already defined in Constants
    //public static final String POSTMASTER = "POSTMASTER";

    /**
     * Don't know what this is supposed to be.
     *
     * @deprecated this is unused
     */
    public static final String MAIL_SERVER = "MAIL_SERVER";

    /**
     * Don't know what this is supposed to be.
     *
     * @deprecated this is unused
     */
    public static final String TRANSPORT = "TRANSPORT";

    /**
     * Don't know what this is supposed to be.
     *
     * @deprecated this is unused
     */
    public static final String TMP_REPOSITORY = "TMP_REPOSITORY";

    /**
     * Key for looking up the MailetLoader
     */
    public static final String MAILET_LOADER = "MAILET_LOADER";

    /**
     * Key for looking up the MatchLoader
     */
    public static final String MATCH_LOADER = "MATCH_LOADER";

    /**
     * Private constructor to prevent instantiation of the class
     */
    private Resources() {}
}
