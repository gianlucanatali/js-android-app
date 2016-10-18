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

package com.jaspersoft.android.jaspermobile.ui.login;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.jaspersoft.android.jaspermobile.support.AccountUrlProvider;
import com.jaspersoft.android.jaspermobile.support.page.LibraryPageObject;
import com.jaspersoft.android.jaspermobile.support.page.LoginPageObject;
import com.jaspersoft.android.jaspermobile.support.rule.ActivityWithLoginRule;
import com.jaspersoft.android.jaspermobile.support.rule.DisableAnimationsRule;
import com.jaspersoft.android.jaspermobile.ui.view.activity.AuthenticatorActivity;
import com.jaspersoft.android.jaspermobile.ui.view.activity.NavigationActivity_;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigInteger;
import java.security.SecureRandom;

import static android.support.test.espresso.matcher.ViewMatchers.hasErrorText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static org.hamcrest.Matchers.not;

/**
 * @author Andrew Tivodar
 * @since 2.5
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class LoginCasesTest {
    private LoginPageObject loginPageObject;
    private LibraryPageObject libraryPageObject;

    @ClassRule
    public static DisableAnimationsRule disableAnimationsRule = new DisableAnimationsRule();

    @Rule
    public ActivityTestRule<NavigationActivity_> init = new ActivityWithLoginRule<>(NavigationActivity_.class);

    @Rule
    public ActivityTestRule<AuthenticatorActivity> page = new ActivityTestRule<>(AuthenticatorActivity.class, false, false);

    @Before
    public void setUp() {
        loginPageObject = new LoginPageObject();
        libraryPageObject = new LibraryPageObject();

        page.launchActivity(null);
    }

    @After
    public void tearDown() throws Exception {
        loginPageObject.waitForToastDisappear();
    }

    private String nextAccName() {
        return new BigInteger(130, new SecureRandom()).toString(32);
    }

    @Test
    public void appLogoAppear() {
        loginPageObject.logoMatches(isDisplayed());
    }

    @Test
    public void loginWithWrongUrl() {
        loginPageObject.typeAlias("testWrongUrl");
        loginPageObject.typeUserName("test");
        loginPageObject.typePassword("test");
        loginPageObject.typeUrl(AccountUrlProvider.provide() + "_wrong");
        loginPageObject.clickLoginButton();
        loginPageObject.assertToastMessage("Not Found.");
    }

    @Test
    public void loginWithNewAccount() {
        loginPageObject.typeAlias(nextAccName());
        loginPageObject.typeUserName("joeuser");
        loginPageObject.typePassword("joeuser");
        loginPageObject.typeUrl(AccountUrlProvider.provide());
        loginPageObject.clickLoginButton();
        libraryPageObject.awaitCategoryList();
    }

    @Test
    public void loginWithDemo() {
        loginPageObject.clickTryDemoButton();
        libraryPageObject.awaitCategoryList();

        page.getActivity().startActivityForResult(new Intent( page.getActivity(), AuthenticatorActivity.class), 1000);
        loginPageObject.demoButtonMatches(not(isDisplayed()));
    }

    @Test
    public void loginWithAlreadyExisted() {
        loginPageObject.typeAlias("accountUnderTest");
        loginPageObject.typeUserName("joeuser");
        loginPageObject.typePassword("joeuser");
        loginPageObject.typeUrl(AccountUrlProvider.provide());
        loginPageObject.clickLoginButton();
        loginPageObject.aliasMatches(hasErrorText("Duplicate account name."));
    }

    @Test
    public void loginWithWrongUserName() {
        loginPageObject.typeAlias("testWrongUserName");
        loginPageObject.typeUrl(AccountUrlProvider.provide());
        loginPageObject.typeUserName("WRONG");
        loginPageObject.typePassword("joeuser");
        loginPageObject.clickLoginButton();
        loginPageObject.assertToastMessage("Authentication credentials were missing or incorrect.");
    }

    @Test
    public void loginWithWrongPassword() {
        loginPageObject.typeAlias("testWrongPassword");
        loginPageObject.typeUrl(AccountUrlProvider.provide());
        loginPageObject.typeUserName("joeuser");
        loginPageObject.typePassword("WRONG");
        loginPageObject.clickLoginButton();
        loginPageObject.assertToastMessage("Authentication credentials were missing or incorrect.");
    }
}
