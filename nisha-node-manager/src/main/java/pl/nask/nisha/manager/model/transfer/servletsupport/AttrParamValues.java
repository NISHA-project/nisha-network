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


public enum AttrParamValues {

    UPDATE("update"),
    ADD("add"),
    LIST_NODES("listNodes"),
    CONFIGURE("configure"),
    LOGIN("login"),
    LOGOUT("logout"),
    ADD_OPERATOR("addOperator"),
    DEFINE_OPERATOR("defineOperator"),
    ADD_SUBMIT("addSubmit"),
    UPDATE_SUBMIT("updateSubmit"),
    SAVE_CONFIG_SUBMIT("saveConfigSubmit"),
    DETAILS_SUBMIT("detailsSubmit"),
    OPERATOR_PREFIX("_oper"),
    BLOCK_OPERATOR("blockOperator"),
    UNBLOCK_OPERATOR("unblockOperator"),
    GO_TO_MENU("goToMenu"),
    GO_TO_INDEX("goToIndex"),
    LOCAL("local"),
    GLOBAL("global"),
    SEARCH_MODE_NAME("search by node name"),
    SEARCH_ALERT_MODE_AFF_NAME("search by affected node name"),
    SEARCH_ALERT_MODE_DET_NAME("search by detecting node name"),
    SEARCH_ALERT_MODE_ALL("search for all alerts"),
    SEARCH_THIS_NODE_ALERTS("search this node alerts"),
    PERMITTED("PERMITTED"),
    BLOCKED("BLOCKED"),
    ALERTS("ALERTS"),
    OK("OK"),
    SHOW_NETWORK("showNetwork"),
    UNDEFINED("UNDEFINED"),
    REMOVE("remove"),
    REMOVE_SUBMIT("removeSubmit"),
    LOGGER_LEVEL_SUBMIT("loggerLevelSubmit"),
    ENABLED("enabled"),
    DETACHED("detached"),
    UNKNOWN_ACTION("Unknown or unallowed action."),
    STATE_REASON_DEFAULT("[required]"),
    INBOX("inbox"),
    OUTBOX("outbox"),
    BROADCAST("broadcast"),
    NEW_MESSAGE("newMessage"),
    RESEND("resend"),
    PARENT_MESSAGE_DISPLAY("parentMessageDisplay"),
    CHOOSE_RECIPIENT("choose_recipient"),
    JSON_MARK("_:_"),
    BREAK_MARK("<br/>"),
    SHOW_RESOURCE_SEARCH("showResourceSearch"),
    ID("id"),
    TITLE("title"),
    ;

    private AttrParamValues (String value) {
        this.val = value;
    }

    public final String val;
}
