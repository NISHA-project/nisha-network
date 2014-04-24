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

<c:choose>
    <c:when test="${not empty loggedOperatorBean.operator.operatorId}">

        <div id="alertsPage" class="lower" align="center">
            <table class="formTable" rules="none">
                <tr>
                    <td valign="top" align="right"><label id="findLabel" for="alertQuerySearch">Find alerts sorted by: [name, time]&nbsp;</label></td>
                    <td>
                        <form action="AlertSearchServlet" method="post">
                            <input id="alertQuerySearch" name="alertQuery" type="text" size="<c:out value="${field_width}"/>"
                                   value="<c:out value="${requestScope.alertQuery}"/>"/>
                            <input name="alertAffectedNameSubmit" type="submit" value="Affected node">
                            <input name="alertDetectingNameSubmit" type="submit" value="Detecting node">
                        </form>
                    </td>
                </tr>
                <tr>
                    <td valign="top" align="right">Find alerts sorted by: [time]&nbsp;</td>
                    <td>
                        <form action="AlertSearchServlet" method="post">
                            <input name="findAllAlerts" type="submit" value="All alerts">
                            <input name="findThisNodeAlerts" type="submit" value="This node alerts">
                        </form>
                    </td>
                </tr>
            </table>



            <hr/>
        </div>


        <div class="lower">

            <c:if test="${alertPageBean ne null}">
                <c:if test="${not empty requestScope.searchMode}">
                    <form action="AlertSearchServlet" method="get">
                        <c:choose>
                            <c:when test="${requestScope.hideClosedAlerts}">
                                <c:set var="showHideText" value="Show closed alerts"/>
                            </c:when>
                            <c:otherwise>
                                <c:set var="showHideText" value="Hide closed alerts"/>
                            </c:otherwise>
                        </c:choose>
                        <div align="center">
                            <input type="submit" id="showHideAlerts" name="showHideAlerts" value="${showHideText}"/>
                            <input type="hidden" id="modeUnderButton" name="mode" value="${requestScope.searchMode}"/>
                            <input type="hidden" id="alertQueryUnderButton" name="alertQuery" value="${requestScope.alertQuery}"/>
                        </div>
                    </form>
                </c:if>
                <c:choose>
                    <c:when test="${fn:length(alertPageBean.alertPage.resultList) > 0}">
                        <c:set var="alertList" value="${alertPageBean.alertPage.resultList}"/>
                        <div class="title">Alerts</div>
                        <div align="center"><c:out value="[${requestScope.searchMode}]"/></div>

                        <c:set var="thisUri" value="http://${localNodeConfiguration.nodeDomainNameFromConfig}:${localNodeConfiguration.portNumberFromConfig}"/>
                        <table rules="rows" align="center" cellpadding="10">
                            <tr><td colspan="7" align="right">

                                </td>
                            </tr>

                            <tr>
                                <th>Detecting node</th>
                                <th>Affected node</th>
                                <th>Description</th>
                                <th>Timestamp</th>
                                <th>Alert state</th>
                            </tr>
                            <c:forEach var="alert" items="${alertList}">
                                <c:choose>
                                    <c:when test="${alert.affectedNodeName eq thisUri}">
                                        <c:set var="trClasses" value="highTr, backgroundGray"/>
                                    </c:when>
                                    <c:otherwise>
                                        <c:set var="trClasses" value="highTr"/>
                                    </c:otherwise>
                                </c:choose>
                                <form action="AlertUpdaterServlet" method="post">
                                    <tr class="${trClasses}">
                                        <td class="centerTd"><c:out value="${alert.detectingNodeName}"/></td>
                                        <td class="centerTd"><c:out value="${alert.affectedNodeName}"/></td>
                                        <td class="centerTd"><c:out value="${alert.description}"/></td>
                                        <td class="centerTd"><c:out value="${alert.timestamp}"/></td>
                                        <td class="centerTd"><c:out value="${alert.alertState}"/></td>

                                        <td class="centerTd">
                                        <%--close option for own alerts - for othera only if suernode--%>
                                        <c:if test="${thisNodeRoleBean.value eq 'SUPERNODE' or alert.affectedNodeName eq thisUri}">
                                            <c:if test="${alert.alertState ne 'CLOSED'}">
                                                <input type="submit" id="closeAlert" name="closeAlert" value="Close alert">
                                                <input type="hidden" id="_id" name="_id" value="${alert._id}">
                                                <input type="hidden" id="alertQuery" name="alertQuery" value="${requestScope.alertQuery}">
                                                <input type="hidden" id="mode" name="mode" value="${requestScope.searchMode}"/>
                                                <input type="hidden" id="pageNumber" name="pageNumber" value="${alertPageBean.alertPage.pageNumber}"/>
                                            </c:if>
                                        </c:if>
                                        </td>
                                    </tr>
                                </form>
                            </c:forEach>
                        </table>

                        <jsp:include page="paginNavigation.jsp">
                            <jsp:param name="mode" value="${requestScope.searchMode}"/>
                            <jsp:param name="searchQuery" value="${requestScope.alertQuery}"/>
                            <jsp:param name="pageNumber" value="${alertPageBean.alertPage.pageNumber}"/>
                            <jsp:param name="hasPrevious" value="${alertPageBean.alertPage.hasPrevious}"/>
                            <jsp:param name="hasNext" value="${alertPageBean.alertPage.hasNext}"/>
                            <jsp:param name="resultFrom" value="${alertPageBean.alertPage.resultFrom}"/>
                            <jsp:param name="resultTo" value="${alertPageBean.alertPage.resultTo}"/>
                            <jsp:param name="totalResults" value="${alertPageBean.alertPage.totalResults}"/>
                            <jsp:param name="pagesToIgnore" value="${requestScope.pagesToIgnore}"/>
                            <jsp:param name="pagination" value="AlertSearchServlet"/>
                        </jsp:include>
                    </c:when>
                    <c:otherwise>
                        <c:if test="${requestScope.searchResult ne null}">
                            <div align="center">
                                <c:out value="No results [${requestScope.searchMode}]"/>
                            </div>
                        </c:if>
                    </c:otherwise>
                </c:choose>
            </c:if>
        </div>
    </c:when>
</c:choose>

<%@include file="foot.jsp" %>
