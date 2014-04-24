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

<jsp:useBean id="nodeInfo" class="pl.nask.nisha.manager.model.domain.network.NodeInfo" scope="session">
    <jsp:setProperty name="nodeInfo" property="*"/>
</jsp:useBean>

<c:if test="${not empty loggedOperatorBean.operator.operatorId}">

    <div id="nodeDetailsPage" class="lower" align="center">
        <p class="title">Node's details&nbsp;</p>

            <table rules="none" class="formTable">
                <tr>
                    <td style="text-align:right"><label for="nodeDomainNameFromNodeInfo">Node URI:&nbsp;</label></td>
                    <td><input id="nodeDomainNameFromNodeInfo" name="nodeDomainNameFromNodeInfo" value="<c:out value="${nodeInfo.nodeDomainNameFromNodeInfo}"/>"
                               readonly="readonly" size="<c:out value="${field_width}"/>"/></td>
                    <td></td>
                </tr>
                <tr>
                    <td style="text-align:right"><label for="description">Description:&nbsp;</label></td>
                    <td><textarea class="textAreaInForm" id="description" name="description" cols="" rows="4"
                                  readonly="readonly"><c:out value="${nodeInfo.description}"/></textarea><br/>
                    </td>
                    <td></td>
                </tr>
                <tr>
                    <td style="text-align:right"><label for="location">Location:&nbsp;</label></td>
                    <td><input id="location" value="<c:out value="${nodeInfo.location}"/>"
                               readonly="readonly" size="<c:out value="${field_width}"/>"></td>
                    <td></td>
                </tr>
                <tr>
                    <td style="text-align:right"><label for="certificate">Certificate:&nbsp;</label></td>
                    <td><input id="certificate" value="<c:out value="${nodeInfo.certificate}"/>"
                               readonly="readonly" size="<c:out value="${field_width}"/>"></td>
                    <td></td>
                </tr>
                <c:set var="first" value="true"/>
                <c:forEach var="opId" items="${nodeInfo.operatorIdListPermitted}">
                    <tr>
                        <td style="text-align:right">
                            <c:if test="${first eq 'true'}">
                                <c:set var="first" value="false"/>
                                Operators:&nbsp;
                            </c:if>
                        </td>
                        <td>
                            <label><input value="<c:out value="${opId}"/>"
                                          readonly="readonly" size="<c:out value="${field_width}"/>"></label><br/>
                        </td>
                        <td>
                            <c:url var="contactUrl" value="OperatorContactViewer">
                                <c:param name="option" value="global"/>
                                <c:param name="nodeDomainNameFromNodeInfo" value="${nodeInfo.nodeDomainNameFromNodeInfo}"/>
                                <c:param name="operatorContactId" value="${opId}"/>
                            </c:url>
                            <a href="${contactUrl}">Contact</a>
                        </td>
                    </tr>
                </c:forEach>
            </table>
    </div>
</c:if>

<%@include file="foot.jsp" %>
