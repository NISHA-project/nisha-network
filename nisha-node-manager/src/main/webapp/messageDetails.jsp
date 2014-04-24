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
<jsp:setProperty name="messageToShow" property="*"/>

<c:if test="${not empty loggedOperatorBean.operator.operatorId}">

    <c:set var="mode" value="${requestScope.mode}"/>
    <c:set var="OUTBOX" value="outbox"/>
    <c:set var="INBOX" value="inbox"/>
    <c:set var="BROADCAST" value="broadcast"/>
    <c:set var="BLOCK" value="BLOCK"/>
    <c:set var="INVALIDATE" value="INVALIDATE"/>

    <c:set var="isReply" value="${not empty messageToShow.referenceId}"/>

    <c:if test="${mode eq OUTBOX or mode eq INBOX or mode eq BROADCAST}">
        <c:choose>
            <c:when test="${mode eq BROADCAST}">
                <c:set var="titleValue" value="Broadcast Message"/>
                <c:set var="otherNodeLabel" value="To:"/>
            </c:when>
            <c:when test="${mode eq OUTBOX}">
                <c:set var="titleValue" value="Sent Message"/>
                <c:set var="otherNodeLabel" value="To:"/>
            </c:when>
            <c:when test="${mode eq INBOX}">
                <c:set var="titleValue" value="Received Message"/>
                <c:set var="otherNodeLabel" value="From:"/>
            </c:when>
        </c:choose>

        <p class="title"><c:out value="${titleValue}"/></p>

            <table class="optionTable" rules="none" id="messageFormTable">
                <tr>
                    <th width="20%"></th>
                    <th width="80%"></th>
                </tr>
                <tr class="backgroundGray">
                    <td>To: </td>
                    <td>
                        <c:forEach var="nodeName" items="${messageToShow.recipientNodeIds}">
                            <c:out value="${nodeName}"/><br/>
                        </c:forEach>
                    </td>
                </tr>
                <tr>
                    <td>From: </td>
                    <td><c:out value="${messageToShow.senderNodeNamePort}"/></td>
                </tr>
                <tr class="backgroundGray">
                    <td>Subject: </td>
                    <td><c:out value="${messageToShow.subject}"/></td>
                </tr>
                <tr>
                    <td>Type:</td>
                    <td><c:out value="${messageToShow.type}"/></td>
                </tr>
                <tr class="backgroundGray">
                    <td>Date:</td>
                    <td><c:out value="${messageToShow.timeDate}"/></td>
                </tr>
                <tr><td colspan="2"><hr/></td> </tr>

                <tr>
                    <td>Text:</td>
                    <td id="messageBodyId">
                        <%--javascript will put message text here--%>
                    </td>
                    <script type="text/javascript">prepareMessageBody('<c:out value="${messageToShow.body}"/>', 'messageBodyId');</script>
                </tr>

                <tr class="backgroundGray">
                    <td>Attachments:</td>
                    <td>
                        <c:choose>
                            <c:when test="${fn:length(messageToShow.attachments) > 0}">
                                <table id="attachmentTable" class="messageWideField" rules="none">
                                    <tr><th width="60%"></th>
                                        <th width="25%"></th>
                                        <th width="15%"></th>
                                    </tr>
                                    <c:set var="counter" value="0"/>
                                    <c:forEach var="attachment" items="${messageToShow.attachments}">
                                        <form method="post" action="AttachmentDownloader">
                                            <c:set var="attachName" value="${attachment.key}"/>
                                            <c:choose>
                                                <c:when test="${fn:containsIgnoreCase(attachName, BLOCK) or fn:containsIgnoreCase(attachName, INVALIDATE)}">
                                                    <c:set var="elementName" value="${fn:replace(attachName, '.json', '')}"/>
                                                    <c:set var="infoTab" value="${fn:split(elementName, '_:_')}" />
                                                    <c:set var="mesType" value="${infoTab[0]}"/>

                                                    <c:choose>
                                                        <c:when test="${infoTab[0] eq 'BLOCK'}">
                                                            <c:out value="${infoTab[0]} PLEASE:"/>
                                                            <c:url var="blockAsRedirectUrl" value="NodeUpdater">
                                                                <c:param name="blockAskRedirect" value="blockAskRedirect"/>
                                                                <c:param name="nodeDomainNameFromRingInfo" value="${infoTab[1]}"/>
                                                            </c:url>
                                                            <a href="${blockAsRedirectUrl}"><c:out value="${infoTab[1]}"/></a>
                                                        </c:when>
                                                        <c:when test="${infoTab[0] eq INVALIDATE}">
                                                            <c:out value="${infoTab[0]} PLEASE:"/>
                                                            <c:url var="invalidateAskRedirectUrl" value="ResourceViewer">
                                                                <c:param name="invalidateAskRedirect" value="invalidateAskRedirect"/>
                                                                <c:param name="artId" value="${infoTab[2]}"/>
                                                                <c:param name="searchMode" value="false"/>
                                                            </c:url>
                                                            <a href="${invalidateAskRedirectUrl}"><c:out value="${infoTab[1]}"/></a>
                                                        </c:when>
                                                        <c:otherwise>otherwise</c:otherwise>
                                                    </c:choose>
                                                </c:when>
                                                <c:otherwise>
                                                    <tr>
                                                        <td>
                                                            <c:set var="attachId" value="attach${counter + 1}"/>

                                                            <input type="hidden" id="_id" name="_id" value="<c:out value="${messageToShow._id}"/>">
                                                            <input type="hidden" id="modeAttach" name="mode" value="<c:out value="${mode}"/>"/>
                                                            <input type="hidden" id="attachmentName" name="attachmentName" value="<c:out value="${attachName}"/>">
                                                            <label for="<c:out value="${attachId}"/>"></label>
                                                            <input class="messageWideField" id="<c:out value="${attachId}"/>" name="<c:out value="${attachId}"/>" value="<c:out value="${attachName}"/>" readonly="readonly">
                                                        </td>
                                                        <td align="right" class="paddedCell">

                                                            <input class="messageWideField" type="submit" id="downloadAttachmentSubmit" name="downloadAttachmentSubmit" value="Download">
                                                        </td>
                                                        <td>
                                                            <input class="messageWideField" type="submit" id="displayAttachmentSubmit" name="displayAttachmentSubmit" value="Display">
                                                        </td>
                                                    </tr>
                                                </c:otherwise>
                                            </c:choose>
                                        </form>
                                    </c:forEach>
                                </table>
                            </c:when>
                            <c:otherwise>
                                --none--
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
                <tr><td colspan="2"><hr/></td></tr>
                <tr>
                    <td><c:if test="${isReply}">Response to parent message: </c:if></td>
                    <td>
                        <table class="messageWideField" rules="none">
                            <tr><th width="60%"></th><th width="20%"></th><th width="20%"></th></tr>
                            <tr>
                                <td><c:if test="${isReply}">
                                    <c:url var="msgDisplayUrl" value="MessageDisplayer">
                                        <c:param name="mode" value="${mode}"/>
                                        <c:param name="option" value="parentMessageDisplay"/>
                                        <c:param name="_id" value="${messageToShow.referenceId}"/>
                                    </c:url>
                                    <a href="${msgDisplayUrl}"><c:out value="${requestScope.parentMessageSubject}"/></a>
                                </c:if></td>
                                <td>
                                    <c:if test="${mode eq BROADCAST}">
                                        <form action="MessageSender" method="post">
                                            <input type="hidden" id="modeReplySender" name="mode" value="<c:out value="${mode}"/>"/>
                                            <input type="hidden" id="parentReferenceIdSender" name="parentReferenceId" value="<c:out value="${messageToShow._id}"/>">
                                            <input class="messageWideField" type="submit" id="replyBroadcastSenderSubmit" name="replyBroadcastSenderSubmit" value="Reply Sender">
                                        </form>
                                    </c:if>
                                </td>
                                <td align="right" class="paddedCell">
                                    <form action="MessageSender" method="post">
                                        <input type="hidden" id="modeReply" name="mode" value="<c:out value="${mode}"/>">
                                        <input type="hidden" id="parentReferenceId" name="parentReferenceId" value="<c:out value="${messageToShow._id}"/>">
                                        <input class="messageWideField" type="submit" id="replySubmit" name="replySubmit" value="Reply">
                                    </form>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
        </table>
    </c:if>
</c:if>

<%@include file="foot.jsp" %>