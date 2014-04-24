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

public enum Params {

    _ID("_id"),
    _REV("_rev"),
    ALERT_AFFECTED_NAME_SUBMIT("alertAffectedNameSubmit"),
    ALERT_DETECTING_NAME_SUBMIT("alertDetectingNameSubmit"),
    ALERT_QUERY("alertQuery"),
    CLOSE_ALERT("closeAlert"),
    SEARCH_QUERY("searchQuery"),

    BLOCK_OPERATOR_ID("blockOperatorId"),
    OPERATOR_CONTACT_ID("operatorContactId"),
    OPERATOR_EMAIL("email"),
    OPERATOR_FULL_NAME("fullName"),
    OPERATOR_ID("operatorId"),
    OPERATOR_NEW_PASSWORD("newPassword"),
    OPERATOR_NEW_PASSWORD_2("newPassword2"),
    OPERATOR_PASSWORD("operatorPassword"),
    OPERATOR_PRIVATE_KEY("privateKey"),
    OPERATOR_TELEPHONE("telephone"),

    CERTYFICATE("certificate"),
    NODE_DOMAIN_NAME_FROM_CONFIG("nodeDomainNameFromConfig"),
    NODE_DOMAIN_NAME_FROM_RING_INFO("nodeDomainNameFromRingInfo"),
    NODE_DOMAIN_NAME_FROM_NODE_INFO("nodeDomainNameFromNodeInfo"),
    NODE_DESCRIPTION("description"),
    NODE_LOCATION("location"),
    PORT_NUMBER_FROM_CONFIG("portNumberFromConfig"),
    PORT_NUMBER_FROM_RING_INFO("portNumberFromRingInfo"),
    ROLE("role"),
    STATE("state"),
    STATE_REASON("stateReason"),

    ART_ID("artId"),
    FIND_ALL_ALERTS("findAllAlerts"),
    FIND_THIS_NODE_ALERTS("findThisNodeAlerts"),
    QUERY("query"),
    OPTION("option"),

    ADD_NODE("addNode"),
    FIND_ALL_RESOURCES("findAllResources"),
    INVALIDATE_RESOURCE_SUBMIT("invalidateResourceSubmit"),
    LOGIN_SUBMIT("loginSubmit"),
    LOGO_REDIRECT("logoRedirect"),
    QUERY_SUBMIT_NAME("querysubmitName"),
    SEARCH_MODE("searchMode"),
    SOURCE_PAGE("sourcePage"),
    SHOW_HIDE_ALERTS("showHideAlerts"),
    ALERTS_OPTION("alertsOption"),
    SHOW_RESOURCE_DETAILS("showResourceDetails"),
    UPDATE_OPERATOR_SUBMIT("updateOperatorSubmit"),

    PREV_STATE_REASON("prevStateReason"),
    DISCARD_MESSAGE_SUBMIT("discardSubmit"),
    SEND_MESSAGE_SUBMIT("sendMessageSubmit"),
    MESSAGE_TYPE("messageType"),
    CAST("cast"),
    MESSAGE_TEXT("messageText"),
    SUBJECT("subject"),
    NODE_PARAMS_NAMES("nodeParamsNames"),
    ATTACH_PARAMS_NAMES("attachParamsNames"),
    ATTACH("attach"),
    JUMP_TO_PAGE("jumpToPage"),
    PAGE_NUMBER("pageNumber"),
    ROWS_PER_PAGE("rowsPerPage"),
    RESIZE_PAGE("resizePage"),
    REFRESH_SUBMIT("refreshSubmit"),
    ARCHIVE_SUBMIT("archiveSubmit"),
    UNDO_ARCHIVE_SUBMIT("undoArchiveSubmit"),
    MESSAGE_DISPLAY_MODE_BEAN("messageDisplayModeBean"),
    SHOW_HIDE_SUBMIT("showHideSubmit"),
    MESSAGE_DISPLAY("messageDisplay"),
    MARK_READ_SUBMIT("markReadSubmit"),
    MARK_DONE_SUBMIT("markDoneSubmit"),
    UNDO_READ_SUBMIT("undoReadSubmit"),
    UNDO_DONE_SUBMIT("undoDoneSubmit"),
    REPLY_SUBMIT("replySubmit"),
    REPLY_BROADCAST_SENDER_SUBMIT("replyBroadcastSenderSubmit"),
    REPLY_RECIPIENTS("replyRecipients"),
    PARENT_REFERENCE_ID("parentReferenceId"),
    MESSAGE_SENDER_NODE_NAME_PORT("messageSenderNodeNamePort"),
    MESSAGE_RECIPIENT_NODES("messageRecipientNodes"),
    FILTER_SUPERNODES_ONLY("filterSupernodesOnly"),
    ATTACHMENT_NAME("attachmentName"),
    DOWNLOAD_ATTACHMENT_SUBMIT("downloadAttachmentSubmit"),
    DISPLAY_ATTACHMENT_SUBMIT("displayAttachmentSubmit"),
    INVALIDATE_RESOURCE_ASK_SUBMIT("invalidateResourceAskSubmit"),
    BLOCK_NODE_ASK_SUBMIT("blockNodeAskSubmit"),
    ARTICLE_TITLE("articleTitle"),
    BLOCK_INVALIDATE_JSON("blockInvalidateJson"),
    BLOCK_ASK_REDIRECT("blockAskRedirect"),
    INVALIDATE_ASK_REDIRECT("invalidateAskRedirect"),
    RESOURCE_SEARCH_SUBMIT("resourceSearchSubmit"),
    SEARCH_RANGE("searchRange"),
    QUERY_TYPE("queryType"),
    RESOURCE_QUERY("resourceQuery"),
    DRAFT_SUBMIT("draftSubmit"),
    DRAFT_ID("draftId"),
    ;

    private Params(String val) {
        this.val = val;
    }

    public final String val;
}
