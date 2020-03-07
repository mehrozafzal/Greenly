package greenely.greenely.home.models

import com.google.gson.annotations.SerializedName

data class FacilitiesResponse(

        @field:SerializedName("city")
        val city: String? = null,

        @field:SerializedName("street")
        val street: String? = null,

        @field:SerializedName("last_name")
        val lastName: String? = null,

        @field:SerializedName("id")
        val id: Int? = null,

        @field:SerializedName("state")
        val state: String? = null,

        @field:SerializedName("meter_id")
        val meterId: String? = null,

        @field:SerializedName("first_name")
        val firstName: String? = null,

        @field:SerializedName("parameters")
        val parameters: Parameters? = null,

        @field:SerializedName("zip_code")
        val zipCode: String? = null,

        @field:SerializedName("status")
        val status: String? = null
)