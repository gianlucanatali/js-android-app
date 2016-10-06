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

package com.jaspersoft.android.jaspermobile.db.migrate;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author Tom Koptel
 * @since 2.0
 */
public interface Migration {
    void migrate(SQLiteDatabase database);

    class Factory {
        public static Migration v2() {
            return new MigrationV2();
        }

        public static Migration v3(Context context) {
            return new MigrationV3(context);
        }

        public static Migration v4(Context context) {
            return new MigrationV4(context);
        }

        public static Migration v5(Context context) {
            return new MigrationV5(context);
        }

        public static Migration v6() {
            return new MigrationV6();
        }

        public static Migration v7(Context context) {
            return new MigrationV7(context);
        }
    }
}
