/***********************************************************************
 * Copyright (c) 2003-2004 The Apache Software Foundation.             *
 * All rights reserved.                                                *
 * ------------------------------------------------------------------- *
 * Licensed under the Apache License, Version 2.0 (the "License"); you *
 * may not use this file except in compliance with the License. You    *
 * may obtain a copy of the License at:                                *
 *                                                                     *
 *     http://www.apache.org/licenses/LICENSE-2.0                      *
 *                                                                     *
 * Unless required by applicable law or agreed to in writing, software *
 * distributed under the License is distributed on an "AS IS" BASIS,   *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or     *
 * implied.  See the License for the specific language governing       *
 * permissions and limitations under the License.                      *
 ***********************************************************************/

package org.apache.jsieve.parser;

/**
 * Class SieveNode defines aspects all jjTree parse nodes may require.
 * 
 * Creation Date: 27-Jan-04
 */
public class SieveNode
{

    /**
     * Constructor for SieveNode.
     */
    public SieveNode()
    {
        super();
    }

    /**
     * The name associated to this node or null 
     */     
    private String fieldName;
    
    /**
     * The value associated to this node or null 
     */    
    private Object fieldValue;    
    /**
     * Returns the name.
     * @return String
     */
    public String getName()
    {
        return fieldName;
    }

    /**
     * Returns the value.
     * @return Object
     */
    public Object getValue()
    {
        return fieldValue;
    }

    /**
     * Sets the name.
     * @param name The name to set
     */
    public void setName(String name)
    {
        fieldName = name;
    }

    /**
     * Sets the value.
     * @param value The value to set
     */
    public void setValue(Object value)
    {
        fieldValue = value;
    }

}
