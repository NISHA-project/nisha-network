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
package pl.nask.nisha.manager.model.logic.alerts;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import pl.nask.nisha.commons.network.AlertState;
import pl.nask.nisha.commons.network.NetworkAlert;
import pl.nask.nisha.manager.model.transfer.servletsupport.AttrParamValues;

import static junit.framework.Assert.assertEquals;

public class AlertSearchTest {


    private List<NetworkAlert> prepareAlertList(){
        List<NetworkAlert> list = new ArrayList<NetworkAlert>();
        list.add(new NetworkAlert("description", "time", "http://affected1.node.name:5984", "http://detecting1.node.name:5984", AlertState.ACTIVE));
        list.add(new NetworkAlert("description", "time", "http://affected2.node.name:5984", "http://detecting2.node.name:5984", AlertState.ACTIVE));
        list.add(new NetworkAlert("description", "time", "http://affected3.node.name:5984", "http://detecting3.node.name:5984", AlertState.ACTIVE));

        return list;
    }

    @Test
    public void filterByNodeNameTest() {
        List<NetworkAlert> alertList = prepareAlertList();

        List<NetworkAlert> result = AlertSearch.filterByNodeName(alertList, "node", AttrParamValues.SEARCH_ALERT_MODE_AFF_NAME.val);
        assertEquals(3, result.size());

        result = AlertSearch.filterByNodeName(alertList, "affected1", AttrParamValues.SEARCH_ALERT_MODE_AFF_NAME.val);
        assertEquals(1, result.size());

        result = AlertSearch.filterByNodeName(alertList, "detecting", AttrParamValues.SEARCH_ALERT_MODE_DET_NAME.val);
        assertEquals(3, result.size());

        result = AlertSearch.filterByNodeName(alertList, "detecting1", AttrParamValues.SEARCH_ALERT_MODE_DET_NAME.val);
        assertEquals(1, result.size());

    }

    @Test
    public void getNodeWithAlertsNames() {
        List<NetworkAlert> alerts = prepareAlertList();
        Set<String> expected = new HashSet<String>();
        expected.add("affected1.node.name");
        expected.add("affected2.node.name");
        expected.add("affected3.node.name");

        List<String> results = AlertSearch.getNodeWithAlertsNames(alerts);
        assertEquals(expected, new HashSet<String>(results));

    }

}

