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
package pl.nask.nisha.commons.config;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class NodeManagerFileConfig {

    public static final Logger LOG = LoggerFactory.getLogger(NodeManagerFileConfig.class);
    private final static String CONFIG_FILE_PATH = "/etc/nisha/nisha-node-manager.properties";
    private static NodeManagerFileConfig NODE_MANAGER_FILE_CONFIG;
    private Properties properties;

    public static NodeManagerFileConfig getNodeManagerFileConfig(boolean loadProperties) {
        if (NODE_MANAGER_FILE_CONFIG == null || NODE_MANAGER_FILE_CONFIG.properties == null || loadProperties) {
            NODE_MANAGER_FILE_CONFIG = new NodeManagerFileConfig(CONFIG_FILE_PATH);
        }
        return NODE_MANAGER_FILE_CONFIG;
    }

    private NodeManagerFileConfig(String configFilePath) {
        try {
            this.properties = loadProperties(configFilePath);
        } catch (IllegalStateException e) {
            LOG.error(e.getMessage());
            this.properties = null;
        }
    }

    private Properties loadProperties(String propertiesFileName) {

        Properties localProperties;
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(propertiesFileName);
            localProperties = new Properties();
            localProperties.load(inputStream);
        } catch (IOException ex) {
            LOG.warn("cannot load properties: " + ex.getMessage());
            throw new IllegalStateException(ex.getMessage());
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException ex) {
                LOG.warn("cannot close properties: " + ex.getMessage());
                throw new IllegalStateException(ex.getMessage());
            }
        }

        LOG.info("Properties: " + localProperties.entrySet().size() + " " + localProperties.keySet());

        try {
            updateConfigPropertiesFile(localProperties, propertiesFileName);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        }

        return localProperties;
    }

    private void updateConfigPropertiesFile(Properties properties, String propertiesFileName) throws IOException {
        if (properties == null) {
            LOG.info("properties are null - cannot be stored");
        } else {
            FileOutputStream outputStream = new FileOutputStream(propertiesFileName);
            String comment =
                    "ATTENTION\nProperties has been loaded to node manager applcation.\n"
                    + "and will not be loaded again.";
            properties.store(outputStream, comment);
            outputStream.close();
            LOG.info("properties backup stored in " + propertiesFileName);
        }
    }

    public String getPropertyValue(ConfigPropertyName propertyName) {
        return getPropertyValue(propertyName, null);
    }

    public String getPropertyValue(ConfigPropertyName propertyName, String defaultValue) {
        if (properties == null) {
            throw new IllegalStateException("configuration properties has not been loaded to node manager (read and write rights to properties file needed)");
        }
        String propNameString = propertyName.value;
        String propertyVal = properties.getProperty(propNameString);
        if (propertyVal != null && !propertyVal.isEmpty()) {
            return propertyVal;
        }
        LOG.info(propertyName + "=" + propNameString + " - property not found - default value returned: " + defaultValue);
        return defaultValue;
    }

    @Override
    public String toString() {
        return "NodeManagerFileConfig{"
                + "properties=" + properties
                + '}';
    }
}
