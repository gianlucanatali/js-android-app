/*
 * Copyright © 2014 TIBCO Software, Inc. All rights reserved.
 *  http://community.jaspersoft.com/project/jaspermobile-android
 *
 *  Unless you have purchased a commercial license agreement from Jaspersoft,
 *  the following license terms apply:
 *
 *  This program is part of Jaspersoft Mobile for Android.
 *
 *  Jaspersoft Mobile is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Jaspersoft Mobile is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Jaspersoft Mobile for Android. If not, see
 *  <http://www.gnu.org/licenses/lgpl>.
 */

package com.jaspersoft.android.jaspermobile.test.acceptance.home;

import android.app.Application;
import android.database.Cursor;

import com.google.inject.Injector;
import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.HomeActivity_;
import com.jaspersoft.android.jaspermobile.test.ProtoActivityInstrumentation;
import com.jaspersoft.android.jaspermobile.test.utils.ApiMatcher;
import com.jaspersoft.android.jaspermobile.test.utils.HackedTestModule;
import com.jaspersoft.android.jaspermobile.test.utils.TestResponses;
import com.jaspersoft.android.jaspermobile.util.ProfileHelper;
import com.jaspersoft.android.sdk.client.JsRestClient;
import com.jaspersoft.android.sdk.client.JsServerProfile;

import org.apache.http.fake.FakeHttpLayerManager;

import roboguice.RoboGuice;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onData;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.pressBack;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.clearText;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.longClick;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.typeText;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;
import static com.jaspersoft.android.jaspermobile.test.utils.DatabaseUtils.createOnlyDefaultProfile;
import static com.jaspersoft.android.jaspermobile.test.utils.espresso.JasperMatcher.hasErrorText;
import static com.jaspersoft.android.jaspermobile.test.utils.espresso.JasperMatcher.onOverflowView;
import static com.jaspersoft.android.jaspermobile.test.utils.espresso.LongListMatchers.withAdaptedData;
import static com.jaspersoft.android.jaspermobile.test.utils.espresso.LongListMatchers.withItemContent;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.text.StringContains.containsString;

/**
 * @author Tom Koptel
 * @since 1.9
 */
public class InitialHomePageTest extends ProtoActivityInstrumentation<HomeActivity_> {
    private JsRestClient jsRestClient;

    public InitialHomePageTest() {
        super(HomeActivity_.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Application application = (Application) this.getInstrumentation()
                .getTargetContext().getApplicationContext();
        registerTestModule(new HackedTestModule());
        createOnlyDefaultProfile(application.getContentResolver());

        Injector injector = RoboGuice.getBaseApplicationInjector(application);
        jsRestClient = injector.getInstance(JsRestClient.class);
    }

    @Override
    protected void tearDown() throws Exception {
        unregisterTestModule();
        super.tearDown();
    }

    public void testUserSelectsDefaultProfile() {
        startActivityUnderTest();

        onData(is(instanceOf(Cursor.class)))
                .inAdapterView(withId(android.R.id.list))
                .atPosition(0).perform(click());

        JsServerProfile serverProfile = jsRestClient.getServerProfile();
        assertThat(serverProfile.getAlias(), is(ProfileHelper.DEFAULT_ALIAS));
        assertThat(serverProfile.getOrganization(), is(ProfileHelper.DEFAULT_ORGANIZATION));
        assertThat(serverProfile.getServerUrl(), is(ProfileHelper.DEFAULT_SERVER_URL));
        assertThat(serverProfile.getUsername(), is(ProfileHelper.DEFAULT_USERNAME));
        assertThat(serverProfile.getPassword(), is(ProfileHelper.DEFAULT_PASS));
    }

    public void testUserCantDeleteActiveProfile() {
        startActivityUnderTest();

        onData(is(instanceOf(Cursor.class)))
                .inAdapterView(withId(android.R.id.list))
                .atPosition(0).perform(click());
        onView(withId(R.id.home_item_servers)).perform(click());

        onView(withText(containsString(ProfileHelper.DEFAULT_ALIAS))).perform(longClick());
        onView(withId(R.id.deleteItem)).perform(click());
        onOverflowView(getActivity(), withText(R.string.spm_delete_btn)).perform(click());

        onView(withId(android.R.id.list)).check(matches(not(withAdaptedData(withItemContent(ProfileHelper.DEFAULT_ALIAS)))));
    }

    public void testUsersRotateScreen() {
        startActivityUnderTest();

        rotate();

        onData(is(instanceOf(Cursor.class)))
                .inAdapterView(withId(android.R.id.list))
                .atPosition(0).perform(click());
    }

    public void testUserIgnoresProfileCreation() {
        startActivityUnderTest();

        int[] ids = {R.id.home_item_library, R.id.home_item_repository, R.id.home_item_favorites};
        for (int id : ids) {
            pressBack();
            onView(withId(id)).perform(click());
            onView(withId(getActionBarTitleId())).check(matches(withText(R.string.spm_list_title)));
        }
    }

    public void testProfileIncorrectSetup() throws Throwable {
        startActivityUnderTest();

        onData(is(instanceOf(Cursor.class)))
                .inAdapterView(withId(android.R.id.list))
                .atPosition(0).perform(click());

        onView(withId(R.id.home_item_library)).perform(click());
        onOverflowView(getCurrentActivity(), withText(android.R.string.ok)).perform(click());
        onView(withId(getActionBarTitleId())).check(matches(withText(R.string.sp_bc_edit_profile)));
        onView(withId(getActionBarSubTitleId())).check(matches(withText(ProfileHelper.DEFAULT_ALIAS)));

        onView(withId(R.id.saveAction)).perform(click());
    }

    public void testProfileIncorrectSetupWithNoPassword() throws Throwable {
        FakeHttpLayerManager.addHttpResponseRule(
                ApiMatcher.SERVER_INFO,
                TestResponses.get().notAuthorized());
        startActivityUnderTest();

        onData(is(instanceOf(Cursor.class)))
                .inAdapterView(withId(android.R.id.list))
                .atPosition(0).perform(click());

        onView(withId(R.id.home_item_library)).perform(click());
        onOverflowView(getCurrentActivity(), withText(android.R.string.ok)).perform(click());

        onView(withId(getActionBarTitleId())).check(matches(withText(R.string.sp_bc_edit_profile)));
        onView(withId(getActionBarSubTitleId())).check(matches(withText(ProfileHelper.DEFAULT_ALIAS)));

        onView(withId(R.id.askPasswordCheckBox)).perform(click());

        FakeHttpLayerManager.clearHttpResponseRules();
        FakeHttpLayerManager.addHttpResponseRule(
                ApiMatcher.SERVER_INFO,
                TestResponses.SERVER_INFO);
        onView(withId(R.id.saveAction)).perform(click());

        // Check whether our dialog is shown with Appropriate info
        onOverflowView(getActivity(), withId(R.id.dialogUsernameText)).check(matches(withText(ProfileHelper.DEFAULT_USERNAME)));
        onOverflowView(getActivity(), withId(R.id.dialogOrganizationText)).check(matches(withText(ProfileHelper.DEFAULT_ORGANIZATION)));
        onOverflowView(getActivity(), withId(R.id.dialogOrganizationTableRow)).check(matches(isDisplayed()));

        // Lets type some invalid password and check validation
        onOverflowView(getActivity(), withId(R.id.dialogPasswordEdit)).perform(clearText());
        onOverflowView(getActivity(), withText(android.R.string.ok)).perform(click());
        onOverflowView(getActivity(), withId(R.id.dialogPasswordEdit))
                .check(matches(hasErrorText(getActivity().getString(R.string.sp_error_field_required))));

        // Lets type some password and check if it set
        onOverflowView(getActivity(), withId(R.id.dialogPasswordEdit)).perform(typeText(PASSWORD));
        onOverflowView(getActivity(), withText(android.R.string.ok)).perform(click());

        JsServerProfile profile = jsRestClient.getServerProfile();
        assertThat(profile.getPassword(), is(PASSWORD));
    }

}
