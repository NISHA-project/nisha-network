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

<div align="center"><img src="img/nisha-logo.png" alt="nisha" ></div>
<p class="title">Welcome to NISHA Manager</p>

<div id="indexPage" align="center">
    <c:set var="pageName" value="/index.jsp"/>   <%-- needed in loggerLevel.jsp--%>
    <c:set var="narrow" value="false"/>   <%-- needed in loggerLevel.jsp--%>
    <%@include file="loggerLevel.jsp" %>

    <c:url var="configUrl" value="AppStarterServlet">
            <c:param name="option" value="configure"/>
    </c:url>
    <c:url var="loginUrl" value="AppStarterServlet">
        <c:param name="option" value="login"/>
    </c:url>
    <a href="${configUrl}">Configure local data</a><br/><br/>
    <a href="${loginUrl}">Log in as operator</a><br/>
</div>

<c:set var="message" value="${requestScope['message']}"/>
<c:if test="${not empty message}">
    <br/>
    <div align="center"><c:out value="${message}"/></div>
</c:if>

<%@include file="foot.jsp" %>
