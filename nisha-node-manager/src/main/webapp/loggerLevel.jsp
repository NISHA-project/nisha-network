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

<%--variables pageName and narrow defined outside in surrounding page--%>

<form action="LoggerUpdaterServlet" method="post">
    <c:set var="loggerLevel" value="${sessionScope.loggerLevel}"/>
    <c:set var="infoChecked" value="${empty loggerLevel or loggerLevel eq 'INFO'}"/>
    <label><input type="radio" name="loggerLevel" value="debug" <c:if test="${infoChecked eq 'false'}">checked="checked"</c:if>> debug</label>
    <label><input type="radio" name="loggerLevel" value="info" <c:if test="${infoChecked eq 'true'}">checked="<c:out value="${infoChecked}"/>"</c:if>> info</label>
    <input type="hidden" id="sourcePage" name="sourcePage" value="<c:out value="${pageName}"/>"/>
    <c:if test="${narrow}"><br/></c:if>
    <input type="submit" id="loggerLevelSubmit" name="loggerLevelSubmit" value="Apply logger level">
</form>