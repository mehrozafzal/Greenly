package greenely.greenely.retail.models

import com.squareup.moshi.Json

data class RetailBankIdProcessJson(
        @field:Json(name = "bankid_status") val bankIdStatus: String,
        @field:Json(name = "bankid_message") val bankIdMessage: String,
        @field:Json(name = "bankid_title") val bankIdTitle: String

)
