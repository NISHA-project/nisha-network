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
package pl.nask.nisha.manager.model.domain.network;

import java.util.ArrayList;
import java.util.List;

import pl.nask.nisha.manager.model.domain.local.NodeConfiguration;

public class NodeInfo {
    private String _id;
    private String _rev;

    private String nodeDomainNameFromNodeInfo;
    private String description;
    private String certificate;
    private String location;
    private List<String> operatorIdListPermitted = new ArrayList<String>();

    public NodeInfo() {
    }

    public NodeInfo(NodeConfiguration config) {
        this.nodeDomainNameFromNodeInfo = config.getNodeDomainNameFromConfig();
        this.description = config.getDescription();
        this.certificate = config.getCertificate();
        this.location = config.getLocation();
        this.operatorIdListPermitted = config.getOperatorIdListPermitted();
    }

    public static NodeInfo loadConfigProps(String docId, String docRev, NodeConfiguration config) {
        NodeInfo result = new NodeInfo();
        result.set_id(docId);
        if (docId.trim().isEmpty()) {
            result.set_id(null);
        }
        result.set_rev(docRev);
        if (docRev.trim().isEmpty()) {
            result.set_rev(null);
        }

        result.nodeDomainNameFromNodeInfo = config.getNodeDomainNameFromConfig();
        result.description = config.getDescription();
        result.certificate = config.getCertificate();
        result.location = config.getLocation();
        result.operatorIdListPermitted = config.getOperatorIdListPermitted();
        return result;
    }


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String get_rev() {
        return _rev;
    }

    public void set_rev(String _rev) {
        this._rev = _rev;
    }

    public String getNodeDomainNameFromNodeInfo() {
        return nodeDomainNameFromNodeInfo;
    }

    public String getDescription() {
        return description;
    }

    public String getCertificate() {
        return certificate;
    }

    public String getLocation() {
        return location;
    }

    public List<String> getOperatorIdListPermitted() {
        return operatorIdListPermitted;
    }

    @Override
    public String toString() {
        return "NodeInfo{" +
                "operatorIdListPermitted=" + operatorIdListPermitted +
                ", location='" + location + '\'' +
                ", certificate='" + certificate + '\'' +
                ", description='" + description + '\'' +
                ", nodeDomainNameFromNodeInfo='" + nodeDomainNameFromNodeInfo + '\'' +
                '}';
    }
}

