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
package pl.nask.nisha.manager.model.logic.local;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.nisha.manager.model.domain.local.NodeConfiguration;

public class LocalConfigSearch {

    public static final Logger LOG = LoggerFactory.getLogger(LocalConfigSearch.class);

    public static boolean configWithOperatorsExists(List<NodeConfiguration> configurations) {
        String msg;

        if (configurations.size() == 0) {
            return false;
        } else if (configurations.size() > 1) {
            msg = "configuration undeterminated - " + configurations.size() + " configurations found in nisha-local db";
            throw new IllegalStateException(msg);
        } else if (configurations.size() == 1) {
            LOG.debug("this node configuration exists");
            if (configurations.get(0).getOperatorIdListPermitted().size() > 0) {
                return true;
            } else {
                msg = "configuration exists but no operators registered";
                throw new IllegalStateException(msg);
            }
        }
        return false;
    }
}

