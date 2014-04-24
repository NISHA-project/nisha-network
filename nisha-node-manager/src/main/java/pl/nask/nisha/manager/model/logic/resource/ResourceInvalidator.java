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
package pl.nask.nisha.manager.model.logic.resource;

import org.lightcouch.CouchDbClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.nisha.manager.model.domain.app.CouchDbConnector;
import pl.nask.nisha.manager.model.domain.resources.*;

public class ResourceInvalidator {

    public static final Logger LOG = LoggerFactory.getLogger(ResourceInvalidator.class);

    public static String doInvalidateResource(String resourceId) {
        String resultMessage;
        CouchDbClient localClient = CouchDbConnector.getCouchDbConnector().resourcesDbClient;
        Article articlePrev = ResourceSearch.getResourceById(localClient, resourceId);
        Article articleToInvalid = ResourceSearch.getResourceById(localClient, resourceId);
        articleToInvalid.setStatus(ArticleStatus.REMOVED);
        String announcementId = null;
        try {
            CouchDbConnector.getCouchDbConnector().resourcesDbClient.update(articleToInvalid);
            ArticleAnnouncement announcement = new ArticleAnnouncement(articleToInvalid, AnnouncementType.ARTICLE, AnnouncementStatus.NEW);
            announcementId = announcement.get_id();
            CouchDbConnector.getCouchDbConnector().announcementDbClient.save(announcement);
            resultMessage = articleToInvalid.getTitle() + " - invalidation by supernode - success";
            LOG.info("{}", resultMessage);

        } catch (Exception e) {
            resultMessage = "cannot invalidate article: " + e.getMessage();
            LOG.warn("{}", resultMessage);
            announcementSaveRollback(articlePrev, announcementId);
        }
        return resultMessage;
    }

    public static void announcementSaveRollback(Article artPrev, String announcId) {
        String msg;
        try {
            if (announcId != null) {
                ArticleAnnouncement announcementToDel = ResourceSearch.getAnnouncement(announcId);
                if (announcementToDel != null) {
                    CouchDbConnector.getCouchDbConnector().announcementDbClient.remove(announcementToDel);
                }
            }

            CouchDbConnector.getCouchDbConnector().resourcesDbClient.update(artPrev);
            LOG.debug("resource invalidation rollback - success");
        } catch (Exception e) {
            msg = "resource invalidation rollback failure - " + e.getMessage();
            throw new IllegalStateException(msg);
        }
    }


}

