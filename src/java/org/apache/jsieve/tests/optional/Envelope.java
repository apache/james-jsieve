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


package org.apache.jsieve.tests.optional;

import java.util.List;

import org.apache.jsieve.mail.MailAdapter;
import org.apache.jsieve.mail.SieveMailException;
import org.apache.jsieve.mail.optional.EnvelopeAccessors;
import org.apache.jsieve.tests.Address;

/**
 * Class Envelope implements the optional Envelope Test as defined in RFC 3028, 
 * section 5.4.
 */
public class Envelope extends Address
{

    /**
     * Constructor for EnvelopeAccessors.
     */
    public Envelope()
    {
        super();
    }

    /**
     * @see org.apache.jsieve.tests.Address#getMatchingValues(MailAdapter, String)
     */
    protected List getMatchingValues(MailAdapter mail, String valueName)
        throws SieveMailException
    {
        return ((EnvelopeAccessors) mail).getMatchingEnvelope(valueName);
    }    


    

}
