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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="messageRightAbsolute">
        <table>
            <tr>
                <td>
                    <c:url var="prevPageUrl" value="${param.pagination}">
                        <c:param name="searchQuery" value="${param.searchQuery}"/>
                        <c:param name="mode" value="${param.mode}"/>
                        <c:param name="jumpToPage" value="${param.pageNumber - 1}"/>
                    </c:url>
                    <c:url var="nextPageUrl" value="${param.pagination}">
                        <c:param name="searchQuery" value="${param.searchQuery}"/>
                        <c:param name="mode" value="${param.mode}"/>
                        <c:param name="jumpToPage" value="${param.pageNumber + 1}">
                        </c:param>
                    </c:url>
                    <c:set var="pageNumberVisible" value="${param.pageNumber - param.pagesToIgnore}"/>
                    <form action="${param.pagination}" method="post">
                        <c:choose>
                            <c:when test="${param.hasPrevious and pageNumberVisible ne 1}">
                                <a href="${prevPageUrl}"> << </a>
                            </c:when>
                            <c:otherwise> << </c:otherwise>
                        </c:choose>
                        <label for="pageNumber"></label>
                        <input class="rightText short" id="pageNumber" name="pageNumber" value="<c:out value="${pageNumberVisible}"/>"/>
                        <input type="hidden" id="mode" name="mode" value="<c:out value="${param.mode}"/>"/>
                        <c:choose>
                            <c:when test="${param.hasNext}">
                                <a href="${nextPageUrl}"> >> </a>
                            </c:when>
                            <c:otherwise> >> </c:otherwise>
                        </c:choose>
                    </form>
                </td>
                <td>
                    <form action="${param.pagination}" method="post">
                        <input type="hidden" id="resizePage" name="resizePage" value="true">
                        <input type="hidden" id="mode" name="mode" value="<c:out value="${param.mode}"/>"/>
                        <input type="hidden" id="jumpToPage" name="jumpToPage" value="1">
                        <input type="hidden" id="searchQuery" name="searchQuery" value="${param.searchQuery}">
                        &nbsp;&nbsp;&nbsp;
                        <c:if test="${empty param.searchQuery}">   <%--because these numbers are correct only without query--%>
                            <c:out value="Showing:  ${param.resultFrom} - ${param.resultTo} of total ${param.totalResults}."/>
                        </c:if>
                        <label for="rowsPerPage">&nbsp;&nbsp;&nbsp;Items per page:&nbsp;</label>
                        <select id="rowsPerPage" name="rowsPerPage" onchange="submit();" style="width: 5em">
                            <option value="3" <c:if test="${sessionScope.rowsPerPage eq '3'}">selected="selected"</c:if> >3</option>
                            <option value="10" <c:if test="${sessionScope.rowsPerPage eq '10'}">selected="selected"</c:if> >10</option>
                            <option value="25" <c:if test="${sessionScope.rowsPerPage eq '25'}">selected="selected"</c:if> >25</option>
                            <option value="50" <c:if test="${sessionScope.rowsPerPage eq '50'}">selected="selected"</c:if> >50</option>
                        </select>
                    </form>
                </td>
            </tr>
        </table>
    </div>