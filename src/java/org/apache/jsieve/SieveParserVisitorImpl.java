/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2004 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache", "Jakarta", "JAMES", "JSieve" and 
 *    "Apache Software Foundation" must not be used to endorse or promote
 *    products derived from this software without prior written permission.
 *    For written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * Portions of this software are based upon public domain software
 * originally written at the National Center for Supercomputing Applications,
 * University of Illinois, Urbana-Champaign.
 */
package org.apache.jsieve;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.jsieve.mail.*;
import org.apache.jsieve.parser.generated.*;



/**
 * <p>Class SieveParserVisitorImpl defines the behaviour for each visited node in the
 * Sieve grammar. Each method corresponds to a node type and is invoked when a node
 * of that type is evaluated.</p>
 * 
 * <p>In essence, this class translates between the nodes operated on by the JavaCC 
 * generated classes and the Sieve classes operated upon by the Commands, Tests and
 * Comparators. A visit to the start node, ASTstart, triggers evaluation of all of
 * its descendants.
 * </p>
 * 
 * <p>See https://javacc.dev.java.net/doc/JJTree.html for indepth information about 
 * Visitor support.</p>
 */
public class SieveParserVisitorImpl implements SieveParserVisitor
{

    /**
     * Constructor for NodeVisitor.
     */
    public SieveParserVisitorImpl()
    {
        super();
    }
    
    /**
     * Method visitChildren adds the children of the node to the passed List.
     * @param node
     * @param data - Assumes a List
     * @return Object - A List 
     * @throws SieveException
     */
    protected Object visitChildren(SimpleNode node, Object data)
        throws SieveException
    {
        List children = new ArrayList(node.jjtGetNumChildren());
        node.childrenAccept(this, children);
        ((List) data).addAll(children);    
        return data;
    }    

    /**
     * @see SieveParserVisitor#visit(ASTargument, Object)
     */
    public Object visit(ASTargument node, Object data) throws SieveException
    {
        List children = new ArrayList(node.jjtGetNumChildren());
        Argument argument = null;

        if (null != node.getValue())
        {
            argument = (Argument)node.getValue();
        }
        else
        {
            argument =
                new StringListArgument(
                    ((List) node.childrenAccept(this, children)));
        }
        ((List) data).add(argument);

        return data;
    }
    /**
     * @see SieveParserVisitor#visit(ASTarguments, Object)
     */
    public Object visit(ASTarguments node, Object data) throws SieveException
    {
        List children = new ArrayList(node.jjtGetNumChildren());
        children = ((List) node.childrenAccept(this, children));

        // Extract Tests and TestList from the children
        Iterator childrenIter = children.iterator();
        TestList testList = null;
        List argList = new ArrayList(children.size());
        while (childrenIter.hasNext())
        {
            Object next = childrenIter.next();
            if (next instanceof Test)
                testList = new TestList((Test) next);
            else if (next instanceof TestList)
                testList = (TestList) next;
            else
                argList.add(next);
        }

        Arguments arguments = new Arguments(argList, testList);
        ((List) data).add(arguments);
        return data;
    }

    /**
     * @see SieveParserVisitor#visit(ASTblock, Object)
     */
    public Object visit(ASTblock node, Object data) throws SieveException
    {
        //        if (node.jjtGetNumChildren() != 1)
        //            throw new ParseException("Expecting exactly one 1 child");
        List children = new ArrayList(node.jjtGetNumChildren());
        Commands commands =
            (Commands) ((List) node.childrenAccept(this, children)).get(0);
        Block block = new Block(commands);
        ((List) data).add(block);
        return data;
    }

    /**
     * @see SieveParserVisitor#visit(ASTcommand, Object)
     */
    public Object visit(ASTcommand node, Object data) throws SieveException
    {
        List children = new ArrayList(node.jjtGetNumChildren());
        children = ((List) node.childrenAccept(this, children));

        // Extract the Arguments and Block from the children
        Iterator childrenIter = children.iterator();
        Arguments arguments = null;
        Block block = null;
        while (childrenIter.hasNext())
        {
            Object next = childrenIter.next();
            if (next instanceof Arguments)
                arguments = (Arguments) next;
            else if (next instanceof Block)
                block = (Block) next;
        }

        Command command = new Command(node.getName(), arguments, block);
        ((List) data).add(command);
        return data;
    }

    /**
     * @see SieveParserVisitor#visit(ASTcommands, Object)
     */
    public Object visit(ASTcommands node, Object data) throws SieveException
    {
        List children = new ArrayList(node.jjtGetNumChildren());
        Commands commands =
            new Commands(((List) node.childrenAccept(this, children)));
        ((List) data).add(commands);
        return data;
    }

    /**
     * @see SieveParserVisitor#visit(ASTstart, Object)
     */
    public Object visit(ASTstart node, Object data) throws SieveException
    {
        // The data object must be the MailAdapter to process
        if (!(data instanceof MailAdapter))
            throw new SieveException(
                "Expecting an instance of "
                    + MailAdapter.class.getName()
                    + " as data, received an instance of "
                    + (data == null ? "<null>" : data.getClass().getName())
                    + ".");

        // Start is an implicit Block
        // There will be one child, an instance of Commands
        List children = new ArrayList(node.jjtGetNumChildren());
        Commands commands =
            (Commands) ((List) node.childrenAccept(this, children)).get(0);
        Block block = new Block(commands);

        // Answer the result of executing the Block
        return block.execute((MailAdapter)data);
    }

    /**
     * @see SieveParserVisitor#visit(ASTstring_list, Object)
     */
    public Object visit(ASTstring_list node, Object data) throws SieveException
    {
        return visitChildren(node, data);
    }

    /**
     * @see SieveParserVisitor#visit(ASTstring, Object)
     */
    public Object visit(ASTstring node, Object data)
    {
        // Strings are always surround by double-quotes
        String value = (String)node.getValue();
        String string = value.substring(1, value.length() - 1);
        
        // A String is terminal, add it
        ((List)data).add(string);
        return data;
    }

    /**
     * @see SieveParserVisitor#visit(ASTtest_list, Object)
     */
    public Object visit(ASTtest_list node, Object data) throws SieveException
    {
        //        return visitChildren(node, data);
        List children = new ArrayList(node.jjtGetNumChildren());
        TestList testList =
            new TestList(((List) node.childrenAccept(this, children)));
        ((List) data).add(testList);
        return data;
    }

    /**
     * @see SieveParserVisitor#visit(ASTtest, Object)
     */
    public Object visit(ASTtest node, Object data) throws SieveException
    {
        List children = new ArrayList(node.jjtGetNumChildren());
        children = ((List) node.childrenAccept(this, children));

        // Extract the Arguments from the children
        Iterator childrenIter = children.iterator();
        Arguments arguments = null;
        while (childrenIter.hasNext())
        {
            Object next = childrenIter.next();
            if (next instanceof Arguments)
                arguments = (Arguments) next;
        }

        Test test = new Test(node.getName(), arguments);
        ((List) data).add(test);
        return data;
    }

    /**
     * @see SieveParserVisitor#visit(SimpleNode, Object)
     */
    public Object visit(SimpleNode node, Object data) throws SieveException
    {
        return visitChildren(node, data);
    }

}
