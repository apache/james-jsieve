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
import java.util.List;

/**
 * Class StringListArgument is a parsed representation of the RFC3028 BNF...
 * </p>
 * 
 * <code>string-list = "[" string *("," string) "]" / string</code>
 */
public class StringListArgument implements Argument {
    private List<String> fieldList;

    /**
     * Constructor for StringListArgument.
     */
    private StringListArgument() {
        super();
    }

    /**
     * Constructor for StringListArgument.
     */
    public StringListArgument(List<String> stringList) {
        this();
        setList(stringList);
    }

    /**
     * Returns the list, lazy initialised if required.
     * 
     * @return List
     */
    public List<String> getList() {
        List<String> list = null;
        if (null == (list = getListBasic())) {
            updateList();
            return getList();
        }
        return list;
    }

    /**
     * Returns the list.
     * 
     * @return List
     */
    private List<String> getListBasic() {
        return fieldList;
    }

    /**
     * Returns a new list.
     * 
     * @return List
     */
    protected List<String> computeList() {
        return new ArrayList<String>();
    }

    /**
     * Sets the list.
     * 
     * @param list
     *            The list to set
     */
    protected void setList(List<String> list) {
        fieldList = list;
    }

    /**
     * Updates the list.
     */
    protected void updateList() {
        setList(computeList());
    }

    /**
     * @see org.apache.jsieve.Argument#getValue()
     */
    public Object getValue() {
        return getList();
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return (getValue() == null) ? "null" : getValue().toString();
    }

}
