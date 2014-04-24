/**
 * *****************************************************************************
 * Copyright (c) 2012 Research and Academic Computer Network. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the GNU Public License v2.0 which accompanies this
 * distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * With financial support from the Prevention, Preparedness and Consequence
 * Management of Terrorism and other Security Related Risks Programme European
 * Commission - Directorate-General Home Affairs
 *
 * Contributors: Research and Academic Computer Network
 *****************************************************************************
 */
package pl.nask.nisha.manager.model.logic.network;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;

import org.lightcouch.CouchDbClient;
import org.lightcouch.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.nisha.commons.network.NodeRingInfo;
import pl.nask.nisha.manager.model.domain.app.CouchDbConnector;
import pl.nask.nisha.manager.model.domain.local.NodeConfiguration;
import pl.nask.nisha.manager.model.domain.network.NetworkRingInfo;
import pl.nask.nisha.manager.model.logic.local.LocalConfigUpdater;
import pl.nask.nisha.manager.model.transfer.servletsupport.AttrParamValues;
import pl.nask.nisha.manager.model.transfer.supportbeans.NetworkChangeType;

public class NodeUpdater {

    public static final Logger LOG = LoggerFactory.getLogger(NodeUpdater.class);
    public static final boolean DO_VALIDATION = true;
    public static final int STATE_REASON_MIN_LENGTH = 10;

    public static String addNodeToDatabase(NodeRingInfo nodeRingInfo) throws IllegalArgumentException, IOException, ServletException {
        String msg = "";
        if (!checkUriUnique(nodeRingInfo.getNodeDomainNameFromRingInfo())) {
            msg = nodeRingInfo.getNodeDomainNameFromRingInfo() + " node already in network (node's uri is not unique) - add failure";
            throw new IllegalArgumentException(msg);
        }

        boolean createNetworkDocIfNeeded = false;
        NetworkRingInfo prevNetwork = NetworkUpdater.getNetworkRingInfo(createNetworkDocIfNeeded);
        Response resp = null;
        try {
            resp = CouchDbConnector.getCouchDbConnector().urisDbClient.save(nodeRingInfo);
            boolean successNetworkUpdate = NetworkUpdater.updateNetwork(nodeRingInfo, NetworkChangeType.NODE_ADD);
            if (!successNetworkUpdate) {
                throw new IllegalStateException("networkRingInfo update problem");
            }
        } catch (Exception e) {
            rollbackAddNode(prevNetwork, resp);
        }
        return msg;
    }

    private static boolean checkUriUnique(String uriToCheck) {
        String msg;
        List<NodeRingInfo> nodeRingInfosList = CouchDbConnector.getCouchDbConnector().urisDbClient.view(("nisha-node-uris/by_type_uris")).includeDocs(true).query(NodeRingInfo.class);

        if (nodeRingInfosList == null) {
            msg = "result list loaded from db is null";
            throw new IllegalArgumentException(msg);
        } else if (nodeRingInfosList.size() < 1) {
            LOG.debug("no nodes in network");
        }

        for (NodeRingInfo n : nodeRingInfosList) {
            if (n.getNodeDomainNameFromRingInfo() == null || n.getNodeDomainNameFromRingInfo().trim().isEmpty()) {
                msg = "node uri is null or empty - node data in base incomplete";
                throw new IllegalArgumentException(msg);
            }

            if (n.getNodeDomainNameFromRingInfo().equals(uriToCheck)) {
                msg = "node's uri is not unique";
                LOG.debug("{} - failure", msg);
                return false;
            }
        }
        LOG.debug("node's uri is unique");
        return true;
    }

    private static void rollbackAddNode(NetworkRingInfo prevNetwork, Response addResponse) {
        String msg;
        try {
            if (addResponse != null && prevNetwork != null) {
                CouchDbConnector.getCouchDbConnector().urisDbClient.update(prevNetwork);
                CouchDbConnector.getCouchDbConnector().urisDbClient.remove(addResponse.getId(), addResponse.getRev());
            }
            msg = "add node rollback success";
            LOG.debug("{}", msg);
        } catch (Exception e) {
            msg = "add node rollback problem";
            LOG.warn("{}", msg);
            throw new IllegalStateException(msg);
        }
    }

    public static String doNodeUpdate(NodeRingInfo nodeRingInfoToUpdate, String prevStateReason) {
        String validateMsg = validateNode(nodeRingInfoToUpdate, prevStateReason);
        if (!validateMsg.equals(AttrParamValues.OK.val)) {
            throw new IllegalArgumentException(validateMsg);
        }
        LOG.debug("{} : {} validated - success",nodeRingInfoToUpdate.getRole(),nodeRingInfoToUpdate.getNodeDomainNameFromRingInfo());
        CouchDbConnector.getCouchDbConnector().urisDbClient.update(nodeRingInfoToUpdate);

        String resultMsg = "node updated - success";
        LOG.info("{}", nodeRingInfoToUpdate.toString());
        LOG.info("{}", resultMsg);
        return resultMsg;
    }

    public static String validateNode(NodeRingInfo nodeToValidate, String prevStateReason) {

        if (!DO_VALIDATION) {
            return AttrParamValues.OK.val;
        }

        String msg;
        try {
            String stateReason = nodeToValidate.getStateReason();
            if (nodeToValidate.getStateReason().equals(prevStateReason)) {
                throw new IllegalArgumentException("new state requires new state reason");
            }

            if (stateReason.length() < STATE_REASON_MIN_LENGTH) {
                msg = "state resson is too short - length: " + stateReason.length();
                throw new IllegalArgumentException(msg);
            } else if (stateReason.equals(AttrParamValues.STATE_REASON_DEFAULT.val)) {
                msg = "no state reason - failure";
                throw new IllegalArgumentException(msg);
            }
        }
        catch (Exception e) {
            msg = "state reason is required and must have at least 10 characters - " + e.getMessage();
            LOG.warn(msg);
            return msg;
        }


        try {
            CouchDbClient nodeToValidateLocalClient = CouchDbConnector.getCouchDbConnector().getCouchDbClientFromNodeDomainName(nodeToValidate.getNodeDomainNameFromRingInfo(),
                    nodeToValidate.getPortNumberFromRingInfo(), CouchDbConnector.DB_NAME_LOCAL);

            if (nodeToValidateLocalClient == null) {
                msg = "cannot connect to couchDb on node: " + nodeToValidate.getNodeDomainNameFromRingInfo();
                LOG.debug("{}", msg);
                return msg;
            }
            LOG.debug("couch connection ok");

            NodeConfiguration nodeConfig;
            try{
                nodeConfig = LocalConfigUpdater.getAnyNodeConfiguration(nodeToValidateLocalClient);
            } catch (Exception e) {
                throw new IllegalStateException(nodeToValidate.getNodeDomainNameFromRingInfo() + " - node should be configured first - failure");
            }

            if (!(nodeConfig.getNodeDomainNameFromConfig() == null || nodeConfig.getNodeDomainNameFromConfig().trim().isEmpty())) {
                if (!(nodeConfig.getNodeDomainNameFromConfig().equals(nodeToValidate.getNodeDomainNameFromRingInfo())
                        && nodeConfig.getPortNumberFromConfig().equals(nodeToValidate.getPortNumberFromRingInfo()))) {
                    msg = "domain names or port numbers of node being added and uri in it's configuration are not the same";
                    LOG.warn("{}", msg);
                    LOG.debug("config: {} {}",nodeConfig.getNodeDomainNameFromConfig(), nodeConfig.getPortNumberFromConfig());
                    LOG.debug("config: {} {}",nodeToValidate.getNodeDomainNameFromRingInfo(), nodeToValidate.getPortNumberFromRingInfo());
                    return msg;
                }
            }

            String otherNodeRole;
            try {
                otherNodeRole = NodeSearch.getOtherNodeRole(nodeToValidate.getNodeDomainNameFromRingInfo());
            } catch (Exception e) {
                otherNodeRole = null;
            }

            if(otherNodeRole != null && !otherNodeRole.equals(nodeToValidate.getRole())) {
                msg = "node " + nodeToValidate.getNodeDomainNameFromRingInfo() + " already added to network with role " + otherNodeRole;
                throw new IllegalArgumentException(msg);
            }
            return AttrParamValues.OK.val;
        } catch (Exception e) {
            msg = e.getMessage();
            return msg;
        }
    }

    public static void updateNodeNamerPort(String prevNodeDomainName, String newNodeDomainName, String newPort) {
        try{
            NodeRingInfo ringInfo = NodeSearch.findNodeRingInfoByNodeDomainName(prevNodeDomainName);
            ringInfo.setNodeDomainNameFromRingInfo(newNodeDomainName);
            ringInfo.setPortNumberFromRingInfo(newPort);
            CouchDbConnector.getCouchDbConnector().urisDbClient.update(ringInfo);
            LOG.info("node ring info updated");
        } catch (IllegalStateException e) {
            if (e.getMessage().contains(NodeSearch.RING_INFO_NOT_FOUND_MESSAGE +"0")) {
                LOG.info("no node ring info to update - node is not in the network ring");
            } else {
                throw e;
            }
        }

    }
}

