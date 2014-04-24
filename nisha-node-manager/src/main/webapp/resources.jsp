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

<jsp:useBean id="resourceSearchContext" class="pl.nask.nisha.manager.model.domain.resources.ResourceSearchContext" scope="request">
    <jsp:setProperty name="resourceSearchContext" property="*"/>
</jsp:useBean>

<c:if test="${not empty loggedOperatorBean.operator.operatorId}">
    <c:set var="role"/>
    <c:set var="isGlobalSearchMode"/>

    <c:choose>
        <c:when test="${thisNodeRoleBean.value eq 'SUPERNODE'}">
            <c:set var="role" value="supernode"/>
            <c:set var="isGlobalSearchMode" value="global"/>
        </c:when>
        <c:when test="${thisNodeRoleBean.value eq 'BASICNODE'}">
            <c:set var="role" value="basicnode"/>
            <c:set var="isGlobalSearchMode" value="${requestScope.isGlobalSearch}"/>
        </c:when>
    </c:choose>

    <c:set var="isBasicnode" value="false"/>
    <c:if test="${thisNodeRoleBean.value == 'BASICNODE'}">
        <c:set var="isBasicnode" value="true"/>
    </c:if>

    <c:set var="searchRange" value="${resourceSearchContext.searchRange}"/>
    <c:if test="${empty searchRange}"><c:set var="searchRange" value="global"/></c:if>
    <c:set var="queryType" value="${resourceSearchContext.queryType}"/>
    <c:if test="${empty queryType}"><c:set var="queryType" value="title"/></c:if>
    <c:set var="resourceQuery" value="${resourceSearchContext.query}"/>

    <jsp:include page="resourceSearch.jsp">
        <jsp:param name="searchRange" value="${searchRange}"/>
        <jsp:param name="queryType" value="${queryType}"/>
        <jsp:param name="resourceQuery" value="${resourceQuery}"/>
        <jsp:param name="isBasicnode" value="${isBasicnode}"/>
    </jsp:include>

    <div id="resourcesPage" class="lower" align="center">

        <c:if test="${resourcePageBean.resourcePage ne null}">
            <c:set var="resourceList" value="${resourcePageBean.resourcePage.resultList}"/>
            <c:choose>
                <c:when test="${fn:length(resourceList) > 0}">
                    <table rules="rows">
                        <tr class="highTr">
                            <th>Id</th>
                            <th>Title</th>
                            <th>Owner</th>
                            <th>Status</th>
                            <th colspan="2">Action</th>
                        </tr>

                        <c:forEach var="art" items="${resourceList}">
                            <tr class="highTr">
                                <c:set var="resId" value="${art._id}"/>
                                <td><c:out value="${resId}"/>&nbsp;&nbsp;

                                </td>
                                <td><c:out value="${art.title}"/>&nbsp;&nbsp;</td>
                                <td><c:out value="${art.ownerNodeGUID}"/>&nbsp;&nbsp;</td>
                                <td><c:out value="${art.status}"/>&nbsp;&nbsp;</td>
                                <td>&nbsp;
                                    <form action="ResourceViewer" method="post">
                                        <input type="hidden" id="searchMode" name="searchMode" value="<c:out value="${isGlobalSearchMode}"/>"/>
                                        <input type="hidden" id="artIdShow" name="artId" value="<c:out value="${resId}"/>"/>
                                        <input type="submit" id="showResourceDetails" name="showResourceDetails" value="Details">
                                    </form>
                                <c:choose>
                                    <c:when test="${art.status eq 'REMOVED'}">
                                        <td></td>
                                    </c:when>
                                    <c:when test="${thisNodeRoleBean.value eq 'BASICNODE'}">
                                        <td>&nbsp;
                                            <form action="MessageSender" method="post">
                                                <input type="hidden" id="_id" name="_id" value="<c:out value="${resId}"/>"/>
                                                <input type="hidden" id="articleTitle" name="articleTitle" value="<c:out value="${art.title}"/>"/>
                                                <input type="submit" id="invalidateResourceAskSubmit" name="invalidateResourceAskSubmit" value="Invalidate Ask">
                                            </form>
                                        </td>
                                    </c:when>
                                    <c:otherwise>
                                        <td>&nbsp;
                                            <form action="ResourceInvalidator" method="post">
                                                <input type="hidden" id="artIdInvalid" name="artId" value="<c:out value="${resId}"/>">
                                                <input type="submit" id="invalidateResourceSubmit" name="invalidateResourceSubmit"
                                                       value="Invalidate">
                                            </form>
                                        </td>
                                    </c:otherwise>
                                </c:choose>
                            </tr>
                        </c:forEach>
                    </table>
                </c:when>
                <c:otherwise>
                        <c:out value="No results for resources"/>
                </c:otherwise>
            </c:choose>
        </c:if>
    </div>
    <c:if test="${fn:length(resourceList) > 0}">

        <jsp:include page="paginNavigation.jsp">
            <jsp:param name="mode" value="${null}"/>
            <jsp:param name="searchQuery" value="${resourceSearchContext.query}"/>
            <jsp:param name="pageNumber" value="${resourcePageBean.resourcePage.pageNumber}"/>
            <jsp:param name="hasPrevious" value="${resourcePageBean.resourcePage.hasPrevious}"/>
            <jsp:param name="hasNext" value="${resourcePageBean.resourcePage.hasNext}"/>
            <jsp:param name="resultFrom" value="${resourcePageBean.resourcePage.resultFrom}"/>
            <jsp:param name="resultTo" value="${resourcePageBean.resourcePage.resultTo}"/>
            <jsp:param name="totalResults" value="${resourcePageBean.resourcePage.totalResults}"/>
            <jsp:param name="pagesToIgnore" value="${requestScope.pagesToIgnore}"/>
            <jsp:param name="pagination" value="ResourcePagination"/>
        </jsp:include>
    </c:if>
</c:if>

<%@include file="foot.jsp" %>
