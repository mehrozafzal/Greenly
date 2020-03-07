package greenely.greenely.retailonboarding.models

import com.squareup.moshi.Json

data class VerifyPromoCodeResponse(@field:Json(name = "promocode") val promocode: String?,
                                   @field:Json(name = "is_valid") val isValid: Boolean)