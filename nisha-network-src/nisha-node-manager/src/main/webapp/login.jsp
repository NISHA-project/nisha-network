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
<div align="right"><a href="<c:url value="index.jsp"/>">Home</a></div>
<div align="center">
    <c:url var="homeUrl" value="LogoRedirectorServlet">
        <c:param name="logoRedirect" value="goToIndex"/>
    </c:url>
    <img src="img/nisha-logo.png" alt="nisha" style="cursor: pointer"
                         onclick="location.href='${homeUrl}'"></div>

<div id="loginPage">
    <form name="loginForm" method="post" action="Authenticator" onsubmit="return loginValidation();">
        <table align="center">
            <tr>
                <td colspan="2"><p class="title">NISHA Manager</p></td>
            </tr>
            <tr>
                <td style="text-align:right"><label for="operatorId">Operator Id:&nbsp;</label></td>
                <td><input id="operatorId" name="operatorId" type="text" size="20" value=""/><br/></td>
            </tr>
            <tr>
                <td style="text-align:right"><label for="operatorPassword">Password:&nbsp;</label></td>
                <td><input id="operatorPassword" name="operatorPassword" type="password" size="20" value=""/><br/></td>
            </tr>
            <tr>
                <td></td>
                <td><input type="submit" id="loginSubmit" name="loginSubmit" value="Log me in"></td>
            </tr>

            <c:set var="message" value="${requestScope['message']}"/>
            <c:if test="${not empty message}">
                <tr>
                    <td colspan="2" align="center"><c:out value="${message}"/></td>
                </tr>
            </c:if>
        </table>
    </form>
</div>
<%@include file="foot.jsp" %>
