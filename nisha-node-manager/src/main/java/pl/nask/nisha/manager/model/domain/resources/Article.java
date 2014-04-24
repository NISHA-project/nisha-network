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

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Article {
    private String _id;
    private String _rev;

    private String ownerNodeGUID;
    private String creationDate;
    private String lastChangeDate;
    private Collection<ArticleRelated> related;
    private Collection<ExternalInfo> externalInfo;
    private Collection<String> targetGroups;
    private String userRating;
    private String operatorRating;
    private Collection<String> tags;
    private ArticleStatus status;
    private String title;
    private Collection<String> keywords;
    private String language;
    private Collection<String> authors;
    private ArticleType type;
    private String description;
    private String supervisor;
    private String titleEn;
    private String descriptionEn;
    private String portalId;

    private String translationBase;
    private Collection<Resource> resources;
    private SpecialInfo specialInfo;


    public static final Logger LOG = LoggerFactory.getLogger(Article.class);

    public Article() {
    }

    public Article(String ownerNodeGUID, String title) {
        this.ownerNodeGUID = ownerNodeGUID;
        this.title = title;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Collection<String> getAuthors() {
        return authors;
    }

    public String getOwnerNodeGUID() {
        return ownerNodeGUID;
    }

    public void setOwnerNodeGUID(String ownerNodeGUID) {
        this.ownerNodeGUID = ownerNodeGUID;
    }

    public ArticleStatus getStatus() {
        return status;
    }

    public void setStatus(ArticleStatus status) {
        this.status = status;
    }

    public String getLanguage() {
        return language;
    }

    public String getDescription() {
        return description;
    }

    public String get_rev() {
        return _rev;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public String getLastChangeDate() {
        return lastChangeDate;
    }

    public Collection<ArticleRelated> getRelated() {
        return related;
    }

    public Collection<ExternalInfo> getExternalInfo() {
        return externalInfo;
    }

    public Collection<String> getTargetGroups() {
        return targetGroups;
    }

    public String getUserRating() {
        return userRating;
    }

    public String getOperatorRating() {
        return operatorRating;
    }

    public Collection<String> getTags() {
        return tags;
    }

    public Collection<String> getKeywords() {
        return keywords;
    }

    public ArticleType getType() {
        return type;
    }

    public void setType(ArticleType type) {
        this.type = type;
    }

    public String getSupervisor() {
        return supervisor;
    }

    public String getTranslationBase() {
        return translationBase;
    }

    public Collection<Resource> getResources() {
        return resources;
    }

    public String getTitleEn() {
        return titleEn;
    }

    public void setTitleEn(String titleEn) {
        this.titleEn = titleEn;
    }

    public String getDescriptionEn() {
        return descriptionEn;
    }

    public void setDescriptionEn(String descriptionEn) {
        this.descriptionEn = descriptionEn;
    }

    public String getPortalId() {
        return portalId;
    }

    public void setPortalId(String portalId) {
        this.portalId = portalId;
    }

    public SpecialInfo getSpecialInfo() {
        return specialInfo;
    }

    public void setSpecialInfo(SpecialInfo specialInfo) {
        this.specialInfo = specialInfo;
    }

    @Override
    public String toString() {
        return "Article{" +
                "ownerNodeGUID='" + ownerNodeGUID + '\'' +
                ", creationDate='" + creationDate + '\'' +
                ", lastChangeDate='" + lastChangeDate + '\'' +
                ", related=" + related +
                ", externalInfo=" + externalInfo +
                ", targetGroups=" + targetGroups +
                ", userRating=" + userRating +
                ", operatorRating=" + operatorRating +
                ", tags=" + tags +
                ", status='" + status + '\'' +
                ", title='" + title + '\'' +
                ", keywords=" + keywords +
                ", language='" + language + '\'' +
                ", authors=" + authors +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", supervisor='" + supervisor + '\'' +
                ", titleEn='" + titleEn + '\'' +
                ", descriptionEn='" + descriptionEn + '\'' +
                ", portalId='" + portalId + '\'' +
                ", translationBase='" + translationBase + '\'' +
                ", resources=" + resources +
                ", specialInfo=" + specialInfo +
                '}';
    }

}


