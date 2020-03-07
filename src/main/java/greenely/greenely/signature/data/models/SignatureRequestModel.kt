package greenely.greenely.signature.data.models

import com.squareup.moshi.Json

data class SignatureRequestModel(
        @field:Json(name = "firstname") val firstName: String,
        @field:Json(name = "lastname") val lastName: String,
        @field:Json(name = "personal_number") val personalNumber: String,
        @field:Json(name = "street") val adress: String,
        @field:Json(name = "zip_code") val postalCode: String,
        @field:Json(name = "city") val postalRegion: String,
        @field:Json(name = "cell_phone") val phoneNumber: String

)

