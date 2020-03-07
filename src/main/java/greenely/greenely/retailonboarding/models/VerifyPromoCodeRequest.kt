package greenely.greenely.retailonboarding.models

import com.squareup.moshi.Json

data class VerifyPromoCodeRequest (@field:Json(name = "promocode") val promocode: String?)