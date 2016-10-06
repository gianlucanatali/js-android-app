package com.jaspersoft.android.jaspermobile.activities.report.chartTypes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.sdk.widget.report.renderer.ChartType;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Olexandr Dahno
 * @since 2.6
 */
public class ChartTypesAdapter extends BaseAdapter implements View.OnClickListener {

    private final ChartTypeSelectListener chartTypeSelectListener;
    private final LayoutInflater layoutInflater;
    private final List<ChartType> mChartTypes;
    private ChartType mSelectedChartType;

    public ChartTypesAdapter(Context context, ChartTypeSelectListener chartTypeSelectListener, List<ChartType> chartTypes, ChartType selectedChartType) {
        if (context == null) {
            throw new IllegalArgumentException("Context should be provided");
        }
        if (chartTypeSelectListener == null) {
            throw new IllegalArgumentException("ChartTypeSelectListener should be provided");
        }
        if (chartTypes == null) {
            throw new IllegalArgumentException("ChartTypes list should be provided");
        }

        this.chartTypeSelectListener = chartTypeSelectListener;
        this.layoutInflater = LayoutInflater.from(context);
        mChartTypes = chartTypes;
        mSelectedChartType = selectedChartType;
    }

    /*
     *   Adapter Impl
     */

    @Override
    public int getCount() {
        return mChartTypes.size();
    }

    @Override
    public ChartType getItem(int i) {
        return mChartTypes.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = layoutInflater.inflate(R.layout.item_chart_type, viewGroup, false);
            holder = new ViewHolder(view);

            view.setTag(holder);
            view.setOnClickListener(this);
        }

        ChartType chartType = getItem(i);

        // TODO: discuss colors
        if (chartType.equals(mSelectedChartType)) {
            view.setBackgroundColor(view.getContext().getResources().getColor(R.color.js_gray_with_opacity));
        } else {
            view.setBackgroundColor(view.getContext().getResources().getColor(R.color.default_text));
        }

        holder.chartTypeName.setText(chartType.getName());
        holder.position = i;

        return view;
    }

    /*
     *   View.OnClickListener
     */

    @Override
    public void onClick(View view) {
        int position = ((ViewHolder) view.getTag()).position;
        ChartType chartType = getItem(position);
        chartTypeSelectListener.onChartTypeSelected(chartType);
    }

    /*
     *  Declaring of ChartTypeSelectListener
     */

    public interface ChartTypeSelectListener {
        void onChartTypeSelected(com.jaspersoft.android.sdk.widget.report.renderer.ChartType chartType);
    }

    /*
     *  Private Section
     */

    static class ViewHolder {
        int position;

        @BindView(R.id.chartTypeName)
        TextView chartTypeName;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
