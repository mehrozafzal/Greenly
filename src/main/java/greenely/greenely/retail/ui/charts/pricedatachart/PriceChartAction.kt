package greenely.greenely.retail.ui.charts.pricedatachart

import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import greenely.greenely.R

abstract class PriceChartAction {

    /**
     * The next element in the chain.
     */
    var next: PriceChartAction? = null

    /**
     * Apply the action to the chart.
     */
    abstract fun applyToChart(chart: LineChart): LineChart
}

internal class StyleXAxis : PriceChartAction() {
    override fun applyToChart(chart: LineChart): LineChart {
        chart.xAxis.apply {
            granularity = 1.0f
            axisMinimum = 0.0f
            setDrawLabels(false)
            setDrawGridLines(false)
            setDrawAxisLine(false)
        }

        return chart.let { next?.applyToChart(it) ?: it }
    }
}

internal class StyleLeftAxis : PriceChartAction() {
    override fun applyToChart(chart: LineChart): LineChart {
        chart.axisLeft.isEnabled = false

        return chart.let { next?.applyToChart(it) ?: it }
    }
}

internal class StyleRightAxis : PriceChartAction() {
    override fun applyToChart(chart: LineChart): LineChart {
        chart.axisRight.isEnabled = false

        return chart.let { next?.applyToChart(it) ?: it }
    }
}

internal class StyleChart : PriceChartAction() {
    override fun applyToChart(chart: LineChart): LineChart {
        chart.apply {
            setTouchEnabled(false)
            description.isEnabled = false
            legend.isEnabled = false
            setScaleEnabled(false)
            setViewPortOffsets(0f, 0f, 0f, 0f)
        }
        return chart.let { next?.applyToChart(it) ?: it }
    }
}

internal class SetChartData(
        private val data: List<Int?>
) : PriceChartAction() {

    companion object {
        /** The width of the lines in the chart. */
        val LINE_WIDTH = 3.0f
    }

    override fun applyToChart(chart: LineChart): LineChart {
        val context = chart.context

        val lineDataSet = createLineDataSet(data, context.getString(R.string.prices)).apply {
            mode = LineDataSet.Mode.HORIZONTAL_BEZIER
            setDrawFilled(true)
            fillColor = ContextCompat.getColor(context, R.color.shadow)
            fillAlpha = 50
            color = ContextCompat.getColor(context, R.color.white)
            setDrawCircles(false)
            setDrawValues(false)
            lineWidth = LINE_WIDTH

        }

        chart.apply {
            data = LineData().apply {
                if (lineDataSet.entryCount > 0) addDataSet(lineDataSet)
            }
            invalidate()
        }
        return chart.let { next?.applyToChart(it) ?: it }
    }

    private fun createLineDataSet(values: List<Int?>, label: String): LineDataSet =
            values.foldIndexed(
                    LineDataSet(mutableListOf(), label)
            ) { i: Int, acc: LineDataSet, v: Int? ->
                v?.let {
                    acc.addEntry(Entry(i.toFloat(), it.toFloat()))
                }
                acc
            }
}

class PriceChartSetupFactory {

    fun createChartSetupWithData(data: List<Int?>): PriceChartAction {
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
