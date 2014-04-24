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

import pl.nask.nisha.manager.model.transfer.supportbeans.NetworkChangeType;

public class NetworkRingInfo {

    private String _id;
    private String _rev;

    private String lastChangeDate;

    private NetworkChangeType lastChangeType;
    private String lastChangeNode;
    private List<String> networkNodeNames = new ArrayList<String>();

    public NetworkRingInfo() {
    }

    public String get_id() {
        return _id;
    }

    public String get_rev() {
        return _rev;
    }

    public String getLastChangeDate() {
        return lastChangeDate;
    }

    public void setLastChangeDate(String lastChangeDate) {
        this.lastChangeDate = lastChangeDate;
    }

    public List<String> getNetworkNodeNames() {
        return networkNodeNames;
    }

    public NetworkChangeType getLastChangeType() {
        return lastChangeType;
    }

    public void setLastChangeType(NetworkChangeType lastChangeType) {
        this.lastChangeType = lastChangeType;
    }

    public String getLastChangeNode() {
        return lastChangeNode;
    }

    public void setLastChangeNode(String lastChangeNode) {
        this.lastChangeNode = lastChangeNode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NetworkRingInfo)) return false;

        NetworkRingInfo that = (NetworkRingInfo) o;

        if (_id != null ? !_id.equals(that._id) : that._id != null) return false;
        if (lastChangeDate != null ? !lastChangeDate.equals(that.lastChangeDate) : that.lastChangeDate != null)
            return false;
        if (lastChangeNode != null ? !lastChangeNode.equals(that.lastChangeNode) : that.lastChangeNode != null)
            return false;
        if (lastChangeType != that.lastChangeType) return false;
        if (networkNodeNames != null ? !networkNodeNames.equals(that.networkNodeNames) : that.networkNodeNames != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = _id != null ? _id.hashCode() : 0;
        result = 31 * result + (lastChangeDate != null ? lastChangeDate.hashCode() : 0);
        result = 31 * result + (lastChangeType != null ? lastChangeType.hashCode() : 0);
        result = 31 * result + (lastChangeNode != null ? lastChangeNode.hashCode() : 0);
        result = 31 * result + (networkNodeNames != null ? networkNodeNames.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "NetworkRingInfo{" +
                "lastChangeDate='" + lastChangeDate + '\'' +
                ", lastChangeType=" + lastChangeType +
                ", lastChangeNode='" + lastChangeNode + '\'' +
                ", networkNodeNames=" + networkNodeNames +
                '}';
    }
}

