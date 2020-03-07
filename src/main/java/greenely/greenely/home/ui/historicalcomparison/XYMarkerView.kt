package greenely.greenely.home.ui.historicalcomparison

import android.graphics.Bitmap
import android.graphics.Canvas
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.utils.ViewPortHandler
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.renderer.LineChartRenderer


//class XYMarkerView(private val lineChart: LineChart, animator: ChartAnimator,
//                   viewPortHandler: ViewPortHandler, private val image: Bitmap)
//    : LineChartRenderer(lineChart, animator, viewPortHandler) {
//
//    override fun drawExtras(c: Canvas) {
//        super.drawExtras(c)
//
//        val highlighted = lineChart.highlighted ?: return
//
//        val phaseY = mAnimator.phaseY
//
//        val imageBuffer = FloatArray(2)
//        imageBuffer[0] = 0f
//        imageBuffer[1] = 0f
//        val lineData = mChart.lineData
//        val dataSets = mChart.lineData.dataSets
//
////        val scaledBitmaps = arrayOfNulls<Bitmap>(dataSets.size)
////        val scaledBitmapOffsets = FloatArray(dataSets.size)
////        for (i in dataSets.indices) {
////            val imageSize = dataSets[i].circleRadius * 10
////            scaledBitmapOffsets[i] = imageSize / 2f
////            scaledBitmaps[i] = scaleImage(imageSize.toInt())
////        }
//
//        for (high in highlighted) {
//            val dataSetIndex = high.dataSetIndex
//            val set = lineData.getDataSetByIndex(dataSetIndex)
//            val trans = lineChart.getTransformer(set!!.axisDependency)
//
//            if (set == null || !set.isHighlightEnabled)
//                continue
//
//            val e = set.getEntryForXValue(high.x, high.y)
//
//            if (!isInBoundsX(e, set))
//                continue
//
//            imageBuffer[0] = e.x
//            imageBuffer[1] = e.y * phaseY
//            trans.pointValuesToPixel(imageBuffer)
//
//            c.drawBitmap(scaledBitmaps[dataSetIndex],
//                    imageBuffer[0] - scaledBitmapOffsets[dataSetIndex],
//                    imageBuffer[1] - scaledBitmapOffsets[dataSetIndex],
//                    mRenderPaint)
//        }
//    }
//
////    private fun scaleImage(radius: Int): Bitmap {
////        return Bitmap.createScaledBitmap(image, radius, radius, false)
////    }
//}