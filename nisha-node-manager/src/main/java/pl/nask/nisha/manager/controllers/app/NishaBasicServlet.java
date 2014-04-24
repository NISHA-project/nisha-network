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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.nisha.manager.model.logic.messages.MessageCounter;
import pl.nask.nisha.manager.model.logic.network.NodeSearch;
import pl.nask.nisha.manager.model.transfer.servletsupport.Attrs;
import pl.nask.nisha.manager.model.transfer.servletsupport.PageJSP;
import pl.nask.nisha.manager.model.transfer.supportbeans.MessageCountInfoBean;
import pl.nask.nisha.manager.model.transfer.supportbeans.ValueBean;

public class NishaBasicServlet extends HttpServlet {

    private static final long serialVersionUID = -7624374539775202610L;
    public static final String UTF_8 = "UTF-8";
    private static ValueBean thisNodeRoleBean = new ValueBean();
    public static final Logger LOG = LoggerFactory.getLogger(NishaBasicServlet.class);
    public static boolean roleDiscovered = false;
    private static MessageCountInfoBean messageCountInfoBean = new MessageCountInfoBean();

    public static void forwardToJsp (PageJSP targetJSP, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!roleDiscovered) {
            LOG.debug("role dicsovering...");
            String thisNodeRole;
            try{
                thisNodeRole = NodeSearch.getThisNodeRole();
                LOG.info("role discovered: {}", thisNodeRole);
                roleDiscovered = true;
            } catch (Exception e) {
                thisNodeRole = "";
                LOG.info("role undefined");
            }
            thisNodeRoleBean.setValue(thisNodeRole);
            request.getSession().setAttribute(Attrs.THIS_NODE_ROLE_BEAN.val, thisNodeRoleBean);
        }

        updateMessagesCount(request, targetJSP);

        LOG.info("forward to ------> {}", targetJSP.val);
        request.getRequestDispatcher(targetJSP.val).forward(request, response);
    }

    private static void updateMessagesCount(HttpServletRequest request, PageJSP targetJSP) {
        if (!targetJSP.equals(PageJSP.INDEX) && !targetJSP.equals(PageJSP.LOGIN)) {
            try {
                messageCountInfoBean =  MessageCounter.getUpdatedMessageCountBean();
            } catch (Exception e) {
                LOG.info("unread messages cannot be counted " + e.getClass().getName() + " " + e.getMessage());
            }
            request.getSession().setAttribute(Attrs.MESSAGE_COUNT_INFO_BEAN.val, messageCountInfoBean);
        }
    }
}

