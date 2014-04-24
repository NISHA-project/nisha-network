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

public class NetworkAlert {

    private String _id;
    private String _rev;

    private String description;
    private String timestamp;
    private String affectedNodeName;
    private String detectingNodeName;
    private AlertState alertState;

    private String affNameTimeViewKey;  // trick for lightcouch:
    private String detNameTimeViewKey;  // we want pagination of alerts with chronological order with search option -
                                        // lightcouch does not allow complex keys in pagination (which is needed for other features) -
                                        // this field is simple view key which carry information of complex key

    public NetworkAlert(String description, String timestamp, String affectedNodeName,
                            String detectingNodeName, AlertState alertState) {

        this.description = description;
        this.timestamp = timestamp;
        this.affectedNodeName = affectedNodeName;
        this.detectingNodeName = detectingNodeName;
        this.alertState = alertState;

        this.affNameTimeViewKey = affectedNodeName + "," + timestamp;
        this.detNameTimeViewKey = detectingNodeName + "," + timestamp ;
    }

    public String get_id() {
        return _id;
    }

    public String get_rev() {
        return _rev;
    }

    public String getDescription() {
        return description;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getAffectedNodeName() {
        return affectedNodeName;
    }

    public String getDetectingNodeName() {
        return detectingNodeName;
    }

    public AlertState getAlertState() {
        return alertState;
    }

    public void setAlertState(AlertState alertState) {
        this.alertState = alertState;
    }

    public String getAffNameTimeViewKey() {
        return affNameTimeViewKey;
    }

    public void setAffNameTimeViewKey(String affNameTimeViewKey) {
        this.affNameTimeViewKey = affNameTimeViewKey;
    }

    public String getDetNameTimeViewKey() {
        return detNameTimeViewKey;
    }

    public void setDetNameTimeViewKey(String detNameTimeViewKey) {
        this.detNameTimeViewKey = detNameTimeViewKey;
    }

    @Override
    public String toString() {
        return "NetworkAlert{" +
                "description='" + description + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", affectedNodeName='" + affectedNodeName + '\'' +
                ", detectingNodeName='" + detectingNodeName + '\'' +
                ", alertState=" + alertState +
                ", affNameTimeViewKey='" + affNameTimeViewKey + '\'' +
                ", detNameTimeViewKey='" + detNameTimeViewKey + '\'' +
                '}';
    }
}

