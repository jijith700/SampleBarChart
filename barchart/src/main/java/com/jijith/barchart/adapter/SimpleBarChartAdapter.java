package com.jijith.barchart.adapter;

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
            getAdapterAnimations(viewToAnimate, AdapterAnimationType.SlideInBottom);

//            viewToAnimate.clearAnimation();
//            TranslateAnimation anim = new TranslateAnimation(0,0,90,0);
////            ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
////            anim.setDuration(new Random().nextInt(501));//to make duration random number between [0,501)
//            anim.setDuration(2000);//to make duration random number between [0,501)
//            anim.setFillAfter(true);
//            viewToAnimate.startAnimation(anim);
            mLastPosition = position;

//            expand(viewToAnimate, 2000, 200);
        } else if (position < mLastPosition && mLastPosition != -1) {
//            viewToAnimate.clearAnimation();
//            TranslateAnimation anim = new TranslateAnimation(0,0,0,90);
//            anim.setFillAfter(true);
//            anim.setDuration(2000);
//            viewToAnimate.startAnimation(anim);
            mLastPosition = position;

//            collapse(viewToAnimate, 2000, 100);


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

    public static void expand(final View v, int duration, int targetHeight) {

        int prevHeight = v.getHeight();

        v.setVisibility(View.VISIBLE);
        ValueAnimator valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.getLayoutParams().height = (int) animation.getAnimatedValue();
                v.requestLayout();
            }
        });
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }

    public static void collapse(final View v, int duration, int targetHeight) {
        int prevHeight = v.getHeight();
        ValueAnimator valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.getLayoutParams().height = (int) animation.getAnimatedValue();
                v.requestLayout();
            }
        });
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }
}