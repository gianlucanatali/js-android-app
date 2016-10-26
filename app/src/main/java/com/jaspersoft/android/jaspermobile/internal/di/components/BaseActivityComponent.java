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

package com.jaspersoft.android.jaspermobile.internal.di.components;

import com.jaspersoft.android.jaspermobile.activities.favorites.FavoritesPageFragment;
import com.jaspersoft.android.jaspermobile.activities.favorites.fragment.FavoritesFragment;
import com.jaspersoft.android.jaspermobile.activities.file.FileLoadFragment;
import com.jaspersoft.android.jaspermobile.activities.file.FileViewerActivity;
import com.jaspersoft.android.jaspermobile.activities.info.fragments.ReportInfoFragment;
import com.jaspersoft.android.jaspermobile.activities.info.fragments.ResourceInfoFragment;
import com.jaspersoft.android.jaspermobile.activities.info.fragments.SimpleInfoFragment;
import com.jaspersoft.android.jaspermobile.activities.library.LibraryPageFragment;
import com.jaspersoft.android.jaspermobile.activities.library.fragment.LibraryFragment;
import com.jaspersoft.android.jaspermobile.activities.recent.RecentPageFragment;
import com.jaspersoft.android.jaspermobile.activities.recent.fragment.RecentFragment;
import com.jaspersoft.android.jaspermobile.activities.repository.RepositoryPageFragment;
import com.jaspersoft.android.jaspermobile.activities.repository.fragment.RepositoryFragment;
import com.jaspersoft.android.jaspermobile.activities.save.fragment.SaveItemFragment;
import com.jaspersoft.android.jaspermobile.activities.settings.fragment.SettingsFragment;
import com.jaspersoft.android.jaspermobile.activities.share.AnnotationActivity;
import com.jaspersoft.android.jaspermobile.activities.storage.SavedReportsFragment;
import com.jaspersoft.android.jaspermobile.activities.storage.fragment.SavedItemsFragment;
import com.jaspersoft.android.jaspermobile.activities.viewer.html.WebViewFragment;
import com.jaspersoft.android.jaspermobile.activities.viewer.html.report.ReportCastActivity;
import com.jaspersoft.android.jaspermobile.dialog.OutputFormatDialogFragment;
import com.jaspersoft.android.jaspermobile.dialog.PasswordDialogFragment;
import com.jaspersoft.android.jaspermobile.dialog.RenameDialogFragment;
import com.jaspersoft.android.jaspermobile.dialog.ReportOptionsFragmentDialog;
import com.jaspersoft.android.jaspermobile.internal.di.PerActivity;
import com.jaspersoft.android.jaspermobile.internal.di.modules.activity.ActivityModule;
import com.jaspersoft.android.jaspermobile.ui.view.activity.ToolbarActivity;
import com.jaspersoft.android.jaspermobile.ui.view.activity.schedule.EditScheduleActivity;
import com.jaspersoft.android.jaspermobile.ui.view.activity.schedule.NewScheduleActivity;
import com.jaspersoft.android.jaspermobile.util.ControllerFragment;
import com.jaspersoft.android.jaspermobile.util.ResourceOpener;
import com.jaspersoft.android.jaspermobile.util.filtering.FavoritesResourceFilter;
import com.jaspersoft.android.jaspermobile.util.filtering.LibraryResourceFilter;
import com.jaspersoft.android.jaspermobile.util.filtering.RecentlyViewedResourceFilter;
import com.jaspersoft.android.jaspermobile.util.filtering.RepositoryResourceFilter;
import com.jaspersoft.android.jaspermobile.util.resource.viewbinder.ReportResourceBinder;
import com.jaspersoft.android.jaspermobile.widget.AnnotationView;
import com.jaspersoft.android.jaspermobile.widget.DraggableViewsContainer;

import dagger.Subcomponent;

/**
 * @author Tom Koptel
 * @since 2.3
 */
@PerActivity
@Subcomponent(
        modules = {
                ActivityModule.class
        }
)
public interface BaseActivityComponent {
    void inject(SaveItemFragment saveItemFragment);
    void inject(LibraryFragment libraryFragment);
    void inject(RepositoryFragment repositoryFragment);
    void inject(SimpleInfoFragment simpleInfoFragment);
    void inject(ResourceInfoFragment resourceInfoFragment);
    void inject(ReportInfoFragment reportInfoFragment);
    void inject(FileLoadFragment fileLoadFragment);
    void inject(FileViewerActivity fileViewerActivity);
    void inject(FavoritesFragment favoritesFragment);
    void inject(RecentFragment recentFragment);
    void inject(SavedItemsFragment savedItemsFragment);
    void inject(SettingsFragment settingsFragment);
    void inject(ControllerFragment controllerFragment);
    void inject(LibraryPageFragment libraryPageFragment);
    void inject(RepositoryPageFragment repositoryPageFragment);
    void inject(RecentPageFragment recentPageFragment);
    void inject(SavedReportsFragment savedReportsFragment);
    void inject(FavoritesPageFragment favoritesPageFragment);
    void inject(WebViewFragment webViewFragment);
    void inject(RenameDialogFragment renameDialogFragment);

    void inject(ReportOptionsFragmentDialog reportOptionsFragmentDialog);
    void inject(PasswordDialogFragment passwordDialogFragment);

    void inject(ResourceOpener resourceOpener);

    void inject(FavoritesResourceFilter filter);
    void inject(LibraryResourceFilter filter);
    void inject(RecentlyViewedResourceFilter filter);
    void inject(RepositoryResourceFilter filter);

    void inject(ReportCastActivity reportCastActivity);
    void inject(NewScheduleActivity newScheduleActivity);
    void inject(EditScheduleActivity editScheduleActivity);
    void inject(AnnotationActivity toolbarActivity);
    void inject(ToolbarActivity toolbarActivity);

    void inject(OutputFormatDialogFragment fragment);
    void inject(DraggableViewsContainer draggableViewsContainer);
    void inject(AnnotationView annotationView);
    void inject(ReportResourceBinder reportResourceBinder);
}
