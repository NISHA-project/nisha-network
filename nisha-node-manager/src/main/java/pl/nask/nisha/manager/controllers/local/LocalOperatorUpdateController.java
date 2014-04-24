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
package pl.nask.nisha.manager.controllers.local;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.nisha.commons.network.NodeRole;
import pl.nask.nisha.commons.node.Operator;
import pl.nask.nisha.commons.security.PasswordValidator;
import pl.nask.nisha.manager.controllers.app.NishaBasicServlet;
import pl.nask.nisha.manager.model.domain.local.NodeConfiguration;
import pl.nask.nisha.manager.model.logic.app.LoggerUpdater;
import pl.nask.nisha.manager.model.logic.local.LocalConfigUpdater;
import pl.nask.nisha.manager.model.logic.local.LocalOperatorUpdater;
import pl.nask.nisha.manager.model.transfer.servletsupport.AttrParamValues;
import pl.nask.nisha.manager.model.transfer.servletsupport.Attrs;
import pl.nask.nisha.manager.model.transfer.servletsupport.PageJSP;
import pl.nask.nisha.manager.model.transfer.servletsupport.Params;
import pl.nask.nisha.manager.model.transfer.supportbeans.OperatorBean;
import pl.nask.nisha.manager.model.logic.security.Authorization;

public class LocalOperatorUpdateController extends NishaBasicServlet {

    private static final long serialVersionUID = 5472422733793872657L;
    public final static Logger LOG = LoggerFactory.getLogger(LocalOperatorUpdateController.class);


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
            throws ServletException, IOException, IllegalArgumentException {

        String msg;
        try {
            request.setCharacterEncoding(UTF_8);
            LoggerUpdater.checkLoggerIsStarted();

            boolean authorized = Authorization.hasLoggedOperatorAndAnyRole(request, NodeRole.getNameArray());
            LOG.info("authorization success: {} (operator display)", authorized);

            if (request.getParameter(Attrs.SHOW_LOGGED_OPERATOR.val) != null) {
                LOG.info("displaying logged operator");
                forwardToJsp(PageJSP.OPERATOR_FORM, request, response);
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

    protected void processRequestPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, IllegalArgumentException {
        String msg;
        try {
            request.setCharacterEncoding(UTF_8);
            LoggerUpdater.checkLoggerIsStarted();

            boolean authorized = Authorization.hasLoggedOperatorAndAnyRole(request, NodeRole.getNameArray());
            LOG.debug("authorization succes: {} (operator update)", authorized);

            if (request.getParameter(Params.UPDATE_OPERATOR_SUBMIT.val) != null) {
                processUpdateOperator(request, response);
            } else if (request.getParameter(AttrParamValues.ADD_OPERATOR.val) != null) {
                LOG.debug("add operator start...");
                addOperatorToLocalConfig(request, response);
            } else {
                throw new IllegalStateException(AttrParamValues.UNKNOWN_ACTION.val);
            }
        } catch (Exception e) {
            msg = e.getMessage();
            if (!msg.isEmpty()){
                LOG.warn("{}", msg);
            }
            request.setAttribute(Attrs.MESSAGE.val, msg);
            forwardToJsp(PageJSP.MENU, request, response);
        }
    }

    private void processUpdateOperator(HttpServletRequest request, HttpServletResponse response) throws IllegalArgumentException,
            IOException, ServletException {

        LOG.debug("operator update start...");
        String operatorId = request.getParameter(Params.OPERATOR_ID.val);
        antiTamperVerification(operatorId, request);

        String msg;
        if (operatorId == null || operatorId.trim().isEmpty()) {
            msg = "operator to update id unknown";
            throw new IllegalArgumentException(msg);
        }

        Operator prevOperator = LocalOperatorUpdater.getOperatorFromLocalDb(operatorId);
        Operator operatorToUpdate = LocalOperatorUpdater.getOperatorFromLocalDb(operatorId);
        if (prevOperator == null) {
            msg = operatorId + " - operator not found in local db";
            throw new IllegalArgumentException(msg);
        }
        try {
            operatorToUpdate = loadOperatorPropsFromReq(operatorToUpdate, request, AttrParamValues.UPDATE.val);
        } catch (Exception e) {
            msg = e.getMessage();
            request.setAttribute(Attrs.MESSAGE.val, msg);
            request.setAttribute(Attrs.SHOW_LOGGED_OPERATOR.val, true);
            forwardToJsp(PageJSP.OPERATOR_FORM, request, response);
            return;
        }
        if (operatorToUpdate == null) {
            request.setAttribute(Attrs.SHOW_LOGGED_OPERATOR.val, true);
            request.setAttribute(Attrs.MESSAGE.val, "cannot update operator - parameters constraints violation");
            forwardToJsp(PageJSP.OPERATOR_FORM, request, response);
            return;
        }

        if (prevOperator.equals(operatorToUpdate)) {
            msg = "no change - no need to update operator";
            LOG.info("{}", msg);
            msg = "";
        } else {
            msg = LocalOperatorUpdater.doUpdateOperator(operatorToUpdate);
        }
        request.setAttribute(Attrs.MESSAGE.val, msg);
        request.getSession().setAttribute(Attrs.LOGGED_OPERATOR_BEAN.val, new OperatorBean(operatorToUpdate));
        request.setAttribute(Attrs.SHOW_LOGGED_OPERATOR.val, true);
        forwardToJsp(PageJSP.OPERATOR_FORM, request, response);
    }

    private void antiTamperVerification(String operatorToCheck, HttpServletRequest request) {
        String msg;
        String loggedOperatorId = LocalOperatorUpdater.getThisNodeLoggedOperator(request).getOperatorId();
        if (! operatorToCheck.equals(loggedOperatorId)) {
            msg = "modified operator (" + operatorToCheck + ") is not logged operator (" + loggedOperatorId + ") - tampering probability - failure";
            throw new IllegalStateException(msg);
        }
        msg = "modified operator is the logged one (" + operatorToCheck + ")";
        LOG.warn("{}", msg);
    }

    private Operator loadOperatorPropsFromReq(Operator operatorResult, HttpServletRequest request, String addUpdateOption) throws IllegalArgumentException, IOException, ServletException {
        String msg;
        if (!addUpdateOption.equals(AttrParamValues.ADD.val) && !addUpdateOption.equals(AttrParamValues.UPDATE.val)) {
            msg = addUpdateOption + " - option unknown - cannot load operator details";
            throw new IllegalArgumentException(msg);
        }

        if (operatorResult == null && addUpdateOption.equals(AttrParamValues.UPDATE.val)) {
            msg = "no operator for update";
            throw new IllegalArgumentException(msg);
        }

        if (operatorResult == null && addUpdateOption.equals(AttrParamValues.ADD.val)) {
            operatorResult = new Operator();
        }

        if (operatorResult == null) {
            msg = "operator cannot be null";
            throw new IllegalArgumentException(msg);
        }

        operatorResult.setOperatorId(request.getParameter(Params.OPERATOR_ID.val));
        operatorResult.setEmail(request.getParameter(Params.OPERATOR_EMAIL.val));

        operatorResult.setFullName(request.getParameter(Params.OPERATOR_FULL_NAME.val));
        operatorResult.setTelephone(request.getParameter(Params.OPERATOR_TELEPHONE.val));
        operatorResult.setCertificate(request.getParameter(Params.CERTYFICATE.val));
        operatorResult.setPrivateKey(request.getParameter(Params.OPERATOR_PRIVATE_KEY.val));
        operatorResult.setContextNodeName(LocalConfigUpdater.getThisNodeDomainNameFromConfig());

        String pass, pass_repeat, passCheckResult;
        if (addUpdateOption.equals(AttrParamValues.ADD.val)) {
            pass = request.getParameter(Params.OPERATOR_PASSWORD.val);

            PasswordValidator.validatePassword(pass);

            //if (pass != null && !pass.trim().isEmpty()) {
            passCheckResult = reportValidOperatorRequestParameters(pass);
            if (passCheckResult.equals(AttrParamValues.OK.val)) {
                operatorResult.hashAndSavePassword(pass);
            } else {
                LOG.debug("{}", passCheckResult);
                request.setAttribute(Attrs.MESSAGE.val, passCheckResult);
                return null;
            }
        } else if (addUpdateOption.equals(AttrParamValues.UPDATE.val)) {
            pass = request.getParameter(Params.OPERATOR_NEW_PASSWORD.val);
            pass_repeat = request.getParameter(Params.OPERATOR_NEW_PASSWORD_2.val);

            if (pass != null && !pass.trim().isEmpty()) {
                passCheckResult = reportValidOperatorRequestParameters(pass, pass_repeat);
                if (! passCheckResult.equals(AttrParamValues.OK.val)) {
                    msg = "parameters constraints violation";
                    LOG.debug("{}", msg);
                    return null;
                }

                request.setAttribute(Attrs.MESSAGE.val, passCheckResult);

                if (!pass.equals(pass_repeat)) {
                    msg = "Both passwords must be identical.";
                    LOG.debug("{}", msg);
                    request.setAttribute(Attrs.MESSAGE.val, msg);
                    return null;
                }

                PasswordValidator.validatePassword(pass);

                operatorResult.hashAndSavePassword(pass);
                LOG.info("operator password change");
            }

        }


        String checkResult = reportValidOperatorRequestParameters(operatorResult.getOperatorId(), operatorResult.getEmail(),
                operatorResult.getContextNodeName());
        if (!checkResult.equals(AttrParamValues.OK.val)) {
            LOG.debug("{}", checkResult);
            throw new IllegalArgumentException(checkResult);
        }

        LOG.debug("loaded {}", operatorResult.toStringVerbose());
        return operatorResult;

    }

    private Operator loadParamsNoValid (HttpServletRequest request) {
        Operator operatorResult = new Operator();
        operatorResult.setOperatorId(request.getParameter(Params.OPERATOR_ID.val));
        operatorResult.setEmail(request.getParameter(Params.OPERATOR_EMAIL.val));
        operatorResult.setFullName(request.getParameter(Params.OPERATOR_FULL_NAME.val));
        operatorResult.setTelephone(request.getParameter(Params.OPERATOR_TELEPHONE.val));
        operatorResult.setCertificate(request.getParameter(Params.CERTYFICATE.val));
        operatorResult.setPrivateKey(request.getParameter(Params.OPERATOR_PRIVATE_KEY.val));
        operatorResult.setContextNodeName(LocalConfigUpdater.getThisNodeDomainNameFromConfig());
        LOG.debug("just loaded: {}", operatorResult);
        return operatorResult;
    }

    private void addOperatorToLocalConfig(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String msg;
        Operator newOperator;
        try {
            newOperator = loadOperatorPropsFromReq(new Operator(), request, AttrParamValues.ADD.val);
            if(newOperator == null) {
                throw new IllegalStateException("operator values problem (missing or whitespaces)");
            }
            newOperator.setBlocked(false);
            LocalOperatorUpdater.checkOperatorHasCriticalProps(newOperator);
        } catch (Exception e) {
            msg = e.getMessage();
            LOG.warn("{}", msg);
            request.setAttribute(Attrs.MESSAGE.val, msg);
            request.setAttribute(Attrs.OPERATOR_TO_SHOW.val, loadParamsNoValid(request));
            request.setAttribute(AttrParamValues.DEFINE_OPERATOR.val, AttrParamValues.DEFINE_OPERATOR.val);
            forwardToJsp(PageJSP.OPERATOR_FORM, request, response);
            return;
        }

        NodeConfiguration prevConfig = LocalConfigUpdater.getNodeConfiguration();
        NodeConfiguration config = LocalConfigUpdater.getNodeConfiguration();

        String resultMessage = LocalConfigUpdater.doConfigUpdate(prevConfig, config, newOperator);

        request.setAttribute(Attrs.MESSAGE.val, resultMessage);
        request.setAttribute(Attrs.OPTION.val, AttrParamValues.UPDATE.val);
        request.setAttribute(Attrs.LOCAL_NODE_CONFIGURATION.val, LocalConfigUpdater.getNodeConfiguration());
        forwardToJsp(PageJSP.LOCAL_CONFIG, request, response);


    }

    public static String reportValidOperatorRequestParameters(String... opParams) {
        String intro = "Failure - found values:";
        for (String param : opParams) {
            if (param == null) return  intro +"  null";
            if (param.isEmpty()) return intro + " empty";
            if (param.trim().isEmpty())
                return intro + " composited of whitespaces only";
            if (!param.trim().equals(param))
                return intro + " that starts or end with whitespaces";
        }
        return AttrParamValues.OK.val;
    }
}

