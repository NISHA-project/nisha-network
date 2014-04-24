/**
 * *****************************************************************************
 * Copyright (c) 2012 Research and Academic Computer Network. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the GNU Public License v2.0 which accompanies this
 * distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * With financial support from the Prevention, Preparedness and Consequence
 * Management of Terrorism and other Security Related Risks Programme European
 * Commission - Directorate-General Home Affairs
 *
 * Contributors: Research and Academic Computer Network
 *****************************************************************************
 */
package pl.nask.nisha.manager.controllers.network;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.nisha.commons.network.NodeRingInfo;
import pl.nask.nisha.commons.network.NodeRole;
import pl.nask.nisha.manager.controllers.app.NishaBasicServlet;
import pl.nask.nisha.manager.model.logic.app.LoggerUpdater;
import pl.nask.nisha.manager.model.logic.network.NodeSearch;
import pl.nask.nisha.manager.model.logic.network.NodeUpdater;
import pl.nask.nisha.manager.model.transfer.servletsupport.AttrParamValues;
import pl.nask.nisha.manager.model.transfer.servletsupport.Attrs;
import pl.nask.nisha.manager.model.transfer.servletsupport.PageJSP;
import pl.nask.nisha.manager.model.transfer.servletsupport.Params;
import pl.nask.nisha.manager.model.logic.security.Authorization;

public class NodeUpdateController extends NishaBasicServlet {

    private static final long serialVersionUID = 5812985093000690011L;
    public static final Logger LOG = LoggerFactory.getLogger(NodeUpdateController.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequestGet(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequestPost(request, response);
    }

    protected void processRequestGet(HttpServletRequest request, HttpServletResponse response)
                throws ServletException, IOException {
        String msg;
        String option = request.getParameter(Params.OPTION.val);
        try {
            request.setCharacterEncoding(UTF_8);
            LoggerUpdater.checkLoggerIsStarted();

            if (request.getParameter(Params.ADD_NODE.val) != null) {
                showAddNodeForm(request, response);
            }
            else if (request.getParameter(Params.BLOCK_ASK_REDIRECT.val) != null) {
                showUpdateFormForNode(request, response);
            }
            else {
                throw new IllegalStateException(AttrParamValues.UNKNOWN_ACTION.val);
            }
        } catch (Exception e) {
            msg = e.getMessage();
            LOG.warn("{}", msg);
            request.setAttribute(Params.OPTION.val, option);
            request.setAttribute(Attrs.MESSAGE.val, msg);
            forwardToJsp(PageJSP.MENU, request, response);
        }
    }

    protected void processRequestPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setCharacterEncoding(UTF_8);
            LoggerUpdater.checkLoggerIsStarted();

            if (request.getParameter(AttrParamValues.UPDATE.val) != null) {
                showUpdateFormForNode(request, response);
            }
            else if (request.getParameter(AttrParamValues.UPDATE_SUBMIT.val) != null) {
                processUpdateSubmit(request, response);
            }
            else if (request.getParameter(AttrParamValues.ADD_SUBMIT.val) != null) {
                processAddSubmit(request, response);
            } else {
                throw new IllegalStateException(AttrParamValues.UNKNOWN_ACTION.val);
            }
        } catch (Exception e) {
            String msg = e.getMessage();
            LOG.warn("{}", msg);
            request.setAttribute(Attrs.MESSAGE.val, msg);
            forwardToJsp(PageJSP.MENU, request, response);
        }
    }

    private void showAddNodeForm(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        boolean authorized = Authorization.hasLoggedOperatorAndAnyRole(request, NodeRole.SUPERNODE.name());
        LOG.info("authorization success? {} (add node display)", authorized);
        forwardToJsp(PageJSP.NODE_FORM, request, response);
    }

    private void showUpdateFormForNode(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        LOG.info("redirect to block: " + request.getParameter(Params.BLOCK_ASK_REDIRECT.val));
        NodeRingInfo toDispl = NodeSearch.findNodeRingInfoByNodeDomainName(request.getParameter(Params.NODE_DOMAIN_NAME_FROM_RING_INFO.val));
        if (toDispl == null) {
            throw new IllegalStateException(request.getParameter(Params.NODE_DOMAIN_NAME_FROM_RING_INFO.val) + " is null.");
        } else if (toDispl.getRole().equals(NodeRole.UNDEFINED.name())) {
            throw new IllegalStateException(toDispl.getNodeDomainNameFromRingInfo() + " role is " + toDispl.getRole() + " - cannot update node that is not in network.");
        } else {
            boolean  authorized = authorizeForUpdateAction(request, toDispl.getNodeDomainNameFromRingInfo());
            LOG.info("authorization success? {} (node update form)", authorized);

            request.setAttribute(Attrs.OPTION.val, AttrParamValues.UPDATE.val);
            request.setAttribute(Attrs.NODE_RING_INFO.val, toDispl);
            LOG.debug("display for update {}", toDispl.toStringVerbose());
            forwardToJsp(PageJSP.NODE_FORM, request, response);
        }
    }

    private void processAddSubmit(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        boolean success = Authorization.hasLoggedOperatorAndAnyRole(request, NodeRole.SUPERNODE.name());
        LOG.info("authorization success? {} (node adding)", success);
        LOG.debug("add node start...");
        NodeRingInfo nodeToAdd = loadNodeRingInfoPropsFromRequest(null, request);
        String prevStateReason = request.getParameter(Params.PREV_STATE_REASON.val);

        String validateMsg = NodeUpdater.validateNode(nodeToAdd, prevStateReason);
        if (!validateMsg.equals(AttrParamValues.OK.val)) {
            request.setAttribute(Attrs.NODE_RING_INFO.val, nodeToAdd);
            throw new IllegalArgumentException(validateMsg);
        }
        LOG.info("{} {} validated - success", nodeToAdd.getRole(), nodeToAdd.getNodeDomainNameFromRingInfo());

        String resultMessage = NodeUpdater.addNodeToDatabase(nodeToAdd);
        request.setAttribute(Attrs.MESSAGE.val, resultMessage);

        String msg = nodeToAdd.getNodeDomainNameFromRingInfo() + " - node  added - success";
        LOG.info("{}", nodeToAdd.toStringVerbose());
        LOG.info("{}", msg);
        request.setAttribute(Attrs.MESSAGE.val, msg);
        forwardToJsp(PageJSP.MENU, request, response);
    }


    private void processUpdateSubmit(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        LOG.debug("updateSubmit");
        NodeRingInfo toDispl = NodeSearch.findNodeRingInfoByNodeDomainName(request.getParameter(Params.NODE_DOMAIN_NAME_FROM_RING_INFO.val));
        if (toDispl == null){
            throw new IllegalStateException(request.getParameter(Params.NODE_DOMAIN_NAME_FROM_RING_INFO.val) + " is null.");
        }
        String nodeName = toDispl.getNodeDomainNameFromRingInfo();
        boolean authorized = authorizeForUpdateAction(request, nodeName);
        LOG.info("authorization success? {} (node update)", authorized);
        processNodeUpdate(request, response);
    }

    private void processNodeUpdate(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String msg;
        NodeRingInfo prevNodeRingInfo = NodeSearch.findNodeRingInfoByDocId(request.getParameter(Params._ID.val));
        NodeRingInfo nodeRingInfoToUpdate = NodeSearch.findNodeRingInfoByDocId(request.getParameter(Params._ID.val));
        nodeRingInfoToUpdate = loadNodeRingInfoPropsFromRequest(nodeRingInfoToUpdate, request);
        String prevStateReason = request.getParameter(Params.PREV_STATE_REASON.val);

        if (prevNodeRingInfo.equals(nodeRingInfoToUpdate)) {
            msg =  "no change - no need to update node ring info";
            LOG.debug("{}", msg);
            msg = null;
        }
        else{
            msg = NodeUpdater.doNodeUpdate(nodeRingInfoToUpdate, prevStateReason);
        }
        request.setAttribute(Attrs.MESSAGE.val, msg);
        request.setAttribute(Attrs.OPTION.val, AttrParamValues.UPDATE.val);
        request.setAttribute(Attrs.NODE_RING_INFO.val, nodeRingInfoToUpdate);
        forwardToJsp(PageJSP.NODE_FORM, request, response);
    }

    protected NodeRingInfo loadNodeRingInfoPropsFromRequest(NodeRingInfo nodeRingInfoResult, HttpServletRequest request) throws NullPointerException {
        if (request == null) {
            throw new IllegalArgumentException("Null request");
        }

        String _id = request.getParameter(Params._ID.val);
        String nodeDomainName = request.getParameter(Params.NODE_DOMAIN_NAME_FROM_RING_INFO.val);
        String portNumber = request.getParameter(Params.PORT_NUMBER_FROM_RING_INFO.val);
        String role = request.getParameter(Params.ROLE.val);
        String state = request.getParameter(Params.STATE.val);
        String stateReason = request.getParameter(Params.STATE_REASON.val);

        if (nodeRingInfoResult == null) {
            nodeRingInfoResult = new NodeRingInfo(nodeDomainName, portNumber, role, state, stateReason, true);
        } else {
            nodeRingInfoResult.setNodeDomainNameFromRingInfo(nodeDomainName);
            nodeRingInfoResult.setPortNumberFromRingInfo(portNumber);
            nodeRingInfoResult.setRole(role);
            nodeRingInfoResult.setState(state);
            nodeRingInfoResult.setStateReason(stateReason);
        }

        if (_id != null && !_id.trim().isEmpty()) {
            nodeRingInfoResult.set_id(_id);
        }
        LOG.debug("node state loaded from request - success");
        return nodeRingInfoResult;
    }

    private boolean authorizeForUpdateAction(HttpServletRequest request, String nodeToDisplName) {
        Authorization.hasLoggedOperator(request);
        try{
            //supernode can see/update others
            return Authorization.hasThisNodeRoleAnyOf(request, NodeRole.SUPERNODE.name());
        } catch (IllegalStateException e) {
            //basic can see/update itself
            return Authorization.hasNameAnyRole(request, nodeToDisplName, NodeRole.BASICNODE.name());
        }
    }
}
