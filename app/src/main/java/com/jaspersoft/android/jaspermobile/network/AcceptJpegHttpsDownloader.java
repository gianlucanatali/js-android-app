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

package com.jaspersoft.android.jaspermobile.network;

import android.content.Context;

import com.jaspersoft.android.jaspermobile.GraphObject;
import com.nostra13.universalimageloader.core.assist.FlushedInputStream;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.IoUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.inject.Inject;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

/**
 * @author Tom Koptel
 * @since 2.0
 */
public class AcceptJpegHttpsDownloader extends BaseImageDownloader {
    private static final String ACCEPT_HEADER = "Accept";

    @Inject
    HostnameVerifier hostnameVerifier;

    @Inject
    SSLSocketFactory sslSocketFactory;

    public AcceptJpegHttpsDownloader(Context context) {
        super(context);
        GraphObject.Factory.from(context).getComponent().inject(this);
    }

    @Override
    protected HttpURLConnection createConnection(String url, Object extra) throws IOException {
        HttpURLConnection conn = super.createConnection(url, extra);
        conn.setRequestProperty(ACCEPT_HEADER, "image/jpeg");
        return conn;
    }

    @Override
    protected InputStream getStreamFromNetwork(String imageUri, Object extra) throws IOException {
        URL url;
        try {
            url = new URL(imageUri);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Incorrect image URI");
        }

        if (Scheme.ofUri(imageUri) != Scheme.HTTPS) {
            return super.getStreamFromNetwork(imageUri, extra);

        }

        HttpsURLConnection.setDefaultSSLSocketFactory(sslSocketFactory);
        HttpsURLConnection https;

        int redirectCount = 0;
        do {
            https = connectHttpsSource(url);
            redirectCount++;
        } while (https.getResponseCode() / 100 == 3 && redirectCount < MAX_REDIRECT_COUNT);

        InputStream imageStream;
        try {
            imageStream = https.getInputStream();
        } catch (IOException e) {
            // Read all data to allow reuse connection (http://bit.ly/1ad35PY)
            IoUtils.readAndCloseStream(https.getErrorStream());
            throw e;
        }
        return new FlushedInputStream(new BufferedInputStream(imageStream));
    }

    private HttpsURLConnection connectHttpsSource(URL url) throws IOException {
        HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
        https.setHostnameVerifier(hostnameVerifier);
        https.setConnectTimeout(connectTimeout);
        https.setReadTimeout(readTimeout);
        https.setRequestProperty(ACCEPT_HEADER, "image/jpeg");
        https.connect();
        return https;
    }
}
