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

package org.apache.jsieve.parser;

import java.util.LinkedList;
import java.util.List;

import org.apache.jsieve.ScriptCoordinate;
import org.apache.jsieve.parser.generated.Token;

/**
 * Class SieveNode defines aspects all jjTree parse nodes may require.
 * 
 * Creation Date: 27-Jan-04
 */
public class SieveNode {

    /**
     * Constructor for SieveNode.
     */
    public SieveNode() {
        super();
    }

    private Token firstToken;

    private Token lastToken;

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
     * 
     * @return String
     */
    public String getName() {
        return fieldName;
    }

    /**
     * Returns the value.
     * 
     * @return Object
     */
    public Object getValue() {
        return fieldValue;
    }

    /**
     * Sets the name.
     * 
     * @param name
     *            The name to set
     */
    public void setName(String name) {
        fieldName = name;
    }

    /**
     * Sets the value.
     * 
     * @param value
     *            The value to set
     */
    public void setValue(Object value) {
        fieldValue = value;
    }

    /**
     * Gets the first token comprising this node.
     * 
     * @return <code>Token</code>, not null
     */
    public Token getFirstToken() {
        return firstToken;
    }

    /**
     * Sets the first token comprising this node.
     * 
     * @param firstToken
     *            <code>Token</code>, not null
     */
    public void setFirstToken(Token firstToken) {
        this.firstToken = firstToken;
    }

    /**
     * Gets the last token comprising this node.
     * 
     * @return <code>Token</code>, not null
     */
    public Token getLastToken() {
        return lastToken;
    }

    /**
     * Sets the last token comprising this node.
     * 
     * @param lastToken
     *            <code>Token</code>, not null
     */
    public void setLastToken(Token lastToken) {
        this.lastToken = lastToken;
    }

    /**
     * Gets the position of this node in the script.
     * 
     * @return <code>ScriptCoordinate</code> containing the position of this
     *         node, not null
     */
    public ScriptCoordinate getCoordinate() {
        final int lastColumn = lastToken.endColumn;
        final int lastList = lastToken.endLine;
        final int firstColumn = firstToken.beginColumn;
        final int firstLine = firstToken.beginLine;
        final ScriptCoordinate scriptCoordinate = new ScriptCoordinate(
                firstLine, firstColumn, lastList, lastColumn);
        return scriptCoordinate;
    }

    /**
     * Get any comments between this node and the previous one.
     * Each comment is returned without whitespace trimming.
     * Comments are returned in the order of occurance in the script.
     * @return collection of strings, not null
     */
    public List<String> getPrecedingComments() {
        final LinkedList<String> results = new LinkedList<String>();
        if (firstToken != null) {
            Token special = firstToken.specialToken;
            while (special != null) {
                final String comment = parseComment(special);
                results.addFirst(comment);
                special = special.specialToken;
            }
        }
        return results;
    }

    private String parseComment(Token special) {
        final String image = special.image;
        final String comment;
        if ('#' == image.charAt(0)) {
            final int leftHandCharactersToIgnore;
            if ('\r' == image.charAt(image.length()-2)) {
                leftHandCharactersToIgnore = 2;
            } else {
                leftHandCharactersToIgnore = 1;
            }
            comment = image.substring(1, image.length()-leftHandCharactersToIgnore);
        } else {
            comment = image.substring(2, image.length()-2);
        }
        return comment;
    }
    
    /**
     * Get the last comment before this node and after the last node.
     * Each comment is returned without whitespace trimming.
     * Comments are returned in the order of occurance in the script.
     * @return the comment without whitespace trimming,
     * or null if there is no comment between this and the last node
     */
    public String getLastComment() {
        final String result;
        if (firstToken == null) {
            result = null;
        } else {
            Token special = firstToken.specialToken;
            if (special == null) {
                result = null;
            } else {
                result = parseComment(special);
            } 
        }
        return result;
    }
}
