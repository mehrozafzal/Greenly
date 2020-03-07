package greenely.greenely.feed.ui.weeklyreport

import android.content.Context
import greenely.greenely.R
import greenely.greenely.feed.models.ChartData
import greenely.greenely.utils.consumptionToString
import greenely.greenely.views.ConsumptionForTimeRangeHeader
import org.joda.time.format.DateTimeFormat
import java.util.*

class HeaderModel(
        private val context: Context,
        private val chartData: ChartData
) : ConsumptionForTimeRangeHeader.Model {
    companion object {
        private val dateFormat = DateTimeFormat.forPattern("d MMM").withLocale(Locale("sv", "SE"))
    }

    override val timeRangeUnit: String
        get() {
            val weekOfYear = chartData.date.weekOfWeekyear.toString()
            return context
                    .getString(R.string.week_x)
                    .format(weekOfYear)
        }
    override val timeRange: String
        get() {
            val startDate = chartData
                    .firstOrNull()
                    ?.date
                    ?.let { dateFormat.print(it) }
                    ?.replace(".", "") ?: "??"

            val endDate = chartData
                    .lastOrNull()
                    ?.date
                    ?.let { dateFormat.print(it) }
                    ?.replace(".", "") ?: "??"

            return "$startDate - $endDate"
        }
    override val consumption: String
        get() = context
                .getString(R.string.x_kwh)
                .format((chartData.sum / 1000.0f).consumptionToString())
}
