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

public class ArticleRelated {

    private String docGUID;
    private String docREV;

    public ArticleRelated() {
    }

    public ArticleRelated(String docGUID, String docREV) {
        this.docGUID = docGUID;
        this.docREV = docREV;
    }

    public String getDocGUID() {
        return docGUID;
    }

    public void setDocGUID(String docGUID) {
        this.docGUID = docGUID;
    }

    public String getDocREV() {
        return docREV;
    }

    public void setDocREV(String docREV) {
        this.docREV = docREV;
    }

    @Override
    public String toString() {
        return "ArticleRelated{" +
                "docGUID='" + docGUID + '\'' +
                ", docREV='" + docREV + '\'' +
                '}';
    }
}

