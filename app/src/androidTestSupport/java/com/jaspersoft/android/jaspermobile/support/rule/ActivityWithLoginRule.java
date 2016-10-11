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

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;

/**
 * @author Andrew Tivodar
 * @since 2.6
 */
public class ActivityWithLoginRule<A extends Activity> extends ActivityTestRule<A> {
   private final AuthRuleDelegate authRuleDelegate;

    public ActivityWithLoginRule(Class<A> activityClass) {
        super(activityClass);
        authRuleDelegate = new AuthRuleDelegate();
    }

    public ActivityWithLoginRule(Class<A> activityClass, boolean initialTouchMode) {
        super(activityClass, initialTouchMode);
        authRuleDelegate = new AuthRuleDelegate();
    }

    public ActivityWithLoginRule(Class<A> activityClass, boolean initialTouchMode, boolean launchActivity) {
        super(activityClass, initialTouchMode, launchActivity);
        authRuleDelegate = new AuthRuleDelegate();
    }

    @Override
    protected void afterActivityLaunched() {
        authRuleDelegate.delegateAfterActivityLaunched();
    }
}
