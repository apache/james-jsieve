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
package org.apache.jsieve.commands.extensions;

import java.util.List;
import java.util.ListIterator;

import org.apache.jsieve.Arguments;
import org.apache.jsieve.Block;
import org.apache.jsieve.Logger;
import org.apache.jsieve.SieveException;
import org.apache.jsieve.StringListArgument;
import org.apache.jsieve.SyntaxException;
import org.apache.jsieve.TagArgument;
import org.apache.jsieve.commands.AbstractCommand;
import org.apache.jsieve.mail.MailAdapter;

/**
 * <p>Class Log is an extension that implements a Command to write messages to the 
 * Sieve Log. The BNF syntax is...</p>
 * <code>log [(:fatal / :error / :warn / :info / :debug / :trace)] string</code>
 * <p>The default log level is :info.</p>
 */
public class Log extends AbstractCommand implements LogLevelTags
{          
    /**
     * Constructor for Log.
     */
    public Log()
    {
        super();
    }

    /**
     * @see org.apache.jsieve.commands.AbstractCommand#executeBasic(MailAdapter, Arguments, Block)
     */
    protected Object executeBasic(
        MailAdapter mail,
        Arguments arguments,
        Block block)
        throws SieveException
    {
        String logLevel = null;
        String message = null;

        // First MAY be a tag argument of fatal, error, warn, info, debug or trace.
        // default is info.
        ListIterator argumentsIter = arguments.getArgumentList().listIterator();
        boolean stop = false;

        // Tag processing
        while (!stop && argumentsIter.hasNext())
        {
            Object argument = argumentsIter.next();
            if (argument instanceof TagArgument)
            {
                String tag = ((TagArgument) argument).getTag();

                // LogLevel?
                if (null == logLevel
                    && (tag.equals(FATAL_TAG)
                        || tag.equals(ERROR_TAG)
                        || tag.equals(WARN_TAG)
                        || tag.equals(INFO_TAG)
                        || tag.equals(DEBUG_TAG)
                        || tag.equals(TRACE_TAG)))
                    logLevel = tag;
                else
                    throw new SyntaxException("Found unexpected TagArgument");
            }
            else
            {
                // Stop when a non-tag argument is encountered
                argumentsIter.previous();
                stop = true;
            }
        }

        // Next MUST be a String
        if (argumentsIter.hasNext())
        {
            Object argument = argumentsIter.next();
            if (argument instanceof StringListArgument)
            {
                List strings = ((StringListArgument) argument).getList();
                if (1 == strings.size())
                    message = (String) strings.get(0);
            }
        }
        if (null == message)
            throw new SyntaxException("Expecting a String");

        // Everthing else is an error
        if (argumentsIter.hasNext())
            throw new SyntaxException("Found unexpected arguments");

        log(null == logLevel ? ":info" : logLevel, message);

        return null;
    }

    /**
     * Method log.
     * @param logLevel
     * @param message
     * @throws SyntaxException
     */
    protected void log(String logLevel, String message) throws SyntaxException
    {
        if (logLevel.equals(INFO_TAG))
            logInfo(message);
        else if (logLevel.equals(ERROR_TAG))
            logError(message);
        else if (logLevel.equals(WARN_TAG))
            logWarn(message);
        else if (logLevel.equals(DEBUG_TAG))
            logDebug(message);            
        else if (logLevel.equals(FATAL_TAG))
            logFatal(message);            
        else if (logLevel.equals(TRACE_TAG))
            logTrace(message);
        else
            throw new SyntaxException("Unsupported logging level: " + logLevel);
    }

    
    /**
     * Method logFatal.
     * @param message
     */
    protected void logFatal(String message)
    {
        org.apache.commons.logging.Log log = Logger.getLog();
        if (log.isFatalEnabled())
            log.fatal(message);
    }
    
    /**
     * Method logWarn.
     * @param message
     */
    protected void logWarn(String message)
    {
        org.apache.commons.logging.Log log = Logger.getLog();
        if (log.isWarnEnabled())
            log.warn(message);
    } 
    
    /**
     * Method logInfo.
     * @param message
     */
    protected void logInfo(String message)
    {
        org.apache.commons.logging.Log log = Logger.getLog();
        if (log.isInfoEnabled())
            log.info(message);
    }
    
    /**
     * Method logDebug.
     * @param message
     */
    protected void logDebug(String message)
    {
        org.apache.commons.logging.Log log = Logger.getLog();
        if (log.isDebugEnabled())
            log.debug(message);
    } 
    
    /**
     * Method logTrace.
     * @param message
     */
    protected void logTrace(String message)
    {
        org.apache.commons.logging.Log log = Logger.getLog();
        if (log.isTraceEnabled())
            log.trace(message);
    }                   
    
    /**
     * Method logError.
     * @param message
     */
    protected void logError(String message)
    {
        org.apache.commons.logging.Log log = Logger.getLog();
        if (log.isErrorEnabled())
            log.error(message);
    }    

    /**
     * @see org.apache.jsieve.commands.AbstractCommand#validateArguments(Arguments)
     */
    protected void validateArguments(Arguments arguments) throws SieveException
    {
        // Validation is performed in executeBasic()
    }

}
