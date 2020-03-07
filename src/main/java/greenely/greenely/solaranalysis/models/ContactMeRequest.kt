package greenely.greenely.solaranalysis.models

import com.squareup.moshi.Json

data class ContactMeRequest(
        @field:Json(name = "name") val name: String,
        @field:Json(name = "email") val email: String,
        @field:Json(name = "phone_number") val phoneNumber: String,
        @field:Json(name = "id") val id: String,
        @field:Json(name = "quality_lead") val qualityLead: Boolean
)


