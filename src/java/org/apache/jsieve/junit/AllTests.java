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
package org.apache.jsieve.junit;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Class AllTests
 */
public class AllTests
{

    public static void main(String[] args)
    {
        junit.swingui.TestRunner.run(AllTests.class);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite("Test for org.apache.jsieve.junit");
        //$JUnit-BEGIN$
        suite.addTest(new TestSuite(ConfigurationManagerTest.class));        
        suite.addTest(new TestSuite(ConditionTest.class));
        suite.addTest(new TestSuite(RequireTest.class));
        suite.addTest(new TestSuite(StopTest.class));
        suite.addTest(new TestSuite(KeepTest.class)); 
        suite.addTest(new TestSuite(DiscardTest.class));
        suite.addTest(new TestSuite(FileIntoTest.class));
        suite.addTest(new TestSuite(RejectTest.class));
        suite.addTest(new TestSuite(TrueTest.class));          
        suite.addTest(new TestSuite(FalseTest.class));
        suite.addTest(new TestSuite(NotTest.class));
        suite.addTest(new TestSuite(AnyOfTest.class));
        suite.addTest(new TestSuite(AllOfTest.class));
        suite.addTest(new TestSuite(ExistsTest.class));
        suite.addTest(new TestSuite(AddressTest.class));
        suite.addTest(new TestSuite(HeaderTest.class));
        suite.addTest(new TestSuite(SizeTest.class));                 
        suite.addTest(new TestSuite(EnvelopeTest.class));
        suite.addTest(new TestSuite(LogTest.class));          
        //$JUnit-END$
        return suite;
    }
}
