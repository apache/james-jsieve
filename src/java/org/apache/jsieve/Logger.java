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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Singleton class Logger manages the configuartion of the Log used by Sieve.
 */
public class Logger
{
    /*
     * The sole instance of the Log.
     */ 
    static private Log fieldLog;

    /**
     * Constructor for Logger.
     */
    private Logger()
    {
        super();
    }

    /**
     * Returns the logger.
     * @return Log
     */
    public static Log getLog()
    {
        Log log = null;
        if (null == (log = getLogBasic()))
        {
            updateLog();
            return getLog();    
        } 
        return log;
    }
    
    /**
     * Returns the log.
     * @return Log
     */
    private static Log getLogBasic()
    {
        return fieldLog;
    }
    
    /**
     * Computes the log.
     * @return Log
     */
    protected static Log computeLog()
    {
        return LogFactory.getLog(getLogName());
    }        

    /**
     * Sets the logger.
     * @param logger The logger to set
     */
    protected static void setLog(Log logger)
    {
        fieldLog = logger;
    }
    
    /**
     * Updates the log.
     */
    protected static void updateLog()
    {
        setLog(computeLog());
    }
    
    /**
     * <p>Answers the log name.</p>
     * 
     * <p>Note that this cannot be fetched from the ConfigurationManager as the
     * ConfigurationManager itself uses the Logger. This would result in a loop! No
     * points for deducing why I am sure of this!</p>
     * 
     * @return String The name of the log
     */
    protected static String getLogName()
    {
        return "Sieve";
    }            

}
