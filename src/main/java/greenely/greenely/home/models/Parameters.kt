package greenely.greenely.home.models

import com.google.gson.annotations.SerializedName


data class Parameters(

	@field:SerializedName("secondary_heating_type")
	val secondaryHeatingType: Any? = null,

	@field:SerializedName("construction_year")
	val constructionYear: String? = null,

	@field:SerializedName("tertiary_heating_type")
	val tertiaryHeatingType: Any? = null,

	@field:SerializedName("municipality")
	val municipality: String? = null,

	@field:SerializedName("quaternary_heating_type")
	val quaternaryHeatingType: Any? = null,

	@field:SerializedName("electric_car_count")
	val electricCarCount: String? = null,

	@field:SerializedName("facility_type")
	val facilityType: String? = null,

	@field:SerializedName("facility_area")
	val facilityArea: String? = null,

	@field:SerializedName("occupants")
	val occupants: String? = null,

	@field:SerializedName("heating_type")
	val heatingType: String? = null
)