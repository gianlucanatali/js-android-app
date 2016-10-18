/*
 * Copyright © 2015 TIBCO Software, Inc. All rights reserved.
 * http://community.jaspersoft.com/project/jaspermobile-android
 *
 * Unless you have purchased a commercial license agreement from TIBCO Jaspersoft,
 * the following license terms apply:
 *
 * This program is part of TIBCO Jaspersoft Mobile for Android.
 *
 * TIBCO Jaspersoft Mobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TIBCO Jaspersoft Mobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with TIBCO Jaspersoft Mobile for Android. If not, see
 * <http://www.gnu.org/licenses/lgpl>.
 */

package com.jaspersoft.android.jaspermobile.support.page;

import android.view.View;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.support.matcher.WatchPeriod;

import org.hamcrest.Matcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.jaspersoft.android.jaspermobile.support.matcher.AdditionalViewAction.scrollToPage;
import static com.jaspersoft.android.jaspermobile.support.matcher.AdditionalViewAction.watch;


/**
 * @author Andrew Tivodar
 * @since 2.5
 */
public class ReportPaginationPageObject extends PageObject {

    public void currentMatches(Matcher<View> reportMatcher) {
        onView(withId(R.id.currentPageLabel))
                .perform(watch(reportMatcher, WatchPeriod.MEDIUM));
    }

    public void totalMatches(Matcher<View> reportMatcher) {
        onView(withId(R.id.totalPageLabel))
                .perform(watch(reportMatcher, WatchPeriod.MEDIUM));
    }

    public void clickNextPage(){
        onView(withId(R.id.nextPage))
                .perform(click());
    }

    public void clickPrevPage(){
        onView(withId(R.id.previousPage))
                .perform(click());
    }

    public void clickLastPage(){
        onView(withId(R.id.lastPage))
                .perform(click());
    }

    public void clickCurrentPage(){
        onView(withId(R.id.currentPageLabel))
                .perform(click());
    }

    public void clickFirstPage(){
        onView(withId(R.id.firstPage))
                .perform(click());
    }

    public void selectPage(int page){
        onView(withId(R.id.numberPicker))
                .perform(scrollToPage(page));
    }

    public void typePage(int page){
        onView(withId(R.id.customNumber))
                .perform(replaceText("" + page));
    }
}
