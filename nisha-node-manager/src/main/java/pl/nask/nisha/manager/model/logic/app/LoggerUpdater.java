/*******************************************************************************
 * Copyright (c) 2012 Research and Academic Computer Network.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * With financial support from the Prevention, Preparedness and Consequence
 * Management of Terrorism and other Security Related Risks Programme
 * European Commission - Directorate-General Home Affairs
 *
 * Contributors:
 *     Research and Academic Computer Network
 ******************************************************************************/
package pl.nask.nisha.manager.model.logic.app;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.FileAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerUpdater {

    public static final Logger LOG = LoggerFactory.getLogger(LoggerUpdater.class);
    public static final String LOG_FILE_APPENDER_NAME = "ROLLING";

    public static boolean checkLoggerIsStarted() {

        Object fileAppender = getLogbackRootLogger().getAppender(LOG_FILE_APPENDER_NAME);

        if (fileAppender instanceof FileAppender && ((FileAppender) fileAppender).isStarted()) {
            System.out.println("logger " + LOG_FILE_APPENDER_NAME + " is started");
            return true;
        } else {
            String logFile = getLoggerFileName();
            String msg = "cannot log to file: " + logFile + " (file logging not started) - operator logged out";
            System.out.println(msg);
            throw new IllegalStateException(msg);
        }
    }

    private static String getLoggerFileName() {
        Object fileAppender = getLogbackRootLogger().getAppender(LOG_FILE_APPENDER_NAME);
        if (fileAppender instanceof FileAppender) {
            return ((FileAppender) fileAppender).getFile();
        } else {
            throw new IllegalStateException("cannot get file name - improper appender type");
        }
    }

    private static ch.qos.logback.classic.Logger getLogbackRootLogger() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        return loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
    }

}

