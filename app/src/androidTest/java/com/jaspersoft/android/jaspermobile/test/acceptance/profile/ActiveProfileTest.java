/*
 * Copyright © 2014 TIBCO Software, Inc. All rights reserved.
 * http://community.jaspersoft.com/project/jaspermobile-android
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is part of Jaspersoft Mobile for Android.
 *
 * Jaspersoft Mobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Jaspersoft Mobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Jaspersoft Mobile for Android. If not, see
 * <http://www.gnu.org/licenses/lgpl>.
 */

package com.jaspersoft.android.jaspermobile.test.acceptance.profile;

import android.app.Application;
import android.database.Cursor;

import com.google.inject.Injector;
import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.profile.ServersManagerActivity_;
import com.jaspersoft.android.jaspermobile.network.ExceptionRule;
import com.jaspersoft.android.jaspermobile.test.ProtoActivityInstrumentation;
import com.jaspersoft.android.jaspermobile.test.utils.ApiMatcher;
import com.jaspersoft.android.jaspermobile.test.utils.HackedTestModule;
import com.jaspersoft.android.jaspermobile.test.utils.TestResponses;
import com.jaspersoft.android.sdk.client.JsRestClient;
import com.jaspersoft.android.sdk.client.JsServerProfile;

import org.apache.http.fake.FakeHttpLayerManager;
import org.hamcrest.Matchers;

import roboguice.RoboGuice;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onData;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.clearText;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.longClick;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.typeText;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.assertThat;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;
import static com.jaspersoft.android.jaspermobile.test.utils.espresso.JasperMatcher.onOverflowView;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

/**
 * @author Tom Koptel
 * @since 1.9
 *
 * Bug related: When user changes data of currently selected profile.
 * For instance from one user to another. Then he should be automatically signed.
 * One of constraints to this trait is that update of Server profile data will
 * undergo check on the server side by simple call to ServerInfo.
 */
public class ActiveProfileTest extends ProtoActivityInstrumentation<ServersManagerActivity_> {

    private JsRestClient jsRestClient;

    public ActiveProfileTest() {
        super(ServersManagerActivity_.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        registerTestModule(new HackedTestModule());
        setDefaultCurrentProfile();

        Application application = (Application) this.getInstrumentation()
                .getTargetContext().getApplicationContext();

        Injector injector = RoboGuice.getBaseApplicationInjector(application);
        jsRestClient = injector.getInstance(JsRestClient.class);
        startActivityUnderTest();
    }

    @Override
    protected void tearDown() throws Exception {
        unregisterTestModule();
        super.tearDown();
    }

    public void testValidChangesToProfileWillBePersisted() {
        onData(Matchers.is(instanceOf(Cursor.class)))
                .inAdapterView(withId(android.R.id.list))
                .atPosition(0).perform(longClick());
        onView(withId(R.id.editItem)).perform(click());

        onView(withId(R.id.organizationEdit)).perform(clearText());
        onView(withId(R.id.usernameEdit)).perform(clearText());
        onView(withId(R.id.usernameEdit)).perform(typeText("another_user"));
        onView(withId(R.id.passwordEdit)).perform(clearText());
        onView(withId(R.id.passwordEdit)).perform(typeText("1234"));

        // We test valid creation case.
        FakeHttpLayerManager.addHttpResponseRule(ApiMatcher.SERVER_INFO, TestResponses.SERVER_INFO);
        onView(withId(R.id.saveAction)).perform(click());

        JsServerProfile jsServerProfile = jsRestClient.getServerProfile();
        assertThat(jsServerProfile.getOrganization(), is(""));
        assertThat(jsServerProfile.getUsername(), is("another_user"));
        assertThat(jsServerProfile.getPassword(), is("1234"));
    }

    public void testInValidChangesToProfileWillBeIgnored() {
        onData(Matchers.is(instanceOf(Cursor.class)))
                .inAdapterView(withId(android.R.id.list))
                .atPosition(0).perform(longClick());
        onView(withId(R.id.editItem)).perform(click());

        onView(withId(R.id.organizationEdit)).perform(clearText());
        onView(withId(R.id.usernameEdit)).perform(clearText());
        onView(withId(R.id.usernameEdit)).perform(typeText("invalid_user"));
        onView(withId(R.id.passwordEdit)).perform(clearText());
        onView(withId(R.id.passwordEdit)).perform(typeText("1234"));

        // We test invalid creation case.
        FakeHttpLayerManager.addHttpResponseRule(ApiMatcher.SERVER_INFO, TestResponses.get().notAuthorized());
        onView(withId(R.id.saveAction)).perform(click());

        // Assert out profile still the same
        JsServerProfile jsServerProfile = jsRestClient.getServerProfile();
        assertThat(jsServerProfile.getOrganization(), is(not("")));
        assertThat(jsServerProfile.getUsername(), is(not("invalid_user")));
        assertThat(jsServerProfile.getPassword(), is(not("1234")));

        // We also should see 401 error dialog
        onOverflowView(getActivity(), withId(R.id.sdl__title)).check(matches(withText(R.string.error_msg)));
        onOverflowView(getActivity(), withId(R.id.sdl__message)).check(matches(withText(ExceptionRule.UNAUTHORIZED.getMessage())));
        onOverflowView(getActivity(), withId(R.id.sdl__negative_button)).check(matches(withText(android.R.string.ok)));
        onOverflowView(getActivity(), withId(R.id.sdl__negative_button)).perform(click());
    }

    public void testOldServerInstanceShouldBeIgnored() {
        onData(Matchers.is(instanceOf(Cursor.class)))
                .inAdapterView(withId(android.R.id.list))
                .atPosition(0).perform(longClick());
        onView(withId(R.id.editItem)).perform(click());

        onView(withId(R.id.organizationEdit)).perform(clearText());
        onView(withId(R.id.usernameEdit)).perform(clearText());
        onView(withId(R.id.usernameEdit)).perform(typeText("another_user"));
        onView(withId(R.id.passwordEdit)).perform(clearText());
        onView(withId(R.id.passwordEdit)).perform(typeText("1234"));

        // We will send server info with unsupported server version
        FakeHttpLayerManager.addHttpResponseRule(ApiMatcher.SERVER_INFO, TestResponses.EMERALD_MR1_SERVER_INFO);
        onView(withId(R.id.saveAction)).perform(click());

        // Assert out profile still the same
        JsServerProfile jsServerProfile = jsRestClient.getServerProfile();
        assertThat(jsServerProfile.getOrganization(), is(not("")));
        assertThat(jsServerProfile.getUsername(), is(not("invalid_user")));
        assertThat(jsServerProfile.getPassword(), is(not("1234")));

        // We also should see info dialog about old JRS usage
        onOverflowView(getActivity(), withId(R.id.sdl__title)).check(matches(withText(R.string.error_msg)));
        onOverflowView(getActivity(), withId(R.id.sdl__message)).check(matches(withText(R.string.r_error_server_not_supported)));
    }
}
