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

<jsp:setProperty name="messagePageBean" property="*"/>
<jsp:setProperty name="messageDisplayModeBean" property="*"/>

<c:set var="showModeText" value=""/>
<c:choose>
    <c:when test="${messageDisplayModeBean.value eq 'ALL'}" >
        <c:set var="showModeText" value="Hide archived"/>
    </c:when>
    <c:when test="${messageDisplayModeBean.value eq 'NOT_ARCHIVED'}">
        <c:set var="showModeText" value="Show all"/>
    </c:when>
</c:choose>

<c:set var="pageNumber" value="${messagePageBean.messagePage.pageNumber}"/>
<c:choose>
    <c:when test="${not empty requestScope.pageNumber}">
        <c:set var="pageNumber" value="${requestScope.pageNumber}"/>
    </c:when>
</c:choose>

<c:if test="${not empty loggedOperatorBean.operator.operatorId}">

    <c:set var="mode" value="${requestScope.mode}"/>
    <c:set var="OUTBOX" value="outbox"/>
    <c:set var="INBOX" value="inbox"/>
    <c:set var="BROADCAST" value="broadcast"/>

    <c:set var="READ_IN_PROGRESS" value="READ/IN-PROGRESS"/>
    <c:set var="READ" value="READ"/>
    <c:set var="SENT" value="SENT"/>
    <c:set var="NOT_SENT" value="NOT_SENT"/>
    <c:set var="NEW_MESSAGE" value="NEW_MESSAGE"/>
    <c:set var="MESSAGE" value="MESSAGE"/>

    <c:if test="${mode eq INBOX or mode eq OUTBOX or mode eq BROADCAST}">
        <div id="" class="lower">
            <c:choose>
                <c:when test="${mode eq OUTBOX}">
                    <p class="title">Sent messages</p>
                    <c:set var="otherNodeColumnHeader" value="To"/>
                </c:when>
                <c:when test="${mode eq BROADCAST}">
                    <p class="title">Broadcast messages</p>
                    <c:set var="otherNodeColumnHeader" value="From"/>
                </c:when>
                <c:otherwise>
                    <p class="title">Received messages</p>
                    <c:set var="otherNodeColumnHeader" value="From"/>
                </c:otherwise>
            </c:choose>
            <hr/>
            <form action="MessagePagination" method="post">
                <div>
                    <input type="hidden" id="modeRefresh" name="mode" value="<c:out value="${mode}"/>">
                    <table class="messageFormTable" rules="none">
                        <tr>
                            <td>
                                <table rules="none">
                                    <tr>
                                        <c:if test="${mode eq INBOX}">
                                            <td class="paddedCell"><input class="messageWideField" type="submit" id="markReadSubmit" name="markReadSubmit" value="Mark read"></td>
                                            <td class="paddedCell"><input class="messageWideField" type="submit" id="markDoneSubmit" name="markDoneSubmit" value="Mark done"></td>
                                        </c:if>
                                        <c:choose>
                                            <c:when test="${mode eq INBOX or mode eq OUTBOX}">
                                                <td class="paddedCell"><input class="messageWideField" type="submit" id="archiveSubmit" name="archiveSubmit" value="Archive"></td>
                                            </c:when>
                                            <c:otherwise>&nbsp;</c:otherwise>
                                        </c:choose>
                                    </tr>
                                    <tr>
                                        <c:if test="${mode eq INBOX}">
                                            <td class="paddedCell"><input class="messageWideField" type="submit" id="undoReadSubmit" name="undoReadSubmit" value="Undo read"></td>
                                            <td class="paddedCell"><input class="messageWideField" type="submit" id="undoDoneSubmit" name="undoDoneSubmit" value="Undo done"></td>
                                        </c:if>
                                        <c:choose>
                                            <c:when test="${mode eq INBOX or mode eq OUTBOX}">
                                            <td class="paddedCell"><input class="messageWideField" type="submit" id="undoArchiveSubmit" name="undoArchiveSubmit" value="Undo archive"></td>
                                            </c:when>
                                            <c:otherwise>&nbsp;</c:otherwise>
                                        </c:choose>
                                    </tr>
                                </table>
                            </td>
                            <td align="right">
                                <table>
                                    <tr>
                                        <td><input class="messageWideField" type="submit" id="refresh" name="refreshSubmit" value="Refresh"></td>
                                    </tr>
                                    <tr>
                                        <c:if test="${mode eq INBOX or mode eq OUTBOX}">
                                            <td><input class="messageWideField" type="submit" id="showHideSubmit"
                                                       name="showHideSubmit" value="<c:out value="${showModeText}"/>"/></td>
                                        </c:if>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </div>
                <hr/>
                <table class="messageBoxTable" rules="all" id="messageTable">
                    <tr>
                        <th width="2%"><label for="selectAllCheck"></label><input type="checkbox" id="selectAllCheck" onchange="affectAllCheckboxes();"></th>
                        <th width="13%"><c:out value="${otherNodeColumnHeader}"/></th>
                        <th width="25%">Date</th>
                        <th width="30%">Subject</th>
                        <th width="12%">Type</th>
                        <c:if test="${mode ne BROADCAST}">
                            <th width="13%">Status</th>
                        </c:if>
                    </tr>
                    <c:set var="counter" value="0"/>
                    <c:forEach var="message" items="${messagePageBean.messagePage.resultList}">
                        <c:set var="stateToDisplay" value="${message.messageState}"/>
                        <c:if test="${stateToDisplay ne 'DISCARDED'}">
                            <c:choose>
                                <c:when test="${mode eq BROADCAST}">
                                    <tr class="backgroundGray">
                                    <c:set var="unread" value="true"/>
                                    <c:if test="${fn:contains(messageCountInfoBean.broadcastReadMessagesIds[message._id], message._id)}">
                                        <tr class="backgroundWhite">
                                        <c:set var="unread" value="false"/>
                                    </c:if>
                                </c:when>
                                <c:when test="${stateToDisplay eq NEW_MESSAGE and mode ne BROADCAST}">
                                    <tr class="backgroundGray">
                                    <c:set var="unread" value="true"/>
                                </c:when>
                                <c:otherwise>
                                    <tr class="backgroundWhite">
                                    <c:set var="unread" value="false"/>
                                </c:otherwise>
                            </c:choose>

                                <td><label><input type="checkbox" class="checkboxClass" id="<c:out value="checkbox_${message._id}"/>"
                                                  name="<c:out value="checkbox_${message._id}"/>"/></label>
                                    <input type="hidden" id="row${counter}" name="row${counter}" value="<c:out value="${message._id}"/>"/>
                                </td>
                                <td>
                                <c:choose>
                                    <c:when test="${otherNodeColumnHeader eq 'To'}">
                                        <c:out value="${message.recipientNodeIds}"/>
                                    </c:when>
                                    <c:when test="${otherNodeColumnHeader eq 'From'}">
                                        <c:out value="${message.senderNodeNamePort}"/>
                                    </c:when>
                                    <c:otherwise>
                                        undefined
                                    </c:otherwise>
                                </c:choose>
                                </td>
                                <td><c:out value="${message.timeDate}"/></td>

                                <c:set var="resend" value="false"/>
                                <c:choose>
                                    <c:when test="${stateToDisplay eq READ and message.type ne MESSAGE}">
                                        <c:set var="stateToDisplay" value="${READ_IN_PROGRESS}"/>
                                    </c:when>
                                    <c:when test="${stateToDisplay eq NEW_MESSAGE and mode eq OUTBOX}">
                                        <c:set var="stateToDisplay" value="${NOT_SENT}"/>
                                        <c:set var="resend" value="true"/>
                                    </c:when>
                                </c:choose>
                                <td>
                                    <c:if test="${unread eq true}">
                                        <span class="boldText">
                                    </c:if>
                                        <c:choose>
                                            <c:when test="${resend}">
                                                <c:url var="resendUrl" value="MessageSender">
                                                    <c:param name="mode" value="${mode}"/>
                                                    <c:param name="option" value="resend"/>
                                                    <c:param name="_id" value="${message._id}"/>
                                                </c:url>
                                                <a href="${resendUrl}">DRAFT:&nbsp;<c:out value="${message.subject}"/></a>
                                            </c:when>
                                            <c:otherwise>
                                                <c:url var="showUrl" value="MessageDisplayer">
                                                    <c:param name="mode" value="${mode}"/>
                                                    <c:param name="option" value="messageDisplay"/>
                                                    <c:param name="_id" value="${message._id}"/>
                                                </c:url>
                                                <a href="${showUrl}"><c:out value="${message.subject}"/></a>
                                            </c:otherwise>
                                        </c:choose>
                                    <c:if test="${(message.messageState eq NEW_MESSAGE)}">
                                        </span>
                                    </c:if>
                                </td>
                                <td>
                                    <c:out value="${message.type}"/>
                                </td>
                                <c:if test="${mode ne BROADCAST}">
                                    <td id="<c:out value="row${counter}messageState"/>"><c:out value="${stateToDisplay}"/></td>
                                </c:if>
                            </tr>
                            <c:set var="counter" value="${counter+1}"/>
                        </c:if>
                    </c:forEach>
                </table>
            </form>
            <br/>
        </div>

        <jsp:include page="paginNavigation.jsp">
            <jsp:param name="mode" value="${mode}"/>
            <jsp:param name="pageNumber" value="${pageNumber}"/>
            <jsp:param name="hasPrevious" value="${messagePageBean.messagePage.hasPrevious}"/>
            <jsp:param name="hasNext" value="${messagePageBean.messagePage.hasNext}"/>
            <jsp:param name="resultFrom" value="${messagePageBean.messagePage.resultFrom}"/>
            <jsp:param name="resultTo" value="${messagePageBean.messagePage.resultTo}"/>
            <jsp:param name="totalResults" value="${messagePageBean.messagePage.totalResults}"/>
            <jsp:param name="pagination" value="MessagePagination"/>
        </jsp:include>
    </c:if>
</c:if>

<%@include file="foot.jsp" %>
