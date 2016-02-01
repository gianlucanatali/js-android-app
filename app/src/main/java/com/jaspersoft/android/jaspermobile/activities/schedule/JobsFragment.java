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

package com.jaspersoft.android.jaspermobile.activities.schedule;

import android.accounts.Account;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.jaspersoft.android.jaspermobile.Analytics;
import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.robospice.RoboSpiceFragment;
import com.jaspersoft.android.jaspermobile.activities.robospice.RoboToolbarActivity;
import com.jaspersoft.android.jaspermobile.dialog.ProgressDialogFragment;
import com.jaspersoft.android.jaspermobile.dialog.SimpleDialogFragment;
import com.jaspersoft.android.jaspermobile.util.ViewType;
import com.jaspersoft.android.jaspermobile.util.account.JasperAccountManager;
import com.jaspersoft.android.jaspermobile.util.resource.JasperResource;
import com.jaspersoft.android.jaspermobile.util.resource.viewbinder.JasperResourceAdapter;
import com.jaspersoft.android.jaspermobile.util.resource.viewbinder.JasperResourceConverter;
import com.jaspersoft.android.jaspermobile.util.rx.RxTransformers;
import com.jaspersoft.android.jaspermobile.util.security.PasswordManager;
import com.jaspersoft.android.jaspermobile.widget.JasperRecyclerView;
import com.jaspersoft.android.sdk.client.JsRestClient;
import com.jaspersoft.android.sdk.client.JsServerProfile;
import com.jaspersoft.android.sdk.network.AuthorizedClient;
import com.jaspersoft.android.sdk.network.Server;
import com.jaspersoft.android.sdk.network.SpringCredentials;
import com.jaspersoft.android.sdk.service.data.schedule.JobUnit;
import com.jaspersoft.android.sdk.service.report.schedule.JobSearchCriteria;
import com.jaspersoft.android.sdk.service.report.schedule.JobSortType;
import com.jaspersoft.android.sdk.service.rx.report.schedule.RxJobSearchTask;
import com.jaspersoft.android.sdk.service.rx.report.schedule.RxReportScheduleService;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import roboguice.inject.InjectView;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
@EFragment(R.layout.fragment_refreshable_resource)
public class JobsFragment extends RoboSpiceFragment implements SwipeRefreshLayout.OnRefreshListener {

    @InjectView(android.R.id.list)
    protected JasperRecyclerView listView;
    @InjectView(R.id.refreshLayout)
    protected SwipeRefreshLayout swipeRefreshLayout;
    @InjectView(android.R.id.empty)
    protected TextView message;

    @Inject
    protected Analytics analytics;
    @Inject
    protected JsRestClient jsRestClient;

    @Bean
    protected PasswordManager mPasswordManager;

    private JasperResourceAdapter mAdapter;
    private RxReportScheduleService mScheduleService;
    private RxJobSearchTask mJobSearchTask;
    private CompositeSubscription mCompositeSubscription;
    private LinkedHashMap<Integer, JobUnit> mJobs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCompositeSubscription = new CompositeSubscription();

        if (savedInstanceState == null) {
            analytics.sendEvent(Analytics.EventCategory.CATALOG.getValue(), Analytics.EventAction.VIEWED.getValue(), Analytics.EventLabel.JOBS.getValue());
        }

        ((RoboToolbarActivity) getActivity()).setCustomToolbarView(null);
    }

    @Override
    public void onViewCreated(View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.js_blue,
                R.color.js_dark_blue,
                R.color.js_blue,
                R.color.js_dark_blue);

        setDataAdapter();
        createJobSearchTask();
    }

    @Override
    public void onRefresh() {
        createJobSearchTask();

        analytics.sendEvent(Analytics.EventCategory.CATALOG.getValue(), Analytics.EventAction.REFRESHED.getValue(), Analytics.EventLabel.JOBS.getValue());
    }

    @Override
    public void onResume() {
        super.onResume();

        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.sch_jobs));
        }

        List<Analytics.Dimension> viewDimension = new ArrayList<>();
        analytics.sendScreenView(Analytics.ScreenName.JOBS.getValue(), viewDimension);
    }

    @Override
    public void onPause() {
        swipeRefreshLayout.clearAnimation();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        listView.setVisibility(View.GONE);
        mCompositeSubscription.unsubscribe();
    }

    private void createJobSearchTask() {
        initRestClient()
                .compose(RxTransformers.<RxReportScheduleService>applySchedulers())
                .subscribe(new Subscriber<RxReportScheduleService>() {
                    @Override
                    public void onStart() {
                        mJobs = new LinkedHashMap<>();
                        showMessage(getString(R.string.loading_msg));
                    }

                    @Override
                    public void onCompleted() {
                        loadJobs();
                    }

                    @Override
                    public void onError(Throwable e) {
                        // TODO: handle error
                        showMessage(getString(R.string.failed_load_data));
                    }

                    @Override
                    public void onNext(RxReportScheduleService service) {
                        JobSearchCriteria jobSearchCriteria = JobSearchCriteria.builder()
                                .withSortType(JobSortType.SORTBY_JOBNAME)
                                .build();
                        mScheduleService = service;
                        mJobSearchTask = service.search(jobSearchCriteria);
                    }
                });
    }

    private void loadJobs() {
        Subscription subscription = mJobSearchTask.nextLookup()
                .compose(RxTransformers.<List<JobUnit>>applySchedulers())
                .subscribe(new Subscriber<List<JobUnit>>() {
                    @Override
                    public void onStart() {
                        setRefreshState(true);
                    }

                    @Override
                    public void onCompleted() {
                        showMessage(mJobs.isEmpty() ? getString(R.string.sch_not_found) : null);
                        setRefreshState(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        // TODO: handle error
                        showMessage(getString(R.string.failed_load_data));
                        setRefreshState(false);
                    }

                    @Override
                    public void onNext(List<JobUnit> jobUnits) {
                        JasperResourceConverter jasperResourceConverter = new JasperResourceConverter(getActivity());

                        mAdapter.addAll(jasperResourceConverter.convertToJasperResources(jobUnits));

                        for (JobUnit job : jobUnits) {
                            mJobs.put(job.getId(), job);
                        }
                    }
                });
        mCompositeSubscription.add(subscription);
    }

    private Observable<RxReportScheduleService> initRestClient() {
        Account account = JasperAccountManager.get(getActivity()).getActiveAccount();
        return mPasswordManager.get(account).map(new Func1<String, RxReportScheduleService>() {
            @Override
            public RxReportScheduleService call(String password) {
                JsServerProfile serverProfile = jsRestClient.getServerProfile();
                Server server = Server.builder()
                        .withBaseUrl(serverProfile.getServerUrl() + "/")
                        .build();
                SpringCredentials credentials = SpringCredentials.builder()
                        .withOrganization(serverProfile.getOrganization())
                        .withUsername(serverProfile.getUsername())
                        .withPassword(password)
                        .build();

                AuthorizedClient client = server.newClient(credentials)
                        .withCookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER))
                        .create();
                return RxReportScheduleService.newService(client);
            }
        });
    }

    private void setDataAdapter() {
        mAdapter = new JasperResourceAdapter(getActivity(), null, ViewType.LIST);
        mAdapter.setOnItemInteractionListener(new JasperResourceAdapter.OnResourceInteractionListener() {
            @Override
            public void onResourceItemClicked(String id) {
                try {
                    int jobId = Integer.parseInt(id);
                    JobUnit job = mJobs.get(jobId);

                    SimpleDialogFragment.createBuilder(getActivity(), getFragmentManager())
                            .setTitle(job.getLabel())
                            .setMessage(createJobInfo(job))
                            .setPositiveButtonText(R.string.ok)
                            .show();
                } catch (NumberFormatException ex) {
                    Toast.makeText(getActivity(), R.string.wrong_action, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSecondaryActionClicked(final JasperResource jasperResource) {
                if (mScheduleService != null) {
                    try {
                        final int jobId = Integer.parseInt(jasperResource.getId());
                        Set<Integer> idToDel = new HashSet<>();
                        idToDel.add(jobId);

                        ProgressDialogFragment.builder(getFragmentManager())
                                .setLoadingMessage(R.string.loading_msg)
                                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        mCompositeSubscription.unsubscribe();
                                        mCompositeSubscription = new CompositeSubscription();
                                    }
                                })
                                .show();

                        mCompositeSubscription.add(mScheduleService.deleteJobs(idToDel)
                                .compose(RxTransformers.<Set<Integer>>applySchedulers())
                                .subscribe(new Subscriber<Set<Integer>>() {
                                    @Override
                                    public void onCompleted() {
                                        ProgressDialogFragment.dismiss(getFragmentManager());
                                        Toast.makeText(getActivity(), R.string.sch_deleted, Toast.LENGTH_SHORT).show();

                                        mJobs.remove(jobId);
                                        mAdapter.remove(jasperResource);

                                        if (mJobs.isEmpty()) {
                                            showMessage(getString(R.string.sch_not_found));
                                        }
                                        analytics.sendEvent(Analytics.EventCategory.RESOURCE.getValue(), Analytics.EventAction.REMOVED.getValue(), Analytics.EventLabel.JOB.getValue());
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        // TODO: Handle error
                                        ProgressDialogFragment.dismiss(getFragmentManager());
                                    }

                                    @Override
                                    public void onNext(Set<Integer> integers) {

                                    }
                                }));
                    } catch (NumberFormatException ex) {
                        Toast.makeText(getActivity(), R.string.wrong_action, Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        listView.setViewType(ViewType.LIST);
        listView.setAdapter(mAdapter);
    }

    private void showMessage(String textMessage) {
        message.setText(textMessage);
        if (textMessage != null) {
            mAdapter.clear();
        }
    }

    private void setRefreshState(boolean refreshing) {
        if (!refreshing) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private String createJobInfo(JobUnit jobUnit) {
        String prevDate = jobUnit.getPreviousFireTime() != null ? "" + jobUnit.getPreviousFireTime() : "-";
        String nextDate = jobUnit.getNextFireTime() != null ? "" + jobUnit.getNextFireTime() : "-";
        return getString(R.string.sch_info, jobUnit.getDescription(), jobUnit.getReportUri(), prevDate,
                nextDate, jobUnit.getOwner().getUsername(), jobUnit.getOwner().getOrganization());
    }
}
