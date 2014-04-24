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

package pl.nask.nisha.manager.model.logic.alerts;

import org.lightcouch.CouchDbClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.nisha.commons.network.AlertState;
import pl.nask.nisha.commons.network.NetworkAlert;
import pl.nask.nisha.manager.model.domain.app.CouchDbConnector;

public class AlertUpdater {

    public static final Logger LOG = LoggerFactory.getLogger(AlertUpdater.class);

    public static void closeAlert(String alertId) {
        CouchDbClient nodeClient = CouchDbConnector.getCouchDbConnector().nodesDbClient;
        NetworkAlert alert = nodeClient.find(NetworkAlert.class, alertId);
        alert.setAlertState(AlertState.CLOSED);
        nodeClient.update(alert);
        LOG.info("closed alert: " + alert);
    }
}

