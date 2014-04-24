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
import pl.nask.nisha.manager.model.domain.network.NodeInfo;
import pl.nask.nisha.manager.model.logic.app.LoggerUpdater;
import pl.nask.nisha.manager.model.logic.network.NodeDisplay;
import pl.nask.nisha.manager.model.transfer.servletsupport.AttrParamValues;
import pl.nask.nisha.manager.model.transfer.servletsupport.Attrs;
import pl.nask.nisha.manager.model.transfer.servletsupport.PageJSP;
import pl.nask.nisha.manager.model.transfer.servletsupport.Params;
import pl.nask.nisha.manager.model.logic.security.Authorization;

public class NodeDisplayController extends NishaBasicServlet {

    private static final long serialVersionUID = 7802873277640892517L;
    public static final Logger LOG = LoggerFactory.getLogger(NodeDisplayController.class);


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequestGet(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequestPost(request, response);
    }

    private void processRequestGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        try {
            request.setCharacterEncoding(UTF_8);
            LoggerUpdater.checkLoggerIsStarted();
            throw new IllegalStateException(AttrParamValues.UNKNOWN_ACTION.val);
        } catch (Exception e) {
            String msg = e.getMessage();
            LOG.warn("{}", msg);
            request.setAttribute(Attrs.MESSAGE.val, msg);
            forwardToJsp(PageJSP.MENU, request, response);
        }
    }

    private void processRequestPost(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {

        try {
            request.setCharacterEncoding(UTF_8);
            LoggerUpdater.checkLoggerIsStarted();

            if (request.getParameter(AttrParamValues.DETAILS_SUBMIT.val) != null) {
                boolean authorized = Authorization.hasLoggedOperatorAndAnyRole(request, NodeRole.BASICNODE.name(), NodeRole.SUPERNODE.name());
                LOG.info("authorization success? {} (node display)", authorized);
                processDisplayNodeDetailsRequest(request, response);
            }  else {
                throw new IllegalStateException(AttrParamValues.UNKNOWN_ACTION.val);
            }
        } catch (Exception e) {
            String msg = e.getMessage();
            LOG.warn("{}", msg);
            request.setAttribute(Attrs.MESSAGE.val, msg);
            forwardToJsp(PageJSP.MENU, request, response);
        }
    }

    private void processDisplayNodeDetailsRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String nodeDomainNameFromRing = request.getParameter(Params.NODE_DOMAIN_NAME_FROM_RING_INFO.val);
        String msg;
        if (nodeDomainNameFromRing == null || nodeDomainNameFromRing.trim().isEmpty()) {
            msg = "node domain name null or empty";
            throw new IllegalArgumentException(msg);
        }

        NodeInfo nodeInfo = NodeDisplay.getNodeInfoByNodeName(nodeDomainNameFromRing);
        if (nodeInfo == null) {
            msg = "cannot display node details - not found (check replicators and cohesion of replicator uri and node config)";
            throw new IllegalArgumentException(msg);
        }
        LOG.info("{}", nodeInfo.toString());
        request.setAttribute(Attrs.NODE_INFO.val, nodeInfo);
        forwardToJsp(PageJSP.NODE_DETAILS, request, response);
    }
}
