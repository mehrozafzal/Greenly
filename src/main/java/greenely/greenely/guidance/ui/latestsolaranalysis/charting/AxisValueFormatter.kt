package greenely.greenely.guidance.ui.latestsolaranalysis.charting

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import greenely.greenely.OpenClassOnDebug
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import javax.inject.Inject

@OpenClassOnDebug
class AxisValueFormatter @Inject constructor() : IAxisValueFormatter {
    companion object {
        val dateFormatter: DateTimeFormatter = DateTimeFormat.forPattern("MMMM")
    }

    override fun getFormattedValue(value: Float, axis: AxisBase?): String {
        return dateFormatter
                .print(DateTime.now().withMonthOfYear((value.toInt() % 12) + 1))
                .capitalize()
                .first()
                .toString()
    }
}
