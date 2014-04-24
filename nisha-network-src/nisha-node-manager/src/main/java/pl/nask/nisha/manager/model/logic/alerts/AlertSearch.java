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
package pl.nask.nisha.manager.model.logic.alerts;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pl.nask.nisha.commons.network.NetworkAlert;
import pl.nask.nisha.manager.model.domain.app.CouchDbConnector;
import pl.nask.nisha.manager.model.transfer.servletsupport.AttrParamValues;

public class AlertSearch {

    public static List<NetworkAlert> filterByNodeName(List<NetworkAlert> alerts, String query, String searchMode) {
        List<NetworkAlert> results = new ArrayList<NetworkAlert>();
        if (query == null || query.trim().isEmpty()) {
            results = alerts;
        } else {
            if (searchMode.equals(AttrParamValues.SEARCH_ALERT_MODE_AFF_NAME.val)) {
                for (NetworkAlert alert : alerts) {
                    if (alert.getAffectedNodeName().contains(query)) {
                        results.add(alert);
                    }
                }
            } else if (searchMode.equals(AttrParamValues.SEARCH_ALERT_MODE_DET_NAME.val)) {
                for (NetworkAlert alert : alerts) {
                    if (alert.getDetectingNodeName().contains(query)) {
                        results.add(alert);
                    }
                }
            }
        }

        return results;
    }

    public static List<NetworkAlert> findAllAlerts() {
        return CouchDbConnector.getCouchDbConnector().nodesDbClient.view(("nisha-nodes/by_type_alerts")).includeDocs(true).query(NetworkAlert.class);
    }

    public static List<String> getNodeWithAlertsNames (List<NetworkAlert> alerts) {
        Set<String> nodeNames = new HashSet<String>();
        for(NetworkAlert alert : alerts) {
            String name = alert.getAffectedNodeName();
            if(name.startsWith("http://")) {
                name = getNodeNameFromUri(name);
            }
            nodeNames.add(name);
        }
        return new ArrayList<String>(nodeNames);
    }

    private static String getNodeNameFromUri(String uri) {
        try {
            String noProtocolUri = uri.replace("http://", "");
            String[] tmpTab = noProtocolUri.split(":");
            return tmpTab[0];
        } catch (Exception e) {
            throw new IllegalArgumentException("could not restore domain name from uri: " + uri);
        }
    }

    public static NetworkAlert findAlert(String id) {
        return CouchDbConnector.getCouchDbConnector().nodesDbClient.find(NetworkAlert.class, id);
    }
}

