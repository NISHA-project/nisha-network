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

import java.util.List;

import org.lightcouch.CouchDbClient;
import org.lightcouch.NoDocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.nisha.manager.model.domain.app.CouchDbConnector;
import pl.nask.nisha.manager.model.domain.local.BroadcastReadLocalInfo;
import pl.nask.nisha.manager.model.domain.messages.Message;
import pl.nask.nisha.manager.model.transfer.servletsupport.AttrParamValues;
import pl.nask.nisha.manager.model.transfer.supportbeans.MessageCountInfoBean;

public class MessageCounter {

    private static final Logger LOG = LoggerFactory.getLogger(MessageCounter.class);

    public static MessageCountInfoBean getUpdatedMessageCountBean() {
        MessageCountInfoBean result = new MessageCountInfoBean();
        result.setInboxUnreadMessages(countInboxOutboxMessages(CouchDbConnector.getCouchDbConnector().messageInboxDbClient));
        result.setOutboxNotSentMessages(countInboxOutboxMessages(CouchDbConnector.getCouchDbConnector().messageOutboxDbClient));
        result = updateReadUnreadBroadcastMessageInfo(result);
        return result;
    }

    public static int countInboxOutboxMessages(CouchDbClient client) {
        String mode = "> never printed value<";
        int messagesCount;
        try {
            if (client.equals(CouchDbConnector.getCouchDbConnector().messageInboxDbClient)){
                mode = AttrParamValues.INBOX.val;
                messagesCount = client.view("nisha-message-inbox/messages_unread").includeDocs(false).reduce(true).queryForInt();
            } else if (client.equals(CouchDbConnector.getCouchDbConnector().messageOutboxDbClient)) {
                mode = AttrParamValues.OUTBOX.val;
                messagesCount = client.view("nisha-message-outbox/messages_unsent").includeDocs(false).reduce(true).queryForInt();
            } else {
                throw new IllegalArgumentException("only inbox and outbox clients allowed as argument and found client to base " + client.getDBUri());
            }
        } catch (NoDocumentException ex) {
            messagesCount = 0;
        }
        LOG.debug(mode + " message counter: " + messagesCount);
        return messagesCount;
    }

    public static MessageCountInfoBean updateReadUnreadBroadcastMessageInfo(MessageCountInfoBean resultBean) {

        BroadcastReadLocalInfo broadcastReadLocalInfo = getLocalBroadcastReadInfo();
        int readMessagesNumber = broadcastReadLocalInfo.getBroadcastReadLocalMessageIds().size();

        List<Message> allBroadcastMessageList = MessageSearch.findAllBroadcastMessages();
        int allBroadcastMessagesNumber = allBroadcastMessageList.size();

        resultBean.setBroadcastUnreadMessages(allBroadcastMessagesNumber - readMessagesNumber);
        resultBean.setBroadcastReadMessagesIds(broadcastReadLocalInfo.getBroadcastReadLocalMessageIds());
        return resultBean;

    }

    public static BroadcastReadLocalInfo getLocalBroadcastReadInfo() {
        CouchDbClient localClient = CouchDbConnector.getCouchDbConnector().localDbClient;

        List<BroadcastReadLocalInfo> readBroadcastInfoListLocal = localClient.view("nisha-local/broadcast_read_info")
                                                                .includeDocs(true).query(BroadcastReadLocalInfo.class);

        if (readBroadcastInfoListLocal.size() == 0) {
            BroadcastReadLocalInfo info = new BroadcastReadLocalInfo();
            localClient.save(info);
            return info;
        } else if (readBroadcastInfoListLocal.size() == 1) {
            return readBroadcastInfoListLocal.get(0);
        }else {
            throw new IllegalStateException("BroadcastReadLocalInfo: 0 or 1 document was expected but found: " + readBroadcastInfoListLocal.size());
        }

    }
}

