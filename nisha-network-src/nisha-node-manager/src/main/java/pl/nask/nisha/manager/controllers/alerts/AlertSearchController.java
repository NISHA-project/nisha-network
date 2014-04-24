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
package pl.nask.nisha.manager.controllers.alerts;

import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.lightcouch.CouchDbClient;
import org.lightcouch.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.nisha.commons.network.NetworkAlert;
import pl.nask.nisha.commons.network.NodeRole;
import pl.nask.nisha.manager.controllers.app.NishaBasicServlet;
import pl.nask.nisha.manager.model.domain.app.CouchDbConnector;
import pl.nask.nisha.manager.model.logic.app.LoggerUpdater;
import pl.nask.nisha.manager.model.logic.app.NishaPagination;
import pl.nask.nisha.manager.model.logic.local.LocalConfigUpdater;
import pl.nask.nisha.manager.model.logic.security.Authorization;
import pl.nask.nisha.manager.model.transfer.servletsupport.AttrParamValues;
import pl.nask.nisha.manager.model.transfer.servletsupport.Attrs;
import pl.nask.nisha.manager.model.transfer.servletsupport.PageJSP;
import pl.nask.nisha.manager.model.transfer.servletsupport.Params;
import pl.nask.nisha.manager.model.transfer.supportbeans.AlertPageBean;

public class AlertSearchController extends NishaBasicServlet{

    private static final long serialVersionUID = -1854572130573919084L;
    public static final Logger LOG = LoggerFactory.getLogger(AlertSearchController.class);
    private static NishaPagination<NetworkAlert> alertNishaPagination = new NishaPagination<NetworkAlert>();
    private static boolean hideClosedAlerts = false;

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
            LOG.info("authorization success? {} (alert display)", authorized);

            if (request.getParameter(Params.ALERTS_OPTION.val) != null){
                forwardToJsp(PageJSP.ALERTS, request, response);
            }
            else if (request.getParameter(Params.JUMP_TO_PAGE.val) != null) {
                String pageNumberString = request.getParameter(Params.JUMP_TO_PAGE.val);
                processAlertSearch(pageNumberString, request, response);

            }
            else if (request.getParameter(Params.SHOW_HIDE_ALERTS.val) != null) {
                hideClosedAlerts = !hideClosedAlerts;
                String pageNumberString = "" + 1;
                processAlertSearch(pageNumberString, request, response);
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

            boolean authorized = Authorization.hasLoggedOperatorAndAnyRole(request, NodeRole.SUPERNODE.name(), NodeRole.BASICNODE.name());
            LOG.info("authorization success? {} (alert search)", authorized);

            String pageNumberString = request.getParameter(Params.PAGE_NUMBER.val);
            if (request.getParameter(Params.ROWS_PER_PAGE.val) != null) {
                alertNishaPagination.updateRowsPerPage(request);
                pageNumberString = request.getParameter(Params.JUMP_TO_PAGE.val);
            }

            processAlertSearch(pageNumberString, request, response);

        } catch (Exception e) {
            msg = e.getMessage();
            LOG.warn("{}", msg);
            request.setAttribute(Attrs.MESSAGE.val, msg);
            forwardToJsp(PageJSP.MENU, request, response);
        }

    }

    static void processAlertSearch(String pageNumberString, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        CouchDbClient nodesClient = CouchDbConnector.getCouchDbConnector().nodesDbClient;
        String searchMode = resolveSearchMode(request);
        String viewName = resolveFullViewName(searchMode, hideClosedAlerts);
        boolean descendingOrder = resolveOrder(searchMode);
        String alertQuery = resolveQuery(request, searchMode);

        Page<NetworkAlert> resultPage = resolveAlertPage(pageNumberString, alertQuery, request, nodesClient, viewName, descendingOrder);

        String[] infoArray = {"" + resultPage.getTotalResults(), searchMode, alertQuery};
        LOG.info("alerts found: {} success (mode {} query\"{}\")", infoArray);

        AlertPageBean alertPageBean = new AlertPageBean(resultPage);

         request.setAttribute(Attrs.ALERT_PAGE_BEAN.val, alertPageBean);
         request.setAttribute(Attrs.HIDE_CLOSED_ALERTS.val, hideClosedAlerts);
         request.setAttribute(Params.ALERT_QUERY.val, alertQuery);
         request.setAttribute(Params.SEARCH_MODE.val, searchMode);
         request.setAttribute(Attrs.SEARCH_RESULT.val, Attrs.SEARCH_RESULT.val);
         request.getSession().setAttribute(Params.ROWS_PER_PAGE.val, "" + alertNishaPagination.getRowsPerPage());
         forwardToJsp(PageJSP.ALERTS, request, response);
     }

    private static Page<NetworkAlert> resolveAlertPage(String pageNumberString, String query, HttpServletRequest request,
                                                       CouchDbClient client, String viewName, boolean descendingOrder) {
        Page<NetworkAlert> alertPage = new Page<NetworkAlert>();
        try{
            LOG.info("alert search - view:" + viewName + " query:" + query + " descending:" + descendingOrder + " page:" + pageNumberString);
            alertPage = alertNishaPagination.getPageForPageJumpNumber(pageNumberString, viewName,
                                    descendingOrder, query, client, NetworkAlert.class, request);
        } catch (Exception e) {
            if (e.getMessage().equals("No result was returned by this view query.")) {
                alertPage.setResultList(new ArrayList<NetworkAlert>());
            }
        }
        return alertPage;
    }

    private static String resolveSearchMode(HttpServletRequest request) {
        String searchMode;
        if (request.getParameter(Params.FIND_ALL_ALERTS.val) != null ||
                (request.getParameter(Attrs.MODE.val) != null && request.getParameter(Attrs.MODE.val).equals(AttrParamValues.SEARCH_ALERT_MODE_ALL.val)) ) {

            searchMode = AttrParamValues.SEARCH_ALERT_MODE_ALL.val;
        }
        else if (request.getParameter(Params.ALERT_AFFECTED_NAME_SUBMIT.val) != null ||
                (request.getParameter(Attrs.MODE.val) != null && request.getParameter(Attrs.MODE.val).equals(AttrParamValues.SEARCH_ALERT_MODE_AFF_NAME.val))) {

            searchMode = AttrParamValues.SEARCH_ALERT_MODE_AFF_NAME.val;
        }
        else if (request.getParameter(Params.ALERT_DETECTING_NAME_SUBMIT.val) != null ||
                (request.getParameter(Attrs.MODE.val) != null && request.getParameter(Attrs.MODE.val).equals(AttrParamValues.SEARCH_ALERT_MODE_DET_NAME.val))) {

            searchMode = AttrParamValues.SEARCH_ALERT_MODE_DET_NAME.val;
        }
        else if (request.getParameter(Params.FIND_THIS_NODE_ALERTS.val) != null ||
                (request.getParameter(Attrs.MODE.val) != null && request.getParameter(Attrs.MODE.val).equals(AttrParamValues.SEARCH_THIS_NODE_ALERTS.val))) {
            searchMode = AttrParamValues.SEARCH_THIS_NODE_ALERTS.val;
        }
        else {
            throw new IllegalStateException(AttrParamValues.UNKNOWN_ACTION.val);
        }
        return searchMode;
    }

    private static String resolveFullViewName(String searchMode, boolean hideClosedAlerts) {
         String viewName;
         String viewBaseName = CouchDbConnector.DB_NAME_NODES;
         if (searchMode.equals(AttrParamValues.SEARCH_ALERT_MODE_ALL.val)){
             if (hideClosedAlerts) {
                 viewName = CouchDbConnector.VIEW_ALERTS_ALL_NOT_CLOSED;
             } else {
                 viewName = CouchDbConnector.VIEW_ALERTS_ALL;
             }
         }
         else if (searchMode.equals(AttrParamValues.SEARCH_ALERT_MODE_AFF_NAME.val) ||
                 searchMode.equals(AttrParamValues.SEARCH_THIS_NODE_ALERTS.val)){
             if (hideClosedAlerts) {
                 viewName = CouchDbConnector.VIEW_ALERTS_AFF_NOT_CLOSED;
             } else {
                 viewName = CouchDbConnector.VIEW_ALERTS_AFF;
             }
         }
         else if (searchMode.equals(AttrParamValues.SEARCH_ALERT_MODE_DET_NAME.val)){
             if (hideClosedAlerts) {
                 viewName = CouchDbConnector.VIEW_ALERTS_DET_NOT_CLOSED;
             } else {
                 viewName = CouchDbConnector.VIEW_ALERTS_DET;
             }
         }
         else {
             throw new IllegalStateException(AttrParamValues.UNKNOWN_ACTION.val);
         }
         return viewBaseName + viewName;
    }

    private static boolean resolveOrder(String searchMode) {
        boolean result = true;
        //at the moment all search modes require descending order
        LOG.info("search mode: " + searchMode + " descending order? " + result);
        return result;
    }

    private static String resolveQuery(HttpServletRequest request, String searchMode) {
        if (searchMode.equals(AttrParamValues.SEARCH_THIS_NODE_ALERTS.val)) {
            return LocalConfigUpdater.getThisNodeUri();
        }

        String alertQuery = request.getParameter(Params.ALERT_QUERY.val);
        if (alertQuery == null || alertQuery.trim().isEmpty()) {
            alertQuery = request.getParameter(Params.SEARCH_QUERY.val);
        }
        if (alertQuery == null || alertQuery.trim().isEmpty()) {
            alertQuery = "";
        }
        return alertQuery;
    }

}
