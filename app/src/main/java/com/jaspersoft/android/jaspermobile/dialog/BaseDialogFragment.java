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

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.jaspersoft.android.jaspermobile.internal.di.components.BaseActivityComponent;
import com.jaspersoft.android.jaspermobile.ui.view.fragment.ComponentProviderDelegate;

/**
 * @author Andrew Tivodar
 * @since 2.0
 */
public abstract class BaseDialogFragment extends DialogFragment {

    private final static String CANCELED_ON_TOUCH_OUTSIDE_ARG = "canceled_on_touch_outside";
    private final static String REQUEST_CODE_ARG = "request_code";

    protected boolean canceledOnTouchOutside;
    protected int requestCode;
    protected DialogClickListener mDialogListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        initDialogParams();
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        attachListener(activity, getDialogCallbackClass());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Fragment targetFragment = getTargetFragment();
        if (targetFragment == null) return;

        if (!getFragmentManager().getFragments().contains(targetFragment)) {
            setTargetFragment(null, -1);
        }
    }

    @NonNull
    public BaseActivityComponent getBaseActivityComponent() {
        return ComponentProviderDelegate.INSTANCE.getBaseActivityComponent(getActivity());
    }

    protected void initDialogParams() {
        Bundle args = getArguments();
        if (args != null) {
            canceledOnTouchOutside = args.getBoolean(CANCELED_ON_TOUCH_OUTSIDE_ARG, true);
            requestCode = args.getInt(REQUEST_CODE_ARG, -1);
        }
    }

    protected abstract <T extends DialogClickListener> Class<T> getDialogCallbackClass();

    private <T extends DialogClickListener> void attachListener(Activity activity, Class<T> callbackClass) {
        Fragment targetFragment = getTargetFragment();
        if (targetFragment == null) {
            try {
                mDialogListener = callbackClass.cast(activity);
            } catch (ClassCastException e) {
                mDialogListener = null;
            }
        } else {
            try {
                mDialogListener = callbackClass.cast(targetFragment);
            } catch (ClassCastException e) {
                mDialogListener = null;
            }
        }
    }

    //---------------------------------------------------------------------
    // Dialog Builder
    //---------------------------------------------------------------------

    public static abstract class BaseDialogFragmentBuilder<T extends BaseDialogFragment> {

        protected final Bundle args;
        protected final FragmentManager mFragmentManager;
        protected Fragment mTargetFragment;

        public BaseDialogFragmentBuilder(FragmentManager fragmentManager) {
            this.args = new Bundle();
            this.mFragmentManager = fragmentManager;
        }

        public BaseDialogFragmentBuilder<T> setTargetFragment(Fragment targetFragment) {
            mTargetFragment = targetFragment;
            return this;
        }

        public BaseDialogFragmentBuilder<T> setCancelableOnTouchOutside(boolean canceledOnTouchOutside) {
            args.putBoolean(CANCELED_ON_TOUCH_OUTSIDE_ARG, canceledOnTouchOutside);
            return this;
        }

        public BaseDialogFragmentBuilder<T> setRequestCode(int requestCode) {
            args.putInt(REQUEST_CODE_ARG, requestCode);
            return this;
        }

        protected abstract T build();

        public void show() {
            T baseDialogFragment = build();
            if (mTargetFragment != null) {
                baseDialogFragment.setTargetFragment(mTargetFragment, 0);
            }

            baseDialogFragment.setArguments(args);
            baseDialogFragment.show(mFragmentManager, baseDialogFragment.getClass().getSimpleName());
        }

    }


    //---------------------------------------------------------------------
    // Dialog Callback
    //---------------------------------------------------------------------

    protected interface DialogClickListener {
    }
}
