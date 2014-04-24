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
 *****************************************************************************
 */
package pl.nask.nisha;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigReader {

    private static final Logger logger = LoggerFactory.getLogger(ConfigReader.class);

    public static Properties loadProperties(String propertiesFileName) {
        URL url = getGlobalResourceURL(propertiesFileName);
        Properties properties = null;
        if (url == null) {
            logger.warn("Cannot locate resource");
            return new Properties();
        }
        try {
            InputStream inputStream = url.openStream();
            properties = new Properties();
            properties.load(inputStream);

        } catch (IOException ex) {
            logger.warn(null, ex);
        }
        return properties;
    }

    public static URL getGlobalResourceURL(String resource) {
        return Thread.currentThread().getContextClassLoader().getResource(resource);
    }
}
