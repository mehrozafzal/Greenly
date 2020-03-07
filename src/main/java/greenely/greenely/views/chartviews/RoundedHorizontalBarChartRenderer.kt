package greenely.greenely.views.chartviews

import android.content.Context
import android.graphics.*
import android.util.Log
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.renderer.HorizontalBarChartRenderer
import com.github.mikephil.charting.utils.ViewPortHandler
import android.graphics.Bitmap
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.buffer.BarBuffer
import com.github.mikephil.charting.data.BarEntry
import greenely.greenely.R
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import androidx.core.graphics.drawable.DrawableCompat
import android.os.Build.VERSION_CODES
import android.os.Build.VERSION_CODES.LOLLIPOP
import android.os.Build.VERSION
import android.os.Build.VERSION.SDK_INT
import android.graphics.drawable.Drawable
import android.os.Build
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IValueFormatter
import greenely.greenely.home.ui.latestcomparison.ComparisonData


class RoundedHorizontalBarChartRenderer(chart: BarDataProvider, animator: ChartAnimator, viewPortHandler: ViewPortHandler) : HorizontalBarChartRenderer(chart, animator, viewPortHandler) {

    private var mRadius = 10f


    fun setmRadius(mRadius: Float) {
        this.mRadius = mRadius
    }


    protected fun drawBarImages(c: Canvas, dataSet: IBarDataSet, index: Int) {

        val buffer = mBarBuffers[index]

        var left: Float //avoid allocation inside loop
        var right: Float
        var top: Float
        var bottom: Float


        var j = 0
        while (j < buffer.buffer.size * mAnimator.phaseX) {

            val entry = dataSet.getEntryForIndex(j / 4)
            entry.data?.let {
                if (it is ComparisonData) {
                    if (it.shouldDisplayIcon) {
                        it.icon?.let {

                            val starWidth = it.width
                            val starOffset = starWidth / 2
                            val topOffset = (it.height / 1.35).toFloat()

                            left = buffer.buffer[j]
                            right = buffer.buffer[j + 2]
                            top = buffer.buffer[j + 1]
                            bottom = buffer.buffer[j + 3]

                            val x = (left + right)
                            val y = (top + bottom) / 2

                            if (!mViewPortHandler.isInBoundsRight(x)) return

                            if (!mViewPortHandler.isInBoundsY(top) || !mViewPortHandler.isInBoundsLeft(x)) {
                                //do nothing
                            } else {
                                drawImage(c, it, x - (starOffset / 1.5f), y - topOffset)
                                //drawImage(c, scaledBarImage, (x - (starOffset/1.5f))+scaledBarImage.width, top-topOffset)
                            }
                        }

                    }

                }
            }
            j += 4


        }

    }


    protected fun drawImage(c: Canvas, image: Bitmap?, x: Float, y: Float) {
        if (image != null) {
            c.drawBitmap(image, x, y, null)
        }
    }

    override fun drawDataSet(c: Canvas, dataSet: IBarDataSet, index: Int) {

        val trans = mChart.getTransformer(dataSet.axisDependency)

        mShadowPaint.color = dataSet.barShadowColor

        val phaseX = mAnimator.phaseX
        val phaseY = mAnimator.phaseY


        // initialize the buffer
        val buffer = mBarBuffers[index]
        buffer.setPhases(phaseX, phaseY)
        buffer.setDataSet(index)
        buffer.setBarWidth(mChart.barData.barWidth)
        buffer.setInverted(mChart.isInverted(dataSet.axisDependency))

        buffer.feed(dataSet)

        trans.pointValuesToPixel(buffer.buffer)

        // if multiple colors
        if (dataSet.colors.size > 1) {

            var j = 0
            while (j < buffer.size()) {

                if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2])) {
                    j += 4
                    continue
                }

                if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j]))
                    break

                if (mChart.isDrawBarShadowEnabled) {
                    if (mRadius > 0)
                        c.drawRoundRect(RectF(buffer.buffer[j], mViewPortHandler.contentTop(),
                                buffer.buffer[j + 2],
                                mViewPortHandler.contentBottom()), mRadius, mRadius, mShadowPaint)
                    else
                        c.drawRect(buffer.buffer[j], mViewPortHandler.contentTop(),
                                buffer.buffer[j + 2],
                                mViewPortHandler.contentBottom(), mShadowPaint)
                }

                // Set the color for the currently drawn value. If the index
                // is
                // out of bounds, reuse colors.

                mRenderPaint.color = dataSet.getColor(j / 4)
                if (mRadius > 0)
                    c.drawRoundRect(RectF(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                            buffer.buffer[j + 3]), mRadius, mRadius, mRenderPaint)
                else
                    c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                            buffer.buffer[j + 3], mRenderPaint)
                j += 4
            }
        } else {

            mRenderPaint.color = dataSet.color

            var j = 0
            while (j < buffer.size()) {

                if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2])) {
                    j += 4
                    continue
                }

                if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j]))
                    break

                if (mChart.isDrawBarShadowEnabled) {
                    if (mRadius > 0)
                        c.drawRoundRect(RectF(buffer.buffer[j], mViewPortHandler.contentTop(),
                                buffer.buffer[j + 2],
                                mViewPortHandler.contentBottom()), mRadius, mRadius, mShadowPaint)
                    else
                        c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                                buffer.buffer[j + 3], mRenderPaint)
                }

                if (mRadius > 0) {

                    val radius = (buffer.buffer[j + 3] - buffer.buffer[j + 1]) / 2
                    val path = RoundedRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2], buffer.buffer[j + 3], radius, radius,
                            false, true, true, false)
                    c.drawPath(path, mRenderPaint)


                } else
                    c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                            buffer.buffer[j + 3], mRenderPaint)
                j += 4
            }
        }

        drawBarImages(c, dataSet, index)
    }

    companion object {


        fun RoundedRect(
                left: Float, top: Float, right: Float, bottom: Float, rx: Float, ry: Float,
                tl: Boolean, tr: Boolean, br: Boolean, bl: Boolean
        ): Path {
            var rx = rx
            var ry = ry
            val path = Path()
            if (rx < 0) rx = 0f
            if (ry < 0) ry = 0f
            val width = right - left
            val height = bottom - top
            if (rx > width / 2) rx = width / 2
            if (ry > height / 2) ry = height / 2
            val widthMinusCorners = width - 2 * rx
            val heightMinusCorners = height - 2 * ry

            path.moveTo(right, top + ry)
            if (tr)
                path.rQuadTo(0f, -ry, -rx, -ry)//top-right corner
            else {
                path.rLineTo(0f, -ry)
                path.rLineTo(-rx, 0f)
            }
            path.rLineTo(-widthMinusCorners, 0f)
            if (tl)
                path.rQuadTo(-rx, 0f, -rx, ry) //top-left corner
            else {
                path.rLineTo(-rx, 0f)
                path.rLineTo(0f, ry)
            }
            path.rLineTo(0f, heightMinusCorners)

            if (bl)
                path.rQuadTo(0f, ry, rx, ry)//bottom-left corner
            else {
                path.rLineTo(0f, ry)
                path.rLineTo(rx, 0f)
            }

            path.rLineTo(widthMinusCorners, 0f)
            if (br)
                path.rQuadTo(rx, 0f, rx, -ry) //bottom-right corner
            else {
                path.rLineTo(rx, 0f)
                path.rLineTo(0f, -ry)
            }

            path.rLineTo(0f, -heightMinusCorners)

            path.close()//Given close, last lineto can be removed.

            return path
        }
    }


    override fun drawValue(c: Canvas, valueText: String, x: Float, y: Float, color: Int) {
        mValuePaint.color = color
        var lines=valueText.lines()
        var i=y

        val bounds = Rect()
         mValuePaint.getTextBounds(valueText, 0, valueText.length, bounds)


        lines.forEach {
            c.drawText(it, x, i, mValuePaint)
            i=i+bounds.height()+15
        }


    }





}