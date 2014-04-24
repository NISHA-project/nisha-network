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
package pl.nask.nisha.manager.model.logic.app;

import javax.servlet.http.HttpServletRequest;

import org.lightcouch.CouchDbClient;
import org.lightcouch.Page;
import org.lightcouch.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.nisha.manager.model.transfer.servletsupport.Attrs;
import pl.nask.nisha.manager.model.transfer.servletsupport.Params;

public class NishaPagination<T>  {

    private static final Logger LOG = LoggerFactory.getLogger(NishaPagination.class);

    public static final int rowsPerPageDefault = 10;
    private Integer rowsPerPage = rowsPerPageDefault;

    public int getValidPageNumber(String pageNumberString, long totalResultsNumber) {
        Integer pageNumber;
        try {
            pageNumber = Integer.parseInt(pageNumberString);
            int lastPageNr =  (int)Math.ceil((double) totalResultsNumber / (double)rowsPerPage);
            if (pageNumber > lastPageNr) {
                pageNumber = lastPageNr;
            }
            else if (pageNumber < 1) {
                pageNumber = 1;
            }
        } catch (Exception e) {
            pageNumber = 1;
        }
        return pageNumber;
    }

    private View prepareView(CouchDbClient client, String viewName, boolean descendingBool, String query) {
        View result = client.view(viewName).descending(descendingBool);
        if (query != null && !query.isEmpty()) {
            if (descendingBool) {
                //in descending order start and end keys must be switched to get proper results
                result = result.startKey(query+"\ufff0").endKey(query);
            } else {
                result = result.startKey(query).endKey(query+"\ufff0");
            }
        }
        return result;
    }

    public Page<T> getPageForPageJumpNumber(String pageNumberString, String viewName, boolean descendingBool, String query,
                                            CouchDbClient client, Class<T> clazz, HttpServletRequest request) {

        if (pageNumberString == null || pageNumberString.isEmpty()) {
            pageNumberString = ""+1;
        }

        View preparedView = prepareView(client, viewName, descendingBool, query);


        Page<T> firstPage = preparedView.queryPage(rowsPerPage, null, clazz);
        int pagesToIgnore = 0;
        int firstPageNumber = firstPage.getPageNumber();
        if (firstPageNumber > 1) {
            pagesToIgnore = firstPageNumber - 1;
            LOG.debug("result first page is " + firstPageNumber + " of all");
        }
        request.setAttribute(Attrs.PAGES_TO_IGNORE.val, pagesToIgnore);

        int pageNumber = getValidPageNumber(pageNumberString, firstPage.getTotalResults());
        if (pageNumber == 1) {
            LOG.info("result page: " + firstPage.getResultList().toString());
            return firstPage;
        }

        Page<T> pageTmp = firstPage;
        String param = firstPage.getNextParam();
        preparedView = prepareView(client, viewName, descendingBool, query);
        for(int i = firstPageNumber + 1; i <= pageNumber; i++) {
            pageTmp = preparedView.queryPage(rowsPerPage, param, clazz);
            param = pageTmp.getNextParam();
        }
        LOG.info("looking for page number: " + pageNumber + " view: " + viewName);

        return pageTmp;
    }

    public void updateRowsPerPage(HttpServletRequest request) {
        String rowsPerPageStr = request.getParameter(Params.ROWS_PER_PAGE.val);
        if(rowsPerPageStr == null) {
            rowsPerPage = rowsPerPageDefault;
        } else {
            rowsPerPage = (Integer.parseInt(rowsPerPageStr));
        }
        request.getSession().setAttribute(Params.ROWS_PER_PAGE.val, rowsPerPage);
    }

    public Integer getRowsPerPage() {
        return rowsPerPage;
    }

}

