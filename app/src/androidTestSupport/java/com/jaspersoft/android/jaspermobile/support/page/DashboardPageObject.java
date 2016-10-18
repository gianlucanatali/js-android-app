/*
 * Copyright � 2015 TIBCO Software, Inc. All rights reserved.
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

import android.graphics.Bitmap;
import android.view.View;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.support.BitmapWrapper;
import com.jaspersoft.android.jaspermobile.support.matcher.WatchPeriod;

import org.hamcrest.Matcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.jaspersoft.android.jaspermobile.support.matcher.AdditionalViewAction.getImage;
import static com.jaspersoft.android.jaspermobile.support.matcher.AdditionalViewAction.waitForTextInDashboard;
import static com.jaspersoft.android.jaspermobile.support.matcher.AdditionalViewAction.watch;
import static com.jaspersoft.android.jaspermobile.support.matcher.AdditionalViewAction.zoomIn;
import static com.jaspersoft.android.jaspermobile.support.matcher.AdditionalViewAction.zoomOut;
import static com.jaspersoft.android.jaspermobile.support.matcher.AdditionalViewAssertion.hasView;
import static com.jaspersoft.android.jaspermobile.support.matcher.AdditionalViewAssertion.isVisible;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;


/**
 * @author Andrew Tivodar
 * @since 2.5
 */
public class DashboardPageObject extends PageObject {

    public void dashboardMatches(Matcher<View> reportMatcher) {
        onView(withId(R.id.webView)).
                check(matches(reportMatcher));
    }

    public Bitmap getDashboardBitmap() {
        BitmapWrapper bitmapWrapper = new BitmapWrapper();
        onView(withId(R.id.webView))
                .perform(getImage(bitmapWrapper));
        return bitmapWrapper.getBitmap();
    }

    public void zoomInDashboard() {
        onView(withId(R.id.webView))
                .perform(zoomIn());
    }

    public void zoomOutDashboard() {
        onView(withId(R.id.webView))
                .perform(zoomOut());
    }

    public void awaitDashboard() {
        awaitDashboardLoaded();
    }

    public void awaitFullDashboard() {
        awaitDashboardLoaded();
        waitForTextInDashboard(not(containsString("Loading...")), WatchPeriod.LONG.getTime());
    }

    private void awaitDashboardLoaded() {
        onView(isRoot()).
                perform(watch(hasView(withId(R.id.webView)), WatchPeriod.LONG));
        onView(withId(R.id.webView)).
                perform(watch(isVisible(), WatchPeriod.LONG));
        onView(withId(R.id.progressMessage)).
                check(doesNotExist());
    }
}
