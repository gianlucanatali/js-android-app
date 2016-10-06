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

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.save.SaveReportActivity_;
import com.jaspersoft.android.jaspermobile.activities.share.AnnotationActivity_;
import com.jaspersoft.android.jaspermobile.dialog.ProgressDialogFragment;
import com.jaspersoft.android.jaspermobile.dialog.SimpleDialogFragment;
import com.jaspersoft.android.jaspermobile.domain.ResourceDetailsRequest;
import com.jaspersoft.android.jaspermobile.domain.ScreenCapture;
import com.jaspersoft.android.jaspermobile.domain.SimpleSubscriber;
import com.jaspersoft.android.jaspermobile.domain.interactor.resource.GetResourceDetailsByTypeCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.resource.SaveScreenCaptureCase;
import com.jaspersoft.android.jaspermobile.internal.di.modules.activity.ActivityModule;
import com.jaspersoft.android.jaspermobile.internal.di.modules.activity.ReportViewModule;
import com.jaspersoft.android.jaspermobile.ui.view.activity.schedule.NewScheduleActivity_;
import com.jaspersoft.android.jaspermobile.util.FavoritesHelper;
import com.jaspersoft.android.jaspermobile.util.InputControlHolder;
import com.jaspersoft.android.jaspermobile.util.ResourceOpener;
import com.jaspersoft.android.jaspermobile.util.ResourceOpener_;
import com.jaspersoft.android.jaspermobile.util.print.ReportPrintJob;
import com.jaspersoft.android.jaspermobile.util.print.ResourcePrintJob;
import com.jaspersoft.android.jaspermobile.util.resource.JasperResource;
import com.jaspersoft.android.jaspermobile.util.resource.viewbinder.JasperResourceConverter;
import com.jaspersoft.android.sdk.client.oxm.report.ReportDestination;
import com.jaspersoft.android.sdk.client.oxm.report.ReportParameter;
import com.jaspersoft.android.sdk.client.oxm.resource.ResourceLookup;
import com.jaspersoft.android.sdk.util.FileUtils;
import com.jaspersoft.android.sdk.widget.report.renderer.RunOptions;
import com.jaspersoft.android.sdk.widget.report.renderer.hyperlink.Hyperlink;
import com.jaspersoft.android.sdk.widget.report.renderer.hyperlink.ReferenceHyperlink;
import com.jaspersoft.android.sdk.widget.report.renderer.hyperlink.RemoteHyperlink;
import com.jaspersoft.android.sdk.widget.report.renderer.hyperlink.ReportExecutionHyperlink;
import com.jaspersoft.android.sdk.widget.report.view.ReportFragment;

import java.io.File;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Andrew Tivodar
 * @since 2.6
 */
public class ReportViewActivity extends BaseReportActivity {
    @Inject
    GetResourceDetailsByTypeCase getResourceDetailsByTypeCase;
    @Inject
    SaveScreenCaptureCase saveScreenCaptureCase;
    @Inject
    ReportPrintJob resourcePrintJob;
    @Inject
    JasperResourceConverter jasperResourceConverter;
    @Inject
    FavoritesHelper favoritesHelper;
    @Inject
    @Named("device_screen_diagonal")
    Double screenDiagonal;

    private ResourceOpener resourceOpener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getProfileComponent().plusReportViewer(new ActivityModule(this), new ReportViewModule()).inject(this);
        super.onCreate(savedInstanceState);
        resourceOpener = ResourceOpener_.getInstance_(this);

        if (init((ReportFragment) getSupportFragmentManager().findFragmentById(R.id.reportFragment))) {
            loadMetadata(resourceLookup.getUri());
        }
        onActionAvailabilityChanged(ActionType.ACTION_TYPE_ALL, reportWidget != null && reportWidget.isControlActionsAvailable());
    }

    @Override
    protected void onCastServiceStarted() {
        Intent castIntent = new Intent(this, ReportCastActivity.class);
        castIntent.putExtra(BaseReportActivity.RESOURCE_LOOKUP_ARG, resourceLookup);
        startActivity(castIntent);
        finish();
    }

    @Override
    protected String getScreenName() {
        //TODO SCREEN NAME FOR VIS AND REST
        return "";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (isFinishing()) {
            getResourceDetailsByTypeCase.unsubscribe();
            getReportShowControlsPropertyCase.unsubscribe();
            saveScreenCaptureCase.unsubscribe();
            reportParamsStorage.clearInputControlHolder(resourceLookup.getUri());
        }
    }

    @Override
    public void onHyperlinkClicked(Hyperlink hyperlink) {
        if (hyperlink instanceof ReferenceHyperlink) {
            Uri reference = ((ReferenceHyperlink) hyperlink).getReference();
            showReference(reference);
        } else if (hyperlink instanceof RemoteHyperlink) {
            Uri resourceUri = ((RemoteHyperlink) hyperlink).getResourceUri();
            resourceOpener.showFile(resourceUri.toString());
        } else if (hyperlink instanceof ReportExecutionHyperlink) {
            String resourceType = ResourceLookup.ResourceType.reportUnit.name();
            String reportUri = ((ReportExecutionHyperlink) hyperlink).getRunOptions().getReportUri();
            ResourceDetailsRequest resource = new ResourceDetailsRequest(reportUri, resourceType);
            getResourceDetailsByTypeCase.execute(resource, new GetResourceDetailListener(((ReportExecutionHyperlink) hyperlink).getRunOptions()));
        }
    }

    @Override
    public void onExternalLinkOpened(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, getString(R.string.sdr_t_no_app_available, "URL"), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.favoriteAction:
                favoritesHelper.switchFavoriteState(resourceLookup, item);
                return true;
            case R.id.saveAction:
                showSaveReportPage();
                return true;
            case R.id.printAction:
                showPrintPage();
                return true;
            case R.id.aboutAction:
                showReportInfo();
                return true;
            case R.id.scheduleAction:
                showSchedulePage();
                return true;
            case R.id.shareAction:
                makeScreenShot();
                return true;
            default:
                return super.onMenuItemClick(item);
        }
    }

    @Override
    protected int provideContentView() {
        return R.layout.activity_report;
    }

    @Override
    protected int provideItemsMenu() {
        return R.menu.report_menu;
    }

    @Override
    protected double provideScale() {
        // Scale depends on device screen size. In this case amount of info that appear is same as for standart tablet (10.1 inch)
        return screenDiagonal / 10.1;
    }

    @Override
    protected void initToolbar(String title) {
        super.initToolbar(title);
        favoritesHelper.updateFavoriteIconState(reportToolbar.getMenu().findItem(R.id.favoriteAction), resourceLookup.getUri());
    }

    private void showSaveReportPage() {
        if (FileUtils.isExternalStorageWritable()) {
            SaveReportActivity_.intent(this)
                    .resource(resourceLookup)
                    .pageCount(getTotalPageCount())
                    .start();
        } else {
            Toast.makeText(this, R.string.rv_t_external_storage_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    private void showPrintPage() {
        Bundle args = new Bundle();
        args.putString(ReportPrintJob.REPORT_URI_KEY, resourceLookup.getUri());
        args.putInt(ReportPrintJob.TOTAL_PAGES_KEY, getTotalPageCount());
        args.putString(ResourcePrintJob.PRINT_NAME_KEY, resourceLookup.getLabel());

        resourcePrintJob.printResource(args);
    }

    private void showReportInfo() {
        SimpleDialogFragment.createBuilder(this, getSupportFragmentManager())
                .setTitle(resourceLookup.getLabel())
                .setMessage(resourceLookup.getDescription())
                .setNegativeButtonText(R.string.ok)
                .show();
    }

    private void showSchedulePage() {
        JasperResource reportResource = jasperResourceConverter.convertToJasperResource(resourceLookup);

        NewScheduleActivity_.intent(this)
                .jasperResource(reportResource)
                .start();
    }

    private void showSharePage(File file) {
        Intent intent = AnnotationActivity_.intent(this)
                .imageUri(Uri.fromFile(file))
                .get();
        startActivity(intent);
    }

    private void showReference(Uri externalLink) {
        String title = getString(R.string.rv_open_link_chooser);
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, externalLink);
        Intent chooser = Intent.createChooser(browserIntent, title);
        if (browserIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(chooser);
        }
    }

    private void makeScreenShot() {
        ScreenCapture reportScreenCapture = ScreenCapture.Factory.capture(reportWidget.getView());
        saveScreenCaptureCase.execute(reportScreenCapture, new SaveScreenCaptureListener());
    }

    private int getTotalPageCount() {
        Integer pagesCount = reportWidget.getReportProperties().getPagesCount();
        return pagesCount == null ? 1 : pagesCount;
    }

    private class GetResourceDetailListener extends SimpleSubscriber<ResourceLookup> {
        private final RunOptions runOptions;

        private GetResourceDetailListener(RunOptions runOptions) {
            this.runOptions = runOptions;
        }

        public void onStart() {
            ProgressDialogFragment.builder(getSupportFragmentManager())
                    .setLoadingMessage(R.string.loading_msg)
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            getResourceDetailsByTypeCase.unsubscribe();
                        }
                    })
                    .show();
        }

        @Override
        public void onError(Throwable e) {
            requestExceptionHandler.showAuthErrorIfExists(e);
        }

        @Override
        public void onNext(ResourceLookup item) {
            List<com.jaspersoft.android.sdk.network.entity.report.ReportParameter> reportParams = runOptions.getParameters();
            List<ReportParameter> legacyReportParams = paramsMapper.retrofittedParamsToLegacy(reportParams);
            ReportDestination reportDestination = destinationMapper.toReportDestination(runOptions.getDestination());

            InputControlHolder icHolder = reportParamsStorage.getInputControlHolder(item.getUri());
            icHolder.setReportParams(legacyReportParams);

            resourceOpener.runReport(item, reportDestination);
        }

        @Override
        public void onCompleted() {
            ProgressDialogFragment.dismiss(getSupportFragmentManager());
        }
    }

    private class SaveScreenCaptureListener extends SimpleSubscriber<File> {
        @Override
        public void onStart() {
            ProgressDialogFragment.builder(getSupportFragmentManager())
                    .setLoadingMessage(R.string.loading_msg)
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            saveScreenCaptureCase.unsubscribe();
                        }
                    })
                    .show();
        }

        @Override
        public void onError(Throwable e) {
            Toast.makeText(ReportViewActivity.this, getString(R.string.wrong_action), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNext(File item) {
            showSharePage(item);
        }

        @Override
        public void onCompleted() {
            ProgressDialogFragment.dismiss(getSupportFragmentManager());
        }
    }
}
