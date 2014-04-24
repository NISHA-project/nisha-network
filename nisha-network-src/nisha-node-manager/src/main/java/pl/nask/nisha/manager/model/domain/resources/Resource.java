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

public class Resource {

    private String _id;
    private String _rev;

    private String name;
    private String URL;
    private String accessDate;

    public String get_id() {
        return _id;
    }

    public String get_rev() {
        return _rev;
    }

    public String getName() {
        return name;
    }

    public String getURL() {
        return URL;
    }

    public String getAccessDate() {
        return accessDate;
    }

    @Override
    public String toString() {
        return "Resource{" +
                "_id='" + _id + '\'' +
                ", _rev='" + _rev + '\'' +
                ", name='" + name + '\'' +
                ", URL='" + URL + '\'' +
                ", accessDate='" + accessDate + '\'' +
                '}';
    }
}

