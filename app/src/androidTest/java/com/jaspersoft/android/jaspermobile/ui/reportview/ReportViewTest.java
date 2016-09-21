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

package com.jaspersoft.android.jaspermobile.ui.reportview;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.support.page.LibraryPageObject;
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

import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.jaspersoft.android.jaspermobile.support.matcher.AdditionalViewAssertion.exist;
import static com.jaspersoft.android.jaspermobile.support.matcher.AdditionalViewAssertion.hasItems;
import static com.jaspersoft.android.jaspermobile.support.matcher.AdditionalViewAssertion.isVisible;
import static com.jaspersoft.android.jaspermobile.support.matcher.AdditionalViewAssertion.sameBitmapAs;
import static com.jaspersoft.android.jaspermobile.support.matcher.AdditionalViewAssertion.withIconResource;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;

/**
 * @author Andrew Tivodar
 * @since 2.5
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ReportViewTest {
    private LibraryPageObject libraryPageObject;
    private ReportViewPageObject reportViewPageObject;

    @Rule
    public ActivityTestRule<NavigationActivity_> init = new ActivityWithLoginRule<>(NavigationActivity_.class);

    @Rule
    public ActivityTestRule<ReportVisualizeActivity_> page = new ActivityTestRule<>(ReportVisualizeActivity_.class, false, false);

    @ClassRule
    public static DisableAnimationsRule disableAnimationsRule = new DisableAnimationsRule();

    @Before
    public void init() {
        reportViewPageObject = new ReportViewPageObject();
        libraryPageObject = new LibraryPageObject();

        Intent startIntent = new Intent();
        startIntent.putExtra(ReportVisualizeActivity_.RESOURCE_EXTRA, createStoreSegmentResourceLookup());
        page.launchActivity(startIntent);
    }

    private ResourceLookup createGeographicResourceLookup() {
        ResourceLookup resourceLookup = new ResourceLookup();
        resourceLookup.setLabel("01. Geographic Result by Segment Report");
        resourceLookup.setDescription("Sample HTML5 multi-axis");
        resourceLookup.setUri("/public/Samples/Reports/01._Geographic_Results_by_Segment_Report");
        resourceLookup.setResourceType("reportUnit");
        return resourceLookup;
    }

    private ResourceLookup createStoreSegmentResourceLookup() {
        ResourceLookup resourceLookup = new ResourceLookup();
        resourceLookup.setLabel("03. Store Segment Performance Report");
        resourceLookup.setDescription("Sample OLAP chart with HTML5 Grouped Bar chart and Filter. Created from an Ad Hoc View.");
        resourceLookup.setUri("/public/Samples/Reports/03._Store_Segment_Performance_Report");
        resourceLookup.setResourceType("reportUnit");
        return resourceLookup;
    }

    @Test
    public void runReport() {
        reportViewPageObject.waitForReportWithKeyWord("Alcoholic");
    }

    @Test
    public void cancelRunReport() {
        reportViewPageObject.reportMatches(not(isVisible()));
        Espresso.pressBack();
        libraryPageObject.resourcesListMatches(hasItems());
    }

    @Test
    public void reportTitle() {
        reportViewPageObject.titleMatches(startsWith("03. Store Segment Performance Report"));
    }

    @Test
    public void favoriteReport() {
        reportViewPageObject.awaitReport();
        reportViewPageObject.clickMenuItem(anyOf(withText("Add to favorites"), withId(R.id.favoriteAction)));
        reportViewPageObject.menuItemMatches(withIconResource(R.drawable.ic_menu_star), anyOf(withText("Add to favorites"), withId(R.id.favoriteAction)));

        reportViewPageObject.clickMenuItem(anyOf(withText("Add to favorites"), withId(R.id.favoriteAction)));
        reportViewPageObject.menuItemMatches(withIconResource(R.drawable.ic_menu_star_outline), anyOf(withText("Add to favorites"), withId(R.id.favoriteAction)));
    }

    @Test
    public void favoriteItemHint() {
        reportViewPageObject.awaitReport();
        reportViewPageObject.longClickMenuItem(anyOf(withText("Add to favorites"), withId(R.id.favoriteAction)));
        reportViewPageObject.assertToastMessage("Add to favorites");
    }

    @Test
    public void saveAction() {
        reportViewPageObject.awaitReport();
        reportViewPageObject.menuItemAssertion(anyOf(withText("Save Report"), withId(R.id.saveReport)), exist());
    }

    @Test
    public void aboutAction() {
        reportViewPageObject.awaitReport();
        reportViewPageObject.clickMenuItem(anyOf(withText("View Details"), withId(R.id.aboutAction)));
        reportViewPageObject.dialogTitleMatches("03. Store Segment Performance Report");
    }

    @Test
    public void refreshReport() {
        reportViewPageObject.awaitReport();
        reportViewPageObject.clickMenuItem(anyOf(withText("Refresh"), withId(R.id.refreshAction)));
        reportViewPageObject.awaitReport();
    }

    @Test
    public void filtersAction() {
        reportViewPageObject.awaitReport();
        reportViewPageObject.menuItemAssertion(anyOf(withText("Show Filters"), withId(R.id.showFilters)), doesNotExist());

        Intent startIntent = new Intent();
        startIntent.putExtra(ReportVisualizeActivity_.RESOURCE_EXTRA, createGeographicResourceLookup());
        page.launchActivity(startIntent);

        reportViewPageObject.awaitReport();
        reportViewPageObject.menuItemAssertion(anyOf(withText("Show Filters"), withId(R.id.showFilters)), exist());
    }

    @Test
    public void printAction() {
        reportViewPageObject.awaitReport();
        reportViewPageObject.menuItemAssertion(anyOf(withText("Print"), withId(R.id.printAction)), exist());
    }

    @Test
    public void reportZoom() {
        reportViewPageObject.awaitReport();
        reportViewPageObject.zoomOutReport();
        Bitmap startBitmap = reportViewPageObject.getReportBitmap();

        reportViewPageObject.zoomInReport();
        Bitmap zoomedBitmap = reportViewPageObject.getReportBitmap();

        reportViewPageObject.zoomOutReport();
        Bitmap finalBitmap = reportViewPageObject.getReportBitmap();

        assertThat(zoomedBitmap, not(sameBitmapAs(startBitmap)));
        assertThat(finalBitmap, sameBitmapAs(startBitmap));
    }

    @Test
    public void reportWithoutPagination() {
        reportViewPageObject.awaitReport();
        reportViewPageObject.paginationMatches(not(isDisplayed()));
    }
}
