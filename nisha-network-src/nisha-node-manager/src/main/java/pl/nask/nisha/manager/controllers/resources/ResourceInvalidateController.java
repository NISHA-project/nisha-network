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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.nisha.commons.network.NodeRole;
import pl.nask.nisha.manager.controllers.app.NishaBasicServlet;
import pl.nask.nisha.manager.model.logic.app.LoggerUpdater;
import pl.nask.nisha.manager.model.logic.network.NodeSearch;
import pl.nask.nisha.manager.model.logic.resource.ResourceInvalidator;
import pl.nask.nisha.manager.model.transfer.servletsupport.AttrParamValues;
import pl.nask.nisha.manager.model.transfer.servletsupport.Attrs;
import pl.nask.nisha.manager.model.transfer.servletsupport.PageJSP;
import pl.nask.nisha.manager.model.transfer.servletsupport.Params;
import pl.nask.nisha.manager.model.logic.security.Authorization;

public class ResourceInvalidateController extends NishaBasicServlet {

    private static final long serialVersionUID = -1603672286316512721L;
    public static final Logger LOG = LoggerFactory.getLogger(ResourceInvalidateController.class);

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
        try{
            request.setCharacterEncoding(UTF_8);
            LoggerUpdater.checkLoggerIsStarted();
            throw new IllegalStateException(AttrParamValues.UNKNOWN_ACTION.val);
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

            if (request.getParameter(Params.INVALIDATE_RESOURCE_SUBMIT.val) != null){
                processInvalidateSubmit(request, response);
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

    private void processInvalidateSubmit(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String nodeRole = NodeSearch.getThisNodeRole();
        String artId = request.getParameter(Params.ART_ID.val);

        if (nodeRole != null && nodeRole.equalsIgnoreCase(NodeRole.SUPERNODE.name()) ) {

            boolean authorized = Authorization.hasLoggedOperatorAndAnyRole(request, NodeRole.SUPERNODE.name());
            LOG.info("authorization success? {} (resource invalidation by supernode)", authorized);

            if (artId != null && !artId.trim().isEmpty()){
                processInvalidateBySupernode(request, response);
            } else {
                throw new IllegalStateException("Id of article to invalidate cannot be null nor empty");
            }
        }
        //this is done by sending message to supernode opertor      - to del
//        else if (nodeRole != null && nodeRole.equalsIgnoreCase(NodeRole.BASICNODE.name())) {
//
//            boolean authorized = Authorization.hasLoggedOperatorAndAnyRole(request, NodeRole.BASICNODE.name());
//            LOG.debug("authorization success? {} (resource invalidation by basicnode)", authorized);
//
//            LOG.debug("invalidate by basic node to be done in the future");
//        }
        else {
            throw new IllegalStateException(AttrParamValues.UNKNOWN_ACTION.val);
        }
    }

    private void processInvalidateBySupernode(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        LOG.debug("invalidation by supernode - start...");
        String msg;
        String idToFind = request.getParameter(Params.ART_ID.val);
        if (idToFind == null || idToFind.trim().isEmpty()) {
            msg = "resource id cannot be null nor empty";
            throw new IllegalStateException(msg);
        }
        String resultMsg = ResourceInvalidator.doInvalidateResource(idToFind);
        request.setAttribute(Attrs.MESSAGE.val, resultMsg);
        forwardToJsp(PageJSP.RESOURCES, request, response);
    }
}

