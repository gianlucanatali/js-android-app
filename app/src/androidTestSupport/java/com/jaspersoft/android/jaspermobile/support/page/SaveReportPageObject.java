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

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.view.View;

import com.jaspersoft.android.jaspermobile.R;

import org.hamcrest.Matcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.hasErrorText;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.jaspersoft.android.jaspermobile.support.matcher.AdditionalViewAssertion.hasError;
import static com.jaspersoft.android.jaspermobile.support.matcher.AdditionalViewAssertion.hasView;
import static com.jaspersoft.android.jaspermobile.support.matcher.AdditionalViewAssertion.withImageResource;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.AllOf.allOf;


/**
 * @author Andrew Tivodar
 * @since 2.5
 */
public class SaveReportPageObject extends PageObject {
    public void savedItemMatches(String name, int iconResource) {
        onView(withId(android.R.id.list))
                .check(matches(isDisplayed()));
        try {
            scrollToItem(allOf(hasSibling(hasView(withText(containsString(name)))), withId(android.R.id.icon), withImageResource(iconResource)));
        } catch (Exception ex) {
            throw new RuntimeException("There is no saved item with " + name + " name");
        }
    }

    public void fileNameErrorMatches(String name) {
        onView(withId(R.id.report_name_input))
                .check(matches(hasError()))
                .check(matches(hasErrorText(name)));
    }

    public void clickSave() {
        clickMenuItem(anyOf(withText("Save"), withId(R.id.saveAction)));
    }

    public void selectFormat(String format) {
        onView(withId(R.id.output_format_spinner))
                .perform(click());
        onView(withText(format))
                .perform(click());
    }

    public void typeFileName(String name) {
        onView(withId(R.id.report_name_input))
                .perform(replaceText(name));
    }

    private void scrollToItem(Matcher<View> matcher) {
        onView(allOf(withId(android.R.id.list), isDisplayed())).
                perform(RecyclerViewActions.scrollTo(hasDescendant(matcher)));
    }
}
