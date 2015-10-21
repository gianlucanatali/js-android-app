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

package com.jaspersoft.android.jaspermobile.activities.inputcontrols.viewholders;

import android.view.View;

import com.jaspersoft.android.sdk.client.oxm.control.InputControl;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * @author Andrew Tivodar
 * @since 2.2
 */
public class TimeInputControlViewHolder extends DateTimeInputControlViewHolder {

    private static final String DEFAULT_DATE_TIME_FORMAT = "HH:mm:ss";

    public TimeInputControlViewHolder(View itemView) {
        super(itemView);

        mUserDateFormat = new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT, Locale.getDefault());
    }

    @Override
    public void populateView(InputControl inputControl) {
        super.populateView(inputControl);

        btnDate.setVisibility(View.GONE);
        dateTimeDivider.setVisibility(View.GONE);
    }
}
