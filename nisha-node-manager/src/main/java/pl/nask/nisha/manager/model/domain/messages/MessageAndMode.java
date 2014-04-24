package pl.nask.nisha.manager.model.domain.messages;

/**
 * ****************************************************************************
 * Copyright (c) 2012 Research and Academic Computer Network.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * <p/>
 * With financial support from the Prevention, Preparedness and Consequence
 * Management of Terrorism and other Security Related Risks Programme
 * European Commission - Directorate-General Home Affairs
 * <p/>
 * Contributors:
 * Research and Academic Computer Network
 * ****************************************************************************
 */

public class MessageAndMode {

    Message message;
    String mode;

    public MessageAndMode(Message message, String mode) {
        this.message = message;
        this.mode = mode;
    }

    public Message getMessage() {
        return message;
    }

    public String getMode() {
        return mode;
    }

    @Override
    public String toString() {
        return "MessageAndMode{" +
                "message=" + message +
                ", mode='" + mode + '\'' +
                '}';
    }
}

