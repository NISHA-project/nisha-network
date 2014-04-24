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

import ch.qos.logback.classic.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.nisha.manager.model.logic.app.LoggerUpdater;
import pl.nask.nisha.manager.model.transfer.servletsupport.AttrParamValues;
import pl.nask.nisha.manager.model.transfer.servletsupport.Attrs;
import pl.nask.nisha.manager.model.transfer.servletsupport.PageJSP;
import pl.nask.nisha.manager.model.transfer.servletsupport.Params;

public class LoggerUpdateController extends NishaBasicServlet {

    private static final long serialVersionUID = -1910076020457224585L;
    public static final Logger LOG = LoggerFactory.getLogger(LoggerUpdateController.class);

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
            throw new IllegalStateException(AttrParamValues.UNKNOWN_ACTION.val);
        } catch (Exception e) {
            msg = e.getMessage();
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
            //no logger allowed
            //authorization - all allowed
            if (request.getParameter(AttrParamValues.LOGGER_LEVEL_SUBMIT.val) != null) {
                processSetLevel(request, response);
            } else {
                throw new IllegalStateException(AttrParamValues.UNKNOWN_ACTION.val);
            }
        } catch (Exception e) {
            msg = e.getMessage();
            LOG.warn("{}", msg);
            request.setAttribute(Attrs.MESSAGE.val, msg);
            forwardToJsp(PageJSP.INDEX, request, response);
        }
    }

    private void processSetLevel(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Object prevLoggerLevel = request.getSession().getAttribute(Attrs.LOGGER_LEVEL.val);
        String newLoggerLevel = request.getParameter(Attrs.LOGGER_LEVEL.val);
        String sourcePage = request.getParameter(Params.SOURCE_PAGE.val);

        if (prevLoggerLevel == null ||
                (prevLoggerLevel instanceof String && !(((String) prevLoggerLevel).equalsIgnoreCase(newLoggerLevel)))) {
            setLoggerLevel(newLoggerLevel, request);
        }
        forwardToJsp(PageJSP.getByVal(sourcePage), request, response);
    }

    public static void setLoggerLevel(String newloggerLevelName, HttpServletRequest request) {

        Level levelToSet = Level.toLevel(newloggerLevelName);
        final Logger rootLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

        if (rootLogger instanceof ch.qos.logback.classic.Logger) {
            ((ch.qos.logback.classic.Logger) rootLogger).setLevel(levelToSet);
            LOG.info("logger level changed to {}", levelToSet);
            request.getSession().setAttribute(Attrs.LOGGER_LEVEL.val, levelToSet.toString());
            request.getSession().setAttribute(Attrs.FILE_LOGGING.val, AttrParamValues.ENABLED.val);
        } else {
            LOG.warn("cannot change logging level of {} at run-time.", rootLogger.getClass().getName());
        }
    }

    public static String resolveCurrentLoggerLevel(HttpServletRequest request, String defaultLevel) {
        String level = (String) request.getSession().getAttribute(Attrs.LOGGER_LEVEL.val);
        if (level == null || level.isEmpty()) {
            return defaultLevel;
        } else {
            return level;
        }
    }
}
