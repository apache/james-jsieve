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


package org.apache.jsieve.commands.extensions;

import java.util.List;
import java.util.ListIterator;

import org.apache.jsieve.Arguments;
import org.apache.jsieve.Block;
import org.apache.jsieve.Logger;
import org.apache.jsieve.SieveContext;
import org.apache.jsieve.StringListArgument;
import org.apache.jsieve.TagArgument;
import org.apache.jsieve.commands.AbstractCommand;
import org.apache.jsieve.exception.SieveException;
import org.apache.jsieve.exception.SyntaxException;
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
     * @see org.apache.jsieve.commands.AbstractCommand#executeBasic(MailAdapter, Arguments, Block, SieveContext)
     */
    protected Object executeBasic(
        MailAdapter mail,
        Arguments arguments,
        Block block, SieveContext context)
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
                    throw context.getCoordinate().syntaxException("Found unexpected TagArgument");
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
            throw context.getCoordinate().syntaxException("Expecting a String");

        // Everthing else is an error
        if (argumentsIter.hasNext())
            throw context.getCoordinate().syntaxException("Found unexpected arguments");

        log(null == logLevel ? ":info" : logLevel, message, context);

        return null;
    }

    /**
     * Method log.
     * @param logLevel
     * @param message
     * @throws SyntaxException
     */
    protected void log(String logLevel, String message, SieveContext context) throws SyntaxException
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
            throw context.getCoordinate().syntaxException("Unsupported logging level: " + logLevel);
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
     * @see org.apache.jsieve.commands.AbstractCommand#validateArguments(Arguments, SieveContext)
     */
    protected void validateArguments(Arguments arguments, SieveContext context) throws SieveException
    {
        // Validation is performed in executeBasic()
    }

}
