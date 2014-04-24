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
import java.util.Enumeration;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.lightcouch.CouchDbClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.nisha.commons.network.NodeRole;
import pl.nask.nisha.commons.node.Operator;
import pl.nask.nisha.commons.security.PasswordValidator;
import pl.nask.nisha.manager.controllers.app.NishaBasicServlet;
import pl.nask.nisha.manager.model.domain.app.CouchDbConnector;
import pl.nask.nisha.manager.model.domain.local.NodeConfiguration;
import pl.nask.nisha.manager.model.domain.network.NodeInfo;
import pl.nask.nisha.manager.model.domain.network.OperatorContact;
import pl.nask.nisha.manager.model.logic.app.LoggerUpdater;
import pl.nask.nisha.manager.model.logic.local.LocalConfigUpdater;
import pl.nask.nisha.manager.model.logic.local.LocalOperatorUpdater;
import pl.nask.nisha.manager.model.logic.network.NodeDisplay;
import pl.nask.nisha.manager.model.logic.network.OperatorContactDisplay;
import pl.nask.nisha.manager.model.transfer.servletsupport.AttrParamValues;
import pl.nask.nisha.manager.model.transfer.servletsupport.Attrs;
import pl.nask.nisha.manager.model.transfer.servletsupport.PageJSP;
import pl.nask.nisha.manager.model.transfer.servletsupport.Params;
import pl.nask.nisha.manager.model.logic.security.Authorization;

public class LocalConfigUpdateController extends NishaBasicServlet {

    private static final long serialVersionUID = 7864064634647916847L;
    public static final Logger LOG = LoggerFactory.getLogger(LocalConfigUpdateController.class);



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

            boolean authorized = Authorization.hasLoggedOperatorAndAnyRole(request, NodeRole.getNameArray());
            LOG.info("authorization success? {} (configuration update)", authorized);

            String option = request.getParameter(Params.OPTION.val);
            if (option != null) {
                if (option.equals(AttrParamValues.UPDATE.val)) {
                    processShowConfig(request, response);
                } else if (option.equals(AttrParamValues.BLOCK_OPERATOR.val)) {
                    processOperatorBlock(request, response);
                } else if (option.equals(AttrParamValues.UNBLOCK_OPERATOR.val)) {
                    processOperatorUnblock(request, response);
                } else {
                    throw new IllegalStateException(AttrParamValues.UNKNOWN_ACTION.val);
                }
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
            throws ServletException, IOException {
        String msg;
        String option = request.getParameter(Params.OPTION.val);

        try {
            request.setCharacterEncoding(UTF_8);
            LoggerUpdater.checkLoggerIsStarted();

            if (request.getParameter(AttrParamValues.SAVE_CONFIG_SUBMIT.val) != null) {
                processConfigFormSubmit(request, response);

            } else if (request.getParameter(AttrParamValues.DEFINE_OPERATOR.val) != null) {
                boolean authorized = Authorization.hasLoggedOperatorAndAnyRole(request, NodeRole.getNameArray());
                LOG.info("authorization success? {} (define new operator)", authorized);

                request.setAttribute(AttrParamValues.DEFINE_OPERATOR.val, AttrParamValues.DEFINE_OPERATOR.val);
                forwardToJsp(PageJSP.OPERATOR_FORM, request, response);
            } else {
                throw new IllegalStateException(AttrParamValues.UNKNOWN_ACTION.val);
            }
        } catch (Exception e) {
            msg = e.getMessage();
            NodeConfiguration configData;
            try {
                configData = LocalConfigUpdater.getNodeConfiguration();
            } catch (Exception ex) {
                boolean doValidate = false;
                configData = loadConfigPropsFromRequest(null, request, doValidate);
            }

            if (option != null && option.equals(AttrParamValues.CONFIGURE.val)) {
                NodeConfiguration config = new NodeConfiguration();
                request.setAttribute(Attrs.LOCAL_NODE_CONFIGURATION.val, config);
                request.setAttribute("operatorIdentificator", request.getParameter("operatorIdentificator"));
                request.setAttribute("operatorPassword", request.getParameter("operatorPassword"));
                request.setAttribute("email", request.getParameter("email"));
            }

            request.setAttribute(Attrs.MESSAGE.val, msg);
            request.setAttribute(Params.OPERATOR_ID.val, request.getParameter(Params.OPERATOR_ID.val));
            request.getSession().setAttribute(Attrs.LOCAL_NODE_CONFIGURATION.val, configData);
            request.getSession().setAttribute(Attrs.OPTION.val, option);
            if (msg != null && !msg.isEmpty()){
                LOG.warn("{}", msg);
            }
            forwardToJsp(PageJSP.LOCAL_CONFIG, request, response);
        }
    }

    private void processShowConfig(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            request.getSession().setAttribute(Attrs.LOCAL_NODE_CONFIGURATION.val, LocalConfigUpdater.getNodeConfiguration());
            request.getSession().setAttribute(Attrs.OPTION.val, AttrParamValues.UPDATE.val);
            forwardToJsp(PageJSP.LOCAL_CONFIG, request, response);

        } catch (IllegalStateException e) {
            LOG.debug("{}", e.getMessage());
            request.setAttribute(Attrs.MESSAGE.val, e.getMessage());
            forwardToJsp(PageJSP.MENU, request, response);
        }
    }

    private void processConfigFormSubmit(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        validateObligatoryFieldsValues(request);
        if (hasNoOperators(request)) {
            processFirstConfigSave(request, response);
        } else {
            processConfigUpdate(request, response);
        }
    }

    private void validateObligatoryFieldsValues(HttpServletRequest request) {
        String msg;

        String domName = request.getParameter(Params.NODE_DOMAIN_NAME_FROM_CONFIG.val);
        String port = request.getParameter(Params.PORT_NUMBER_FROM_CONFIG.val);
        if (domName == null || domName.trim().isEmpty()) {
            msg = "node name must have value";
            LOG.debug("{}", msg);
            throw new IllegalArgumentException(msg);
        }
        if (port == null || port.trim().isEmpty()) {
            msg = "port must have value";
            LOG.debug("{}", msg);
            throw new IllegalArgumentException(msg);
        }
        CouchDbClient nodeToValidateLocalClient = CouchDbConnector.getCouchDbConnector().getCouchDbClientFromNodeDomainName(domName, port, CouchDbConnector.DB_NAME_LOCAL);
        if (nodeToValidateLocalClient == null) {
            msg = "cannot connect to couchDb on node: " + domName + ":" + port;
            LOG.debug("{}", msg);
            throw new IllegalStateException(msg);
        }
        LOG.debug("{} - valid couch client found", nodeToValidateLocalClient.getDBUri() );
    }

    private boolean hasNoOperators(HttpServletRequest request) {
        boolean noOperators = true;
        Enumeration en = request.getParameterNames();
        String nam;
        while (en.hasMoreElements()) {
            nam = en.nextElement() + "";
            if (nam.startsWith(AttrParamValues.OPERATOR_PREFIX.val)) {
                noOperators = false;
                break;
            }
        }
        return noOperators;
    }

    private void processFirstConfigSave(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        boolean authorized = Authorization.hasNoLoggedOperator(request);
        LOG.debug("no logged operator - authorization success? {}", authorized);
        authorized = Authorization.hasThisNodeRoleAnyOf(request, NodeRole.UNDEFINED.name());
        LOG.debug("role authorized success? {}", authorized);
        LOG.info("authorization success? {} (first configuration)", authorized);
        String msg;
        Operator op = prepareOperatorToAddObligatoryProps(request, null);
        if (op == null) {
            msg = "failure - cannot create operator to add to configuration (parameters missing or whitespaces problems)";
            throw new IllegalArgumentException(msg);
        }
        NodeConfiguration config = prepareConfigToSave(request, op);
        if (config == null) {
            msg = "failure - new configuration cannot be null";
            throw new IllegalArgumentException(msg);
        }

        String resultMessage = LocalConfigUpdater.doFirstConfigSave(config, op);
        request.setAttribute(Attrs.MESSAGE.val, resultMessage);
        if (resultMessage.equals(LocalConfigUpdater.CONFIG_SAVE_SUCCESS_MESSAGE)) {
            request.getSession().setAttribute(Attrs.LOCAL_NODE_CONFIGURATION.val, LocalConfigUpdater.getNodeConfiguration());
            request.getSession().setAttribute(Attrs.THIS_NODE_ROLE_BEAN.val, AttrParamValues.UNDEFINED.val);
            forwardToJsp(PageJSP.LOGIN, request, response);
        } else {
            forwardToJsp(PageJSP.INDEX, request, response);
        }
    }

    private NodeConfiguration loadConfigPropsFromRequest(NodeConfiguration config, HttpServletRequest request, boolean doValidate) {
        if (config == null) {
            config = new NodeConfiguration();
        }

        String msg;
        String nodeDomainNameFromConfig = request.getParameter(Params.NODE_DOMAIN_NAME_FROM_CONFIG.val);
        if (doValidate && (nodeDomainNameFromConfig == null || nodeDomainNameFromConfig.trim().isEmpty())) {
            msg = "Null or empty node id";
            throw new IllegalArgumentException(msg);
        }

        String port = request.getParameter(Params.PORT_NUMBER_FROM_CONFIG.val);
        if (doValidate && (port == null || port.trim().isEmpty())) {
            msg = "Null or empty port";
            throw new IllegalArgumentException(msg);
        }

        config.setNodeDomainNameFromConfig(nodeDomainNameFromConfig);
        config.setPortNumberFromConfig(port);
        config.setDescription(request.getParameter(Params.NODE_DESCRIPTION.val));
        config.setLocation(request.getParameter(Params.NODE_LOCATION.val));
        config.set_id(request.getParameter(Params._ID.val));
        config = setCurrentConfigRev(config);   // to avoid update conflicts

        Enumeration en = request.getParameterNames();
        String nam, opId;
        List<String> permittedOperators = config.getOperatorIdListPermitted();
        while (en.hasMoreElements()) {
            nam = en.nextElement() + "";
            if (nam.startsWith("_oper")) {
                opId = nam.substring(5);
                if (!permittedOperators.contains(opId)) {
                    config.getOperatorIdListPermitted().add(opId);
                }
            }
        }

        LOG.debug("configuration loaded");
        return config;
    }

    static Operator prepareOperatorToAddObligatoryProps(HttpServletRequest request, Operator operatorResult) {
        if (operatorResult == null) {
            operatorResult = new Operator();
        }
        String operatorId = request.getParameter(Params.OPERATOR_ID.val);
        String operatorPass = request.getParameter(Params.OPERATOR_PASSWORD.val);
        String email = request.getParameter(Params.OPERATOR_EMAIL.val);
        String contextNodeName = request.getParameter(Params.NODE_DOMAIN_NAME_FROM_CONFIG.val);

        PasswordValidator.validatePassword(operatorPass);

        if (contextNodeName == null || contextNodeName.trim().isEmpty()) {
            contextNodeName = LocalConfigUpdater.getThisNodeDomainNameFromConfig();
        }

        String checkResult = LocalOperatorUpdateController.reportValidOperatorRequestParameters(operatorId, operatorPass, email, contextNodeName);
        if (checkResult.equals(AttrParamValues.OK.val)) {

            operatorResult.setOperatorId(operatorId);
            operatorResult.hashAndSavePassword(operatorPass);
            operatorResult.setEmail(email);
            operatorResult.setContextNodeName(contextNodeName);
            operatorResult.setBlocked(false);
            return operatorResult;
        } else {
            String msg = "operator id, password, email or nodeName is missing or composited of whitespaces";
            LOG.debug("{}", msg);
            return null;
        }
    }

    private NodeConfiguration prepareConfigToSave(HttpServletRequest request, Operator op) {
        boolean doValidate = true;
        NodeConfiguration config = loadConfigPropsFromRequest(null, request, doValidate);
        config.getOperatorIdListPermitted().add(op.getOperatorId());
        if (config.get_id() != null) {
            config.set_id(null);
        }
        if (config.get_rev() != null) {
            config.set_rev(null);
        }
        return config;
    }

    private void processConfigUpdate(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        boolean authorized = Authorization.hasLoggedOperator(request);
        LOG.debug("operator authorized success? {}", authorized);
        authorized = Authorization.hasThisNodeRoleAnyOf(request, NodeRole.getNameArray());
        LOG.debug("role authorized success? {}", authorized);

        LOG.debug("config update start...");
        NodeConfiguration prevConfig = LocalConfigUpdater.getNodeConfiguration();
        NodeConfiguration config = LocalConfigUpdater.getNodeConfiguration();
        boolean doValidate = true;
        config = loadConfigPropsFromRequest(config, request, doValidate);

        String resultMessage = LocalConfigUpdater.doConfigUpdate(prevConfig, config, null);

        request.setAttribute(Attrs.MESSAGE.val, resultMessage);
        if (!resultMessage.equals(LocalConfigUpdater.CONFIG_SAVE_SUCCESS_MESSAGE)) {
            request.setAttribute(Attrs.LOCAL_NODE_CONFIGURATION.val, config);
            forwardToJsp(PageJSP.LOCAL_CONFIG, request, response);
            return;
        }

        String prevNodeDomainName = prevConfig.getNodeDomainNameFromConfig();
        String newNodeDomainName = config.getNodeDomainNameFromConfig();
        String prevPort = prevConfig.getPortNumberFromConfig();
        String newPort = config.getPortNumberFromConfig();

        LocalConfigUpdater.propagateChangesToOperatorsContactsAndRing(prevNodeDomainName, prevPort, newNodeDomainName, newPort);

        request.getSession().setAttribute(Attrs.LOCAL_NODE_CONFIGURATION.val, LocalConfigUpdater.getNodeConfiguration());
        request.getSession().setAttribute(Attrs.OPTION.val, AttrParamValues.UPDATE.val);
        forwardToJsp(PageJSP.LOCAL_CONFIG, request, response);
    }


    private void processOperatorBlock(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String operatorIdToBlock = request.getParameter(Params.BLOCK_OPERATOR_ID.val);
        if (operatorIdToBlock == null) {
            throw new IllegalArgumentException("operator id to block not found");
        }
        String contextNodeName = request.getParameter(Params.NODE_DOMAIN_NAME_FROM_CONFIG.val);
        if (contextNodeName == null) {
            throw new IllegalArgumentException("context node unknown");
        }

        LocalOperatorUpdater.blockOperator(operatorIdToBlock);

        request.getSession().setAttribute(Attrs.LOCAL_NODE_CONFIGURATION.val, LocalConfigUpdater.getNodeConfiguration());
        request.getSession().setAttribute(Attrs.OPTION.val, AttrParamValues.UPDATE.val);
        forwardToJsp(PageJSP.LOCAL_CONFIG, request, response);
    }

    private NodeConfiguration setCurrentConfigRev(NodeConfiguration config) {
        if (config == null) {
            config = new NodeConfiguration();
        } else {
            if (config.get_rev() != null) {
                config.set_rev(LocalConfigUpdater.getNodeConfiguration().get_rev());
            }
        }

        return config;
    }

    private void processOperatorUnblock(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String msg;
        String operatorIdToUnblock = request.getParameter(Params.BLOCK_OPERATOR_ID.val);
        LOG.debug("unblock operator start...");
        LOG.debug("get state before changes");
        NodeConfiguration prevConfig = LocalConfigUpdater.getNodeConfiguration();
        String nodeName = prevConfig.getNodeDomainNameFromConfig();
        NodeInfo prevNodeInfo = NodeDisplay.getNodeInfoByNodeName(nodeName);
        OperatorContact prevContact = OperatorContactDisplay.findContact(nodeName, operatorIdToUnblock);

        if (operatorIdToUnblock == null || operatorIdToUnblock.trim().isEmpty()) {
            msg = "cannot unblock - operator id is null or empty";
            throw new IllegalArgumentException(msg);
        }
        LocalOperatorUpdater.unblockOperator(operatorIdToUnblock, nodeName, prevConfig, prevNodeInfo, prevContact);

        request.getSession().setAttribute(Attrs.LOCAL_NODE_CONFIGURATION.val, LocalConfigUpdater.getNodeConfiguration());
        request.getSession().setAttribute(Attrs.OPTION.val, AttrParamValues.UPDATE.val);
        forwardToJsp(PageJSP.LOCAL_CONFIG, request, response);
    }
}

