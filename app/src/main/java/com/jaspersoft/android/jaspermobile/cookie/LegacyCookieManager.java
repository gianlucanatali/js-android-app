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

package com.jaspersoft.android.jaspermobile.cookie;

import android.content.Context;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.jaspersoft.android.jaspermobile.util.account.AccountServerData;
import com.jaspersoft.android.jaspermobile.util.account.JasperAccountManager;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @author Tom Koptel
 * @since 1.9
 */
public class LegacyCookieManager implements JsCookieManager {
    private final Context mContext;

    public LegacyCookieManager(Context context) {
        mContext = context;
    }

    @Override
    public Observable<Boolean> manage() {
        return JasperAccountManager.get(mContext)
                .getAsyncActiveServerData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<AccountServerData, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(final AccountServerData accountServerData) {
                        return Observable.create(new Observable.OnSubscribe<Boolean>() {
                            @Override
                            public void call(final Subscriber<? super Boolean> subscriber) {
                                CookieSyncManager.createInstance(mContext);

                                final CookieManager cookieManager = CookieManager.getInstance();
                                cookieManager.removeSessionCookie();
                                cookieManager.setCookie(accountServerData.getServerUrl(), accountServerData.getServerCookie());
                                CookieSyncManager.getInstance().sync();
                                if (!subscriber.isUnsubscribed()) {
                                    subscriber.onNext(true);
                                    subscriber.onCompleted();
                                }
                            }
                        });
                    }
                });
    }
}
