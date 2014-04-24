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
<%@include file="commons.jsp" %>
<jsp:setProperty name="localNodeConfiguration" property="*"/>

<c:if test="${sessionScope.thisNodeRoleBean == null}">
    <jsp:useBean id="thisNodeRoleBean" class="pl.nask.nisha.manager.model.transfer.supportbeans.ValueBean" scope="session"/>
</c:if>

<jsp:setProperty name="thisNodeRoleBean" property="*"/>

<c:set var="msg" value="${requestScope.message}"/>
<c:set var="fileLoggerState" value="${sessionScope.fileLogging}"/>

<c:choose>
    <c:when test="${fileLoggerState == null or empty fileLoggerState or fileLoggerState ne 'enabled'}">
            <p align="center"><a href="<c:url value="index.jsp"/>">Cannot log to file. Return to home page</a></p>
    </c:when>
    <c:when test="${empty loggedOperatorBean.operator.operatorId }">
        <p align="center"><a href="<c:url value="login.jsp"/>">No operator logged - Log in please!</a></p>
    </c:when>
    <c:otherwise>

        <c:set var="contextRole" value="${thisNodeRoleBean.value}"/>
        <c:choose>
            <c:when test="${contextRole == null or empty contextRole}">
                <jsp:setProperty name="thisNodeRoleBean" property="value" value="UNDEFINED"/>
            </c:when>
            <c:when test="${contextRole eq 'UNDEFINED'}">
                <jsp:setProperty name="thisNodeRoleBean" property="*"/>
            </c:when>
        </c:choose>
        <c:set var="contextRole" value="${thisNodeRoleBean.value}"/>

        <c:set var="roleOk" value="false"/>
        <c:forEach var="roleName" items="${nodeRolesBean.nodeRoles}">
            <c:if test="${roleName eq contextRole}">
                <c:set var="roleOk" value="true"/>
            </c:if>
        </c:forEach>
        <c:choose>
            <c:when test="${roleOk ne 'true'}">
                <p align="center"><a href="<c:url value="login.jsp"/>">
                    <c:out value=">${contextRole}<"/> Unknown node role for operator. No operator logged - Log in please!</a></p>
            </c:when>
            <c:otherwise>
                <div>
                    <span class="spanLeftAbsolute">
                        <%--<div><c:out value="localName: ${sessionScope.localName}"/></div>--%>
                        <span class="spanRightAbsolute"><c:out value="${contextRole}"/></span>
                        <c:url var="menuUrl" value="LogoRedirectorServlet">
                            <c:param name="logoRedirect" value="goToMenu"/>
                        </c:url>
                        <img src="img/nisha-logo-small.png" alt="nisha" style="cursor: pointer"
                             onclick="location.href='${menuUrl}'">
                        <br/>
                        <c:set var="pageName" value="/menu.jsp"/>   <%-- needed in loggerLevel.jsp--%>
                        <c:set var="narrow" value="true"/>   <%-- needed in loggerLevel.jsp--%>
                        <%@include file="loggerLevel.jsp" %>
                    </span>
                    <span class="spanRightAbsolute">
                        <a href="${menuUrl}">Menu</a>&nbsp;&nbsp;Logged as <c:out value="${loggedOperatorBean.operator.operatorId}"/>
                        <c:url var="logoutUrl" value="Authenticator">
                            <c:param name="option" value="logout"/>
                        </c:url>
                        <a href="${logoutUrl}">Log me out!</a>
                    </span>
                </div>

                <p align="center" class="title">Operator Menu</p>

                <div id="optionsPage">

                    <c:choose>
                    <c:when test="${contextRole eq 'UNDEFINED'}">
                        <table align="center">
                            <tr><td class="optionTd">
                                <c:url var="configUrl" value="LocalConfigUpdaterServlet">
                                    <c:param name="option" value="update"/>
                                </c:url>
                                <a href="${configUrl}">Configuration</a></td>
                            </tr>
                            <tr><td class="optionTd">
                                <c:url var="operatorUrl" value="LocalOperatorUpdaterServlet">
                                    <c:param name="showLoggedOperator" value="true"/>
                                </c:url>
                                <a href="${operatorUrl}">Logged operator</a></td>
                            </tr>
                        </table>
                    </c:when>
                    <c:when test="${contextRole eq 'BASICNODE' or contextRole eq 'SUPERNODE'}">
                    <table class="optionTable" rules="none">
                        <tr>
                            <td>
                                <table>
                                    <tr><th align="left">Configuration</th></tr>
                                    <tr><td class="optionTd">
                                        <c:url var="configUrl" value="LocalConfigUpdaterServlet">
                                            <c:param name="option" value="update"/>
                                        </c:url>
                                        <a href="${configUrl}">Configuration</a></td>
                                    </tr>
                                    <tr><td class="optionTd">
                                        <c:url var="operatorUrl" value="LocalOperatorUpdaterServlet">
                                            <c:param name="showLoggedOperator" value="true"/>
                                        </c:url>
                                        <a href="${operatorUrl}">Logged operator</a></td></tr>
                                    <tr><td class="optionTd">&nbsp;</td></tr>
                                    <tr><td class="optionTd">&nbsp;</td></tr>
                                </table>
                            </td>
                            <td>
                                <table>
                                    <tr><th align="left">NISHA Network</th></tr>
                                    <c:if test="${contextRole eq 'SUPERNODE'}">
                                            <tr><td class="optionTd">
                                                <c:url var="addNodeUrl" value="NodeUpdater">
                                                    <c:param name="addNode" value="addNode"/>
                                                </c:url>
                                                <a href="${addNodeUrl}">New node</a></td>
                                            </tr>
                                    </c:if>
                                    <tr><td class="optionTd">
                                        <c:url var="netUpdateUrl" value="NetworkUpdater">
                                            <c:param name="option" value="showNetwork"/>
                                        </c:url>
                                        <a href="${netUpdateUrl}">Network update</a></td>
                                    </tr>
                                    <tr><td class="optionTd">
                                        <c:url var="alertsUrl" value="AlertSearchServlet">
                                            <c:param name="alertsOption" value="alertsOption"/>
                                        </c:url>
                                        <a href="${alertsUrl}">Alerts</a></td></tr>
                                    <c:if test="${contextRole eq 'BASICNODE'}">
                                        <tr><td class="optionTd">&nbsp;</td></tr>
                                    </c:if>
                                    <tr><td class="optionTd">&nbsp;</td></tr>
                                </table>
                            </td>
                            <td>
                                <c:set var="inboxCount" value=""/>
                                <c:set var="outboxCount" value=""/>
                                <c:set var="broadcastCount" value=""/>
                                <c:if test="${sessionScope.messageCountInfoBean.inboxUnreadMessages gt 0}">
                                    <c:set var="inboxCount" value="(${sessionScope.messageCountInfoBean.inboxUnreadMessages})"/>
                                </c:if>
                                <c:if test="${sessionScope.messageCountInfoBean.outboxNotSentMessages gt 0}">
                                    <c:set var="outboxCount" value="(${sessionScope.messageCountInfoBean.outboxNotSentMessages})"/>
                                </c:if>
                                <c:if test="${sessionScope.messageCountInfoBean.broadcastUnreadMessages gt 0}">
                                    <c:set var="broadcastCount" value="(${sessionScope.messageCountInfoBean.broadcastUnreadMessages})"/>
                                </c:if>

                                <c:url var="newMsgUrl" value="MessageSender">
                                    <c:param name="option" value="newMessage"/>
                                </c:url>
                                <c:url var="inboxUrl" value="MessagePagination">
                                    <c:param name="mode" value="inbox"/>
                                </c:url>
                                <c:url var="outboxUrl" value="MessagePagination">
                                    <c:param name="mode" value="outbox"/>
                                </c:url>
                                <c:url var="broadcastUrl" value="MessagePagination">
                                    <c:param name="mode" value="broadcast"/>
                                </c:url>

                                <table>
                                    <tr><th align="left">Messages</th></tr>
                                    <tr><td class="optionTd"><a href="${newMsgUrl}">New message</a></td></tr>
                                    <tr><td class="optionTd"><a href="${inboxUrl}"><c:out value="Inbox ${inboxCount}"/></a></td></tr>
                                    <tr><td class="optionTd"><a href="${outboxUrl}"><c:out value="Outbox ${outboxCount}"/></a></td></tr>
                                    <tr><td class="optionTd"><a href="${broadcastUrl}"><c:out value="Broadcast ${broadcastCount}"/></a></td></tr>
                                </table>
                            </td>
                            <td>
                                <table>
                                    <tr><th align="left">Resources</th></tr>
                                    <tr>
                                        <td class="optionTd">
                                            <c:url var="resourcesSearchUri" value="ResourceViewer">
                                                <c:param name="option" value="showResourceSearch"/>
                                            </c:url>
                                            <a href="${resourcesSearchUri}">Resources search</a></td>
                                    </tr>
                                    <tr><td class="optionTd">&nbsp;</td></tr>
                                    <tr><td class="optionTd">&nbsp;</td></tr>
                                    <tr><td class="optionTd">&nbsp;</td></tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                    </c:when>
                    </c:choose>
                </div>

                <c:if test="${msg ne null}">
                    <div align="center"><br/><c:out value="${msg}"/></div>
                </c:if>
                <hr/>
            </c:otherwise>
        </c:choose>
    </c:otherwise>
</c:choose>
