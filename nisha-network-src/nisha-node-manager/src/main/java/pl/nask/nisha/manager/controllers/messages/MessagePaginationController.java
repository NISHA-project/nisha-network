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
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.lightcouch.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.nisha.manager.controllers.app.NishaBasicServlet;
import pl.nask.nisha.manager.model.domain.messages.Message;
import pl.nask.nisha.manager.model.domain.messages.MessageDisplayMode;
import pl.nask.nisha.manager.model.domain.messages.MessageSearchContext;
import pl.nask.nisha.manager.model.domain.messages.MessageState;
import pl.nask.nisha.manager.model.logic.app.LoggerUpdater;
import pl.nask.nisha.manager.model.logic.app.NishaPagination;
import pl.nask.nisha.manager.model.logic.messages.MessageUpdater;
import pl.nask.nisha.manager.model.transfer.servletsupport.AttrParamValues;
import pl.nask.nisha.manager.model.transfer.servletsupport.Attrs;
import pl.nask.nisha.manager.model.transfer.servletsupport.PageJSP;
import pl.nask.nisha.manager.model.transfer.servletsupport.Params;
import pl.nask.nisha.manager.model.transfer.supportbeans.MessagePageBean;
import pl.nask.nisha.manager.model.transfer.supportbeans.ValueBean;

public class MessagePaginationController extends NishaBasicServlet{

    public static final Logger LOG = LoggerFactory.getLogger(MessagePaginationController.class);
    private static final long serialVersionUID = -4182966653430941965L;

    public static final MessageDisplayMode messageDisplayModeDefault = MessageDisplayMode.NOT_ARCHIVED;
    public static MessageDisplayMode messageDisplayMode = messageDisplayModeDefault;
    private static MessageSearchContext messageSearchContext = new MessageSearchContext();
    private static NishaPagination<Message> messageNishaPagination = new NishaPagination<Message>();

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

            String mode = request.getParameter(Attrs.MODE.val);
            if (mode != null){
                messageSearchContext.loadCurrentContext(mode, messageDisplayMode);
                if (mode.equals(AttrParamValues.OUTBOX.val) || mode.equals(AttrParamValues.INBOX.val) ||
                        mode.equals(AttrParamValues.BROADCAST.val) ) {
                    String pageNumberString = request.getParameter(Params.JUMP_TO_PAGE.val);
                    processListMessages(pageNumberString, request, response);
                }
                else {
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

            String pageNumberString = request.getParameter(Params.PAGE_NUMBER.val);
            if (request.getParameter(Params.ROWS_PER_PAGE.val) != null) {
                messageNishaPagination.updateRowsPerPage(request);
                pageNumberString = request.getParameter(Params.JUMP_TO_PAGE.val);
            }

            String mode = request.getParameter(Attrs.MODE.val);
            if (mode != null && !mode.isEmpty()) {
                messageSearchContext.loadCurrentContext(mode, messageDisplayMode);

                String refreshClicked = request.getParameter(Params.REFRESH_SUBMIT.val);
                if (refreshClicked != null || (pageNumberString != null && !pageNumberString.isEmpty())) {
                    processListMessages(pageNumberString, request, response);
                }
                else if (request.getParameter(Params.SHOW_HIDE_SUBMIT.val) != null) {
                    processShowHideSubmit(request, response);
                }
                else if (request.getParameter(Params.ARCHIVE_SUBMIT.val) != null) {
                    processChangeStateRequest(MessageState.ARCHIVED, false, request, response);
                }
                else if (request.getParameter(Params.UNDO_ARCHIVE_SUBMIT.val) != null) {
                    processChangeStateRequest(null, true, request, response);
                }
                else if (request.getParameter(Params.MARK_READ_SUBMIT.val) != null) {
                    processChangeStateRequest(MessageState.READ, false, request, response);
                }
                else if (request.getParameter(Params.UNDO_READ_SUBMIT.val) != null) {
                    processChangeStateRequest(MessageState.NEW_MESSAGE, false, request, response);
                }
                else if (request.getParameter(Params.MARK_DONE_SUBMIT.val) != null) {
                    processChangeStateRequest(MessageState.DONE, false, request, response);
                }
                else if (request.getParameter(Params.UNDO_DONE_SUBMIT.val) != null) {
                    processChangeStateRequest(MessageState.READ, false, request, response);
                }
                else {
                    throw new IllegalStateException(AttrParamValues.UNKNOWN_ACTION.val);
                }
            } else {
                throw new IllegalStateException(AttrParamValues.UNKNOWN_ACTION.val);
            }

        }  catch (Exception e) {
            String msg = e.getMessage();
            LOG.warn("{}", msg);
            request.setAttribute(Attrs.MESSAGE.val, msg);
            forwardToJsp(PageJSP.MENU, request, response);
        }
    }

    private void processChangeStateRequest(MessageState newMessageState, boolean undoArchive,
                                           HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        //if undoArchive is true newMessageState is ignored because it must be resolved anyway
        try{
            List<String> checkedMessagesIdList = getCheckedMessageIdList(request);
            if (undoArchive){
                MessageUpdater.updateStateUndoArchive(checkedMessagesIdList, messageSearchContext, request);
            } else {
                MessageUpdater.updateState(checkedMessagesIdList, messageSearchContext, newMessageState, request);
            }

        } catch (IllegalArgumentException e) {
            request.setAttribute(Attrs.MESSAGE.val, e.getMessage());
        } finally {
            processListMessages("" + 1, request, response);
        }
    }

    private static void processListMessages(String pageNumberString, HttpServletRequest request, HttpServletResponse response)
               throws IOException, ServletException {

        Page<Message> messagesPage = new Page<Message>();
        try{
            messagesPage = messageNishaPagination.getPageForPageJumpNumber(pageNumberString, messageSearchContext.getContextDisplayViewName(),
                    true, null, messageSearchContext.getContextClient(), Message.class, request);
        } catch (Exception e) {
            if (e.getMessage().equals("No result was returned by this view query.")) {
                messagesPage.setResultList(new ArrayList<Message>());

            }
        }
        request.setAttribute(Attrs.MESSAGE_PAGE_BEAN.val, new MessagePageBean(messagesPage));
        request.setAttribute(Params.PAGE_NUMBER.val, messagesPage.getPageNumber());
        request.setAttribute(Attrs.MODE.val, messageSearchContext.getContextMode());
        request.getSession().setAttribute(Params.ROWS_PER_PAGE.val, "" + messageNishaPagination.getRowsPerPage());
        request.setAttribute(Params.MESSAGE_DISPLAY_MODE_BEAN.val, new ValueBean(messageDisplayMode.name()));
        forwardToJsp(PageJSP.MESSAGES, request, response);
    }

    private List<String> getCheckedMessageIdList(HttpServletRequest request) {
        List<String> checkedMessagesIdList = new ArrayList<String>();
        String id, checkboxResult;
        for (int i = 0; i < messageNishaPagination.getRowsPerPage(); i++) {
            id = request.getParameter("row" + i);
            checkboxResult = request.getParameter("checkbox_" + id);

            if (checkboxResult != null && checkboxResult.equals("on")) {
                checkedMessagesIdList.add(id);
            }
        }
        anyMessageChecked(checkedMessagesIdList);
        return checkedMessagesIdList;
    }

    public static void anyMessageChecked(List<String> checkedMessagesIdList) {
        if (checkedMessagesIdList == null || checkedMessagesIdList.isEmpty()) {
            throw new IllegalArgumentException("no message checked");
        }
        LOG.info("chosen messages: " + checkedMessagesIdList.size());
    }

    private void processShowHideSubmit(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        switchMessageDisplayMode();
        processListMessages("" + 1, request, response);
    }

    private void switchMessageDisplayMode() {
        if (messageDisplayMode.equals(MessageDisplayMode.ALL)) {
            messageDisplayMode = MessageDisplayMode.NOT_ARCHIVED;
        } else if (messageDisplayMode.equals(MessageDisplayMode.NOT_ARCHIVED)) {
            messageDisplayMode = MessageDisplayMode.ALL;
        }
        messageSearchContext.adaptViewNameToMessageDisplayMode(messageDisplayMode);
    }
}
