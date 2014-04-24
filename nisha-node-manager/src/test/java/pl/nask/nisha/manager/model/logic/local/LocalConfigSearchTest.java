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

import pl.nask.nisha.manager.model.domain.local.NodeConfiguration;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class LocalConfigSearchTest {

    @Test
    public void configWithOperatorsExistsOkTest() {

        List<NodeConfiguration> configurationList = new ArrayList<NodeConfiguration>();
        configurationList.add(prepareConfig());

        boolean result = LocalConfigSearch.configWithOperatorsExists(configurationList);
        assertTrue(result);

    }

    @Test (expected = IllegalStateException.class)
    public void configWithOperatorsExistsNoPermittedTest() {

        List<NodeConfiguration> configurationList = new ArrayList<NodeConfiguration>();
        NodeConfiguration config = prepareConfig();
        config.getOperatorIdListPermitted().clear();
        configurationList.add(config);

        LocalConfigSearch.configWithOperatorsExists(configurationList);
        assertTrue(false);
    }

    @Test (expected = IllegalStateException.class)
    public void configWithOperatorsExistsTooManyConfigsFoundTest() {

        List<NodeConfiguration> configurationList = new ArrayList<NodeConfiguration>();
        NodeConfiguration config = prepareConfig();
        NodeConfiguration config2 = prepareConfig();
        configurationList.add(config);
        configurationList.add(config2);

        LocalConfigSearch.configWithOperatorsExists(configurationList);
        assertTrue(false);
    }

    @Test
    public void configWithOperatorsExistsNoConfigsFoundTest() {

        List<NodeConfiguration> configurationList = new ArrayList<NodeConfiguration>();

        boolean result = LocalConfigSearch.configWithOperatorsExists(configurationList);
        assertFalse(result);
    }

    private NodeConfiguration prepareConfig(){
        List<String> permitted = new ArrayList<String>();
        permitted.add("op1");
        permitted.add("op2");
        List<String> blocked = new ArrayList<String>();
        return new NodeConfiguration("123", "123", "localhost", "my test node configuration", "1234",
                        "polska", "certificate", "key", "timestamp:" + new Date(), permitted, blocked);
    }
}

