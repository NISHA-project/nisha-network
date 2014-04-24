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
package pl.nask.nisha.manager.model.logic.local;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.nisha.commons.node.Operator;
import pl.nask.nisha.manager.model.domain.app.CouchDbConnector;
import pl.nask.nisha.manager.model.domain.local.NodeConfiguration;
import pl.nask.nisha.manager.model.domain.network.NodeInfo;
import pl.nask.nisha.manager.model.domain.network.OperatorContact;
import pl.nask.nisha.manager.model.logic.network.NodeDisplay;
import pl.nask.nisha.manager.model.logic.network.OperatorContactDisplay;
import pl.nask.nisha.manager.model.transfer.servletsupport.Attrs;
import pl.nask.nisha.manager.model.transfer.supportbeans.OperatorBean;

public class LocalOperatorUpdater {

    public static final Logger LOG = LoggerFactory.getLogger(LocalOperatorUpdater.class);

    public static void blockOperator(String operatorIdToBlock) throws Exception {
        String msg;
        NodeConfiguration prevConfig = LocalConfigUpdater.getNodeConfiguration();
        String nodeName = prevConfig.getNodeDomainNameFromConfig();
        NodeInfo prevNodeInfo = NodeDisplay.getNodeInfoByNodeName(nodeName);
        OperatorContact prevContact = OperatorContactDisplay.findContact(nodeName, operatorIdToBlock);

        NodeConfiguration config = LocalConfigUpdater.getNodeConfiguration();
        if (!config.getOperatorIdListPermitted().contains(operatorIdToBlock)) {
            msg = operatorIdToBlock + " - already blocked or no such operator to block";
            throw new IllegalArgumentException(msg);
        }

        if (config.getOperatorIdListPermitted().size() <= 1) {
            msg = "at least one operator must be configured and not blocked";
            throw new IllegalArgumentException(msg);
        }

        try {
            LOG.debug("block operator - start");
            config.getOperatorIdListPermitted().remove(operatorIdToBlock);
            config.getOperatorIdListBlocked().add(operatorIdToBlock);
            Collections.sort(config.getOperatorIdListBlocked());
            CouchDbConnector.getCouchDbConnector().localDbClient.update(config);

            Operator operatorToBlock = getOperatorFromLocalDb(operatorIdToBlock);
            operatorToBlock.setBlocked(true);
            CouchDbConnector.getCouchDbConnector().localDbClient.update(operatorToBlock);

            NodeInfo nodeInfo = NodeDisplay.getNodeInfoByNodeName(config.getNodeDomainNameFromConfig());
            nodeInfo.getOperatorIdListPermitted().remove(operatorIdToBlock);
            CouchDbConnector.getCouchDbConnector().nodesDbClient.update(nodeInfo);
            OperatorContact blockedContact = OperatorContactDisplay.findContact(config.getNodeDomainNameFromConfig(), operatorIdToBlock);
            blockedContact.setBlocked(true);
            CouchDbConnector.getCouchDbConnector().nodesDbClient.update(blockedContact);
            LOG.info("{} - operator blocked - success", operatorIdToBlock);

        } catch (Exception e) {
            blockUnblockRollback(prevConfig, prevNodeInfo, prevContact);
        }
        LOG.debug("after operator blocking - permitted: {} blocked: {}", config.getOperatorIdListPermitted().toString(), config.getOperatorIdListBlocked().toString());
    }


    public static void unblockOperator(String operatorIdToUnblock, String nodeName, NodeConfiguration prevConfig,
                                         NodeInfo prevNodeInfo, OperatorContact prevContact) {
        LOG.debug("unblocking action");
        NodeConfiguration config = LocalConfigUpdater.getNodeConfiguration();
        if (!config.getOperatorIdListBlocked().contains(operatorIdToUnblock)) {
            String msg = operatorIdToUnblock + " - already unblocked or no such operator";
            throw new IllegalArgumentException(msg);
        }

        try {

            config.getOperatorIdListBlocked().remove(operatorIdToUnblock);
            config.getOperatorIdListPermitted().add(operatorIdToUnblock);
            Collections.sort(config.getOperatorIdListPermitted());
            CouchDbConnector.getCouchDbConnector().localDbClient.update(config);

            Operator operatorToUnblock = getOperatorFromLocalDb(operatorIdToUnblock);
            operatorToUnblock.setBlocked(false);
            CouchDbConnector.getCouchDbConnector().localDbClient.update(operatorToUnblock);

            NodeInfo nodeInfo = NodeDisplay.getNodeInfoByNodeName(config.getNodeDomainNameFromConfig());
            nodeInfo.getOperatorIdListPermitted().add(operatorIdToUnblock);
            CouchDbConnector.getCouchDbConnector().nodesDbClient.update(nodeInfo);
            OperatorContact contact = OperatorContactDisplay.findContact(nodeName, operatorIdToUnblock);
            contact.setBlocked(false);
            CouchDbConnector.getCouchDbConnector().nodesDbClient.update(contact);
            LOG.info("{} - operator unblocked - success", operatorIdToUnblock);
        } catch (Exception e) {
            blockUnblockRollback(prevConfig, prevNodeInfo, prevContact);
        }
    }

    private static void blockUnblockRollback(NodeConfiguration prevConfig, NodeInfo prevNodeInfo, OperatorContact prevContact) {
        LOG.debug("operator block or unblock rollback start...");
        CouchDbConnector.getCouchDbConnector().localDbClient.update(prevConfig);
        CouchDbConnector.getCouchDbConnector().nodesDbClient.update(prevNodeInfo);
        CouchDbConnector.getCouchDbConnector().nodesDbClient.update(prevContact);
        LOG.info("operator block or unblock rollback - success");
    }

    public static List<String> getAllOperatorIds(NodeConfiguration config) {
        List<String> result = new ArrayList<String>();
        result.addAll(config.getOperatorIdListPermitted());
        result.addAll(config.getOperatorIdListBlocked());
        return result;
    }

    public static Operator getOperatorFromLocalDb(String operatoridtoFind) {
        List<Operator> operators = CouchDbConnector.getCouchDbConnector().localDbClient.view(("nisha-local/by_type_operators")).includeDocs(true).key(operatoridtoFind).query(Operator.class);
        if (operators.size() == 1) {
            return operators.get(0);
        }
        LOG.debug("{} - expected 1 operator but found: {}", operatoridtoFind, operators.size());
        return null;
    }

    public static void updateOperatorsContextNodeName (String nodeName) {
        List<Operator> operatorList = getAllOperatorsFromBase();
        for (Operator operator : operatorList) {
           operator.setContextNodeName(nodeName);
           CouchDbConnector.getCouchDbConnector().localDbClient.update(operator);
        }
        LOG.debug("operators updated with context node name " + nodeName);
    }

    public static OperatorContact getContactFromLocalDb(String operatorIdToFind) {
        Operator operator = LocalOperatorUpdater.getOperatorFromLocalDb(operatorIdToFind);
        if (operator == null) {
            throw new IllegalStateException(operatorIdToFind + " - operator unknown");
        }
        OperatorContact contact = new OperatorContact(operator);
        LOG.info("display {}", contact.toStringVerbose());
        return contact;
    }

    public static String doUpdateOperator(Operator operatorToUpdate) {
        checkOperatorHasCriticalProps(operatorToUpdate);
        CouchDbConnector.getCouchDbConnector().localDbClient.update(operatorToUpdate);
        OperatorContact contact = OperatorContactDisplay.findContact(LocalConfigUpdater.getThisNodeDomainNameFromConfig(), operatorToUpdate.getOperatorId());
        contact.loadContactProps(operatorToUpdate);
        LOG.info("changed contact: {}", operatorToUpdate.toStringVerbose());
        CouchDbConnector.getCouchDbConnector().nodesDbClient.update(contact);

        String msg = "operator updated - success";
        LOG.info("{}", msg);
        return msg;
    }

    public static void checkOperatorHasCriticalProps(Operator operator) {
        LOG.debug("checking: {}", operator);
        String msg = "cannot add/update operator - ";
        if (operator == null) {
            msg = "operator is null";
            throw new IllegalArgumentException(msg);
        }
        if (operator.getOperatorId() == null || operator.getOperatorId().trim().isEmpty()) {
            msg += "id is missing";
            throw new IllegalArgumentException(msg);
        } else if (operator.getPasswordHash() == null || operator.getPasswordHash().length() == 0) {
            msg += "password is missing";
            throw new IllegalArgumentException(msg);
        } else if (operator.getEmail() == null || operator.getEmail().trim().isEmpty()) {
            msg += "email is missing";
            throw new IllegalArgumentException(msg);
        } else if (operator.getContextNodeName() == null || operator.getContextNodeName().trim().isEmpty()) {
            msg += "context node name is missing";
            throw new IllegalArgumentException(msg);
        }
    }

    public static List<Operator> getAllOperatorsFromBase() {
        return CouchDbConnector.getCouchDbConnector().localDbClient.view(("nisha-local/by_type_operators")).includeDocs(true).query(Operator.class);
    }

    public static Operator getThisNodeLoggedOperator(HttpServletRequest request) {
        return ((OperatorBean) request.getSession(false).getAttribute(Attrs.LOGGED_OPERATOR_BEAN.val)).getOperator();
    }
}

