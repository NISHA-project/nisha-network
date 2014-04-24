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

<%@include file="head.jsp"%>
<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%--<%@page errorPage="errorPage.jsp" %>--%>

<jsp:useBean id="operator" class="pl.nask.nisha.commons.node.Operator" scope="session">
    <jsp:setProperty name="operator" property="*"/>
</jsp:useBean>

<jsp:useBean id="loggedOperatorBean" class="pl.nask.nisha.manager.model.transfer.supportbeans.OperatorBean" scope="session">
    <jsp:setProperty name="loggedOperatorBean" property="*"/>
</jsp:useBean>

<jsp:useBean id="network" class="pl.nask.nisha.manager.model.domain.network.NetworkRingInfo" scope="session">
    <jsp:setProperty name="network" property="*"/>
</jsp:useBean>

<%--<jsp:useBean id="alertCollection" class="pl.nask.nisha.manager.model.transfer.supportbeans.AlertCollection" scope="session">--%>
    <%--<jsp:setProperty name="alertCollection" property="*"/>--%>
<%--</jsp:useBean>--%>

<jsp:useBean id="operatorContact" class="pl.nask.nisha.manager.model.domain.network.OperatorContact" scope="session">
    <jsp:setProperty name="operatorContact" property="*"/>
</jsp:useBean>

<jsp:useBean id="nodeRolesBean" class="pl.nask.nisha.manager.model.transfer.supportbeans.NodeRolesBean" scope="session">
    <jsp:setProperty name="nodeRolesBean" property="*"/>
</jsp:useBean>

<jsp:useBean id="nodeStatesBean" class="pl.nask.nisha.manager.model.transfer.supportbeans.NodeStatesBean" scope="session">
    <jsp:setProperty name="nodeStatesBean" property="*"/>
</jsp:useBean>

<jsp:useBean id="messageTypesBean" class="pl.nask.nisha.manager.model.transfer.supportbeans.MessageTypesBean" scope="session">
    <jsp:setProperty name="messageTypesBean" property="*"/>
</jsp:useBean>

<jsp:useBean id="localNodeConfiguration" class="pl.nask.nisha.manager.model.domain.local.NodeConfiguration" scope="session">
    <jsp:setProperty name="localNodeConfiguration" property="*"/>
</jsp:useBean>

<jsp:useBean id="article" class="pl.nask.nisha.manager.model.domain.resources.Article" scope="session">
    <jsp:setProperty name="article" property="*"/>
</jsp:useBean>

<jsp:useBean id="messagePageBean" class="pl.nask.nisha.manager.model.transfer.supportbeans.MessagePageBean" scope="request">
    <jsp:setProperty name="messagePageBean" property="*"/>
</jsp:useBean>

<jsp:useBean id="messageDisplayModeBean" class="pl.nask.nisha.manager.model.transfer.supportbeans.ValueBean" scope="session">
    <jsp:setProperty name="messageDisplayModeBean" property="*"/>
</jsp:useBean>

<jsp:useBean id="messageToShow" class="pl.nask.nisha.manager.model.domain.messages.Message" scope="request">
    <jsp:setProperty name="messageToShow" property="*"/>
</jsp:useBean>

<jsp:useBean id="messageStateBean" class="pl.nask.nisha.manager.model.transfer.supportbeans.MessageStateBean" scope="session">
    <jsp:setProperty name="messageToShow" property="*"/>
</jsp:useBean>

<jsp:useBean id="messageCountInfoBean" class="pl.nask.nisha.manager.model.transfer.supportbeans.MessageCountInfoBean" scope="session">
    <jsp:setProperty name="messageCountInfoBean" property="*"/>
</jsp:useBean>

<jsp:useBean id="resourcePageBean" class="pl.nask.nisha.manager.model.transfer.supportbeans.ResourcePageBean" scope="request">
    <jsp:setProperty name="resourcePageBean" property="*"/>
</jsp:useBean>

<jsp:useBean id="alertPageBean" class="pl.nask.nisha.manager.model.transfer.supportbeans.AlertPageBean" scope="request">
    <jsp:setProperty name="alertPageBean" property="*"/>
</jsp:useBean>

<c:set var="field_width" value="50%"/>
<c:set var="field_width_100" value="100%"/>
