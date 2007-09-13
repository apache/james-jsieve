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

package org.apache.jsieve.junit;

import org.apache.jsieve.SieveException;
import org.apache.jsieve.mail.MailAdapter;
import org.apache.jsieve.tests.Address;

final class OpenedAddress extends Address {

    protected boolean match(MailAdapter mail, String addressPart, String comparator, String matchType, String headerName, String key) throws SieveException {
        return super.match(mail, addressPart, comparator, matchType, headerName, key);
    }
    
}
