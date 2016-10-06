/*
 * Copyright © 2016 TIBCO Software,Inc.All rights reserved.
 * http://community.jaspersoft.com/project/jaspermobile-android
 *
 * Unless you have purchased a commercial license agreement from TIBCO Jaspersoft,
 * the following license terms apply:
 *
 * This program is part of TIBCO Jaspersoft Mobile for Android.
 *
 * TIBCO Jaspersoft Mobile is free software:you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation,either version 3of the License,or
 * (at your option)any later version.
 *
 * TIBCO Jaspersoft Mobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY;without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with TIBCO Jaspersoft Mobile for Android.If not,see
 * <http://www.gnu.org/licenses/lgpl>.
 */

package com.jaspersoft.android.jaspermobile.internal.di.components;

import com.jaspersoft.android.jaspermobile.JasperMobileApplication;
import com.jaspersoft.android.jaspermobile.auth.JasperAuthenticator;
import com.jaspersoft.android.jaspermobile.internal.di.modules.ProfileModule;
import com.jaspersoft.android.jaspermobile.internal.di.modules.activity.ActivityModule;
import com.jaspersoft.android.jaspermobile.internal.di.modules.activity.StartupActivityModule;
import com.jaspersoft.android.jaspermobile.internal.di.modules.app.AppModule;
import com.jaspersoft.android.jaspermobile.internal.di.modules.app.CacheModule;
import com.jaspersoft.android.jaspermobile.internal.di.modules.app.ConstantsModule;
import com.jaspersoft.android.jaspermobile.internal.di.modules.app.RepoModule;
import com.jaspersoft.android.jaspermobile.network.AcceptJpegHttpsDownloader;

import javax.inject.Singleton;

import dagger.Component;

/**
 * @author Tom Koptel
 * @since 2.3
 */
@Singleton
@Component(modules = {
        AppModule.class,
        CacheModule.class,
        RepoModule.class,
        ConstantsModule.class,
})
public interface AppComponent {
    void inject(JasperAuthenticator authenticator);
    void inject(JasperMobileApplication application);
    void inject(AcceptJpegHttpsDownloader acceptJpegHttpsDownloader);

    AuthenticatorActivityComponent plus(ActivityModule activityModule);
    StartupActivityComponent plus(StartupActivityModule startupActivityModule);
    ProfileComponent plus(ProfileModule profileModule);
}
