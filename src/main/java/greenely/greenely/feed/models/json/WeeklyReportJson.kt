package greenely.greenely.feed.models.json

import com.squareup.moshi.Json

data class WeeklyReportJson(
        @field:Json(name = "week_chart_data") var currentPeriodChartData: List<DataPointJson>?,
        @field:Json(name = "created") val timestamp: Long,
        @field:Json(name = "title") val title: String,
        @field:Json(name = "text") val body: String,
        @field:Json(name = "new_entry") var isNewEntry: Boolean,
        @field:Json(name = "previous_period_total") val previousPeriodTotal: Float?,
        @field:Json(name = "current_period_total") val currentPeriodChartTotal: Float,
        @field:Json(name = "previous_period_chart_data") val previousPeriodChartData: List<DataPointJson>?
)

