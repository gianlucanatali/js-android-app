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

package com.jaspersoft.android.jaspermobile.activities.library;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;

import com.google.inject.Inject;
import com.jaspersoft.android.jaspermobile.Analytics;
import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.library.fragment.LibraryControllerFragment;
import com.jaspersoft.android.jaspermobile.activities.library.fragment.LibraryControllerFragment_;
import com.jaspersoft.android.jaspermobile.activities.library.fragment.LibrarySearchFragment;
import com.jaspersoft.android.jaspermobile.activities.library.fragment.LibrarySearchFragment_;
import com.jaspersoft.android.jaspermobile.activities.robospice.RoboToolbarActivity;
import com.jaspersoft.android.jaspermobile.dialog.SortDialogFragment;
import com.jaspersoft.android.jaspermobile.util.VoiceRecognitionHelper;
import com.jaspersoft.android.jaspermobile.util.filtering.Filter;
import com.jaspersoft.android.jaspermobile.util.filtering.LibraryResourceFilter;
import com.jaspersoft.android.jaspermobile.util.sorting.SortOptions;
import com.jaspersoft.android.jaspermobile.util.sorting.SortOrder;
import com.jaspersoft.android.jaspermobile.widget.FilterTitleView;
import com.jaspersoft.android.sdk.client.JsRestClient;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;

import roboguice.fragment.RoboFragment;


/**
 * @author Tom Koptel
 * @since 2.0
 */
@OptionsMenu({R.menu.sort_menu, R.menu.am_voice_command})
@EFragment(R.layout.content_layout)
public class LibraryPageFragment extends RoboFragment implements SortDialogFragment.SortDialogClickListener {

    private static final int VOICE_COMMAND = 132;

    @Inject
    protected Analytics analytics;

    @OptionsMenuItem(R.id.voiceCommand)
    protected MenuItem voiceCommandAction;

    @Pref
    protected LibraryPref_ pref;
    @Bean
    protected LibraryResourceFilter libraryResourceFilter;
    @Bean
    protected SortOptions sortOptions;

    private LibraryControllerFragment libraryControllerFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        analytics.setScreenName(Analytics.ScreenName.LIBRARY.getValue());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState == null) {
            // Reset all controls state
            pref.sortType().put(null);

            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

            libraryControllerFragment = LibraryControllerFragment_.builder()
                    .sortOrder(sortOptions.getOrder())
                    .build();
            transaction.replace(R.id.resource_controller, libraryControllerFragment, LibraryControllerFragment.TAG);

            LibrarySearchFragment searchControllerFragment = LibrarySearchFragment_.builder()
                    .build();
            transaction.replace(R.id.search_controller, searchControllerFragment);
            transaction.commit();

            analytics.sendEvent(Analytics.EventCategory.CATALOG.getValue(), Analytics.EventAction.VIEWED.getValue(), Analytics.EventLabel.LIBRARY.getValue());
        } else {
            libraryControllerFragment = (LibraryControllerFragment) getChildFragmentManager()
                    .findFragmentByTag(LibraryControllerFragment.TAG);
        }

        FilterTitleView filterTitleView = new FilterTitleView(getActivity());
        boolean filterViewInitialized = filterTitleView.init(libraryResourceFilter);
        if (filterViewInitialized) {
            filterTitleView.setFilterSelectedListener(new FilterChangeListener());
            ((RoboToolbarActivity) getActivity()).setDisplayCustomToolbarEnable(true);
            ((RoboToolbarActivity) getActivity()).setCustomToolbarView(filterTitleView);
        } else {
            ((RoboToolbarActivity) getActivity()).setCustomToolbarView(null);
        }
    }

    @OptionsItem(R.id.sort)
    final void startSorting() {
        SortDialogFragment.createBuilder(getFragmentManager())
                .setInitialSortOption(sortOptions.getOrder())
                .setTargetFragment(this)
                .show();
    }

    @OptionsItem(R.id.voiceCommand)
    final void voiceCommand() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.voice_command_title));
        startActivityForResult(intent, VOICE_COMMAND);
        analytics.sendEvent(Analytics.EventCategory.CATALOG.getValue(), Analytics.EventAction.CLICKED.getValue(), Analytics.EventLabel.VOICE_COMMANDS.getValue());
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        boolean voicRecognationAvailable = VoiceRecognitionHelper.isVoiceRecognizerAvailable(getActivity());
        voiceCommandAction.setVisible(voicRecognationAvailable);
    }

    @Override
    public void onOptionSelected(SortOrder sortOrder) {
        sortOptions.putOrder(sortOrder);

        if (libraryControllerFragment != null) {
            libraryControllerFragment.loadResourcesBySortOrder(sortOrder);
        }
    }

    @OnActivityResult(VOICE_COMMAND)
    final void voiceCommandAction(int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;

        ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
        if (libraryControllerFragment != null) {
            libraryControllerFragment.handleVoiceCommand(matches);
        }
    }

    private class FilterChangeListener implements FilterTitleView.FilterListener {
        @Override
        public void onFilter(Filter filter) {
            libraryResourceFilter.persist(filter);
            if (libraryControllerFragment != null) {
                libraryControllerFragment.loadResourcesByTypes();
            }
        }
    }
}
