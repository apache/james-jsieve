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

import org.apache.jsieve.exception.FeatureException;


/**
 * Interface Contains defines the method signatures for contains comparators.
 */
public interface Contains {

    /**
     * Method contains answers a <code>boolean</code> indicating if parameter
     * <code>container</code> contains parameter <code>content</code> using
     * the comparison rules defind by the implementation.
     * 
     * @param container
     * @param content
     * @return boolean
     * @throws FeatureException when substring is unsupported
     */
    public boolean contains(String container, String content) throws FeatureException;

}
