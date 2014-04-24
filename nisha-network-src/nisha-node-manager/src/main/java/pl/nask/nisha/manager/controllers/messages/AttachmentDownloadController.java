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
import java.io.OutputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.nisha.manager.controllers.app.NishaBasicServlet;
import pl.nask.nisha.manager.model.logic.app.LoggerUpdater;
import pl.nask.nisha.manager.model.logic.messages.AttachmentDownloader;
import pl.nask.nisha.manager.model.transfer.servletsupport.AttrParamValues;
import pl.nask.nisha.manager.model.transfer.servletsupport.Attrs;
import pl.nask.nisha.manager.model.transfer.servletsupport.PageJSP;
import pl.nask.nisha.manager.model.transfer.servletsupport.Params;

public class AttachmentDownloadController extends NishaBasicServlet {

    private static final long serialVersionUID = 8605352996963793879L;
    public static final Logger LOG = LoggerFactory.getLogger(AttachmentDownloadController.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequestGet(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequestPost(request, response);
    }

    protected void processRequestGet (HttpServletRequest request, HttpServletResponse response)
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

    protected void processRequestPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setCharacterEncoding(UTF_8);
            LoggerUpdater.checkLoggerIsStarted();
            String attachmentName = request.getParameter(Params.ATTACHMENT_NAME.val);
            if (request.getParameter(Params.DOWNLOAD_ATTACHMENT_SUBMIT.val) != null) {
                processDownloadAttachment(attachmentName, request, response);
            }
            else if (request.getParameter(Params.DISPLAY_ATTACHMENT_SUBMIT.val) != null) {
                processDisplayAttachment(attachmentName, request, response);
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

    private void processDownloadAttachment(String attachmentName, HttpServletRequest request, HttpServletResponse response) throws IOException {

        OutputStream outputStreamToResponse = response.getOutputStream();
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + attachmentName + "\"");
        String messageId = request.getParameter(Params._ID.val);
        String mode = request.getParameter(Attrs.MODE.val);
        AttachmentDownloader.processAttachment(attachmentName, messageId, mode, outputStreamToResponse);
    }

    private void processDisplayAttachment(String attachmentName, HttpServletRequest request, HttpServletResponse response) throws IOException {
        OutputStream outputStreamToResponse = response.getOutputStream();
        String messageId = request.getParameter(Params._ID.val);
        String mode = request.getParameter(Attrs.MODE.val);
        AttachmentDownloader.processAttachment(attachmentName, messageId, mode, outputStreamToResponse);
    }

}

