package greenely.greenely.feed.utils.charting

import android.graphics.Canvas
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.renderer.YAxisRenderer
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.ViewPortHandler

class CustomYAxisRenderer(viewPortHandler: ViewPortHandler, yAxis: YAxis, trans: Transformer) : YAxisRenderer(viewPortHandler, yAxis, trans) {

    override fun renderGridLines(c: Canvas?) {
        if (!mYAxis.isEnabled)
            return

        if (mYAxis.isDrawGridLinesEnabled) {

            val clipRestoreCount = c?.save()
            c?.clipRect(gridClippingRect)

            val positions = transformedPositions

            mGridPaint.color = mYAxis.gridColor
            mGridPaint.strokeWidth = mYAxis.gridLineWidth
            mGridPaint.pathEffect = mYAxis.gridDashPathEffect

            val gridLinePath = mRenderGridLinesPath
            gridLinePath.reset()

            // draw the grid
            var i = 0
            while (i < positions.size) {

                // draw a path because lines don't support dashing on lower android versions
                if(i>0)c?.drawPath(linePath(gridLinePath, i, positions), mGridPaint)
                gridLinePath.reset()
                i += 2
            }

            clipRestoreCount?.let {
                c?.restoreToCount(it)
            }
        }

        if (mYAxis.isDrawZeroLineEnabled) {
            drawZeroLine(c)
        }
    }

}