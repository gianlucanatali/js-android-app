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

import android.support.test.espresso.ViewInteraction;
import android.view.View;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.support.matcher.WatchPeriod;

import org.hamcrest.Matcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.jaspersoft.android.jaspermobile.support.matcher.AdditionalViewAction.swipeFromLeftEdge;
import static com.jaspersoft.android.jaspermobile.support.matcher.AdditionalViewAction.swipeFromRightEdge;
import static com.jaspersoft.android.jaspermobile.support.matcher.AdditionalViewAction.watch;


/**
 * @author Andrew Tivodar
 * @since 2.5
 */
public class LeftPanelPageObject extends PageObject {

    public ViewInteraction accountsMatches(Matcher<View> matcher) {
        return onView(withId(R.id.lv_accounts_menu)).check(matches(matcher));
    }

    public ViewInteraction profileMatches(Matcher<View> matcher) {
        return onView(withId(R.id.tv_profile)).check(matches(matcher));
    }

    public ViewInteraction libraryMatches(Matcher<View> matcher) {
        return onView(withId(R.id.vg_library)).check(matches(matcher));
    }

    public ViewInteraction repositoryMatches(Matcher<View> matcher) {
        return onView(withId(R.id.vg_repository)).check(matches(matcher));
    }

    public ViewInteraction recentMatches(Matcher<View> matcher) {
        return onView(withId(R.id.vg_recent)).check(matches(matcher));
    }

    public ViewInteraction favoritesMatches(Matcher<View> matcher) {
        return onView(withId(R.id.vg_favorites)).check(matches(matcher));
    }

    public ViewInteraction savedItemsMatches(Matcher<View> matcher) {
        return onView(withId(R.id.vg_saved_items)).check(matches(matcher));
    }

    public void clickBurgerButton() {
        onView(withContentDescription("open"))
                .perform(click());
    }

    public void clickBackButton() {
        onView(withContentDescription("close"))
                .perform(click());
    }

    public void clickAccountsButton() {
        onView(withId(R.id.vg_profile))
                .perform(click());
    }

    public void clickAddAccountButton() {
        onView(withId(R.id.vg_add_account))
                .perform(click());
    }

    public void clickManageAccountButton() {
        onView(withId(R.id.vg_manage_accounts))
                .perform(click());
    }

    public void goToLibrary() {
        swipeToOpenMenu();
        onView(withId(R.id.vg_library))
                .perform(click());
    }

    public void goToRepository() {
        swipeToOpenMenu();
        onView(withId(R.id.vg_repository))
                .perform(click());
    }

    public void goToRecentlyViewed() {
        swipeToOpenMenu();
        onView(withId(R.id.vg_recent))
                .perform(click());
    }

    public void goToFavorites() {
        swipeToOpenMenu();
        onView(withId(R.id.vg_favorites))
                .perform(click());
    }

    public void goToSavedItems() {
        swipeToOpenMenu();
        onView(withId(R.id.vg_saved_items))
                .perform(click());
    }

    public void goToSettings() {
        swipeToOpenMenu();
        onView(withId(R.id.tv_settings))
                .perform(click());
    }

    public void showAbout() {
        swipeToOpenMenu();
        onView(withId(R.id.tv_about))
                .perform(click());
    }

    public void showFeedback() {
        swipeToOpenMenu();
        onView(withId(R.id.tv_feedback))
                .perform(click());
    }

    public void selectAccount(String accountName) {
        onView(withText(accountName))
                .perform(click());
    }

    public void swipeToOpenMenu() {
        onView(isRoot())
                .perform(swipeFromLeftEdge());
    }

    public void swipeToCloseMenu() {
        onView(isRoot())
                .perform(swipeFromRightEdge());
    }
    public ViewInteraction waitForLeftPanelMatches(Matcher<View> matcher) {
        return onView(withId(R.id.npl_navigation_menu)).perform(watch(matcher, WatchPeriod.SHORT));
    }

}
