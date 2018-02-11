package com.jijith.barchart.model;

/**
 * Created by jijith on 2/11/18.
 *
 * This Model class is used to store the bar chart data
 */

public class ChartData {

    private int barColor; // To set Bar Color
    private int progress; // To set the progress of bar
    private String xValueText; // To set the X value blow the bar chart
    private String yValueText; // To set the Y value above the bar chart
    private String barValueText; //To set text in the bottom of the bar chart

    public ChartData() {
    }

    public ChartData(int barColor, int progress, String xValueText, String yValueText, String barValueText) {
        this.barColor = barColor;
        this.progress = progress;
        this.xValueText = xValueText;
        this.yValueText = yValueText;
        this.barValueText = barValueText;
    }

    public int getBarColor() {
        return barColor;
    }

    public void setBarColor(int barColor) {
        this.barColor = barColor;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getxValueText() {
        return xValueText;
    }

    public void setxValueText(String xValueText) {
        this.xValueText = xValueText;
    }

    public String getyValueText() {
        return yValueText;
    }

    public void setyValueText(String yValueText) {
        this.yValueText = yValueText;
    }

    public String getBarValueText() {
        return barValueText;
    }

    public void setBarValueText(String barValueText) {
        this.barValueText = barValueText;
    }
}
