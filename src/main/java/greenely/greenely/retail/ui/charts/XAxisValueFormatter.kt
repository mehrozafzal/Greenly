package greenely.greenely.retail.ui.charts

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class XAxisValueFormatter : IAxisValueFormatter {

    override fun getFormattedValue(value: Float, axis: AxisBase): String {

        val formattedDate = value.toBigDecimal().toPlainString()

        return getDateTime(formattedDate)
    }

    private fun getDateTime(labelTimestamp: String): String {
        return try {

            val calendar = GregorianCalendar()
            calendar.timeInMillis = labelTimestamp.toLong() * 1000

            val rawDate = calendar.time

            val roundedDate = toNearestWholeHour(rawDate)

            val prettyFormat = SimpleDateFormat("HH:mm")
            prettyFormat.timeZone = TimeZone.getTimeZone("Europe/Stockholm")
            prettyFormat.format(roundedDate)

        } catch (e: Exception) {
            e.toString()
        }
    }

    fun toNearestWholeHour(d: Date): Date {
        val calendar = GregorianCalendar()
        calendar.time = d

        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        return calendar.time
    }
}