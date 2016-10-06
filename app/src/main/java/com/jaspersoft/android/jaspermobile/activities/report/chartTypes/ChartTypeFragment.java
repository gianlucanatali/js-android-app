package com.jaspersoft.android.jaspermobile.activities.report.chartTypes;

import org.jetbrains.annotations.NotNull;
import android.support.annotation.Nullable;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.ui.view.fragment.BaseFragment;
import com.jaspersoft.android.sdk.widget.report.renderer.ChartType;


import java.util.ArrayList;
import java.util.List;

/**
 * @author Olexandr Dahno
 * @since 2.6
 */
public class ChartTypeFragment extends BaseFragment {

    private ChartTypesAdapter chartTypesAdapter;
    private ChartTypesAdapter.ChartTypeSelectListener listener;

    private ChartType mSelectedChartType;

    public static ChartTypeFragment create(List<ChartType> chartTypes, ChartType selectedChartType) {
        ChartTypeFragment chartTypeFragment = new ChartTypeFragment();

        Bundle bundle = new Bundle();
        ArrayList<ChartType> chartTypesArrayList = new ArrayList<>(chartTypes);
        bundle.putParcelableArrayList(ChartTypesActivity.CHART_TYPES_ARG, chartTypesArrayList);
        bundle.putParcelable(ChartTypesActivity.SELECTED_CHART_TYPE_ARG, selectedChartType);
        chartTypeFragment.setArguments(bundle);

        return chartTypeFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof ChartTypesAdapter.ChartTypeSelectListener) {
            listener = (ChartTypesAdapter.ChartTypeSelectListener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement ChartTypesAdapter.ChartTypeSelectListener.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        listener = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<ChartType> chartTypes = fetchChartTypes();
        chartTypesAdapter = new ChartTypesAdapter(getContext(), listener, chartTypes, mSelectedChartType);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chart_types, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ListView chartTypes = (ListView) view.findViewById(R.id.chartTypesListView);
        chartTypes.setAdapter(chartTypesAdapter);
    }

    /*
     * Private API
     */

    @NotNull
    private List<ChartType> fetchChartTypes() {
        Bundle extras = getArguments();
        List<ChartType> chartTypes = null;
        if (extras != null) {
            chartTypes = extras.getParcelableArrayList(ChartTypesActivity.CHART_TYPES_ARG);
            mSelectedChartType = extras.getParcelable(ChartTypesActivity.SELECTED_CHART_TYPE_ARG);
        }
        if (chartTypes == null) {
            throw new RuntimeException("ChartTypes should be provided");
        }
        return chartTypes;
    }

}
