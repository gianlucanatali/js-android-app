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

package com.jaspersoft.android.jaspermobile.db.migrate;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author Andrew Tivodar
 * @since 2.5.1
 */
final class MigrationV7 implements Migration {
    private static final String JASPER_ACCOUNT_TYPE = "com.jaspersoft";
    private static final String JASPER_DEMO_URL = "http://mobiledemo.jaspersoft.com/jasperserver-pro";
    private static final String JASPER_DEMO2_URL = "http://mobiledemo2.jaspersoft.com/jasperserver-pro";
    private static final String JASPER_DEMOS_URL = "https://mobiledemo.jaspersoft.com/jasperserver-pro";
    private static final String JASPER_SERVER_URL = "SERVER_URL_KEY";

    private final AccountManager accountManager;

    MigrationV7(Context context) {
        this.accountManager = AccountManager.get(context);
    }

    @Override
    public void migrate(SQLiteDatabase database) {
        Account[] accounts = accountManager.getAccountsByType(JASPER_ACCOUNT_TYPE);
        for (Account account : accounts) {
            String url = accountManager.getUserData(account, JASPER_SERVER_URL);
            if (url.startsWith(JASPER_DEMO_URL) || url.startsWith(JASPER_DEMO2_URL)){
                accountManager.setUserData(account, JASPER_SERVER_URL, JASPER_DEMOS_URL);
            }
        }
    }
}
