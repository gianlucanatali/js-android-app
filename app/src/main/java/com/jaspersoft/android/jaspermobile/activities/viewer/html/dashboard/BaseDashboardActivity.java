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

package com.jaspersoft.android.jaspermobile.activities.viewer.html.dashboard;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.jaspersoft.android.jaspermobile.Analytics;
import com.jaspersoft.android.jaspermobile.BuildConfig;
import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.robospice.RoboToolbarActivity;
import com.jaspersoft.android.jaspermobile.cookie.CookieManagerFactory;
import com.jaspersoft.android.jaspermobile.dialog.LogDialog;
import com.jaspersoft.android.jaspermobile.dialog.ProgressDialogFragment;
import com.jaspersoft.android.jaspermobile.dialog.SimpleDialogFragment;
import com.jaspersoft.android.jaspermobile.util.FavoritesHelper_;
import com.jaspersoft.android.jaspermobile.util.account.JasperAccountManager;
import com.jaspersoft.android.jaspermobile.util.print.JasperPrintJobFactory;
import com.jaspersoft.android.jaspermobile.util.print.JasperPrinter;
import com.jaspersoft.android.jaspermobile.util.print.ResourcePrintJob;
import com.jaspersoft.android.jaspermobile.webview.DefaultUrlPolicy;
import com.jaspersoft.android.jaspermobile.webview.JasperChromeClientListenerImpl;
import com.jaspersoft.android.jaspermobile.webview.JasperWebViewClientListener;
import com.jaspersoft.android.jaspermobile.webview.SystemChromeClient;
import com.jaspersoft.android.jaspermobile.webview.SystemWebViewClient;
import com.jaspersoft.android.jaspermobile.webview.UrlPolicy;
import com.jaspersoft.android.jaspermobile.webview.WebViewEnvironment;
import com.jaspersoft.android.jaspermobile.webview.dashboard.InjectionRequestInterceptor;
import com.jaspersoft.android.sdk.client.oxm.resource.ResourceLookup;

import rx.Subscriber;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Activity that performs dashboard viewing in HTML format through native component.
 *
 * @author Tom Koptel
 * @since 2.0
 */
public abstract class BaseDashboardActivity extends RoboToolbarActivity
        implements JasperWebViewClientListener, DefaultUrlPolicy.SessionListener {
    public final static String RESOURCE_EXTRA = "resource";

    protected WebView webView;
    private TextView emptyView;
    private ProgressBar progressBar;

    protected ResourceLookup resource;
    private MenuItem favoriteAction;

    private FavoritesHelper_ favoritesHelper;
    private JasperChromeClientListenerImpl chromeClientListener;

    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    @Inject
    protected Analytics analytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_viewer);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            resource = extras.getParcelable(RESOURCE_EXTRA);
        }

        favoritesHelper = FavoritesHelper_.getInstance_(this);

        webView = (WebView) findViewById(R.id.webView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        emptyView = (TextView) findViewById(android.R.id.empty);

        showProgressDialog(R.string.loading_msg);
        Subscription cookieSubscription = CookieManagerFactory.syncCookies(this).subscribe(new Subscriber<Void>() {
            @Override
            public void onCompleted() {
                hideProgressDialog();
                initWebView();
            }

            @Override
            public void onError(Throwable e) {
                hideProgressDialog();
                showMessage(e.getMessage());
            }

            @Override
            public void onNext(Void aVoid) {

            }
        });
        mCompositeSubscription.add(cookieSubscription);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.dashboard_menu, menu);
        favoriteAction = menu.findItem(R.id.favoriteAction);

        favoritesHelper.updateFavoriteIconState(favoriteAction, resource.getUri());

        if (isDebugOrQa()) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.debug, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.refreshAction) {
            onRefresh();
        }
        if (itemId == R.id.aboutAction) {
            aboutAction();
        }
        if (itemId == R.id.favoriteAction) {
            favoriteAction();
        }
        if (itemId == R.id.showLog) {
            showLog();
        }
        if (itemId == android.R.id.home) {
            onHomeAsUpCalled();
        }
        if (itemId == R.id.printAction) {
            analytics.sendEvent(Analytics.EventCategory.RESOURCE.getValue(), Analytics.EventAction.PRINTED.getValue(), Analytics.EventLabel.DASHBOARD.getValue());
            ResourcePrintJob job = JasperPrintJobFactory.createDashboardPrintJob(webView, resource);
            JasperPrinter.print(job);
        }

        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCompositeSubscription.unsubscribe();
        mCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.removeAllViews();
            webView.destroy();
        }
    }

    //---------------------------------------------------------------------
    // Protected Util method
    //---------------------------------------------------------------------

    protected void resetZoom() {
        while (webView.zoomOut()) ;
    }

    protected void showMessage(CharSequence message) {
        if (!TextUtils.isEmpty(message) && emptyView != null) {
            emptyView.setVisibility(View.VISIBLE);
            emptyView.setText(message);
        }
    }

    private void showProgressDialog(int message) {
        ProgressDialogFragment.builder(getSupportFragmentManager())
                .setLoadingMessage(message)
                .show();
    }

    private void hideProgressDialog() {
        ProgressDialogFragment.dismiss(getSupportFragmentManager());
    }

    //---------------------------------------------------------------------
    // JasperWebViewClientListener callbacks
    //---------------------------------------------------------------------

    @Override
    public void onPageStarted(String newUrl) {
    }

    @Override
    public void onReceivedError(int errorCode, String description, String failingUrl) {
    }

    @Override
    public void onPageFinishedLoading(String url) {
        onPageFinished();
    }

    //---------------------------------------------------------------------
    // DefaultUrlPolicy.SessionListener callback
    //---------------------------------------------------------------------

    @Override
    public void onSessionExpired() {
        showProgressDialog(R.string.loading_msg);
        JasperAccountManager.get(BaseDashboardActivity.this).invalidateActiveToken();
        Subscription cookieSubscription = CookieManagerFactory.syncCookies(this)
                .subscribe(new Subscriber<Void>() {
                    @Override
                    public void onCompleted() {
                        onSessionRefreshed();
                        hideProgressDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideProgressDialog();
                        showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(Void aVoid) {

                    }
                });
        mCompositeSubscription.add(cookieSubscription);
    }

    //---------------------------------------------------------------------
    // Abstract methods
    //---------------------------------------------------------------------

    public abstract void onWebViewConfigured(WebView webView);

    public abstract void onPageFinished();

    public abstract void onRefresh();

    public abstract void onHomeAsUpCalled();

    public abstract void onSessionRefreshed();

    //---------------------------------------------------------------------
    // Helper methods
    //---------------------------------------------------------------------

    private boolean isDebugOrQa() {
        return BuildConfig.FLAVOR.equals("qa") || BuildConfig.DEBUG;
    }

    private void initWebView() {
        chromeClientListener = new JasperChromeClientListenerImpl(progressBar);

        UrlPolicy defaultPolicy = DefaultUrlPolicy.from(this).withSessionListener(this);

        SystemChromeClient systemChromeClient = new SystemChromeClient.Builder(this)
                .withDelegateListener(chromeClientListener)
                .build();
        SystemWebViewClient systemWebViewClient = new SystemWebViewClient.Builder()
                .withDelegateListener(this)
                .registerInterceptor(new InjectionRequestInterceptor())
                .registerUrlPolicy(defaultPolicy)
                .build();

        WebViewEnvironment.configure(webView)
                .withDefaultSettings()
                .withChromeClient(systemChromeClient)
                .withWebClient(systemWebViewClient);
        onWebViewConfigured(webView);
    }

    private void favoriteAction() {
        favoritesHelper.switchFavoriteState(resource, favoriteAction);
    }

    private void aboutAction() {
        SimpleDialogFragment.createBuilder(this, getSupportFragmentManager())
                .setTitle(resource.getLabel())
                .setMessage(resource.getDescription())
                .setNegativeButtonText(R.string.ok)
                .show();
    }

    private void showLog() {
        if (chromeClientListener != null) {
            LogDialog.create(getSupportFragmentManager(), chromeClientListener.getMessages());
        }
    }

}
