package greenely.greenely.retail.models

import com.squareup.moshi.Json

data class PricingPointsJson(
        @field:Json(name = "price") val price: Int?,
        @field:Json(name = "timestamp") val timestamp: String
)
