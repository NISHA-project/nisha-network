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
package pl.nask.nisha.commons.network;

import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NodeRoleTest {

    @Test
    public void getNameListTest() {
        int expectedRolesNumber = 3;
        assertEquals(expectedRolesNumber, NodeRole.values().length);

        Object result = NodeRole.getNameList();
        assertTrue(result instanceof List);
        List resultList = (List) result;

        assertEquals(expectedRolesNumber, resultList.size());
        assertTrue(resultList.get(0) instanceof String);
        assertTrue(resultList.get(1) instanceof String);
        assertTrue(resultList.get(2) instanceof String);
        assertTrue(resultList.contains("SUPERNODE"));
        assertTrue(resultList.contains("BASICNODE"));
        assertTrue(resultList.contains("UNDEFINED"));

    }
}

