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
package pl.nask.nisha.manager.controllers.app;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch.qos.logback.classic.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.nisha.commons.network.NodeRole;
import pl.nask.nisha.manager.model.domain.app.CouchDbConnector;
import pl.nask.nisha.manager.model.domain.local.NodeConfiguration;
import pl.nask.nisha.manager.model.logic.app.LoggerUpdater;
import pl.nask.nisha.manager.model.logic.local.LocalConfigSearch;
import pl.nask.nisha.manager.model.transfer.servletsupport.AttrParamValues;
import pl.nask.nisha.manager.model.transfer.servletsupport.Attrs;
import pl.nask.nisha.manager.model.transfer.servletsupport.PageJSP;
import pl.nask.nisha.manager.model.transfer.servletsupport.Params;
import pl.nask.nisha.manager.model.transfer.supportbeans.MessageCountInfoBean;
import pl.nask.nisha.manager.model.transfer.supportbeans.NodeRolesBean;
import pl.nask.nisha.manager.model.transfer.supportbeans.ValueBean;

public class AppStarterController extends NishaBasicServlet {

    private static final long serialVersionUID = 338634430010101836L;
    public static final Logger LOG = LoggerFactory.getLogger(AppStarterController.class);


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

        try {
            request.setCharacterEncoding(UTF_8);
            loadBeans(request);
            LoggerUpdater.checkLoggerIsStarted();
            LoggerUpdateController.setLoggerLevel(LoggerUpdateController.resolveCurrentLoggerLevel(request, Level.INFO.toString()), request);
            //            try{
            //                LoggerUpdater.checkLoggerIsStarted();
            //            } catch (IllegalStateException ex) {
            //                LoggerUpdater.enableFileLogging(LoggerUpdater.LOG_FILE_NAME, request);
            //            }

            String option = request.getParameter(Params.OPTION.val);

            if (option != null) {
                if (option.equals(AttrParamValues.CONFIGURE.val)) {
                    //authorization - all allowed
                    String msg;
                    List<NodeConfiguration> configurations = CouchDbConnector.getCouchDbConnector().localDbClient.view(("nisha-local/by_type_config")).includeDocs(true).query(NodeConfiguration.class);
                    if (LocalConfigSearch.configWithOperatorsExists(configurations)) {
                        msg = "node already configured";
                        request.setAttribute(Attrs.MESSAGE.val, msg);
                        throw new IllegalStateException(msg);
                    } else {
                        LOG.debug("node not configured yet");

                        request.setAttribute(Attrs.LOGGED_OPERATOR_BEAN.val, null);
                        request.setAttribute(Attrs.LOCAL_NODE_CONFIGURATION.val, null);
                        request.getSession().setAttribute(Attrs.LOCAL_NODE_CONFIGURATION.val, null);
                        NodeConfiguration configFromFileConfig = new NodeConfiguration();
                        request.getSession().setAttribute(Attrs.LOCAL_NODE_CONFIGURATION.val, configFromFileConfig);
                        request.setAttribute(Attrs.OPTION.val, AttrParamValues.CONFIGURE.val);
                        forwardToJsp(PageJSP.LOCAL_CONFIG, request, response);
                    }
                } else if (option.equals(AttrParamValues.LOGIN.val)) {
                    //authorization - all allowed
                    forwardToJsp(PageJSP.LOGIN, request, response);
                } else {
                    throw new IllegalStateException(AttrParamValues.UNKNOWN_ACTION.val);
                }
            } else {
                throw new IllegalStateException(AttrParamValues.UNKNOWN_ACTION.val);
            }
        }
        catch (Exception e) {
            String msg = e.getMessage();
            LOG.warn("{}", msg);
            request.setAttribute(Attrs.MESSAGE.val, msg);
            forwardToJsp(PageJSP.INDEX, request, response);
        }
    }

    protected void processRequestPost(HttpServletRequest request, HttpServletResponse response)
                throws ServletException, IOException {
        String msg;
        try {
            request.setCharacterEncoding(UTF_8);
            LoggerUpdater.checkLoggerIsStarted();
            throw new IllegalStateException(AttrParamValues.UNKNOWN_ACTION.val);
        } catch (Exception e) {
            msg = e.getMessage();
            LOG.warn("{}", msg);
            request.setAttribute(Attrs.MESSAGE.val, msg);
            forwardToJsp(PageJSP.INDEX, request, response);
        }
    }

    private void loadBeans(HttpServletRequest request) {
        request.getSession().setAttribute(Attrs.NODE_ROLES.val, new NodeRolesBean());
        request.getSession().setAttribute(Attrs.THIS_NODE_ROLE_BEAN.val, new ValueBean(NodeRole.UNDEFINED.name()));
        request.getSession().setAttribute(Attrs.MESSAGE_COUNT_INFO_BEAN.val, new MessageCountInfoBean());
//        request.getSession().setAttribute("localName", request.getLocalName());

        LOG.debug("role beans loaded to session  - success");
    }
}

