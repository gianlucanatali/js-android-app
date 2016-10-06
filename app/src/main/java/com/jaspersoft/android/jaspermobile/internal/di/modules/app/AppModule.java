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

package com.jaspersoft.android.jaspermobile.internal.di.modules.app;

import android.accounts.AccountManager;
import android.app.Application;
import android.content.Context;

import com.jaspersoft.android.jaspermobile.Analytics;
import com.jaspersoft.android.jaspermobile.AppConfigurator;
import com.jaspersoft.android.jaspermobile.AppConfiguratorImpl;
import com.jaspersoft.android.jaspermobile.BackgroundThread;
import com.jaspersoft.android.jaspermobile.GraphObject;
import com.jaspersoft.android.jaspermobile.JasperAnalytics;
import com.jaspersoft.android.jaspermobile.JasperSecurityProviderUpdater;
import com.jaspersoft.android.jaspermobile.UIThread;
import com.jaspersoft.android.jaspermobile.activities.SecurityProviderUpdater;
import com.jaspersoft.android.jaspermobile.data.ComponentManager;
import com.jaspersoft.android.jaspermobile.data.cache.SecureCache;
import com.jaspersoft.android.jaspermobile.data.cache.SecureStorage;
import com.jaspersoft.android.jaspermobile.domain.executor.PostExecutionThread;
import com.jaspersoft.android.jaspermobile.domain.executor.PreExecutionThread;
import com.jaspersoft.android.jaspermobile.internal.di.ApplicationContext;
import com.jaspersoft.android.jaspermobile.network.cookie.CookieAuthenticationHandler;
import com.jaspersoft.android.jaspermobile.network.cookie.CookieHandlerFactory;
import com.jaspersoft.android.jaspermobile.ui.component.ProfileActivationListener;
import com.jaspersoft.android.jaspermobile.util.DefaultPrefHelper_;
import com.jaspersoft.android.sdk.network.AuthenticationLifecycle;
import com.jaspersoft.android.sdk.network.Server;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import dagger.Module;
import dagger.Provides;

/**
 * @author Tom Koptel
 * @since 2.3
 */
@Module
public final class AppModule {
    private final Application mApplication;

    public AppModule(Application application) {
        mApplication = application;
    }

    @Provides
    @Singleton
    @ApplicationContext
    Context provideApplicationContext() {
        return mApplication;
    }

    @Provides
    @Singleton
    CookieHandler provideCookieHandler(CookieHandlerFactory cookieHandlerFactory, @ApplicationContext Context context) {
        return cookieHandlerFactory.newStore(context);
    }

    @Provides
    @Singleton
    AuthenticationLifecycle providesAuthenticationHandler(CookieHandler cookieHandler) {
        CookieManager cookieManager = (CookieManager) cookieHandler;
        return new CookieAuthenticationHandler(cookieManager);
    }

    @Provides
    @Singleton
    PostExecutionThread providePostExecutionThread(UIThread uiThread) {
        return uiThread;
    }

    @Provides
    @Singleton
    PreExecutionThread providePreExecutionThread(BackgroundThread backgroundThread) {
        return backgroundThread;
    }

    @Provides
    @Singleton
    AccountManager providesAccountManager(@ApplicationContext Context context) {
        return AccountManager.get(context);
    }

    @Provides
    @Singleton
    Analytics providesAnalytics(@ApplicationContext Context context) {
        return new JasperAnalytics(context);
    }

    @Provides
    @Singleton
    SecurityProviderUpdater providesSecurityProviderUpdater() {
        return new JasperSecurityProviderUpdater();
    }

    @Provides
    @Singleton
    AppConfigurator providesAppConfigurator() {
        return new AppConfiguratorImpl();
    }

    @Provides
    @Singleton
    SecureCache provideSecureStorage(SecureStorage secureStorage) {
        return secureStorage;
    }

    @Provides
    Server.Builder provideServerBuilder(HostnameVerifier hostnameVerifier, SSLSocketFactory sslSocketFactory) {
        DefaultPrefHelper_ helper = DefaultPrefHelper_.getInstance_(mApplication);

        return Server.builder()
                .withConnectionTimeOut(helper.getConnectTimeoutValue(), TimeUnit.MILLISECONDS)
                .withReadTimeout(helper.getReadTimeoutValue(), TimeUnit.MILLISECONDS)
                .withSslSocketFactory(sslSocketFactory)
                .withHostnameVerifier(hostnameVerifier);
    }

    @Provides
    HostnameVerifier providesHostnameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
    }

    @Provides
    SSLSocketFactory providesSSLSocketFactory() {
        final TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }
                }
        };

        SSLContext sslContext;

        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            return null;
        }
        return sslContext.getSocketFactory();
    }

    @Provides
    GraphObject providesGraphObject() {
        return GraphObject.Factory.from(mApplication);
    }

    @Singleton
    @Provides
    ComponentManager.Callback providesComponentCallback(ProfileActivationListener activationListener) {
        return activationListener;
    }

    @Singleton
    @Named("webview_client")
    @Provides
    OkHttpClient provideWebViewClient(@ApplicationContext Context context) {
        OkHttpClient client = new OkHttpClient();

        File cacheDir = context.getApplicationContext().getCacheDir();
        File okCache = new File(cacheDir, "ok-cache");
        if (!okCache.exists()) {
            boolean cachedCreated = okCache.mkdirs();

            if (cachedCreated) {
                int cacheSize = 50 * 1024 * 1024;
                Cache cache = new Cache(okCache, cacheSize);
                client.setCache(cache);
            }
        }

        return client;
    }
}
