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
package pl.nask.nisha.manager.controllers.messages;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONObject;
import org.lightcouch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.nisha.commons.config.ConfigPropertyName;
import pl.nask.nisha.commons.config.NodeManagerFileConfig;
import pl.nask.nisha.commons.node.Operator;
import pl.nask.nisha.manager.controllers.app.NishaBasicServlet;
import pl.nask.nisha.manager.model.domain.app.CouchDbConnector;
import pl.nask.nisha.manager.model.domain.messages.*;
import pl.nask.nisha.manager.model.logic.app.LoggerUpdater;
import pl.nask.nisha.manager.model.logic.local.LocalConfigUpdater;
import pl.nask.nisha.manager.model.logic.local.LocalOperatorUpdater;
import pl.nask.nisha.manager.model.logic.messages.MessageSearch;
import pl.nask.nisha.manager.model.logic.messages.MessageSender;
import pl.nask.nisha.manager.model.logic.messages.MessageUpdater;
import pl.nask.nisha.manager.model.transfer.servletsupport.AttrParamValues;
import pl.nask.nisha.manager.model.transfer.servletsupport.Attrs;
import pl.nask.nisha.manager.model.transfer.servletsupport.PageJSP;
import pl.nask.nisha.manager.model.transfer.servletsupport.Params;
import pl.nask.nisha.manager.model.transfer.supportbeans.StringCollection;

public class MessageSendController extends NishaBasicServlet{

    private static final long serialVersionUID = 4705148649607652629L;
    public static final Logger LOG = LoggerFactory.getLogger(MessageSendController.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequestGet(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequestPost(request, response);
    }

    protected void processRequestGet(HttpServletRequest request, HttpServletResponse response)
                    throws ServletException, IOException {
        try {
           request.setCharacterEncoding(UTF_8);
            LoggerUpdater.checkLoggerIsStarted();

            String option = request.getParameter(Params.OPTION.val);
            if (request.getParameter(Params.DISCARD_MESSAGE_SUBMIT.val) != null) {
                processDiscardMessage(request, response);
            }
            else if (option != null){
                boolean isResend;
                if (option.equals(AttrParamValues.NEW_MESSAGE.val)) {
                    isResend = false;
                    processNewMessageRequest(isResend, request, response);
                }
                else if (option.equals(AttrParamValues.RESEND.val)) {
                    isResend = true;
                    processNewMessageRequest(isResend, request, response);
                }
                else {
                    throw new IllegalStateException(AttrParamValues.UNKNOWN_ACTION.val);
                }
            }
            else {
                throw new IllegalStateException(AttrParamValues.UNKNOWN_ACTION.val);
            }
        } catch (Exception e) {
            String msg = e.getMessage();
            LOG.warn("{}", msg);
            request.setAttribute(Attrs.MESSAGE.val, msg);
            forwardToJsp(PageJSP.MENU, request, response);
        }
    }

    protected void processRequestPost (HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            request.setCharacterEncoding(UTF_8);
            LoggerUpdater.checkLoggerIsStarted();
            Operator loggedOperator = LocalOperatorUpdater.getThisNodeLoggedOperator(request);
            String operatorMessageFullName = MessageSender.prepareOperatorIdFullName(loggedOperator);

            if (request.getParameter(Params.REPLY_SUBMIT.val) != null) {
                processShowReplyForm(request, response, operatorMessageFullName, false);
            }
            else if (request.getParameter(Params.REPLY_BROADCAST_SENDER_SUBMIT.val) != null) {
                processShowReplyForm(request, response, operatorMessageFullName, true);
            }
            else if (request.getParameter(Params.INVALIDATE_RESOURCE_ASK_SUBMIT.val) != null) {
                processShowBlockInvalidateMessageForm(request, response, Params.INVALIDATE_RESOURCE_ASK_SUBMIT.val);
            }
            else if (request.getParameter(Params.BLOCK_NODE_ASK_SUBMIT.val) != null) {
                processShowBlockInvalidateMessageForm(request, response, Params.BLOCK_NODE_ASK_SUBMIT.val);
            }
            else {
                MultipartFormParameters multipartFormParameters= getParametersMultipartForm(request);
                if (multipartFormParameters.getStringParametersMap().get(Params.SEND_MESSAGE_SUBMIT.val) != null ||
                        multipartFormParameters.getStringParametersMap().get(Params.DRAFT_SUBMIT.val) != null) {

                    processSendMessageWithAttachments(multipartFormParameters, operatorMessageFullName, request, response);
                }
                else {
                    processPrepareRecipients(multipartFormParameters, operatorMessageFullName, request, response);
                }
            }
        } catch (Exception e) {
            String msg = e.getMessage();
            LOG.warn("{}", msg);
            request.setAttribute(Attrs.MESSAGE.val, msg);
            forwardToJsp(PageJSP.MENU, request, response);
        }
    }

    private void processShowReplyForm(HttpServletRequest request, HttpServletResponse response,
                                      String operatorMessageFullName, boolean replyBroadcastSender) throws IOException, ServletException {

        String parentMessageId = request.getParameter(Params.PARENT_REFERENCE_ID.val);
        String mode = request.getParameter(Attrs.MODE.val);

        MessageAndMode parentMessageAndMode = MessageSearch.getParentMessage(parentMessageId, mode);
        Message parentMessage = parentMessageAndMode.getMessage();
        mode = parentMessageAndMode.getMode();
        Message replyMessage = MessageSender.prepareMessageReply(parentMessage, mode, operatorMessageFullName,
                                              replyBroadcastSender, LocalConfigUpdater.getThisNodeNameAndPort());

        request.setAttribute(Attrs.MESSAGE_TO_SHOW.val, replyMessage);
        request.setAttribute(Attrs.REPLY.val, true);
        forwardToJsp(PageJSP.MESSAGE_FORM, request, response);
    }

    private static void processNewMessageRequest (boolean isResend, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Message messageToShow = new Message();
        messageToShow.setType(MessageType.MESSAGE);

        if (isResend) {
            String messageId = request.getParameter(Params._ID.val);
            String mode = request.getParameter(Attrs.MODE.val);
            messageToShow = MessageSearch.findMessageByIdAndMode(messageId, mode);
            request.setAttribute(AttrParamValues.RESEND.val, AttrParamValues.RESEND.val);
        }

        boolean onlySupernodes = messageToShow.getType().equals(MessageType.BLOCK) || messageToShow.getType().equals(MessageType.INVALIDATE);
        StringCollection activeNodeAddresses = MessageSender.getFilteredRecipients(onlySupernodes, messageToShow);

        request.setAttribute(Attrs.MESSAGE_TO_SHOW.val, messageToShow);
        request.setAttribute(Attrs.NODE_NAMES_COLLECTION.val, activeNodeAddresses);
        forwardToJsp(PageJSP.MESSAGE_FORM, request, response);
    }

    private static void processDiscardMessage(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String draftId = request.getParameter(Params.DRAFT_ID.val);
        String msg = MessageUpdater.discardMessage(draftId);
        LOG.info(msg);
        request.setAttribute(Attrs.MESSAGE.val, msg);
        forwardToJsp(PageJSP.MENU, request, response);
    }

    private static  void draftFlowForward (String messageSubject, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String msg = messageSubject + " - message draft saved success";
        LOG.info(msg);
        request.setAttribute(Attrs.MESSAGE.val, msg);
        forwardToJsp(PageJSP.MENU, request, response);
    }

    private static void processSendMessageOrSaveDraft(HttpServletRequest request, HttpServletResponse response,
                                           MultipartFormParameters multipartFormParameters, String operatorMessageFullName) throws IOException, ServletException {
        LOG.info("sending message");
        Map<String, String> stringParameters = multipartFormParameters.getStringParametersMap();
        Message message = loadMessageBasicsFromRequest(null, stringParameters, operatorMessageFullName);
        validateMessage(message);
        message = saveMessageInOutboxWithAttachments(message, multipartFormParameters.getAttachmentParametersMap());

        if (multipartFormParameters.getStringParametersMap().get(Params.DRAFT_SUBMIT.val) != null) {
            draftFlowForward(message.getSubject(), request, response);
            return;
        }

        //try to send
        if (message.getType().equals(MessageType.INVALIDATE) || message.getType().equals(MessageType.BLOCK)){
            MessageSender.attachBlockInvalidateJson(multipartFormParameters.getStringParametersMap(), message);
        }

        CastMode castMode = getCastMode(message.getType(), message.getRecipientNodeIds(),
                stringParameters.get(Attrs.REPLY.val), stringParameters.get(Params.CAST.val));

        processReplication(castMode, message.get_id(), request, response);

    }

    private static boolean validateMessage(Message message) {
        if ( message.getRecipientNodeIds() == null || message.getRecipientNodeIds().isEmpty()) {
            throw new IllegalStateException("message must have recipient");
        }
        else {
            return true;
        }
    }

    private static Message loadMessageBasicsFromRequest(Message resultMessage, Map<String, String> parametersFromRequest,
                                                        String operatorMessageFullName) {
        if (resultMessage == null) {
            resultMessage = new Message();
        }

        MessageType messageType = getMessageType(parametersFromRequest.get(Params.MESSAGE_TYPE.val));

        resultMessage.setType(messageType);
        resultMessage.set_id(parametersFromRequest.get(Params._ID.val));
        resultMessage.set_rev(parametersFromRequest.get(Params._REV.val));
        resultMessage.setMessageState(MessageState.NEW_MESSAGE);
        resultMessage.setSubject(parametersFromRequest.get(Params.SUBJECT.val));
        resultMessage.setBody(parametersFromRequest.get(Params.MESSAGE_TEXT.val));
        resultMessage.setSenderNodeNamePort(parametersFromRequest.get(Params.MESSAGE_SENDER_NODE_NAME_PORT.val));
        resultMessage.setSenderOperatorFullName(operatorMessageFullName);
        resultMessage.setTimeDate(new Date());
        resultMessage.setReferenceId(parametersFromRequest.get(Params.PARENT_REFERENCE_ID.val));

        List<String> recipientNodeIds = resolveRecipientNodeIds(parametersFromRequest);
        resultMessage.setRecipientNodeIds(recipientNodeIds);

        LOG.info("basic loaded: " + resultMessage.toString());
        return resultMessage;
    }

    private static MessageType getMessageType (String messageTypeString) {
        LOG.debug("message messageType: " + messageTypeString);
        if (messageTypeString.equals(MessageType.MESSAGE.name())) {
            return MessageType.MESSAGE;
        } else if (messageTypeString.equals(MessageType.BLOCK.name())) {
            return MessageType.BLOCK;
        } else if (messageTypeString.equals(MessageType.INVALIDATE.name())) {
            return MessageType.INVALIDATE;
        } else {
            throw new IllegalStateException("Unknown message type");
        }
    }

    private static CastMode getCastMode(MessageType messageType, List<String> recipientsList, String replyParam, String castParam) {

        if (replyParam != null && replyParam.equals(Attrs.REPLY.val)) {
            return getCastModeForReply(recipientsList);
        } else {
            return getCastModeForNewMessage(messageType, castParam);
        }
    }
    private static CastMode getCastModeForReply(List<String> recipients) {
        if (recipients.size() == 1 && recipients.get(0).equals(AttrParamValues.BROADCAST.val)) {
            return CastMode.BROADCAST;
        } else {
            return CastMode.MULTICAST;
        }
    }

    private static CastMode getCastModeForNewMessage(MessageType messageType, String cast) {
        CastMode result = null;
        if (messageType.equals(MessageType.BLOCK) || messageType.equals(MessageType.INVALIDATE)) {
            result = CastMode.UNICAST;
        }
        else if (messageType.equals(MessageType.MESSAGE)) {
            if (cast.equalsIgnoreCase(CastMode.BROADCAST.name())) {
                result = CastMode.BROADCAST;
            } else if (cast.equalsIgnoreCase(CastMode.MULTICAST.name())) {
                result = CastMode.MULTICAST;
            }
        }
        else {
            throw new IllegalStateException("unknown message messageType");
        }
        LOG.debug("message cast mode: " + result);
        return result;
    }

    private static List<String> resolveRecipientNodeIds(Map<String, String> parametersFromRequest) {
        String reply = parametersFromRequest.get(Attrs.REPLY.val);
        if (reply != null && reply.equals(Attrs.REPLY.val)) {
            return getRecipientNodeIdsForReply(parametersFromRequest);
        } else {
            return getRecipientNodeIdsForNewMessage(parametersFromRequest);
        }
    }

    private static List<String> getRecipientNodeIdsForNewMessage(Map<String, String> parametersFromRequest) {
        List<String> result = new ArrayList<String>();
        String allNames = parametersFromRequest.get(Params.NODE_PARAMS_NAMES.val);
        LOG.debug("all names: " + allNames);
        if (allNames == null) {
            throw new IllegalStateException(Params.NODE_PARAMS_NAMES.val + " field is missing");
        }

        String[] namesTab = allNames.split(",");
        for (String paramName : namesTab) {
            String nodeId = parametersFromRequest.get(paramName);
            LOG.debug("getting name: " + paramName + " - " + nodeId);
            if (nodeId != null && !nodeId.isEmpty()) {
                result.add(nodeId);
            }

        }
        Collections.sort(result);
        return result;
    }

    private static List<String> getRecipientNodeIdsForReply(Map<String, String> parametersFromRequest) {
        String allRecipientsString = parametersFromRequest.get(Params.REPLY_RECIPIENTS.val);
        LOG.debug("reply recipients: " + allRecipientsString);
        allRecipientsString = allRecipientsString.replace("[", "");
        allRecipientsString = allRecipientsString.replace("]", "");
        String[] addressesTab = allRecipientsString.split(", ");
        for (String addr : addressesTab) {
            LOG.debug("***" + addr);
        }
        List<String> result = Arrays.asList(addressesTab);
        Collections.sort(result);
        return result;
    }

    private static String saveMessageInOutbox(Message message) {
        boolean firstSentTry = (message.get_id() == null || message.get_id().isEmpty());
        CouchDbClient outboxClient = CouchDbConnector.getCouchDbConnector().messageOutboxDbClient;
        Response couchdbResponse;
        if (firstSentTry) {
            if (message.get_id() != null) {
                message.set_id(null);
            }
            if (message.get_rev() != null) {
                message.set_rev(null);
            }
            couchdbResponse = outboxClient.save(message);
        } else {
            message.setMessageState(MessageState.NEW_MESSAGE);
            couchdbResponse = outboxClient.update(message);
        }
        return couchdbResponse.getId();
    }

    private static void processReplication(CastMode castMode, String messageOutboxId,
                                           HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        LOG.debug("preparing replication");
        if (castMode.equals(CastMode.BROADCAST)) {
            prepareForBroadcastReplication(messageOutboxId, request, response);
        } else if (castMode.equals(CastMode.UNICAST) || castMode.equals(CastMode.MULTICAST)) {
            triggerPointPointReplication(CouchDbConnector.DB_NAME_MESSAGE_OUTBOX,
                                         CouchDbConnector.DB_NAME_MESSAGE_INBOX,
                                         messageOutboxId, request, response);
        }
    }

    private static void prepareForBroadcastReplication(String messageIdFromOutbox,
                                           HttpServletRequest request, HttpServletResponse response) {
        if (messageIdFromOutbox == null || messageIdFromOutbox.isEmpty()) {
            throw new IllegalStateException("message id in outbox cannot be null nor empty");
        } else {
            try{
                triggerPointPointReplication(CouchDbConnector.DB_NAME_MESSAGE_OUTBOX,
                                             CouchDbConnector.DB_NAME_MESSAGE_BROADCAST,
                                             messageIdFromOutbox, request, response);
            }
            catch (Exception e) {
                throw new IllegalStateException("prepare message for broadcast - failure " + e.getMessage());
            }
        }
    }

    private static void triggerPointPointReplication(String outboxDatabaseName, String inboxDatabaseName,
                                                     String messageOutboxId, HttpServletRequest request,
                                                     HttpServletResponse response) throws IOException, ServletException {

        CouchDbClient outboxDbClient = CouchDbConnector.getCouchDbConnector().messageOutboxDbClient;
        List<String> failureList = new ArrayList<String>();
        List<String> successList = new ArrayList<String>();
        Message messageFromOutbox = MessageSearch.findMessageByIdOutbox(messageOutboxId);
        List<String> targetList = messageFromOutbox.getRecipientNodeIds();

        for (String targetNode : targetList) {

            String user = NodeManagerFileConfig.getNodeManagerFileConfig(false).getPropertyValue(ConfigPropertyName.COUCHDB_NISHA_USER);
            String pass = NodeManagerFileConfig.getNodeManagerFileConfig(false).getPropertyValue(ConfigPropertyName.COUCHDB_NISHA_PASSWORD);
            String userPass = user + ":" + pass;
            String targetDb;

            if (targetNode.equals(AttrParamValues.BROADCAST.val)) {
                targetDb = CouchDbConnector.prepareTargetDbFullUri(userPass, messageFromOutbox.getSenderNodeNamePort(), inboxDatabaseName);
            } else {
                targetDb = CouchDbConnector.prepareTargetDbFullUri(userPass, targetNode, inboxDatabaseName);
            }

            Replication replication = outboxDbClient.replication();
            replication.source(outboxDatabaseName);
            replication.target(targetDb);
            replication.createTarget(true);
            replication.docIds(messageOutboxId);

            try{
                ReplicationResult replicationResult = replication.trigger();
                if (replicationResult.isOk()) {
                    successList.add(targetNode);
                }
                else {
                    failureList.add(targetNode);
                }
            } catch (CouchDbException e) {
                failureList.add(targetNode);
            }
        }

        successFailureOutboxUpdate(successList, failureList, messageFromOutbox);

        //communicate on jsp
        String msg = "message: " + messageFromOutbox.getSubject() + " sent success: " + successList.toString();
        if (failureList.size() != 0) {
            msg += " - sent failure: " + failureList.toString();
        }
        LOG.info(msg);

        request.setAttribute(Attrs.MESSAGE.val, msg);
        forwardToJsp(PageJSP.MENU, request, response);
    }

    private static void successFailureOutboxUpdate(List<String> successList, List<String> failureList, Message messageFromOutbox) {
        boolean noSuccesses = successList.size() == 0;
        if (!successList.isEmpty()) {
            outboxMessageUpdate(successList, Message.cloneMessage(messageFromOutbox), MessageState.SENT, true, noSuccesses);
        }
        if (!failureList.isEmpty()) {
            outboxMessageUpdate(failureList, Message.cloneMessage(messageFromOutbox), MessageState.NEW_MESSAGE, false, noSuccesses);

        }
        LOG.info("messageFromOutbox in outbox updated");
    }

    private static void outboxMessageUpdate(List<String> recipientsList, Message messageFromOutbox, MessageState newMessageState,
                                            boolean processingSuccess, boolean noSuccesses) {
        CouchDbClient client = CouchDbConnector.getCouchDbConnector().messageOutboxDbClient;
        messageFromOutbox.setRecipientNodeIds(recipientsList);
        messageFromOutbox.setMessageState(newMessageState);
        if (processingSuccess || noSuccesses) {
            client.update(messageFromOutbox);
        } else {
            Map<String, AttachmentProperties> attachmentPropertiesMap = AttachmentProperties.getAttachmentsPropertiesMap(messageFromOutbox, client);
            Message cloneMessage = Message.cloneMessage(messageFromOutbox);
            cloneMessage.set_id(null);
            cloneMessage.set_rev(null);
            cloneMessage.getAttachments().clear();
            saveMessageInOutboxWithAttachments(cloneMessage,attachmentPropertiesMap);
        }
    }

    private static Message saveMessageInOutboxWithAttachments(Message message, Map<String, AttachmentProperties> attachmentPropertiesMap) {
        String messageOutboxId = saveMessageInOutbox(message);
        message = loadAttachments(messageOutboxId, attachmentPropertiesMap, CouchDbConnector.getCouchDbConnector().messageOutboxDbClient);
        LOG.info(message.getSubject() + " message saved in outbox, attachments loaded: " + attachmentPropertiesMap.size() + " - success");
        return message;
    }


    private static Message loadAttachments(String messageOutboxId, Map<String, AttachmentProperties> attachmentMap,
                                                      CouchDbClient outboxClient) {

        Message message = MessageSearch.findMessageByIdOutbox(messageOutboxId);
        if (message == null) {
            throw new IllegalArgumentException("null message for id: " + messageOutboxId);
        }

        String messageRev = message.get_rev();

        Object tmp;
        AttachmentProperties attachmentProperties;
        Response couchResponse;
        for (String key : attachmentMap.keySet()) {
            tmp = attachmentMap.get(key);
            if (tmp instanceof AttachmentProperties) {
                attachmentProperties = (AttachmentProperties)tmp;
                couchResponse = outboxClient.saveAttachment(attachmentProperties.getInputStream(),
                                            attachmentProperties.getAttachName(),
                                            attachmentProperties.getContentType(),
                                            messageOutboxId,
                                            messageRev);

                messageRev = couchResponse.getRev();
            }
        }
        return MessageSearch.findMessageByIdOutbox(messageOutboxId);
    }


    private static MultipartFormParameters getParametersMultipartForm(HttpServletRequest request) {
        MultipartFormParameters result = new MultipartFormParameters();
        FileItem item;
        String fieldname, fieldvalue, filename, contentType;
        InputStream filecontent;
        AttachmentProperties attachmentProperties;
        List items;
        try {
            ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
            upload.setFileSizeMax(Message.MAX_ATTACHMENT_SIZE);
            items = upload.parseRequest(request);

            for (Object itemObject : items) {
                if (itemObject instanceof FileItem) {
                    item = (FileItem)itemObject;
                    if (item.isFormField()) {
                        // non files
                        fieldname = item.getFieldName();
                        fieldvalue = item.getString();
                        result.getStringParametersMap().put(fieldname, fieldvalue);
                    } else {
                        //files
                        fieldname = item.getFieldName();
                        filename = FilenameUtils.getName(item.getName());
                        filecontent = item.getInputStream();
                        contentType = item.getContentType();
                        attachmentProperties = new AttachmentProperties(filename, contentType, filecontent);
                        result.getAttachmentParametersMap().put(fieldname, attachmentProperties);

                    }
                }
            }
        } catch (FileUploadException e) {
            throw new IllegalStateException(e.getMessage());
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        }
        return result;
    }

    private void processResendMessageOrUpdateDraft(HttpServletRequest request, HttpServletResponse response,
                                      MultipartFormParameters multipartFormParameters, String operatorFullName)
                                      throws IOException, ServletException {

        Map<String, String> stringParametersMap = multipartFormParameters.getStringParametersMap();
        String messageId = stringParametersMap.get(Params._ID.val);
        Message messageFromOutbox = MessageSearch.findMessageByIdOutbox(messageId);
        messageFromOutbox = loadMessageBasicsFromRequest(messageFromOutbox, stringParametersMap, operatorFullName);
        CouchDbClient outboxClient = CouchDbConnector.getCouchDbConnector().messageOutboxDbClient;
        outboxClient.update(messageFromOutbox);

        messageFromOutbox = MessageSearch.findMessageByIdOutbox(messageId);
        updateAttachments(messageFromOutbox, outboxClient, multipartFormParameters);

        if (multipartFormParameters.getStringParametersMap().get(Params.DRAFT_SUBMIT.val) != null) {
            draftFlowForward(messageFromOutbox.getSubject(), request, response);
            return;
        }

        CastMode castMode = getCastMode(messageFromOutbox.getType(), messageFromOutbox.getRecipientNodeIds(),
                stringParametersMap.get(Attrs.REPLY.val), stringParametersMap.get(Params.CAST.val));

        processReplication(castMode, messageFromOutbox.get_id(), request, response);

    }

    private void updateAttachments(Message message, CouchDbClient client, MultipartFormParameters multipartFormParameters) {
        List<String> attachToKeep = getAttachmentsToKeep(multipartFormParameters.getStringParametersMap());
        removeUnwantedAttachments(message, attachToKeep, client);
        loadAttachments(message.get_id(), multipartFormParameters.getAttachmentParametersMap(), client);

    }

    private void removeUnwantedAttachments(Message message, List<String> attachmentsToKeep, CouchDbClient client) {
        if (message == null || message.getAttachments() == null) {
            return;
        }

        for (String attachName : message.getAttachments().keySet()) {
            if (!attachmentsToKeep.contains(attachName)) {
                message.getAttachments().remove(attachName);
            }
        }
        client.update(message);

    }

    private List<String> getAttachmentsToKeep(Map<String, String> stringParametersMap) {
        List<String> attachToKeep = new ArrayList<String>();
        for (String key : stringParametersMap.keySet()) {
            if (key.startsWith(Params.ATTACH.val) && !key.equals(Params.ATTACH_PARAMS_NAMES.val)) {
                attachToKeep.add(stringParametersMap.get(key));
            }
        }
        return attachToKeep;
    }

    private void processShowBlockInvalidateMessageForm(HttpServletRequest request, HttpServletResponse response, String submitOption)
                                                     throws IOException, ServletException {
        Message message = new Message();
        StringCollection recipientsFiltered = MessageSender.getFilteredRecipients(true, message);
        message.setRecipientNodeIds(recipientsFiltered.getStringList());
        String name, blockInvalidateJson, jsonName;

        String id = request.getParameter(Params._ID.val);

        if (submitOption.equals(Params.BLOCK_NODE_ASK_SUBMIT.val)) {
            name = request.getParameter(Params.NODE_DOMAIN_NAME_FROM_RING_INFO.val);
            blockInvalidateJson = MessageSender.makeBlockNodeJson(id, name);
            message.setType(MessageType.BLOCK);
            message.setSubject("BLOCK: " + name);
        }
        else if (submitOption.equals(Params.INVALIDATE_RESOURCE_ASK_SUBMIT.val)) {
            name = request.getParameter(Params.ARTICLE_TITLE.val);
            blockInvalidateJson= makeInvalidateResourceJson(id, name);
            message.setType(MessageType.INVALIDATE);
            message.setSubject("INVALIDATE: " + name);
        }
        else {
            throw new IllegalArgumentException(submitOption + " - unknown option");
        }

        jsonName = MessageSender.prepareJsonName(message.getType().name(), name, id);
        LOG.info("json prepared: " + blockInvalidateJson);

        request.setAttribute(Attrs.NODE_NAMES_COLLECTION.val, recipientsFiltered);
        request.setAttribute(Attrs.BLOCK_INVALIDATE_NAME.val, jsonName);
        request.setAttribute(Attrs.BLOCK_INVALIDATE_JSON.val, blockInvalidateJson);
        request.setAttribute(Attrs.MESSAGE_TO_SHOW.val, message);
        forwardToJsp(PageJSP.MESSAGE_FORM, request, response);
    }

    private String makeInvalidateResourceJson(String resourceId, String resourceTitle) {
        Map<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("doc_id", resourceId);
        paramsMap.put("title", resourceTitle);
        JSONObject jsonObject = MessageSender.makeJson(paramsMap);
        return jsonObject.toString();
    }

//    public void processSendOrFilter(HttpServletRequest request, HttpServletResponse response, String operatorMessageFullName)
//                                                                            throws IOException, ServletException {
//
//        MultipartFormParameters multipartFormParameters= getParametersMultipartForm(request);
//
//
//        if (multipartFormParameters.getStringParametersMap().get(Params.SEND_MESSAGE_SUBMIT.val) != null ||
//                multipartFormParameters.getStringParametersMap().get(Params.DRAFT_SUBMIT.val) != null) {
//
//            processSendMessageWithAttachments(multipartFormParameters, operatorMessageFullName, request, response);
//        }
//        else {
//            processFilterRecipients(multipartFormParameters, operatorMessageFullName, request, response);
//        }
//    }

    private void processSendMessageWithAttachments(MultipartFormParameters multipartFormParameters, String operatorMessageFullName,
                                         HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String resendParam = multipartFormParameters.getStringParametersMap().get(AttrParamValues.RESEND.val);
        if (resendParam != null && resendParam.equalsIgnoreCase(Boolean.TRUE.toString())) {
            processResendMessageOrUpdateDraft(request, response, multipartFormParameters, operatorMessageFullName);
        } else {
            processSendMessageOrSaveDraft(request, response, multipartFormParameters, operatorMessageFullName);
        }
    }

    private void processPrepareRecipients(MultipartFormParameters multipartFormParameters, String operatorMessageFullName,
                                         HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        String supernodesOnly = multipartFormParameters.getStringParametersMap().get(Params.FILTER_SUPERNODES_ONLY.val);
        if (supernodesOnly != null && (supernodesOnly.isEmpty() || supernodesOnly.equalsIgnoreCase(Boolean.FALSE.toString()))) {
            processFilterRecipients(false, request, response, multipartFormParameters, operatorMessageFullName);
        }
        else if (supernodesOnly != null && supernodesOnly.equalsIgnoreCase(Boolean.TRUE.toString())) {
            processFilterRecipients(true, request, response, multipartFormParameters, operatorMessageFullName);
        }
        else {
            throw new IllegalStateException(AttrParamValues.UNKNOWN_ACTION.val);
        }
    }

    private void processFilterRecipients(boolean onlySupernodes, HttpServletRequest request, HttpServletResponse response,
                                         MultipartFormParameters multipartFormParameters, String operatorMessageFullName) throws IOException, ServletException {

        Message messageToShow = loadMessageBasicsFromRequest(null, multipartFormParameters.getStringParametersMap(), operatorMessageFullName);
        StringCollection activeFilteredNodeAddresses = MessageSender.getFilteredRecipients(onlySupernodes, messageToShow);
        LOG.info("nodes after filtering: " + activeFilteredNodeAddresses.getStringList());

        request.setAttribute(Attrs.MESSAGE_TO_SHOW.val, messageToShow);
        request.setAttribute(Attrs.NODE_NAMES_COLLECTION.val, activeFilteredNodeAddresses);
        forwardToJsp(PageJSP.MESSAGE_FORM, request, response);
    }
}

