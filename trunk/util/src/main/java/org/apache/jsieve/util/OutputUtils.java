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
import java.io.Writer;

import org.apache.jsieve.exception.SieveException;
import org.apache.jsieve.parser.generated.Node;

/**
 * Output utilities.
 * These are mostly convenience methods.
 * More power and flexibility is available when using the objects directly.
 */
public class OutputUtils {

    /**
     * <p>Writes the given node as xml.
     * This convenience method first writes a prolog before calling {@link #toXml(Node, Writer)}.
     * </p><p>
     * The output format is a subset of the
     * <a href='http://tools.ietf.org/html/draft-freed-sieve-in-xml-04'>sieve-in-xml</a>
     * Internet Draft.
     * </p>
     * @param node not null
     * @param writer not null
     * @throws IOException when prolog cannot be written
     * @throws SieveException when script cannot be converted to xml
     * @see #toXml(Node, Writer)
     */
    public static void toXmlDocument(final Node node, final Writer writer) throws IOException, SieveException {
        writer.append("<?xml version='1.0'?>");
        toXml(node, writer);
    }
    
    /**
     * <p>Writes the given node as xml.
     * Note that the xml will be written as a fragment.
     * An appropriate prolog must be added to convert this fragment 
     * to a document.
     * </p><p>
     * The output format is a subset of the
     * <a href='http://tools.ietf.org/html/draft-freed-sieve-in-xml-04'>sieve-in-xml</a>
     * Internet Draft. Note that this support is experimental.
     * </p>
     * @param node not null
     * @param writer not null
     * @throws SieveException when script cannot be converted to xml
     * @see XmlOut
     * @see SieveToXml
     * @see SieveHandler
     */
    public static void toXml(final Node node, final Writer writer) throws SieveException {
        final XmlOut out = new XmlOut(writer);
        final SieveToXml sieveToXml = new SieveToXml();
        final SieveHandler handler = sieveToXml.build(out);
        final NodeTraverser traverser = new NodeTraverser();
        traverser.traverse(handler, node);
    }
    
    /**
     * <p>Writes the tree rooted at the given node to a Sieve script.
     * @param node not null
     * @param writer not null
     * @throws SieveException whenever the serialization fails
     */
    public static void toSieve(final Node node, final Writer writer) throws SieveException {
        final ToSieveHandlerFactory factory = new ToSieveHandlerFactory();
        final SieveHandler handler = factory.build(writer);
        final NodeTraverser traverser = new NodeTraverser();
        traverser.traverse(handler, node);
    }
}
