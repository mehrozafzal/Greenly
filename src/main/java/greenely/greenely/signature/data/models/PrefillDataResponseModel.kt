package greenely.greenely.signature.data.models

import com.squareup.moshi.Json

data class PrefillDataResponseModel(
        @field:Json(name = "first_name") val firstName: String,
        @field:Json(name = "last_name") val lastName: String,
        @field:Json(name = "address") val adress: String,
        @field:Json(name = "city") val postalRegion: String,
        @field:Json(name = "zip_code") val postalCode: String
)

