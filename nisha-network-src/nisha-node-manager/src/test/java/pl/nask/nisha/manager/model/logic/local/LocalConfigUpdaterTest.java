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

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class LocalConfigUpdaterTest {

    @Test
    public void checkOperatorBlockedOkTest(){
        String idToCheck = "op3";
        NodeConfiguration config = prepareConfig();

        boolean result = LocalConfigUpdater.checkOperatorBlocked(idToCheck, config);
        assertTrue(result);
    }

    @Test
    public void checkOperatorBlockedIsPermittedTest(){
        String idToCheck = "op1";
        NodeConfiguration config = prepareConfig();

        boolean result = LocalConfigUpdater.checkOperatorBlocked(idToCheck, config);
        assertFalse(result);
    }

    @Test (expected = IllegalArgumentException.class)
    public void checkOperatorBlockedUnknownTest(){
        String idToCheck = "opX";
        NodeConfiguration config = prepareConfig();

        boolean result = LocalConfigUpdater.checkOperatorBlocked(idToCheck, config);
        assertFalse(result);
    }

    //-----------------------------------------------------------------------------------------------------------------
//    @Test
//    public void doConfigUpdateOkTest(){
//        NodeConfiguration prevConfig = prepareConfig();
//        NodeConfiguration newConfig = prepareConfig();
//        newConfig.setLocation("pol");
//        String result = LocalConfigUpdater.doConfigUpdate(prevConfig, newConfig, prepareOperator());
//        String expected = LocalConfigUpdater.CONFIG_SAVE_SUCCESS_MESSAGE;
//        assertEquals(expected, result);
//    }        //needs refactoring

    @Test (expected = Exception.class)
    public void doConfigUpdateNoChangesTest(){
        NodeConfiguration prevConfig = prepareConfig();
        NodeConfiguration newConfig = prepareConfig();
        LocalConfigUpdater.doConfigUpdate(prevConfig, newConfig, prepareOperator());
        assertTrue(false);
    }

    @Test (expected = IllegalStateException.class)
    public void doConfigUpdateBlockedOperatorTest(){
        NodeConfiguration prevConfig = prepareConfig();
        NodeConfiguration newConfig = prepareConfig();
        Operator operator = prepareOperator();
        operator.setBlocked(true);
        LocalConfigUpdater.doConfigUpdate(prevConfig, newConfig, operator);
        assertTrue(false);
    }

    //-----------------------------------------------------------------------------------------------------------------

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

    private Operator prepareOperator(){
        return new Operator("op1", "", "", "localhost", false, "Jan Kowalski", "jan@domain.com", "", "");
    }
}

