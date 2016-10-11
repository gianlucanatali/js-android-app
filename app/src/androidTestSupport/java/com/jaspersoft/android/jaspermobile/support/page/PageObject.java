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

import android.support.test.espresso.NoMatchingRootException;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewAssertion;
import android.view.View;

import com.jaspersoft.android.jaspermobile.R;

import junit.framework.AssertionFailedError;

import org.hamcrest.Matcher;

import java.util.concurrent.TimeUnit;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.jaspersoft.android.jaspermobile.support.matcher.AdditionalViewAction.openOverflowMenu;
import static com.jaspersoft.android.jaspermobile.support.matcher.AdditionalViewAction.watch;
import static com.jaspersoft.android.jaspermobile.support.matcher.AdditionalViewAssertion.exist;
import static com.jaspersoft.android.jaspermobile.support.matcher.AdditionalViewAssertion.hasView;
import static com.jaspersoft.android.jaspermobile.support.matcher.AdditionalViewAssertion.isShown;
import static com.jaspersoft.android.jaspermobile.support.matcher.AdditionalViewAssertion.isToast;
import static com.jaspersoft.android.jaspermobile.support.matcher.AdditionalViewAssertion.isVisible;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.core.AllOf.allOf;

/**
 * @author Andrew Tivodar
 * @since 2.5
 */
public abstract class PageObject {
    public void titleMatches(Matcher<String> stringMatcher) {
        onView(allOf(withChild(withText(stringMatcher)), isDisplayed()))
                .check(exist());
    }

    public void dialogTitleMatches(String title) {
        onView(allOf((withText(title)), withId(R.id.alertTitle)))
                .check(matches(isDisplayed()));
    }

    public void assertToastMessage(String string) {
        onView(withText(startsWith(string)))
                .inRoot(isToast())
                .perform(watch(isShown(), TimeUnit.SECONDS.toMillis(15)));
    }

    public void waitForToastDisappear() {
        try {
            onView(withId(android.R.id.message))
                    .inRoot(isToast()).check(matches(isVisible()));
        } catch (NoMatchingRootException ex) {
            return;
        }

        onView(withId(android.R.id.message))
                .inRoot(isToast())
                .perform(watch(not(isShown()), TimeUnit.SECONDS.toMillis(15)));
    }

    public void dialogPositiveButtonClick() {
        onView(withId(android.R.id.button1))
                .perform(click());
    }

    public void dialogNegativeButtonClick() {
        onView(withId(android.R.id.button2))
                .perform(click());
    }

    public void clickMenuItem(Matcher<View> viewMatcher) {
        menuItemAction(click(), viewMatcher);
    }

    public void longClickMenuItem(Matcher<View> viewMatcher) {
        menuItemAction(longClick(), viewMatcher);
    }

    public void menuItemMatches(Matcher<View> menuMatcher, Matcher<View> viewMatcher) {
        try {
            onView(isRoot())
                    .check(matches(hasView(viewMatcher)));
        } catch (AssertionFailedError ex) {
            onView(withId(R.id.tb_navigation))
                    .perform(openOverflowMenu());
        }

        onView(viewMatcher)
                .check(matches(menuMatcher));
    }

    public void menuItemAssertion(Matcher<View> viewMatcher, ViewAssertion viewAssertion) {
        try {
            onView(isRoot())
                    .check(matches(hasView(viewMatcher)));
        } catch (AssertionFailedError ex) {
            onView(withId(R.id.tb_navigation))
                    .perform(openOverflowMenu());
        }

        onView(viewMatcher)
                .check(viewAssertion);
    }

    public void menuItemAction(ViewAction menuAction, Matcher<View> viewMatcher) {
        try {
            onView(isRoot())
                    .check(matches(hasView(viewMatcher)));
        } catch (AssertionFailedError ex) {
            onView(withId(R.id.tb_navigation))
                    .perform(openOverflowMenu());
        }

        onView(viewMatcher)
                .perform(menuAction);
    }
}
