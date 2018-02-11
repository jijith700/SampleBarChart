package com.jijith.barchart.adapter;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.jijith.barchart.R;
import com.jijith.barchart.model.ChartData;
import com.jijith.barchart.view.BarChart;
import com.jijith.barchart.view.BarChartViewHolder;

import java.util.List;

/*
*
* * This is the Adapter class for the bar chart *
*
* */
public class SimpleBarChartAdapter extends BarChartViewAdapter<SimpleBarChartAdapter.ViewHolder> {

    private List<ChartData> dataList;

    public SimpleBarChartAdapter(List<ChartData> dataList) {
        this.dataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_bar_chart, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public int getAdapterItemCount() {
        return dataList.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.barChart.setBarColor(dataList.get(position).getBarColor());
        holder.barChart.setProgress(dataList.get(position).getProgress());
        holder.barChart.setBarText(dataList.get(position).getBarValueText());
        holder.barChart.setYValueText(dataList.get(position).getyValueText());
        holder.xValueText.setText(dataList.get(position).getxValueText());

        setAnimation(holder.barChart, position);

    }

    private int mLastPosition = -1;

    private void setAnimation(View viewToAnimate, int position) {
        if (position > mLastPosition) {
            Animator[] anim = getAdapterAnimations(viewToAnimate, AdapterAnimationType.SlideInBottom);
            if (anim.length > 0) {
                anim[0].setDuration(2000);
                anim[0].start();
            }

            mLastPosition = position;

        } else if (position < mLastPosition && mLastPosition != -1) {
            Animator[] anim = getAdapterAnimations(viewToAnimate, AdapterAnimationType.SlideInBottom);
            if (anim.length > 0) {
                anim[0].setDuration(2000);
                anim[0].start();
            }

            mLastPosition = position;
        }
    }

    class ViewHolder extends BarChartViewHolder {

        BarChart barChart;
        TextView xValueText;

        private ViewHolder(View itemView) {
            super(itemView);

            barChart = (BarChart) itemView.findViewById(R.id.bar_chart);
            xValueText = (TextView) itemView.findViewById(R.id.x_value);

        }
    }
}