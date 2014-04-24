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
import javax.servlet.http.HttpServletRequest;

import org.lightcouch.CouchDbClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.nisha.manager.model.domain.app.CouchDbConnector;
import pl.nask.nisha.manager.model.domain.local.BroadcastReadLocalInfo;
import pl.nask.nisha.manager.model.domain.messages.Message;
import pl.nask.nisha.manager.model.domain.messages.MessageSearchContext;
import pl.nask.nisha.manager.model.domain.messages.MessageState;
import pl.nask.nisha.manager.model.domain.messages.MessageType;
import pl.nask.nisha.manager.model.transfer.servletsupport.AttrParamValues;
import pl.nask.nisha.manager.model.transfer.servletsupport.Attrs;

public class MessageUpdater {

    public static final Logger LOG = LoggerFactory.getLogger(MessageUpdater.class);
    public static final String outboxTransitionsDescription = "New -> Sent <-> Archived";
    public static final String inboxMessageTypeTransitionsDescription = "New <-> Read <-> Archived";
    public static final String inboxOtherTypeTransitionsDescription = "New <-> Read <-> Done <-> Archived";

    public static void updateState(List<String> checkedMessagesIdList, MessageSearchContext messageSearchContext,
                                   MessageState newMessageState, HttpServletRequest request) {
        List<String> successList = new ArrayList<String>();
        Message message;
        for (String messageId : checkedMessagesIdList) {
            message = MessageSearch.findMessageByIdAndMode(messageId, messageSearchContext.getContextMode());
            checkStateTransition(messageSearchContext.getContextMode(), message.getType().name(), message.getMessageState().name(), newMessageState.name());
            message.setMessageState(newMessageState);
            messageSearchContext.getContextClient().update(message);
            successList.add(message.getSubject());
        }
        String msg = "state changed to " + newMessageState + " for " + successList;
        LOG.info("{}", msg);
        request.setAttribute(Attrs.MESSAGE.val, msg);
    }

    private static void checkStateTransition(String mode, String messageType, String oldMessageState, String newMessageState) {
        if (!oldMessageState.equals(newMessageState)) {
            if (mode.equals(AttrParamValues.OUTBOX.val)) {
                checkOutboxStateTransition(messageType, oldMessageState, newMessageState);
            }
            else if (mode.equals(AttrParamValues.INBOX.val)) {
                checkInboxStateTransition(messageType, oldMessageState, newMessageState);
            }
        }
    }

    private static void checkOutboxStateTransition (String messageType, String oldMessageState, String newMessageState) {
        if (oldMessageState.equals(MessageState.NEW_MESSAGE.name()) && newMessageState.equals(MessageState.SENT.name()) ||
                oldMessageState.equals(MessageState.SENT.name()) && newMessageState.equals(MessageState.ARCHIVED.name()) ||
                oldMessageState.equals(MessageState.ARCHIVED.name()) && newMessageState.equals(MessageState.SENT.name())) {
            LOG.debug("Type: " + messageType + " - message type transition OK: " + oldMessageState + " -> " + newMessageState);
        }
        else {
            throw new IllegalArgumentException("Type: " + messageType + " - unallowed message state transition: "+ oldMessageState + " -> " + newMessageState +
                    ". Legal transition is: " + outboxTransitionsDescription);
        }
    }

    private static void checkInboxStateTransition (String messageType, String oldMessageState, String newMessageState) {
        if (messageType.equals(MessageType.MESSAGE.name())) {
            checkInboxMessageTypeStateTransition(oldMessageState, newMessageState);
        }
        else {
            checkInboxOtherTypeStateTransition(messageType, oldMessageState, newMessageState);
        }
    }

    private static void checkInboxMessageTypeStateTransition (String oldMessageState, String newMessageState) {
        if (oldMessageState.equals(MessageState.NEW_MESSAGE.name()) && newMessageState.equals(MessageState.READ.name()) ||
                oldMessageState.equals(MessageState.READ.name()) && newMessageState.equals(MessageState.ARCHIVED.name()) ||
                oldMessageState.equals(MessageState.ARCHIVED.name()) && newMessageState.equals(MessageState.READ.name()) ||
                oldMessageState.equals(MessageState.READ.name()) && newMessageState.equals(MessageState.NEW_MESSAGE.name()) ) {
            LOG.debug("Type: MESSAGE - message type transition OK: " + oldMessageState + " -> " + newMessageState);
        }
        else {
            throw new IllegalArgumentException("Type: MESSAGE - unallowed message state transition: "+ oldMessageState + " -> " + newMessageState +
                                ". Legal transition is: " + inboxMessageTypeTransitionsDescription);
        }
    }

    private static void checkInboxOtherTypeStateTransition (String messageType, String oldMessageState, String newMessageState) {
        if (oldMessageState.equals(MessageState.NEW_MESSAGE.name()) && newMessageState.equals(MessageState.READ.name()) ||
                oldMessageState.equals(MessageState.READ.name()) && newMessageState.equals(MessageState.DONE.name()) ||
                oldMessageState.equals(MessageState.DONE.name()) && newMessageState.equals(MessageState.ARCHIVED.name()) ||
                oldMessageState.equals(MessageState.ARCHIVED.name()) && newMessageState.equals(MessageState.DONE.name()) ||
                oldMessageState.equals(MessageState.DONE.name()) && newMessageState.equals(MessageState.READ.name()) ||
                oldMessageState.equals(MessageState.READ.name()) && newMessageState.equals(MessageState.NEW_MESSAGE.name()) ) {
            LOG.debug("Type: " + messageType + " - message type transition OK: " + oldMessageState + " -> " + newMessageState);
        }
        else {
            throw new IllegalArgumentException("Type: " + messageType + " - unallowed message state transition: "+ oldMessageState + " -> " + newMessageState +
                                ". Legal transition is: " + inboxOtherTypeTransitionsDescription);
        }
    }

    public static void updateStateUndoArchive(List<String> checkedMessagesIdList, MessageSearchContext messageSearchContext, HttpServletRequest request) {
        List<String> successList = new ArrayList<String>();
        Message message;
        for (String messageId : checkedMessagesIdList) {
            message = MessageSearch.findMessageByIdAndMode(messageId, messageSearchContext.getContextMode());
            MessageState newMessageState = resolveUndoArchiveState(message.getType(), messageSearchContext.getContextMode());
            message.setMessageState(newMessageState);
            messageSearchContext.getContextClient().update(message);
            successList.add(message.getSubject());
        }
        String msg = "state changed for " + successList;
        LOG.info("{}", msg);
        request.setAttribute(Attrs.MESSAGE.val, msg);
    }

    private static MessageState resolveUndoArchiveState(MessageType messageType, String mode) {

        if (mode.equals(AttrParamValues.OUTBOX.val)) {
            return MessageState.SENT;
        }
        else if (mode.equals(AttrParamValues.INBOX.val)) {
            if (messageType.equals(MessageType.MESSAGE)) {
                return MessageState.READ;
            }
            else {
                return MessageState.DONE;
            }
        }
        else {
            throw new IllegalArgumentException(mode + " - unknown mode");
        }
    }

    public static CouchDbClient resolveCouchDbClientByMode(String mode) {
        if (mode.equals(AttrParamValues.OUTBOX.val)) {
            return CouchDbConnector.getCouchDbConnector().messageOutboxDbClient;
        }
        else if (mode.equals(AttrParamValues.INBOX.val)) {
            return CouchDbConnector.getCouchDbConnector().messageInboxDbClient;
        }
        else if (mode.equals(AttrParamValues.BROADCAST.val)) {
            return CouchDbConnector.getCouchDbConnector().messageBroadcastDbClient;
        }
        else {
            throw new IllegalArgumentException(mode + " - unknown mode");
        }
    }


    public static void markBroadcastMessageIfNeeded(Message message) {
        BroadcastReadLocalInfo broadcastReadLocalInfo = MessageCounter.getLocalBroadcastReadInfo();
        if (broadcastReadLocalInfo.getBroadcastReadLocalMessageIds().keySet().contains(message.get_id())) {
            LOG.debug(message.getSubject() + " - message already marked as read");
        }
        else {
            broadcastReadLocalInfo.getBroadcastReadLocalMessageIds().put(message.get_id(), message.getSubject() + " _id: " + message.get_id());
            CouchDbConnector.getCouchDbConnector().localDbClient.update(broadcastReadLocalInfo);
            LOG.info(message.getSubject() + " - message now marked as read");
        }
    }

    public static void prepareMessageBodyToDisplayRows(Message messageToDisplay) {
        String body = messageToDisplay.getBody();
        LOG.debug("body pre newline processing: " + body);
        body = body.replaceAll("\\r\\n", AttrParamValues.BREAK_MARK.val);
        messageToDisplay.setBody(body);
        LOG.debug("body post newline processing: " + body);
    }

    public static String discardMessage(String draftId) {
        String resultMsg = "";
        if (draftId != null && !draftId.isEmpty()) {
            Message message = MessageSearch.findMessageByIdOutbox(draftId);
            message.setMessageState(MessageState.DISCARDED);
            CouchDbConnector.getCouchDbConnector().messageOutboxDbClient.update(message);
            resultMsg = "already saved ";
        }
        resultMsg += "message draft discarded by operator - success";
        return resultMsg;
    }
}

