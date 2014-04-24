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

import javax.servlet.http.HttpServletRequest;

import org.lightcouch.CouchDbClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.nisha.manager.model.domain.app.CouchDbConnector;
import pl.nask.nisha.manager.model.logic.network.NodeSearch;
import pl.nask.nisha.manager.model.transfer.servletsupport.AttrParamValues;
import pl.nask.nisha.manager.model.transfer.servletsupport.Params;

public class ResourceSearchContext {

    private final static Logger LOG = LoggerFactory.getLogger(ResourceSearchContext.class);

    private String query;
    private String queryType;
    private String searchRange;
    private String pageNumberString;
    private String viewName;
    private CouchDbClient client;


    public void loadPropsFromRequest(HttpServletRequest request) {

        query = overrideParamIfNotNull(Params.RESOURCE_QUERY.val, query, request);
        queryType = overrideParamIfNotNull(Params.QUERY_TYPE.val, queryType, request);
        searchRange = overrideParamIfNotNull(Params.SEARCH_RANGE.val, searchRange, request);
        pageNumberString = overrideParamIfNotNull(Params.JUMP_TO_PAGE.val, pageNumberString, request);
        viewName = resolveViewName();
        client = CouchDbConnector.resolveResourceCouchDbClient(searchRange.equals(AttrParamValues.GLOBAL.val),
                NodeSearch.getThisNodeRole());
        LOG.info("resource search - range: " + searchRange + " query type: " + queryType + " query: " + query +
                    " jump to page: " + pageNumberString + " viewName: " + viewName);
    }

    private String overrideParamIfNotNull(String paramName, String prevValue, HttpServletRequest request) {
        String paramVal = request.getParameter(paramName);
        if (paramVal == null) {
            return prevValue;
        } else {
            return paramVal;
        }
    }

    private String resolveViewName() {
        String result;
        if (queryType.equals(AttrParamValues.ID.val)) {
            result = CouchDbConnector.DB_NAME_RESOURCES + CouchDbConnector.VIEW_RESOURCE_BY_ID;
        }
        else if (queryType.equals(AttrParamValues.TITLE.val)) {
            result = CouchDbConnector.DB_NAME_RESOURCES + CouchDbConnector.VIEW_RESOURCE_BY_TITLE;
        }
        else {
            throw new IllegalStateException(queryType + " - unexpected query type");
        }
        return result;
    }

    public String getQuery() {
        return query;
    }

    public String getQueryType() {
        return queryType;
    }

    public String getSearchRange() {
        return searchRange;
    }

    public String getPageNumberString() {
        return pageNumberString;
    }

    public void setPageNumberString(String pageNumberString) {
        this.pageNumberString = pageNumberString;
    }

    public String getViewName() {
        return viewName;
    }

    public CouchDbClient getClient() {
        return client;
    }

    @Override
    public String toString() {
        String clientInfo = "null";
        if (client != null) {
            clientInfo = client.getDBUri().toString();
        }
        return "ResourceSearchContext{" +
                "query='" + query + '\'' +
                ", queryType='" + queryType + '\'' +
                ", searchRange='" + searchRange + '\'' +
                ", pageNumberString='" + pageNumberString + '\'' +
                ", viewName='" + viewName + '\'' +
                ", client=" + clientInfo +
                '}';
    }
}

