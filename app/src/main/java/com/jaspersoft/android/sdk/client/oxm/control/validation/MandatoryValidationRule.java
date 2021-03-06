/*
 * Copyright � 2016 TIBCO Software,Inc.All rights reserved.
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

package com.jaspersoft.android.sdk.client.oxm.control.validation;

import android.os.Parcel;

/**
 * @author Ivan Gadzhega
 * @since 1.4
 */
public class MandatoryValidationRule extends ValidationRule {

    // currently it's enough to have super class's fields only, i.e. errorMessage.

    public MandatoryValidationRule() { }

    //---------------------------------------------------------------------
    // Parcelable
    //---------------------------------------------------------------------


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    protected MandatoryValidationRule(Parcel in) {
        super(in);
    }

    public static final Creator<MandatoryValidationRule> CREATOR = new Creator<MandatoryValidationRule>() {
        public MandatoryValidationRule createFromParcel(Parcel source) {
            return new MandatoryValidationRule(source);
        }

        public MandatoryValidationRule[] newArray(int size) {
            return new MandatoryValidationRule[size];
        }
    };
}
