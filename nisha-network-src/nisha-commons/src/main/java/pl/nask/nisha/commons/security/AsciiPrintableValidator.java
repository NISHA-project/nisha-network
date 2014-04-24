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
package pl.nask.nisha.commons.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsciiPrintableValidator {

    public final static Logger LOG = LoggerFactory.getLogger(AsciiPrintableValidator.class);

    public static boolean isStringOfAsciiPrintableChars(String stringToCheck) {
        char[] passwordCharArray = stringToCheck.toCharArray();
        for (char ch : passwordCharArray) {
            if (!isAsciiPrintable(ch)) {
                String msg = "ASCII printable requirement violated";
                LOG.warn(msg);
                throw new IllegalArgumentException(msg);
            }
        }
        LOG.debug("ascii printables ok");
        return true;

    }
    public static boolean isAsciiPrintable(char ch) {
        return ch > 32 && ch < 127;
    }
}

