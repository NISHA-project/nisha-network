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
package pl.nask.nisha.manager.model.logic.app;

import java.util.List;

import org.lightcouch.CouchDbClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.nisha.commons.network.NodeRingInfo;
import pl.nask.nisha.manager.model.domain.app.CouchDbConnector;

public class SupernodeIndicator {

    private static final Logger LOG = LoggerFactory.getLogger(SupernodeIndicator.class);
    private static String supernodeToAskName = "";
    private static String supernodeToAskPort = "";

    public static CouchDbClient getSupernodeDbClient() {
            updateSupernodeToAsk();
            LOG.info("supernode to ask {}", supernodeToAskName);
            return CouchDbConnector.getCouchDbConnector().getCouchDbClientFromNodeDomainName(supernodeToAskName, supernodeToAskPort, CouchDbConnector.DB_NAME_RESOURCES);
        }

    private static void updateSupernodeToAsk() throws NullPointerException {
        String msg;
        List<NodeRingInfo> activeSupernodeUris =
                CouchDbConnector.getCouchDbConnector().urisDbClient.view(("nisha-node-uris/supernodes_only_active")).includeDocs(true).query(NodeRingInfo.class);

        if (supernodeToAskName.trim().isEmpty()) {
            if (activeSupernodeUris.size() > 0) {
                NodeRingInfo chosenSuperNode = activeSupernodeUris.get(0);
                supernodeToAskName = chosenSuperNode.getNodeDomainNameFromRingInfo();
                supernodeToAskPort = chosenSuperNode.getPortNumberFromRingInfo();
            } else {
                msg = "no supernodes in nisha-uris db";
                LOG.debug("{}", msg);
                throw new IllegalArgumentException(msg);
            }
        }

        int index = -1;
        for (int i = 0; i < activeSupernodeUris.size(); i++) {
            if (activeSupernodeUris.get(i).getNodeDomainNameFromRingInfo().equals(supernodeToAskName)) {
                index = i;
            }
        }
        if (index == -1) {
            msg = "supernode to ask not found";
            LOG.info("{}", msg);
            throw new IllegalArgumentException(msg);
        }
        index = (index + 1) % activeSupernodeUris.size();
        NodeRingInfo chosenSuperNode = activeSupernodeUris.get(index);
        supernodeToAskName = chosenSuperNode.getNodeDomainNameFromRingInfo();
        supernodeToAskPort = chosenSuperNode.getPortNumberFromRingInfo();
    }
}

