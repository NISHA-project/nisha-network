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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.nisha.manager.model.logic.app.LoggerUpdater;
import pl.nask.nisha.manager.model.transfer.servletsupport.AttrParamValues;
import pl.nask.nisha.manager.model.transfer.servletsupport.Attrs;
import pl.nask.nisha.manager.model.transfer.servletsupport.PageJSP;
import pl.nask.nisha.manager.model.transfer.servletsupport.Params;

public class LogoRedirectorController extends NishaBasicServlet {

    private static final long serialVersionUID = -3754388443990585289L;
    public static final Logger LOG = LoggerFactory.getLogger(LogoRedirectorController.class);


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

            //authorization - all allowed
            if (request.getParameter(Params.LOGO_REDIRECT.val) != null) {
                String redirectVal = request.getParameter(Params.LOGO_REDIRECT.val);
                if (!redirectVal.trim().isEmpty()) {
                    PageJSP nextPage = resolveNextPage(redirectVal);
                    if (nextPage.equals(PageJSP.INDEX)) {
                        request.setAttribute(Attrs.LOGGED_OPERATOR_BEAN.val, null);
                    }
                    forwardToJsp(nextPage, request, response);
                } else {
                    throw new IllegalStateException(AttrParamValues.UNKNOWN_ACTION.val);
                }
            } else {
                throw new IllegalStateException(AttrParamValues.UNKNOWN_ACTION.val);
            }
        } catch (Exception e) {
            String msg = e.getMessage();
            LOG.warn("{}", msg);
            request.setAttribute(Attrs.MESSAGE.val, msg);
            forwardToJsp(PageJSP.INDEX, request, response);
        }
    }

    protected void processRequestPost(HttpServletRequest request, HttpServletResponse response)
                throws ServletException, IOException {
        try {
            request.setCharacterEncoding(UTF_8);
            LoggerUpdater.checkLoggerIsStarted();
            throw new IllegalStateException(AttrParamValues.UNKNOWN_ACTION.val);
        } catch (Exception e) {
            String msg = e.getMessage();
            LOG.warn("{}", msg);
            request.setAttribute(Attrs.MESSAGE.val, msg);
            forwardToJsp(PageJSP.INDEX, request, response);
        }
    }
    private PageJSP resolveNextPage(String redirectVal) throws IOException, ServletException {
        if (redirectVal.equals(AttrParamValues.GO_TO_MENU.val)) {
            return PageJSP.MENU;
        }
        else if (redirectVal.equals(AttrParamValues.GO_TO_INDEX.val)) {
            return PageJSP.INDEX;
        }
        else {
            throw new IllegalArgumentException(redirectVal + " - unexpected redirection");
        }
    }

}

