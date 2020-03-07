package greenely.greenely.feed.utils.charting

import android.content.Context
import android.graphics.Typeface
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import greenely.greenely.R


fun YAxis.setYAxisStyle(context: Context,
                        labelCount: Int,
                        yAxisFormatter: IAxisValueFormatter) {

    this.textColor = ContextCompat.getColor(context, R.color.grey10)
    this.textSize = 8f
    this.typeface = ResourcesCompat.getFont(context, R.font.gt_america_medium)
    setLabelCount(labelCount,true)
    this.valueFormatter = yAxisFormatter
    this.axisMinimum = 0f

}

fun XAxis.setXAxisStyle(context: Context,
                        labelCount: Int,
                        XAxisFormatter: IAxisValueFormatter) {

    this.textColor = ContextCompat.getColor(context, R.color.grey10)
    this.textSize = 8f
//    this.labelCount = labelCount
    this.typeface = ResourcesCompat.getFont(context, R.font.gt_america_medium)
    this.position = XAxis.XAxisPosition.BOTTOM
    this.valueFormatter = XAxisFormatter
    this.setDrawGridLines(false)
    this.isEnabled = true
    this.setAxisMinimum(-0.5f);

}




