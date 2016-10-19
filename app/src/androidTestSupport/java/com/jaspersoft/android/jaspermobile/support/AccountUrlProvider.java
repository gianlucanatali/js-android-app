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

package com.jaspersoft.android.jaspermobile.support;

import android.os.Bundle;
import android.support.test.InstrumentationRegistry;

/**
 * @author Andrew Tivodar
 * @since 2.6
 */

public class AccountUrlProvider {
    private final static String SERVER_URL_ARG = "serverUrl";

    public static String provide() {
        Bundle extras = InstrumentationRegistry.getArguments();
        if (!extras.containsKey(SERVER_URL_ARG)) return "http://192.168.88.55:8092/jasperserver-pro-630-ui-tests";
        return extras.getString(SERVER_URL_ARG);
    }
}
