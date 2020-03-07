package greenely.greenely.retail.models

import com.squareup.moshi.Json

data class CurrentMonthPointsJson(
        @field:Json(name = "cost_in_kr") val costInKr: Int?,
        @field:Json(name = "timestamp") val timestamp: String
)
