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
<jsp:setProperty name="operatorContact" property="*"/>

<c:if test="${not empty loggedOperatorBean.operator.operatorId}">

    <div id="operatorContactPage" class="lower">
        <p class="title">Operator Contact</p>

        <form method="post" action="LocalOperatorUpdaterServlet" onsubmit="return addNodeFormValidation();">
            <table rules="none" class="formTable">
                <c:choose>
                    <c:when test="${operatorContact.blocked}">
                        <c:set var="blockedState" value="BLOCKED"/>
                    </c:when>
                    <c:otherwise>
                        <c:set var="blockedState" value="PERMITTED"/>
                    </c:otherwise>
                </c:choose>

                <tr>
                    <td></td>
                    <td>
                      <c:out value="${blockedState}"/>
                    </td>
                </tr>


                <c:choose>
                    <c:when test="${requestScope.optionAttr eq 'global' and operatorContact.blocked}">
                        <tr>
                            <td></td>
                            <td><c:out value="Contact to blocked operator available only on their node."/></td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <tr>
                            <td style="text-align:right">Operator Id:&nbsp;</td>
                            <td><label><input type="text" value="<c:out value="${operatorContact.operatorContactId}"/>"
                                              size="<c:out value="${field_width}"/>" readonly="readonly"></label></td>
                        </tr>
                        <tr>
                            <td style="text-align:right">Context node:&nbsp;</td>
                            <td><label><input type="text" value="<c:out value="${operatorContact.contextNodeName}"/>"
                                              size="<c:out value="${field_width}"/>" readonly="readonly"></label></td>
                        </tr>
                        <tr>
                            <td style="text-align:right">E-mail:&nbsp;</td>
                            <td><label><input type="text" value="<c:out value="${operatorContact.email}"/>"
                                              size="<c:out value="${field_width}"/>" readonly="readonly"></label></td>
                        </tr>
                        <tr>
                            <td style="text-align:right">Full name:&nbsp;</td>
                            <td><label><input type="text" value="<c:out value="${operatorContact.fullName}"/>"
                                              size="<c:out value="${field_width}"/>" readonly="readonly"></label></td>
                        </tr>
                        <tr>
                            <td style="text-align:right">Telephone:&nbsp;</td>
                            <td><label><input type="text" value="<c:out value="${operatorContact.telephone}"/>"
                                              size="<c:out value="${field_width}"/>" readonly="readonly"></label></td>
                        </tr>
                        <tr>
                            <td style="text-align:right">Certificate:&nbsp;</td>
                            <td><label><input type="text" value="<c:out value="${operatorContact.certificate}"/>"
                                              size="<c:out value="${field_width}"/>" readonly="readonly"></label></td>
                        </tr>
                    </c:otherwise>
                </c:choose>
            </table>
        </form>
    </div>
</c:if>

<%@include file="foot.jsp" %>
