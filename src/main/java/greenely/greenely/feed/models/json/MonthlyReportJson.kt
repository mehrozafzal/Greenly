package greenely.greenely.feed.models.json

import com.squareup.moshi.Json
import greenely.greenely.feed.models.MonthlyDataPointJson

data class MonthlyReportJson(
        @field:Json(name = "created") val timestamp: Long,
        @field:Json(name = "title") val title: String,
        @field:Json(name = "text") val body: String,
        @field:Json(name = "new_entry") var isNewEntry: Boolean,
        @field:Json(name = "chart_data") val chartData: List<MonthlyDataPointJson>?
)
