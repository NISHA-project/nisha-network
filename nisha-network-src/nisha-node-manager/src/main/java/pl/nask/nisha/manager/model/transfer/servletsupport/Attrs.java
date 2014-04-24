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
package pl.nask.nisha.manager.model.transfer.servletsupport;

public enum Attrs {

    THIS_NODE_ROLE_BEAN("thisNodeRoleBean"),
    NODE_ROLES("nodeRoles"),
    MESSAGE("message"),
    OPTION("optionAttr"),
    SEARCH_RESULT("searchResult"),
    LOCAL_NODE_CONFIGURATION("localNodeConfiguration"),
    LOGGED_OPERATOR_BEAN("loggedOperatorBean"),
    NODE_RING_INFO("nodeRingInfo"),
    NODE_RING_INFO_RICH_COLLECTION("nodeRingInfoRichCollection"),
    NODE_INFO("nodeInfo"),
    ALERT_PAGE_BEAN("alertPageBean"),
    HIDE_CLOSED_ALERTS("hideClosedAlerts"),
    RESOURCE_COLLECTION("resourceCollection"),
    OPERATOR_CONTACT("operatorContact"),
    OPERATOR_TO_SHOW("operatorToShow"),
    SHOW_LOGGED_OPERATOR("showLoggedOperator"),
    IS_GLOBAL_SEARCH("isGlobalSearch"),
    ARTICLE("article"),
    SEARCH_STATE_FILTERS("searchAcceptedStates"),
    NETWORK("network"),
    LOGGER_LEVEL("loggerLevel"),
    FILE_LOGGING("fileLogging"),
    MODE("mode"),
    NODE_NAMES_COLLECTION("nodeNamesCollection"),
    MESSAGE_PAGE_BEAN("messagePageBean"),
    RESOURCE_PAGE_BEAN("resourcePageBean"),
    MESSAGE_TO_SHOW("messageToShow"),
    REPLY("reply"),
    PARENT_MESSAGE_SUBJECT("parentMessageSubject"),
    BLOCK_INVALIDATE_JSON("blockInvalidateJson"),
    BLOCK_INVALIDATE_NAME("blockInvalidateName"),
    MESSAGE_COUNT_INFO_BEAN("messageCountInfoBean"),
    RESOURCE_SEARCH_CONTEXT("resourceSearchContext"),
    PAGES_TO_IGNORE("pagesToIgnore"),
    ;

    private Attrs (String val) {
        this.val = val;
    }

    public final String val;
}
