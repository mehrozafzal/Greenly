package greenely.greenely.signature.data.models

import com.squareup.moshi.Json

data class ValidationResponse(
        @field:Json(name = "sv_meter") val meterIdError: String?,
        @field:Json(name = "sv_social") val pNoError: String?
)