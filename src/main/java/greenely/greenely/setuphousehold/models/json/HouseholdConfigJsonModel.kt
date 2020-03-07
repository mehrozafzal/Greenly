package greenely.greenely.setuphousehold.models.json

import com.squareup.moshi.Json
import greenely.greenely.setuphousehold.models.HouseholdInputOptions

data class HouseholdConfigJsonModel(
        @field:Json(name = "intro_title") val introTitle: String,
        @field:Json(name = "intro_text") val introText: String,
        @field:Json(name = "municipalities") val municipalities: List<HouseholdInputOptions>,
        @field:Json(name = "facility_types") val facilityTypes: List<HouseholdInputOptions>,
        @field:Json(name = "heating_types") val heatingTypes: List<HouseholdInputOptions>,
        @field:Json(name = "construction_years") val constructionYears: List<HouseholdInputOptions>,
        @field:Json(name = "occupants") val occupants: List<HouseholdInputOptions>,
        @field:Json(name = "facility_areas") val facilityAreas: List<HouseholdInputOptions>,
        @field:Json(name = "electric_car_counts") val electricCarCounts: List<HouseholdInputOptions>
)
