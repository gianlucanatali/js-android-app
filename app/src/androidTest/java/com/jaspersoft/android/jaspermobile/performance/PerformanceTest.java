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

package com.jaspersoft.android.jaspermobile.performance;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.jaspersoft.android.jaspermobile.support.page.LibraryPageObject;
import com.jaspersoft.android.jaspermobile.support.page.DashboardPageObject;
import com.jaspersoft.android.jaspermobile.support.page.ReportViewPageObject;
import com.jaspersoft.android.jaspermobile.ui.view.activity.NavigationActivity_;
import com.jaspersoft.android.jaspermobile.support.rule.AuthenticateProfileTestRule;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

/**
 * @author Andrew Tivodar
 * @since 2.5
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class PerformanceTest {

    @Rule
    public ActivityTestRule<NavigationActivity_> page = new ActivityTestRule<>(NavigationActivity_.class);

    @ClassRule
    public static TestRule authRule = AuthenticateProfileTestRule.create();

    private ReportViewPageObject reportViewPageObject;
    private LibraryPageObject libraryPageObject;
    private DashboardPageObject dashboardPageObject;

    @Before
    public void setUp() throws Exception {
        libraryPageObject = new LibraryPageObject();
        dashboardPageObject = new DashboardPageObject();
        reportViewPageObject = new ReportViewPageObject();
    }

    @Test
    public void openDashboardTest() {
        libraryPageObject.enforceFilter("All");
        libraryPageObject.awaitLibrary();

        libraryPageObject.selectFilter("Dashboards");
        libraryPageObject.awaitLibrary();

        libraryPageObject.clickOnItem("1. Supermart Dashboard");

        dashboardPageObject.awaitFullDashboard();
    }

    @Test
    public void openReportTest() {
        libraryPageObject.enforceFilter("All");
        libraryPageObject.awaitLibrary();

        libraryPageObject.clickOnItem("03. Store Segment");

        reportViewPageObject.awaitReport();
    }
}