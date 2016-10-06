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

package com.jaspersoft.android.jaspermobile.webview;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jaspersoft.android.jaspermobile.webview.intercept.WebResourceInterceptor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tom Koptel
 * @since 2.0
 */
public class SystemWebViewClient extends WebViewClient {
    private final JasperWebViewClientListener webViewClientListener;
    private final NativeWebRequestMapper nativeWebRequestMapper;
    private final NativeWebResponseMapper nativeWebResponseMapper;
    private final List<WebResourceInterceptor> requestInterceptors;
    private final List<UrlPolicy> urlPolicies;

    public SystemWebViewClient() {
        this(new Builder());
    }

    private SystemWebViewClient(Builder builder) {
        webViewClientListener = builder.webViewClientListener;
        nativeWebRequestMapper = builder.nativeWebRequestMapper;
        nativeWebResponseMapper = builder.nativeWebResponseMapper;
        requestInterceptors = builder.requestInterceptors;
        urlPolicies = builder.urlPolicies;
    }

    public Builder newBuilder() {
        return new Builder(this);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        boolean defaultResult = super.shouldOverrideUrlLoading(view, url);
        for (UrlPolicy urlPolicy : urlPolicies) {
            defaultResult |= urlPolicy.shouldOverrideUrlLoading(view, url);
        }
        return defaultResult;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        webViewClientListener.onPageStarted(url);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        webViewClientListener.onPageFinishedLoading(url);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        webViewClientListener.onReceivedError(errorCode, description, failingUrl);
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        handler.proceed();
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        WebRequest request = nativeWebRequestMapper.toGenericRequest(url);
        WebResourceResponse webResponse = intercept(view, request);
        if (webResponse == null) {
            return super.shouldInterceptRequest(view, url);
        }
        return webResponse;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest webResourceRequest) {
        WebRequest request = nativeWebRequestMapper.toGenericRequest(webResourceRequest);
        WebResourceResponse webResponse = intercept(view, request);
        if (webResponse == null) {
            return super.shouldInterceptRequest(view, webResourceRequest);
        }
        return webResponse;
    }

    @Nullable
    private WebResourceResponse intercept(WebView view, WebRequest request) {
        for (WebResourceInterceptor requestInterceptor : requestInterceptors) {
            WebResponse webResponse = requestInterceptor.interceptRequest(view, request);
            if (webResponse != null) {
                int statusCode = webResponse.getStatusCode();
                // WebResourceResponse statusCode can be in the [200, 299] range.
                if (statusCode >= 200 && statusCode < 300) {
                    return nativeWebResponseMapper.toNativeResponse(webResponse);
                }
            }
        }
        return null;
    }

    public static class Builder {
        private JasperWebViewClientListener webViewClientListener;
        private List<WebResourceInterceptor> requestInterceptors;
        private List<UrlPolicy> urlPolicies;
        private NativeWebRequestMapper nativeWebRequestMapper;
        private NativeWebResponseMapper nativeWebResponseMapper;

        public Builder() {
            requestInterceptors = new ArrayList<>();
            urlPolicies = new ArrayList<>();
            nativeWebRequestMapper = new NativeWebRequestMapper();
            nativeWebResponseMapper = new NativeWebResponseMapper();
            webViewClientListener = JasperWebViewClientListener.NULL;
        }

        private Builder(SystemWebViewClient client) {
            requestInterceptors = client.requestInterceptors;
            urlPolicies = client.urlPolicies;
            nativeWebRequestMapper = client.nativeWebRequestMapper;
            nativeWebResponseMapper = client.nativeWebResponseMapper;
            webViewClientListener = client.webViewClientListener;
        }

        public Builder withDelegateListener(@Nullable JasperWebViewClientListener webViewClientListener) {
            this.webViewClientListener = (webViewClientListener == null) ? JasperWebViewClientListener.NULL : webViewClientListener;
            return this;
        }

        public Builder registerInterceptor(@NonNull WebResourceInterceptor requestInterceptor) {
            requestInterceptors.add(requestInterceptor);
            return this;
        }

        public Builder registerUrlPolicy(@NonNull UrlPolicy urlPolicy) {
            urlPolicies.add(urlPolicy);
            return this;
        }

        @VisibleForTesting
        Builder withRequestMapper(NativeWebRequestMapper requestMapper) {
            nativeWebRequestMapper = requestMapper;
            return this;
        }


        @VisibleForTesting
        Builder withResponseMapper(NativeWebResponseMapper responseMapper) {
            nativeWebResponseMapper = responseMapper;
            return this;
        }

        public SystemWebViewClient build() {
            return new SystemWebViewClient(this);
        }
    }
}
