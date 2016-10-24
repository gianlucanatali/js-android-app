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

package com.jaspersoft.android.jaspermobile.ui.categories;

import android.support.test.espresso.Espresso;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.support.page.LeftPanelPageObject;
import com.jaspersoft.android.jaspermobile.support.page.LibraryPageObject;
import com.jaspersoft.android.jaspermobile.support.rule.ActivityWithLoginRule;
import com.jaspersoft.android.jaspermobile.support.rule.DisableAnimationsRule;
import com.jaspersoft.android.jaspermobile.ui.view.activity.NavigationActivity_;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

import static android.support.test.espresso.matcher.ViewMatchers.isSelected;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.jaspersoft.android.jaspermobile.support.matcher.AdditionalViewAssertion.hasImage;
import static com.jaspersoft.android.jaspermobile.support.matcher.AdditionalViewAssertion.hasItems;
import static com.jaspersoft.android.jaspermobile.support.matcher.AdditionalViewAssertion.hasText;
import static com.jaspersoft.android.jaspermobile.support.matcher.AdditionalViewAssertion.withSearchViewHint;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

/**
 * @author Andrew Tivodar
 * @since 2.5
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class LibraryTest {
    private LeftPanelPageObject leftPanelPageObject;
    private LibraryPageObject libraryPageObject;

    @ClassRule
    public static DisableAnimationsRule disableAnimationsRule = new DisableAnimationsRule();

    @Rule
    public TestRule page = new ActivityWithLoginRule<>(NavigationActivity_.class);

    @Before
    public void init() {
        libraryPageObject = new LibraryPageObject();
        leftPanelPageObject = new LeftPanelPageObject();
    }

    @Test
    public void libraryAppear() {
        libraryPageObject.awaitCategoryList();
        libraryPageObject.resourcesListMatches(hasItems());
    }

    @Test
    public void libraryIsSelected() {
        leftPanelPageObject.clickBurgerButton();
        leftPanelPageObject.libraryMatches(isSelected());
    }

    @Test
    public void librarySearch() {
        libraryPageObject.selectFilter("All");

        libraryPageObject.expandSearch();
        libraryPageObject.searchFor("Mix");
        libraryPageObject.awaitCategoryList();
        libraryPageObject.resourceMatches(hasText("02. Sales Mix by"), 0);
    }

    @Test
    public void librarySearchHint() {
        libraryPageObject.expandSearch();
        libraryPageObject.searchViewMatches(withSearchViewHint(containsString("Search resources")));
    }

    @Test
    public void libraryIncorrectSearch() {
        libraryPageObject.expandSearch();
        libraryPageObject.searchFor("INCORRECT");

        libraryPageObject.awaitCategoryList();
        libraryPageObject.messageMatches(withText("No resources found"));
        libraryPageObject.resourcesListMatches(not(hasItems()));
    }

    @Test
    public void viewTypeSwitch() {
        libraryPageObject.enforceViewType("List");

        libraryPageObject.changeViewType();
        libraryPageObject.awaitCategoryList();
        libraryPageObject.viewTypeMatches("Grid");

        libraryPageObject.changeViewType();
        libraryPageObject.awaitCategoryList();
        libraryPageObject.viewTypeMatches("List");
    }

    @Test
    public void viewTypePersist() {
        libraryPageObject.enforceViewType("List");

        libraryPageObject.changeViewType();

        leftPanelPageObject.goToRepository();
        leftPanelPageObject.goToLibrary();

        libraryPageObject.awaitCategoryList();
        libraryPageObject.viewTypeMatches("Grid");
    }

    @Test
    public void viewTypeSyncWithSearch() {
        libraryPageObject.enforceViewType("List");

        libraryPageObject.changeViewType();

        libraryPageObject.expandSearch();
        libraryPageObject.searchFor("Mix");
        libraryPageObject.awaitCategoryList();
        libraryPageObject.viewTypeMatches("Grid");

        libraryPageObject.changeViewType();
        Espresso.pressBack();
        libraryPageObject.awaitCategoryList();
        libraryPageObject.viewTypeMatches("List");
    }

    @Test
    public void libraryFilter() {
        libraryPageObject.selectFilter("Reports");
        libraryPageObject.awaitCategoryList();
        libraryPageObject.filterMatches("Reports");
        libraryPageObject.resourceMatches(hasText("01. Geographic Results"), 0);

        libraryPageObject.selectFilter("Dashboards");
        libraryPageObject.awaitCategoryList();
        libraryPageObject.filterMatches("Dashboards");
        libraryPageObject.resourceMatches(hasText("1. Supermart Dashboard"), 0);

        libraryPageObject.selectFilter("All");
        libraryPageObject.awaitCategoryList();
        libraryPageObject.filterMatches("All");
        libraryPageObject.resourceMatches(hasText("01. Geographic Results"), 0);
    }

    @Test
    public void libraryFilterPersist() {
        libraryPageObject.selectFilter("Dashboards");
        libraryPageObject.filterMatches("Dashboards");

        leftPanelPageObject.goToRepository();
        leftPanelPageObject.goToLibrary();

        libraryPageObject.awaitCategoryList();
        libraryPageObject.filterMatches("Dashboards");
        libraryPageObject.resourceMatches(hasText("1. Supermart Dashboard"), 0);
    }

    @Test
    public void librarySort() {
        libraryPageObject.selectSort("Creation date");
        libraryPageObject.awaitCategoryList();
        libraryPageObject.resourceMatches(hasText("Test report label"), 0);
        libraryPageObject.selectSort("Label");
        libraryPageObject.awaitCategoryList();
        libraryPageObject.resourceMatches(hasText("01. Geographic Results"), 0);
    }

    @Test
    public void librarySortNotPersist() {
        libraryPageObject.selectSort("Creation date");
        libraryPageObject.awaitCategoryList();
        libraryPageObject.resourceMatches(hasText("Test report label"), 0);

        leftPanelPageObject.goToRepository();
        leftPanelPageObject.goToLibrary();

        libraryPageObject.awaitCategoryList();
        libraryPageObject.resourceMatches(hasText("01. Geographic Results"), 0);
    }

    @Test
    public void thumbnailAppear() {
        libraryPageObject.awaitCategoryList();
        libraryPageObject.resourceMatches(not(hasImage(R.drawable.ic_report)), 0);
    }
}
