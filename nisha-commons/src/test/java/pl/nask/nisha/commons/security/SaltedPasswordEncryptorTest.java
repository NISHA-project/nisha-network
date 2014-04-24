/**
 * ****************************************************************************
 * Copyright (c) 2012 Research and Academic Computer Network.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * <p/>
 * With financial support from the Prevention, Preparedness and Consequence
 * Management of Terrorism and other Security Related Risks Programme
 * European Commission - Directorate-General Home Affairs
 * <p/>
 * Contributors:
 * Research and Academic Computer Network
 * ****************************************************************************
 */
package pl.nask.nisha.commons.security;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;


public class SaltedPasswordEncryptorTest {

    @Test
    public void getEncryptedPasswordOk() {
        String password = "password";
        String salt = "salt";
        String expectedResult;
        try{
            //encrypt password + passwordHash + salt
            Base64 base64 = new Base64(true);
            byte[] passBytes = base64.decode(password);
            byte[] passHash = DigestUtils.sha256(passBytes);
            byte[] saltBytes = base64.decode(salt);
            int inputLength = passBytes.length + passHash.length + saltBytes.length;
            byte[] input = new byte[inputLength];
            System.arraycopy(passBytes, 0, input, 0, passBytes.length);
            System.arraycopy(passHash, 0, input, passBytes.length, passHash.length);
            System.arraycopy(saltBytes, 0, input, passBytes.length + passHash.length, saltBytes.length);
            expectedResult = DigestUtils.sha256Hex(input);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("cannot decode String to bytes");
        }

        String result = SaltedPasswordEncryptor.getEncryptedPassword(password, salt);
        assertEquals(expectedResult, result);
    }
}

