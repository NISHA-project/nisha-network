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
<jsp:setProperty name="thisNodeRoleBean" property="*"/>

<c:set var="contextRole" value="${sessionScope.thisNodeRoleBean.value}"/>
<c:set var="fileLoggerState" value="${sessionScope.fileLogging}"/>

<c:choose>
    <c:when test="${fileLoggerState == null or empty fileLoggerState or fileLoggerState ne 'enabled'}">
        <p align="center"><a href="<c:url value="index.jsp"/>">Cannot log to file. Return to home page</a></p>
    </c:when>
    <c:otherwise>
        <c:choose>
            <c:when test="${contextRole == null or empty contextRole}">
                <jsp:setProperty name="thisNodeRoleBean" property="value" value="UNDEFINED"/>
                <c:set var="contextRole" value="UNDEFINED"/>
            </c:when>
            <c:when test="${contextRole eq 'UNDEFINED'}">
                <jsp:setProperty name="thisNodeRoleBean" property="*"/>
            </c:when>
        </c:choose>

        <c:choose>
            <c:when test="${param.option eq 'update' or requestScope.optionAttr eq 'update' or param.option eq 'blockOperator' or param.option eq 'unblockOperator'}">
                <c:set var="optionName" value="update"/>
                <div id="updateLocalConfigPage">
                    <span class="spanLeftAbsolute">
                        <span class="spanRightAbsolute"><c:out value="${sessionScope.thisNodeRoleBean.value}"/></span>
                        <c:url var="redirectUrl" value="LogoRedirectorServlet">
                            <c:param name="logoRedirect" value="goToMenu"/>
                        </c:url>
                        <img src="img/nisha-logo-small.png" alt="nisha" style="cursor: pointer"
                             onclick="location.href='${redirectUrl}'">
                    </span>
                    <span class="spanRightAbsolute">
                        <c:url var="redirectUrl" value="LogoRedirectorServlet">
                            <c:param name="logoRedirect" value="goToMenu"/>
                        </c:url>
                        <a href="${redirectUrl}">Menu</a>&nbsp;&nbsp;Logged as <c:out value="${loggedOperatorBean.operator.operatorId}"/>
                        <c:url var="logoutUrl" value="Authenticator">
                            <c:param name="option" value="logout"/>
                        </c:url>
                        <a href="${logoutUrl}">Log me out!</a>
                    </span>
                </div>
            </c:when>
            <c:when test="${requestScope.optionAttr eq 'configure' or param.option eq 'configure'}">
                <c:set var="optionName" value="configure"/>
                <div id="firstLocalConfigPage">
                    <span class="spanLeftAbsolute">
                        <span class="spanRightAbsolute"><c:out value="${sessionScope.thisNodeRoleBean.value}"/></span>
                        <c:url var="homeUrl" value="LogoRedirectorServlet">
                            <c:param name="logoRedirect" value="goToIndex"/>
                        </c:url>
                        <img src="img/nisha-logo-small.png" alt="nisha" style="cursor: pointer"
                             onclick="location.href='${homeUrl}'">
                    </span>
                    <span class="spanRightAbsolute"><a href="<c:url value="index.jsp"/>">Home</a>&nbsp;
                                                    <a href="<c:url value="login.jsp"/>">Log in</a>
                    </span>
                </div>
            </c:when>
        </c:choose>

        <p class="title">Local configuration </p>

        <c:choose>
            <c:when test="${empty loggedOperatorBean.operator.operatorId and optionName eq 'update'} ">
                <p align="center"><a href="<c:url value="login.jsp"/>">No operator logged - Log in please!</a></p>
            </c:when>
            <c:otherwise>
                <div class="lower">
                    <c:set var="message" value="${requestScope['message']}"/>
                    <c:if test="${not empty message}">
                        <div align="center"><c:out value="${message}"/></div>
                    </c:if>
                    <hr/>
                    <form method="post" action="LocalConfigUpdaterServlet?option=<c:out value="${optionName}"/>"
                            onsubmit="return localConfigValidation();"
                            >
                        <table rules="groups" class="formTable">
                            <c:if test="${optionName == 'update'}">
                                <tr>
                                    <td style="text-align:right"><label for="role">Role:&nbsp;</label></td>
                                    <td>
                                        <input type="hidden" name="_id" value="<c:out value="${localNodeConfiguration._id}"/>">
                                        <input id="role" name="role" type="text" readonly="readonly"
                                               size="<c:out value="${field_width}"/>"
                                               value="<c:out value="${sessionScope.thisNodeRoleBean.value}"/>">
                                    </td>
                                    <td colspan="2"></td>
                                </tr>
                            </c:if>

                            <c:set var="readonlyAttributeIfNeeded" value=""/>
                            <c:if test="${not empty localNodeConfiguration.nodeDomainNameFromConfig}">
                                <c:set var="readonlyAttributeIfNeeded" value="readonly=\"readonly\""/>
                            </c:if>
                            <tr>
                                <td style="text-align:right"><label for="nodeDomainNameFromConfig">Node Domain Name:&nbsp;</label></td>
                                <td><input id="nodeDomainNameFromConfig" name="nodeDomainNameFromConfig" type="text" size="<c:out value="${field_width}"/>"
                                           value="<c:out value="${localNodeConfiguration.nodeDomainNameFromConfig}"/>"
                                           <c:out value="${readonlyAttributeIfNeeded}"/><br/>
                                </td>
                                <td colspan="2"></td>
                            </tr>
                            <tr>
                                <c:if test="${not empty localNodeConfiguration.portNumberFromConfig}">
                                    <c:set var="portValue" value="${localNodeConfiguration.portNumberFromConfig}"/>
                                </c:if>
                                <td style="text-align:right"><label for="portNumberFromConfig">Port:&nbsp;</label></td>
                                <td><input id="portNumberFromConfig" name="portNumberFromConfig" type="text" size="<c:out value="${field_width}"/>"
                                           value="<c:out value="${portValue}"/>" /><br/>
                                </td>
                                <td colspan="2"></td>
                            </tr>
                            <tr>
                                <td style="text-align:right"><label for="description">Description:&nbsp;</label></td>
                                <td><textarea class="textAreaInForm" id="description" name="description"
                                        cols="" rows="4"><c:out value="${localNodeConfiguration.description}"/></textarea><br/>
                                </td>
                                <td colspan="2"></td>
                            </tr>

                            <tr>
                                <td style="text-align:right"><label for="location">Location:&nbsp;</label></td>
                                <td><input id="location" name="location" type="text" size="<c:out value="${field_width}"/>"
                                           value="<c:out value="${localNodeConfiguration.location}"/>"/><br/>
                                </td>
                                <td colspan="2"></td>
                            </tr>
                            <c:set var="first" value="true"/>
                            <c:forEach var="opId" items="${localNodeConfiguration.operatorIdListPermitted}">
                                <label for="<c:out value="${opId}"/>"></label>
                                <tr>
                                    <td style="text-align:right">
                                        <c:if test="${first eq 'true'}">
                                            <c:set var="first" value="false"/>
                                            Operators:&nbsp;
                                        </c:if>
                                    </td>
                                    <td>
                                        <label for="<c:out value="${opId}"/>"></label>
                                        <input id="<c:out value="${opId}"/>" class="operatorClass"
                                               name="_oper<c:out value="${opId}"/>" value="<c:out value="${opId}"/>" readonly="true"
                                               size="<c:out value="${field_width}"/>"/>
                                    </td>
                                    <td>
                                        <c:url var="contactUrl" value="OperatorContactViewer">
                                            <c:param name="option" value="local"/>
                                            <c:param name="operatorContactId" value="${opId}"/>
                                        </c:url>
                                        <a href="${contactUrl}">Contact&nbsp;</a>
                                    </td>
                                    <c:if test="${loggedOperatorBean ne null and loggedOperatorBean.operator.operatorId ne opId}">
                                        <td>
                                            <c:url var="blockUrl" value="LocalConfigUpdaterServlet">
                                                <c:param name="option" value="blockOperator"/>
                                                <c:param name="blockOperatorId" value="${opId}"/>
                                                <c:param name="nodeDomainNameFromConfig" value="${localNodeConfiguration.nodeDomainNameFromConfig}"/>
                                            </c:url>
                                            <a href="${blockUrl}">Block</a>
                                        </td>
                                    </c:if>
                                </tr>
                            </c:forEach>

                            <c:set var="first" value="true"/>
                            <c:forEach var="opId" items="${localNodeConfiguration.operatorIdListBlocked}"><label
                                    for="<c:out value="${opId}"/>"></label>
                                <c:if test="${first eq 'true'}">
                                    <tr>
                                        <td colspan="4">
                                            <hr/>
                                    </tr>
                                </c:if>
                                <tr>
                                    <td style="text-align:right">
                                        <c:if test="${first eq 'true'}">
                                            <c:set var="first" value="false"/>
                                            Blocked Operators:&nbsp;
                                        </c:if>
                                    </td>
                                    <td>
                                        <label for="<c:out value="${opId}"/>"></label>
                                        <input id="<c:out value="${opId}"/>" class="operatorClass"
                                               name="<c:out value="_blockoper${opId}"/>" value="<c:out value="${opId}"/>" readonly="true"
                                               size="<c:out value="${field_width}"/>"/>
                                    </td>
                                    <td>
                                        <c:url var="contactUrl" value="OperatorContactViewer">
                                            <c:param name="option" value="local"/>
                                            <c:param name="operatorContactId" value="${opId}"/>
                                        </c:url>
                                        <a href="${contactUrl}">Contact&nbsp;</a>
                                    </td>
                                    <c:if test="${loggedOperatorBean ne null and loggedOperatorBean.operator.operatorId ne opId}">
                                        <td>
                                            <c:url var="unblockUrl" value="LocalConfigUpdaterServlet">
                                                <c:param name="option" value="unblockOperator"/>
                                                <c:param name="nodeDomainNameFromConfig" value="${localNodeConfiguration.nodeDomainNameFromConfig}"/>
                                                <c:param name="blockOperatorId" value="${opId}"/>
                                            </c:url>
                                            <a href="${unblockUrl}">Unblock</a>
                                        </td>
                                    </c:if>
                                </tr>
                            </c:forEach>

                            <%--------------------------------------------------------------------------------------%>

                            <c:if test="${optionName eq 'configure'}">
                                <tr id="firstConfigElt" class="highTr">
                                    <td colspan="4"></td>
                                </tr>
                                <tr>
                                    <td style="text-align:right"><label for="operatorId">Operator Id:&nbsp;</label></td>
                                    <td><input id="operatorId" name="operatorId" type="text"
                                               size="<c:out value="${field_width}"/>" value="<c:out value="${requestScope.operatorId}"/>"></td>
                                    <td colspan="2"></td>
                                </tr>
                                <tr>
                                    <td style="text-align:right"><label for="operatorPassword">Password:&nbsp;</label></td>
                                    <td><input id="operatorPassword" name="operatorPassword" type="password"
                                               value="${requestScope.operatorPassword}"
                                               size="<c:out value="${field_width}"/>"></td>
                                    <td colspan="2"></td>
                                </tr>
                                <tr>
                                    <td style="text-align:right"><label for="email">Email:&nbsp;</label></td>
                                    <td><input id="email" name="email" type="text" value="${requestScope.email}"
                                               size="<c:out value="${field_width}"/>"></td>
                                    <td colspan="2"></td>
                                </tr>
                            </c:if>

                            <c:set var="buttonWidth" value="50%"/>
                            <tr class="highTr">
                                <td></td>
                                <td><input type="submit" id="saveConfigSubmit" name="saveConfigSubmit" value="Save configuration"
                                        style="width: ${buttonWidth}"/>

                                </td>
                                <td colspan="2"></td>
                            </tr>

                            <c:if test="${optionName ne 'configure'}">
                                <tr class="highTr">
                                    <td></td>
                                    <td>
                                        <input type="submit" id="defineOperator" name="defineOperator" value="Define new operator"
                                                   style="width: ${buttonWidth}"/>
                                    </td>
                                    <td colspan="3"></td>
                                </tr>
                            </c:if>
                        </table>
                    </form>
                </div>
            </c:otherwise>
        </c:choose>
    </c:otherwise>
</c:choose>

<%@include file="foot.jsp" %>
