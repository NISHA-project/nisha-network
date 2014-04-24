/**
 * ****************************************************************************
 * Copyright (c) 2012 Research and Academic Computer Network.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * <p/>
 * With financial support from the Prevention, Preparedness and Consequence
 * Management of Terrorism and other Security Related Risks Programme
 * European Commission - Directorate-General Home Affairs
 * <p/>
 * Contributors:
 * Research and Academic Computer Network
 * ****************************************************************************
 */
package pl.nask.nisha;

import java.util.Date;
import java.util.List;

import org.lightcouch.CouchDbClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.nisha.commons.network.NetworkAlert;
import pl.nask.nisha.commons.network.NodeState;

public class AlertGenerationState {

    private static final Logger logger = LoggerFactory.getLogger(AlertGenerationState .class);

    private String affectedNode;
    private int replicationFailureCounter;
    private boolean alreadyAlerted;
    private boolean countingEnabled;
    private Date enablingDate;
    private String alertDescription;
    private static final String ALERT_DESCRIPTION_DEFAULT = "replication failed";

    public AlertGenerationState(String affectedNode) {
        this(affectedNode, 0, false, false, ALERT_DESCRIPTION_DEFAULT);
    }

    public AlertGenerationState(String affectedNode, int replicationFailureCounter, boolean alreadyAlerted,
                                boolean countingEnabled, String alertDescription) {
        this.affectedNode = affectedNode;
        this.replicationFailureCounter = replicationFailureCounter;
        this.alreadyAlerted = alreadyAlerted;
        this.countingEnabled = countingEnabled;
        this.alertDescription = alertDescription;
    }

    public void updateStateAfterReplicationFailure(CouchDbClient client, String affectedNode, String thisNodeUri, String alertDescription) {
        synchronizeAlreadyAlerted(client, affectedNode, thisNodeUri);
        if (!alreadyAlerted) {
            if (!countingEnabled) {
                countingEnabled = true;
                enablingDate = new Date();
                this.alertDescription = alertDescription;
            }
            replicationFailureCounter++;
        }
        logger.info("state after FAILURE: " + this.toString());
    }

    private void synchronizeAlreadyAlerted(CouchDbClient client, String affectedNode, String thisNodeUri) {
        String state = NodeState.ACTIVE.name();
        List<NetworkAlert> alertsList = client.view("nisha-nodes/alerts_for_aff_det").includeDocs(true).
                key(affectedNode, thisNodeUri, state).query(NetworkAlert.class);


        if (alertsList == null || alertsList.isEmpty()) {
            if (alreadyAlerted) {
                alreadyAlerted = false;
                replicationFailureCounter = 0;
                countingEnabled = false;
                alreadyAlerted = false;
            }

        } else {
            alreadyAlerted = true;
        }
    }

    public void updateStateAfterAlertGeneration() {
        alreadyAlerted = true;
        countingEnabled = false;
        replicationFailureCounter = 0;
        alertDescription = ALERT_DESCRIPTION_DEFAULT;
        logger.info("state after ALERT: " + this.toString());
    }

    public void updateStateAfterReplicationSuccess() {
        replicationFailureCounter = 0;
        countingEnabled = false;
        alertDescription = ALERT_DESCRIPTION_DEFAULT;
        logger.info("state after SUCCESS: " + this.toString());
    }

    //------------------------------------------------------
    public String getAffectedNode() {
        return affectedNode;
    }

    public void setAffectedNode(String affectedNode) {
        this.affectedNode = affectedNode;
    }

    public int getReplicationFailureCounter() {
        return replicationFailureCounter;
    }

    public void setReplicationFailureCounter(int replicationFailureCounter) {
        this.replicationFailureCounter = replicationFailureCounter;
    }

    public boolean isAlreadyAlerted() {
        return alreadyAlerted;
    }

    public void setAlreadyAlerted(boolean alreadyAlerted) {
        this.alreadyAlerted = alreadyAlerted;
    }

    public boolean isCountingEnabled() {
        return countingEnabled;
    }

    public void setCountingEnabled(boolean countingEnabled) {
        this.countingEnabled = countingEnabled;
    }

    public Date getEnablingDate() {
        return enablingDate;
    }

    public void setEnablingDate(Date enablingDate) {
        this.enablingDate = enablingDate;
    }

    public String getAlertDescription() {
        return alertDescription;
    }

    @Override
    public String toString() {
        return "AlertGenerationState{" +
                "affectedNode='" + affectedNode + '\'' +
                ", alertDescription='" + alertDescription + '\'' +
                ", replicationFailureCounter=" + replicationFailureCounter +
                ", alreadyAlerted=" + alreadyAlerted +
                ", countingEnabled=" + countingEnabled +
                ", enablingDate=" + enablingDate +
                '}';
    }
}
