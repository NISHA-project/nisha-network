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

import java.util.HashMap;
import java.util.Map;

public class MessageCountInfoBean {

    private int inboxUnreadMessages;
    private int outboxNotSentMessages;
    private int broadcastUnreadMessages;
    private Map<String, String> broadcastReadMessagesIds = new HashMap<String, String>();

    public MessageCountInfoBean() {
    }

    public int getInboxUnreadMessages() {
        return inboxUnreadMessages;
    }

    public void setInboxUnreadMessages(int inboxUnreadMessages) {
        this.inboxUnreadMessages = inboxUnreadMessages;
    }

    public int getBroadcastUnreadMessages() {
        return broadcastUnreadMessages;
    }

    public void setBroadcastUnreadMessages(int broadcastUnreadMessages) {
        this.broadcastUnreadMessages = broadcastUnreadMessages;
    }

    public int getOutboxNotSentMessages() {
        return outboxNotSentMessages;
    }

    public void setOutboxNotSentMessages(int outboxNotSentMessages) {
        this.outboxNotSentMessages = outboxNotSentMessages;
    }

    public Map<String, String> getBroadcastReadMessagesIds() {
        return broadcastReadMessagesIds;
    }

    public void setBroadcastReadMessagesIds(Map<String, String> broadcastReadMessagesIds) {
        this.broadcastReadMessagesIds = broadcastReadMessagesIds;
    }

    @Override
    public String toString() {
        return "MessageCountInfoBean{" +
                "inboxUnreadMessages=" + inboxUnreadMessages +
                ", outboxNotSentMessages=" + outboxNotSentMessages +
                ", broadcastUnreadMessages=" + broadcastUnreadMessages +
                ", broadcastReadMessagesIds=" + broadcastReadMessagesIds +
                '}';
    }
}

