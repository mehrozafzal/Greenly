package greenely.greenely.feed.utils.charting

import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import greenely.greenely.R
import greenely.greenely.feed.models.ChartData
import greenely.greenely.retail.ui.charts.consumptiondatachart.CustomBarChartRenderer
import greenely.greenely.utils.consumptionToString
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormatter

//This class provides seperate Chart Implementation
// as Combined bar chart does not support multiple BarSet as per Mpchart 3.0.3
class WeeklyReportChartManager(val chart: BarChart,
                               private val previousWeekChartData: ChartData?,
                               private val dateFormatter: DateTimeFormatter) {


    private val _labels = mutableListOf<DateTime>()
    protected var labels: List<DateTime>
        get() = _labels
        set(value) {
            _labels.clear()
            _labels.addAll(value)
        }

    val groupSpace = 0.4f
    val barSpace = 0.1f // x2 DataSet
    val barWid = 0.2f

    val offsetForXAxis=-0.5f
    val xAxisMinimum=-1f


    init {
        chart.setScaleEnabled(false)
        chart.setTouchEnabled(false)
        chart.legend.isEnabled = false
        chart.renderer = CustomBarChartRenderer(chart, chart.animator, chart.viewPortHandler)
        chart.axisLeft.setYAxisStyle(chart.context, getYAxisLabelCount(), provideYAxisFormatter())
        chart.xAxis.setXAxisStyle(chart.context, getXAxisLabelCount(), provideXAxisFormatter())
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

            chart.data = createChartData(value)
            chart.xAxis.axisMaximum = labels.count().toFloat()


            previousWeekChartData?.let {
                chart.barData.barWidth = barWid
                chart.groupBars(offsetForXAxis, groupSpace, barSpace)
            }
            chart.notifyDataSetChanged()
        }


    fun setAxisLeft(axisLeft: YAxis) {
        axisLeft.let {
            it.setDrawGridLines(true)
            it.gridColor = ContextCompat.getColor(chart.context, R.color.grey9)
            it.setDrawZeroLine(false)
            it.setDrawAxisLine(false)
            it.gridLineWidth = 0.5f
        }
    }

    fun setXAxis(xAxis: XAxis) {
        xAxis.setDrawAxisLine(true)
        xAxis.setLabelCount(9, true)  // 7 days in week plus 2 empty label to make the graph look right
        xAxis.axisMinimum = xAxisMinimum
        xAxis.axisLineColor = ContextCompat.getColor(chart.context, android.R.color.white)
    }

    fun provideXAxisFormatter(): IAxisValueFormatter {
        return IAxisValueFormatter { value, _ ->
            if (value.toFloat() in 0f..7f) {
                val label = labels.getOrNull(value.toInt())
                if (label != null) {
                    return@IAxisValueFormatter dateFormatter.print(label).toUpperCase().take(3)
                } else {
                    ""
                }

            }
            ""

        }
    }

    fun provideYAxisFormatter(): IAxisValueFormatter {
        return IAxisValueFormatter { value, _ ->

            value.consumptionToString()
        }

    }

    fun getXAxisLabelCount() = labels.count()

    fun getYAxisLabelCount() = 3


    fun createChartData(currentPeriodChartData: ChartData?): BarData {

        var max = 0f
        var set1: BarDataSet? = null
        var set2: BarDataSet? = null


        currentPeriodChartData?.let {
            val usageEntries = it.data?.map { it.first to it.second.toFloat() / 1000.0f }.map { it.second }

            usageEntries?.let {
                val entries = it.mapIndexed { index, value -> BarEntry(index.toFloat(), value) }
                set1 = BarDataSet(entries, chart.context.getString(R.string.consumption)).apply {
                    setDrawValues(false)
                    color = ContextCompat.getColor(chart.context, R.color.green_1)
                }
                usageEntries.max()?.let {
                    max = it
                }
            }


            previousWeekChartData?.let {
                val usageEntries = it.data?.map { it.first to it.second.toFloat() / 1000.0f }.map { it.second }

                usageEntries?.let {
                    val entries = it.mapIndexed { index, value -> BarEntry(index.toFloat(), value) }
                    set2 = BarDataSet(entries, chart.context.getString(R.string.consumption)).apply {
                        setDrawValues(false)
                        color = ContextCompat.getColor(chart.context, R.color.yellow3)
                    }
                    usageEntries.max()?.let {
                        max = if (it > max) it else max
                    }
                }
            }


            chart.axisLeft.mAxisMaximum = max

        }

        //This display two Bar in combined bar chart
        //Note Using List in BarChart will display stacked bar chart
        set2?.let {
            return BarData(set1!!, set2!!)
        }

        return BarData(set1!!).apply { this.barWidth = barWid }
    }
}