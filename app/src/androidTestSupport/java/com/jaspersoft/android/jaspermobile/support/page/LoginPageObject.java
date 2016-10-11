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

import org.hamcrest.Matcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.jaspersoft.android.jaspermobile.support.matcher.AdditionalViewAssertion.isInAuthActivity;
import static com.jaspersoft.android.jaspermobile.support.matcher.AdditionalViewAssertion.withImageResource;
import static org.hamcrest.Matchers.not;


/**
 * @author Andrew Tivodar
 * @since 2.5
 */
public class LoginPageObject extends PageObject {

    public ViewInteraction loginButtonMatches(Matcher<View> matcher) {
        return onView(withId(R.id.addAccount)).check(matches(matcher));
    }

    public ViewInteraction demoButtonMatches(Matcher<View> matcher) {
        return onView(withId(R.id.tryDemo)).check(matches(matcher));
    }

    public ViewInteraction logoMatches(Matcher<View> matcher) {
        return onView(withImageResource(R.drawable.im_logo_single_line))
                .check(matches(matcher));
    }

    public ViewInteraction aliasMatches(Matcher<View> matcher) {
        return onView(withId(R.id.aliasEdit))
                .check(matches(matcher));
    }

    public ViewInteraction urlMatches(Matcher<View> matcher) {
        return onView(withId(R.id.serverUrlEdit))
                .check(matches(matcher));
    }

    public ViewInteraction userNameMatches(Matcher<View> matcher) {
        return onView(withId(R.id.usernameEdit))
                .check(matches(matcher));
    }

    public ViewInteraction passwordMatches(Matcher<View> matcher) {
        return onView(withId(R.id.passwordEdit))
                .check(matches(matcher));
    }

    public void typeAlias(String alias) {
        onView(withId(R.id.aliasEdit))
                .perform(replaceText(alias));
    }

    public void typeUrl(String url) {
        onView(withId(R.id.serverUrlEdit))
                .perform(replaceText(url));
    }

    public void typeOrganization(String organization) {
        onView(withId(R.id.organizationEdit))
                .perform(replaceText(organization));
    }

    public void typeUserName(String userName) {
        onView(withId(R.id.usernameEdit))
                .perform(replaceText(userName));
    }

    public void typePassword(String password) {
        onView(withId(R.id.passwordEdit))
                .perform(replaceText(password));
    }

    public void clickLoginButton() {
        onView(withId(R.id.addAccount))
                .perform(click());
    }

    public void clickTryDemoButton() {
        onView(withId(R.id.tryDemo))
                .perform(click());
    }

    public void awaitForLoginDone() {
        onView(isRoot())
                .check(matches(not(isInAuthActivity())));
    }
}
