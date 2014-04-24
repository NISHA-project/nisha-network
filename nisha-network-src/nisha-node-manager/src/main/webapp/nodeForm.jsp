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

<c:set var="titlepart" value=""/>
<c:set var="submittext" value=""/>
<c:set var="optionName" value=""/>

<c:choose>
    <c:when test="${param.option eq 'update'}">
        <c:set var="titlepart" value="Update"/>
        <c:set var="submittext" value="Update node"/>
        <c:set var="optionName" value="update"/>
    </c:when>
    <c:when test="${requestScope.optionAttr eq 'update'}">
        <c:set var="titlepart" value="Update"/>
        <c:set var="submittext" value="Update node"/>
        <c:set var="optionName" value="update"/>
    </c:when>
    <c:otherwise>
        <c:set var="titlepart" value="Add"/>
        <c:set var="submittext" value="Add node"/>
        <c:set var="optionName" value="add"/>
    </c:otherwise>
</c:choose>

<c:choose>
    <c:when test="${sessionScope.thisNodeRoleBean.value ne 'SUPERNODE' and optionName eq 'add'}">
        <div align="center"><c:out value="Add node form is available only for supernodes."/></div>
    </c:when>
    <c:otherwise>
        <c:if test="${not empty loggedOperatorBean.operator.operatorId}">

            <c:set var="portValue" value=""/>
            <c:if test="${not empty requestScope.nodeRingInfo.portNumberFromRingInfo}">
                <c:set var="portValue" value="${requestScope.nodeRingInfo.portNumberFromRingInfo}"/>
            </c:if>

            <c:set var="stateReasonValue" value="[required]"/>
            <c:if test="${not empty requestScope.nodeRingInfo.stateReason}">
                <c:set var="stateReasonValue" value="${requestScope.nodeRingInfo.stateReason}"/>
            </c:if>

            <div id="" class="lower">
                <p class="title"><c:out value="${titlepart}"/>&nbsp;node</p>

                <form method="post" action="NodeUpdater" onsubmit="return addNodeFormValidation();">

                    <table class="formTable">
                        <tr>
                            <td style="text-align:right"><label for="nodeDomainNameFromRingInfo">Host:&nbsp;</label></td>
                            <td><input id="nodeDomainNameFromRingInfo" name="nodeDomainNameFromRingInfo" type="text" size="<c:out value="${field_width}"/>"
                                       value="<c:out value="${requestScope.nodeRingInfo.nodeDomainNameFromRingInfo}"/>"
                                       <c:if test="${optionName eq 'update'}">readonly="readonly"</c:if> /><br/>
                            </td>
                        </tr>
                        <tr>
                            <td style="text-align:right"><label for="portNumberFromRingInfo">Port:&nbsp;</label></td>
                            <td><input id="portNumberFromRingInfo" name="portNumberFromRingInfo" type="text" size="<c:out value="${field_width}"/>"
                                       <c:if test="${requestScope.nodeRingInfo.state eq 'REMOVED'}">readonly="readonly" </c:if>
                                       value="<c:out value="${portValue}"/>"/><br/>
                            </td>
                        </tr>
                        <tr>
                            <td style="text-align:right"><label for="role">Role:&nbsp;</label></td>
                            <td>
                                <c:choose>
                                    <c:when test="${optionName eq 'add'}">
                                        <select id="role" name="role">
                                            <c:forEach var="roleTmp" items="${nodeRolesBean.nodeRoles}">
                                                <c:if test="${roleTmp ne 'UNDEFINED'}">
                                                    <option value="<c:out value="${roleTmp}"/>"
                                                            <c:if test="${roleTmp eq 'BASICNODE'}">selected="selected" </c:if> >
                                                        <c:out value="${roleTmp}"/>&nbsp;&nbsp;&nbsp;
                                                    </option>
                                                </c:if>
                                            </c:forEach>
                                        </select>
                                    </c:when>
                                    <c:when test="${optionName eq 'update'}">
                                        <input id="role" name="role" type="text" size="<c:out value="${field_width}"/>"
                                                    readonly="readonly" value="<c:out value="${requestScope.nodeRingInfo.role}"/>"/>
                                    </c:when>
                                </c:choose>
                            </td>
                        </tr>
                        <tr>
                            <td style="text-align:right"><label for="state">State:&nbsp;</label></td>
                            <td>
                                <c:choose>
                                <c:when test="${requestScope.nodeRingInfo.state eq 'REMOVED'}">
                                    <label><input id="stateInput" type="text" readonly="readonly"
                                                  value="<c:out value="${requestScope.nodeRingInfo.state}"/>"
                                                  size="<c:out value="${field_width}"/>" /></label>
                                </c:when>
                                    <c:otherwise>
                                        <select id="state" name="state">
                                        <c:forEach var="stateTmp" items="${nodeStatesBean.nodeStates}">
                                            <c:choose>
                                                <c:when test="${thisNodeRoleBean.value eq 'BASICNODE' and (stateTmp eq 'REMOVED' or stateTmp eq 'BLOCKED')}">
                                                </c:when>
                                                <c:otherwise>
                                                    <option value="<c:out value="${stateTmp}"/>"
                                                        <c:if test="${stateTmp eq requestScope.nodeRingInfo.state}">selected="selected" </c:if> >
                                                        <c:out value="${stateTmp}"/>&nbsp;&nbsp;&nbsp;
                                                    </option>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:forEach>
                                        </select>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                        <tr>
                            <td style="text-align:right"><label for="stateReason">State reason: </label></td>
                            <td><textarea class="textAreaInForm" id="stateReason" name="stateReason" cols="" onclick="return hideDefaultText();" onblur="return restoreDefaultText()"
                                        <c:if test="${requestScope.nodeRingInfo.state eq 'REMOVED'}">readonly="readonly" </c:if>
                                        rows="4"><c:out value="${stateReasonValue}"/></textarea>
                                <input type="hidden" id="prevStateReason" name="prevStateReason"
                                       value="<c:out value="${stateReasonValue}"/>" readonly="readonly">
                            </td>
                        </tr>
                        <c:if test="${requestScope.nodeRingInfo.state ne 'REMOVED'}">
                            <tr>
                                <td>
                                    <c:if test="${optionName eq 'update'}">
                                        <input type="hidden" name="_id" value="<c:out value="${requestScope.nodeRingInfo._id}"/>"/>
                                        <input type="hidden" name="option" value="<c:out value="${optionName}"/>">
                                    </c:if>
                                </td>
                                <td><input name="<c:out value="${optionName}Submit"/>" type="submit" value="<c:out value="${submittext}"/>"/></td>
                            </tr>
                        </c:if>
                    </table>
                </form>
            </div>
        </c:if>
</c:otherwise>
</c:choose>
<%@include file="foot.jsp" %>
