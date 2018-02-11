package com.jijith.samplebarchart

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import butterknife.BindView
import com.jijith.barchart.adapter.SimpleBarChartAdapter
import com.jijith.barchart.model.ChartData
import com.jijith.barchart.view.BarChartRecyclerView
import java.util.*


class MainActivity : AppCompatActivity() {

    internal var simpleRecyclerViewBarChartAdapter: SimpleBarChartAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val barChartRecyclerView = findViewById(R.id.bar_chart_recycle_view) as BarChartRecyclerView
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        barChartRecyclerView.setLayoutManager(linearLayoutManager)
        barChartRecyclerView.setHasFixedSize(false)

        val chartData = ArrayList<ChartData>()

        var progress: Int;
        for (i in 1..10) {
            if ((i % 2) == 0) {
                progress = 80
                chartData.add(ChartData(Color.GREEN, progress,
                        Integer.toString(i), Integer.toString(progress), "chart"))
            } else if ((i % 3) == 0) {
                progress = 50
                chartData.add(ChartData(Color.RED, progress,
                        Integer.toString(i), Integer.toString(progress), "chart"))
            } else {
                progress = 20;
                chartData.add(ChartData(Color.RED, progress,
                        Integer.toString(i), Integer.toString(progress), "chart"))
            }
        }

        simpleRecyclerViewBarChartAdapter = SimpleBarChartAdapter(chartData)

        barChartRecyclerView.setAdapter(simpleRecyclerViewBarChartAdapter)

    }
}
