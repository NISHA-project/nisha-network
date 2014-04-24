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
package pl.nask.nisha.manager.model.domain.resources;

public class ArticleAnnouncement {

    private String _id;
    private String _rev;

    private AnnouncementType announcementType;
    private String title;
    private String ownerNodeGUID;
    private String language;
    private String description;
    private String articleGUID;
    private AnnouncementStatus status;
    private String portalId;


    public ArticleAnnouncement(Article article, AnnouncementType announcementType, AnnouncementStatus announcementStatus) {
        this.announcementType = announcementType;
        this.title = article.getTitle();
        this.ownerNodeGUID = article.getOwnerNodeGUID();
        this.language = article.getLanguage();
        this.description = article.getDescription();
        this.articleGUID = article.get_id();
        this.portalId = article.getPortalId();
        this.status = announcementStatus;
    }

    public String get_id() {
        return _id;
    }


}

