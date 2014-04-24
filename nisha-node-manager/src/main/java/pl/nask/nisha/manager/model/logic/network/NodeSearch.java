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
package pl.nask.nisha.manager.model.logic.network;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.nisha.commons.network.NodeRingInfo;
import pl.nask.nisha.commons.network.NodeRole;
import pl.nask.nisha.commons.network.NodeState;
import pl.nask.nisha.manager.model.domain.app.CouchDbConnector;
import pl.nask.nisha.manager.model.logic.alerts.AlertSearch;
import pl.nask.nisha.manager.model.logic.local.LocalConfigUpdater;
import pl.nask.nisha.manager.model.transfer.servletsupport.AttrParamValues;
import pl.nask.nisha.manager.model.transfer.supportbeans.NodeRingInfoRich;
import pl.nask.nisha.manager.model.transfer.supportbeans.NodeRingInfoRichCollection;

public class NodeSearch {

    private static final Logger LOG = LoggerFactory.getLogger(NodeSearch.class);
    public static final String RING_INFO_NOT_FOUND_MESSAGE = "nodeRingInfo  was expected but found: ";

    public static String resolveSearchMode(String querySubmitName) {
        String searchMode;
        if (querySubmitName != null) {
            searchMode = AttrParamValues.SEARCH_MODE_NAME.val;
        } else {
            throw new IllegalStateException(AttrParamValues.UNKNOWN_ACTION.val);
        }
        return searchMode;
    }


    public static NodeRingInfoRichCollection findNodeRingInfosByQuery(String query, String searchMode, Set<String> acceptedStates) {
        NodeRingInfoRichCollection result = new NodeRingInfoRichCollection();

        String thisNodeRole = getThisNodeRole();
        List<NodeRingInfo> nodeRingInfoList = CouchDbConnector.getCouchDbConnector().urisDbClient.view(("nisha-node-uris/by_type_uris")).includeDocs(true).query(NodeRingInfo.class);
        String msg, nodeDomainNameFromRing;

        List<NodeRingInfoRich> richNodeRingInfoList = enrichNodeRingInfos(nodeRingInfoList);

        if (query == null || query.trim().isEmpty() || query.equals("*")) {
            result.getNodeRingInfoRichList().addAll(richNodeRingInfoList);
        } else {
            LOG.debug("query: \"{}\"", query);
            if (searchMode.equals(AttrParamValues.SEARCH_MODE_NAME.val)) {
                for (NodeRingInfoRich n : richNodeRingInfoList) {
                    nodeDomainNameFromRing = n.getNodeRingInfo().getNodeDomainNameFromRingInfo();
                    if (nodeDomainNameFromRing != null && nodeDomainNameFromRing.contains(query)) {
                        if (!nodeDomainNameFromRing.equalsIgnoreCase("null")) {
                            result.getNodeRingInfoRichList().add(n);
                        }

                    }
                }
            } else {
                msg = "search mode unknown";
                throw new IllegalArgumentException(msg);
            }
        }

        NodeRingInfoRichCollection resultsFilteredBySearchRole = new NodeRingInfoRichCollection();
        if (thisNodeRole.equals(NodeRole.SUPERNODE.name())) {
            //SUPERNODE - display results with any state
            resultsFilteredBySearchRole.getNodeRingInfoRichList().addAll(result.getNodeRingInfoRichList());
        } else if (thisNodeRole.equals(NodeRole.BASICNODE.name())) {
            //BASICNODE - display only active results
            for (NodeRingInfoRich tmp : result.getNodeRingInfoRichList()) {
                if (tmp.getNodeRingInfo().getState().equals(NodeState.ACTIVE.name()) ||
                    tmp.getNodeRingInfo().getState().equals(NodeState.INACTIVE.name()) ) {
                    resultsFilteredBySearchRole.getNodeRingInfoRichList().add(tmp);
                }
            }
        } else {
            msg = "unexpected this node role: " + thisNodeRole;
            throw new IllegalArgumentException(msg);
        }

        return applyStateFilters(resultsFilteredBySearchRole, acceptedStates);
    }


    private static List<NodeRingInfoRich> enrichNodeRingInfos(List<NodeRingInfo> nodeRingInfoList) {
        NodeRingInfoRich richTmp;
        List<NodeRingInfoRich> result = new ArrayList<NodeRingInfoRich>();
        String alertsInfo = "";
        List<String> nodesWithAlerts = AlertSearch.getNodeWithAlertsNames(AlertSearch.findAllAlerts());
        LOG.debug("nodes with alerts: {}", nodesWithAlerts);
        for(NodeRingInfo nodeRingInfo : nodeRingInfoList) {
            if(nodesWithAlerts.contains(nodeRingInfo.getNodeDomainNameFromRingInfo())) {
                alertsInfo = AttrParamValues.ALERTS.val;
            }
            richTmp = new NodeRingInfoRich(nodeRingInfo, alertsInfo);
            result.add(richTmp);
            alertsInfo = "";
            LOG.debug("enriched node: {}", richTmp);
        }
        return result;
    }

    public static NodeRingInfo findNodeRingInfoByDocId(String docid) {
        return CouchDbConnector.getCouchDbConnector().urisDbClient.find(NodeRingInfo.class, docid);
    }

    public static NodeRingInfo findNodeRingInfoByNodeDomainName(String domainName) {
        List<NodeRingInfo> results = CouchDbConnector.getCouchDbConnector().urisDbClient.view("nisha-node-uris/all_nodes").key(domainName).includeDocs(true).query(NodeRingInfo.class);
        if (results.size() == 1) {
            NodeRingInfo result = results.get(0);
            LOG.debug("{}", "node ring info found: " + result);
            return result;
        } else {
            String msg = domainName + " - 1 "+ RING_INFO_NOT_FOUND_MESSAGE + results.size();            //message used later to recognize exception
            throw new IllegalStateException(msg);
        }
    }

    private static NodeRingInfoRichCollection applyStateFilters (NodeRingInfoRichCollection collectionToFilter,Set<String> acceptedStates) {

        if (acceptedStates.isEmpty()) {
            return collectionToFilter;
        }

        NodeRingInfoRichCollection filteredCollection = new NodeRingInfoRichCollection();
        for (NodeRingInfoRich infoRich : collectionToFilter.getNodeRingInfoRichList()) {
            if (acceptedStates.contains(infoRich.getNodeRingInfo().getState())) {
                filteredCollection.getNodeRingInfoRichList().add(infoRich);
                LOG.debug("rich node: {}", infoRich);
            }
        }
        return filteredCollection;
    }

    public static String getThisNodeRole () {
        String thisNodeName = LocalConfigUpdater.getThisNodeDomainNameFromConfig();
        LOG.debug("checking role for: " + thisNodeName);
        NodeRingInfo ringInfo = findNodeRingInfoByNodeDomainName(thisNodeName);
        LOG.debug("checking role: " + ringInfo.toString());
        String role = ringInfo.getRole();
        LOG.debug("checking role result: " + role);
        return role;
    }

    public static String getOtherNodeRole (String nodeDomainName) {
        return findNodeRingInfoByNodeDomainName(nodeDomainName).getRole();
    }

}

