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
package pl.nask.nisha.manager.model.transfer.supportbeans;

import pl.nask.nisha.commons.network.NodeRingInfo;

public class NodeRingInfoRich {
    private NodeRingInfo nodeRingInfo;
    private String hasAlerts;

    public NodeRingInfoRich(NodeRingInfo nodeRingInfo, String hasAlerts) {
        this.nodeRingInfo = nodeRingInfo;
        this.hasAlerts = hasAlerts;
    }

    public NodeRingInfo getNodeRingInfo() {
        return nodeRingInfo;
    }

    public String getHasAlerts() {
        return hasAlerts;
    }

    @Override
    public String toString() {
        return "NodeRingInfoRich{" +
                "nodeRingInfo=" + nodeRingInfo +
                ", hasAlerts=" + hasAlerts +
                '}';
    }
}

