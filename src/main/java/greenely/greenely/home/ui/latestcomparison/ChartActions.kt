package greenely.greenely.home.ui.latestcomparison

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import greenely.greenely.OpenClassOnDebug
import greenely.greenely.R
import greenely.greenely.home.data.Comparison
import greenely.greenely.utils.toCurrenyWithDecimalCheck
import greenely.greenely.views.chartviews.RoundedHorizontalBarChartRenderer
import java.math.RoundingMode
import java.text.DecimalFormat


/**
 * Chain for actions to be applied to a bar chart.
 */
abstract class ChartAction {

    /**
     * The next element in the chain.
     */
    var next: ChartAction? = null

    /**
     * Apply the action the the bar chart.
     */
    abstract fun applyToChart(chart: HorizontalBarChart): HorizontalBarChart
}

/**
 * Value fromatter for the x-axis of the chart.
 */
 class XAxisValueFormatter(private val context: Context) : IAxisValueFormatter {

    var setAllLabelsZero=false

    /**
     * Get the label for a specified index.
     */
    override fun getFormattedValue(value: Float, axis: AxisBase?): String {

        if (axis is XAxis) {
            return when (value) {
                1.0f -> context.getString(R.string.me)
                2.0f -> context.getString(R.string.others)
                3.0f -> context.getString(R.string.top)
                else -> ""
            }
        } else {

            if(setAllLabelsZero)
                return "0,0"

            val formatter = DecimalFormat()
            formatter.roundingMode = RoundingMode.CEILING

            when {
                value == 0.0f -> {
                    return ""
                }

                value < 10 -> {
                    formatter.applyPattern("###,###,##0.00")
                    return formatter.format(value)
                }
                value < 100 -> {
                    formatter.applyPattern("###,###,##0.0")
                    return formatter.format(value)
                }
                else -> {
                    formatter.applyPattern("###,###,##0")
                    return formatter.format(value)
                }
            }
        }
    }
}



internal class BestValueFormatter(val context: Context) : IValueFormatter {
    /**
     * Get the label for a specified index.
     */
    override fun getFormattedValue(value: Float, entry: Entry?, dataSetIndex: Int, viewPortHandler: ViewPortHandler?): String {
        val formatter = DecimalFormat()
        formatter.roundingMode = RoundingMode.CEILING

        when {
            value == 0f -> {
                return ""
            }
            else -> {
                return value.toCurrenyWithDecimalCheck() + " kWh \n" + context.getString(R.string.top)
            }
        }
    }
}


internal class MeValueFormatter(val context: Context) : IValueFormatter {
    /**
     * Get the label for a specified index.
     */
    override fun getFormattedValue(value: Float, entry: Entry?, dataSetIndex: Int, viewPortHandler: ViewPortHandler?): String {
        val formatter = DecimalFormat()
        formatter.roundingMode = RoundingMode.CEILING

        when {
            value == 0f -> {
                return ""
            }
            else -> {
                return value.toCurrenyWithDecimalCheck() + " kWh \n " + context.getString(R.string.me)
            }
        }
    }
}

internal class OthersValueFormatter(val context: Context) : IValueFormatter {
    /**
     * Get the label for a specified index.
     */
    override fun getFormattedValue(value: Float, entry: Entry?, dataSetIndex: Int, viewPortHandler: ViewPortHandler?): String {
        val formatter = DecimalFormat()
        formatter.roundingMode = RoundingMode.CEILING

        when {
            value == 0f -> {
                return ""
            }
            else -> {
                return value.toCurrenyWithDecimalCheck() + " kWh \n" + context.getString(R.string.others)
            }
        }
    }
}


/**
 * Style the x-axis of the chart.
 *
 * @property valueFormatter The [IAxisValueFormatter] of the axis.
 * @property next The next action in the chain.
 */
internal class StyleXAxis(
        private val customValueFormatter: XAxisValueFormatter,
        val maxValue: Float?
) : ChartAction() {
    override fun applyToChart(chart: HorizontalBarChart): HorizontalBarChart {
        val context = chart.context
        chart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            valueFormatter =customValueFormatter
            textSize = 14.0f
            setDrawGridLines(false)
            setLabelCount(5,true)
            setDrawAxisLine(false)
            gridLineWidth = 0.5f
            maxValue?.let {
                axisMaximum=4f
            }
            gridColor = ContextCompat.getColor(context, R.color.grey9)
            isEnabled = false
            textColor = ContextCompat.getColor(context, R.color.axis_value_text_color)
        }

        return chart.let { next?.applyToChart(it) ?: it }
    }
}

/**
 * Style the left axis of the chart.
 *
 * @property next The next action in the chain.
 */
internal class StyleLeftAxis(
) : ChartAction() {
    override fun applyToChart(chart: HorizontalBarChart): HorizontalBarChart {
        val context = chart.context
        chart.axisLeft.apply {
            labelCount=5
            isEnabled = false
        }

        return chart.let { next?.applyToChart(it) ?: it }
    }
}

/**
 * Style the right axis of the chart.
 *
 * @property next The next action in the chain.
 */
internal class StyleRightAxis(
        private val maxValue: Float?,
        private val customValueFormatter: XAxisValueFormatter
) : ChartAction() {
    override fun applyToChart(chart: HorizontalBarChart): HorizontalBarChart {
        val context = chart.context
        chart.axisRight.apply {
            isEnabled = true
            axisMinimum = 0f


            if(maxValue?.compareTo(0)?:0 > 0) {
                customValueFormatter.setAllLabelsZero=false
                maxValue?.let {
                    axisMaximum=it
                }
            }
            else
                customValueFormatter.setAllLabelsZero=true


            typeface = ResourcesCompat.getFont(context, R.font.gt_america_light)
            valueFormatter = customValueFormatter
            gridColor = ContextCompat.getColor(context, R.color.grey9)
            setDrawAxisLine(false)
            textSize=12f
            setLabelCount(5,true)
            textColor = ContextCompat.getColor(context, R.color.grey15)
        }

        return chart.let { next?.applyToChart(it) ?: it }
    }
}

/**
 * Style the non axis parts of the chart.
 *
 * @property next The next action in the chain.
 */
internal class StyleChart : ChartAction() {
    override fun applyToChart(chart: HorizontalBarChart): HorizontalBarChart {
        chart.apply {
            setTouchEnabled(false)
            description.isEnabled = false
            legend.isEnabled = false
            setScaleEnabled(false)
            minOffset = -35f
            extraLeftOffset = -35f
            extraBottomOffset = 35f
            extraRightOffset = 35f
            extraTopOffset = 35f
            renderer = RoundedHorizontalBarChartRenderer(chart, chart.getAnimator(), chart.getViewPortHandler())

        }



        return chart.let { next?.applyToChart(chart) ?: it }
    }
}

/**
 * Set the data for the chart.
 *
 * @property comparison The data of the chart.
 * @property next The next action in the chain.
 */

internal class SetChartData(
        private val comparison: Comparison
) : ChartAction() {

    companion object {
        var BAR_WIDTH=0.70f
        var TEXT_SIZE=12.0f
        var DEPENDENCY=YAxis.AxisDependency.RIGHT
    }

    override fun applyToChart(chart: HorizontalBarChart): HorizontalBarChart {
        val context = chart.context
        val meSet = BarDataSet(mutableListOf(BarEntry(3.0f, comparison.me
                ?: 0f,ComparisonData(context,comparison))), context.getString(R.string.me)).apply {
            color = ContextCompat.getColor(context, R.color.green_1)
            valueTextSize = TEXT_SIZE
            valueTypeface = ResourcesCompat.getFont(context, R.font.gt_america_light)
            axisDependency = DEPENDENCY
            valueFormatter = MeValueFormatter(context)
        }
        val othersSet = BarDataSet(mutableListOf(BarEntry(2.0f, comparison.others
                ?: 0.0f)), context.getString(R.string.others)).apply {
            color = ContextCompat.getColor(context, R.color.yellow4)
            valueTextSize = TEXT_SIZE
            valueTypeface = ResourcesCompat.getFont(context, R.font.gt_america_light)
            axisDependency = DEPENDENCY
            valueFormatter = OthersValueFormatter(context)

        }
        val bestSet = BarDataSet(mutableListOf(BarEntry(1.0f, comparison.best
                ?: 0.0f)), context.getString(R.string.top)).apply {
            color = ContextCompat.getColor(context, R.color.blue1)
            valueTextSize = TEXT_SIZE
            valueTypeface = ResourcesCompat.getFont(context, R.font.gt_america_light)
            axisDependency = DEPENDENCY
            valueFormatter = BestValueFormatter(context)

        }

        chart.apply {
            data = BarData(meSet, bestSet, othersSet).apply {
                barWidth = BAR_WIDTH
            }
            invalidate()
        }

        chart.animateY(1000)

        return chart.let {
            next?.applyToChart(it) ?: it
        }
    }

}

/**
 * Factory for a [ChartAction].
 */
@OpenClassOnDebug
class LatestComparisonChartSetupFactory(
        private val valueFormatter: XAxisValueFormatter
) {
    /**
     * Create a chart chain with.
     *
     * [StyleXAxis] -> [StyleLeftAxis] -> [StyleRightAxis] -> [StyleChart] -> [SetChartData]
     */
    fun createChartSetupWithData(comparison: Comparison, maxValue: Float?): ChartAction {
        val styleXAxis = StyleXAxis(valueFormatter,maxValue)
        val styleLeftAxis = StyleLeftAxis()
        val styleRightAxis = StyleRightAxis(maxValue, valueFormatter)
        val styleChart = StyleChart()
        val setChartData = SetChartData(comparison)

        styleXAxis.next = styleLeftAxis
        styleLeftAxis.next = styleRightAxis
        styleRightAxis.next = styleChart
        styleChart.next = setChartData

        return styleXAxis
    }
}

data class ComparisonData(val context: Context,val lastMonthComparion: Comparison) {

    var shouldDisplayIcon: Boolean = false

    var icon: Bitmap? = null

    init {
        shouldDisplayIcon = lastMonthComparion.me?.let {
            it > 0 && it < lastMonthComparion.others ?: 0f
        } ?: false

        if(shouldDisplayIcon)
            icon=getBitmapFromVectorDrawable(R.drawable.ic_happy_new)

    }

    fun getBitmapFromVectorDrawable(drawableId: Int): Bitmap {
        var drawable = ContextCompat.getDrawable(context, drawableId)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = DrawableCompat.wrap(drawable!!).mutate()
        }

        val bitmap = Bitmap.createBitmap(drawable!!.intrinsicWidth,
                drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }




}