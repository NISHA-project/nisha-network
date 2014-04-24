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

import java.util.List;

import org.lightcouch.CouchDbClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.nisha.manager.model.domain.app.CouchDbConnector;
import pl.nask.nisha.manager.model.domain.resources.Article;
import pl.nask.nisha.manager.model.domain.resources.ArticleAnnouncement;

public class ResourceSearch {

    public static final Logger LOG = LoggerFactory.getLogger(ResourceSearch.class);

    public static Article getResourceById(CouchDbClient dbClient, String resourceId) {
        List<Article> articles = dbClient.view(("nisha-resources/articles_by_id")).includeDocs(true).key(resourceId).query(Article.class);
        LOG.debug("articles with id: \"{}\" found: {}", resourceId, articles.size());
        if (articles.size() == 1) {
            return articles.get(0);
        } else {
            String msg = "1 resource with id: " + resourceId + " was expected, but found: " + articles.size();
            LOG.warn("{}", msg);
            throw new IllegalStateException(msg);
        }
    }

    public static ArticleAnnouncement getAnnouncement(String announcementId) {
        String msg;
        List<ArticleAnnouncement> announcements = CouchDbConnector.getCouchDbConnector().announcementDbClient
                .view("nisha-announcements/announcements_by_id")
                .includeDocs(true)
                .key(announcementId)
                .query(ArticleAnnouncement.class);
        LOG.debug("announcements found (by id): {}", announcements.size());
        if (announcements.size() == 1) {
            return announcements.get(0);
        } else {
            msg = "articles found (by id): " + announcements.size();
            throw new IllegalStateException(msg);
        }
    }
}

