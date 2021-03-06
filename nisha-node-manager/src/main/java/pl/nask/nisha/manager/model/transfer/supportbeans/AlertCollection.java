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

import pl.nask.nisha.commons.network.NetworkAlert;

public class AlertCollection {
    private List<NetworkAlert> alertList = new ArrayList<NetworkAlert>();

    public List<NetworkAlert> getAlertList() {
        return alertList;
    }

    public void setAlertList(List<NetworkAlert> alertList) {
        this.alertList = alertList;
    }
}

