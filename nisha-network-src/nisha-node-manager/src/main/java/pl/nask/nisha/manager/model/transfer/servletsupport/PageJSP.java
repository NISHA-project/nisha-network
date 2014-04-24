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
package pl.nask.nisha.manager.model.transfer.servletsupport;

import java.util.Arrays;
import java.util.List;

public enum PageJSP {

    ALERTS("/alerts.jsp"),
    INDEX("/index.jsp"),
    LOCAL_CONFIG("/localConfig.jsp"),
    LOGIN("/login.jsp"),
    MENU("/menu.jsp"),
    MESSAGE_DETAILS("/messageDetails.jsp"),
    MESSAGE_FORM("/messageForm.jsp"),
    MESSAGES("/messages.jsp"),
    NODE_DETAILS("/nodeDetails.jsp"),
    NODE_FORM("/nodeForm.jsp"),
    OPERATOR_CONTACT("/operatorContact.jsp"),
    OPERATOR_FORM("/operatorForm.jsp"),
    RESOURCES("/resources.jsp"),
    RESOURCE_DETAILS("/resourceDetails.jsp"),
    NODES("/nodes.jsp");

    private PageJSP(String val){
        this.val = val;
    }
    public final String val;

    public static PageJSP getByVal(String val) {
        List<PageJSP> list = Arrays.asList(PageJSP.values());
        for(PageJSP jsp : list) {
            if (jsp.val.equalsIgnoreCase(val)) {
                return jsp;
            }
        }
        throw new IllegalArgumentException(val + " - not found in PageJSP.");
    }
}
