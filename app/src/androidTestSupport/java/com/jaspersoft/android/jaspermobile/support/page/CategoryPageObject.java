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
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.view.KeyEvent;
import android.view.View;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.support.matcher.WatchPeriod;

import junit.framework.AssertionFailedError;

import org.hamcrest.Matcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressKey;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.jaspersoft.android.jaspermobile.support.matcher.AdditionalViewAction.watch;
import static com.jaspersoft.android.jaspermobile.support.matcher.AdditionalViewAssertion.hasView;
import static com.jaspersoft.android.jaspermobile.support.matcher.AdditionalViewAssertion.withPosition;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;

/**
 * @author Andrew Tivodar
 * @since 2.5
 */
public abstract class CategoryPageObject extends PageObject {

    public ViewInteraction viewTypeMatches(String type) {
        if (type.equals("List")) return resourceMatches(hasView(withId(android.R.id.text2)), 0);
        else return resourceMatches(not(hasView(withId(android.R.id.text2))), 0);
    }

    public void messageMatches(Matcher<View> viewMatcher) {
        onView(withId(android.R.id.empty))
                .check(matches(viewMatcher));
    }

    public ViewInteraction resourcesListMatches(Matcher<View> viewMatcher) {
        return onView(withId(android.R.id.list))
                .check(matches(viewMatcher));
    }

    public ViewInteraction resourceMatches(Matcher<View> viewMatcher, int position) {
        return onView(withId(android.R.id.list))
                .check(matches(withPosition(viewMatcher, position)));
    }

    public ViewInteraction searchViewMatches(Matcher<View> matcher) {
        return onView(withId(R.id.search_src_text))
                .check(matches(matcher));
    }

    public void changeViewType() {
        clickMenuItem(anyOf(withText("Switch view"), withId(R.id.switchLayout)));
    }

    public void expandSearch() {
        onView(anyOf(withText("Search"), withId(R.id.search)))
                .perform(click());
    }

    public void searchFor(String title) {
        onView(withId(R.id.search_src_text))
                .perform(typeText(title))
                .perform(pressKey(KeyEvent.KEYCODE_ENTER));
    }

    public void awaitCategoryList() {
        onView(withId(android.R.id.empty))
                .perform(watch(not(withText(startsWith("Loading"))), WatchPeriod.MEDIUM));
    }

    public void clickOnItem(String itemTitle) {
        onView(withId(android.R.id.list)).
                perform(RecyclerViewActions.actionOnItem(hasDescendant(withText(startsWith(itemTitle))), click()));
    }

    public void enforceViewType(String viewType) {
        awaitCategoryList();
        try {
            viewTypeMatches(viewType);
        } catch (AssertionFailedError error) {
            changeViewType();
            awaitCategoryList();
        }
    }

}
