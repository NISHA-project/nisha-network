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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.nisha.commons.NishaDateTime;
import pl.nask.nisha.commons.network.NodeRingInfo;
import pl.nask.nisha.manager.model.domain.app.CouchDbConnector;
import pl.nask.nisha.manager.model.domain.network.NetworkRingInfo;
import pl.nask.nisha.manager.model.transfer.supportbeans.NetworkChangeType;

public class NetworkUpdater {

    public static final Logger LOG = LoggerFactory.getLogger(NetworkUpdater.class);

    public static NetworkRingInfo getNetworkRingInfoNewIfNull() {
        boolean createNetworkDocIfNeeded = false;
        NetworkRingInfo networkRingInfo = NetworkUpdater.getNetworkRingInfo(createNetworkDocIfNeeded);
        if(networkRingInfo == null) {
            networkRingInfo = new NetworkRingInfo();
        }
        return networkRingInfo;
    }

    public static NetworkRingInfo getNetworkRingInfo(boolean createIfNull) {
        if (createIfNull) {
            prepareNetworkRingInfoIfNeeded();
        }

        List<NetworkRingInfo> networkRingInfoList = CouchDbConnector.getCouchDbConnector().urisDbClient.view("nisha-node-uris/network_ring_info").includeDocs(true).query(NetworkRingInfo.class);
        if (networkRingInfoList.size() == 1) {
            return networkRingInfoList.get(0);
        } else if (networkRingInfoList.size() == 0) {
            return null;
        } else {
            String msg = "1 networkRingInfo was expected but found: " + networkRingInfoList.size();
            NetworkRingInfo result;
            try {
                result = mergeNetworkRingInfo(networkRingInfoList);
                LOG.debug("merge success");
            } catch (ParseException e) {
                throw new IllegalStateException(msg + " - merge network ring info documents problem");
            }

            return result;
        }
    }

    public static void prepareNetworkRingInfoIfNeeded() {
        boolean networkDocExists = checkIfNetworkRingInfoExists();
        if (!networkDocExists) {
            NetworkRingInfo networkRingInfo = new NetworkRingInfo();
            CouchDbConnector.getCouchDbConnector().urisDbClient.save(networkRingInfo);
            LOG.debug(" network ring info document created");
        }
    }

    public static boolean updateNetwork(NodeRingInfo nodeRingInfo, NetworkChangeType changeType) {
        String msg;
        try {

            boolean createNetworkDocIfNeeded = true;
            NetworkRingInfo networkRingInfo = getNetworkRingInfo(createNetworkDocIfNeeded);
            if (networkRingInfo == null) {
                msg = "no document of network ring info";
                throw new IllegalArgumentException(msg);
            }
            networkRingInfo = updateNetworkLastChange(networkRingInfo, changeType, nodeRingInfo.getNodeDomainNameFromRingInfo());


            if (changeType.equals(NetworkChangeType.NODE_ADD)) {
                List<String> names = networkRingInfo.getNetworkNodeNames();
                if (!names.contains(nodeRingInfo.getNodeDomainNameFromRingInfo())) {
                    names.add(nodeRingInfo.getNodeDomainNameFromRingInfo());
                }
            }
            CouchDbConnector.getCouchDbConnector().urisDbClient.update(networkRingInfo);
            return true;

        } catch (Exception e) {
            return false;
        }
    }


    private static NetworkRingInfo mergeNetworkRingInfo(List<NetworkRingInfo> networkList) throws ParseException {
        LOG.debug("merge start..");
        if (networkList.size() > 1) {

            NetworkRingInfo tmp;
            NetworkRingInfo result = networkList.get(0);
            DateFormat dateFormat = new SimpleDateFormat(NishaDateTime.dateTimePattern);
            for (int i = 1; i < networkList.size(); i++) {
                tmp = networkList.get(i);
                if (dateFormat.parse(tmp.getLastChangeDate()).after(dateFormat.parse(result.getLastChangeDate()))) {
                    result.setLastChangeDate(tmp.getLastChangeDate());
                    result.setLastChangeType(tmp.getLastChangeType());
                    result.setLastChangeNode(tmp.getLastChangeNode());

                    for (String nodeName : tmp.getNetworkNodeNames()) {
                        if (!result.getNetworkNodeNames().contains(nodeName)) {
                            result.getNetworkNodeNames().add(nodeName);
                        }
                    }
                }
                LOG.debug("removing {}", tmp.get_id());
                CouchDbConnector.getCouchDbConnector().urisDbClient.remove(tmp.get_id(), tmp.get_rev());
            }
            CouchDbConnector.getCouchDbConnector().urisDbClient.update(result);
            LOG.debug("network ring info merged - success");
            return result;
        }
        return null;
    }

    private static NetworkRingInfo updateNetworkLastChange(NetworkRingInfo networkRingInfo, NetworkChangeType changeType, String nodeDomainName) {
        if (networkRingInfo == null) {
            networkRingInfo = new NetworkRingInfo();
        }
        DateFormat dateFormat = new SimpleDateFormat(NishaDateTime.dateTimePattern);
        networkRingInfo.setLastChangeDate(dateFormat.format(new Date()));
        networkRingInfo.setLastChangeType(changeType);
        networkRingInfo.setLastChangeNode(nodeDomainName);
        return networkRingInfo;
    }

    public static boolean checkIfNetworkRingInfoExists() {
            List<NetworkRingInfo> networkRingInfoList = CouchDbConnector.getCouchDbConnector().urisDbClient.view("nisha-node-uris/network_ring_info").includeDocs(true).query(NetworkRingInfo.class);
            if (networkRingInfoList.size() == 1) {
                return true;
            } else if (networkRingInfoList.size() < 1) {
                return false;
            } else {
                String msg = "too many networkRingInfo docs found: " + networkRingInfoList.size();
                LOG.debug("{}", msg);
                NetworkRingInfo result;
                try {
                    result = mergeNetworkRingInfo(networkRingInfoList);
                } catch (ParseException e) {
                    throw new IllegalStateException("merge network ring info documents problem");
                }
                return result != null;
            }
        }
}

