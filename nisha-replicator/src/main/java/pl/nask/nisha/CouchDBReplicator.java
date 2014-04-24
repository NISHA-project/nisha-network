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
 * ****************************************************************************
 */
package pl.nask.nisha;

import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.*;

import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbException;
import org.lightcouch.CouchDbProperties;
import org.lightcouch.ReplicationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.nisha.commons.network.NodeRingInfo;
import pl.nask.nisha.proxy.SocketProxy;

public class CouchDBReplicator implements Replicator {

    private static final String[] dbsToReplicateToAny = {
        "nisha-nodes", "nisha-node-uris", "nisha-announcements", "nisha-message-broadcast"
    };
    private static final String[] dbsToReplicateToBasic = {
        "nisha-notifications"
    };
    private static final String[] dbsToReplicateToSuper = {
        "nisha-resources"
    };
    private static final int SOCKET_PROXY_PORT = 31337;
    private static final Logger logger = LoggerFactory.getLogger(CouchDBReplicator.class);
    private NodeReader nodeReader;
    private boolean thisNodeIsBasicNode = false;
    private boolean thisNodeIsSuperNode = false;
    private String thisNodeUri;
    private Random random = new Random();

    private long replicationIntervalInMills;
    private CouchDbClient nodesDbClient;
    private AlertManager alertGenerator;

    private static final String REPLICATION_SUCCESS = "replication success";
    private static final String REPLICATOR_IS_RUNNING = "replicator is running";



    public CouchDBReplicator(String thisNodeUri, String couchdbHost, int couchdbPort, String nishaUsername, String nishaPassword,
                             int socketTimeoutMilliseconds, long replicationIntervalInMills, AlertManager alertGenerator) {

        this.replicationIntervalInMills = replicationIntervalInMills;
        logger.info("Replication interval set to {}", this.replicationIntervalInMills);

        this.alertGenerator = alertGenerator;

        try {
            this.thisNodeUri = thisNodeUri;
            this.nodeReader = new NodeReader(couchdbHost, couchdbPort, nishaUsername, nishaPassword);

            CouchDbProperties couchDbProperties = new CouchDbProperties("nisha-nodes", false, "http", couchdbHost, couchdbPort, nishaUsername, nishaPassword);
            couchDbProperties.setSocketTimeout(socketTimeoutMilliseconds);
            logger.info("socket timeout set to: " + couchDbProperties.getSocketTimeout());
            this.nodesDbClient = new CouchDbClient(couchDbProperties);

            checkThisNodeRole();
        } catch (CouchDbException ex) {
            logger.error("couldn't initialize replicator");
            System.exit(-1);
        }

        SocketProxy socketProxy = new SocketProxy(SOCKET_PROXY_PORT);
        Thread t = new Thread(socketProxy);
        t.setDaemon(true);
        logger.info("starting proxy");
        t.start();
    }
    
    private void checkThisNodeRole() {
        List<NodeRingInfo> basicNodeUris = nodeReader.loadBasicNodeUris();
        List<NodeRingInfo> superNodeUris = nodeReader.loadSuperNodeUris();
        thisNodeIsBasicNode = containsThisNode(basicNodeUris);
        thisNodeIsSuperNode = containsThisNode(superNodeUris);
        logger.info("this node URI is {}", thisNodeUri);
        if (thisNodeIsBasicNode) {
            logger.info("this node is basic node");
        }
        if (thisNodeIsSuperNode) {
            logger.info("this node is super node");
        }
    }

    private boolean containsThisNode(List<NodeRingInfo> nodes) {
        return NodeReader.getNodeIndex(nodes, thisNodeUri) != -1;
    }

    private void processReplicationFailure(String target, String failureDescription) {
        alertGenerator.processReplicationFailure(nodesDbClient, target, thisNodeUri, failureDescription);
    }

    private void processReplicationSuccess(String target) {
        alertGenerator.processReplicationSuccess(target);
    }

    @Override
    public void runReplication() {
        while (true) {
            replicateInRing(dbsToReplicateToAny, nodeReader.loadAllNodeUris(), "ANY");
            if (thisNodeIsBasicNode) {
                replicateFromBasicToSuper();
                replicateInRing(dbsToReplicateToBasic, nodeReader.loadBasicNodeUris(), "BASIC");
            } else if (thisNodeIsSuperNode) {
                replicateInRing(dbsToReplicateToSuper, nodeReader.loadSuperNodeUris(), "SUPER");
            } else {
                logger.info("checking this node role...");
                checkThisNodeRole();
            }
            waitForNextReplication();
        }
    }

    private void replicateFromBasicToSuper() {
        List<NodeRingInfo> nodes = nodeReader.loadSuperNodeUris();
        logger.info("replication: BASIC to SUPER, {} nodes", nodes.size());
        if (nodes.isEmpty()) {
            logger.warn("list of super nodes is empty");
            return;
        }

        int size = nodes.size();
        int index = random.nextInt(size);
        selectTargetAndReplicateDbs(size, nodes, index, dbsToReplicateToSuper);
    }

    private void replicateInRing(String[] dbsToReplicate, List<NodeRingInfo> nodes, String ringName) {
        logger.info("replication: RING of {}, {} nodes", ringName, nodes.size());
        if (!containsThisNode(nodes)) {
            logger.error("this node is not on the list");
            return;
        }
        if (nodes.size() <= 1) {
            logger.warn("you are the only node on the list");
            return;
        }

        int size = nodes.size() - 1;
        int index = NodeReader.getNodeIndex(nodes, thisNodeUri) + 1;
        selectTargetAndReplicateDbs(size, nodes, index, dbsToReplicate);
    }

    private boolean selectTargetAndReplicateDbs(int size, List<NodeRingInfo> nodes, int index, String[] dbNames) {
        for (int i = 0; i < size; ++i) {
            NodeRingInfo targetRingInfo = nodes.get((index + i) % nodes.size());
            String target = targetRingInfo.getUri();
            String state = targetRingInfo.getState();

            if (!state.equals("ACTIVE")) {
                logger.warn("skipping node {} - state = {}", target, state);
                continue;
            }

            String replicationResult = replicateDbs(target, dbNames);
            if (replicationResult.equals(REPLICATION_SUCCESS)) {
                processReplicationSuccess(target);
                return true;
            } else {
                processReplicationFailure(target, replicationResult);
                logger.warn("switching target");
            }
        }
        logger.warn("couldn't replicate to any target");
        return false;
    }



    private String replicateDbs(String target, String[] dbNames) {
        logger.info("replicating from {} to {}", thisNodeUri, target);
        try {
            for (String dbName : dbNames) {
                String replicatorRunningResult = checkIsSocketProxyAvailable(target);
                if (!replicatorRunningResult.equals(REPLICATOR_IS_RUNNING)) {
                    return replicatorRunningResult;
                }
                ReplicationResult result = nodesDbClient.replication().source(dbName).target(target + "/" + dbName).createTarget(true).trigger();
                if (!result.isOk()) {
                    // this is never reached, exception goes first
                    String msg = "replication failed for session " + result.getSessionId();
                    logger.warn(msg);
                    return msg;
                }
                logger.info("replication of {} succeded", dbName);
            }
        } catch (CouchDbException e) {
            return "replication failed - couchDb is not responding - couchDb or nginx proxy is not working";
        }
        logger.info("replication of all DBs succeeded");
        return REPLICATION_SUCCESS;
    }

    private String checkIsSocketProxyAvailable(String target) {
        //check if replicator has been started
        String result = REPLICATOR_IS_RUNNING;
        try {
            URI uri = new URI(target);
            Socket socket = new Socket(uri.getHost(), SOCKET_PROXY_PORT);
            boolean connectionOk = socket.isConnected();
            socket.close();
            if (!connectionOk) {
                result = "replication failed - could not connect to SocketProxy";
                logger.warn(result);
                return result;
            }
        } catch (URISyntaxException ex) {
            result = "replication failed - uri syntax invalid - " + ex.getMessage();
            logger.warn(result);
            return result;
        } catch (UnknownHostException ex) {
            result = "replication failed - unknown host - " + ex.getMessage();
            logger.warn(result);
            return result;
        } catch (IOException ex) {
            result = "replication failed - " + ex.getMessage() +" - remote replicator is not listening";
            logger.warn(result);
            return result;
        }
        return result;
    }

    private void waitForNextReplication() {
        try {
            Thread.sleep(replicationIntervalInMills);
        } catch (InterruptedException e) {
            logger.warn("sleep interrupted");
        }
    }
}
