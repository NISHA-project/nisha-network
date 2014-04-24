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
package pl.nask.nisha.manager.model.logic.messages;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.lightcouch.CouchDbClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttachmentDownloader {

    public static final Logger LOG = LoggerFactory.getLogger(AttachmentDownloader.class);

    public static void processAttachment(String attachmentName, String messageId, String mode, OutputStream outputStream) throws IOException {
        InputStream attachmentInputStream = null;

        try{
            attachmentInputStream = getAttachmentInputStream(messageId, attachmentName, mode);
            int read;
            int BYTES_DOWNLOAD = 1024;
            byte[] bytes = new byte[BYTES_DOWNLOAD];


            while((read = attachmentInputStream.read(bytes)) != -1){
                outputStream.write(bytes, 0, read);
            }
            outputStream.flush();
            outputStream.close();
        } finally {
            if (attachmentInputStream != null) {
                attachmentInputStream.close();
            }
        }

    }

    public static InputStream getAttachmentInputStream(String messageId, String attachmentName, String mode) {
        CouchDbClient client = MessageUpdater.resolveCouchDbClientByMode(mode);
        LOG.info("downloading attachment: " + attachmentName +  " client: " + client.getDBUri() + " of document: " + messageId);

        return client.find(messageId + "/" + attachmentName);
    }

}

