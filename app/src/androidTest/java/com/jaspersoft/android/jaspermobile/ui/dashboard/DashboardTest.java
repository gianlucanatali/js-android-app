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

package com.jaspersoft.android.jaspermobile.ui.dashboard;

import android.content.Intent;
import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.viewer.html.dashboard.Amber2DashboardActivity_;
import com.jaspersoft.android.jaspermobile.support.page.DashboardPageObject;
import com.jaspersoft.android.jaspermobile.support.page.LibraryPageObject;
import com.jaspersoft.android.jaspermobile.support.rule.ActivityWithLoginRule;
import com.jaspersoft.android.jaspermobile.support.rule.DisableAnimationsRule;
import com.jaspersoft.android.jaspermobile.ui.view.activity.NavigationActivity_;
import com.jaspersoft.android.sdk.client.oxm.resource.ResourceLookup;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.jaspersoft.android.jaspermobile.support.matcher.AdditionalViewAssertion.exist;
import static com.jaspersoft.android.jaspermobile.support.matcher.AdditionalViewAssertion.isVisible;
import static com.jaspersoft.android.jaspermobile.support.matcher.AdditionalViewAssertion.withIconResource;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;

/**
 * @author Andrew Tivodar
 * @since 2.5
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class DashboardTest {
    private LibraryPageObject libraryPageObject;
    private DashboardPageObject dashboardPageObject;

    @ClassRule
    public static DisableAnimationsRule disableAnimationsRule = new DisableAnimationsRule();

    @Rule
    public ActivityTestRule<NavigationActivity_> init = new ActivityWithLoginRule<>(NavigationActivity_.class);

    @Rule
    public ActivityTestRule<Amber2DashboardActivity_> page = new ActivityTestRule<>(Amber2DashboardActivity_.class, false, false);

    @Before
    public void init() {
        dashboardPageObject = new DashboardPageObject();
        libraryPageObject = new LibraryPageObject();

        Intent dashboardIntent = new Intent();
        dashboardIntent.putExtra(Amber2DashboardActivity_.RESOURCE_EXTRA, createResourceLookup());

        page.launchActivity(dashboardIntent);
    }

    private ResourceLookup createResourceLookup() {
        ResourceLookup resourceLookup = new ResourceLookup();
        resourceLookup.setResourceType(ResourceLookup.ResourceType.dashboard);
        resourceLookup.setUri("/public/Samples/Dashboards/1._Supermart_Dashboard");
        resourceLookup.setLabel("1. Supermart Dashboard");
        resourceLookup.setDescription("Sample containing 5 Dashlets and Filter wiring. One Dashlet is a report with hyperlinks, the other Dashlets are defined as part of the Dashboard.");
        return resourceLookup;
    }

    @Test
    public void cancelRunDashboard() {
        dashboardPageObject.dashboardMatches(not(isVisible()));
        Espresso.pressBack();
        libraryPageObject.resourcesListMatches(isVisible());
    }

    @Test
    public void dashboardTitle() {
        dashboardPageObject.awaitDashboard();
        dashboardPageObject.titleMatches(startsWith("1. Supermart"));
    }

    @Test
    public void favoriteDashboard() {
        dashboardPageObject.awaitDashboard();
        dashboardPageObject.clickMenuItem(anyOf(withText("Add to favorites"), withId(R.id.favoriteAction)));
        dashboardPageObject.menuItemMatches(withIconResource(R.drawable.ic_menu_star), anyOf(withText("Add to favorites"), withId(R.id.favoriteAction)));

        dashboardPageObject.clickMenuItem(anyOf(withText("Add to favorites"), withId(R.id.favoriteAction)));
        dashboardPageObject.menuItemMatches(withIconResource(R.drawable.ic_menu_star_outline), anyOf(withText("Add to favorites"), withId(R.id.favoriteAction)));
    }

    @Test
    public void favoriteItemHint() {
        dashboardPageObject.awaitDashboard();
        dashboardPageObject.longClickMenuItem(anyOf(withText("Add to favorites"), withId(R.id.favoriteAction)));
        dashboardPageObject.assertToastMessage("Add to favorites");
    }

    @Test
    public void aboutAction() {
        dashboardPageObject.awaitDashboard();
        dashboardPageObject.clickMenuItem(anyOf(withText("View Details"), withId(R.id.aboutAction)));
        dashboardPageObject.dialogTitleMatches("1. Supermart Dashboard");
    }

    @Test
    public void refreshDashboard() {
        dashboardPageObject.awaitFullDashboard();
        dashboardPageObject.clickMenuItem(anyOf(withText("Refresh"), withId(R.id.refreshAction)));
        dashboardPageObject.awaitFullDashboard();
    }

    @Test
    public void printAction() {
        dashboardPageObject.awaitDashboard();
        dashboardPageObject.menuItemAssertion(anyOf(withText("Print"), withId(R.id.printAction)), exist());
    }
}
