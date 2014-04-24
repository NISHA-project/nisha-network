/**
 * ****************************************************************************
 * Copyright (c) 2012 Research and Academic Computer Network.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * <p/>
 * With financial support from the Prevention, Preparedness and Consequence
 * Management of Terrorism and other Security Related Risks Programme
 * European Commission - Directorate-General Home Affairs
 * <p/>
 * Contributors:
 * Research and Academic Computer Network
 * ****************************************************************************
 */
package pl.nask.nisha.manager.model.logic.app;

import org.junit.Test;

import pl.nask.nisha.manager.model.domain.messages.Message;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class NishaPaginationTest {

    private NishaPagination<Message> messagePagination = new NishaPagination<Message>();

    @Test
    public void getValidPageNumberOkTest(){
        String pageNumberString = "2";
        long totalResultsNumber = 100;
        int allPages = (int)(totalResultsNumber/(long)messagePagination.getRowsPerPage());

        assertTrue(allPages > Integer.parseInt(pageNumberString));
        int expected = 2;

        System.out.println("pagination test - rows per page: " + messagePagination.getRowsPerPage());
        int result = messagePagination.getValidPageNumber(pageNumberString, totalResultsNumber);

        assertEquals(expected, result);
    }

    @Test
    public void getValidPageNumberTooSmallTest(){
        String pageNumberString = "-2";
        long totalResultsNumber = 100;
        int expected = 1;

        System.out.println("pagination test - rows per page: " + messagePagination.getRowsPerPage());
        int result = messagePagination.getValidPageNumber(pageNumberString, totalResultsNumber);

        assertEquals(expected, result);
    }

    @Test
    public void getValidPageNumberTooBigTest(){
        String pageNumberString = "150";
        long totalResultsNumber = 100;
        int allPages = (int)(totalResultsNumber/(long)messagePagination.getRowsPerPage());

        assertTrue(allPages < Integer.parseInt(pageNumberString));
        //expected = allPages;

        System.out.println("pagination test - rows per page: " + messagePagination.getRowsPerPage());
        int result = messagePagination.getValidPageNumber(pageNumberString, totalResultsNumber);

        assertEquals(allPages, result);
        assertTrue(result < Integer.parseInt(pageNumberString));
    }



}

