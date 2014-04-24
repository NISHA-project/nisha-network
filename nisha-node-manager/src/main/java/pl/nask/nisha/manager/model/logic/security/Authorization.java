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
package pl.nask.nisha.manager.model.logic.security;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.nisha.commons.network.NodeRole;
import pl.nask.nisha.commons.node.Operator;
import pl.nask.nisha.manager.model.domain.local.NodeConfiguration;
import pl.nask.nisha.manager.model.logic.local.LocalOperatorUpdater;
import pl.nask.nisha.manager.model.transfer.servletsupport.Attrs;
import pl.nask.nisha.manager.model.transfer.supportbeans.OperatorBean;
import pl.nask.nisha.manager.model.transfer.supportbeans.ValueBean;


public class Authorization {

    public static final Logger LOG = LoggerFactory.getLogger(Authorization.class);
    private static String msg;

    public static boolean hasLoggedOperatorAndAnyRole(HttpServletRequest request, String... roles) {
        return hasLoggedOperator(request) && hasThisNodeRoleAnyOf(request, roles);
    }

    public static boolean hasLoggedOperator(HttpServletRequest request) {
        Operator operator = LocalOperatorUpdater.getThisNodeLoggedOperator(request);
        return checkOperator(operator);
    }

    public static boolean hasNoLoggedOperator(HttpServletRequest request) {
        OperatorBean operatorBean = (OperatorBean) request.getSession().getAttribute(Attrs.LOGGED_OPERATOR_BEAN.val);
        return operatorBean.getOperator() == null;
    }

    public static boolean hasThisNodeRoleAnyOf(HttpServletRequest request, String... roles) {
        String thisNodeRole = getValidThisNodeRole(request);
        return matchRoles(thisNodeRole, roles);
    }

    public static boolean hasNameAnyRole(HttpServletRequest request, String nodeName, String... roles) {
            return matchName(request, nodeName) && hasThisNodeRoleAnyOf(request, roles);
    }

    static boolean matchName (HttpServletRequest request, String expectedName) {
        String thisNodeName = getThisNodeName(request);
        return isEqualConfirmed(thisNodeName, expectedName);
    }



    static boolean checkOperator(Operator operator) {
        if (operator != null) {
            String opId = operator.getOperatorId();
            String ctxNode = operator.getContextNodeName();
            if (opId == null || opId.isEmpty()) {
                msg = "authorization fail: operator found in session has no id";
                LOG.debug("{}", msg);
                throw new IllegalStateException(msg);
            }
            if (ctxNode == null || ctxNode.isEmpty()) {
                msg = "authorization fail: operator found in session has no context node";
                LOG.debug("{}", msg);
                throw new IllegalStateException(msg);
            }
            LOG.debug("logged operator found: {}", opId);
            return true;
        } else {
            msg = "authorization fail: no logged operator found";
            LOG.debug("{}", msg);
            throw new IllegalStateException(msg);
        }
    }



    static boolean matchRoles(String thisNodeRole, String... roles) {
        if (thisNodeRole == null) {
            throw new IllegalStateException("authorization fail: no this node role found");
        }
        LOG.debug("this node role {} roles to check -> {}" , thisNodeRole, Arrays.toString(roles));
        for (String role : roles) {
            try {
                if (isEqualConfirmed(thisNodeRole, role)) {
                    return true;
                }
            } catch (IllegalStateException e) {
                //LOG.debug("check next role if possible...");
            }
        }
        throw new IllegalStateException("authorization fail: " + thisNodeRole + " - this node role is not expected");
    }

    static boolean isEqualConfirmed(String thisNodeVal, String expectedVal) {
        if (thisNodeVal.equals(expectedVal)) {
            return true;
        } else {
            throw new IllegalStateException(msg);
        }
    }

    public static String getValidThisNodeRole(HttpServletRequest request) {
        Object obj = request.getSession().getAttribute(Attrs.THIS_NODE_ROLE_BEAN.val);
        if (obj == null) {
            LOG.debug("there is no this node role bean in session");
        } else if (obj instanceof ValueBean) {
            String thisNodeRole = ((ValueBean) obj).getValue();
            if (isValidRoleName(thisNodeRole)) {
                return thisNodeRole;
            }
        } else {
            LOG.debug("no expected attribute in session");
        }
        return null;
    }

    static boolean isValidRoleName(String role) {
        if (role.isEmpty()) {
            LOG.debug("there is no node role");
            return false;
        } else if (!NodeRole.getNameList().contains(role)) {
            LOG.debug("{} - role is not valid NodeRole value", role);
            return false;
        } else {
            LOG.debug("{} - is a valid role", role);
            return true;
        }
    }

    static String getThisNodeName (HttpServletRequest request) {
        Object obj = request.getSession().getAttribute(Attrs.LOCAL_NODE_CONFIGURATION.val);
        if( obj != null && obj instanceof NodeConfiguration) {
            String name = ((NodeConfiguration)obj).getNodeDomainNameFromConfig();
            if (name != null && !name.isEmpty()) {
                return name;
            }
        }
        String msg = "this node name was not found in session";
        LOG.debug("{}", msg);
        throw new IllegalStateException(msg);
    }
}

