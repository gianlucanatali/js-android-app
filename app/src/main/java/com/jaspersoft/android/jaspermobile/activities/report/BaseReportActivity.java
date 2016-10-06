/*
 * Copyright � 2015 TIBCO Software, Inc. All rights reserved.
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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.inputcontrols.InputControlsActivity;
import com.jaspersoft.android.jaspermobile.activities.inputcontrols.InputControlsActivity_;
import com.jaspersoft.android.jaspermobile.activities.report.bookmarks.BookmarksActivity;
import com.jaspersoft.android.jaspermobile.activities.report.chartTypes.ChartTypesActivity;
import com.jaspersoft.android.jaspermobile.data.JasperRestClient;
import com.jaspersoft.android.jaspermobile.data.entity.mapper.DestinationMapper;
import com.jaspersoft.android.jaspermobile.data.entity.mapper.ReportParamsMapper;
import com.jaspersoft.android.jaspermobile.dialog.NumberDialogFragment;
import com.jaspersoft.android.jaspermobile.domain.JasperServer;
import com.jaspersoft.android.jaspermobile.domain.ReportControlFlags;
import com.jaspersoft.android.jaspermobile.domain.SimpleSubscriber;
import com.jaspersoft.android.jaspermobile.domain.interactor.report.GetReportShowControlsPropertyCase;
import com.jaspersoft.android.jaspermobile.network.RequestExceptionHandler;
import com.jaspersoft.android.jaspermobile.ui.view.activity.CastActivity;
import com.jaspersoft.android.jaspermobile.util.InputControlHolder;
import com.jaspersoft.android.jaspermobile.util.ReportParamsStorage;
import com.jaspersoft.android.jaspermobile.widget.LoadingView;
import com.jaspersoft.android.jaspermobile.widget.ReportErrorActionView;
import com.jaspersoft.android.jaspermobile.widget.ReportPartsTabLayout;
import com.jaspersoft.android.jaspermobile.widget.ReportToolbar;
import com.jaspersoft.android.jaspermobile.widget.SimplePaginationView;
import com.jaspersoft.android.sdk.client.oxm.report.ReportDestination;
import com.jaspersoft.android.sdk.client.oxm.resource.ResourceLookup;
import com.jaspersoft.android.sdk.network.entity.report.ReportParameter;
import com.jaspersoft.android.sdk.service.data.server.ServerInfo;
import com.jaspersoft.android.sdk.service.data.server.ServerVersion;
import com.jaspersoft.android.sdk.service.exception.ServiceException;
import com.jaspersoft.android.sdk.service.exception.StatusCodes;
import com.jaspersoft.android.sdk.widget.report.renderer.Bookmark;
import com.jaspersoft.android.sdk.widget.report.renderer.ChartType;
import com.jaspersoft.android.sdk.widget.report.renderer.Destination;
import com.jaspersoft.android.sdk.widget.report.renderer.ReportComponent;
import com.jaspersoft.android.sdk.widget.report.renderer.ReportPart;
import com.jaspersoft.android.sdk.widget.report.renderer.RunOptions;
import com.jaspersoft.android.sdk.widget.report.renderer.hyperlink.Hyperlink;
import com.jaspersoft.android.sdk.widget.report.view.ReportEventListener;
import com.jaspersoft.android.sdk.widget.report.view.ReportProperties;
import com.jaspersoft.android.sdk.widget.report.view.ReportWidget;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Andrew Tivodar
 * @since 2.6
 */
public abstract class BaseReportActivity extends CastActivity implements Toolbar.OnMenuItemClickListener, ReportPartsTabLayout.ReportPartSelectListener,
        ReportEventListener, SimplePaginationView.PageSelectListener, NumberDialogFragment.NumberDialogClickListener {
    public static final String RESOURCE_LOOKUP_ARG = "resource_lookup";
    public static final String REPORT_DESTINATION_ARG = "report_destination";

    private static final int REPORT_FILTERS_CODE = 100;
    private static final int BOOKMARKS_CODE = 101;
    private static final int CHART_TYPES_CODE = 102;

    protected ReportWidget reportWidget;
    @BindView(R.id.loading)
    LoadingView loading;
    @BindView(R.id.reportToolbar)
    ReportToolbar reportToolbar;
    @BindView(R.id.reportPartsTabs)
    ReportPartsTabLayout reportPartsTabs;
    @BindView(R.id.reportMessage)
    ReportErrorActionView reportErrorActionView;
    @BindView(R.id.paginationControl)
    SimplePaginationView paginationView;

    @Inject
    JasperServer jasperServer;
    @Inject
    JasperRestClient jasperRestClient;
    @Inject
    GetReportShowControlsPropertyCase getReportShowControlsPropertyCase;
    @Inject
    ReportParamsStorage reportParamsStorage;
    @Inject
    ReportParamsMapper paramsMapper;
    @Inject
    DestinationMapper destinationMapper;
    @Inject
    RequestExceptionHandler requestExceptionHandler;

    protected ResourceLookup resourceLookup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(provideContentView());
        ButterKnife.bind(this);

        resourceLookup = getResourceLookup();
        initToolbar(resourceLookup.getLabel());
        reportPartsTabs.setReportPartSelectListener(this);
        paginationView.setPageSelectListener(this);
        reportErrorActionView.setOnActionClickListener(new HandleErrorActionClickListener());

        getReportShowControlsPropertyCase.updateSubscriber(new ReportShowControlsSubscriber());
    }

    @Override
    public void onReportPartSelected(int index) {
        ReportPart reportPart = reportWidget.getReportProperties().getReportPartList().get(index);
        reportWidget.navigateToPage(reportPart.getPage());
    }

    @Override
    public void onPageSelected(int page) {
        reportWidget.navigateToPage(page);
    }

    @Override
    public void onRemotePageSelected(Integer pagesCount) {
        NumberDialogFragment.createBuilder(getSupportFragmentManager())
                .setMaxValue(pagesCount == null ? Integer.MAX_VALUE : pagesCount)
                .show();
    }

    @Override
    public void onNumberSubmit(int number, int requestCode) {
        reportWidget.navigateToPage(number);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refreshAction:
                refresh();
                return true;
            case R.id.filtersAction:
                showFiltersPage();
                return true;
            case R.id.bookmarksAction:
                showBookmarksPage();
                return true;
            case android.R.id.home:
                finish();
                return true;
            case R.id.chageChartTypesAction:
                changeChartType();
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;

        if (requestCode == REPORT_FILTERS_CODE) {
            boolean isNewParamsEqualOld = data.getBooleanExtra(InputControlsActivity.RESULT_SAME_PARAMS, false);

            if (reportWidget.isControlActionsAvailable() && !isNewParamsEqualOld) {
                applyParams();
            } else {
                runReport(resourceLookup.getUri());
            }
        } else if (requestCode == BOOKMARKS_CODE) {
            Bookmark bookmark = data.getExtras().getParcelable(BookmarksActivity.SELECTED_BOOKMARK_ARG);
            if (bookmark == null) {
                throw new RuntimeException("Selected bookmark should be provided");
            }
            reportWidget.navigateToPage(bookmark.getPage());
        } else if (requestCode == CHART_TYPES_CODE) {
            ChartType chartType = data.getExtras().getParcelable(ChartTypesActivity.SELECTED_CHART_TYPE_ARG);
            if (chartType == null) {
                throw new RuntimeException("Selected chartType should be provided");
            }
            ReportProperties reportProperties = reportWidget.getReportProperties();
            if (reportProperties.getComponents().size() > 1) {
                throw new RuntimeException("Support only elastic charts");
            }
            ReportComponent component = reportProperties.getComponents().get(0);
            reportWidget.updateChartType(component, chartType);
        }
    }

    protected abstract int provideContentView();

    protected abstract int provideItemsMenu();

    protected abstract double provideScale();

    protected final boolean init(ReportWidget reportViewer) {
        this.reportWidget = reportViewer;

        reportViewer.setReportEventListener(this);
        reportViewer.setReportPaginationListener(paginationView);
        reportViewer.setReportBookmarkListener(reportToolbar);
        reportViewer.setReportPartsListener(reportPartsTabs);

        boolean inited = false;
        if (!reportViewer.isInited()) {
            reportViewer.init(jasperRestClient.authorizedClient(), mapServerInfo(), provideScale());
            inited = true;
        }

        ReportProperties reportProperties = reportViewer.getReportProperties();
        paginationView.setReportProperties(reportProperties);
        reportPartsTabs.setReportProperties(reportProperties);

        return inited;
    }

    protected void loadMetadata(String reportUri) {
        getReportShowControlsPropertyCase.execute(reportUri, new ReportShowControlsSubscriber());
    }

    protected void initToolbar(String title) {
        reportToolbar.inflateMenu(provideItemsMenu());
        reportToolbar.setOnMenuItemClickListener(this);
        reportToolbar.setTitle(title);

        onToolbarMenuCreated(reportToolbar.getMenu().findItem(R.id.castAction));
    }

    @NotNull
    private ResourceLookup getResourceLookup() {
        Bundle extras = getIntent().getExtras();
        ResourceLookup resourceLookup = null;
        if (extras != null) {
            resourceLookup = extras.getParcelable(RESOURCE_LOOKUP_ARG);
        }
        if (resourceLookup == null) {
            throw new RuntimeException("Resource lookup should be provided");
        }
        return resourceLookup;
    }

    private Destination getReportDestination() {
        Bundle extras = getIntent().getExtras();
        ReportDestination reportDestination = null;
        if (extras != null) {
            reportDestination = (ReportDestination) extras.getSerializable(REPORT_DESTINATION_ARG);
        }
        if (reportDestination != null) {
            return destinationMapper.toDestination(reportDestination);
        }
        return null;
    }

    private void runReport(String reportUri) {
        Destination destination = getReportDestination();
        reportWidget.run(new RunOptions.Builder()
                .reportUri(reportUri)
                .parameters(getReportParams())
                .destination(destination)
                .build());
        reportErrorActionView.setVisibility(View.GONE);
    }

    private void applyParams() {
        reportWidget.applyParams(getReportParams());
    }

    private void refresh() {
        reportWidget.refresh();
    }

    private List<ReportParameter> getReportParams() {
        InputControlHolder icHolder = reportParamsStorage.getInputControlHolder(resourceLookup.getUri());
        return paramsMapper.legacyParamsToRetrofitted(icHolder.getReportParams());
    }

    @NotNull
    private ServerInfo mapServerInfo() {
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setEdition(jasperServer.getEdition());
        serverInfo.setVersion(ServerVersion.valueOf(jasperServer.getVersion()));
        return serverInfo;
    }

    private void showFiltersPage() {
        InputControlsActivity_.intent(this)
                .reportUri(resourceLookup.getUri())
                .startForResult(REPORT_FILTERS_CODE);
    }

    private void showBookmarksPage() {
        Intent bookmarksIntent = new Intent(this, BookmarksActivity.class);
        List<Bookmark> bookmarkList = reportWidget.getReportProperties().getBookmarkList();
        ArrayList<Bookmark> bookmarks = new ArrayList<>(bookmarkList);
        bookmarksIntent.putParcelableArrayListExtra(BookmarksActivity.BOOKMARK_LIST_ARG, bookmarks);
        startActivityForResult(bookmarksIntent, BOOKMARKS_CODE);
    }

    private void showErrorMessage(String errorMessage, int handleAction) {
        reportErrorActionView.setVisibility(View.VISIBLE);
        reportErrorActionView.showError(errorMessage, handleAction);
    }

    private void changeChartType() {
        Intent chartTypesIntent = new Intent(this, ChartTypesActivity.class);
        List<ChartType> chartTypesList = reportWidget.getAvailableChartTypes();
        ArrayList<ChartType> chartTypes = new ArrayList<>(chartTypesList);
        chartTypesIntent.putParcelableArrayListExtra(ChartTypesActivity.CHART_TYPES_ARG, chartTypes);

        List<ReportComponent> reportComponents = reportWidget.getReportProperties().getComponents();
        if (reportComponents.size() > 1) {
            // For now we don't support multi charts
            // TODO: should we throw exception?
            return;
        }
        ReportComponent chartComponent = reportComponents.get(0);
        ChartType chartType = chartComponent.getChartTypeInstance();
        if (chartType == null) {
            throw new RuntimeException("Chart Type should be set");
        }

        chartTypesIntent.putExtra(ChartTypesActivity.SELECTED_CHART_TYPE_ARG, chartType);
        startActivityForResult(chartTypesIntent, CHART_TYPES_CODE);
    }

    /*
     *  ReportEventListener Impl
     */

    @Override
    public void onActionAvailabilityChanged(ActionType actionType, boolean isAvailable) {
        switch (actionType) {
            case ACTION_TYPE_ALL: {
                reportToolbar.setActionGroupEnabled(isAvailable);
                reportPartsTabs.setEnabled(isAvailable);
                paginationView.setEnabled(isAvailable);
            }
            case ACTION_TYPE_CHANGE_CHART_TYPE: {
                reportToolbar.setChangeChartTypeAvailable(isAvailable);
            }
        }
    }

    @Override
    public void onHyperlinkClicked(Hyperlink hyperlink) {

    }

    @Override
    public void onExternalLinkOpened(String url) {

    }

    @Override
    public void onError(ServiceException exception) {
        switch (exception.code()) {
            case StatusCodes.AUTHORIZATION_ERROR:
                showErrorMessage(getString(R.string.da_session_expired), ReportErrorActionView.RELOAD_ACTION);
                break;
            case StatusCodes.REPORT_EXECUTION_EMPTY:
                if (reportToolbar.isFilterAvailable()) {
                    showErrorMessage(getString(R.string.rv_error_empty_report) + " " + getString(R.string.rv_apply_filters_no_data), ReportErrorActionView.APPLY_FILTERS_ACTION);
                } else {
                    showErrorMessage(getString(R.string.rv_error_empty_report), ReportErrorActionView.NO_ACTION);
                }
                break;
            case StatusCodes.EXPORT_PAGE_OUT_OF_RANGE:
            case StatusCodes.EXPORT_EXECUTION_CANCELLED:
            case StatusCodes.EXPORT_EXECUTION_FAILED:
            case StatusCodes.EXPORT_ANCHOR_ABSENT:
                showErrorMessage(getString(R.string.sr_failed_to_execute_report), ReportErrorActionView.NO_ACTION);
                requestExceptionHandler.showCommonErrorMessage(exception);
                break;
            default:
                String errorMessage = requestExceptionHandler.extractMessage(exception);
                showErrorMessage(errorMessage, ReportErrorActionView.RELOAD_ACTION);
        }
    }

    /*
     * Private Classes
     */

    private class ReportShowControlsSubscriber extends SimpleSubscriber<ReportControlFlags> {
        @Override
        public void onNext(ReportControlFlags flags) {
            boolean filtersAvailable = flags.hasControls();
            boolean needPrompt = flags.needPrompt();

            if (filtersAvailable && needPrompt) {
                showErrorMessage(getString(R.string.rv_apply_filters_message), ReportErrorActionView.APPLY_FILTERS_ACTION);
                showFiltersPage();
            } else {
                runReport(resourceLookup.getUri());
            }

            reportToolbar.setFilterAvailable(filtersAvailable);
        }

        @Override
        public void onError(Throwable e) {
            String errorMessage = requestExceptionHandler.extractMessage(e);
            showErrorMessage(errorMessage, ReportErrorActionView.RELOAD_ACTION);
        }
    }

    private class HandleErrorActionClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (reportErrorActionView.getAction() == ReportErrorActionView.APPLY_FILTERS_ACTION) {
                showFiltersPage();
            } else if (reportErrorActionView.getAction() == ReportErrorActionView.RELOAD_ACTION) {
                reportErrorActionView.setVisibility(View.GONE);
                loadMetadata(resourceLookup.getUri());
            }
        }
    }
}
