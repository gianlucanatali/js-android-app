package com.jaspersoft.android.jaspermobile.activities.report.chartTypes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.ui.view.activity.ToolbarActivity;
import com.jaspersoft.android.sdk.widget.report.renderer.ChartType;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Olexandr Dahno
 * @since 2.6
 */
public class ChartTypesActivity extends ToolbarActivity implements ChartTypesAdapter.ChartTypeSelectListener {
    public static final String CHART_TYPES_ARG = "chartTypes";
    public static final String SELECTED_CHART_TYPE_ARG = "selectedChartType";

    private ChartType mSelectedChartType;

    @BindView(R.id.chartTypesToolbar)
    Toolbar chartTypesToolbar;
    @BindView(R.id.chartTypesContainer)
    FrameLayout chartTypesContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_types);
        ButterKnife.bind(this);
        initToolbar(getString(R.string.rv_ab_chart_types));

        if (savedInstanceState == null) {
            List<ChartType> chartTypes = fetchChartTypes();
            showChartTypes(getString(R.string.s_fd_option_all), chartTypes, true);
        }
    }

    /*
     * PRIVATE API
     */

    private void initToolbar(String title) {
        chartTypesToolbar.setTitle(title);
        chartTypesToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @NotNull
    private List<ChartType> fetchChartTypes() {
        Bundle extras = getIntent().getExtras();
        List<ChartType> chartTypes = null;
        if (extras != null) {
            chartTypes = extras.getParcelableArrayList(CHART_TYPES_ARG);
            mSelectedChartType = extras.getParcelable(SELECTED_CHART_TYPE_ARG);
        }
        if (chartTypes == null) {
            throw new RuntimeException("ChartTypes should be provided");
        }
        return chartTypes;
    }

    private void showChartTypes(String name, List<ChartType> chartTypes, boolean isRoot) {
        Fragment chartTypeFragment = ChartTypeFragment.create(chartTypes, mSelectedChartType);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (!isRoot) {
            fragmentTransaction.addToBackStack(null)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        }
        fragmentTransaction.replace(R.id.chartTypesContainer, chartTypeFragment)
                .commit();
    }

    /*
     *  ChartTypesAdapter.ChartTypeSelectListener Impl
     */

    @Override
    public void onChartTypeSelected(ChartType chartType) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(SELECTED_CHART_TYPE_ARG, chartType);

        Intent returnIntent = new Intent();
        returnIntent.putExtras(bundle);

        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
