package greenely.greenely.feed.utils.charting

import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import greenely.greenely.feed.models.ChartData
import org.joda.time.DateTime

abstract class MultiChartManager(
        protected val chart: CombinedChart) {

    private val _labels = mutableListOf<DateTime>()
    protected var labels: List<DateTime>
        get() = _labels
        set(value) {
            _labels.clear()
            _labels.addAll(value)
        }

    init {
        chart.setScaleEnabled(false)
        chart.setTouchEnabled(false)
        chart.legend.isEnabled = false
        chart.renderer = CustomCombinedChartRenderer(chart, chart.animator, chart.viewPortHandler)

        chart.axisLeft.setYAxisStyle(chart.context,getYAxisLabelCount(),provideYAxisFormatter())
        chart.xAxis.setXAxisStyle(chart.context,getXAxisLabelCount(),provideXAxisFormatter())

        chart.axisRight.isEnabled = false

        chart.getDescription().setEnabled(false)

        setAxisLeft(chart.axisLeft)
        setXAxis(chart.xAxis)

    }


    var chartData: ChartData?
        get() = chartData
        set(value) {
            value?.let {
                labels = it.data?.map { it.first to it.second.toFloat() / 1000.0f }.map { it.first }
            }

            chart.drawOrder = listOf(CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE).toTypedArray()
            chart.data = createChartData(value)



            chart.notifyDataSetChanged()

            chart.animateY(1000)
        }

    abstract fun createChartData(chartData: ChartData?): CombinedData

    fun setBarWidth(barData: BarData) {
        barData.barWidth = barData.barWidth - (barData.barWidth / 3)
        chart.xAxis.setSpaceMin(barData.getBarWidth() / 2f);
        chart.xAxis.setSpaceMax(barData.getBarWidth() / 2f);

    }


    abstract fun setAxisLeft(axisLeft: YAxis)

    abstract fun setXAxis(xAxis: XAxis)

    abstract fun provideXAxisFormatter(): IAxisValueFormatter

    abstract fun provideYAxisFormatter(): IAxisValueFormatter


    abstract fun getXAxisLabelCount(): Int


    abstract fun getYAxisLabelCount(): Int


}
