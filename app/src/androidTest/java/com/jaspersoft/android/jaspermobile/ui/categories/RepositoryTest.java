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

import android.content.Intent;
import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.support.page.LeftPanelPageObject;
import com.jaspersoft.android.jaspermobile.support.page.RepositoryPageObject;
import com.jaspersoft.android.jaspermobile.support.rule.ActivityWithLoginRule;
import com.jaspersoft.android.jaspermobile.support.rule.DisableAnimationsRule;
import com.jaspersoft.android.jaspermobile.ui.view.activity.NavigationActivity_;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.matcher.ViewMatchers.isSelected;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.jaspersoft.android.jaspermobile.support.matcher.AdditionalViewAssertion.hasImage;
import static com.jaspersoft.android.jaspermobile.support.matcher.AdditionalViewAssertion.hasText;
import static com.jaspersoft.android.jaspermobile.support.matcher.AdditionalViewAssertion.withSearchViewHint;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

/**
 * @author Andrew Tivodar
 * @since 2.5
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class RepositoryTest {
    private LeftPanelPageObject leftPanelPageObject;
    private RepositoryPageObject repositoryPageObject;

    @ClassRule
    public static DisableAnimationsRule disableAnimationsRule = new DisableAnimationsRule();

    @Rule
    public ActivityTestRule<NavigationActivity_> page = new ActivityWithLoginRule<>(NavigationActivity_.class, false, false);

    @Before
    public void init() {
        repositoryPageObject = new RepositoryPageObject();
        leftPanelPageObject = new LeftPanelPageObject();

        Intent repoIntent = new Intent();
        repoIntent.putExtra("currentSelection", R.id.vg_repository);
        page.launchActivity(repoIntent);
    }

    @Test
    public void repositoryAppear() {
        repositoryPageObject.awaitCategoryList();
        repositoryPageObject.resourceMatches(hasText("Organization"), 0);
    }

    @Test
    public void repositoryIsSelected() {
        leftPanelPageObject.clickBurgerButton();
        leftPanelPageObject.repositoryMatches(isSelected());
    }

    @Test
    public void repositoryTitle() {
        repositoryPageObject.titleMatches(is("Repository"));
    }

    @Test
    public void openFolder() {
        repositoryPageObject.awaitCategoryList();
        repositoryPageObject.clickOnItem("Public");
        repositoryPageObject.awaitCategoryList();
        repositoryPageObject.resourceMatches(hasText("Ad Hoc Components"), 0);
    }

    @Test
    public void folderTitle() {
        repositoryPageObject.awaitCategoryList();
        repositoryPageObject.clickOnItem("Public");
        repositoryPageObject.titleMatches(is("Public"));
    }

    @Test
    public void backFromFolder() {
        repositoryPageObject.awaitCategoryList();
        repositoryPageObject.clickOnItem("Public");
        Espresso.pressBack();
        repositoryPageObject.titleMatches(is("Repository"));
    }

    @Test
    public void repositorySearch() {
        repositoryPageObject.expandSearch();
        repositoryPageObject.searchFor("repo");
        repositoryPageObject.awaitCategoryList();
        repositoryPageObject.resourceMatches(hasText("Ad Hoc Reports"), 0);
    }

    @Test
    public void repositorySearchHint() {
        repositoryPageObject.expandSearch();
        repositoryPageObject.searchViewMatches(withSearchViewHint(containsString("Search resources")));
    }

    @Test
    public void repositoryIncorrectSearch() {
        repositoryPageObject.expandSearch();
        repositoryPageObject.searchFor("INCORRECT");
        repositoryPageObject.awaitCategoryList();
        repositoryPageObject.messageMatches(withText("No resources found"));
    }

    @Test
    public void viewTypeSwitch() {
        repositoryPageObject.enforceViewType("List");

        repositoryPageObject.changeViewType();
        repositoryPageObject.awaitCategoryList();
        repositoryPageObject.viewTypeMatches("Grid");

        repositoryPageObject.changeViewType();
        repositoryPageObject.awaitCategoryList();
        repositoryPageObject.viewTypeMatches("List");
    }

    @Test
    public void viewTypePersist() {
        repositoryPageObject.enforceViewType("List");

        repositoryPageObject.changeViewType();

        leftPanelPageObject.goToLibrary();
        leftPanelPageObject.goToRepository();
        repositoryPageObject.awaitCategoryList();

        repositoryPageObject.viewTypeMatches("Grid");
    }

    @Test
    public void viewTypeSyncWithSearch() {
        repositoryPageObject.enforceViewType("List");

        repositoryPageObject.changeViewType();

        repositoryPageObject.expandSearch();
        repositoryPageObject.searchFor("repo");
        repositoryPageObject.awaitCategoryList();
        repositoryPageObject.viewTypeMatches("Grid");

        repositoryPageObject.changeViewType();
        Espresso.pressBack();
        repositoryPageObject.awaitCategoryList();
        repositoryPageObject.viewTypeMatches("List");
    }

    @Test
    public void emptyFolder() {
        repositoryPageObject.awaitCategoryList();

        repositoryPageObject.clickOnItem("Organization");
        repositoryPageObject.awaitCategoryList();
        repositoryPageObject.clickOnItem("Domains");
        repositoryPageObject.awaitCategoryList();
        repositoryPageObject.messageMatches(withText("No resources found"));
    }

    @Test
    public void thumbnailAppear() {
        repositoryPageObject.awaitCategoryList();

        repositoryPageObject.clickOnItem("Public");
        repositoryPageObject.awaitCategoryList();
        repositoryPageObject.clickOnItem("Samples");
        repositoryPageObject.awaitCategoryList();
        repositoryPageObject.clickOnItem("Reports");
        repositoryPageObject.awaitCategoryList();
        repositoryPageObject.resourceMatches(not(hasImage(R.drawable.ic_report)), 0);
    }
}
