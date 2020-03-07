package greenely.greenely.home.ui.historicalcomparison

import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.*
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.renderer.YAxisRenderer
import greenely.greenely.OpenClassOnDebug
import greenely.greenely.R
import greenely.greenely.extensions.dp
import greenely.greenely.home.data.Comparison
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*
import javax.inject.Inject
import androidx.databinding.adapters.TextViewBindingAdapter.setText
import android.R.id
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorSpace
import android.graphics.Paint
import android.opengl.ETC1.*
import android.view.View
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider
import com.github.mikephil.charting.renderer.LineChartRenderer
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.ViewPortHandler
import greenely.greenely.home.ui.latestcomparison.ComparisonData
import greenely.greenely.utils.toCurrenyWithDecimalCheck
import greenely.greenely.utils.toCurrenyWithDecimalCheckAndSymbol


/**
 * Chain action for actions to be applied to a [CombinedChart].
 */
abstract class ChartAction {

    /**
     * The next element in the chain.
     */
    var next: ChartAction? = null

    /**
     * Apply the action to the chart.
     */
    abstract fun applyToChart(chart: LineChart): LineChart
}

/**
 * Factory for XAxis formatters.
 */
@OpenClassOnDebug
class XAxisValueFormatterFactory {
    private val monthFormatter: DateTimeFormatter by lazy {
        DateTimeFormat.forPattern("MMM").withLocale(Locale("sv", "SE"))
    }

    private val weekFormatter: DateTimeFormatter by lazy {
        DateTimeFormat.forPattern("ww")
    }

    private val dayFormatter: DateTimeFormatter by lazy {
        DateTimeFormat.forPattern("EE").withLocale(Locale("sv", "SE"))
    }

    /**
     * Create an XAxis formatter.
     */
    fun createValueFormatterForData(comparisons: List<Comparison>, resolution: Int) =
            IAxisValueFormatter { value, _ ->
                val date = comparisons.getOrNull(value.toInt())?.date
                if (date != null) {
                    when (resolution) {
                        R.id.weeks -> weekFormatter.print(date)
                        R.id.days -> dayFormatter.print(date).capitalize()
                        R.id.months -> monthFormatter.print(date).removeSuffix(".").capitalize()
                        else -> ""
                    }
                } else {
                    ""
                }
            }
}

class YAxisValueFormatterFactory @Inject constructor() : IAxisValueFormatter {

    /**
     * Get the label for a specified index.
     */
    override fun getFormattedValue(value: Float, axis: AxisBase?): String {

        val formatter = DecimalFormat()
        formatter.roundingMode = RoundingMode.CEILING

        when {
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

internal class StyleXAxis(
        private val comparisons: List<Comparison>,
        private val resolution: Int,
        private val formatterFactory: XAxisValueFormatterFactory
) : ChartAction() {
    override fun applyToChart(chart: LineChart): LineChart {
        val context = chart.context
        chart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1.0f
            setDrawGridLines(true)
            axisMinimum = -0.5f
            axisMaximum = 6.5f
            setDrawAxisLine(false)

            typeface = ResourcesCompat.getFont(context, R.font.gt_america_light)
            gridColor = ContextCompat.getColor(context, R.color.grey9)
            textSize = 12f
            textColor = ContextCompat.getColor(context, R.color.grey15)
            valueFormatter = formatterFactory.createValueFormatterForData(comparisons, resolution)
        }

        return chart.let { next?.applyToChart(it) ?: it }
    }
}

internal class StyleLeftAxis(
        private val maxValue: Float?
) : ChartAction() {
    override fun applyToChart(chart: LineChart): LineChart {
        val context = chart.context
        chart.axisLeft.apply {
            isEnabled = false
            axisMaximum = maxValue ?: 0.0f
        }


        return chart.let { next?.applyToChart(it) ?: it }
    }
}

internal class StyleLegend : ChartAction() {


    override fun applyToChart(chart: LineChart): LineChart {
        val context = chart.context
        chart.legend.apply {
            isEnabled = false
        }

        return chart.let { next?.applyToChart(it) ?: it }
    }
}

internal class StyleRightAxis : ChartAction() {
    override fun applyToChart(chart: LineChart): LineChart {
        chart.axisRight.isEnabled = false
        chart.axisRight.setDrawGridLines(false)

        return chart.let { next?.applyToChart(it) ?: it }
    }
}

internal class StyleChart : ChartAction() {
    override fun applyToChart(chart: LineChart): LineChart {
        chart.apply {
            setNoDataText(context.getString(R.string.loading))
            setNoDataTextColor(ContextCompat.getColor(context, R.color.accent))
            setScaleEnabled(false)
            setTouchEnabled(true)
            isHighlightPerDragEnabled=false
            isDragEnabled=false
            description.isEnabled = false
            extraBottomOffset = 10f
            renderer = CustomLineChartRenderer(chart.context, this, animator, viewPortHandler)

        }


        chart.marker = CustomMarkerView(chart.context, R.layout.historical_chart_marker_view, chart)



        return chart.let { next?.applyToChart(it) ?: it }
    }
}

internal class SetChartData(
        private val data: List<Comparison>
) : ChartAction() {

    companion object {
        /** The width of the lines in the chart. */
        val LINE_WIDTH = 2.0f
        val HIGHLIGHT_WIDTH = 1.1f
    }

    override fun applyToChart(chart: LineChart): LineChart {
        val context = chart.context

        val meValues = data.map { it.date to it.me }.map { it.second }.toMutableList()
        val othersValues = data.map { it.date to it.others }.map { it.second }.toMutableList()
        val bestValues = data.map { it.date to it.best }.map { it.second }.toMutableList()


        val meLineColor = ContextCompat.getColor(context, R.color.green_1)
        val othersLineColor = ContextCompat.getColor(context, R.color.yellow4)
        val bestLineColor = ContextCompat.getColor(context, R.color.blue1)

        val meLineSet = createLineDataSet(meValues, meValues, othersValues, bestValues, context.getString(R.string.me), meLineColor).apply {
            color = meLineColor
            setDrawValues(false)
            setDrawCircles(false)
            isHighlightEnabled = true
            axisDependency = YAxis.AxisDependency.LEFT
            lineWidth = LINE_WIDTH
            highlightLineWidth = HIGHLIGHT_WIDTH
            highLightColor = ContextCompat.getColor(context, R.color.grey13)
            setDrawHorizontalHighlightIndicator(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER

        }





        val othersLineSet = createLineDataSet(othersValues, meValues, othersValues, bestValues, context.getString(R.string.others), othersLineColor).apply {
            color = othersLineColor
            setDrawValues(false)
            setDrawCircles(false)
            lineWidth = LINE_WIDTH
            setDrawHorizontalHighlightIndicator(false)
            isHighlightEnabled = true
            highlightLineWidth = HIGHLIGHT_WIDTH
            highLightColor = ContextCompat.getColor(context, R.color.grey13)
            axisDependency = YAxis.AxisDependency.LEFT
            mode = LineDataSet.Mode.CUBIC_BEZIER


        }

        val bestLineSet = createLineDataSet(bestValues, meValues, othersValues, bestValues, context.getString(R.string.top), bestLineColor).apply {
            color = bestLineColor
            setDrawValues(false)
            setDrawCircles(false)
            axisDependency = YAxis.AxisDependency.LEFT
            lineWidth = LINE_WIDTH
            highlightLineWidth = HIGHLIGHT_WIDTH
            highLightColor = ContextCompat.getColor(context, R.color.grey13)
            setDrawHorizontalHighlightIndicator(false)
            isHighlightEnabled = true
            mode = LineDataSet.Mode.CUBIC_BEZIER

        }

        chart.apply {
            data = LineData().apply {
                if (meLineSet.entryCount > 0) addDataSet(meLineSet)
                if (othersLineSet.entryCount > 0) addDataSet(othersLineSet)

                if (bestLineSet.entryCount > 0) addDataSet(bestLineSet)
                setData(lineData)
            }



            invalidate()
        }
        chart.highlightValue(chart.data.xMax-1,0)


        return chart.let { next?.applyToChart(it) ?: it }
    }


    private fun createLineDataSet(values: List<Float?>,
                                  me: List<Float?>,
                                  others: List<Float?>,
                                  best: List<Float?>,
                                  label: String, highlightCircleColor: Int): LineDataSet {

        var acc = mutableListOf<Entry>()
        var i = 0
        values.forEach {
            it?.let {
                if (i == 0) {
                    acc.add(Entry(i.toFloat() - 0.5f, it))
                }
                acc.add(Entry(i.toFloat(), it, MarkerObject(me.get(i)?.toCurrenyWithDecimalCheckAndSymbol() ?: "",
                        best.get(i)?.toCurrenyWithDecimalCheckAndSymbol() ?: "",
                        others.get(i)?.toCurrenyWithDecimalCheckAndSymbol() ?: "",
                        highlightCircleColor)))

                if (i == values.size - 1) {
                    acc.add(Entry(i.toFloat() + 1, it))
                }
            }

            i++
        }




        return LineDataSet(acc, label)

    }


}

class CustomMarkerView(context: Context, layoutResource: Int, val chart: LineChart) : MarkerView(context, layoutResource) {

    private val tvMe: TextView
    private val tvBest: TextView
    private val tvOthers: TextView


    init {
        // this markerview only displays a textview
        tvMe = findViewById<TextView>(R.id.tvMe)
        tvBest = findViewById<TextView>(R.id.tvBest)
        tvOthers = findViewById<TextView>(R.id.tvOthers)
        chartView = chart

    }

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        e?.data?.let {
            if (it is MarkerObject) {

                tvMe.text = (it.me)
                tvBest.text = (it.best)
                tvOthers.text = (it.others)

                tvMe.visibility = if (it.me.length == 0) View.GONE else View.VISIBLE
                tvBest.visibility = if (it.best.length == 0) View.GONE else View.VISIBLE
                tvOthers.visibility = if (it.others.length == 0) View.GONE else View.VISIBLE

                super.refreshContent(e, highlight)
            }
        }

    }

    override fun draw(canvas: Canvas, posX: Float, posY: Float) {

        val offset = getOffsetForDrawingAtPoint(posX, posY)

        val saveId = canvas.save()
        // translate to the correct position and draw
        canvas.translate(posX + offset.x, 0f)
        draw(canvas)
        canvas.restoreToCount(saveId)
    }

}


class CustomLineChartRenderer(val context: Context, chart: LineDataProvider, animator: ChartAnimator,
                              viewPortHandler: ViewPortHandler) : LineChartRenderer(chart, animator, viewPortHandler) {

    override fun drawHighlighted(c: Canvas, indices: Array<Highlight>) {
        if (indices.size == 1)
            if (indices[0].x < 0)
                return


        val lineData = mChart.lineData

        for (high in indices) {

            val set = lineData.getDataSetByIndex(high.dataSetIndex)


            if (set == null || !set.isHighlightEnabled)
                continue

            val e = set.getEntryForXValue(high.x, high.y)


            if (!isInBoundsX(e, set))
                continue

            val pix = mChart.getTransformer(set.axisDependency).getPixelForValues(e.x, e.y * mAnimator
                    .phaseY)

            high.setDraw(pix.x.toFloat(), pix.y.toFloat())

            // draw the lines
            drawHighlightLines(c, pix.x.toFloat(), pix.y.toFloat(), set)

            lineData.dataSets.forEach {
                var label = it.label
                it?.let {
                    var entries = it.getEntriesForXValue(high.x)
                    if (!entries.isEmpty()) {

                        var entry = entries[0]
                        var set = it
                        entry?.let {

                            it.data.let {
                                if (it is MarkerObject) {

                                    val pix = mChart.getTransformer(set.axisDependency).getPixelForValues(entry.x, entry.y * mAnimator.phaseY)

                                    var circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
                                    circlePaint.style = Paint.Style.FILL
                                    circlePaint.color = it.highlightCircleColor

                                    var outerCircle = Paint(Paint.ANTI_ALIAS_FLAG)
                                    outerCircle.style = Paint.Style.FILL
                                    outerCircle.color = Color.rgb(255,255,255)

                                    c.drawCircle(pix.x.toFloat(), pix.y.toFloat(), 15f, outerCircle)
                                    c.drawCircle(pix.x.toFloat(), pix.y.toFloat(), 10f, circlePaint)

                                }

                            }


                        }
                    }

                }

            }


        }

    }

}


data class MarkerObject(val me: String, val best:
String, val others: String,
                        val highlightCircleColor: Int)

/**
 * Factory for creating a [ChartAction] for the historical comparison chart.
 */
class HistoricalComparisonChartSetupFactory(
        private val valueFormatter: IAxisValueFormatter,
        private val valueFormatterFactory: XAxisValueFormatterFactory
) {
    /**
     * Should Create a [ChartAction] with:
     *
     * [StyleXAxis] -> [StyleLeftAxis] -> [StyleLegend] -> [StyleRightAxis] -> [StyleChart] -> [SetChartData].
     */
    fun createChartSetupWithData(data: List<Comparison>, resolution: Int, maxValue: Float?): ChartAction {
        val styleXAxis = StyleXAxis(data, resolution, valueFormatterFactory)
        val styleLeftAxis = StyleLeftAxis(maxValue)
        val styleLegend = StyleLegend()
        val styleRightAxis = StyleRightAxis()
        val styleChart = StyleChart()
        val setChartData = SetChartData(data)

        styleXAxis.next = styleLeftAxis
        styleLeftAxis.next = styleLegend
        styleLegend.next = styleRightAxis
        styleRightAxis.next = styleChart
        styleChart.next = setChartData

        return styleXAxis
    }
}
