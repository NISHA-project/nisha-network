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
package pl.nask.nisha.manager.controllers.security;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.nisha.commons.network.NodeRole;
import pl.nask.nisha.commons.node.Operator;
import pl.nask.nisha.commons.security.SaltedPasswordEncryptor;
import pl.nask.nisha.manager.controllers.app.NishaBasicServlet;
import pl.nask.nisha.manager.model.domain.local.NodeConfiguration;
import pl.nask.nisha.manager.model.logic.app.LoggerUpdater;
import pl.nask.nisha.manager.model.logic.local.LocalConfigUpdater;
import pl.nask.nisha.manager.model.logic.local.LocalOperatorUpdater;
import pl.nask.nisha.manager.model.logic.network.NodeSearch;
import pl.nask.nisha.manager.model.logic.security.Authenticator;
import pl.nask.nisha.manager.model.transfer.servletsupport.AttrParamValues;
import pl.nask.nisha.manager.model.transfer.servletsupport.Attrs;
import pl.nask.nisha.manager.model.transfer.servletsupport.PageJSP;
import pl.nask.nisha.manager.model.transfer.servletsupport.Params;
import pl.nask.nisha.manager.model.transfer.supportbeans.OperatorBean;
import pl.nask.nisha.manager.model.transfer.supportbeans.ValueBean;
import pl.nask.nisha.manager.model.logic.security.Authorization;

public class AuthenticateController extends NishaBasicServlet {

    private static final long serialVersionUID = -7549888775391441513L;

    public static final Logger LOG = LoggerFactory.getLogger(AuthenticateController.class);

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
            LoggerUpdater.checkLoggerIsStarted();

            if (request.getParameter(Params.OPTION.val) != null &&
                    request.getParameter(Params.OPTION.val).equals(AttrParamValues.LOGOUT.val)) {

                boolean authorized = Authorization.hasLoggedOperatorAndAnyRole(request, NodeRole.getNameArray());
                LOG.debug("authorization success? {} (operator logout)", authorized);
                processLogoutRequest(request, response);
            }
            else {
                throw new IllegalStateException(AttrParamValues.UNKNOWN_ACTION.val);
            }
        } catch (Exception e) {
            String msg = e.getMessage();
            LOG.warn("{}", msg);
            request.setAttribute(Attrs.MESSAGE.val, msg);
            forwardToJsp(PageJSP.LOGIN, request, response);
        }
    }
    protected void processRequestPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            request.setCharacterEncoding(UTF_8);

            LoggerUpdater.checkLoggerIsStarted();
//            try{
//                LoggerUpdater.checkLoggerIsStarted();
//            } catch (IllegalStateException ex) {
//                LoggerUpdater.enableFileLogging(LoggerUpdater.LOG_FILE_NAME, request);
//            }

            LOG.info("fileLogging: {}", request.getSession().getAttribute(Attrs.FILE_LOGGING.val));
            LOG.info("logging level: {}", request.getSession().getAttribute(Attrs.LOGGER_LEVEL.val));

            if (request.getParameter(Params.LOGIN_SUBMIT.val) != null) {
                //authorization -> all allowed
                processLoginRequest(request, response);
            }
            else {
                throw new IllegalStateException(AttrParamValues.UNKNOWN_ACTION.val);
            }
        } catch (Exception e) {
            String msg = e.getMessage();
            LOG.warn("{}", msg);
            request.setAttribute(Attrs.MESSAGE.val, msg);
            forwardToJsp(PageJSP.LOGIN, request, response);
        }
    }

    private void processLoginRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String msg;
        String login = request.getParameter(Params.OPERATOR_ID.val);
        String password = request.getParameter(Params.OPERATOR_PASSWORD.val);

        if (authenticate(login, password, request)) {
            LOG.info("operator authenticated - success");
            NodeConfiguration config = LocalConfigUpdater.getNodeConfiguration();
            boolean operatorBlocked = LocalConfigUpdater.checkOperatorBlocked(login, config);
            if (operatorBlocked) {
                msg = "operator is blocked";
                request.setAttribute(Attrs.MESSAGE.val, msg);
                forwardToJsp(PageJSP.LOGIN, request, response);
                return;
            }

            String thisNodeRole;
            try {
                thisNodeRole = NodeSearch.getThisNodeRole();
                LOG.debug("thisNodeRole {}", thisNodeRole);
            } catch (Exception e) {
                LOG.debug("couldn't get node role - role set to UNDEFINED");
                thisNodeRole = NodeRole.UNDEFINED.name();
            }
            request.getSession().setAttribute(Attrs.THIS_NODE_ROLE_BEAN.val, new ValueBean(thisNodeRole));
            forwardToJsp(PageJSP.MENU, request, response);
        } else {
            msg = " unknown operator: " + login;
            LOG.info("{}", msg);
            request.setAttribute(Attrs.MESSAGE.val, msg);
            forwardToJsp(PageJSP.LOGIN, request, response);
        }
    }

    protected boolean authenticate(String operatorIdToCheck, String operatorPasswordToCheck, HttpServletRequest request) {
        try {
            Operator operatorFromBase = LocalOperatorUpdater.getOperatorFromLocalDb(operatorIdToCheck);
            String passwordHashToCheck = SaltedPasswordEncryptor.getEncryptedPassword(operatorPasswordToCheck, operatorFromBase.getSalt());
            boolean authenticSuccess = Authenticator.doAuthenticate(operatorIdToCheck, passwordHashToCheck, operatorFromBase);

            if (authenticSuccess) {
                request.getSession().setAttribute(Attrs.LOGGED_OPERATOR_BEAN.val, new OperatorBean(operatorFromBase));
                request.getSession().setAttribute(Attrs.LOCAL_NODE_CONFIGURATION.val, LocalConfigUpdater.getNodeConfiguration());
            }
            return authenticSuccess;
        }
        catch (Exception e) {
            throw new IllegalStateException("unknown operator: " + operatorIdToCheck);
        }
    }

    public static void processLogoutRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String currentOp = LocalOperatorUpdater.getThisNodeLoggedOperator(request).getOperatorId();
        request.getSession().setAttribute(Attrs.LOGGED_OPERATOR_BEAN.val, null);
        LOG.info("{} logged out - success", currentOp);
        forwardToJsp(PageJSP.LOGIN, request, response);
    }
}
