package greenely.greenely.retail.ui.charts.consumptiondatachart

import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import greenely.greenely.R
import greenely.greenely.retail.models.CurrentMonthPointsJson
import greenely.greenely.retail.ui.charts.BarChartMarkView
import java.lang.Math.abs

abstract class ConsumptionBarChartAction {

    /**
     * The next element in the chain.
     */
    var next: ConsumptionBarChartAction? = null

    /**
     * Apply the action to the chart.
     */
    abstract fun applyToChart(chart: BarChart): BarChart
}

internal class StyleXAxis : ConsumptionBarChartAction() {
    override fun applyToChart(chart: BarChart): BarChart {
        chart.xAxis.apply {
            granularity = 1.0f
            setDrawLabels(false)
            setDrawGridLines(false)
            setDrawAxisLine(false)
        }

        return chart.let { next?.applyToChart(it) ?: it }
    }
}

internal class StyleLeftAxis : ConsumptionBarChartAction() {
    override fun applyToChart(chart: BarChart): BarChart {
        chart.axisLeft.apply {
            axisMinimum = 0.0f
            setDrawAxisLine(false)
            setDrawGridLines(false)
            setDrawLabels(false)
        }

        return chart.let { next?.applyToChart(it) ?: it }
    }
}

internal class StyleRightAxis : ConsumptionBarChartAction() {
    override fun applyToChart(chart: BarChart): BarChart {
        chart.axisRight.isEnabled = false

        return chart.let { next?.applyToChart(it) ?: it }
    }
}

internal class StyleChart : ConsumptionBarChartAction() {
    override fun applyToChart(chart: BarChart): BarChart {
        chart.apply {
            setTouchEnabled(true)
            description.isEnabled = false
            legend.isEnabled = false
            setScaleEnabled(false)
            setViewPortOffsets(0f, 0f, 0f, 0f)
            renderer = CustomBarChartRenderer(chart, chart.animator, chart.viewPortHandler)
            animateY(1000)

            // create marker to display box when values are selected
            if (chart.context != null) {
                val markView = BarChartMarkView(chart.context, R.layout.custom_marker_view_bar)
                markView.chartView = chart
                chart.marker = markView
            }
        }
        return chart.let { next?.applyToChart(it) ?: it }
    }
}

internal class SetChartData(
        private val data: List<CurrentMonthPointsJson?>
) : ConsumptionBarChartAction() {

    override fun applyToChart(chart: BarChart): BarChart {
        val context = chart.context

        val barDataSet = createBarDataSet(data, context.getString(R.string.consumption_per_month)).apply {
            color = ContextCompat.getColor(context, R.color.consumption_chart)
            setDrawValues(false)
            highLightAlpha = 255
            highLightColor = ContextCompat.getColor(context, R.color.lime_green)
        }

        // By default MPAndroidChart sets the width of a bar to 0.85f
        // Because we use timestamps the differences between points is so large that
        // Every bar width becomes so slim that these bars become "invisible"
        // Workaround is to get the difference between first two timestamps and adjust the bar width

        val barWidthDiff: Long = kotlin.math.abs((data[0]?.timestamp?.toLong()
                ?: 0) - (data[1]?.timestamp?.toLong() ?: 0))

        chart.apply {

            data = BarData().apply {

                if (barDataSet.entryCount > 0) {
                    addDataSet(barDataSet)
                    barWidth = 0.6f * barWidthDiff
                }

                //Adjust min/max because barchart cuts off bars
                if (xAxis != null) {
                    xAxis.axisMinimum = barDataSet.xMin - barWidthDiff / 2
                    xAxis.axisMaximum = barDataSet.xMax + barWidthDiff / 2
                }

            }

            invalidate()
        }

        return chart.let { next?.applyToChart(it) ?: it }
    }

    private fun createBarDataSet(values: List<CurrentMonthPointsJson?>, label: String): BarDataSet =
            values.foldIndexed(
                    BarDataSet(mutableListOf(), label)
            ) { i: Int, acc: BarDataSet, v: CurrentMonthPointsJson? ->
                v?.let {
                    acc.addEntry(v.costInKr?.toFloat()?.let { it1 -> BarEntry(v.timestamp.toFloat(), it1) })
                }
                acc
            }
}

class ConsumptionChartSetupFactory {

    fun createChartSetupWithData(data: List<CurrentMonthPointsJson?>): ConsumptionBarChartAction {
        val styleXAxis = StyleXAxis()
        val styleRightAxis = StyleRightAxis()
        val styleLeftAxis = StyleLeftAxis()
        val styleChart = StyleChart()
        val setChartData = SetChartData(data)

        styleXAxis.next = styleRightAxis
        styleRightAxis.next = styleLeftAxis
        styleLeftAxis.next = styleChart
        styleChart.next = setChartData

        return styleXAxis
    }
}

