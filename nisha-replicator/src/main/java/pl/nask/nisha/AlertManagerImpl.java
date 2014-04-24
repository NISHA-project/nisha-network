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

import java.text.DateFormat;
import java.util.Date;
import java.util.Map;

import org.lightcouch.CouchDbClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.nisha.commons.network.AlertState;
import pl.nask.nisha.commons.network.NetworkAlert;

public class AlertManagerImpl implements AlertManager {

    private static final Logger logger = LoggerFactory.getLogger(AlertManagerImpl.class);
    private final int delayFailuresMax;
    private final long delayInMills;
    private Map<String, AlertGenerationState> alertGenerationStateMap;
    private String alertCreationReason;
    private DateFormat dateFormat;

    public AlertManagerImpl(long replicationIntervalInMills, long alertDelayTimeInMills,
                                       Map<String, AlertGenerationState> alertGenerationStateMap,
                                       DateFormat dateFormat) {

        this.delayInMills = alertDelayTimeInMills;
        this.delayFailuresMax = getDelayFailuresMax(replicationIntervalInMills, alertDelayTimeInMills);
        this.alertGenerationStateMap = alertGenerationStateMap;
        this.dateFormat = dateFormat;
    }

    @Override
    public void processReplicationFailure(CouchDbClient nodesClient, String affectedNodeUri, String detectingNodeUri, String alertDescription) {
        AlertGenerationState alertGenerationState = resolveAlertGenerationState(affectedNodeUri);
        alertGenerationState.updateStateAfterReplicationFailure(nodesClient, affectedNodeUri, detectingNodeUri, alertDescription);
        String intro;
        if (alertGenerationState.isAlreadyAlerted()) {
            intro = "already alerted ";
        } else {
            intro = alertGenerationState.getReplicationFailureCounter() + " ";
        }
        logger.info(intro + " replication failure(s) detected to " + affectedNodeUri);

        if (!alertGenerationState.isAlreadyAlerted() && shouldCreateAlert(alertGenerationState)) {
            NetworkAlert alert = new NetworkAlert(alertGenerationState.getAlertDescription(), dateFormat.format(new Date()), affectedNodeUri, detectingNodeUri, AlertState.ACTIVE);
            nodesClient.save(alert);
            alertGenerationState.updateStateAfterAlertGeneration();
            if (alertCreationReason == null || alertCreationReason.trim().isEmpty()) {
                alertCreationReason = "unknown";
            }
            logger.info("ALERT generated, reason: " + alertCreationReason + ". " + alert.toString());
        }
    }

    @Override
    public void processReplicationSuccess(String affectedNodeUri) {
        AlertGenerationState alertGenerationState = resolveAlertGenerationState(affectedNodeUri);
        alertGenerationState.updateStateAfterReplicationSuccess();
        logger.info("replication SUCCESS detected to " + affectedNodeUri + " - (alert delay reset)");
    }

    private boolean shouldCreateAlert(AlertGenerationState state) {
        boolean tooManyFailures = state.getReplicationFailureCounter() >= delayFailuresMax;
        boolean afterTimeout = (new Date().getTime() - state.getEnablingDate().getTime()) > delayInMills;
        if (tooManyFailures) {
            alertCreationReason = "too many failures";
            return true;
        }
        if (afterTimeout) {
            alertCreationReason = "after timeout";
            return true;
        }
        return false;
    }

    private int getDelayFailuresMax(long replicationIntervalInMills, long alertDelayTimeInMills) {
        long delaySeconds = (long) ((double)alertDelayTimeInMills / (double)1000);
        int delayFailuresMax = (int) ((double)alertDelayTimeInMills / (double)replicationIntervalInMills) + 1; // +1 so that alert for sure was not generated before timeout

        logger.info("Alert generation delay: " + delaySeconds + " seconds (" + delayFailuresMax + " failures)");
        return delayFailuresMax;
    }

    private AlertGenerationState resolveAlertGenerationState(String affectedNodeName) {
        if (!alertGenerationStateMap.keySet().contains(affectedNodeName)) {
            AlertGenerationState state = new AlertGenerationState(affectedNodeName);
            alertGenerationStateMap.put(affectedNodeName, state);
        }
        return alertGenerationStateMap.get(affectedNodeName);
    }
}

