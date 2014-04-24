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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PasswordValidator {

    public final static Logger LOG = LoggerFactory.getLogger(PasswordValidator.class);
    public static final int MIN_PASSWORD_LENGTH = 10;
    public static  final String INTRO = "password validation ";


    public static boolean validatePassword(String password) {
        boolean success = lengthTest(password) && hasNumberTest(password) && hasSpecialTest(password) &&
                hasUpperCaseTest(password) && asciiPrintableTest(password);
        LOG.info(INTRO +"completed - success: {}", success);
        return success;
    }

    private static boolean lengthTest(String password) {
        if (password.length() >= MIN_PASSWORD_LENGTH) {
            LOG.debug(INTRO +"- length ok");
            return true;
        } else {
            String msg = INTRO + "failure - password too short - minimal length: " + MIN_PASSWORD_LENGTH;
            LOG.warn("{}", msg);
            throw new IllegalArgumentException(msg);
        }
    }

    private static boolean hasNumberTest(String password) {
        boolean success = hasCharacterTest(password, "[0-9]", INTRO + "failure - password must contain at least one number");
        LOG.debug(INTRO +"- has number ok");
        return success;
    }

    private static boolean hasSpecialTest(String password) {
        String regex = "[^a-zA-Z0-9]";
        boolean success = hasCharacterTest(password, regex, INTRO + "failure - password must contain a special ascii printable character");
        LOG.debug(INTRO + "- has special character ok");
        return success;
    }

    private static boolean hasUpperCaseTest(String password) {
        String regex = "[A-Z]";
        boolean success = hasCharacterTest(password, regex, INTRO + "failure - password must contain at least one upper case letter");
        LOG.debug(INTRO + "- has upper case letter ok");
        return success;
    }

    private static boolean hasCharacterTest(String password, String regex, String message) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        if (matcher.find()) {
            return true;
        } else {
            LOG.warn(message);
            throw new IllegalArgumentException(message);
        }
    }

    private static boolean asciiPrintableTest(String password) {
        try {
        boolean  success = AsciiPrintableValidator.isStringOfAsciiPrintableChars(password);
        LOG.debug(INTRO + "- ascii printable ok");
        return success;
        } catch (IllegalArgumentException e) {
            String msg = INTRO + "failure - password must stick to ASCII charset";
            throw new IllegalArgumentException(msg);
        }
    }

}

