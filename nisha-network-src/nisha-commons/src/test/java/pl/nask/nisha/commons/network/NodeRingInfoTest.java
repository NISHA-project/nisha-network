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

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class NodeRingInfoTest {

    private NodeRingInfo nodeRingInfo;

    @Before
    public void init() {
        nodeRingInfo = new NodeRingInfo("195.187.240.100", "5984", NodeRole.BASICNODE.name(), NodeState.ACTIVE.name(), "just added", true);
    }

    @Test
    public void validateNodeDomainNameOkIp() {
        String ip = "195.187.240.100";
        boolean result = nodeRingInfo.validateNodeDomainNameOK(ip);
        assertTrue(result);
    }

    @Test
    public void validateNodeDomainNameOkName() {
        String ip = "nisha1";
        boolean result = nodeRingInfo.validateNodeDomainNameOK(ip);
        assertTrue(result);
    }

    //------------------------------------------------------------------------------------------------------------------
    @Test
    public void validatePortOk() {
        String port = "123";
        boolean result = nodeRingInfo.validatePortOK(port);
        assertTrue(result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void validatePortLetters() {
        String port = "12x";
        nodeRingInfo.validatePortOK(port);
        assertTrue(false);
    }

    //------------------------------------------------------------------------------------------------------------------

    @Test
    public void validateRoleOk() {
        String role = NodeRole.UNDEFINED.name();
        boolean result = nodeRingInfo.validateRoleOk(role);
        assertTrue(result);

        role = NodeRole.BASICNODE.name();
        result = nodeRingInfo.validateRoleOk(role);
        assertTrue(result);

        role = NodeRole.SUPERNODE.name();
        result = nodeRingInfo.validateRoleOk(role);
        assertTrue(result);
    }

    @Test (expected = IllegalArgumentException.class)
    public void validateRoleOther() {
        String otherRole = "OTHER";
        assertFalse(NodeRole.getNameList().contains(otherRole));

        nodeRingInfo.validateRoleOk(otherRole);
        assertTrue(false);
    }

    //------------------------------------------------------------------------------------------------------------------
    @Test
    public void constructNodeRingInfoOK() {
        String port = "123";
        String expectedName = "195.187.240.100";
        String expectedRole = NodeRole.SUPERNODE.name();
        String expectedState = NodeState.ACTIVE.name();
        String expectedStateReason = "asd";

        nodeRingInfo = new NodeRingInfo(expectedName, port, expectedRole, expectedState, expectedStateReason, true);
        assertEquals(expectedName, nodeRingInfo.getNodeDomainNameFromRingInfo());
        assertEquals(expectedRole, nodeRingInfo.getRole());
        assertEquals(expectedState, nodeRingInfo.getState());
        assertEquals(expectedStateReason, nodeRingInfo.getStateReason());
    }

    //------------------------------------------------------------------------------------------------------------------
    @Test
    public void validateStateOkTest(){
        String state = NodeState.ACTIVE.name();
        nodeRingInfo.validateStateOk(state);
    }

    @Test (expected = IllegalArgumentException.class)
    public void validateStateNullTest(){
        String state = null;
        nodeRingInfo.validateStateOk(state);
    }

    @Test (expected = IllegalArgumentException.class)
    public void validateStateUnknownTest(){
        String state = "unknown state";
        nodeRingInfo.validateStateOk(state);
    }
}

