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
package pl.nask.nisha.manager.controllers.network;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.nisha.commons.network.NodeRole;
import pl.nask.nisha.manager.controllers.app.NishaBasicServlet;
import pl.nask.nisha.manager.model.domain.network.NetworkRingInfo;
import pl.nask.nisha.manager.model.logic.app.LoggerUpdater;
import pl.nask.nisha.manager.model.logic.network.NetworkUpdater;
import pl.nask.nisha.manager.model.logic.network.NodeRemover;
import pl.nask.nisha.manager.model.transfer.servletsupport.AttrParamValues;
import pl.nask.nisha.manager.model.transfer.servletsupport.Attrs;
import pl.nask.nisha.manager.model.transfer.servletsupport.PageJSP;
import pl.nask.nisha.manager.model.transfer.servletsupport.Params;
import pl.nask.nisha.manager.model.logic.security.Authorization;

public class NodeRemoveController extends NishaBasicServlet {

    private static final long serialVersionUID = -5120364312135597988L;
    public static final Logger LOG = LoggerFactory.getLogger(NodeRemoveController.class);

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

    protected void processRequestGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try{
            request.setCharacterEncoding(UTF_8);
            LoggerUpdater.checkLoggerIsStarted();

            boolean authorized = Authorization.hasLoggedOperatorAndAnyRole(request, NodeRole.SUPERNODE.name());
            LOG.info("authorization success? {} (unknown action)", authorized);

            throw new IllegalStateException(AttrParamValues.UNKNOWN_ACTION.val);
        } catch (Exception e) {
            String msg = e.getMessage();
            LOG.warn("{}", msg);
            request.setAttribute(Attrs.MESSAGE.val, msg);
            forwardToJsp(PageJSP.MENU, request, response);
        }
    }

    protected void processRequestPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try{
            request.setCharacterEncoding(UTF_8);
            LoggerUpdater.checkLoggerIsStarted();

            boolean authorized = Authorization.hasLoggedOperatorAndAnyRole(request, NodeRole.SUPERNODE.name());
            LOG.debug("authorization success? {} (node removal)", authorized);

            String optionRemoveSubmit = request.getParameter(AttrParamValues.REMOVE_SUBMIT.val);
            if (optionRemoveSubmit != null && optionRemoveSubmit.equalsIgnoreCase(AttrParamValues.REMOVE.val)) {
                processRemoveFirstSubmit(request, response);
            } else if (request.getParameter("removeCancel") != null) {
                processCancelRemove(request, response);
            } else if (request.getParameter("removeConfirm") != null) {
               processNodeRemove(request, response);
            }
        } catch (Exception e) {
            String msg = e.getMessage();
            request.setAttribute(Attrs.MESSAGE.val, msg);
            forwardToJsp(PageJSP.MENU, request, response);
        }
    }

    private void processRemoveFirstSubmit(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String _id = request.getParameter(Params._ID.val);
        String nodeDomainNameFromRing = request.getParameter(Params.NODE_DOMAIN_NAME_FROM_RING_INFO.val);
        if ( _id != null && !_id.trim().isEmpty() && nodeDomainNameFromRing != null && !nodeDomainNameFromRing.trim().isEmpty()) {
            request.setAttribute(Params._ID.val, _id);
            request.setAttribute(Params.NODE_DOMAIN_NAME_FROM_RING_INFO.val, nodeDomainNameFromRing);
            request.setAttribute(Attrs.OPTION.val, AttrParamValues.REMOVE.val);
            LOG.info("{} - node chosen to remove", nodeDomainNameFromRing);
            forwardToJsp(PageJSP.NODES, request, response);
        } else {
            throw new IllegalStateException("_id, nodeDomainNameFromRing - one or both parameters missing");
        }

    }

    private void processCancelRemove(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        boolean createNetworkDocIfNeeded = false;
        NetworkRingInfo networkRingInfo = NetworkUpdater.getNetworkRingInfo(createNetworkDocIfNeeded);
        request.setAttribute(Attrs.NETWORK.val, networkRingInfo);
        LOG.info("node removal canceled");
        forwardToJsp(PageJSP.NODES, request, response);
    }

    private void processNodeRemove(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        NodeRemover.removeNode(request.getParameter("docidToRemove"));
        LOG.info("node removed - success");
        request.setAttribute(Attrs.MESSAGE.val, "node removed - success");
        forwardToJsp(PageJSP.MENU, request, response);
    }
}

