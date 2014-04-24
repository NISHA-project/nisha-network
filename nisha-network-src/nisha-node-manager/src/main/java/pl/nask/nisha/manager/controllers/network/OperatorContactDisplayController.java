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
import pl.nask.nisha.manager.model.domain.network.OperatorContact;
import pl.nask.nisha.manager.model.logic.app.LoggerUpdater;
import pl.nask.nisha.manager.model.logic.network.OperatorContactDisplay;
import pl.nask.nisha.manager.model.transfer.servletsupport.AttrParamValues;
import pl.nask.nisha.manager.model.transfer.servletsupport.Attrs;
import pl.nask.nisha.manager.model.transfer.servletsupport.PageJSP;
import pl.nask.nisha.manager.model.transfer.servletsupport.Params;
import pl.nask.nisha.manager.model.logic.security.Authorization;

public class OperatorContactDisplayController extends NishaBasicServlet {

    private static final long serialVersionUID = 2535299627849851659L;
    public static final Logger LOG = LoggerFactory.getLogger(OperatorContactDisplayController.class);


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequestGet(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequestPost(request, response);
    }

    private void processRequestGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String msg;
        try {
            request.setCharacterEncoding(UTF_8);
            LoggerUpdater.checkLoggerIsStarted();

            String option = request.getParameter(Params.OPTION.val);
            if (option != null) {
                processOption(option, request, response);
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

    private void processRequestPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String msg;
        try {
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

    private void processOption(String option, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String opId = request.getParameter(Params.OPERATOR_CONTACT_ID.val);
        String contextNodeName = request.getParameter(Params.NODE_DOMAIN_NAME_FROM_NODE_INFO.val);
        if (contextNodeName == null) {
         contextNodeName = request.getParameter(Params.NODE_DOMAIN_NAME_FROM_CONFIG.val);
        }

        if (option.equals(AttrParamValues.LOCAL.val)) {
            boolean authorized = Authorization.hasLoggedOperatorAndAnyRole(request, NodeRole.getNameArray());
            LOG.info("authorization success? {} (local contact display)", authorized);

            if (opId == null || opId.trim().isEmpty() ) {
             throw new IllegalArgumentException("operator id cannot be null nor empty");
            }
        }
        else if (option.equals(AttrParamValues.GLOBAL.val)) {
            boolean authorized = Authorization.hasLoggedOperatorAndAnyRole(request, NodeRole.BASICNODE.name(), NodeRole.SUPERNODE.name());
            LOG.info("authorization success? {} (global contact display) ", authorized);

            if (opId == null || opId.trim().isEmpty() || contextNodeName == null ||contextNodeName.trim().isEmpty()) {
             throw new IllegalArgumentException("operator id and context node name cannot be null nor empty: " +
                     "opId:" + opId + " node: " + contextNodeName);
            }
        }
        else {
         throw new IllegalArgumentException(option + " - unknown option");
        }

        prepareContactToDisplay(option, contextNodeName, opId, request, response);

    }

    private static void prepareContactToDisplay(String option, String contextNodeName, String opId, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String msg;

        if (opId.trim().isEmpty()) {
            msg = "cannot display contact of operator with empty id";
            LOG.warn("{}", msg);
            forwardToJsp(PageJSP.MENU, request, response);

        } else {
            OperatorContact contact = OperatorContactDisplay.resolveContactLocallyOrGlobally(option, opId, contextNodeName);

            if (contact == null) {
                msg = "contact not found";
                LOG.debug("{}", msg);
                request.setAttribute(Attrs.MESSAGE.val, msg);
                forwardToJsp(PageJSP.MENU, request, response);
            } else {
                request.setAttribute(Attrs.OPERATOR_CONTACT.val, contact);
                request.setAttribute(Attrs.OPTION.val, option);
                forwardToJsp(PageJSP.OPERATOR_CONTACT, request, response);
            }
        }
    }

}

