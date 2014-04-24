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

package pl.nask.nisha.manager.model.logic.local;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import pl.nask.nisha.commons.node.Operator;
import pl.nask.nisha.manager.model.domain.local.NodeConfiguration;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class LocalOperatorUpdaterTest {

    @Test
    public void getAllOperatorIdsTest() {
        NodeConfiguration config = prepareConfig();
        List<String> result = LocalOperatorUpdater.getAllOperatorIds(config);
        List<String> blockedList = config.getOperatorIdListBlocked();
        List<String> permittedList = config.getOperatorIdListPermitted();
        int expected = blockedList.size() + permittedList.size();
        int resultSize = result.size();
        assertEquals(expected, resultSize);
        assertTrue(result.containsAll(permittedList));
        assertTrue(result.containsAll(blockedList));
    }

    //----------------------------------------------------------------------------------------------------------------
    @Test
    public void checkOperatorHasCriticalPropsOkTest() {
        LocalOperatorUpdater.checkOperatorHasCriticalProps(prepareOperator());
        assertTrue(true);
    }

    @Test (expected = IllegalArgumentException.class)
    public void checkOperatorHasCriticalPropsNullTest() {
        LocalOperatorUpdater.checkOperatorHasCriticalProps(null);
        assertTrue(false);
    }

    @Test (expected = IllegalArgumentException.class)
    public void checkOperatorHasCriticalPropsNoIdTest() {
        Operator operator = new Operator("", "passHash", "key", "localhost", false, "Jan Kowalski", "jan@domain.com", "123456", "cert");
        LocalOperatorUpdater.checkOperatorHasCriticalProps(operator);
        assertTrue(false);
    }

    @Test (expected = IllegalArgumentException.class)
    public void checkOperatorHasCriticalPropsNoPasswordTest() {
        Operator operator = new Operator("op1", "", "key", "localhost", false, "Jan Kowalski", "jan@domain.com", "123456", "cert");
        LocalOperatorUpdater.checkOperatorHasCriticalProps(operator);
        assertTrue(false);
    }


    @Test (expected = IllegalArgumentException.class)
    public void checkOperatorHasCriticalPropsEmailTest() {
        Operator operator = new Operator("op1", "passHash", "key", "localhost", false, "Jan Kowalski", "", "123456", "cert");
        LocalOperatorUpdater.checkOperatorHasCriticalProps(operator);
        assertTrue(false);
    }


    @Test (expected = IllegalArgumentException.class)
    public void checkOperatorHasCriticalPropsNoNodeTest() {
        Operator operator = new Operator("op1", "passHash", "key", "", false, "Jan Kowalski", "jan@domain.com", "123456", "cert");
        LocalOperatorUpdater.checkOperatorHasCriticalProps(operator);
        assertTrue(false);
    }

    private Operator prepareOperator(){
        return new Operator("op1", "passHash", "key", "localhost", false, "Jan Kowalski", "jan@domain.com", "123456", "cert");
    }

    private NodeConfiguration prepareConfig(){
        List<String> permitted = new ArrayList<String>();
        permitted.add("op1");
        permitted.add("op2");
        List<String> blocked = new ArrayList<String>();
        blocked.add("op3");
        blocked.add("op4");
        return new NodeConfiguration("123", "123", "localhost", "my test node configuration", "1234",
                        "polska", "certificate", "key", "timestamp:" + new Date(), permitted, blocked);
    }
}

