package greenely.greenely.history.models

import android.content.Context
import greenely.greenely.R
import greenely.greenely.history.HistoryResolution
import greenely.greenely.history.NavigationDataPoint
import greenely.greenely.utils.consumptionToString
import greenely.greenely.utils.formatDate
import greenely.greenely.views.ConsumptionForTimeRangeHeader
import org.joda.time.format.DateTimeFormat
import java.util.*

class HeaderModel private constructor(
        private val context: Context,
        private val resolution: HistoryResolution,
        private val dataPoint: NavigationDataPoint ) : ConsumptionForTimeRangeHeader.Model {

    companion object {
        @JvmStatic
        fun forResolutionWithDataPoint(
                context: Context,
                resolution: HistoryResolution,
                dataPoint: NavigationDataPoint
        ): HeaderModel = HeaderModel(context, resolution, dataPoint)
    }

    override val timeRangeUnit: String
        get() = when (resolution) {
            is HistoryResolution.Month -> context.getString(R.string.day)
            is HistoryResolution.Year -> context.getString(R.string.month)
        }
    override val timeRange: String
        get() = if (resolution is HistoryResolution.Year) {
            val dateFormatter = DateTimeFormat
                    .forPattern("MMM yyyy")
                    .withLocale(Locale("sv", "SE"))
            dateFormatter.print(dataPoint.dateTime).formatDate()
        } else {
            val dateFormatter = DateTimeFormat
                    .forPattern("dd MMM yyyy")
                    .withLocale(Locale("sv", "SE"))
            dateFormatter.print(dataPoint.dateTime).split(" ").joinToString(" ") { it.formatDate() }
        }



     val consumptionHeaderText: String
        get() = context.getString(R.string.consumption_header_text).format(timeRange.toLowerCase())

    val temperatureHeaderText: String
        get() = context.getString(R.string.temperature_header_text).format(timeRange.toLowerCase())

    val priceHeaderText: String
        get() = context.getString(R.string.price_header_text).format(timeRange.toLowerCase())


    override val consumption: String
        get() = context.getString(R.string.x_kwh).format(dataPoint.usage?.consumptionToString() ?: "--")
}
