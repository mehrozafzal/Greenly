package greenely.greenely.guidance.models.json

import com.squareup.moshi.Json

data class LatestSolarAnalysisJson(
        @field:Json(name = "total_saving") val totalSaving: Int,
        @field:Json(name = "estimated_cost_after_solar_support") val estimatedCostAfterSolarSupport: Int,
        @field:Json(name = "yearly_saving") val yearlySaving: Int,
        @field:Json(name = "yearly_production") val yearlyProduction: Float,
        @field:Json(name = "potential_saving") val potentialSaving: Int,
        @field:Json(name = "payback_time_with_solar_support") val paybackTimeWithSolarSupport: Int,
        @field:Json(name = "solar_panel_lifespan") val solarPanelLifespan: Int,
        @field:Json(name = "month_data") val monthData: List<Float>
)

data class LatestSolarAnalysisResponseJson(
        @field:Json(name = "id") val id: String,
        @field:Json(name = "version") val version: Int,
        @field:Json(name = "created") val date: String,
        @field:Json(name = "payload") val payload: LatestSolarAnalysisJson
)