/*
 * Copyright © 2014 TIBCO Software, Inc. All rights reserved.
 * http://community.jaspersoft.com/project/jaspermobile-android
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is part of Jaspersoft Mobile for Android.
 *
 * Jaspersoft Mobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Jaspersoft Mobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Jaspersoft Mobile for Android. If not, see
 * <http://www.gnu.org/licenses/lgpl>.
 */

package com.jaspersoft.android.jaspermobile.activities.viewer.html.report.fragment;

import android.accounts.Account;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.google.inject.Inject;
import com.jaspersoft.android.jaspermobile.BuildConfig;
import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.robospice.RoboSpiceFragment;
import com.jaspersoft.android.jaspermobile.activities.viewer.html.report.support.ExportOutputData;
import com.jaspersoft.android.jaspermobile.activities.viewer.html.report.support.ReportExportOutputLoader;
import com.jaspersoft.android.jaspermobile.activities.viewer.html.report.support.ReportSession;
import com.jaspersoft.android.jaspermobile.activities.viewer.html.report.support.RequestExecutor;
import com.jaspersoft.android.jaspermobile.cookie.CookieManagerFactory;
import com.jaspersoft.android.jaspermobile.dialog.SimpleDialogFragment;
import com.jaspersoft.android.jaspermobile.util.JSWebViewClient;
import com.jaspersoft.android.jaspermobile.widget.JSWebView;
import com.jaspersoft.android.retrofit.sdk.account.AccountServerData;
import com.jaspersoft.android.retrofit.sdk.account.JasperAccountManager;
import com.jaspersoft.android.retrofit.sdk.server.ServerRelease;
import com.jaspersoft.android.sdk.client.JsRestClient;
import com.jaspersoft.android.sdk.client.oxm.report.ErrorDescriptor;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

/**
 * @author Tom Koptel
 * @since 1.9
 */
@EFragment(R.layout.report_html_viewer)
@OptionsMenu(R.menu.webview_menu)
public class NodeWebViewFragment extends RoboSpiceFragment implements SimpleDialogFragment.SimpleDialogClickListener{
    public static final String TAG = NodeWebViewFragment.class.getSimpleName();

    @ViewById
    protected JSWebView webView;
    @ViewById
    protected ProgressBar progressBar;

    @InstanceState
    @FragmentArg
    protected int page;

    @InstanceState
    protected String currentHtml;
    @InstanceState
    protected boolean outputFinal;

    @Inject
    protected JsRestClient jsRestClient;

    @Bean
    protected JSWebViewClient jsWebViewClient;
    @Bean
    protected ReportSession reportSession;
    @Bean
    protected ReportExportOutputLoader reportExportOutputLoader;

    private ExportResultListener exportResultListener;
    private OnPageLoadListener onPageLoadListener;
    private RequestExecutor requestExecutor;
    private ServerRelease mRelease;
    private boolean mReportNotFound;

    @OptionsItem
    final void refreshAction() {
        fetchReport();
    }

    @AfterViews
    final void init() {
        setHasOptionsMenu(true);

        Account account = JasperAccountManager.get(getActivity()).getActiveAccount();
        AccountServerData serverData = AccountServerData.get(getActivity(), account);
        mRelease = ServerRelease.parseVersion(serverData.getVersionName());

        exportResultListener = new ExportResultListener();
        requestExecutor = RequestExecutor.builder()
                .setSpiceManager(getSpiceManager())
                .setFragmentManager(getFragmentManager())
                .setExecutionMode(RequestExecutor.Mode.SILENT)
                .create();
        reportSession.registerObserver(sessionObserver);
        prepareWebView();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!mReportNotFound) {
            if (TextUtils.isEmpty(currentHtml)) {
                fetchReport();
            } else {
                loadHtml(currentHtml);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        reportSession.removeObserver(sessionObserver);
    }

    @Override
    public void onDestroy() {
        webView.destroy();
        webView = null;
        super.onDestroy();
    }

    public void setOnPageLoadListener(OnPageLoadListener onPageLoadListener) {
        this.onPageLoadListener = onPageLoadListener;
    }

    //---------------------------------------------------------------------
    // Helper methods
    //---------------------------------------------------------------------

    private void loadHtml(String html) {
        if (html == null) {
            throw new IllegalStateException("Html can`t be null");
        }
        if (webView == null) {
            throw new IllegalStateException("WebView can`t be null");
        }
        if (jsRestClient == null) {
            throw new IllegalStateException("Client can`t be null");
        }

        if (!html.equals(currentHtml)) {
            currentHtml = html;
        }
        String mime = "text/html";
        String encoding = "utf-8";
        webView.loadDataWithBaseURL(
                jsRestClient.getServerProfile().getServerUrl(),
                currentHtml, mime, encoding, null);
    }

    @SuppressLint({"SetJavaScriptEnabled", "NewApi"})
    private void prepareWebView() {
        WebSettings settings = webView.getSettings();

        // disable hardware acceleration
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        }
        if (BuildConfig.DEBUG) {
            enableDebug();
        }

        // configure additional settings
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);

        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        webView.setWebViewClient(jsWebViewClient);

        CookieManagerFactory.syncCookies(getActivity());
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void enableDebug() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
    }

    private void fetchReport() {
        progressBar.setVisibility(View.VISIBLE);
        reportExportOutputLoader.loadByPage(requestExecutor, exportResultListener, page);
    }

    //---------------------------------------------------------------------
    // Implementing SimpleDialogFragment.SimpleDialogClickListener
    //---------------------------------------------------------------------

    @Override
    public void onPositiveClick(int requestCode) {
        PaginationManagerFragment paginationManagerFragment =
                (PaginationManagerFragment) getFragmentManager()
                        .findFragmentByTag(PaginationManagerFragment.TAG);
        paginationManagerFragment.paginateTo(1);
    }

    @Override
    public void onNegativeClick(int requestCode) {
        getActivity().finish();
    }

    //---------------------------------------------------------------------
    // Inner classes
    //---------------------------------------------------------------------

    private final ReportSession.ExecutionObserver sessionObserver =
            new ReportSession.ExecutionObserver() {
                @Override
                public void onRequestIdChanged(String requestId) {
                    fetchReport();
                }

                @Override
                public void onPagesLoaded(int totalPage) {
                    if (!outputFinal && mRelease.code() >= ServerRelease.EMERALD_MR3.code()) {
                        fetchReport();
                    }
                }
            };

    private class ExportResultListener implements ReportExportOutputLoader.ResultListener {
        @Override
        public void onFailure(Exception exception) {
            cacheNotFoundHttpException(exception);
            progressBar.setVisibility(View.GONE);
            if (onPageLoadListener != null) {
                onPageLoadListener.onFailure(exception);
            }
        }

        /**
         * Caching 404 error for later use in lifecycle events of fragment
         */
        private void cacheNotFoundHttpException(Exception exception) {
            if (exception instanceof HttpClientErrorException) {
                HttpClientErrorException httpError = (HttpClientErrorException) exception.getCause();
                HttpStatus status = httpError.getStatusCode();
                mReportNotFound = (status == HttpStatus.NOT_FOUND);
            }
        }

        @Override
        public void onSuccess(ExportOutputData output) {
            progressBar.setVisibility(View.GONE);
            outputFinal = output.isFinal();
            loadHtml(output.getData());
            if (onPageLoadListener != null) {
                onPageLoadListener.onSuccess(page);
            }
        }

        @Override
        public void onOutOfRange(boolean isOutOfRange, ErrorDescriptor errorDescriptor ) {
            if (isOutOfRange) {
                SimpleDialogFragment.createBuilder(getActivity(), getActivity().getSupportFragmentManager())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.rv_out_of_range)
                        .setMessage(errorDescriptor.getMessage())
                        .setNegativeButtonText(android.R.string.cancel)
                        .setPositiveButtonText(R.string.rv_dialog_reload)
                        .setTargetFragment(NodeWebViewFragment.this)
                        .setCancelableOnTouchOutside(false)
                        .show();
            } else {
                SimpleDialogFragment.createBuilder(getActivity(), getActivity().getSupportFragmentManager())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(errorDescriptor.getErrorCode())
                        .setMessage(errorDescriptor.getMessage())
                        .setCancelableOnTouchOutside(false)
                        .setTargetFragment(NodeWebViewFragment.this)
                        .show();
            }
        }
    }

    public static interface OnPageLoadListener {
        void onFailure(Exception exception);

        void onSuccess(int page);
    }

}