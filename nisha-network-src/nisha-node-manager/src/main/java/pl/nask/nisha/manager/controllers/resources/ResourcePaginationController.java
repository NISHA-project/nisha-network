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
package pl.nask.nisha.manager.controllers.resources;

import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.lightcouch.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.nisha.manager.controllers.app.NishaBasicServlet;
import pl.nask.nisha.manager.model.domain.resources.Article;
import pl.nask.nisha.manager.model.logic.app.LoggerUpdater;
import pl.nask.nisha.manager.model.logic.app.NishaPagination;
import pl.nask.nisha.manager.model.transfer.servletsupport.AttrParamValues;
import pl.nask.nisha.manager.model.transfer.servletsupport.Attrs;
import pl.nask.nisha.manager.model.transfer.servletsupport.PageJSP;
import pl.nask.nisha.manager.model.transfer.servletsupport.Params;
import pl.nask.nisha.manager.model.transfer.supportbeans.ResourcePageBean;
import pl.nask.nisha.manager.model.domain.resources.ResourceSearchContext;

public class ResourcePaginationController extends NishaBasicServlet{

    public static final Logger LOG = LoggerFactory.getLogger(ResourcePaginationController.class);
    private static final long serialVersionUID = -7302983194843143310L;
    private static ResourceSearchContext resourceSearchContext = new ResourceSearchContext();

    private NishaPagination<Article> resourcesNishaPagination = new NishaPagination<Article>();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
           throws ServletException, IOException {
       processRequestGet(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
           throws ServletException, IOException {
       processRequestPost(request, response);
    }

    protected void processRequestGet(HttpServletRequest request, HttpServletResponse response)
                   throws ServletException, IOException {
        String msg;
        try {
            request.setCharacterEncoding(UTF_8);
            LoggerUpdater.checkLoggerIsStarted();

            if (request.getParameter(Params.JUMP_TO_PAGE.val) != null) {
                String pageNumberString = request.getParameter(Params.JUMP_TO_PAGE.val);
                resourceSearchContext.setPageNumberString(pageNumberString);
                processResourceSearch(request, response, false);
            }
            else {
                throw new IllegalStateException(AttrParamValues.UNKNOWN_ACTION.val);
            }
        }
        catch (Exception e) {
           msg = e.getMessage();
           LOG.warn("{}", msg);
           request.setAttribute(Attrs.MESSAGE.val, msg);
           forwardToJsp(PageJSP.MENU, request, response);
        }
    }
    protected void processRequestPost(HttpServletRequest request, HttpServletResponse response)
                   throws ServletException, IOException {
        String msg;
        try {
            request.setCharacterEncoding(UTF_8);
            LoggerUpdater.checkLoggerIsStarted();

            if (request.getParameter(Params.RESOURCE_SEARCH_SUBMIT.val) != null) {
                processResourceSearch(request, response, true);
            }
            else if (request.getParameter(Params.ROWS_PER_PAGE.val) != null) {
                //resize page
                resourcesNishaPagination.updateRowsPerPage(request);
                processResourceSearch(request, response, false);
            }
            else if (request.getParameter(Params.PAGE_NUMBER.val) != null) {
                String pageNumberString = request.getParameter(Params.PAGE_NUMBER.val);
                resourceSearchContext.setPageNumberString(pageNumberString);
                processResourceSearch(request, response, false);
            }
            else {
                throw new IllegalStateException(AttrParamValues.UNKNOWN_ACTION.val);
            }
        }
        catch (Exception e) {
           msg = e.getMessage();
           LOG.warn("{}", msg);
           request.setAttribute(Attrs.MESSAGE.val, msg);
           forwardToJsp(PageJSP.MENU, request, response);
        }
    }

    private void processResourceSearch(HttpServletRequest request, HttpServletResponse response, boolean newSearchCriteria) throws Exception {
        LOG.info("resource pagination start...");

        if (newSearchCriteria){
            resourceSearchContext.loadPropsFromRequest(request);
            resourceSearchContext.setPageNumberString(null);
        }

        Page<Article> resultPage = new Page<Article>();
        try{
            resultPage = resourcesNishaPagination.getPageForPageJumpNumber(resourceSearchContext.getPageNumberString(),
                    resourceSearchContext.getViewName(), false, resourceSearchContext.getQuery(),
                    resourceSearchContext.getClient(), Article.class, request);
        } catch (Exception e) {
            if (e.getMessage().equals("No result was returned by this view query.")) {
                resultPage.setResultList(new ArrayList<Article>());
            } else {
                throw e;
            }
        }
        request.setAttribute(Attrs.RESOURCE_PAGE_BEAN.val, new ResourcePageBean(resultPage));
        request.setAttribute(Params.PAGE_NUMBER.val, resultPage.getPageNumber());
        request.setAttribute(Attrs.RESOURCE_SEARCH_CONTEXT.val, resourceSearchContext);
        request.getSession().setAttribute(Params.ROWS_PER_PAGE.val, "" + resourcesNishaPagination.getRowsPerPage());
        LOG.info("resources to list: " + resultPage.getResultList());
        LOG.info("resource pagination success... cxt: " + request.getAttribute(Attrs.RESOURCE_SEARCH_CONTEXT.val));
        forwardToJsp(PageJSP.RESOURCES, request, response);
    }

}

