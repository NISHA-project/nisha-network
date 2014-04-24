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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.Random;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SaltedPasswordEncryptor {

    public static final Logger LOG = LoggerFactory.getLogger(SaltedPasswordEncryptor.class);
    private static Base64 base64 = new Base64(true);

    public static String generateSalt() throws UnsupportedEncodingException {
        Random r = new SecureRandom();
        byte[] saltBytes = new byte[20];
        r.nextBytes(saltBytes);
        String result = byteToBase64(saltBytes);
        LOG.info("salt generated: " + result + "<");
        return result;
    }

    public static String getEncryptedPassword(String password, String salt) {
        try{
            byte[] passBytes = base64ToByte(password);
            byte[] passHash = DigestUtils.sha256(passBytes);
            byte[] saltBytes = base64ToByte(salt);
            int inputLength = passBytes.length + passHash.length + saltBytes.length;
            byte[] input = new byte[inputLength];
            System.arraycopy(passBytes, 0, input, 0, passBytes.length);
            System.arraycopy(passHash, 0, input, passBytes.length, passHash.length);
            System.arraycopy(saltBytes, 0, input, passBytes.length + passHash.length, saltBytes.length);
            return DigestUtils.sha256Hex(input);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("cannot decode String to bytes");
        }
    }

    public static byte[] base64ToByte(String data) throws IOException {
        return base64.decode(data);
    }


    public static String byteToBase64(byte[] data) {
        return base64.encodeToString(data);
    }
}

