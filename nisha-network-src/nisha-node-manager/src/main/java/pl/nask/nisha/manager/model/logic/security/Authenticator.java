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
package pl.nask.nisha.manager.model.logic.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.nisha.commons.node.Operator;

public class Authenticator {

    public static final Logger LOG = LoggerFactory.getLogger(Authenticator.class);

    public static boolean doAuthenticate(String operatorIdToCheck, String passwordHashToCheck, Operator operatorFromBase) {
        boolean authSuccess = false;
        if (operatorFromBase != null) {
            authSuccess = operatorFromBase.getOperatorId().equals(operatorIdToCheck) &&
                           operatorFromBase.getPasswordHash().equals(passwordHashToCheck);
        }
        LOG.info("credentials match? " + authSuccess);
        return  authSuccess;
    }
}

