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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.SerializedName;
import org.lightcouch.Attachment;

import pl.nask.nisha.commons.NishaDateTime;

public class Message implements Comparable<Message>{

    public static final DateFormat messageDateFormat = new SimpleDateFormat(NishaDateTime.dateTimePattern);
    public static final long MAX_ATTACHMENT_SIZE = 5 * 1024 * 1024;   //    5MB -> bytes

    private String _id;
    private String _rev;

    private String timeDate;    // for type Date there were problems with achieving both: sorting records and pagination - at the same time
    private String subject;
    private String body;
    private List<String> recipientNodeIds;
    private String senderNodeNamePort;
    private String senderOperatorFullName;
    private MessageState messageState;
    private MessageType type;
    private String referenceId;
    @SerializedName("_attachments")
    private Map<String, Attachment> attachments;


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String get_rev() {
        return _rev;
    }

    public void set_rev(String _rev) {
        this._rev = _rev;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Map<String, Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(Map<String, Attachment> attachments) {
        this.attachments = attachments;
    }

    public List<String> getRecipientNodeIds() {
        return recipientNodeIds;
    }

    public void setRecipientNodeIds(List<String> recipientNodeIds) {
        this.recipientNodeIds = recipientNodeIds;
    }

    public String getSenderNodeNamePort() {
        return senderNodeNamePort;
    }

    public void setSenderNodeNamePort(String senderNodeNamePort) {
        this.senderNodeNamePort = senderNodeNamePort;
    }

    public String getSenderOperatorFullName() {
        return senderOperatorFullName;
    }

    public void setSenderOperatorFullName(String senderOperatorFullName) {
        this.senderOperatorFullName = senderOperatorFullName;
    }

    public MessageState getMessageState() {
        return messageState;
    }

    public void setMessageState(MessageState messageState) {
        this.messageState = messageState;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }
    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getTimeDate() {
        return timeDate;
    }

    public void setTimeDate(Date date) {
        this.timeDate = messageDateFormat.format(date);
    }

    @Override
    public String toString() {
        return "Message{" +
                "timeDate=" + timeDate +
                ", subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                ", recipientNodeIds=" + recipientNodeIds +
                ", senderNodeNamePort='" + senderNodeNamePort + '\'' +
                ", senderOperatorFullName='" + senderOperatorFullName + '\'' +
                ", messageState=" + messageState +
                ", type=" + type +
                ", referenceId=" + referenceId +
                ", attachments=" + attachments +
                '}';
    }

    public static Message cloneMessage(Message message) {
        Message resultMessage = new Message();

        resultMessage._id = message._id;
        resultMessage._rev = message._rev;
        resultMessage.timeDate = message.timeDate;
        resultMessage.subject = message.subject;
        resultMessage.body = message.body;
        resultMessage.recipientNodeIds = message.recipientNodeIds;
        resultMessage.senderNodeNamePort = message.senderNodeNamePort;
        resultMessage.senderOperatorFullName = message.senderOperatorFullName;
        resultMessage.messageState = message.messageState;
        resultMessage.type = message.type;
        resultMessage.referenceId = message.referenceId;
        if (message.attachments != null) {
            resultMessage.attachments = new HashMap<String, Attachment>();
            resultMessage.attachments.putAll(message.attachments);
        }
        return resultMessage;
    }

    @Override
    public int compareTo(Message o) {
        //inverse
        return o.getTimeDate().compareTo(this.timeDate);
    }

    public long getMaxAttachmentSize() {
        return MAX_ATTACHMENT_SIZE;
    }
}

