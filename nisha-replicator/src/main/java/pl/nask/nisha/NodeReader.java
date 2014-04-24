/**
 * *****************************************************************************
 * Copyright (c) 2012 Research and Academic Computer Network. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the GNU Public License v2.0 which accompanies this
 * distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * With financial support from the Prevention, Preparedness and Consequence
 * Management of Terrorism and other Security Related Risks Programme European
 * Commission - Directorate-General Home Affairs
 *
 * Contributors: Research and Academic Computer Network
 * ****************************************************************************
 */
package pl.nask.nisha;

import java.util.ArrayList;
import java.util.List;

import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbException;
import org.lightcouch.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.nisha.commons.network.NodeRingInfo;

public class NodeReader {

    private static final Logger logger = LoggerFactory.getLogger(NodeReader.class);
    CouchDbClient couchDbClient;
        
    static int getNodeIndex(List<NodeRingInfo> nodes, String source) {
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).getUri().equalsIgnoreCase(source)) {
                return i;
            }
        }
        return -1;
    }
    
    public NodeReader(String couchdbHost, int couchdbPort, String username, String password) {
        couchDbClient = new CouchDbClient("nisha-node-uris", false, "http", couchdbHost, couchdbPort, username, password);
    }

    List<NodeRingInfo> loadAllNodeUris() {
        return loadNodeUris("nisha-node-uris/all_nodes");
    }

    List<NodeRingInfo> loadBasicNodeUris() {
        return loadNodeUris("nisha-node-uris/basicnodes_only");
    }

    public List<NodeRingInfo> loadSuperNodeUris() {
        return loadNodeUris("nisha-node-uris/supernodes_only");
    }

    private List<NodeRingInfo> loadNodeUris(String viewName) {
        try {
            View supernodesView = couchDbClient.view(viewName);
            List<NodeRingInfo> supernodes = supernodesView.includeDocs(true).query(NodeRingInfo.class);
            logger.debug("fetched list of {} nodes", supernodes.size());
            for (NodeRingInfo nui : supernodes) {
                logger.debug("{}", nui.toString());
            }
            return supernodes;
        } catch (CouchDbException ex) {
            logger.error("couldn't fetch node list");
            return new ArrayList();
        }
    }
}
