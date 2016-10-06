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

package com.jaspersoft.android.jaspermobile.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.jaspersoft.android.jaspermobile.BuildConfig;
import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.EulaActivity;

/**
 * @author Tom Koptel
 * @since 1.9
 */
public class AboutDialogFragment extends SimpleDialogFragment implements View.OnClickListener {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.sa_show_about);

        String version = getString(R.string.sa_about_version, BuildConfig.VERSION_NAME);
        View aboutGroup = LayoutInflater.from(getContext()).inflate(R.layout.about_dialog, null, false);
        aboutGroup.findViewById(R.id.moreInfo).setOnClickListener(this);
        aboutGroup.findViewById(R.id.privacyPolicy).setOnClickListener(this);
        aboutGroup.findViewById(R.id.latestUpdates).setOnClickListener(this);
        aboutGroup.findViewById(R.id.eula).setOnClickListener(this);
        ((TextView) aboutGroup.findViewById(R.id.appVersion)).setText(version);

        builder.setView(aboutGroup);
        builder.setNeutralButton(R.string.ok, null);

        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);

        return dialog;
    }

    public static AboutDialogFragmentBuilder createBuilder(Context context, FragmentManager fragmentManager) {
        return new AboutDialogFragmentBuilder(context, fragmentManager);
    }

    @Override
    public void onClick(View v) {
        Intent actionIntent;
        switch (v.getId()) {
            case R.id.moreInfo:
                actionIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://community.jaspersoft.com/project/tibco-jaspermobile-android"));
                break;
            case R.id.privacyPolicy:
                actionIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.tibco.com/company/privacy-cma"));
                break;
            case R.id.latestUpdates:
                actionIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Jaspersoft/js-android-app/wiki/What's-new"));
                break;
            case R.id.eula:
                actionIntent = new Intent(getActivity(), EulaActivity.class);
                break;
            default:
                actionIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://community.jaspersoft.com/project/tibco-jaspermobile-android"));
        }
        startActivity(actionIntent);
    }

    public static class AboutDialogFragmentBuilder extends SimpleDialogFragmentBuilder<AboutDialogFragment> {

        public AboutDialogFragmentBuilder(Context context, FragmentManager fragmentManager) {
            super(context, fragmentManager);
        }

        @Override
        public AboutDialogFragment build() {
            return new AboutDialogFragment();
        }
    }
}