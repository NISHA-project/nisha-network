/**
 * ****************************************************************************
 * Copyright (c) 2012 Research and Academic Computer Network.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * <p/>
 * With financial support from the Prevention, Preparedness and Consequence
 * Management of Terrorism and other Security Related Risks Programme
 * European Commission - Directorate-General Home Affairs
 * <p/>
 * Contributors:
 * Research and Academic Computer Network
 * ****************************************************************************
 */
package pl.nask.nisha.manager.model.logic.messages;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.junit.Test;

import pl.nask.nisha.commons.node.Operator;
import pl.nask.nisha.manager.model.domain.messages.Message;
import pl.nask.nisha.manager.model.domain.messages.MessageType;
import pl.nask.nisha.manager.model.transfer.supportbeans.StringCollection;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class MessageSenderTest {

    @Test
    public void prepareOperatorIdFullNameOkTest() {
        String operatorId = "op1";
        String firstLastName = "Jan Kowalski";
        Operator operator = prepareOperator();
        operator.setFullName(firstLastName);
        operator.setOperatorId(operatorId);
        String expected = "op1: Jan Kowalski";
        String result = MessageSender.prepareOperatorIdFullName(operator);
        assertEquals(expected, result);
    }

    @Test
    public void prepareOperatorIdFullNameNullTest() {
        String operatorId = "op1";
        String firstLastName = null;
        Operator operator = prepareOperator();
        operator.setFullName(firstLastName);
        operator.setOperatorId(operatorId);
        String expected = "op1: ***anonymous***";
        String result = MessageSender.prepareOperatorIdFullName(operator);
        assertEquals(expected, result);
    }

    @Test
    public void prepareOperatorIdFullNameAnonymousTest() {
        String operatorId = "op1";
        String firstLastName = "";
        Operator operator = prepareOperator();
        operator.setFullName(firstLastName);
        operator.setOperatorId(operatorId);
        String expected = "op1: ***anonymous***";
        String result = MessageSender.prepareOperatorIdFullName(operator);
        assertEquals(expected, result);
    }


    @Test (expected = IllegalArgumentException.class)
    public void prepareOperatorIdFullNameNoIdTest() {
        Operator operator = new Operator("", "passHash", "key", "localhost", false, "Jan Kowalski", "jan@domain.com", "123456", "cert");
        String expected = ": ***anonymous***";
        String result = MessageSender.prepareOperatorIdFullName(operator);
        assertEquals(expected, result);
    }
    //----------------------------------------------------------------------------------------------------------------
    @Test
    public void getSortedNodeNamesPortsCollectionOkTest() {
        List<String> namesPorts = Arrays.asList("host1:5984", "host2:5984", "host3:5984", "host4:5984");

        StringCollection result = MessageSender.getSortedNodeNamesPortsCollection(namesPorts);
        List<String> resultList = result.getStringList();
        assertEquals(namesPorts.size(), result.getStringList().size());
        assertTrue(resultList.get(0).equals("host1:5984"));
        assertTrue(resultList.get(1).equals("host2:5984"));
        assertTrue(resultList.get(2).equals("host3:5984"));
        assertTrue(resultList.get(3).equals("host4:5984"));
    }

    //----------------------------------------------------------------------------------------------------------------
    @Test
    public void prepareMessageReplyOkTest() {
        Message parentMsg = prepareMessage();
        Message result = MessageSender.prepareMessageReply(parentMsg, "inbox", "op1:Jan Kowalski", false, "host1:5984");

        List<String> expectedRecipients = Arrays.asList("host0:5984", "host2:5984", "host3:5984");
        assertEquals(expectedRecipients, result.getRecipientNodeIds());
        assertTrue(result.getBody().contains("wrote"));
        assertTrue(result.getBody().contains("Tomasz"));
        assertTrue(result.getSenderOperatorFullName().contains("Jan"));
        assertEquals("host1:5984", result.getSenderNodeNamePort());
    }

    //--------------------------------------------------------------------------------------------------------------
    @Test
    public void prepareJsonNameOkTest() {
        String id = "idxxxxxxxxxxxxxxx";
        String name = "localhost";
        String type = MessageType.BLOCK.name();
        String result = MessageSender.prepareJsonName(type, name, id);
        String expectedJson = "BLOCK_:_localhost_:_idxxxxxxxxxxxxxxx.json";

        assertEquals(expectedJson, result);
    }

    @Test
    public void makeJsonTest() {
        String nodeId = "nodeId";
        String nodeName = "nodeName";
        Map<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("node_id", nodeId);
        paramsMap.put("node_domain", nodeName);

        JSONObject result = MessageSender.makeJson(paramsMap);

        JSONObject expected = new JSONObject(paramsMap);
        assertEquals(expected.toString(), result.toString());
    }

    //--------------------------------------------------------------------------------------------------------------
    private Operator prepareOperator() {
        return new Operator("op1", "passHash", "key", "localhost", false, "Jan Kowalski", "jan@domain.com", "123456", "cert");
    }

    private Message prepareMessage() {
        String text = "this is parent message";
        List<String> recipiens = Arrays.asList("host1:5984", "host2:5984", "host3:5984");
        Message message = new Message();
        message.setBody(text);
        message.setRecipientNodeIds(recipiens);
        message.setSenderNodeNamePort("host0:5984");
        message.setSenderOperatorFullName("opX:Tomasz Nowak");
        message.setSubject("Important message");
        return message;
    }
}

