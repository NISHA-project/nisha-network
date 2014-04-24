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
package pl.nask.nisha.manager.model.logic.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.nisha.commons.network.NodeRingInfo;
import pl.nask.nisha.commons.network.NodeState;
import pl.nask.nisha.manager.model.domain.app.CouchDbConnector;
import pl.nask.nisha.manager.model.domain.network.NetworkRingInfo;
import pl.nask.nisha.manager.model.transfer.supportbeans.NetworkChangeType;

public class NodeRemover {

    public static final Logger LOG = LoggerFactory.getLogger(NodeRemover.class);

    public static void removeNode(String docidToRemove) {
        String msg;
        if (docidToRemove == null) {
            msg = "document id cannot be null";
            throw new IllegalArgumentException(msg);
        }

        boolean createNetworkDocIfNeeded = false;
        NetworkRingInfo prevNetwork = NetworkUpdater.getNetworkRingInfo(createNetworkDocIfNeeded);
        NodeRingInfo prevNodeRingInfo = NodeSearch.findNodeRingInfoByDocId(docidToRemove);

        try {
            NodeRingInfo nodeRingInfo = NodeSearch.findNodeRingInfoByDocId(docidToRemove);
            nodeRingInfo.setState(NodeState.REMOVED.name());
            CouchDbConnector.getCouchDbConnector().urisDbClient.update(nodeRingInfo);

            boolean successNetworkUpdate = NetworkUpdater.updateNetwork(nodeRingInfo, NetworkChangeType.NODE_REMOVAL);
            if (!successNetworkUpdate) {
                msg = "networkRingInfo update problem";
                throw new IllegalStateException(msg);
            }
        } catch (Exception e) {
            LOG.warn("{}", e.getMessage());
            rollbackRemoveNode(prevNetwork, prevNodeRingInfo);
        }
    }

    private static void rollbackRemoveNode(NetworkRingInfo prevNetwork, NodeRingInfo prevNode) {
        String msg;
        try {
            CouchDbConnector.getCouchDbConnector().urisDbClient.update(prevNetwork);
            CouchDbConnector.getCouchDbConnector().urisDbClient.update(prevNode);
            msg = "add node rollback success";
            LOG.debug("{}", msg);
        } catch (Exception e) {
            msg = "remove node rollback problem";
            LOG.warn("{}", msg);
            throw new IllegalStateException(msg);
        }
    }

}

