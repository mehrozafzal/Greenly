package greenely.greenely.history.views.chart

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.renderer.LineChartRenderer
import com.github.mikephil.charting.utils.ViewPortHandler
import greenely.greenely.history.views.TemperatureChartFragment
import greenely.greenely.home.ui.historicalcomparison.MarkerObject

class CustomLineChartRenderer(chart: LineDataProvider, animator: ChartAnimator,
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
                                if (it is MarkerObject || it is MarkerObject) {
                                    setPointCircle(c, set, entry, it.highlightCircleColor)

                                } else if (it is TemperatureChartFragment.TemperatureMarkerObject) {
                                    setPointCircle(c, set, entry, it.highlightCircleColor)

                                } else if (it is PriceChartFragment.PriceMarkerObject) {
                                    setPointCircle(c, set, entry, it.highlightCircleColor)

                                }

                            }


                        }
                    }

                }

            }


        }

    }

    private fun setPointCircle(c: Canvas, set: ILineDataSet, entry: Entry, highLightColor: Int) {
        val pix = mChart.getTransformer(set.axisDependency).getPixelForValues(entry.x, entry.y * mAnimator.phaseY)

        var circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        circlePaint.style = Paint.Style.FILL
        circlePaint.color = highLightColor

        var outerCircle = Paint(Paint.ANTI_ALIAS_FLAG)
        outerCircle.style = Paint.Style.FILL
        outerCircle.color = Color.rgb(255, 255, 255)

        c.drawCircle(pix.x.toFloat(), pix.y.toFloat(), 15f, outerCircle)
        c.drawCircle(pix.x.toFloat(), pix.y.toFloat(), 10f, circlePaint)
    }

}
