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
package pl.nask.nisha.commons.node;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class OperatorTest {

    private Operator operator;

    @Before
    public void init() {
        operator = new Operator();
    }

    //----------------------------------------------------------------------------------------------------------------
    @Test
    public void setEmailOkTest() {
        String email = "name@host.domain";
        operator.setEmail(email);

        assertEquals(email, operator.getEmail());
    }

    @Test (expected = IllegalArgumentException.class)
    public void setEmailMalformedTest() {
        String email = "malformed.email";
        operator.setEmail(email);

        assertTrue(false);
    }

    //----------------------------------------------------------------------------------------------------------------
    @Test
    public void getHasPasswordStringOkTest() {
        String pass = "pass";
        operator.hashAndSavePassword(pass);
        String result = operator.getHasPasswordString();
        assertEquals(Operator.HAS_PASSWORD, result);
    }

}

