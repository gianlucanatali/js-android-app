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
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.domain.Profile;
import com.jaspersoft.android.jaspermobile.util.SavedItemHelper;
import com.jaspersoft.android.sdk.util.FileUtils;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.SystemService;

import java.io.File;

import javax.inject.Inject;

/**
 * @author Tom Koptel
 * @since 1.9
 */
@EFragment
public class RenameDialogFragment extends BaseDialogFragment implements DialogInterface.OnShowListener {

    private final static String SELECTED_FILE_ARG = "selected_file";
    private final static String EXTENSION_ARG = "extension_arg";
    private final static String RECORD_URI_ARG = "record_uri";

    private File selectedFile;
    private String extension;
    private Uri recordUri;

    private AlertDialog mDialog;
    private EditText reportNameEdit;

    @Bean
    protected SavedItemHelper savedItemHelper;

    @Inject
    protected Profile mProfile;

    @SystemService
    protected InputMethodManager inputMethodManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getBaseActivityComponent().inject(this);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final View customLayout = LayoutInflater.from(getActivity())
                .inflate(R.layout.rename_report_dialog_layout, null);

        String reportName = FileUtils.getBaseName(selectedFile.getName());
        reportNameEdit = (EditText) customLayout.findViewById(R.id.report_name_input);
        reportNameEdit.setText(reportName);
        reportNameEdit.setSelection(reportNameEdit.getText().length());
        reportNameEdit.addTextChangedListener(new RenameTextWatcher());

        builder.setView(customLayout);
        builder.setTitle(R.string.sdr_rrd_title);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.ok, null);
        builder.setNegativeButton(R.string.cancel, null);

        mDialog = builder.create();
        mDialog.setOnShowListener(this);
        return mDialog;
    }

    @Override
    public void onShow(DialogInterface dialogInterface) {
        mDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new OnPositiveClickListener());
        inputMethodManager.showSoftInput(reportNameEdit, 0);
    }

    @Override
    protected Class<RenameDialogClickListener> getDialogCallbackClass() {
        return RenameDialogClickListener.class;
    }

    protected void initDialogParams() {
        super.initDialogParams();

        Bundle args = getArguments();
        if (args != null) {
            if (args.containsKey(SELECTED_FILE_ARG)) {
                selectedFile = (File) args.getSerializable(SELECTED_FILE_ARG);
            }
            if (args.containsKey(EXTENSION_ARG)) {
                extension = args.getString(EXTENSION_ARG);
            }
            if (args.containsKey(RECORD_URI_ARG)) {
                recordUri = args.getParcelable(RECORD_URI_ARG);
            }
        }
    }

    public static RenameDialogFragmentBuilder createBuilder(FragmentManager fragmentManager) {
        return new RenameDialogFragmentBuilder(fragmentManager);
    }

    //---------------------------------------------------------------------
    // Dialog Builder
    //---------------------------------------------------------------------

    public static class RenameDialogFragmentBuilder extends BaseDialogFragmentBuilder<RenameDialogFragment> {

        public RenameDialogFragmentBuilder(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        public RenameDialogFragmentBuilder setSelectedFile(File file) {
            args.putSerializable(SELECTED_FILE_ARG, file);
            return this;
        }

        public RenameDialogFragmentBuilder setExtension(String extension) {
            args.putString(EXTENSION_ARG, extension);
            return this;
        }

        public RenameDialogFragmentBuilder setRecordUri(Uri recordUri) {
            args.putParcelable(RECORD_URI_ARG, recordUri);
            return this;
        }

        @Override
        protected RenameDialogFragment build() {
            return new RenameDialogFragment_();
        }
    }

    //---------------------------------------------------------------------
    // Dialog Callback
    //---------------------------------------------------------------------

    public interface RenameDialogClickListener extends DialogClickListener {
        void onRenamed(String newFileName, Uri recordUri);
    }

    //---------------------------------------------------------------------
    // Nested Classes
    //---------------------------------------------------------------------

    private class OnPositiveClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String reportName = reportNameEdit.getText().toString().trim();

            if (reportName.isEmpty()) {
                reportNameEdit.setError(getString(R.string.sdr_rrd_error_name_is_empty));
                return;
            }

            if (FileUtils.nameContainsReservedChars(reportName)) {
                reportNameEdit.setError(getString(R.string.sdr_rrd_error_characters_not_allowed));
                return;
            }

            if (savedItemHelper.itemExist(reportName, extension, mProfile.getKey())) {
                reportNameEdit.setError(getString(R.string.sdr_rrd_error_report_exists));
                return;
            }

            if (mDialogListener != null) {
                ((RenameDialogClickListener) mDialogListener).onRenamed(reportName, recordUri);
            }
            dismiss();
        }
    }

    private class RenameTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            reportNameEdit.setError(null);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }
}
