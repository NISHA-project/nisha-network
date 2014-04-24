<%--
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
--%>

<%@include file="options.jsp" %>

<c:if test="${not empty loggedOperatorBean.operator.operatorId}">

<jsp:useBean id="nodeNamesCollection" class="pl.nask.nisha.manager.model.transfer.supportbeans.StringCollection">
    <jsp:setProperty name="nodeNamesCollection" property="*"/>
</jsp:useBean>

<c:set var="nodeNamesAsOneString" value=""/>
<c:set var="broadcast" value="broadcast"/>
<c:set var="MESSAGE" value="MESSAGE"/>
<c:set var="BLOCK" value="BLOCK"/>
<c:set var="INVALIDATE" value="INVALIDATE"/>

<c:set var="isReply" value="${requestScope.reply eq true}"/>
<c:set var="resend" value="${requestScope.resend == 'resend'}"/>
<c:set var="blockInvalidateJson" value="${requestScope.blockInvalidateJson}"/>

    <p class="title"><c:if test="${resend}">Resend&nbsp;</c:if>Message</p>
    <input type="hidden" id="selfName" value="<c:out value="${localNodeConfiguration.nodeDomainNameFromConfig}"/>"/>
    <form action="MessageSender" method="post" onsubmit="return sendMessageValidation();" id="sendMessageForm" enctype="multipart/form-data">
        <input type="hidden" id="parentReferenceId" name="parentReferenceId" value="<c:out value="${messageToShow.referenceId}"/>">
        <input type="hidden" id="_id" name="_id" value="<c:out value="${messageToShow._id}"/>">
        <input type="hidden" id="_rev" name="_rev" value="<c:out value="${messageToShow._rev}"/>">
        <input type="hidden" id="filterSupernodesOnly" name="filterSupernodesOnly" value="false">

        <table class="optionTable" rules="none" id="messageFormTable">
            <tr>
                <th width="20%"></th>
                <th width="80%"></th>
            </tr>
            <tr>
                <td><label for="selectnode1">To:</label></td>
                <td>
                    <c:choose>
                        <c:when test="${isReply}">
                            <table id="nodeNamesTable" class="messageWideField" rules="none">
                                <tr><td><c:out value="${messageToShow.recipientNodeIds}"/>
                                    <input class="messageWideField" type="hidden" id="replyRecipients" name="replyRecipients"
                                           value="<c:out value="${messageToShow.recipientNodeIds}"/>">
                                </td></tr>
                            </table>
                        </c:when>
                        <c:otherwise>
                            <table id="nodeNamesTable" class="messageWideField" rules="none">
                                <tr>
                                    <th width="95%"></th>
                                    <th width="5%"></th>
                                </tr>
                                <c:set var="toFieldColspanVal" value="2"/>
                                <c:if test="${messageToShow.type eq MESSAGE}">
                                    <c:set var="toFieldColspanVal" value="1"/>
                                    <tr id="rowcast"><td colspan="2">
                                            <label><input type="radio" name="cast" id="multicast" value="multicast"
                                                          onkeypress="return noEnterSubmit(event);"
                                                          checked="checked" onclick="multicastChosen();" >multicast</label>
                                            <label><input type="radio" name="cast" id="broadcast" value="broadcast"
                                                          onkeypress="return noEnterSubmit(event);"
                                                          onclick="broadcastChosen();">broadcast</label>
                                        </td>
                                    </tr>
                                </c:if>

                                <tr id="rownode1">
                                    <td colspan="${toFieldColspanVal}">
                                        <select id="selectnode1" name="selectnode1" class="messageWideField">
                                            <c:forEach var="nodeAddress" items="${requestScope.nodeNamesCollection.stringList}">
                                                    <option value="<c:out value="${nodeAddress}"/>"><c:out value="${nodeAddress}"/></option>
                                                <c:set var="nodeNamesAsOneString" value="${nodeNamesAsOneString} ${nodeAddress}"/>
                                            </c:forEach>
                                        </select>
                                        <input type="hidden" id="nodesNamesAsOneString" name="nodesNamesAsOneString" value="<c:out value="${nodeNamesAsOneString}"/>">
                                        <input type="hidden" id="nodeParamsNames" name="nodeParamsNames" value="<c:out value=""/>"/>
                                        <script type="text/javascript">updateParamsNames('nodeNamesTable', 'nodeParamsNames');</script>
                                    </td>
                                    <c:if test="${messageToShow.type eq MESSAGE}">
                                        <td class="paddedCell"><span id="plusSpan" class="cursorPointer" onclick="addNodeRowToTable('minus');"><img src="img/add.png" alt="add"></span></td>
                                    </c:if>
                                </tr>
                                <c:if test="${resend == true}">
                                    <c:set var="recipients" value="${messageToShow.recipientNodeIds}"/>
                                    <input type="hidden" id="recipientsAsString" name="recipientsAsString" value="<c:out value="${recipients}"/>"/>
                                    <script type="text/javascript">loadMessageRecipientsToForm()</script>
                                </c:if>
                            </table>
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>
            <tr>
                <c:set var="fromAddress" value="${localNodeConfiguration.nodeDomainNameFromConfig}:${localNodeConfiguration.portNumberFromConfig}"/>
                <td>From:</td>
                <td><c:out value="${fromAddress}"/>
                    <input type="hidden" class="messageWideField" id="messageSenderNodeNamePort"
                           name="messageSenderNodeNamePort" value="<c:out value="${fromAddress}"/>">
                </td>
            </tr>
            <tr>
                <td><label for="subject">Subject:</label></td>
                <td>
                    <input class="messageWideField" id="subject" name="subject" onkeypress="return noEnterSubmit(event);"
                             value="<c:out value="${messageToShow.subject}"/>" maxlength="2048"/>
                </td>
            </tr>
            <tr>
                <td>Type:</td>
                <td><c:out value="${messageToShow.type}"/>
                    <input type="hidden" class="messageWideField" id="messageType" name="messageType" value="<c:out value="${messageToShow.type}"/>">
                </td>
            </tr>

            <tr><td colspan="2"><hr/></td> </tr>

            <tr>
                <td><label for="messageText">Text:</label></td>
                <td><c:set var="citationTitle" value=""/>
                    <c:set var="text" value="${messageToShow.body}"/>
                    <textarea class="textAreaInForm" id="messageText" name="messageText"
                        rows="10" cols="80"><c:out value="${text}"/></textarea>
                </td>
            </tr>

            <tr><td colspan="2"><hr/></td> </tr>

            <tr>
                <td>Attachments:</td>
                <td>
                    <c:set var="currentType" value="${MESSAGE}"/>
                    <c:if test="${messageToShow != null and messageToShow.type != null}">
                        <c:set var="currentType" value="${messageToShow.type}"/>
                    </c:if>

                    <c:if test="${(currentType != MESSAGE) and (resend == false)}">
                        <label for="blockInvalidateJson">${messageToShow.type}:</label><br/>
                            <input type="text" class="messageWideField" id="blockInvalidateJson" name="blockInvalidateJson"
                                   value="<c:out value="${blockInvalidateJson}"/>" readonly="readonly">
                        <input type="hidden" id="blockInvalidateName" name="blockInvalidateName"
                               value="<c:out value="${requestScope.blockInvalidateName}"/>">
                    </c:if>
                    <table id="attachmentTable" rules="none">
                        <tr id="rowattachPlus">
                            <td colspan="2">
                                <input type="hidden" id="maxAttachmentSize" name="maxAttachmentSize" value="<c:out value="${messageToShow.maxAttachmentSize}"/>">
                                <input type="hidden" id="resend" name="resend" value="<c:out value="${resend}"/>">
                                <input type="hidden" id="attachParamsNames" name="attachParamsNames" value="">
                                <script type="text/javascript">updateParamsNames('attachmentTable', 'attachParamsNames')</script>
                                <span id="plusAttachSpan" class="cursorPointer" onclick="addAttachRowToTable();"><img src="img/add.png" alt="add"></span>
                            </td>
                        </tr>
                        <c:set var="counter" value="1"/>
                        <c:forEach var="attach" items="${messageToShow.attachments}">
                            <c:set var="attachId" value="attach${counter}"/>
                            <tr id="<c:out value="row${attachId}"/>">
                                <td>
                                    <label><input type="text" class="messageWideField" id="<c:out value="${attachId}"/>" name="<c:out value="${attachId}"/>"
                                                  value="<c:out value="${attach.key}"/>" readonly="readonly"></label>
                                </td>
                                <td class="paddedCell">
                                    <span class="cursorPointer" onclick="removeAttachRow(this);"><img src="img/remove.png" alt="remove"></span>
                                </td>
                            </tr>
                            <c:set var="counter" value="${counter + 1}"/>
                        </c:forEach>
                    </table>
                </td>
            </tr>

            <tr><td colspan="2"><hr/></td> </tr>

            <tr><td>
                    <c:url var="discardMsgUrl" value="MessageSender">
                        <c:param name="discardSubmit" value="discardSubmit"/>
                        <c:param name="draftId" value="${messageToShow._id}"/>
                    </c:url>
                    <c:url var="draftMsgUrl" value="MessageSender">
                        <c:param name="draftSubmit" value="draftSubmit"/>
                    </c:url>
                    <input type="button" id="discardSubmit" name="discardSubmit" value="Discard"
                           onclick="window.location.href='<c:out value="${discardMsgUrl}"/>'">
                </td>
                <td align="right">
                    <input type="submit" id="draftSubmit" name="draftSubmit" value="Save draft"
                             onclick="window.location.href='<c:out value="${draftMsgUrl}"/>'">
                    <c:if test="${isReply}"><input type="hidden" id="reply" name="reply" value="reply"></c:if>
                    <input type="submit" id="sendMessageSubmit" name="sendMessageSubmit" value="Send">
                </td>
            </tr>
        </table>
    </form>
</c:if>

<%@include file="foot.jsp" %>
