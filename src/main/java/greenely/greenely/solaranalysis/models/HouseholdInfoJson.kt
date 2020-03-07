package greenely.greenely.solaranalysis.models

import com.squareup.moshi.Json

data class HouseholdInfoJson(
        @field:Json(name = "address") val address: String,
        @field:Json(name = "roof_angle") val roofAngle: Int,
        @field:Json(name = "direction") val direction: Int,
        @field:Json(name = "area") val roofArea: Int
)

