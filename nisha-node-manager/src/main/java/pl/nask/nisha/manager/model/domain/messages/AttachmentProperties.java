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
package pl.nask.nisha.manager.model.domain.messages;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.lightcouch.Attachment;
import org.lightcouch.CouchDbClient;

public class AttachmentProperties {
    private String attachName;
    private String contentType;
    private InputStream inputStream;

    public AttachmentProperties(String attachName, String contentType, InputStream inputStream) {
        this.attachName = attachName;
        this.contentType = contentType;
        this.inputStream = inputStream;
    }

    public AttachmentProperties(String attachName, Attachment attachment, String messageId, CouchDbClient client) {

        this.attachName = attachName;
        this.contentType = attachment.getContentType();
        this.inputStream = client.find(messageId + "/" + attachName);
    }


    public static Map<String, AttachmentProperties> getAttachmentsPropertiesMap(Message message, CouchDbClient client) {
        Map<String, AttachmentProperties> result = new HashMap<String, AttachmentProperties>();
        Attachment attachment;
        AttachmentProperties attachmentProperties;
        Map<String, Attachment> messageAttachments = message.getAttachments();
        String messageId = message.get_id();
        for (String key : messageAttachments.keySet()) {
            attachment = messageAttachments.get(key);
            attachmentProperties = new AttachmentProperties(key, attachment, messageId, client);
            result.put(key, attachmentProperties);
        }
        return result;
    }


    public String getAttachName() {
        return attachName;
    }

    public String getContentType() {
        return contentType;
    }
    public InputStream getInputStream() {
        return inputStream;
    }
}

