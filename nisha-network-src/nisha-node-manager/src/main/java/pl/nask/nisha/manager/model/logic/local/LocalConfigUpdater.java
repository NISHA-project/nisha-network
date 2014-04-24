package pl.nask.nisha.manager.model.logic.local;

import java.util.List;

import org.lightcouch.CouchDbClient;
import org.lightcouch.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.nisha.commons.node.Operator;
import pl.nask.nisha.manager.model.domain.app.CouchDbConnector;
import pl.nask.nisha.manager.model.domain.local.NodeConfiguration;
import pl.nask.nisha.manager.model.domain.network.NodeInfo;
import pl.nask.nisha.manager.model.domain.network.OperatorContact;
import pl.nask.nisha.manager.model.logic.network.NodeDisplay;
import pl.nask.nisha.manager.model.logic.network.NodeUpdater;
import pl.nask.nisha.manager.model.logic.network.OperatorContactDisplay;

public class LocalConfigUpdater {

    public static final Logger LOG = LoggerFactory.getLogger(LocalConfigUpdater.class);
    public static final String CONFIG_SAVE_SUCCESS_MESSAGE = "configuration saved - success";

    public static NodeConfiguration getAnyNodeConfiguration(CouchDbClient client) {
        List<NodeConfiguration> configurationList = client.view(("nisha-local/by_type_config")).includeDocs(true).query(NodeConfiguration.class);
        String msg;
        if (configurationList.size() == 1) {
            return configurationList.get(0);
        } else if (configurationList.size() == 0) {
            msg = client.getDBUri() + " - cannot find node configuration";
            throw new IllegalStateException(msg);
        } else {
            msg = "too many node configurations found in db: " + configurationList.size();
            throw new IllegalStateException(msg);
        }
    }

    public static NodeConfiguration getNodeConfiguration() {
        return getAnyNodeConfiguration(CouchDbConnector.getCouchDbConnector().localDbClient);
    }


    public static String getThisNodeDomainNameFromConfig() {
        return getNodeConfiguration().getNodeDomainNameFromConfig();
    }

    public static String getThisNodePortFromConfig() {
        return getNodeConfiguration().getPortNumberFromConfig();
    }

    public static void propagateChangesToOperatorsContactsAndRing(String prevNodeDomainName, String prevPort,
                                                                  String newNodeDomainName, String newPort) {

        if ( !prevNodeDomainName.equals(newNodeDomainName)) {
            updateOperatorsAndContacts(newNodeDomainName);
        }
        if ( !prevNodeDomainName.equals(newNodeDomainName) || !prevPort.equals(newPort)) {
            NodeUpdater.updateNodeNamerPort(prevNodeDomainName, newNodeDomainName, newPort);
        }
    }

    private static void updateOperatorsAndContacts(String nodeDomainName) {
        LocalOperatorUpdater.updateOperatorsContextNodeName(nodeDomainName);
        OperatorContactDisplay.updateOperatorContactsContextNodeName(nodeDomainName);
    }

    public static String getThisNodeNameAndPort() {
        return getThisNodeDomainNameFromConfig() + ":" + getThisNodePortFromConfig();
    }

    public static String getThisNodeUri() {
        return CouchDbConnector.couchdbProtocol +"://" + getThisNodeNameAndPort();
    }

    public static boolean checkOperatorBlocked(String idToCheck, NodeConfiguration config) {
        String msg;
        if (config.getOperatorIdListBlocked().contains(idToCheck)) {
            msg = "operator is blocked";
            LOG.info("{}", msg);
            return true;
        } else if (config.getOperatorIdListPermitted().contains(idToCheck)) {
            msg = idToCheck + " - operator is not blocked";
            LOG.info("{}", msg);
            return false;
        } else {
            msg = idToCheck + " - operator unknown";
            LOG.info("{}", msg);
            throw new IllegalArgumentException(msg);
        }
    }

    public static String doFirstConfigSave(NodeConfiguration config, Operator operator) {
        String msg;
        Response respConfig = null;
        Response respNodeInfo = null;
        Response respOperator = null;
        Response respContact = null;

        try {
            NodeInfo nodeInfo = new NodeInfo(config);
            OperatorContact opContact = new OperatorContact(operator);

            respConfig = CouchDbConnector.getCouchDbConnector().localDbClient.save(config);
            respNodeInfo = CouchDbConnector.getCouchDbConnector().nodesDbClient.save(nodeInfo);
            respOperator = CouchDbConnector.getCouchDbConnector().localDbClient.save(operator);
            respContact = CouchDbConnector.getCouchDbConnector().nodesDbClient.save(opContact);
            LOG.info("{}", config.toString());
            msg = CONFIG_SAVE_SUCCESS_MESSAGE;
            LOG.info("{}", msg);
        } catch (Exception e) {
            msg = "first config save problem: " + e.getMessage();
            LOG.warn("{}", msg);
            revertConfig(respConfig, respNodeInfo, respOperator, respContact, null, null);
        }
        return msg;
    }

    public static String doConfigUpdate(NodeConfiguration prevConfig, NodeConfiguration configWithChanges, Operator operator) {
        String msg;
        boolean updateWithNewOperator = (operator != null);
        if (prevConfig.equals(configWithChanges) && !updateWithNewOperator) {
            msg = "no changes in configuration - no need to update";
            LOG.info("{}", msg);
            throw new IllegalStateException("");
        }

        if (updateWithNewOperator && operator.getBlocked()) {
            msg = "new operator cannot be blocked";
            throw new IllegalStateException(msg);
        }

        NodeInfo prevNodeInfo = NodeDisplay.getNodeInfoByNodeName(prevConfig.getNodeDomainNameFromConfig());
        NodeInfo nodeInfo = NodeDisplay.getNodeInfoByNodeName(prevConfig.getNodeDomainNameFromConfig());
        if (nodeInfo == null) {
            msg = "nisha-nodes and nisha-local inconsistent - " + configWithChanges.getNodeDomainNameFromConfig();
            throw new IllegalStateException(msg);
        }

        if (updateWithNewOperator) {
            String operatorId = operator.getOperatorId();
            boolean idTaken = LocalOperatorUpdater.getAllOperatorIds(configWithChanges).contains(operatorId);
            if (idTaken) {
                msg = operatorId + " - id for new operator already taken - failure";
                return msg;
            } else {
                LOG.debug("{} - operatorId unique", operatorId);
                configWithChanges.getOperatorIdListPermitted().add(operatorId);
            }
        }

        nodeInfo = NodeInfo.loadConfigProps(nodeInfo.get_id(), nodeInfo.get_rev(), configWithChanges);

        Response respConfig = null;
        Response respNodeInfo = null;
        Response respOperator = null;
        Response respContact = null;

        try {

            respConfig = CouchDbConnector.getCouchDbConnector().localDbClient.update(configWithChanges);
            respNodeInfo = CouchDbConnector.getCouchDbConnector().nodesDbClient.update(nodeInfo);
            if (updateWithNewOperator) {
                OperatorContact opContact = new OperatorContact(operator);
                respOperator = CouchDbConnector.getCouchDbConnector().localDbClient.save(operator);
                respContact = CouchDbConnector.getCouchDbConnector().nodesDbClient.save(opContact);
            }

            NodeConfiguration currentConfig = LocalConfigUpdater.getNodeConfiguration();
            LOG.info("{}", currentConfig.toString());
            msg = CONFIG_SAVE_SUCCESS_MESSAGE;
            LOG.info("{}", msg);
        }
        catch (Exception e) {
            LocalConfigUpdater.revertConfig(respConfig, respNodeInfo, respOperator, respContact, prevConfig, prevNodeInfo);
            msg = e.getMessage() + " - changes reverted";
            LOG.info("{}", msg);
        }
        return msg;
    }

    public static void revertConfig(Response respConfig, Response respNodeInfo, Response respOperator, Response respContact,
                                    NodeConfiguration previousConfig, NodeInfo previousNodeInfo) {
        LOG.debug("revert configuration start...");
        if (respConfig != null) {
            if (previousConfig == null) {
                CouchDbConnector.getCouchDbConnector().localDbClient.remove(respConfig.getId(), respConfig.getRev());
            } else {
                NodeConfiguration previous = CouchDbConnector.getCouchDbConnector().localDbClient.find(NodeConfiguration.class, previousConfig.get_id(), previousConfig.get_rev());
                previous.set_rev(respConfig.getRev());
                CouchDbConnector.getCouchDbConnector().localDbClient.update(previous);
            }
            LOG.debug("configuration reverted");
        }

        if (respNodeInfo != null) {
            if (previousNodeInfo == null) {
                CouchDbConnector.getCouchDbConnector().nodesDbClient.remove(respNodeInfo.getId(), respNodeInfo.getRev());
            } else {
                NodeInfo previous = CouchDbConnector.getCouchDbConnector().nodesDbClient.find(NodeInfo.class, previousNodeInfo.get_id(), previousNodeInfo.get_rev());
                previous.set_rev(respNodeInfo.getRev());
                CouchDbConnector.getCouchDbConnector().nodesDbClient.update(previous);
            }

            LOG.debug("nodeInfo reverted");
        }

        if (respOperator != null) {
            CouchDbConnector.getCouchDbConnector().localDbClient.remove(respOperator.getId(), respOperator.getRev());
            LOG.debug("local operator removed");
        }

        if (respContact != null) {
            CouchDbConnector.getCouchDbConnector().nodesDbClient.remove(respContact.getId(), respContact.getRev());
            LOG.debug(" operator contact removed");
        }
        LOG.info("revert configuration - success");
    }
}

