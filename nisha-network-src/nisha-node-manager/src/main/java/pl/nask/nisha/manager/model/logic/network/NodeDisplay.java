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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.nisha.manager.model.domain.app.CouchDbConnector;
import pl.nask.nisha.manager.model.domain.network.NodeInfo;

public class NodeDisplay {

    public static final Logger LOG = LoggerFactory.getLogger(NodeDisplay.class);

    public static NodeInfo getNodeInfoByNodeName(String nodeNameToFind) {
        LOG.debug("node name to find: {}", nodeNameToFind);
        String msg;
        List<NodeInfo> nodeInfoList = CouchDbConnector.getCouchDbConnector().nodesDbClient.view(("nisha-nodes/by_type_nodes")).key(nodeNameToFind).includeDocs(true).query(NodeInfo.class);
        NodeInfo result;
        if (nodeInfoList.size() == 1) {
            result = nodeInfoList.get(0);
            LOG.debug("nodeInfo found: {}", result);
            return result;
        } else {
            msg = "node name is null or not unique";
            LOG.warn("{}", msg);
            return null;
        }
    }
}

