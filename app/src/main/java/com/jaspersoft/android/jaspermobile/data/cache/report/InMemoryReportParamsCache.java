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

package com.jaspersoft.android.jaspermobile.data.cache.report;

import com.jaspersoft.android.jaspermobile.internal.di.PerProfile;
import com.jaspersoft.android.jaspermobile.util.ReportParamsStorage;
import com.jaspersoft.android.sdk.client.oxm.report.ReportParameter;

import java.util.List;

import javax.inject.Inject;

/**
 * @author Tom Koptel
 * @since 2.3
 */
@PerProfile
public final class InMemoryReportParamsCache implements ReportParamsCache {
    private final ReportParamsStorage mParamsStorage;

    @Inject
    public InMemoryReportParamsCache(ReportParamsStorage paramsStorage) {
        mParamsStorage = paramsStorage;
    }

    @Override
    public void put(String uri, List<ReportParameter> parameters) {
        mParamsStorage.getInputControlHolder(uri).setReportParams(parameters);
    }

    @Override
    public List<ReportParameter> get(String uri) {
        return mParamsStorage.getInputControlHolder(uri).getReportParams();
    }

    @Override
    public void evict(String uri) {
        mParamsStorage.clearInputControlHolder(uri);
    }

    @Override
    public boolean contains(String uri) {
        List<ReportParameter> reportParams = mParamsStorage.getInputControlHolder(uri).getReportParams();
        return reportParams != null && !reportParams.isEmpty();
    }
}
