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
package pl.nask.nisha.manager.model.logic.messages;

import java.util.ArrayList;
import java.util.List;

import org.lightcouch.CouchDbClient;
import org.lightcouch.NoDocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.nisha.manager.model.domain.app.CouchDbConnector;
import pl.nask.nisha.manager.model.domain.messages.Message;
import pl.nask.nisha.manager.model.domain.messages.MessageAndMode;
import pl.nask.nisha.manager.model.transfer.servletsupport.AttrParamValues;

public class MessageSearch {

    public static final Logger LOG = LoggerFactory.getLogger(MessageSearch.class);

    public static Message findMessageById(String messageId, String viewName, CouchDbClient client ) {
        LOG.debug("getting message with id: " + messageId);
        List<Message> messageList = client.view(viewName).includeDocs(true).key(messageId).query(Message.class);
        if (messageList.size() != 1) {
            throw new IllegalStateException("1 message should be found but found: " + messageList.size());
        } else {
            return messageList.get(0);
        }
    }

    public static Message findMessageByIdBroadcast(String messageId) {
        List<Message> messageList = CouchDbConnector.getCouchDbConnector().messageBroadcastDbClient
                .view("nisha-message-broadcast/message_by_id").includeDocs(true).key(messageId).query(Message.class);
        if (messageList.size() != 1) {
            throw new IllegalStateException("1 message should be found but found: " + messageList.size());
        } else {
            return messageList.get(0);
        }
    }

    public static Message findMessageByIdOutbox(String messageId) {
        return findMessageById(messageId, "nisha-message-outbox/message_by_id", CouchDbConnector.getCouchDbConnector().messageOutboxDbClient);
    }

    public static Message findMessageByIdInbox(String messageId) {
        return findMessageById(messageId, "nisha-message-inbox/message_by_id", CouchDbConnector.getCouchDbConnector().messageInboxDbClient);
    }


    public static List<Message> findAllBroadcastMessages() {
        List<Message> allBroadcastMessageList;
        try {
            allBroadcastMessageList = CouchDbConnector.getCouchDbConnector().messageBroadcastDbClient.
                                            view("nisha-message-broadcast/message_by_id").includeDocs(false).query(Message.class);
        } catch (NoDocumentException e) {
            allBroadcastMessageList= new ArrayList<Message>();
        }
        return allBroadcastMessageList;
    }

    public static Message findMessageByIdAndMode(String messageId, String mode) {
        Message message;
        if (mode.equals(AttrParamValues.OUTBOX.val)) {
            message = MessageSearch.findMessageByIdOutbox(messageId);
        }
        else if (mode.equals(AttrParamValues.INBOX.val)) {
            message = MessageSearch.findMessageByIdInbox(messageId);
        }
        else if (mode.equals(AttrParamValues.BROADCAST.val)) {
            message = MessageSearch.findMessageByIdBroadcast(messageId);
        }
        else {
            throw new IllegalStateException(mode + " - not allowed mode value");
        }
        return message;
    }

    public static MessageAndMode getParentMessage(String parentMessageId, String mode) {
        if (parentMessageId == null || parentMessageId.isEmpty()) {
            throw new IllegalArgumentException("message has no parent");
        }

        if (mode.equals(AttrParamValues.BROADCAST.val)) {
            return new MessageAndMode(findMessageByIdBroadcast(parentMessageId), mode);
        }

        Message message;
        String box;
        try {
            message = findMessageByIdInbox(parentMessageId);
            box = AttrParamValues.INBOX.val;
        } catch (Exception e) {
            message = findMessageByIdOutbox(parentMessageId);
            box = AttrParamValues.OUTBOX.val;
        }
        LOG.info("success: " + box + " contained message parent: " + message);
        return new MessageAndMode(message, box);
    }
}

