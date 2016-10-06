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

package com.jaspersoft.android.jaspermobile.widget;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.MenuRes;
import android.support.annotation.Nullable;
import android.support.v7.view.menu.ActionMenuItem;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.sdk.widget.report.renderer.Bookmark;
import com.jaspersoft.android.sdk.widget.report.view.ReportBookmarkListener;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Andrew Tivodar
 * @since 2.6
 */
public class ReportToolbar extends Toolbar implements Toolbar.OnMenuItemClickListener, ReportBookmarkListener {
    private static final Set<Integer> ACTION_GROUP = new HashSet<>(Arrays.asList(
            new Integer[] {
                    R.id.bookmarksAction,
                    R.id.shareAction,
                    R.id.filtersAction,
                    R.id.refreshAction,
                    R.id.saveAction,
                    R.id.printAction,
                    R.id.chageChartTypesAction
            }
    ));
    private MenuItem filtersAction, bookmarksAction, chageChartTypesAction;
    private OnMenuItemClickListener listener;

    public ReportToolbar(Context context) {
        super(context);
        init();
    }

    public ReportToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ReportToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setFilterAvailable(boolean filterAvailable) {
        filtersAction.setVisible(filterAvailable);
    }

    public void setChangeChartTypeAvailable(boolean available) {
        chageChartTypesAction.setVisible(available);
    }

    public boolean isFilterAvailable() {
        return filtersAction.isVisible();
    }

    public void setActionGroupEnabled(boolean enabled) {
        Menu menu = getMenu();

        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (ACTION_GROUP.contains(item.getItemId())) {
                item.setEnabled(enabled);
                item.getIcon().setAlpha(enabled ? 255 : 128);
            }
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(null);
        ((TextView) findViewById(R.id.reportTitle)).setText(title);
    }

    @Override
    public void onBookmarkListChanged(List<Bookmark> bookmarkList) {
        bookmarksAction.setVisible(!bookmarkList.isEmpty());
    }

    @Override
    public void inflateMenu(@MenuRes int resId) {
        super.inflateMenu(resId);

        Menu menu = getMenu();
        filtersAction = menu.findItem(R.id.filtersAction);
        bookmarksAction = menu.findItem(R.id.bookmarksAction);
        chageChartTypesAction = menu.findItem(R.id.chageChartTypesAction);
    }

    @Override
    public void setOnMenuItemClickListener(OnMenuItemClickListener listener) {
        super.setOnMenuItemClickListener(this);
        this.listener = listener;
    }

    private void init() {
        setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    MenuItem navItem = new ActionMenuItem(getContext(), 0, android.R.id.home, 0, 0, null);
                    listener.onMenuItemClick(navItem);
                }
            }
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return listener != null && listener.onMenuItemClick(item);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);

        ss.isFiltersAvailable = filtersAction.isVisible();
        ss.isBookmarksAvailable = bookmarksAction.isVisible();

        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        filtersAction.setVisible(ss.isFiltersAvailable);
        bookmarksAction.setVisible(ss.isBookmarksAvailable);
    }

    static class SavedState extends BaseSavedState {
        boolean isFiltersAvailable;
        boolean isBookmarksAvailable;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel source) {
            super(source);
            this.isFiltersAvailable = source.readInt() == 1;
            this.isBookmarksAvailable = source.readInt() == 1;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(isFiltersAvailable ? 1 : 0);
            out.writeInt(isBookmarksAvailable ? 1 : 0);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }
}
