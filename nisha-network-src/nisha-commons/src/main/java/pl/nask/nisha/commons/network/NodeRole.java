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
package pl.nask.nisha.commons.network;

import java.util.ArrayList;
import java.util.List;

public enum NodeRole {
    BASICNODE,
    SUPERNODE,
    UNDEFINED;

    public static List<String> getNameList() {
        List<String> rolesNames = new ArrayList<String>();
        for (NodeRole role : NodeRole.values()) {
            rolesNames.add(role.name());
        }
        return rolesNames;
    }

    public static String[] getNameArray () {
        List<String> roleNames = getNameList();
        return roleNames.toArray(new String[roleNames.size()]);
    }

}
