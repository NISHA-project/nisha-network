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
package pl.nask.nisha.commons.network;

import pl.nask.nisha.commons.security.AsciiPrintableValidator;

public class NodeRingInfo {

    public static final int PORT_MIN = 1;
    public static final int PORT_MAX = 65535;

    private String _id;
    private String _rev;

    private String nodeDomainNameFromRingInfo;
    private String portNumberFromRingInfo;
    private String role;
    private String state;
    private String stateReason;

    public NodeRingInfo() {
    }

    public NodeRingInfo(String nodeDomainNameFromRingInfo, String portNumberFromRingInfo, String nodeRoleName, String nodeStateName, String stateReason, boolean valuesValidated) {
        if (valuesValidated) {
            validateNodeDomainNamePortRoleStateOk(nodeDomainNameFromRingInfo, portNumberFromRingInfo, nodeRoleName, nodeStateName);
        }
        this.nodeDomainNameFromRingInfo = nodeDomainNameFromRingInfo;
        this.portNumberFromRingInfo = portNumberFromRingInfo;
        this.role = nodeRoleName;
        this.state = nodeStateName;
        this.stateReason = stateReason;
    }

    public NodeRingInfo(String nodeDomainNameFromRingInfo, String portNumberFromRingInfo, NodeRole role, NodeState nodeState, String stateReason, boolean valuesValidated) {
        this(nodeDomainNameFromRingInfo, portNumberFromRingInfo, role.name(), nodeState.name(), stateReason, valuesValidated);
    }

    public NodeRingInfo(String _id, String _rev, String nodeDomainNameFromRingInfo, String portNumberFromRingInfo, NodeRole role, NodeState state, String stateReason, boolean valuesValidated) {
        this(nodeDomainNameFromRingInfo, portNumberFromRingInfo, role.name(), state.name(), stateReason, valuesValidated);
        this._id = _id;
        this._rev = _rev;
    }

    public String getNodeDomainNameFromRingInfo() {
        return nodeDomainNameFromRingInfo;
    }

    public void setNodeDomainNameFromRingInfo(String nodeDomainNameFromRingInfo) {
        this.nodeDomainNameFromRingInfo = nodeDomainNameFromRingInfo;
    }

    public String getPortNumberFromRingInfo() {
        return portNumberFromRingInfo;
    }

    public void setPortNumberFromRingInfo(String portNumberFromRingInfo) {
        this.portNumberFromRingInfo = portNumberFromRingInfo;
    }

    public String getUri() {
        return "http://" + nodeDomainNameFromRingInfo + ":" + portNumberFromRingInfo;
    }


    public String getRole() {
        return role;
    }

    public void setRole(String nodeRoleName) {
        if (validateRoleOk(nodeRoleName)) {
            this.role = nodeRoleName;
        }
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        if(this.state != null && this.state.equals(NodeState.REMOVED.name())) {
            throw new IllegalStateException("node state is REMOVED which means it cannot be changed");
        }
        if (validateStateOk(state)){
            this.state = state;
        }
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

    public boolean validateNodeDomainNamePortRoleStateOk(String nodeDomainName, String port, String roleName, String stateName) {
        return validateNodeDomainNameOK(nodeDomainName) && validatePortOK(port) && validateRoleOk(roleName) && validateStateOk(stateName);
    }

    public boolean validateNodeDomainNameOK(String nodeDomainName) {
        if (nodeDomainName == null || nodeDomainName.trim().isEmpty()) {
            throw new IllegalArgumentException("Node domain name cannot be null nor empty");
        }

        try{
            return AsciiPrintableValidator.isStringOfAsciiPrintableChars(nodeDomainName);
        } catch (IllegalArgumentException e) {
            String msg = nodeDomainName + " - node domain name must stick to ASCII charset";
            throw new IllegalArgumentException(msg);
        }
    }

    public boolean validatePortOK(String port) {
        if (port == null || port.trim().isEmpty()) {
            throw new IllegalArgumentException("port number cannot be null nor empty");
        }
        try {
            int portNum = Integer.parseInt(port);
            if (portNum < PORT_MIN || portNum > PORT_MAX) {
                throw new IllegalArgumentException(portNumberFromRingInfo + " - port number is out of range <" + PORT_MIN + ", " + PORT_MAX + ">");
            }
            return true;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(portNumberFromRingInfo + " - port number is not an integer number");
        }
    }

    public boolean validateRoleOk (String roleName) {
        if (roleName == null || roleName.trim().isEmpty()) {
            throw new IllegalArgumentException("Node role cannot be null nor empty");
        }

        if(NodeRole.getNameList().contains(roleName)) {
            return true;
        } else {
            throw new IllegalArgumentException(roleName + " - unknown node role");
        }
    }

    public boolean validateStateOk (String stateName) {
        if (stateName == null || stateName.trim().isEmpty()) {
            throw new IllegalArgumentException("Node state cannot be null nor empty");
        }

        if(NodeState.getNameList().contains(stateName)) {
            return true;
        } else {
            throw new IllegalArgumentException(stateName + " - unknown node state");
        }
    }

    public String getStateReason() {
        return stateReason;
    }

    public void setStateReason(String stateReason) {
        this.stateReason = stateReason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NodeRingInfo)) return false;

        NodeRingInfo that = (NodeRingInfo) o;

        if (_id != null ? !_id.equals(that._id) : that._id != null) return false;
        if (nodeDomainNameFromRingInfo != null ? !nodeDomainNameFromRingInfo.equals(that.nodeDomainNameFromRingInfo) : that.nodeDomainNameFromRingInfo != null) return false;
        if (portNumberFromRingInfo != null ? !portNumberFromRingInfo.equals(that.portNumberFromRingInfo) : that.portNumberFromRingInfo != null) return false;
        if (role != null ? !role.equals(that.role) : that.role != null) return false;
        if (state != null ? !state.equals(that.state) : that.state != null) return false;
        if (stateReason != null ? !stateReason.equals(that.stateReason) : that.stateReason != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = _id != null ? _id.hashCode() : 0;
        result = 31 * result + (nodeDomainNameFromRingInfo != null ? nodeDomainNameFromRingInfo.hashCode() : 0);
        result = 31 * result + (portNumberFromRingInfo != null ? portNumberFromRingInfo.hashCode() : 0);
        result = 31 * result + (role != null ? role.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (stateReason != null ? stateReason.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "NodeRingInfo{" +
                " nodeDomainNameFromRingInfo='" + nodeDomainNameFromRingInfo + '\'' +
                ", portNumberFromRingInfo='" + portNumberFromRingInfo + '\'' +
                ", role='" + role + '\'' +
                ", state='" + state + '\'' +
                ", stateReason='" + stateReason + '\'' +
                '}';
    }

    public String toStringVerbose() {
        return "NodeRingInfo{" +
                "_id='" + _id + '\'' +
                ", _rev='" + _rev + '\'' +
                ", nodeDomainNameFromRingInfo='" + nodeDomainNameFromRingInfo + '\'' +
                ", portNumberFromRingInfo='" + portNumberFromRingInfo + '\'' +
                ", role=" + role +
                ", state=" + state +
                ", stateReason='" + stateReason + '\'' +
                '}';
    }
}
