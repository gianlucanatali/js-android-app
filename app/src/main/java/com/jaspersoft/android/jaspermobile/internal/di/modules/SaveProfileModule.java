/*
 * Copyright � 2015 TIBCO Software, Inc. All rights reserved.
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

package com.jaspersoft.android.jaspermobile.internal.di.modules;

import android.content.Context;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.data.cache.CredentialsCache;
import com.jaspersoft.android.jaspermobile.data.cache.CredentialsCacheImpl;
import com.jaspersoft.android.jaspermobile.data.cache.JasperServerCache;
import com.jaspersoft.android.jaspermobile.data.cache.JasperServerCacheImpl;
import com.jaspersoft.android.jaspermobile.data.cache.ProfileActiveCache;
import com.jaspersoft.android.jaspermobile.data.cache.ProfileActiveCacheImpl;
import com.jaspersoft.android.jaspermobile.data.cache.ProfileCache;
import com.jaspersoft.android.jaspermobile.data.cache.ProfileCacheImpl;
import com.jaspersoft.android.jaspermobile.data.repository.CredentialsDataRepository;
import com.jaspersoft.android.jaspermobile.data.repository.JasperServerDataRepository;
import com.jaspersoft.android.jaspermobile.data.repository.ProfileDataRepository;
import com.jaspersoft.android.jaspermobile.data.validator.ServerValidatorImpl;
import com.jaspersoft.android.jaspermobile.data.validator.ProfileValidatorImpl;
import com.jaspersoft.android.jaspermobile.data.validator.CredentialsValidatorImpl;
import com.jaspersoft.android.jaspermobile.domain.interactor.CompositeUseCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.SaveProfile;
import com.jaspersoft.android.jaspermobile.domain.network.ServerApi;
import com.jaspersoft.android.jaspermobile.domain.repository.CredentialsRepository;
import com.jaspersoft.android.jaspermobile.domain.repository.JasperServerRepository;
import com.jaspersoft.android.jaspermobile.domain.repository.ProfileRepository;
import com.jaspersoft.android.jaspermobile.domain.validator.CredentialsValidator;
import com.jaspersoft.android.jaspermobile.domain.validator.ProfileValidator;
import com.jaspersoft.android.jaspermobile.domain.validator.ServerValidator;
import com.jaspersoft.android.jaspermobile.internal.di.PerActivity;
import com.jaspersoft.android.jaspermobile.presentation.action.ProfileActionListener;
import com.jaspersoft.android.jaspermobile.presentation.presenter.AuthenticationPresenter;
import com.jaspersoft.android.jaspermobile.util.security.PasswordManager;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * @author Tom Koptel
 * @since 2.3
 */
@Module
public final class SaveProfileModule {

    @PerActivity
    @Provides
    ProfileValidator provideProfileValidator(ProfileValidatorImpl profileValidator) {
        return profileValidator;
    }

    @PerActivity
    @Provides
    ServerValidator providesServerValidator(ServerValidatorImpl validator) {
        return validator;
    }

    @PerActivity
    @Provides
    CredentialsValidator providesCredentialsValidator(CredentialsValidatorImpl validator) {
        return validator;
    }

    @PerActivity
    @Provides
    ProfileActiveCache provideProfileActiveCache(ProfileActiveCacheImpl profileCache) {
        return profileCache;
    }

    @PerActivity
    @Provides
    ProfileCache provideProfileCache(ProfileCacheImpl profileCache) {
        return profileCache;
    }

    @PerActivity
    @Provides
    ProfileRepository providesProfileRepository(ProfileDataRepository dataRepository) {
        return dataRepository;
    }

    @PerActivity
    @Provides
    CredentialsCache provideCredentialsCache(Context context, @Named("accountType") String accountType) {
        String secret = context.getString(R.string.password_salt_key);
        PasswordManager passwordManager = PasswordManager.init(context, secret);
        return new CredentialsCacheImpl(context, passwordManager, accountType);
    }

    @PerActivity
    @Provides
    CredentialsRepository providesCredentialsRepository(CredentialsCache credentialsCache) {
        return new CredentialsDataRepository(credentialsCache);
    }

    @PerActivity
    @Provides
    JasperServerCache providesJasperSeverCache(JasperServerCacheImpl cache) {
        return cache;
    }

    @PerActivity
    @Provides
    JasperServerRepository providesServerRepository(JasperServerDataRepository repository) {
        return repository;
    }

    @PerActivity
    @Provides
    ProfileActionListener provideProfileActionListener(AuthenticationPresenter presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    SaveProfile provideAddProfileUseCase(
            ServerApi.Factory serverFactory,
            CredentialsValidator credentialsValidator,
            ServerValidator serverValidator,
            ProfileValidator profileValidator,
            ProfileRepository profileRepo,
            CredentialsRepository credentialsRepo,
            JasperServerRepository serverRepo,
            CompositeUseCase compositeUseCase) {
        return new SaveProfile(
                serverFactory,
                credentialsValidator,
                serverValidator,
                profileValidator,
                profileRepo,
                credentialsRepo,
                serverRepo,
                compositeUseCase);
    }
}
