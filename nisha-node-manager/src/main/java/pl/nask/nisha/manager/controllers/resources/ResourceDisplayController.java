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
package pl.nask.nisha.manager.controllers.resources;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.lightcouch.CouchDbClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.nisha.commons.network.NodeRole;
import pl.nask.nisha.manager.controllers.app.NishaBasicServlet;
import pl.nask.nisha.manager.model.domain.app.CouchDbConnector;
import pl.nask.nisha.manager.model.domain.resources.Article;
import pl.nask.nisha.manager.model.logic.app.LoggerUpdater;
import pl.nask.nisha.manager.model.logic.network.NodeSearch;
import pl.nask.nisha.manager.model.logic.resource.ResourceSearch;
import pl.nask.nisha.manager.model.transfer.servletsupport.AttrParamValues;
import pl.nask.nisha.manager.model.transfer.servletsupport.Attrs;
import pl.nask.nisha.manager.model.transfer.servletsupport.PageJSP;
import pl.nask.nisha.manager.model.transfer.servletsupport.Params;
import pl.nask.nisha.manager.model.logic.security.Authorization;

public class ResourceDisplayController extends NishaBasicServlet {

    private static final long serialVersionUID = -7471289659418999568L;
    private static final Logger LOG = LoggerFactory.getLogger(ResourceDisplayController.class);


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
        try {
            request.setCharacterEncoding(UTF_8);
            LoggerUpdater.checkLoggerIsStarted();
            if (request.getParameter(Params.OPTION.val) != null && request.getParameter(Params.OPTION.val).equals(AttrParamValues.SHOW_RESOURCE_SEARCH.val)) {
                forwardToJsp(PageJSP.RESOURCES, request, response);
            }
            else if (request.getParameter(Params.INVALIDATE_ASK_REDIRECT.val) != null) {
                showResourceDetails(request, response);
            }
            else {
                throw new IllegalStateException(AttrParamValues.UNKNOWN_ACTION.val);
            }
        } catch (Exception e) {
            msg = e.getMessage();
            LOG.warn("{}", msg);
            request.setAttribute(Attrs.MESSAGE.val, msg);
            forwardToJsp(PageJSP.MENU, request, response);
        }
    }

    protected void processRequestPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String msg;
        try {
            request.setCharacterEncoding(UTF_8);
            LoggerUpdater.checkLoggerIsStarted();

            if (request.getParameter(Params.SHOW_RESOURCE_DETAILS.val) != null) {
                showResourceDetails(request, response);
            } else {
                throw new IllegalStateException(AttrParamValues.UNKNOWN_ACTION.val);
            }
        } catch (Exception e) {
            msg = e.getMessage();
            LOG.warn("{}", msg);
            request.setAttribute(Attrs.MESSAGE.val, msg);
            forwardToJsp(PageJSP.MENU, request, response);
        }
    }

    private void showResourceDetails(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String resourceId = request.getParameter(Params.ART_ID.val);
        String thisNodeRole = NodeSearch.getThisNodeRole();

        Boolean isGlobalSearch = Boolean.valueOf(request.getParameter(Params.SEARCH_MODE.val));
        LOG.info("global search?: {}", isGlobalSearch);
        boolean authorized;
        if(isGlobalSearch) {
            authorized = Authorization.hasLoggedOperatorAndAnyRole(request, NodeRole.BASICNODE.name());
        } else {
            authorized = Authorization.hasLoggedOperatorAndAnyRole(request, NodeRole.SUPERNODE.name(), NodeRole.BASICNODE.name());
        }
        LOG.info("authorization success? {} (resource details)", authorized);
        CouchDbClient dbClient = CouchDbConnector.resolveResourceCouchDbClient(isGlobalSearch, thisNodeRole);
        Article article = ResourceSearch.getResourceById(dbClient, resourceId);
        LOG.info("{}", article.toString());

        request.setAttribute(Attrs.ARTICLE.val, article);
        forwardToJsp(PageJSP.RESOURCE_DETAILS, request, response);
    }

}

