package greenely.greenely.feed.utils.charting

import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import greenely.greenely.R
import greenely.greenely.utils.consumptionToString
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormatter


class ChartManager(
        private val barChart: BarChart,
        private val dateFormatter: DateTimeFormatter
) {

    private val _labels = mutableListOf<DateTime>()
    private var labels: List<DateTime>
        get() = _labels
        set(value) {
            _labels.clear()
            _labels.addAll(value)
        }

    private val _values = mutableListOf<Float>()
    private var values: List<Float>
        get() = _values
        set(value) {
            _values.clear()
            _values.addAll(value)
        }

    private val xAxisFormatter = IAxisValueFormatter { value, _ ->
        val label = labels.getOrNull(value.toInt())

        if (label != null) {
            dateFormatter.print(label).capitalize()
        } else {
            ""
        }
    }

    private val yAxisFormatter = IAxisValueFormatter { value, _ ->
        value.consumptionToString()
    }

    init {
        barChart.setScaleEnabled(false)
        barChart.setTouchEnabled(false)

        barChart.axisLeft.setDrawAxisLine(false)
        barChart.axisLeft.axisMinimum = 0f
        barChart.axisLeft.valueFormatter = yAxisFormatter

        barChart.axisRight.isEnabled = false

        barChart.xAxis.setDrawGridLines(false)
        barChart.xAxis.valueFormatter = xAxisFormatter
        barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM

        barChart.description.isEnabled = false

        barChart.legend.isEnabled = false
    }

    var chartData: List<Pair<DateTime, Float>>
        get() = values.zip(labels) { value: Float, label: DateTime -> label to value }
        set(value) {
            values = value.map { it.second }
            labels = value.map { it.first }

            barChart.data = createChartData()
            barChart.notifyDataSetChanged()
        }

    private fun createChartData(): BarData {
        val entries = values.mapIndexed { index, value -> BarEntry(index.toFloat(), value) }
        val set = BarDataSet(entries, barChart.context.getString(R.string.consumption)).apply {
            setDrawValues(false)
            color = ContextCompat.getColor(barChart.context, R.color.feed_chart_color)
        }

        barChart.axisLeft.axisMaximum = values.max()?.times(1.2f) ?: 0.0f




        return BarData(set)
    }
}

