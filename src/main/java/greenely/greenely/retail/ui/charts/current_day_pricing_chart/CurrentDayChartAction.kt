package greenely.greenely.retail.ui.charts.current_day_pricing_chart

import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import greenely.greenely.R
import greenely.greenely.retail.models.PricingPointsJson
import greenely.greenely.retail.ui.charts.LeftAxisValueFormatter
import greenely.greenely.retail.ui.charts.LineChartMarkView
import greenely.greenely.retail.ui.charts.XAxisValueFormatter

abstract class CurrentDayChartAction {

    /**
     * The next element in the chain.
     */
    var next: CurrentDayChartAction? = null

    /**
     * Apply the action to the chart.
     */
    abstract fun applyToChart(chart: LineChart): LineChart
}

internal class StyleXAxis : CurrentDayChartAction() {
    override fun applyToChart(chart: LineChart): LineChart {
        chart.xAxis.apply {
            setDrawLabels(true)
            setDrawGridLines(false)
            position = XAxis.XAxisPosition.BOTTOM
            setDrawAxisLine(true)
            valueFormatter = XAxisValueFormatter()
            setLabelCount(5, true)
        }

        return chart.let { next?.applyToChart(it) ?: it }
    }
}

internal class StyleLeftAxis : CurrentDayChartAction() {
    override fun applyToChart(chart: LineChart): LineChart {
        chart.axisLeft.apply {
            isEnabled = true
            setDrawLabels(true)
            setDrawAxisLine(false)
            valueFormatter = LeftAxisValueFormatter()
            setLabelCount(5, true)
        }
        return chart.let { next?.applyToChart(it) ?: it }
    }
}

internal class StyleRightAxis : CurrentDayChartAction() {
    override fun applyToChart(chart: LineChart): LineChart {
        chart.axisRight.isEnabled = false

        return chart.let { next?.applyToChart(it) ?: it }
    }
}

internal class StyleChart : CurrentDayChartAction() {
    override fun applyToChart(chart: LineChart): LineChart {

        chart.apply {
            //setTouchEnabled(true)
            description.isEnabled = false
            legend.isEnabled = false
            setScaleEnabled(false)
            extraBottomOffset = 100f
        }
        return chart.let { next?.applyToChart(it) ?: it }
    }
}

internal class SetChartData(
        private val data: List<PricingPointsJson?>
) : CurrentDayChartAction() {

    companion object {
        /** The width of the lines in the chart. */
        val LINE_WIDTH = 2.0f
    }

    override fun applyToChart(chart: LineChart): LineChart {
        val context = chart.context

        val lineDataSet = createLineDataSet(data, context.getString(R.string.prices)).apply {
            mode = LineDataSet.Mode.HORIZONTAL_BEZIER
            setDrawFilled(true)
            fillColor = ContextCompat.getColor(context, R.color.lightning_yellow)
            fillAlpha = 100
            color = ContextCompat.getColor(context, R.color.lightning_yellow)
            setDrawCircles(false)
            setCircleColor(ContextCompat.getColor(context, R.color.lightning_yellow))
            setCircleColorHole(ContextCompat.getColor(context, R.color.lightning_yellow))
            setDrawValues(false)
            circleRadius = 5f
            lineWidth = LINE_WIDTH
            setDrawValues(false)
            setDrawHorizontalHighlightIndicator(false)

            chart.axisLeft.textColor = ContextCompat.getColor(context, R.color.grey2)
            chart.xAxis.textColor = ContextCompat.getColor(context, R.color.grey2)

            // create marker to display box when values are selected
            val mv = LineChartMarkView(context, R.layout.custom_marker_view)
            // Set the marker to the chart
            mv.chartView = chart
            chart.marker = mv

        }

        //chart.xAxis.axisMinimum = lineDataSet.xMin - 10000

        chart.apply {
            data = LineData().apply {
                if (lineDataSet.entryCount > 0) {
                    addDataSet(lineDataSet)
                }
                xAxis.yOffset = 20f
            }

            invalidate()
        }
        return chart.let { next?.applyToChart(it) ?: it }
    }

    private fun createLineDataSet(values: List<PricingPointsJson?>, label: String): LineDataSet =
            values.foldIndexed(LineDataSet(mutableListOf(), label))
            { i: Int, acc: LineDataSet, v: PricingPointsJson? ->
                v?.let {
                    acc.addEntry(v.price?.toFloat()?.let { it1 -> Entry(v.timestamp.toFloat(), it1) })
                }
                acc
            }
}

class CurrentDayPriceChartSetupFactory {

    fun createChartSetupWithData(data: List<PricingPointsJson?>): CurrentDayChartAction {
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