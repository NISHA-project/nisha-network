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

import java.util.ArrayList;
import java.util.List;

public class NodeRingInfoRichCollection {
    private ArrayList<NodeRingInfoRich> nodeRingInfoRichList = new ArrayList<NodeRingInfoRich>();

    public ArrayList<NodeRingInfoRich> getNodeRingInfoRichList() {
        return nodeRingInfoRichList;
    }

    public List<String> getNodeNames () {
        List<String> names = new ArrayList<String>();
        for(NodeRingInfoRich rich : nodeRingInfoRichList) {
            names.add(rich.getNodeRingInfo().getNodeDomainNameFromRingInfo());
        }
        return names;
    }

    public List<String> getNodeNamesPorts () {
        List<String> namesPorts = new ArrayList<String>();
        for(NodeRingInfoRich rich : nodeRingInfoRichList) {
            namesPorts.add(rich.getNodeRingInfo().getNodeDomainNameFromRingInfo() + ":" + rich.getNodeRingInfo().getPortNumberFromRingInfo());
        }
        return namesPorts;
    }
    @Override
    public String toString() {
        return "NodeRingInfoRichCollection{" +
                "nodeRingInfoRichList=" + nodeRingInfoRichList +
                '}';
    }
}

