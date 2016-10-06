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

package com.jaspersoft.android.jaspermobile.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.jaspersoft.android.jaspermobile.db.database.JasperMobileDbDatabase;
import com.jaspersoft.android.jaspermobile.db.migrate.Migration;

/**
 * @author Tom Koptel
 * @since 1.9
 */
public class JSDatabaseHelper extends JasperMobileDbDatabase {
    private final Context mContext;

    public JSDatabaseHelper(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        executePragmas(db);
        switch (oldVersion) {
            case 1:
            case 2:
                Migration.Factory.v2().migrate(db);
            case 3:
                Migration.Factory.v3(mContext).migrate(db);
            case 4:
                Migration.Factory.v4(mContext).migrate(db);
            case 5:
                Migration.Factory.v5(mContext).migrate(db);
            case 6:
                Migration.Factory.v6().migrate(db);
            case 7:
                Migration.Factory.v7(mContext).migrate(db);
        }
    }
}
