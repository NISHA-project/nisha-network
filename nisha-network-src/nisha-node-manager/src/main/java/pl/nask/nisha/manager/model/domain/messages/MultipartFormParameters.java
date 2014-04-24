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

import java.util.HashMap;
import java.util.Map;

public class MultipartFormParameters {

    private Map<String, String> stringParametersMap = new HashMap<String, String>();
    private Map<String, AttachmentProperties> attachmentParametersMap = new HashMap<String, AttachmentProperties>();

    public Map<String, String> getStringParametersMap() {
        return stringParametersMap;
    }

    public Map<String, AttachmentProperties> getAttachmentParametersMap() {
        return attachmentParametersMap;
    }
}

