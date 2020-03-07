package greenely.greenely.setuphousehold.models.json

import com.squareup.moshi.Json

data class HouseholdRequestJsonModel(
        @field:Json(name = "municipality_id") var municipalityId: Int? = null,
        @field:Json(name = "facility_type_id") var facilityTypeId: Int? = null,
        @field:Json(name = "heating_type_id") var heatingTypeId: Int? = null,
        @field:Json(name = "secondary_heating_type_id") var secondaryHeatingTypeId: Int? = null,
        @field:Json(name = "tertiary_heating_type_id") var tertiaryHeatingTypeId: Int? = null,
        @field:Json(name = "quaternary_heating_type_id") var quaternaryHeatingTypeId: Int? = null,
        @field:Json(name = "occupant_id") var occupants: Int? = null,
        @field:Json(name = "facility_area_id") var facilityAreaId: Int? = null,
        @field:Json(name = "construction_year_id") var constructionYearId: Int? = null,
        @field:Json(name = "electric_car_count_id") var electricCarCountId: Int? = null
)