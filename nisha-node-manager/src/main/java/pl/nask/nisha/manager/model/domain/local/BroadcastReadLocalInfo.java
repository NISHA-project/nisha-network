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

import java.util.HashMap;
import java.util.Map;

public class BroadcastReadLocalInfo {

    private String _id;
    private String _rev;

    public final String type = "BroadcastReadLocalInfo";
    private Map<String, String> broadcastReadLocalMessageIds = new HashMap<String, String>();

    public BroadcastReadLocalInfo() {
    }

    public String get_id() {
        return _id;
    }

    public String get_rev() {
        return _rev;
    }

    public Map<String, String> getBroadcastReadLocalMessageIds() {
        return broadcastReadLocalMessageIds;
    }

    public void setBroadcastReadLocalMessageIds(Map<String, String> broadcastReadLocalMessageIds) {
        this.broadcastReadLocalMessageIds = broadcastReadLocalMessageIds;
    }

    @Override
    public String toString() {
        return "BroadcastReadLocalInfo{" +
                "_id='" + _id + '\'' +
                ", _rev='" + _rev + '\'' +
                ", type='" + type + '\'' +
                ", broadcastReadLocalMessageIds=" + broadcastReadLocalMessageIds +
                '}';
    }
}

