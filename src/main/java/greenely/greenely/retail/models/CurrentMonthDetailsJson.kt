package greenely.greenely.retail.models

import com.squareup.moshi.Json

data class CurrentMonthDetailsJson(
        @field:Json(name = "title") val title: String,
        @field:Json(name = "sub_title") val subTitle: String,
        @field:Json(name = "value") val cost: Int?,
        @field:Json(name = "points") val points: List<CurrentMonthPointsJson>
)
