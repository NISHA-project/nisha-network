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

import java.io.Serializable;

import pl.nask.nisha.commons.network.NodeRole;

public class NodeRolesBean implements Serializable {

    private static final long serialVersionUID = 4639821420562769497L;

    public NodeRole[] getNodeRoles() {
        return NodeRole.values();
    }
}

