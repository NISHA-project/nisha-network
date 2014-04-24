/**
 * ****************************************************************************
 * Copyright (c) 2012 Research and Academic Computer Network.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * <p/>
 * With financial support from the Prevention, Preparedness and Consequence
 * Management of Terrorism and other Security Related Risks Programme
 * European Commission - Directorate-General Home Affairs
 * <p/>
 * Contributors:
 * Research and Academic Computer Network
 * ****************************************************************************
 */

package pl.nask.nisha.manager.controllers.alerts;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.nisha.commons.network.NetworkAlert;
import pl.nask.nisha.commons.network.NodeRole;
import pl.nask.nisha.manager.controllers.app.NishaBasicServlet;
import pl.nask.nisha.manager.model.logic.alerts.AlertSearch;
import pl.nask.nisha.manager.model.logic.alerts.AlertUpdater;
import pl.nask.nisha.manager.model.logic.app.LoggerUpdater;
import pl.nask.nisha.manager.model.logic.security.Authorization;
import pl.nask.nisha.manager.model.transfer.servletsupport.AttrParamValues;
import pl.nask.nisha.manager.model.transfer.servletsupport.Attrs;
import pl.nask.nisha.manager.model.transfer.servletsupport.PageJSP;
import pl.nask.nisha.manager.model.transfer.servletsupport.Params;

public class AlertUpdaterController extends NishaBasicServlet{

    public static final Logger LOG = LoggerFactory.getLogger(AlertUpdaterController.class);

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

            boolean authorized = Authorization.hasLoggedOperatorAndAnyRole(request, NodeRole.SUPERNODE.name(), NodeRole.BASICNODE.name());
            LOG.info("authorization success? {} (alert update)", authorized);

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

            boolean authorized = Authorization.hasLoggedOperatorAndAnyRole(request, NodeRole.SUPERNODE.name(), NodeRole.BASICNODE.name());
            LOG.info("authorization success? {} (alert update)", authorized);

            if (request.getParameter(Params.CLOSE_ALERT.val) != null){
                processCloseAlert(request, response);
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

    private void processCloseAlert(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String alertId = request.getParameter(Params._ID.val);
        if (alertId == null || alertId.trim().isEmpty()) {
            throw new IllegalArgumentException("alert id cannot be null nor empty");
        }
        AlertUpdater.closeAlert(alertId);
        NetworkAlert alert = AlertSearch.findAlert(alertId);
        String msg = "success - alert closed - affected node: " + alert.getAffectedNodeName() + ", time: " + alert.getTimestamp();
        request.setAttribute(Attrs.MESSAGE.val, msg);

        AlertSearchController.processAlertSearch(getPageNumberString(request), request, response);
    }

    private String getPageNumberString(HttpServletRequest request) {
        String result = request.getParameter(Params.PAGE_NUMBER.val);
        if (result == null || result.trim().isEmpty()) {
            return "" + 1;
        } else {
            return result;
        }
    }
}

