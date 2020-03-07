package greenely.greenely.signature.data.models

import com.squareup.moshi.Json
import greenely.greenely.OpenClassOnDebug

@OpenClassOnDebug
data class InputValidationModel(
        @field:Json(name = "validations") var List: ValidationsList
)

@OpenClassOnDebug
data class ValidationsList(
        @field:Json(name = "sv_social_number") var pNo: String?
)

