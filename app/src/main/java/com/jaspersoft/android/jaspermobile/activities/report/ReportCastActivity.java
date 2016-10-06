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

package com.jaspersoft.android.jaspermobile.activities.report;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.internal.di.modules.activity.ActivityModule;
import com.jaspersoft.android.jaspermobile.internal.di.modules.activity.ReportViewModule;
import com.jaspersoft.android.jaspermobile.util.cast.ResourcePresentationService;
import com.jaspersoft.android.sdk.widget.report.view.ViewAction;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTouch;

/**
 * @author Andrew Tivodar
 * @since 2.6
 */
public class ReportCastActivity extends BaseReportActivity implements ResourcePresentationService.ResourcePresentationCallback {
    private ResourcePresentationService resourcePresentationService;
    private Timer timer;

    @BindView(R.id.controlsContainer)
    LinearLayout controlsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getProfileComponent().plusReportViewer(new ActivityModule(this), new ReportViewModule()).inject(this);
        super.onCreate(savedInstanceState);

        resourcePresentationService = (ResourcePresentationService) ResourcePresentationService.getInstance();
        resourcePresentationService.setReportPresentationCallback(this);
        if (resourcePresentationService.getReportViewer() != null) {
            init(resourcePresentationService.getReportViewer());
            runReport();
        }
        onActionAvailabilityChanged(ActionType.ACTION_TYPE_ALL, reportWidget != null && reportWidget.isControlActionsAvailable());
        timer = new Timer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        cancelScrolling();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        resourcePresentationService.setReportPresentationCallback(null);
    }

    @Override
    protected int provideContentView() {
        return R.layout.activity_report_cast;
    }

    @Override
    protected int provideItemsMenu() {
        return R.menu.report_cast_menu;
    }

    @Override
    protected double provideScale() {
        // To make casting device-dpi independent we are considering device density
        return 0.66f / getResources().getDisplayMetrics().density;
    }

    @Override
    protected String getScreenName() {
        return getString(R.string.ja_rc_s);
    }

    @Override
    public void onPresentationStarted() {
        init(resourcePresentationService.getReportViewer());
        runReport();
    }

    @Override
    public void onPresentationStopped() {
        finish();
    }

    @Override
    public void onActionAvailabilityChanged(ActionType actionType, boolean isAvailable) {
        super.onActionAvailabilityChanged(ActionType.ACTION_TYPE_ALL, isAvailable);

        controlsContainer.setVisibility(isAvailable ? View.VISIBLE : View.GONE);
        resourcePresentationService.onReportRenderStateUpdated();
    }

    @Override
    public void finish() {
        super.finish();
        resourcePresentationService.onCloseReportCasting();
    }

    @OnTouch(R.id.btnScrollUp)
    protected boolean scrollUpAction(MotionEvent event) {
        scrollTo(event, new TimerTask() {
            @Override
            public void run() {
                reportWidget.performViewAction(ViewAction.SCROLL_UP);
            }
        });
        return false;
    }

    @OnTouch(R.id.btnScrollDown)
    protected boolean scrollDownAction(MotionEvent event) {
        scrollTo(event, new TimerTask() {
            @Override
            public void run() {
                reportWidget.performViewAction(ViewAction.SCROLL_DOWN);
            }
        });
        return false;
    }

    @OnTouch(R.id.btnScrollLeft)
    protected boolean scrollLeftAction(MotionEvent event) {
        scrollTo(event, new TimerTask() {
            @Override
            public void run() {
                reportWidget.performViewAction(ViewAction.SCROLL_LEFT);
            }
        });
        return false;
    }

    @OnTouch(R.id.btnScrollRight)
    protected boolean scrollRightAction(MotionEvent event) {
        scrollTo(event, new TimerTask() {
            @Override
            public void run() {
                reportWidget.performViewAction(ViewAction.SCROLL_RIGHT);
            }
        });
        return false;
    }

    @OnClick(R.id.btnZoomIn)
    void zoomInAction() {
        reportWidget.performViewAction(ViewAction.ZOOM_IN);
    }

    @OnClick(R.id.btnZoomOut)
    void zoomOutAction() {
        reportWidget.performViewAction(ViewAction.ZOOM_OUT);
    }

    private void runReport() {
        String reportUri = resourceLookup.getUri();
        if (!reportUri.equals(resourcePresentationService.getCurrentReportUri())) {
            resourcePresentationService.onCloseReportCasting();
            loadMetadata(reportUri);
        }
        resourcePresentationService.onStartReportCasting(resourceLookup);
    }

    private void scrollTo(MotionEvent event, TimerTask task) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            cancelScrolling();
            timer.scheduleAtFixedRate(task, 0, 10);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            cancelScrolling();
        }
    }

    private void cancelScrolling() {
        timer.cancel();
        timer.purge();
        timer = new Timer();
    }

}
