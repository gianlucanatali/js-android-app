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

package com.jaspersoft.android.jaspermobile.support.rule;

import android.support.test.espresso.NoMatchingViewException;

import com.jaspersoft.android.jaspermobile.support.AccountUrlProvider;
import com.jaspersoft.android.jaspermobile.support.page.LoginPageObject;

import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;

/**
 * @author Andrew Tivodar
 * @since 2.6
 */
public class AuthRuleDelegate {
    private LoginPageObject loginPageObject = new LoginPageObject();

    public void delegateAfterActivityLaunched() {
        boolean noAccounts = false;
        try {
            loginPageObject.loginButtonMatches(isDisplayed());
            noAccounts = true;
        } catch (NoMatchingViewException error) {
            noAccounts = false;
        }
        finally {
            if (noAccounts) {
                addAccount();
            }
        }
    }

    private void addAccount() {
        loginPageObject.typeAlias("accountUnderTest");
        loginPageObject.typeUserName("joeuser");
        loginPageObject.typePassword("joeuser");
        loginPageObject.typeUrl(AccountUrlProvider.provide());
        loginPageObject.clickLoginButton();
        loginPageObject.awaitForLoginDone();
    }
}
