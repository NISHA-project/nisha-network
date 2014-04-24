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

package pl.nask.nisha.commons.config;

public enum ConfigPropertyName {

    COUCHDB_HOST("couchdb_host"),
    COUCHDB_PORT("couchdb_port"),
    COUCHDB_ADMIN_USER("couchdb_admin_user"),
    COUCHDB_ADMIN_PASSWORD("couchdb_admin_password"),
    COUCHDB_NISHA_USER("couchdb_nisha_user"),
    COUCHDB_NISHA_PASSWORD("couchdb_nisha_password"),
    COUCHDB_HTTP_SOCKET_TIMEOUT("couchdb_http_socket_timeout");

    public final String value;

    private ConfigPropertyName(String val) {
        this.value = val;
    }

}
