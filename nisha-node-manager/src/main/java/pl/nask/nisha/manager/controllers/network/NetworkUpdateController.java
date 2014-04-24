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
import pl.nask.nisha.manager.model.transfer.servletsupport.AttrParamValues;
import pl.nask.nisha.manager.model.transfer.servletsupport.Attrs;
import pl.nask.nisha.manager.model.transfer.servletsupport.PageJSP;
import pl.nask.nisha.manager.model.transfer.servletsupport.Params;
import pl.nask.nisha.manager.model.transfer.supportbeans.NodeRingInfoRichCollection;
import pl.nask.nisha.manager.model.logic.security.Authorization;

public class NetworkUpdateController extends NishaBasicServlet {

    private static final long serialVersionUID = 5438507565968403990L;
    public static final Logger LOG = LoggerFactory.getLogger(NetworkUpdateController.class);

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

        String option = null;
        String msg;
        try {
            request.setCharacterEncoding(UTF_8);
            LoggerUpdater.checkLoggerIsStarted();

            boolean authorized = Authorization.hasLoggedOperatorAndAnyRole(request, NodeRole.BASICNODE.name(), NodeRole.SUPERNODE.name());
            LOG.info("authorization success? {} (show network)", authorized);

            option = request.getParameter(Params.OPTION.val);
            if (option.equals(AttrParamValues.SHOW_NETWORK.val)) {
                boolean createNetworkDocIfNeeded = false;
                NetworkRingInfo networkRingInfo = NetworkUpdater.getNetworkRingInfo(createNetworkDocIfNeeded);
                LOG.info("network: {}", networkRingInfo);
                request.setAttribute(Attrs.NETWORK.val, networkRingInfo);
                request.setAttribute(Attrs.NODE_RING_INFO_RICH_COLLECTION.val, new NodeRingInfoRichCollection());
                forwardToJsp(PageJSP.NODES, request, response);
            } else {
                throw new IllegalStateException(AttrParamValues.UNKNOWN_ACTION.val);
            }
        } catch (Exception e) {
            msg = e.getMessage();
            request.setAttribute(Params.OPTION.val, option);
            request.setAttribute(Attrs.MESSAGE.val, msg);
            LOG.warn("{}", msg);
            forwardToJsp(PageJSP.MENU, request, response);
        }
    }

    protected void processRequestPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String option = null;
        String msg;
        try {
            request.setCharacterEncoding(UTF_8);
            LoggerUpdater.checkLoggerIsStarted();
            option = request.getParameter(Params.OPTION.val);
            throw new IllegalStateException(AttrParamValues.UNKNOWN_ACTION.val);
        } catch (Exception e) {
            msg = e.getMessage();
            LOG.warn("{} option: {}", msg, option);
            request.setAttribute(Params.OPTION.val, option);
            request.setAttribute(Attrs.MESSAGE.val, msg);
            forwardToJsp(PageJSP.MENU, request, response);
        }
    }

}

