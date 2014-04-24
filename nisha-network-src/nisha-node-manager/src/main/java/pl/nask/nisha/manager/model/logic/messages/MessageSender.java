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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.*;

import org.json.JSONException;
import org.json.JSONObject;
import org.lightcouch.CouchDbClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.nisha.commons.network.NodeRole;
import pl.nask.nisha.commons.network.NodeState;
import pl.nask.nisha.commons.node.Operator;
import pl.nask.nisha.manager.model.domain.app.CouchDbConnector;
import pl.nask.nisha.manager.model.domain.messages.Message;
import pl.nask.nisha.manager.model.domain.messages.MessageType;
import pl.nask.nisha.manager.model.logic.network.NodeSearch;
import pl.nask.nisha.manager.model.transfer.servletsupport.AttrParamValues;
import pl.nask.nisha.manager.model.transfer.servletsupport.Attrs;
import pl.nask.nisha.manager.model.transfer.servletsupport.Params;
import pl.nask.nisha.manager.model.transfer.supportbeans.NodeRingInfoRichCollection;
import pl.nask.nisha.manager.model.transfer.supportbeans.StringCollection;

public class MessageSender {

    public static final Logger LOG = LoggerFactory.getLogger(MessageSender.class);

    private static final int citationWidthMin = 60;
    private static final int citationMargin = 15;
    private static final String citationPrefix = "> ";

    public static StringCollection getSortedNodeNamesPortsCollection(List<String> nodesNamesPorts) {
        StringCollection nodeNamesCollection = new StringCollection();
        nodeNamesCollection.setStringList(nodesNamesPorts);
        Collections.sort(nodeNamesCollection.getStringList());
        return nodeNamesCollection;
    }

    public static String prepareOperatorIdFullName(Operator loggedOperator) {
        String fullname = loggedOperator.getFullName();
        if (fullname == null || fullname.trim().isEmpty()) {
            fullname = "***anonymous***";
        }
        return loggedOperator.getOperatorId() + ": " + fullname;
    }

    public static Message prepareMessageReply(Message parentMessage, String mode, String operatorMessageFullName,
                                              boolean replyBroadcastSender, String replySenderNodeNamePort) {

//        String replySenderNodeNamePort = LocalConfigUpdater.getThisNodeNameAndPort();

        Message replyMessage = new Message();
        replyMessage.setReferenceId(parentMessage.get_id());

        replyMessage.setSenderNodeNamePort(replySenderNodeNamePort);
        replyMessage.setSenderOperatorFullName(operatorMessageFullName);

        replyMessage.setRecipientNodeIds(resolveReplyRecipients(parentMessage, replySenderNodeNamePort, mode, replyBroadcastSender));
        replyMessage.setSubject("Re: " + parentMessage.getSubject());
        replyMessage.setType(MessageType.MESSAGE);


        String citation = changeTextIntoCitation(parentMessage);
        replyMessage.setBody(citation);

        LOG.info("prepared reply: " + replyMessage.toString());
        return replyMessage;
    }


    private static List<String> resolveReplyRecipients(Message parentMessage, String replySenderNodePort, String mode, boolean replyBroadcastSender) {
        LOG.debug("replySender: " + replySenderNodePort);
        List<String> result = new ArrayList<String>();

        if(replyBroadcastSender) {
            result.add(parentMessage.getSenderNodeNamePort());
            Collections.sort(result);
            return result;
        }

        for (String recipient : parentMessage.getRecipientNodeIds()) {
            result.add(recipient);
        }

        if (mode.equals(AttrParamValues.OUTBOX.val) || mode.equals(AttrParamValues.BROADCAST.val)) {
            return result;
        } else if (mode.equals(AttrParamValues.INBOX.val)) {
            result.remove(replySenderNodePort);
            result.add(parentMessage.getSenderNodeNamePort());
        } else {
            throw new IllegalArgumentException(mode + " - unknown mode");
        }
        Collections.sort(result);
        return result;
    }


    private static String changeTextIntoCitation(Message message) {
        String resultFinal;
        String operatorName = message.getSenderOperatorFullName();
        String nodeId = message.getSenderNodeNamePort();

        StringBuilder result = new StringBuilder();
        result.append("\n");
        result.append(citationPrefix);
        result.append(operatorName);
        result.append(" from ");
        result.append(nodeId);
        result.append(" wrote:\n");

        String[] versTab = message.getBody().split("\n");
        StringBuilder versTmp = new StringBuilder();
        for(String line : versTab){
            if (line.startsWith(citationPrefix)) {
                versTmp.append(citationPrefix);
                versTmp.append(line);
            } else {
                versTmp.append(prepareCitation(line));
            }

        }
        result.append(versTmp);
        resultFinal = result.toString();
        LOG.debug("result: ");
        LOG.debug(resultFinal);
        return resultFinal;
    }

    private static String prepareCitation(String textLine) {
        StringBuilder textBuilder = new StringBuilder(textLine);
        boolean tryToBreakLine = true;
        int indexToBreak;
        int startIndex;
        int lineCount = 1;

        while (tryToBreakLine) {

            startIndex = citationWidthMin * lineCount;
            indexToBreak = textBuilder.indexOf(" ", startIndex);
            if (indexToBreak == -1) {
                LOG.debug("len: " + textBuilder.length());
                if (textBuilder.length() <= startIndex + citationMargin) {
                   return citationPrefix + textBuilder.toString();
                } else {
                   textBuilder.insert(startIndex, "\n" + citationPrefix);
                   lineCount ++;
                }
            }
            else if (indexToBreak >= startIndex) {
                if (indexToBreak <= startIndex + citationMargin) {
                    textBuilder.insert(indexToBreak + 1, "\n" + citationPrefix);
                    lineCount ++;
                } else {
                    textBuilder.insert(startIndex, "\n" + citationPrefix);
                    lineCount ++;
                }
            }
            else {
                tryToBreakLine = false;
            }

        }
        return textBuilder.toString();
    }


    public static StringCollection getFilteredRecipients(boolean onlySupernodes, Message message) {
        LOG.info("filtering supernodes only? " + onlySupernodes);
        Set<String> acceptedStates = new HashSet<String>();
        acceptedStates.add(NodeState.ACTIVE.name());
        NodeRingInfoRichCollection nodeCollection = NodeSearch.findNodeRingInfosByQuery("", AttrParamValues.SEARCH_MODE_NAME.val, acceptedStates);
        StringCollection allActiveNodesPorts = getSortedNodeNamesPortsCollection(nodeCollection.getNodeNamesPorts());
        if(!onlySupernodes) {
            return allActiveNodesPorts;
        }
        else {
            StringCollection result = new StringCollection();
            List<String> resultList = result.getStringList();
            String nodeName;
            for (String recipient : allActiveNodesPorts.getStringList()) {
                nodeName = recipient.split(":")[0];

                if (NodeSearch.findNodeRingInfoByNodeDomainName(nodeName).getRole().equals(NodeRole.SUPERNODE.name())) {
                    resultList.add(recipient);
                    LOG.debug(nodeName + " is supernode");
                } else {
                    LOG.debug(nodeName + " cannot be on supernodes' list");
                }
            }

            if (message != null && message.getRecipientNodeIds() != null) {
                message.getRecipientNodeIds().clear();
            }
            return result;
        }
    }

    public static String prepareJsonName(String messageType, String name, String id) {
        return messageType + AttrParamValues.JSON_MARK.val + name + AttrParamValues.JSON_MARK.val + id + ".json";
    }


    public static String makeBlockNodeJson(String nodeId, String nodeName) {
        Map<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("node_id", nodeId);
        paramsMap.put("node_domain", nodeName);
        JSONObject jsonObject = makeJson(paramsMap);
        return jsonObject.toString();
    }

    public static JSONObject makeJson(Map<String, String> paramsMap) {
        JSONObject jsonObject = new JSONObject();
        validateArguments(paramsMap.keySet(), paramsMap.values());
        String value;
        for (String key : paramsMap.keySet()) {
            value = paramsMap.get(key);
            try {
                jsonObject.put(key, value);
            } catch (JSONException e) {
                throw new IllegalArgumentException(key + ":" + value + " json creation problem - " + e.getMessage());
            }
        }
        return jsonObject;
    }

    private static void validateArguments (Collection<String> jsonElementsNames, Collection<String> jsonElementsValues) {
        notEmptyStringsCheck(jsonElementsNames);
        notEmptyStringsCheck(jsonElementsValues);
        if (jsonElementsNames.size() != jsonElementsValues.size()) {
            throw new IllegalArgumentException("number of json elements names and values must be equal");
        }
    }

    private static void notEmptyStringsCheck(Collection<String> values) {
        int index = 0;
        for (String val : values) {
            if (val == null || val.isEmpty()) {
                throw new IllegalArgumentException("element name cannot be null nor empty (index: " + index + ")");
            }
            index++;
        }
    }


    public static void attachBlockInvalidateJson(Map<String, String> formStringParameters, Message message) throws UnsupportedEncodingException {
        String json = formStringParameters.get(Params.BLOCK_INVALIDATE_JSON.val);
        LOG.info("attaching json: " + json);
        String attachName = formStringParameters.get(Attrs.BLOCK_INVALIDATE_NAME.val);
        InputStream inputStream = new ByteArrayInputStream(json.getBytes(Charset.forName("UTF-8")));
        CouchDbClient outboxClient = CouchDbConnector.getCouchDbConnector().messageOutboxDbClient;
        outboxClient.saveAttachment(inputStream, attachName, "application/json", message.get_id(), message.get_rev());
        LOG.info("json attached - success");
    }
}

