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

package com.jaspersoft.android.jaspermobile.ui.filters;

import android.content.Intent;
import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.support.page.ReportFiltersPageObject;
import com.jaspersoft.android.jaspermobile.support.page.ReportViewPageObject;
import com.jaspersoft.android.jaspermobile.support.rule.ActivityWithLoginRule;
import com.jaspersoft.android.jaspermobile.support.rule.DisableAnimationsRule;
import com.jaspersoft.android.jaspermobile.ui.view.activity.NavigationActivity_;
import com.jaspersoft.android.jaspermobile.ui.view.activity.ReportVisualizeActivity_;
import com.jaspersoft.android.sdk.client.oxm.resource.ResourceLookup;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.jaspersoft.android.jaspermobile.support.matcher.AdditionalViewAssertion.hasItems;
import static com.jaspersoft.android.jaspermobile.support.matcher.AdditionalViewAssertion.isVisible;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.Matchers.anyOf;

/**
 * @author Andrew Tivodar
 * @since 2.5
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class ReportFiltersTest {
    private ReportViewPageObject reportViewPageObject;
    private ReportFiltersPageObject reportFiltersPageObject;

    @ClassRule
    public static DisableAnimationsRule disableAnimationsRule = new DisableAnimationsRule();

    @Rule
    public ActivityTestRule<NavigationActivity_> init = new ActivityWithLoginRule<>(NavigationActivity_.class);

    @Rule
    public ActivityTestRule<ReportVisualizeActivity_> page = new ActivityTestRule<>(ReportVisualizeActivity_.class, false, false);

    @Before
    public void init() {
        reportViewPageObject = new ReportViewPageObject();
        reportFiltersPageObject = new ReportFiltersPageObject();

        Intent startIntent = new Intent();
        startIntent.putExtra(ReportVisualizeActivity_.RESOURCE_EXTRA, createResourceLookup());
        page.launchActivity(startIntent);

        reportViewPageObject.waitForReportWithKeyWord("");
        reportViewPageObject.clickMenuItem(anyOf(withText("Show Filters"), withId(R.id.showFilters)));
    }

    private ResourceLookup createResourceLookup() {
        ResourceLookup resourceLookup = new ResourceLookup();
        resourceLookup.setLabel("01. Geographic Result by Segment Report");
        resourceLookup.setDescription("Sample HTML5 multi-axis");
        resourceLookup.setUri("/public/Samples/Reports/01._Geographic_Results_by_Segment_Report");
        resourceLookup.setResourceType("reportUnit");
        return resourceLookup;
    }

    @Test
    public void filtersPageAppear() {
        reportFiltersPageObject.filtersListMatches(allOf(isDisplayed(), hasItems()));
    }

    @Test
    public void closeFilters() {
        reportFiltersPageObject.crossButtonMatches(R.drawable.ic_menu_close);
        Espresso.pressBack();
        reportViewPageObject.reportMatches(isVisible());
    }

    @Test
    public void runReportButton() {
        reportFiltersPageObject.editTextFilter("Store Sales 2013", "16");
        reportFiltersPageObject.clickRunReport();
        reportViewPageObject.waitForReportWithKeyWord("");
    }

    @Test
    public void incorrectFilters() {
        reportFiltersPageObject.editTextFilter("Store Sales 2013", "50");
        reportFiltersPageObject.clickRunReport();
        reportViewPageObject.waitForReportError("There is no report data.");
    }
}
