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
package org.apache.jsieve.util;

import java.io.IOException;

/**
 * <p>Converts Sieve nodes to xml.
 * Settings default to <code>draft-freed-sieve-in-xml-01</code>
 * <blockquote cite='http://tools.ietf.org/id/draft-freed-sieve-in-xml-01.txt'>
 * Sieve Email Filtering: Sieves and display directives in XML</blockquote>. 
 * </p>
 * <h4>Known limitations</h4>
 * <p>For simplicity, allow elements are out into a single namespace.</p>                   
 */
public class SieveToXml {
    
    public static final String DEFAULT_NAME_ATTRIBUTE = "name";

    public static final String DEFAULT_NAME_ACTION_COMMAND = "action";

    public static final String DEFAULT_NAME_CONTROL_COMMAND = "control";

    public static final String DEFAULT_NAME_TEST = "test";

    public static final String DEFAULT_NAME_LIST = "list";

    public static final String DEFAULT_NAME_NUM = "num";

    public static final String DEFAULT_NAME_TAG = "tag";

    public static final String DEFAULT_NAME_STRING = "str";

    public static final String DEFAULT_PREFIX = "sieve";
    
    public static final String DEFAULT_NAMESPACE = "urn:ietf:params:xml:ns:sieve";
    
    /** Control commands (as listed in RFC 3028) */
    public static final String[] CONTROL_COMMANDS = {"If", "Require", "Stop"};
    
    /**
     * <p>Simple infoset output.
     * </p><p>
     * Note that the approach taken to namespaces is slightly different from both 
     * <a href='http://java.sun.com/j2ee/1.4/docs/api/javax/xml/namespace/QName.html'>QName</a>
     * and <a href='http://www.saxproject.org'>SAX</a>.
     * </p>
     * <ul>
     * <li>
     * The <code>localPart</code> parameter gives the 
     * <a href='http://www.w3.org/TR/REC-xml-names/#NT-LocalPart'  rel='tag'>LocalPart</a>
     * and <code>MUST</code> be provided in all cases.
     * When the name is an <a href='http://www.w3.org/TR/REC-xml-names/#NT-UnprefixedName'  rel='tag'>UnprefixedName</a>,
     * this is the complete <a href='http://www.w3.org/TR/REC-xml-names/#dt-qualname'  rel='tag'>QName</a>.
     * When the name is a <a href='http://www.w3.org/TR/REC-xml-names/#NT-PrefixedName'  rel='tag'>PrefixedName</a>,
     * the output must combine this with the <a href='http://www.w3.org/TR/REC-xml-names/#NT-Prefix' rel='tag'>Prefix</a>
     * </li>  
     * <li>
     * </li>
     * </ul>
     * </p>
     */
    public interface Out {
        
        /**
         * Starts an XML element.
         * @throws IOException when output fails
         */
        public void openElement(CharSequence localName, CharSequence uri, CharSequence prefix) throws IOException;
        
        /**
         * Outputs a attribute.
         * @param value unescaped XML attribute content, not null
         * @throws IOException when output fails
         */
        public void attribute(CharSequence localName, CharSequence uri, CharSequence prefix, CharSequence value) throws IOException;
        
        /**
         * Outputs body text.
         * All attribute will be output before any body text
         * so this call implicitly marks the end of any attributes
         * for the element.
         * @param text unescaped body text, not null
         * @throws IOException when output fails
         */
        public void content(CharSequence text) throws IOException;
        
        /**
         * Ends an XML Element.
         * @throws IOException when output fails
         */
        public void closeElement() throws IOException;
    }
    
    /**
     * Maps node names to element names.
     */
    public interface NameMapper {
        /**
         * Converts the given node name to an
         * element local name.
         * @param name not null
         * @return element local name to use for given name, not null
         */
        public String toElementName(String name);
        
    }
    
    /**
     * Creates a mapper which will return the same
     * name for any node.
     * @param elementLocalName to be returned for all names, not null
     * @return not null
     */
    public static final NameMapper uniformMapper(final String elementLocalName) {
        return new NameMapper() {
            public String toElementName(String name) {
                return elementLocalName;
            }
        };
    }
    
    /**
     * Creates a mapper which returns values given in 
     * <code>draft-freed-sieve-in-xml-01</code>
     * <blockquote cite='http://tools.ietf.org/id/draft-freed-sieve-in-xml-01.txt'>
     * Sieve Email Filtering: Sieves and display directives in XML</blockquote>.  
     * @return not null
     */
    public static final NameMapper sieveInXmlMapper() {
        return new NameMapper() {

            public String toElementName(String name) {
                boolean isControlCommand = false;
                for (int i=0;i< CONTROL_COMMANDS.length;i++) {
                    if (CONTROL_COMMANDS[i].equalsIgnoreCase(name)) {
                        isControlCommand = true;
                        break;
                    }
                }
                final String result;
                if (isControlCommand) {
                    result = DEFAULT_NAME_CONTROL_COMMAND;
                } else {
                    result = DEFAULT_NAME_ACTION_COMMAND;
                }
                return result;
            }
        };
    }
   
    private String namespaceUri = DEFAULT_NAMESPACE;
    private String namespacePrefix = DEFAULT_PREFIX;
    private String stringElementName = DEFAULT_NAME_STRING;
    private String tagElementName = DEFAULT_NAME_TAG;
    private String numberElementName = DEFAULT_NAME_NUM;
    private String listElementName = DEFAULT_NAME_LIST;
    
    private String nameAttributeName = DEFAULT_NAME_ATTRIBUTE;
    private NameMapper commandNameMapper = sieveInXmlMapper();
    private NameMapper testNameMapper = uniformMapper(DEFAULT_NAME_TEST);
    
    /**
     * Gets mapper for command names.
     * @return not null
     */
    public NameMapper getCommandNameMapper() {
        return commandNameMapper;
    }

    /**
     * Sets mapper for command names.
     * @param commandNameMapper
     */
    public void setCommandNameMapper(NameMapper commandNameMapper) {
        this.commandNameMapper = commandNameMapper;
    }

    /**
     * Gets the element name used for lists.
     * @return element name used for lists, not null
     */
    public String getListElementName() {
        return listElementName;
    }

    /**
     * Sets the element name used for lists.
     * @param listElementName not null
     */
    public void setListElementName(String listElementName) {
        this.listElementName = listElementName;
    }

    /**
     * Gets the name of the attribute to be used to name command and tests.
     * @return name, or null when not attribute should be written
     */
    public String getNameAttributeName() {
        return nameAttributeName;
    }

    /**
     * Sets the name of the attribute to be used to indicate command and test names.
     * @param nameAttributeName naming attribute, 
     * or null when no attribute should be used
     */
    public void setNameAttributeName(String nameAttributeName) {
        this.nameAttributeName = nameAttributeName;
    }

    /**
     * Gets the namespace prefix to be used for all elements and attributes.
     * @return namespace prefix, or null when no namespace should be used 
     */
    public String getNamespacePrefix() {
        return namespacePrefix;
    }

    /**
     * Sets the namespace prefix to be used for all elements and attributes.
     * @param namespacePrefix namespace, or null when no namespace should be used
     */
    public void setNamespacePrefix(String namespacePrefix) {
        this.namespacePrefix = namespacePrefix;
    }

    /**
     * Gets the namespace URI to be used for all elements and attributes.
     * @return namespace URI, or null when no namespace should be used
     */
    public String getNamespaceUri() {
        return namespaceUri;
    }

    /**
     * Sets the namespace uri to be used for all elements and attributes.
     * @param namespaceUri namespace URI, or null when no namespace should be used
     */
    public void setNamespaceUri(String namespaceUri) {
        this.namespaceUri = namespaceUri;
    }

    /**
     * Gets the name of the element that wraps a numeric argument.
     * @return not null
     */
    public String getNumberElementName() {
        return numberElementName;
    }

    /**
     * Sets the name of the element that wraps a numeric argument.
     * @param numberElementName not null
     */
    public void setNumberElementName(String numberElementName) {
        this.numberElementName = numberElementName;
    }

    /**
     * Gets the name of the element that wraps a string element.
     * @return not null
     */
    public String getStringElementName() {
        return stringElementName;
    }

    /**
     * Sets the name of the element that wraps a string element.
     * @param stringElementName not null
     */
    public void setStringElementName(String stringElementName) {
        this.stringElementName = stringElementName;
    }

    /**
     * Gets the name of the element that wraps a tag element.
     * @return not null
     */
    public String getTagElementName() {
        return tagElementName;
    }

    /**
     * Sets the name of the element that wraps a tag element
     * @param tagElementName not null
     */
    public void setTagElementName(String tagElementName) {
        this.tagElementName = tagElementName;
    }

    /**
     * Gets the mapper for names of test nodes.
     * @return not null
     */
    public NameMapper getTestNameMapper() {
        return testNameMapper;
    }

    /**
     * Sets the mapper for names of test nodes.
     * @param testNameMapper not null
     */
    public void setTestNameMapper(NameMapper testNameMapper) {
        this.testNameMapper = testNameMapper;
    }

    /**
     * Builds a handler to writes to the given output.
     * @param out output, not null
     * @return hanlder, not null
     */
    public SieveHandler build(final Out out) {
        final Worker worker = new Worker(nameAttributeName, namespaceUri, namespacePrefix, stringElementName, 
                tagElementName, numberElementName, listElementName, commandNameMapper, testNameMapper, out);
        return worker;
    }
    
    /**
     * Worker performs actual conversion allowing enclosing to be shared safely
     * between threads.
     */
    private static final class Worker extends SieveHandler.Base {
        
        
        private final String nameAttributeName;
        
        private final String namespaceUri;
        private final String namespacePrefix;
        private final String stringElementName;
        private final String tagElementName;
        private final String numberElementName;
        private final String listElementName;
        
        private final NameMapper commandNameMapper;
        private final NameMapper testNameMapper;
        
        private final Out out;
        
        public Worker(final String nameAttributeName, final String namespaceUri, final String namespacePrefix, 
                final String stringElementName, final String tagElementName, final String numberElementName, 
                final String listElementName, final NameMapper commandNameMapper, final NameMapper testNameMapper, 
                final Out out) {
            super();
            this.nameAttributeName = nameAttributeName;
            this.namespaceUri = namespaceUri;
            this.namespacePrefix = namespacePrefix;
            this.stringElementName = stringElementName;
            this.tagElementName = tagElementName;
            this.numberElementName = numberElementName;
            this.listElementName = listElementName;
            this.commandNameMapper = commandNameMapper;
            this.testNameMapper = testNameMapper;
            this.out = out;
        }

        @Override
        public SieveHandler endCommand(String commandName) throws HaltTraversalException {
            return closeElement();
        }

        private SieveHandler closeElement() throws HaltTraversalException {
            try {
                out.closeElement();
                return this;
            } catch (IOException e) {
                throw new HaltTraversalException(e);
            }
        }

        @Override
        public SieveHandler startCommand(String commandName) throws HaltTraversalException {
            try {
                out.openElement(commandNameMapper.toElementName(commandName), namespaceUri, namespacePrefix);
                nameAttribute(commandName);
                return this;
            } catch (IOException e) {
                throw new HaltTraversalException(e);
            }
        }

        @Override
        public SieveHandler listMember(String string) throws HaltTraversalException {
            try {
                out.openElement(stringElementName, namespaceUri, namespacePrefix);
                out.content(string);
                out.closeElement();
                return this;
            } catch (IOException e) {
                throw new HaltTraversalException(e);
            }
        }

        @Override
        public SieveHandler argument(int number) throws HaltTraversalException {
            try {
                out.openElement(numberElementName, namespaceUri, namespacePrefix);
                out.content(Integer.toString(number));
                out.closeElement();
                return this;
            } catch (IOException e) {
                throw new HaltTraversalException(e);
            }
        }

        @Override
        public SieveHandler argument(String tag) throws HaltTraversalException {
            try {
                out.openElement(tagElementName, namespaceUri, namespacePrefix);
                out.content(tag);
                out.closeElement();
                return this;
            } catch (IOException e) {
                throw new HaltTraversalException(e);
            }
        }

        @Override
        public SieveHandler endTest(String testName) throws HaltTraversalException {
            return closeElement();
        }

        @Override
        public SieveHandler startTest(String testName) throws HaltTraversalException {
            try {
                out.openElement(testNameMapper.toElementName(testName), namespaceUri, namespacePrefix);
                nameAttribute(testName);
                return this;
            } catch (IOException e) {
                throw new HaltTraversalException(e);
            }
        }

        @Override
        public SieveHandler endTestList() throws HaltTraversalException {
            return closeElement();
        }

        @Override
        public SieveHandler startTestList() throws HaltTraversalException {
            try {
                out.openElement(listElementName, namespaceUri, namespacePrefix);
                return this;
            } catch (IOException e) {
                throw new HaltTraversalException(e);
            }
        }

        private void nameAttribute(String name) throws IOException {
            if (nameAttributeName != null) {
                out.attribute(nameAttributeName, namespaceUri, namespacePrefix, name);
            }
        }
    }
}
