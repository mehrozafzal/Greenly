package greenely.greenely.feed.utils.charting

import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import greenely.greenely.R
import greenely.greenely.feed.models.ChartData
import greenely.greenely.feed.models.MonthlyChartData
import greenely.greenely.utils.consumptionToString
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormatter

class MonthlyReportChartManager(chart: CombinedChart,
                                private val dateFormatter: DateTimeFormatter) : MultiChartManager(chart) {
    val priceDisplayFactory=1000f
    val energyDisplayFactor=1000f
    val usageDisplayFactor=1000f

    override fun setAxisLeft(axisLeft: YAxis) {

        axisLeft.let {
            it.setDrawGridLines(true)
            it.gridColor=ContextCompat.getColor(chart.context,R.color.grey9)
            it.setDrawZeroLine(false)
            it.setDrawAxisLine(false)
            it.gridLineWidth=0.5f
            chart.rendererLeftYAxis = CustomYAxisRenderer(chart.viewPortHandler, it, chart.getTransformer(chart.axisLeft.axisDependency))

        }
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
            value.consumptionToString()
        }

    }

    override fun getXAxisLabelCount() = 4

    override fun getYAxisLabelCount() = 3


    override fun createChartData(chartData: ChartData?): CombinedData {
        val combinedData = CombinedData()


        when (chartData) {
            is MonthlyChartData -> {

                val lineData = LineData()

                val usageEntries = chartData.data?.map { it.first to it.second.toFloat() / usageDisplayFactor }.map { it.second }

                  usageEntries?.let {
                    val entries = it.mapIndexed { index, value -> BarEntry(index.toFloat(), value) }
                    val set = BarDataSet(entries, chart.context.getString(R.string.consumption)).apply {
                        setDrawValues(false)
                        color = ContextCompat.getColor(chart.context, R.color.green_1)
                    }
                    usageEntries.max()?.let {
                        chart.axisLeft.axisMaximum = it.times(2.5f)
                    }
                    var barData = BarData(set)
                    combinedData.setData(barData)
                    setBarWidth(barData)
                }

                if(chartData.hasValidTemperatureData){
                    val temperatureEntries = chartData.temperatureData?.map { point(it,energyDisplayFactor)}
                    temperatureEntries?.let {

                        val entries = it.mapIndexedNotNull{ index, value -> value?.let {Entry(index.toFloat(), value)  } }
                        val set = LineDataSet(entries, chart.context.getString(R.string.temperature)).apply {
                            setDrawValues(false)
                            setDrawCircles(false)
                            mode = LineDataSet.Mode.CUBIC_BEZIER
                            lineWidth = 2f
                            color = ContextCompat.getColor(chart.context, R.color.blue1)

                        }
                        lineData.addDataSet(set)
                    }
                }

                if(chartData.hasValidSpotPriceData){
                    val spotPriceEntries = chartData.spotPriceData?.map { point(it,priceDisplayFactory)}
                    spotPriceEntries?.let {
                        val entries = it.mapIndexedNotNull{ index, value -> value?.let {Entry(index.toFloat(), value)  } }
                        val set = LineDataSet(entries, chart.context.getString(R.string.price_lable)).apply {
                            setDrawValues(false)
                            setDrawCircles(false)
                            mode = LineDataSet.Mode.CUBIC_BEZIER
                            lineWidth = 2f
                            color = ContextCompat.getColor(chart.context, R.color.yellow2)

                        }
                        lineData.addDataSet(set)
                    }
                }

                combinedData.setData(lineData)

            }
        }






        return combinedData
    }

    fun point( point:Pair<DateTime, Float?>,displayFactory:Float):Float?{

        point.second?.let {
          return  (point.first to point.second!!.toFloat() / displayFactory).second
        }

        return null
    }
}