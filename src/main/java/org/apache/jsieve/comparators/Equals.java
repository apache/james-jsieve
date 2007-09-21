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

package org.apache.jsieve.comparators;

/**
 * Interface Equals defines the method signatures for equals comparators.
 */
public interface Equals {
    /**
     * Method equals answers a <code>boolean</code> indicating if parameter
     * <code>string1</code> is equal to parameter <code>string2</code> using
     * the comparison rules defind by the implementation.
     * 
     * @param string1
     * @param string2
     * @return boolean
     */
    public boolean equals(String string1, String string2);
}
