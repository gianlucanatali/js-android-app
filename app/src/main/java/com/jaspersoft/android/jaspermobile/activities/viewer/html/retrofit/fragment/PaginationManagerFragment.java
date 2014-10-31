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

package com.jaspersoft.android.jaspermobile.activities.viewer.html.retrofit.fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.robospice.RoboSpiceFragment;
import com.jaspersoft.android.jaspermobile.dialog.AlertDialogFragment;
import com.jaspersoft.android.jaspermobile.dialog.NumberDialogFragment;
import com.jaspersoft.android.jaspermobile.network.CommonRequestListener;
import com.jaspersoft.android.sdk.client.JsRestClient;
import com.jaspersoft.android.sdk.client.async.request.ReportDetailsRequest;
import com.jaspersoft.android.sdk.client.oxm.report.ReportExecutionResponse;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;

import java.util.Map;

/**
 * @author Tom Koptel
 * @since 1.9
 */
@EFragment(R.layout.pagination_bar_layout)
public class PaginationManagerFragment extends RoboSpiceFragment {

    public static final String TAG = PaginationManagerFragment.class.getSimpleName();
    private static final int FIRST_PAGE = 1;

    @Inject
    JsRestClient jsRestClient;

    @ViewById
    TextView firstPage;
    @ViewById
    TextView previousPage;
    @ViewById
    TextView nextPage;
    @ViewById
    TextView lastPage;
    @ViewById
    View rootContainer;

    @ViewById
    TextView currentPageLabel;
    @ViewById
    TextView totalPageLabel;

    @InstanceState
    int mTotalPage;
    @InstanceState
    String requestId;

    @InstanceState
    int currentPage = FIRST_PAGE;

    private final Map<Integer, NodeWebViewFragment> pagesMap = Maps.newHashMap();
    private PagesAdapter mAdapter;

    @AfterViews
    final void init() {
        mAdapter = new PagesAdapter(getFragmentManager());

        totalPageLabel.setVisibility(View.INVISIBLE);
        lastPage.setVisibility(View.INVISIBLE);

        currentPageLabel.setText(String.valueOf(currentPage));
        alterControlStates();
    }

    public void showTotalPageCount(int totalPage) {
        mTotalPage = totalPage;

        totalPageLabel.setVisibility(View.VISIBLE);
        lastPage.setVisibility(View.VISIBLE);

        totalPageLabel.setText(getString(R.string.of, totalPage));
    }

    @Click
    final void firstPage() {
        currentPage = FIRST_PAGE;
        paginateToCurrentSelection();
    }

    @Click
    final void previousPage() {
        if (currentPage != FIRST_PAGE) {
            currentPage -= 1;
        }
        paginateToCurrentSelection();
    }

    @Click
    final void nextPage() {
        if (currentPage != mTotalPage) {
            currentPage += 1;
        }
        paginateToCurrentSelection();
    }

    @Click
    final void lastPage() {
        currentPage = mTotalPage;
        paginateToCurrentSelection();
    }

    @Click(R.id.currentPageLabel)
    final void selectCurrentPage() {
        if (mTotalPage != 0) {
            NumberDialogFragment.show(getFragmentManager(), currentPage, mTotalPage,
                    new NumberDialogFragment.OnPageSelectedListener() {
                        @Override
                        public void onPageSelected(int page) {
                            currentPage = page;
                            alterControlStates();
                        }
                    });
        }
    }

    public void setVisible(boolean visible) {
        TranslateAnimation animate = new TranslateAnimation(0, 0, 0,
                (visible ? 0 : 1) * rootContainer.getHeight());
        animate.setDuration(500);
        animate.setFillAfter(true);
        rootContainer.startAnimation(animate);
        rootContainer.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void paginateToCurrentSelection() {
        alterControlStates();

        NodeWebViewFragment nodeWebViewFragment;
        if (mAdapter.getCount() == 0) {
            nodeWebViewFragment = createNodeWebViewFragment();
        } else {
            if (pagesMap.containsKey(currentPage)) {
                nodeWebViewFragment = pagesMap.get(currentPage);
            } else {
                nodeWebViewFragment = createNodeWebViewFragment();
            }
        }

        getFragmentManager().beginTransaction()
                .replace(R.id.content, nodeWebViewFragment,
                        NodeWebViewFragment.TAG + currentPage).commit();
    }

    private NodeWebViewFragment createNodeWebViewFragment() {
        NodeWebViewFragment nodeWebViewFragment =
                NodeWebViewFragment_.builder().requestId(requestId).page(currentPage).build();
        pagesMap.put(currentPage, nodeWebViewFragment);
        return nodeWebViewFragment;
    }

    private void alterControlStates() {
        currentPageLabel.setText(String.valueOf(currentPage));

        if (currentPage == mTotalPage) {
            previousPage.setEnabled(true);
            firstPage.setEnabled(true);
            nextPage.setEnabled(false);
            lastPage.setEnabled(false);
            return;
        }
        if (currentPage == FIRST_PAGE) {
            previousPage.setEnabled(false);
            firstPage.setEnabled(false);
            nextPage.setEnabled(true);
            lastPage.setEnabled(true);
            return;
        }
        previousPage.setEnabled(true);
        firstPage.setEnabled(true);
        nextPage.setEnabled(true);
        lastPage.setEnabled(true);
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public void update() {
        boolean paginationLoaded = (mTotalPage != 0);
        if (!paginationLoaded) {
            ReportDetailsRequest reportDetailsRequest = new ReportDetailsRequest(jsRestClient, requestId);
            ReportDetailsRequestListener requestListener = new ReportDetailsRequestListener();
            getSpiceManager().execute(reportDetailsRequest, requestListener);
        }

        NodeWebViewFragment nodeWebViewFragment = getCurrentNodeWebViewFragment();
        if (nodeWebViewFragment.isResourceLoaded()) {
            // check Output-Final
        }
    }

    public boolean isResourceLoaded() {
        NodeWebViewFragment currentWebView = getCurrentNodeWebViewFragment();
        if (currentWebView == null) return false;
        return currentWebView.isResourceLoaded();
    }

    //---------------------------------------------------------------------
    // Helper methods
    //---------------------------------------------------------------------

    private NodeWebViewFragment getCurrentNodeWebViewFragment() {
        return (NodeWebViewFragment)
                getFragmentManager().findFragmentByTag(NodeWebViewFragment.TAG + currentPage);
    }

    //---------------------------------------------------------------------
    // Inner classes
    //---------------------------------------------------------------------

    private class PagesAdapter extends FragmentPagerAdapter {
        public PagesAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return pagesMap.keySet().size();
        }

        @Override
        public Fragment getItem(int position) {
            return Lists.newLinkedList(pagesMap.values()).get(position);
        }
    }

    private class ReportDetailsRequestListener extends CommonRequestListener<ReportExecutionResponse> {
        @Override
        public final void onSemanticSuccess(ReportExecutionResponse response) {
            int totalPageCount = response.getTotalPages();
            boolean needToShow = (totalPageCount > 1);
            setVisible(needToShow);

            if (needToShow) {
                showTotalPageCount(response.getTotalPages());
            }

            if (totalPageCount == 0) {
                AlertDialogFragment.createBuilder(getActivity(), getFragmentManager())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.warning_msg)
                        .setMessage(R.string.rv_error_empty_report).show();
            }
        }

        @Override
        public Activity getCurrentActivity() {
            return getActivity();
        }
    }
}
