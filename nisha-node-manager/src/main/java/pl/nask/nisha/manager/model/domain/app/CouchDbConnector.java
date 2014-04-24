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
package pl.nask.nisha.manager.model.domain.app;

import java.net.InetAddress;

import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;
import org.lightcouch.DesignDocument;
import org.lightcouch.NoDocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.nisha.commons.config.ConfigPropertyName;
import pl.nask.nisha.commons.config.NodeManagerFileConfig;
import pl.nask.nisha.commons.network.NodeRole;
import pl.nask.nisha.manager.model.logic.app.SupernodeIndicator;

public class CouchDbConnector {

    public static final Logger LOG = LoggerFactory.getLogger(CouchDbConnector.class);

    private static CouchDbConnector COUCHDB_CONNECTOR;

    public final CouchDbClient announcementDbClient;
    public final CouchDbClient localDbClient;
    public final CouchDbClient messageInboxDbClient;
    public final CouchDbClient messageOutboxDbClient;
    public final CouchDbClient messageBroadcastDbClient;
    public final CouchDbClient nodesDbClient;
    public final CouchDbClient notificationsDbClient;
    public final CouchDbClient urisDbClient;
    public final CouchDbClient resourcesDbClient;

    public static final String DB_NAME_ANNOUNCEMENTS = "nisha-announcements";
    public static final String DB_NAME_LOCAL = "nisha-local";
    public static final String DB_NAME_MESSAGE_BROADCAST = "nisha-message-broadcast";
    public static final String DB_NAME_MESSAGE_INBOX = "nisha-message-inbox";
    public static final String DB_NAME_MESSAGE_OUTBOX = "nisha-message-outbox";
    public static final String DB_NAME_NODES = "nisha-nodes";
    public static final String DB_NAME_NOTIFICATIONS = "nisha-notifications";
    public static final String DB_NAME_RESOURCES = "nisha-resources";
    public static final String DB_NAME_URIS = "nisha-node-uris";

    public static final String VIEW_MESSAGE_BY_DATE_ALL = "/message_id_by_date";
    public static final String VIEW_MESSAGE_BY_DATE_NOT_ARCHIVED = "/message_id_by_date_not_archived";
    public static final String VIEW_RESOURCE_BY_ID = "/articles_by_id";
    public static final String VIEW_RESOURCE_BY_TITLE = "/articles_by_title";
    public static final String VIEW_ALERTS_ALL = "/by_type_alerts";
    public static final String VIEW_ALERTS_ALL_NOT_CLOSED = "/by_type_alerts_not_closed";
    public static final String VIEW_ALERTS_AFF = "/alerts_for_aff_name";
    public static final String VIEW_ALERTS_AFF_NOT_CLOSED = "/alerts_for_aff_not_closed";
    public static final String VIEW_ALERTS_DET = "/alerts_for_det_name";
    public static final String VIEW_ALERTS_DET_NOT_CLOSED  = "/alerts_for_det_not_closed";


    public static final String couchdbProtocol = "http";

    private CouchDbConnector() {
        boolean isAdmin = false;
        announcementDbClient = createCouchDbClient(isAdmin, DB_NAME_ANNOUNCEMENTS);
        localDbClient = createCouchDbClient(isAdmin, DB_NAME_LOCAL);
        nodesDbClient = createCouchDbClient(isAdmin, DB_NAME_NODES);
        notificationsDbClient = createCouchDbClient(isAdmin, DB_NAME_NOTIFICATIONS);
        urisDbClient = createCouchDbClient(isAdmin, DB_NAME_URIS);
        resourcesDbClient = createCouchDbClient(isAdmin, DB_NAME_RESOURCES);
        messageBroadcastDbClient = createCouchDbClient(isAdmin, DB_NAME_MESSAGE_BROADCAST);
        messageInboxDbClient = createCouchDbClient(isAdmin, DB_NAME_MESSAGE_INBOX);
        messageOutboxDbClient = createCouchDbClient(isAdmin, DB_NAME_MESSAGE_OUTBOX);

    }

    public static CouchDbConnector getCouchDbConnector() {
        if(COUCHDB_CONNECTOR == null){
            COUCHDB_CONNECTOR = new CouchDbConnector();
        }
        return COUCHDB_CONNECTOR;
    }

    public CouchDbClient createCouchDbClient(boolean isAdmin, String databaseName) {
        boolean loadPropsFromFile = false;
        NodeManagerFileConfig nodeManagerFileConfig = NodeManagerFileConfig.getNodeManagerFileConfig(loadPropsFromFile);
        String host = nodeManagerFileConfig.getPropertyValue(ConfigPropertyName.COUCHDB_HOST);
        int port = Integer.parseInt(nodeManagerFileConfig.getPropertyValue(ConfigPropertyName.COUCHDB_PORT));
        String username, password;
        boolean createIfNotExist = isAdmin;
        if (isAdmin) {
            username = nodeManagerFileConfig.getPropertyValue(ConfigPropertyName.COUCHDB_ADMIN_USER);
            password = nodeManagerFileConfig.getPropertyValue(ConfigPropertyName.COUCHDB_ADMIN_PASSWORD);
        } else {
            username = nodeManagerFileConfig.getPropertyValue(ConfigPropertyName.COUCHDB_NISHA_USER);
            password = nodeManagerFileConfig.getPropertyValue(ConfigPropertyName.COUCHDB_NISHA_PASSWORD);
        }

        CouchDbProperties couchDbProperties = new CouchDbProperties(databaseName, createIfNotExist, couchdbProtocol,
                                                                    host, port, username, password);
        int responseTimeoutMillis;
        String timeoutPropertyValue = nodeManagerFileConfig.getPropertyValue(ConfigPropertyName.COUCHDB_HTTP_SOCKET_TIMEOUT, "0");
        try {
            responseTimeoutMillis = Integer.parseInt(timeoutPropertyValue);
        }
        catch (NumberFormatException ex) {
            throw new IllegalArgumentException(timeoutPropertyValue + " is not a valid number - cannot load couchdb http socket timeout");
        }
        couchDbProperties.setSocketTimeout(responseTimeoutMillis);

        LOG.info("(for " + databaseName + " admin? " + isAdmin + ") " + getCouchDbPropertiesToString(couchDbProperties));
        return new CouchDbClient(couchDbProperties);
    }

    public CouchDbClient getCouchDbClientFromNodeDomainName(String nodeDomainName, String port, String dbName) {

        try {
            InetAddress address = InetAddress.getByName(nodeDomainName);
            String host = address.getHostName();
            int portNum = Integer.parseInt(port);

            boolean loadPropsFromFile = false;
            NodeManagerFileConfig fileConfig = NodeManagerFileConfig.getNodeManagerFileConfig(loadPropsFromFile);
            String user = fileConfig.getPropertyValue(ConfigPropertyName.COUCHDB_NISHA_USER);
            String password = fileConfig.getPropertyValue(ConfigPropertyName.COUCHDB_NISHA_PASSWORD);
            LOG.info("connecting to couchDb database: " + dbName + " on host " + host + " port: " + port + " as user " + user);
            CouchDbClient remoteCouchDbClient = new CouchDbClient(dbName, false, "http", host, portNum, user, password);
            LOG.info("connection success: " + remoteCouchDbClient.getDBUri().toString());
            return remoteCouchDbClient;
        }
        catch (Exception e) {
            LOG.warn("{}", e.getMessage());
            return null;
        }
    }

    public static String prepareTargetDbFullUri(String userPass, String nodeNamePort, String databaseName) {
        return couchdbProtocol +"://" + userPass +"@" + nodeNamePort + "/" + databaseName;
    }

    public static CouchDbClient resolveResourceCouchDbClient(boolean searchGlobal, String thisNodeRole) {
        CouchDbClient dbClient;
        if (thisNodeRole.equals(NodeRole.UNDEFINED.name())) {
            throw new IllegalStateException("node role is " + thisNodeRole + " - improper role to call resource actions");
        }

        if (searchGlobal && thisNodeRole.equals(NodeRole.BASICNODE.name())) {
            LOG.debug("GLOBAL resources from {}", thisNodeRole);
            dbClient = SupernodeIndicator.getSupernodeDbClient();
        } else {
            LOG.debug("LOCAL resources of {}", thisNodeRole);
            dbClient = CouchDbConnector.getCouchDbConnector().resourcesDbClient;
        }
        return dbClient;
    }

//-----------design doc synchronization (mvn clean resolves old empty views problem)-----------//
public static void synchronizeDeskDesignDocs() {

        loadDesignDoc(CouchDbConnector.DB_NAME_ANNOUNCEMENTS);
        loadDesignDoc(CouchDbConnector.DB_NAME_LOCAL);
        loadDesignDoc(CouchDbConnector.DB_NAME_MESSAGE_BROADCAST);
        loadDesignDoc(CouchDbConnector.DB_NAME_MESSAGE_INBOX);
        loadDesignDoc(CouchDbConnector.DB_NAME_MESSAGE_OUTBOX);
        loadDesignDoc(CouchDbConnector.DB_NAME_NODES);
        loadDesignDoc(CouchDbConnector.DB_NAME_NOTIFICATIONS);
        loadDesignDoc(CouchDbConnector.DB_NAME_RESOURCES);
        loadDesignDoc(CouchDbConnector.DB_NAME_URIS);

        LOG.info("all design docs loaded to databases - success");
    }

    private static void loadDesignDoc(String databaseName) {
        CouchDbClient adminCouchDbClient = CouchDbConnector.getCouchDbConnector().createCouchDbClient(true, databaseName);
        DesignDocument designDocFromDesk= adminCouchDbClient.design().getFromDesk(databaseName);
        DesignDocument designFromBase = getDesignDocFromBase(adminCouchDbClient, databaseName);

        if (designFromBase == null || !designDocFromDesk.equals(designFromBase)) {
            adminCouchDbClient.design().synchronizeWithDb(designDocFromDesk);
            LOG.info(databaseName + " - design docs updated");
        }
        else {
            LOG.info(databaseName + " - no need to update design docs ");
        }
    }

    private static DesignDocument getDesignDocFromBase(CouchDbClient client, String databaseName) {
        DesignDocument designFromBase;
        try {
            designFromBase = client.design().getFromDb("_design/"+databaseName);
        } catch (NoDocumentException e) {
            designFromBase = null;
        }
        return designFromBase;
    }

    public String getCouchDbPropertiesToString(CouchDbProperties couchDbProperties) {
        if (couchDbProperties == null) {
            return "couchDbProperties is null";
        }

        String result = "CouchDbProperties { ";
       	// required
        result += " dbName " + couchDbProperties.getDbName();
        result += ", createDbIfNotExist " + couchDbProperties.isCreateDbIfNotExist();
        result += ", protocol " + couchDbProperties.getProtocol();
        result += ", host " + couchDbProperties.getHost();
        result += ", port " + couchDbProperties.getPort();
        result += ", username " + couchDbProperties.getUsername();
        result += ", password ***";

        // optional
        result += ", socketTimeout " + couchDbProperties.getSocketTimeout();
        result += ", connectionTimeout " + couchDbProperties.getConnectionTimeout();
        result += ", maxConnections " + couchDbProperties.getMaxConnections();
        result += "}";
        return result;
    }
}
