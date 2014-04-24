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

<jsp:useBean id="nodeRingInfoRichCollection" class="pl.nask.nisha.manager.model.transfer.supportbeans.NodeRingInfoRichCollection" scope="session">
    <jsp:setProperty name="nodeRingInfoRichCollection" property="*"/>
</jsp:useBean>

<jsp:useBean id="searchAcceptedStates" class="java.util.HashSet" scope="request">
    <jsp:setProperty name="searchAcceptedStates" property="*"/>
</jsp:useBean>

<jsp:setProperty name="localNodeConfiguration" property="*"/>
<jsp:setProperty name="network" property="*"/>

<c:set var="BASICNODE" value="BASICNODE"/>
<c:set var="SUPERNODE" value="SUPERNODE"/>

<c:choose>
    <c:when test="${not empty loggedOperatorBean.operator.operatorId}">

        <div id="nodeSearchPage" class="lower" align="center">

            <form action="NodeSearch" method="post">
                <table style="width: 60%;">
                    <tr>
                        <td style="text-align: right"><label for="query">Find node:&nbsp;</label></td>
                        <td>
                            <input name="query" id="query" type="text" style="width: 100%;" value="<c:out value="${requestScope.query}"/>"/>
                        </td>
                        <td><input name="querysubmitName" type="submit" value="Name Search">
                        <td>
                    </tr>
                    <tr>
                        <td style="text-align: right">State filter?</td>
                        <td>
                            <label><input type="checkbox" id="critAct" name="critAct" value="active"
                                    <%-- because 'active' is substring of 'inactive' --%>
                                    <c:forEach var="stateName" items="${searchAcceptedStates}">
                                        <c:if test="${stateName eq 'ACTIVE'}">checked="checked"</c:if>
                                    </c:forEach>
                                    />active</label>
                            <label><input type="checkbox" id="critInact" name="critInact" value="inactive"
                                    <c:if test="${fn:contains(searchAcceptedStates, 'INACTIVE')}">checked="checked"</c:if>
                                    />inactive</label>
                            <c:if test="${thisNodeRoleBean.value eq SUPERNODE}">
                                <label><input type="checkbox" id="critBlk" name="critBlk" value="blocked"
                                    <c:if test="${fn:contains(searchAcceptedStates, 'BLOCKED')}">checked="checked"</c:if>
                                    />blocked</label>
                                <label><input type="checkbox" id="critRmv" name="critRmv" value="removed"
                                    <c:if test="${fn:contains(searchAcceptedStates, 'REMOVED')}">checked="checked"</c:if>
                                    />removed</label>
                            </c:if>
                        </td>
                        <td></td>
                    </tr>
                </table>
            </form>
            <hr/>
        </div>

        <c:if test="${requestScope.optionAttr == null or requestScope.optionAttr ne 'remove'}">
            <br/><div align="center">
                <c:out value="Network - nodes: ${fn:length(network.networkNodeNames)}, "/>
                <c:out value="last change: ${network.lastChangeDate} ${network.lastChangeType} ${network.lastChangeNode}"/>
            </div>
        </c:if>

        <c:if test="${requestScope.optionAttr ne null and requestScope.optionAttr eq 'listNodes'}">
            <div class="lower" align="center">
                <c:if test="${nodeRingInfoRichCollection ne null}">
                    <c:choose>
                        <c:when test="${fn:length(nodeRingInfoRichCollection.nodeRingInfoRichList) > 0}">
                            <table rules="rows">
                                <tr class="highTr">
                                    <th>Role</th>
                                    <th>Name</th>
                                    <th>Port</th>
                                    <th>State</th>
                                    <th>Alerts</th>
                                    <th colspan="3"></th>
                                </tr>
                                <c:forEach var="nodeRingInfoRich" items="${nodeRingInfoRichCollection.nodeRingInfoRichList}">
                                    <c:set var="nodeRingInfo" value="${nodeRingInfoRich.nodeRingInfo}"/>
                                    <tr class="highTr">
                                        <td><c:out value="${nodeRingInfo.role}"/>&nbsp;&nbsp;</td>
                                        <td><c:out value="${nodeRingInfo.nodeDomainNameFromRingInfo}"/>&nbsp;&nbsp;</td>
                                        <td><c:out value="${nodeRingInfo.portNumberFromRingInfo}"/>&nbsp;&nbsp;</td>
                                        <td><c:out value="${nodeRingInfo.state}"/>&nbsp;&nbsp;</td>
                                        <td><span style="color: #dc143c; ">
                                            <c:out value="${nodeRingInfoRich.hasAlerts}"/>&nbsp;&nbsp;</span></td>

                                        <c:choose>
                                            <c:when test="${thisNodeRoleBean.value eq SUPERNODE}">
                                                <c:choose>
                                                    <c:when test="${nodeRingInfo.state eq 'REMOVED'}">
                                                        <td></td>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <td>&nbsp;
                                                            <form action="NodeRemover" method="post">
                                                                <input type="hidden" id="_id" name="_id" value="<c:out value="${nodeRingInfo._id}"/>">
                                                                <input type="hidden" id="nodeDomainNameRemove" name="nodeDomainNameFromRingInfo"
                                                                       value="<c:out value="${nodeRingInfo.nodeDomainNameFromRingInfo}"/>"/>
                                                                <input type="submit" id="removeSubmit" name="removeSubmit" value="Remove"/>
                                                            </form>
                                                        </td>
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:when>
                                            <c:otherwise>
                                                <c:choose>
                                                    <c:when test="${nodeRingInfo.state eq 'REMOVED'}">
                                                        <td></td>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <td>&nbsp;
                                                            <form action="MessageSender" method="post">
                                                                <input type="hidden" id="_idBlock" name="_id" value="<c:out value="${nodeRingInfo._id}"/>"/>
                                                                <input type="hidden" id="nodeDomainNameBlock" name="nodeDomainNameFromRingInfo" value="<c:out value="${nodeRingInfo.nodeDomainNameFromRingInfo}"/>"/>
                                                                <input type="submit" id="blockNodeAskSubmit" name="blockNodeAskSubmit" value="Block Ask"/>
                                                            </form>
                                                        </td>
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:otherwise>
                                        </c:choose>
                                        <td>&nbsp;
                                            <form action="NodeDisplayer" method="post">
                                                <input type="hidden" id="nodeDomainNameFromRingInfo" name="nodeDomainNameFromRingInfo" value="<c:out value="${nodeRingInfo.nodeDomainNameFromRingInfo}"/>"/>
                                                <input type="submit" id="detailsSubmit" name="detailsSubmit" value="Details"/>
                                            </form>
                                        </td>
                                        <c:choose>
                                            <c:when test="${thisNodeRoleBean.value eq BASICNODE}">
                                                <c:choose>
                                                    <c:when test="${localNodeConfiguration.nodeDomainNameFromConfig eq nodeRingInfo.nodeDomainNameFromRingInfo}">
                                                        <td>&nbsp;
                                                            <form action="NodeUpdater" method="post">
                                                                <input type="hidden" id="nodeDomainNameUpdate_BN" name="nodeDomainNameFromRingInfo"
                                                                       value="<c:out value="${nodeRingInfo.nodeDomainNameFromRingInfo}"/>"/>
                                                                <input type="submit" id="update_bn" name="update" value="Update"/>
                                                            </form>
                                                        </td>
                                                    </c:when>
                                                </c:choose>
                                                <td></td>
                                            </c:when>
                                            <c:when test="${thisNodeRoleBean.value eq SUPERNODE}">
                                                <td>&nbsp;
                                                    <form action="NodeUpdater" method="post">
                                                        <input type="hidden" id="nodeDomainNameUpdate_SN" name="nodeDomainNameFromRingInfo" value="<c:out value="${nodeRingInfo.nodeDomainNameFromRingInfo}"/>"/>
                                                        <input type="submit" id="update_sn" name="update" value="Update"/>
                                                    </form>
                                                </td>
                                            </c:when>
                                        </c:choose>
                                    </tr>
                                </c:forEach>
                            </table>
                        </c:when>
                        <c:otherwise>
                            <c:if test="${requestScope.searchResult ne null}">
                                <c:out value="No results for NISHA nodes with query: \"${requestScope.query}\" [${requestScope.searchMode}]"/>
                            </c:if>
                        </c:otherwise>
                    </c:choose>
                </c:if>
            </div>
        </c:if>

        <c:if test="${requestScope.optionAttr ne null and requestScope.optionAttr eq 'remove'}">
            <c:choose>
                <c:when test="${thisNodeRoleBean.value eq SUPERNODE}">
                    <div align="center">
                        <c:if test="${param._id ne null and param.nodeDomainNameFromRingInfo ne null}">
                            <form action="NodeRemover" method="post">
                                <br/><c:out value="Remove node with uri: ${param.nodeDomainNameFromRingInfo}? Removal is permanent."/><br/><br/>
                                <input type="hidden" name="docidToRemove" value="<c:out value="${param._id}"/>"/>
                                <input type="submit" name="removeConfirm" value="Confirm">
                                <input type="submit" name="removeCancel" value="Cancel">
                            </form>
                        </c:if>
                    </div>
                </c:when>
                <c:otherwise>
                    <div align="center"><br/><c:out value="Only supernodes can remove nodes - this node is ${thisNodeRoleBean.value}"/></div>
                </c:otherwise>
            </c:choose>
        </c:if>
    </c:when>
</c:choose>

<%@include file="foot.jsp" %>
