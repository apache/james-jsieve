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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Singleton class Logger manages the configuartion of the Log used by Sieve.
 */
public class Logger {
    /*
     * The sole instance of the Log.
     */
    static private Log fieldLog;

    /**
     * Constructor for Logger.
     */
    private Logger() {
        super();
    }

    /**
     * Returns the logger.
     * 
     * @return Log
     */
    public static Log getLog() {
        Log log = null;
        if (null == (log = getLogBasic())) {
            updateLog();
            return getLog();
        }
        return log;
    }

    /**
     * Returns the log.
     * 
     * @return Log
     */
    private static Log getLogBasic() {
        return fieldLog;
    }

    /**
     * Computes the log.
     * 
     * @return Log
     */
    protected static Log computeLog() {
        return LogFactory.getLog(getLogName());
    }

    /**
     * Sets the logger.
     * 
     * @param logger
     *                The logger to set
     */
    protected static void setLog(Log logger) {
        fieldLog = logger;
    }

    /**
     * Updates the log.
     */
    protected static void updateLog() {
        setLog(computeLog());
    }

    /**
     * <p>
     * Answers the log name.
     * </p>
     * 
     * <p>
     * Note that this cannot be fetched from the ConfigurationManager as the
     * ConfigurationManager itself uses the Logger. This would result in a loop!
     * No points for deducing why I am sure of this!
     * </p>
     * 
     * @return String The name of the log
     */
    protected static String getLogName() {
        return "Sieve";
    }

}
