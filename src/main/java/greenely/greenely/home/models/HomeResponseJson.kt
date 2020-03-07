package greenely.greenely.home.models

import com.squareup.moshi.Json
import greenely.greenely.home.data.Comparison
import greenely.greenely.home.data.DataState

data class HomeResponseJson(
        @field:Json(name = "state") val state: DataState,
        @field:Json(name = "num_friends") val num_friends: Int,
        @field:Json(name = "points") val points: List<Comparison>,
        @field:Json(name = "feedback_text") val feedback: String?,
        @field:Json(name = "comparison_info_title") val comparisonInfoTitle: String?,
        @field:Json(name = "comparison_info_text") val comparisonInfoText: String?,
        @field:Json(name = "resolution") val resolution: String?,
        @field:Json(name = "comparison_max_value") val comparisonMaxValue: Int?,
        @field:Json(name = "points_max_value") val pointsMaxValue: Int?,
        @field:Json(name = "consumption_comparison_percentage") val consumptionPercentage: Int?,
        @field:Json(name = "consumption_description") val consumptionDescription: String?,
        @field:Json(name = "consumption_difference") val consumptionDifference: Float?,
        @field:Json(name = "description") val description: String?
)