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

import pl.nask.nisha.commons.node.Operator;

public class OperatorContact {
    private String _id;
    private String _rev;

    private String operatorContactId;
    private String contextNodeName;
    private boolean isBlocked = false;
    private String fullName;
    private String email;
    private String telephone;
    private String certificate;

    public OperatorContact() {
    }

    public OperatorContact(Operator op) {
        this.operatorContactId = op.getOperatorId();
        this.contextNodeName = op.getContextNodeName();
        this.isBlocked = op.getBlocked();
        this.fullName = op.getFullName();
        this.email = op.getEmail();
        this.telephone = op.getTelephone();
        this.certificate = op.getCertificate();
    }

    public void loadContactProps(Operator op) {
        this.fullName = op.getFullName();
        this.email = op.getEmail();
        this.telephone = op.getTelephone();
        this.certificate = op.getCertificate();
    }

    public String get_id() {
        return _id;
    }

    public String get_rev() {
        return _rev;
    }

    public String getOperatorContactId() {
        return operatorContactId;
    }

    public String getContextNodeName() {
        return contextNodeName;
    }

    public void setContextNodeName(String contextNodeName) {
        this.contextNodeName = contextNodeName;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getCertificate() {
        return certificate;
    }

    public Boolean getBlocked() {
        return isBlocked;
    }

    public void setBlocked(Boolean blocked) {
        isBlocked = blocked;
    }

    @Override
    public String toString() {
        return "OperatorContact[operatorContactId:" + operatorContactId + ", contextNodeName: " + contextNodeName +
                ", isBlocked:" + isBlocked + ", fullName:" + fullName + "]";
    }

    public String toStringVerbose() {
        return "OperatorContact[" + "operatorContactId:" + operatorContactId + ", contextNodeName:" + contextNodeName +
                ", isBlocked:" + isBlocked + ", fullName:" + fullName + ", email:" + email +
                ", telephone:" + telephone + ", certificate:" + certificate + "]";
    }
}

