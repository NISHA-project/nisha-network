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

import org.junit.Test;

import static junit.framework.Assert.assertTrue;

public class PasswordValidatorTest {

    @Test
    public void lengthOkTest(){
        String passOK = "2!@#456YUIop";
        boolean result = PasswordValidator.validatePassword(passOK);
        assertTrue(result);
    }

    @Test (expected = IllegalArgumentException.class)
    public void lengthShortTest(){
        String shortPass = "!@34QWer";
        PasswordValidator.validatePassword(shortPass);
        assertTrue(false);
    }

    @Test (expected = IllegalArgumentException.class)
    public void noNumberTest(){
        String noNumberPass = "!@#QWEasdzxc";
        PasswordValidator.validatePassword(noNumberPass);
        assertTrue(false);
    }


    @Test (expected = IllegalArgumentException.class)
    public void noSpecialTest(){
        String noNumberPass = "ABCdef1234";
        PasswordValidator.validatePassword(noNumberPass);
        assertTrue(false);
    }

    @Test (expected = IllegalArgumentException.class)
    public void noUppercaseTest(){
        String noNumberPass = "abc1234xyz";
        PasswordValidator.validatePassword(noNumberPass);
        assertTrue(false);
    }

    @Test (expected = IllegalArgumentException.class)
    public void notPrintableTest(){
        String noNumberPass = "abc 1234xyz";
        PasswordValidator.validatePassword(noNumberPass);
        assertTrue(false);
    }
}

