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
import java.util.HashSet;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.nisha.commons.network.NodeRole;
import pl.nask.nisha.commons.network.NodeState;
import pl.nask.nisha.manager.controllers.app.NishaBasicServlet;
import pl.nask.nisha.manager.model.domain.network.NetworkRingInfo;
import pl.nask.nisha.manager.model.logic.app.LoggerUpdater;
import pl.nask.nisha.manager.model.logic.network.NetworkUpdater;
import pl.nask.nisha.manager.model.logic.network.NodeSearch;
import pl.nask.nisha.manager.model.transfer.servletsupport.AttrParamValues;
import pl.nask.nisha.manager.model.transfer.servletsupport.Attrs;
import pl.nask.nisha.manager.model.transfer.servletsupport.PageJSP;
import pl.nask.nisha.manager.model.transfer.servletsupport.Params;
import pl.nask.nisha.manager.model.transfer.supportbeans.NodeRingInfoRichCollection;
import pl.nask.nisha.manager.model.logic.security.Authorization;

public class NodeSearchController extends NishaBasicServlet {

    private static final long serialVersionUID = -3926536378754818382L;
    private static final Logger LOG = LoggerFactory.getLogger(NodeSearchController.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequestGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequestPost(req, resp);
    }

    private void processRequestGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String msg;
        try {
            request.setCharacterEncoding(UTF_8);
            LoggerUpdater.checkLoggerIsStarted();

            boolean authorized = Authorization.hasLoggedOperatorAndAnyRole(request, NodeRole.BASICNODE.name(), NodeRole.SUPERNODE.name());
            LOG.info("authorization success? {} (unknown action)", authorized);

            throw new IllegalStateException(AttrParamValues.UNKNOWN_ACTION.val);
        } catch (Exception e) {
            msg = e.getMessage();
            LOG.warn("{}", msg);
            request.setAttribute(Attrs.MESSAGE.val, msg);
            forwardToJsp(PageJSP.NODES, request, response);
        }
    }

    private void processRequestPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String msg;
        try {
            request.setCharacterEncoding(UTF_8);
            LoggerUpdater.checkLoggerIsStarted();

            boolean authorized = Authorization.hasLoggedOperatorAndAnyRole(request, NodeRole.BASICNODE.name(), NodeRole.SUPERNODE.name());
            LOG.debug("authorization success? {} (node search)", authorized);

            //searching by docid or query?
            if (request.getParameter(Params.QUERY.val) != null) {
                String searchMode = NodeSearch.resolveSearchMode(request.getParameter(Params.QUERY_SUBMIT_NAME.val));
                String query = request.getParameter(Params.QUERY.val);
                Set<String> acceptedStates = getAcceptedStates(request);

                NodeRingInfoRichCollection nodeRingInfoRichCollection = NodeSearch.findNodeRingInfosByQuery(query, searchMode, acceptedStates);
                LOG.info("searching with user query: \"{}\"", query);

                NetworkRingInfo networkRingInfo = NetworkUpdater.getNetworkRingInfoNewIfNull();

                request.setAttribute(Attrs.SEARCH_STATE_FILTERS.val, acceptedStates);
                request.setAttribute(Params.QUERY.val, query);
                request.setAttribute(Params.SEARCH_MODE.val, searchMode);
                request.setAttribute(Attrs.SEARCH_RESULT.val, Attrs.SEARCH_RESULT.val);
                request.setAttribute(Attrs.NODE_RING_INFO_RICH_COLLECTION.val, nodeRingInfoRichCollection);
                request.setAttribute(Attrs.NETWORK.val, networkRingInfo);
                request.setAttribute(Attrs.OPTION.val, AttrParamValues.LIST_NODES.val);
                LOG.info("nodes found: {} success {}", nodeRingInfoRichCollection.getNodeRingInfoRichList().size(),
                        nodeRingInfoRichCollection.getNodeNames());
                forwardToJsp(PageJSP.NODES, request, response);
            } else {
                throw new IllegalStateException(AttrParamValues.UNKNOWN_ACTION.val);
            }
        } catch (Exception e) {
            msg = e.getMessage();
            LOG.warn("{}", msg);
            request.setAttribute(Attrs.MESSAGE.val, msg);
            forwardToJsp(PageJSP.NODES, request, response);
        }
    }

    private Set<String> getAcceptedStates(HttpServletRequest request) {
        Set<String> stateAccepted = new HashSet<String>();
        if (request.getParameter("critAct") != null) {
            stateAccepted.add(NodeState.ACTIVE.name());
        }
        if (request.getParameter("critInact") != null) {
            stateAccepted.add(NodeState.INACTIVE.name());
        }
        if (request.getParameter("critBlk") != null) {
            stateAccepted.add(NodeState.BLOCKED.name());
        }
        if (request.getParameter("critRmv") != null) {
            stateAccepted.add(NodeState.REMOVED.name());
        }
        String msg = "get all states";
        if (stateAccepted.size() != 0 ){
            msg = stateAccepted.toString();
        }
        LOG.info("search state filters: {}", msg);
        return stateAccepted;
    }
}
