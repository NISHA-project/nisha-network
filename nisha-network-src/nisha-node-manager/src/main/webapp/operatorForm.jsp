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
<jsp:setProperty name="loggedOperatorBean" property="*"/>
<jsp:useBean id="operatorToShow" class="pl.nask.nisha.commons.node.Operator"/>
<jsp:setProperty name="operatorToShow" property="*"/>

<c:if test="${not empty loggedOperatorBean.operator.operatorId and thisNodeRoleBean.value ne null}">

    <c:if test="${param.showLoggedOperator ne null || requestScope.showLoggedOperator ne null}">
        <c:set var="showLoggedOperator" value="true"/>
        <c:set var="operatorToShow" value="${loggedOperatorBean.operator}"/>
    </c:if>

    <div id="operatorPage" class="lower">
        <p class="title">Operator</p>

        <form method="post" action="LocalOperatorUpdaterServlet" onsubmit="return operatorFormValidation(<c:out value="${showLoggedOperator}"/>);">
            <table rules="none" class="formTable">
                <c:choose>
                    <c:when test="${showLoggedOperator ne null}">
                        <tr>
                            <td class="rightText">Operator id:*&nbsp;</td>
                            <td class="valueTd"><c:out value="${operatorToShow.operatorId}"/>
                                <input class="inputLong" type="hidden" id="operatorIdLogged" name="operatorId"
                                       value="<c:out value="${operatorToShow.operatorId}"/>">
                            </td>
                        </tr>
                        <tr>
                            <td class="rightText">Password hashed:*&nbsp;</td>
                            <td  class="valueTd">****</td>
                        </tr>
                        <tr>
                            <c:set var="shortWidth" value="20%"/>
                            <td class="rightText"><label for="newPassword">New password:&nbsp;</label></td>
                            <td  class="valueTd">
                                <input class="inputLong" id="newPassword" name="newPassword" type="password"  value=""/>
                            </td>

                        </tr>
                        <tr>
                            <td class="rightText"><label for="newPassword2">New password:&nbsp;</label></td>
                            <td class="valueTd">
                                <input class="inputLong" id="newPassword2" name="newPassword2" type="password" value=""/>
                            </td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <tr>
                            <td class="rightText"><label for="operatorId">Operator Id:*&nbsp;</label>
                            </td>
                            <td class="valueTd">
                                <input class="inputLong" type="text" id="operatorId" name="operatorId"
                                       value="<c:out value="${operatorToShow.operatorId}"/>" >
                            </td>
                        </tr>
                        <tr>
                            <td class="rightText"><label for="operatorPassword">Password:*&nbsp;</label></td>
                            <td class="valueTd">
                                <input class="inputLong" id="operatorPassword" name="operatorPassword" type="password">
                            </td>
                        </tr>
                    </c:otherwise>
                </c:choose>
                <tr>
                    <td class="rightText"><label for="email">E-mail:*&nbsp;</label></td>
                    <td class="valueTd">
                        <input class="inputLong" id="email" name="email" value="<c:out value="${operatorToShow.email}"/>" />
                    </td>
                </tr>
                <tr>
                    <td class="rightText"><label for="fullName">Full name:&nbsp;</label></td>
                    <td class="valueTd">
                        <input class="inputLong" id="fullName" name="fullName" value="<c:out value="${operatorToShow.fullName}"/>"/>
                    </td>
                </tr>
                <tr>
                    <td class="rightText"><label for="telephone">Telephone:&nbsp;</label></td>
                    <td class="valueTd">
                        <input class="inputLong" id="telephone" name="telephone" value="<c:out value="${operatorToShow.telephone}"/>"/>
                    </td>
                </tr>
                <tr>
                    <td class="rightText"><label for="certificate">Certificate:&nbsp;</label></td>
                    <td class="valueTd">
                        <input class="inputLong" id="certificate" name="certificate"
                               value="<c:out value="${operatorToShow.certificate}"/>"/>
                    </td>
                </tr>
                <tr>
                    <td class="rightText"><label for="privateKey">Private key:&nbsp;</label></td>
                    <td class="valueTd">
                        <input class="inputLong" id="privateKey" name="privateKey" value="<c:out value="${operatorToShow.privateKey}"/>"/>
                    </td>
                </tr>
                <tr>
                    <td></td>
                    <td>
                        <input type="hidden" name="_id" value="<c:out value="${operatorToShow._id}"/>">

                        <c:choose>
                            <c:when test="${requestScope.defineOperator eq 'defineOperator'}">
                                <input type="submit" name="addOperator" value="Add new operator">
                            </c:when>
                            <c:otherwise>
                                <input type="submit" name="updateOperatorSubmit" value="Update operator">
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</c:if>


<%@include file="foot.jsp" %>
