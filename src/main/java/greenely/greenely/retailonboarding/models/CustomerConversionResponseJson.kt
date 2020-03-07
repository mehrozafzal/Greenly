package greenely.greenely.retailonboarding.models

import com.squareup.moshi.Json

data class CustomerConversionResponseJson(
        @field:Json(name = "bankid_order_ref") val bankidOrderRef: String?,
        @field:Json(name = "bankid_start_token") val bankidStartToken: String?
)