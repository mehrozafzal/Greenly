package greenely.greenely.feed.utils.charting

import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import greenely.greenely.R
import greenely.greenely.feed.models.ChartData
import greenely.greenely.feed.models.CostAnalysisChartData
import greenely.greenely.utils.consumptionToString
import org.joda.time.format.DateTimeFormatter

class CostAnalysisChartManager(chart: CombinedChart,
                               private val dateFormatter: DateTimeFormatter) : MultiChartManager(chart) {


    override fun createChartData(chartData: ChartData?): CombinedData {

        val combinedData = CombinedData()
        val lineData = LineData()


        chartData?.let {
            if (it is CostAnalysisChartData){
                val usageEntries = it.data?.map { it.first to it.second.toFloat() / 1000.0f }.map { it.second }
                val greenelyPriceEntries = it.greenelyPriceData?.map { it.first to it.second / 100.0f }.map { it.second }
                val marketPriceEntries = it.marketPriceData?.map { it.first to it.second / 100.0f }.map { it.second }


                var max=0f

                usageEntries?.let {
                    val entries = it.mapIndexed { index, value -> BarEntry(index.toFloat(), value) }
                    val set = BarDataSet(entries, chart.context.getString(R.string.consumption)).apply {
                        setDrawValues(false)
                        color = ContextCompat.getColor(chart.context, R.color.grey11)
                        axisDependency=YAxis.AxisDependency.RIGHT

                    }
                    usageEntries.max()?.let {
                        chart.axisLeft.axisMaximum = usageEntries.count()+1f
                        var rightAxis = chart.axisRight
                        rightAxis.axisMaximum = (it).times(1.2f)
                        rightAxis.axisMinimum = 0f
                    }



                    var barData = BarData(set)
                    combinedData.setData(barData)
                    setBarWidth(barData)
                }


                greenelyPriceEntries?.let {
                    val entries = it.mapIndexed { index, value -> Entry(index.toFloat(), value) }
                    val set = LineDataSet(entries, chart.context.getString(R.string.greenely_price_label_chart)).apply {
                        setDrawValues(false)
                        setDrawCircles(false)
                        mode = LineDataSet.Mode.CUBIC_BEZIER
                        lineWidth = 2f
                        axisDependency=YAxis.AxisDependency.LEFT
                        color = ContextCompat.getColor(chart.context, R.color.green_1)

                        it.max()?.let {
                            max = if(it>max)it else max
                        }

                    }
                    lineData.addDataSet(set)
                }

                marketPriceEntries?.let {
                    val entries = it.mapIndexed { index, value -> Entry(index.toFloat(), value) }
                    val set = LineDataSet(entries, chart.context.getString(R.string.market_price_label_chart)).apply {
                        setDrawValues(false)
                        setDrawCircles(false)
                        mode = LineDataSet.Mode.CUBIC_BEZIER
                        lineWidth = 2f
                        axisDependency=YAxis.AxisDependency.LEFT
                        color = ContextCompat.getColor(chart.context, R.color.blue1)

                        it.max()?.let {
                            max = if(it>max)it else max
                        }

                    }
                    lineData.addDataSet(set)
                }


                combinedData.setData(lineData)
                chart.axisLeft.axisMaximum=max.times(1.2f)

            }
        }

        return combinedData
    }

    override fun setAxisLeft(axisLeft: YAxis) {
        axisLeft.let {
            it.setDrawGridLines(true)
            it.gridColor = ContextCompat.getColor(chart.context, R.color.grey9)
            it.setDrawZeroLine(false)
            it.setDrawAxisLine(false)
            it.setDrawGridLines(false)
            it.gridLineWidth = 0.5f
            it.axisMinimum=-1f
            chart.rendererLeftYAxis = CustomYAxisRenderer(chart.viewPortHandler, it, chart.getTransformer(chart.axisLeft.axisDependency))
        }

//        chart.axisRight.let {
//            it.isEnabled=true
//            it.setDrawGridLines(true)
//        }
    }

    override fun setXAxis(xAxis: XAxis) {
        xAxis.setDrawAxisLine(true)
        xAxis.axisLineColor = ContextCompat.getColor(chart.context, android.R.color.white)
    }

    override fun provideXAxisFormatter(): IAxisValueFormatter {
        return IAxisValueFormatter { value, _ ->
            val label = labels.getOrNull(value.toInt())

            if (label != null) {
                dateFormatter.print(label).capitalize()
            } else {
                ""
            }
        }
    }

    override fun provideYAxisFormatter(): IAxisValueFormatter {
        return IAxisValueFormatter { value, _ ->
            if(value<0f) "0,0"
            else
            value.consumptionToString()
        }
    }

    override fun getXAxisLabelCount() = 4

    override fun getYAxisLabelCount() = 3
}