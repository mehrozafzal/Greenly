package greenely.greenely.models

import com.squareup.moshi.Json

/**
 * Data representing intercom information.
 */
data class IntercomInfo(
        @field:Json(name = "user_id") val userId: String,
        @field:Json(name = "user_hash") val userHash: String,
        @field:Json(name = "email") val email: String
)

data class IntercomProperties(
        @field:Json(name = "municipality") val municipality: String?,
        @field:Json(name = "facility_type") val facilityType: String?,
        @field:Json(name = "heating_type") val heatingType: String?,
        @field:Json(name = "secondary_heating_type") val secondaryHeatingType: String?,
        @field:Json(name = "tertiary_heating_type") val tertiaryHeatingType: String?,
        @field:Json(name = "quaternary_heating_type") val quaternaryHeatingType: String?,
        @field:Json(name = "facility_area") val facilityArea: String?,
        @field:Json(name = "occupants") val occupants: String?,
        @field:Json(name = "construction_year") val constructionYear: String?,
        @field:Json(name = "electric_car_count") val electricCarCount: String?,
        @field:Json(name = "has_meter") val hasMeter: String?
)

