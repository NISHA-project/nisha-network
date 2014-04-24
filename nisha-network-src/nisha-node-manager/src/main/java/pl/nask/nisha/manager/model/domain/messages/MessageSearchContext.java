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
package pl.nask.nisha.manager.model.domain.messages;

import org.lightcouch.CouchDbClient;

import pl.nask.nisha.manager.model.domain.app.CouchDbConnector;
import pl.nask.nisha.manager.model.transfer.servletsupport.AttrParamValues;

public class MessageSearchContext {

    private String contextMode;
    private String contextDisplayViewName;
    private String contextDbName;
    private CouchDbClient contextClient;

    public MessageSearchContext() {
    }

    public void loadCurrentContext(String mode, MessageDisplayMode messageDisplayMode) {
        if (mode.equals(AttrParamValues.OUTBOX.val)) {
            contextClient = CouchDbConnector.getCouchDbConnector().messageOutboxDbClient;
            contextDbName = CouchDbConnector.DB_NAME_MESSAGE_OUTBOX;
            contextMode = AttrParamValues.OUTBOX.val;
            contextDisplayViewName = getOutboxDisplayViewName(messageDisplayMode);
        }
        else if (mode.equals(AttrParamValues.INBOX.val)) {
            contextClient = CouchDbConnector.getCouchDbConnector().messageInboxDbClient;
            contextDbName = CouchDbConnector.DB_NAME_MESSAGE_INBOX;
            contextMode = AttrParamValues.INBOX.val;
            contextDisplayViewName = getInboxDisplayViewName(messageDisplayMode);
        }
        else if (mode.equals(AttrParamValues.BROADCAST.val)) {
            contextClient = CouchDbConnector.getCouchDbConnector().messageBroadcastDbClient;
            contextDbName = CouchDbConnector.DB_NAME_MESSAGE_BROADCAST;
            contextMode = AttrParamValues.BROADCAST.val;
            contextDisplayViewName = getBroadcastDisplayViewName(messageDisplayMode);
        }
        else {
            throw new IllegalArgumentException(mode + " - unknown mode allowed values are [inbox, outbox]");
        }
    }

    public String getOutboxDisplayViewName(MessageDisplayMode messageDisplayMode) {
        return getDisplayViewName(CouchDbConnector.DB_NAME_MESSAGE_OUTBOX, messageDisplayMode);
    }

    public String getInboxDisplayViewName(MessageDisplayMode messageDisplayMode) {
        return getDisplayViewName(CouchDbConnector.DB_NAME_MESSAGE_INBOX, messageDisplayMode);
    }

    public String getBroadcastDisplayViewName(MessageDisplayMode messageDisplayMode) {
        return getDisplayViewName(CouchDbConnector.DB_NAME_MESSAGE_BROADCAST, messageDisplayMode);
    }

    public String getDisplayViewName(String dbName, MessageDisplayMode messageDisplayMode) {
        if (messageDisplayMode.equals(MessageDisplayMode.ALL)) {
            return  dbName + CouchDbConnector.VIEW_MESSAGE_BY_DATE_ALL;
        } else {
            return dbName + CouchDbConnector.VIEW_MESSAGE_BY_DATE_NOT_ARCHIVED;
        }

    }

    public void adaptViewNameToMessageDisplayMode(MessageDisplayMode messageDisplayMode){
        if(contextMode.equals(AttrParamValues.OUTBOX.val)) {
            contextDisplayViewName = getOutboxDisplayViewName(messageDisplayMode);
        } else if (contextMode.equals(AttrParamValues.INBOX.val)) {
            contextDisplayViewName = getInboxDisplayViewName(messageDisplayMode);
        }
    }

    public String getContextMode() {
        return contextMode;
    }

    public void setContextMode(String contextMode) {
        this.contextMode = contextMode;
    }

    public String getContextDisplayViewName() {
        return contextDisplayViewName;
    }

    public void setContextDisplayViewName(String contextDisplayViewName) {
        this.contextDisplayViewName = contextDisplayViewName;
    }

    public String getContextDbName() {
        return contextDbName;
    }

    public void setContextDbName(String contextDbName) {
        this.contextDbName = contextDbName;
    }

    public CouchDbClient getContextClient() {
        return contextClient;
    }

    public void setContextClient(CouchDbClient contextClient) {
        this.contextClient = contextClient;
    }

    @Override
    public String toString() {
        return "MessageSearchContext{" +
                "contextMode='" + contextMode + '\'' +
                ", contextDisplayViewName='" + contextDisplayViewName + '\'' +
                ", contextDbName='" + contextDbName + '\'' +
                ", contextClient=" + contextClient.getDBUri() +
                '}';
    }
}

