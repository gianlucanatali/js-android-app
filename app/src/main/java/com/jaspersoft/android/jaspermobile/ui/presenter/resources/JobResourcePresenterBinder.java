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

package com.jaspersoft.android.jaspermobile.ui.presenter.resources;

import com.jaspersoft.android.jaspermobile.domain.entity.job.JobResource;
import com.jaspersoft.android.jaspermobile.domain.model.JobResourceModel;
import com.jaspersoft.android.jaspermobile.internal.di.PerActivity;
import com.jaspersoft.android.jaspermobile.ui.eventbus.JobResourcesBus;
import com.jaspersoft.android.jaspermobile.ui.view.viewholder.JobResourceViewHolder;

import javax.inject.Inject;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
@PerActivity
public class JobResourcePresenterBinder extends ResourcePresenterBinder<JobResource, JobResourceViewHolder, JobResourceModel>{

    private final JobResourcesBus mJobResourcesBus;

    @Inject
    public JobResourcePresenterBinder(JobResourcesBus mJobResourcesBus) {
        this.mJobResourcesBus = mJobResourcesBus;
    }

    @Override
    public void bind(JobResourceViewHolder holder, JobResource entity, JobResourceModel model) {
        JobsResourcePresenter resourcePresenter = new JobsResourcePresenter(model, entity, mJobResourcesBus);
        resourcePresenter.bindView(holder);
        holder.setEventListener(resourcePresenter);
    }
}
