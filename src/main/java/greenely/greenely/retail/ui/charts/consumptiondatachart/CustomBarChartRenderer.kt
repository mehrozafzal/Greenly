package greenely.greenely.retail.ui.charts.consumptiondatachart

import android.graphics.*
import android.os.Build
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.github.mikephil.charting.utils.ViewPortHandler

open class CustomBarChartRenderer(chart: BarDataProvider, animator: ChartAnimator, viewPortHandler: ViewPortHandler) : BarChartRenderer(chart, animator, viewPortHandler) {

    override fun drawDataSet(c: Canvas?, dataSet: IBarDataSet?, index: Int) {

        val trans = mChart.getTransformer(dataSet!!.axisDependency)

        val phaseX = mAnimator.phaseX
        val phaseY = mAnimator.phaseY

        val buffer = mBarBuffers[index]
        buffer.setPhases(phaseX, phaseY)
        buffer.setDataSet(index)
        buffer.setInverted(mChart.isInverted(dataSet.axisDependency))
        buffer.setBarWidth(mChart.barData.barWidth)

        buffer.feed(dataSet)

        trans.pointValuesToPixel(buffer.buffer)

        val isSingleColor = dataSet.colors.size == 1

        if (isSingleColor) {
            mRenderPaint.color = dataSet.color
        }

        var j = 0
        while (j < buffer.size()) {

            if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2])) {
                j += 4
                continue
            }

            if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j]))
                break

            if (!isSingleColor) {
                mRenderPaint.color = dataSet.getColor(j / 4)
            }

            mRenderPaint.shader = LinearGradient(
                    buffer.buffer[j],
                    buffer.buffer[j + 3],
                    buffer.buffer[j],
                    buffer.buffer[j + 1],
                    dataSet.color,
                    dataSet.color,
                    Shader.TileMode.MIRROR)


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                c?.save()
                c?.clipRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2], buffer.buffer[j + 3])
                c?.drawRoundRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                        buffer.buffer[j + 3] + 70, 70f, 70f, mRenderPaint)
                c?.restore()

            } else
                c?.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                        buffer.buffer[j + 3], mRenderPaint)

            j += 4
        }
    }

    override fun drawHighlighted(canvas: Canvas, indices: Array<Highlight>) {
        val barData = mChart.barData

        for (high in indices) {

            val set = barData.getDataSetByIndex(high.dataSetIndex)

            if (set == null || !set.isHighlightEnabled)
                continue

            val e = set.getEntryForXValue(high.x, high.y)

            if (!isInBoundsX(e, set))
                continue

            val trans = mChart.getTransformer(set.axisDependency)

            mHighlightPaint.color = set.highLightColor
//            mHighlightPaint.alpha = set.highLightAlpha

            val isStack = if (high.stackIndex >= 0 && e.isStacked) true else false

            val y1: Float
            val y2: Float

            if (isStack) {

                if (mChart.isHighlightFullBarEnabled) {

                    y1 = e.positiveSum
                    y2 = -e.negativeSum

                } else {

                    val range = e.ranges[high.stackIndex]

                    y1 = range.from
                    y2 = range.to
                }

            } else {
                y1 = e.y
                y2 = 0f
            }

            prepareBarHighlight(e.x, y1, y2, barData.barWidth / 2f, trans)

            setHighlightDrawPos(high, mBarRect)

            canvas.drawTopRoundRect(mBarRect, mHighlightPaint, 70f)

        }
    }

    private fun Canvas.drawTopRoundRect(rect: RectF, paint: Paint, radius: Float) {
        // Step 1. Draw rect with rounded corners.

        // Step 2. Draw simple rect with reduced height,
        // so it wont cover top rounded corners.
        drawRoundRect(rect, radius, radius, paint)

        drawRect(
                rect.left,
                rect.top+30 ,
                rect.right,
                rect.bottom,
                paint
        )


    }

}

