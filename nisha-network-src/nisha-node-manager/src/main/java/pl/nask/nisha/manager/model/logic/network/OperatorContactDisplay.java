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
package pl.nask.nisha.manager.model.logic.network;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.nisha.manager.model.domain.app.CouchDbConnector;
import pl.nask.nisha.manager.model.domain.network.OperatorContact;
import pl.nask.nisha.manager.model.logic.local.LocalOperatorUpdater;
import pl.nask.nisha.manager.model.transfer.servletsupport.AttrParamValues;

public class OperatorContactDisplay {

    public static final Logger LOG = LoggerFactory.getLogger(OperatorContactDisplay.class);


    public static OperatorContact findContact(String contextNodeName, String operatorId) {
        List<OperatorContact> contacts = CouchDbConnector.getCouchDbConnector().nodesDbClient.view(("nisha-nodes/contacts_for_node")).includeDocs(true).key(contextNodeName, operatorId).query(OperatorContact.class);
        OperatorContact result;
        if (contacts.size() == 1) {
            result = contacts.get(0);
            LOG.debug("contact found {}", result.toString());
            return result;
        } else {
            String msg = "expected 1 contact [" + contextNodeName + ", " + operatorId + "] but found: " + contacts.size();
            throw new IllegalArgumentException(msg);
        }
    }

    public static List<OperatorContact> findAllContacts() {
        return CouchDbConnector.getCouchDbConnector().nodesDbClient.view(("nisha-nodes/contacts_for_node")).includeDocs(true).query(OperatorContact.class);
    }

    public static void updateOperatorContactsContextNodeName (String nodeName) {
        List<OperatorContact> contactList = findAllContacts();
        for (OperatorContact contact : contactList) {
            contact.setContextNodeName(nodeName);
            CouchDbConnector.getCouchDbConnector().nodesDbClient.update(contact);
        }
        LOG.debug("contacts updated with context node name " + nodeName);
    }

    public static OperatorContact resolveContactLocallyOrGlobally(String option, String operatorId, String contextNodeName) {
        OperatorContact contact;
        if (option.equals(AttrParamValues.LOCAL.val)) {
            LOG.debug("looking for contact locally");
            contact = LocalOperatorUpdater.getContactFromLocalDb(operatorId);
        } else if (option.equals(AttrParamValues.GLOBAL.val)) {
            LOG.debug("looking for contact globally");
            contact = OperatorContactDisplay.findContact(contextNodeName, operatorId);
        } else {
            String msg = "contact search mode - unknown";
            throw new IllegalArgumentException(msg);
        }
        return contact;
    }
}

