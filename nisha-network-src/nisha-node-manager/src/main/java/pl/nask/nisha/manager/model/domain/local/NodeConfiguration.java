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
package pl.nask.nisha.manager.model.domain.local;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NodeConfiguration implements Serializable{

    private static final long serialVersionUID = 736302287825793811L;

    private String _id;             //id of couch document
    private String _rev;            //revision of couch document

    private String nodeDomainNameFromConfig;
    private String portNumberFromConfig;
    private String description;
    private String location;
    private String certificate;
    private String privateKey;
    private String timestamp;

    private List<String> operatorIdListPermitted = new ArrayList<String>();
    private List<String> operatorIdListBlocked = new ArrayList<String>();


    public NodeConfiguration() {
    }

    public NodeConfiguration(String _id, String _rev, String nodeDomainNameFromConfig, String description, String portNumberFromConfig,
                             String location, String certificate, String privateKey, String timestamp,
                             List<String> operatorIdListPermitted, List<String> operatorIdListBlocked) {

        this();

        this._id = _id;
        this._rev = _rev;

        this.nodeDomainNameFromConfig = nodeDomainNameFromConfig;
        this.description = description;
        this.portNumberFromConfig = portNumberFromConfig;

        setPortNumberFromConfig(portNumberFromConfig);
        this.location = location;
        this.certificate = certificate;
        this.privateKey = privateKey;
        this.timestamp = timestamp;
        this.operatorIdListPermitted = operatorIdListPermitted;
        this.operatorIdListBlocked = operatorIdListBlocked;
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

    public String getNodeDomainNameFromConfig() {
        return nodeDomainNameFromConfig;
    }

    public void setNodeDomainNameFromConfig(String nodeDomainNameFromConfig) {
        this.nodeDomainNameFromConfig = nodeDomainNameFromConfig;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPortNumberFromConfig() {
        return portNumberFromConfig;
    }

    public void setPortNumberFromConfig(String portNumberFromConfig) {
        this.portNumberFromConfig = portNumberFromConfig;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCertificate() {
        return certificate;
    }

    public List<String> getOperatorIdListPermitted() {
        return operatorIdListPermitted;
    }

    public List<String> getOperatorIdListBlocked() {
        return operatorIdListBlocked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NodeConfiguration)) return false;

        NodeConfiguration that = (NodeConfiguration) o;

        if (certificate != null ? !certificate.equals(that.certificate) : that.certificate != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (location != null ? !location.equals(that.location) : that.location != null) return false;
        if (nodeDomainNameFromConfig != null ? !nodeDomainNameFromConfig.equals(that.nodeDomainNameFromConfig) : that.nodeDomainNameFromConfig != null) return false;
        if (operatorIdListBlocked != null ? !operatorIdListBlocked.equals(that.operatorIdListBlocked) : that.operatorIdListBlocked != null)
            return false;
        if (operatorIdListPermitted != null ? !operatorIdListPermitted.equals(that.operatorIdListPermitted) : that.operatorIdListPermitted != null)
            return false;
        if (portNumberFromConfig != null ? !portNumberFromConfig.equals(that.portNumberFromConfig) : that.portNumberFromConfig != null) return false;
        if (privateKey != null ? !privateKey.equals(that.privateKey) : that.privateKey != null) return false;
        if (timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = nodeDomainNameFromConfig != null ? nodeDomainNameFromConfig.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (portNumberFromConfig != null ? portNumberFromConfig.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (certificate != null ? certificate.hashCode() : 0);
        result = 31 * result + (privateKey != null ? privateKey.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        result = 31 * result + (operatorIdListPermitted != null ? operatorIdListPermitted.hashCode() : 0);
        result = 31 * result + (operatorIdListBlocked != null ? operatorIdListBlocked.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "NodeConfiguration{" +
                "_id='" + _id + '\'' +
                ", _rev='" + _rev + '\'' +
                ", nodeDomainNameFromConfig='" + nodeDomainNameFromConfig + '\'' +
                ", description='" + description + '\'' +
                ", portNumberFromConfig='" + portNumberFromConfig + '\'' +
                ", location='" + location + '\'' +
                ", certificate='" + certificate + '\'' +
                ", privateKey='" + privateKey + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", operatorIdListPermitted=" + operatorIdListPermitted +
                ", operatorIdListBlocked=" + operatorIdListBlocked +
                '}';
    }
}

