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
package pl.nask.nisha.manager.model.transfer.supportbeans;

import org.lightcouch.Page;

import pl.nask.nisha.manager.model.domain.messages.Message;

public class MessagePageBean {
    private Page<Message> messagePage;

    //needed to be used as bean in jsp
    public MessagePageBean() {
    }

    public MessagePageBean(Page<Message> page) {
        messagePage = page;
    }

    public Page<Message> getMessagePage() {
        return messagePage;
    }

    public void setMessagePage(Page<Message> messagePage) {
        this.messagePage = messagePage;
    }
}

