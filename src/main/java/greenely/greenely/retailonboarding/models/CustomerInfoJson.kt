package greenely.greenely.retailonboarding.models

import com.squareup.moshi.Json

data class CustomerInfoJson(
        @field:Json(name = "personal_number") val personal_number: String,
        @field:Json(name = "address") val address: String,
        @field:Json(name = "zip_code") val zip_code: String,
        @field:Json(name = "city") val city: String,
        @field:Json(name = "invoice_email") val invoice_email: String,
        @field:Json(name = "cell_phone") val cell_phone: String,
        @field:Json(name = "promocode") val promocode: String?,
        @field:Json(name = "poa_process_required") val poaProcessRequired: Boolean


)
