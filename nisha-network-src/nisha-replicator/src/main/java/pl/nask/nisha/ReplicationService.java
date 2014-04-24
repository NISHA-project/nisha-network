/**
 * *****************************************************************************
 * Copyright (c) 2012 Research and Academic Computer Network. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the GNU Public License v2.0 which accompanies this
 * distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * With financial support from the Prevention, Preparedness and Consequence
 * Management of Terrorism and other Security Related Risks Programme European
 * Commission - Directorate-General Home Affairs
 *
 * Contributors: Research and Academic Computer Network
 * ****************************************************************************
 */
package pl.nask.nisha;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Properties;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.FileAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.nisha.commons.NishaDateTime;
import pl.nask.nisha.commons.config.ConfigPropertyName;

public class ReplicationService {

    private static final Logger logger = LoggerFactory.getLogger(CouchDBReplicator.class);
    private static final String FILE_LOGGER_NAME = "ROLLING";
    private static final String DEFAULT_COUCHDBHOST = "127.0.0.1";
    private static final String DEFAULT_COUCHDBPORT = "5984";
    private static final String DEFAULT_NODEID = "localhost";
    private static final long DEFAULT_INTERVAL = 5000;
    private static final long DEFAULT_ALERT_DELAY = 5*60*1000;


    private static void usage() {
        System.out.println("Invalid usage:\n Replicator [flags]");
        System.out.println("Available flags:\n -c <file> - config file | --config <file> [REQUIRED]");
        System.out.println("Available flags:\n -d - enables debug mode [OPTIONAL]");

    }

    public static void main(String[] args) {
        if (args.length < 2) {
            usage();
            System.exit(-1);
        }

        boolean debugEnabled = ((args.length >= 3 && args[2].contains("-d")));


        if (debugEnabled) {
            enableDetailedLogging();
        }

        checkLoggerIsStarted(FILE_LOGGER_NAME);


        Properties externalProperties = new Properties();
        if (args.length >= 2 && (args[0].contains("-c") || args[0].contains("--config"))) {
            try {
                externalProperties.load(new FileInputStream(new File(args[1])));
            } catch (FileNotFoundException e) {
                logger.error("config file not found, exiting...");
                System.exit(-1);
            } catch (IOException e) {
                logger.error("IO exception, exiting...");
            }
        } else {
            logger.error("no config file given, exiting...");
            System.exit(-1);
        }

        String couchdbHost = externalProperties.getProperty("couchdb_host");
        if (couchdbHost == null || couchdbHost.isEmpty()) {
            logger.warn("no couchdb host given, assuming default: " + DEFAULT_COUCHDBHOST);
            couchdbHost = DEFAULT_COUCHDBHOST;
        }

        String couchdbPort = externalProperties.getProperty("couchdb_port");
        if (couchdbPort == null || couchdbHost.isEmpty()) {
            logger.warn("no couchdb port given, assuming default: " + DEFAULT_COUCHDBPORT);
            couchdbPort = DEFAULT_COUCHDBPORT;
        }

        String thisNodeURI = externalProperties.getProperty("node_id");
        if (thisNodeURI == null || thisNodeURI.isEmpty()) {
            logger.warn("no node id given, assuming default: " + DEFAULT_NODEID);
            thisNodeURI = DEFAULT_NODEID;
        }

        String externalPropertiesFilePath = args[1];
        String nishaUsername = getProprertyOrExit(externalProperties, "couchdb_nisha_user", externalPropertiesFilePath);
        String nishaPassword = getProprertyOrExit(externalProperties, "couchdb_nisha_password", externalPropertiesFilePath);
        long replicationIntervalInMills = getLongProperty(externalProperties, externalPropertiesFilePath, "replicationIntervalInMills", DEFAULT_INTERVAL);
        long alertDelayTimeInMills = getLongProperty(externalProperties, externalPropertiesFilePath, "alertDelayTimeInMills", DEFAULT_ALERT_DELAY);

        AlertManager alertGenerator = new AlertManagerImpl(replicationIntervalInMills, alertDelayTimeInMills,
                new HashMap<String, AlertGenerationState>(), new SimpleDateFormat(NishaDateTime.dateTimePattern));

        Replicator replicator = new CouchDBReplicator(thisNodeURI, couchdbHost, Integer.parseInt(couchdbPort),
                                        nishaUsername, nishaPassword, resolveTimeoutFromProperties(externalProperties),
                replicationIntervalInMills, alertGenerator);
        replicator.runReplication();
    }

    private static long getLongProperty(Properties externalProperties, String propertiesFileName,
                                                                String propertyName, long defaultValue) {
        long result;
        String interval = externalProperties.getProperty(propertyName);
        try {
            result = Long.parseLong(interval);
        } catch (Exception e) {
            logger.warn("Property problem: '" + propertyName + "' in " + propertiesFileName +". " +
                        "Value could not be parsed to long - value: " + interval +". " +
                        "Default value used:  " + defaultValue);

            result = defaultValue;
        }
        return result;
    }

    private static String getProprertyOrExit(Properties properties, String name, String propertiesFileName) {
        String value = properties.getProperty(name);
        if (value == null || value.isEmpty()) {
            logger.error("Property '" + name + "' not found in " + propertiesFileName);
            System.exit(-1);
        }
        return value;
    }

    public static void enableDetailedLogging() {
        final Logger rootLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        final String loggerClassName = rootLogger.getClass().getName();

        System.out.println("Using " + loggerClassName + " as logging backend.");
        if (rootLogger instanceof ch.qos.logback.classic.Logger) {
            ((ch.qos.logback.classic.Logger) rootLogger).setLevel(ch.qos.logback.classic.Level.ALL);
        } else {
            System.out.println("Cannot change logging level of " + loggerClassName + " at run-time.");
        }
    }

    private static int resolveTimeoutFromProperties(Properties properties) {
        int responseTimeoutMillis;
        String timeoutPropertyValue = properties.getProperty(ConfigPropertyName.COUCHDB_HTTP_SOCKET_TIMEOUT.value, "0");
        try {
            responseTimeoutMillis = Integer.parseInt(timeoutPropertyValue);
        }
        catch (NumberFormatException ex) {
            throw new IllegalArgumentException(timeoutPropertyValue + " is not a valid number - cannot load couchdb http socket timeout");
        }
        return responseTimeoutMillis;
    }

    private static boolean checkLoggerIsStarted(String appenderName) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
        Object fileAppender = rootLogger.getAppender(appenderName);

        if (fileAppender instanceof FileAppender && ((FileAppender) fileAppender).isStarted()) {
            System.out.println("logger " + appenderName + " is started");
            return true;
        } else {
            String msg = "cannot log to file: " + appenderName + " (file logging not started) - operator logged out";
            printAndExit(msg);
        }
        return false;
    }

    private static void printAndExit(String msg) {
        System.out.println(msg);
        System.out.println("Exiting application");
        System.exit(1);
    }
}
