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

package com.jaspersoft.android.jaspermobile.activities;

import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.ui.view.activity.ToolbarActivity;

import java.io.InputStream;

/**
 * @author Andrew Tivodar
 * @since 2.5.1
 */

public class EulaActivity extends ToolbarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eula);

        String eula = fetchEula();
        if (eula == null) {
            Toast.makeText(this, R.string.fv_can_not_show_message, Toast.LENGTH_SHORT).show();
            finish();
        }

        TextView eulaText = (TextView) findViewById(R.id.eula);
        eulaText.setText(eula);
    }

    private String fetchEula() {
        try {
            Resources res = getResources();
            InputStream in_s = res.openRawResource(R.raw.eula_app_2_5_0);

            byte[] b = new byte[in_s.available()];
            in_s.read(b);
            return new String(b);
        } catch (Exception e) {
            return null;
        }
    }
}
