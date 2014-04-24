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
package pl.nask.nisha.manager.controllers.messages;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.lightcouch.CouchDbClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.nisha.manager.controllers.app.NishaBasicServlet;
import pl.nask.nisha.manager.model.domain.messages.CastMode;
import pl.nask.nisha.manager.model.domain.messages.Message;
import pl.nask.nisha.manager.model.domain.messages.MessageAndMode;
import pl.nask.nisha.manager.model.domain.messages.MessageState;
import pl.nask.nisha.manager.model.logic.app.LoggerUpdater;
import pl.nask.nisha.manager.model.logic.messages.MessageSearch;
import pl.nask.nisha.manager.model.logic.messages.MessageUpdater;
import pl.nask.nisha.manager.model.transfer.servletsupport.AttrParamValues;
import pl.nask.nisha.manager.model.transfer.servletsupport.Attrs;
import pl.nask.nisha.manager.model.transfer.servletsupport.PageJSP;
import pl.nask.nisha.manager.model.transfer.servletsupport.Params;

public class MessageDisplayController extends NishaBasicServlet{

    private static final long serialVersionUID = 940322646683498227L;
    public static final Logger LOG = LoggerFactory.getLogger(MessageDisplayController.class);


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

            String option = request.getParameter(Params.OPTION.val);
            if (option != null) {
                if (option.equals(Params.MESSAGE_DISPLAY.val)) {
                    processDisplayMessage(request, response, false);
                } else if (option.equals(AttrParamValues.PARENT_MESSAGE_DISPLAY.val)) {
                    processDisplayMessage(request, response, true);
                } else {
                    throw new IllegalStateException(AttrParamValues.UNKNOWN_ACTION.val);
                }
            }
            else {
                throw new IllegalStateException(AttrParamValues.UNKNOWN_ACTION.val);
            }
        } catch (Exception e) {
            String msg = e.getMessage();
            LOG.warn("{}", msg);
            request.setAttribute(Attrs.MESSAGE.val, msg);
            forwardToJsp(PageJSP.MENU, request, response);
        }
    }

    protected void processRequestPost (HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setCharacterEncoding(UTF_8);
            LoggerUpdater.checkLoggerIsStarted();
            throw new IllegalStateException(AttrParamValues.UNKNOWN_ACTION.val);

        } catch (Exception e) {
            String msg = e.getMessage();
            LOG.warn("{}", msg);
            request.setAttribute(Attrs.MESSAGE.val, msg);
            forwardToJsp(PageJSP.MENU, request, response);
        }
    }

    private void processDisplayMessage(HttpServletRequest request, HttpServletResponse response, boolean isParentMessage) throws IOException, ServletException {
        String messageId = request.getParameter(Params._ID.val);
        String mode = request.getParameter(Attrs.MODE.val);

        validateParametersValues(messageId, mode);
        Message message;
        if (isParentMessage) {
            MessageAndMode parentMessageAndMode = MessageSearch.getParentMessage(messageId, mode);
            message = parentMessageAndMode.getMessage();
            mode = parentMessageAndMode.getMode();
        } else {
            message = MessageSearch.findMessageByIdAndMode(messageId, mode);
        }

        markReadIfNeeded(message, mode);
        LOG.info(message.getSubject() + " - display message details");

        String parentMessageId = message.getReferenceId();
        if (parentMessageId != null && !parentMessageId.isEmpty()) {
            MessageAndMode parentMessageAndMode = MessageSearch.getParentMessage(parentMessageId, mode);
            Message parentMessage = parentMessageAndMode.getMessage();
            mode = parentMessageAndMode.getMode();
            request.setAttribute(Attrs.PARENT_MESSAGE_SUBJECT.val, parentMessage.getSubject());
            request.setAttribute(Attrs.REPLY.val, true);
            LOG.debug("REPLY");
        }

        MessageUpdater.prepareMessageBodyToDisplayRows(message);

        request.setAttribute(Attrs.MODE.val, mode);
        request.setAttribute(Attrs.MESSAGE_TO_SHOW.val, message);


        forwardToJsp(PageJSP.MESSAGE_DETAILS, request, response);
    }

    private void validateParametersValues(String messageId, String mode) {
        if (messageId == null || messageId.isEmpty()) {
            throw new IllegalArgumentException("id of message to display cannot be null nor empty");
        }

        if (mode == null || mode.isEmpty()) {
            throw new IllegalArgumentException("mode cannot be null nor empty, allowed are [inbox, outbox, broadcast]");
        }
    }

    private void markReadIfNeeded(Message message, String mode) {
        if (mode.equalsIgnoreCase(CastMode.BROADCAST.name())) {
            MessageUpdater.markBroadcastMessageIfNeeded(message);
        }
        else {
            if (message.getMessageState().equals(MessageState.NEW_MESSAGE)) {
                message.setMessageState(MessageState.READ);
                CouchDbClient client = MessageUpdater.resolveCouchDbClientByMode(mode);
                client.update(message);
                LOG.info("message: " + message.getSubject() + " state updated to " + message.getMessageState());
                return;
            }
            LOG.info(message.getMessageState() + " - message state should not be changed");
        }
    }

}

