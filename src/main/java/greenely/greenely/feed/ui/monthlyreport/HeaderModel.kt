package greenely.greenely.feed.ui.monthlyreport

import android.content.Context
import greenely.greenely.R
import greenely.greenely.feed.models.ChartData
import greenely.greenely.utils.consumptionToString
import greenely.greenely.utils.getCurrencyFormat
import greenely.greenely.views.ConsumptionForTimeRangeHeader
import org.joda.time.format.DateTimeFormat
import java.util.*
import kotlin.math.ceil
import kotlin.math.round

class HeaderModel(
        private val context: Context,
        private val chartData: ChartData
) : ConsumptionForTimeRangeHeader.Model {
    companion object {
        private val dateFormat = DateTimeFormat.forPattern("MMMM").withLocale(Locale("sv", "SE"))
    }

    override val timeRangeUnit: String
        get() = context.getString(R.string.month)
    override val timeRange: String
        get() = dateFormat.print(chartData.date).capitalize()
    override val consumption: String
        get() = round(chartData.sum / 1000.0f).getCurrencyFormat()
}

