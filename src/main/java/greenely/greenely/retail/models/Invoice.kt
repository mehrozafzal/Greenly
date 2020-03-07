package greenely.greenely.retail.models

import com.squareup.moshi.Json

data class Invoice(
        val month: String,
        val state: String,
        @field:Json(name = "is_paid") val isPaid: Boolean,
        @field:Json(name = "pdf_url") val pdfUrl: String,
        @field:Json(name = "cost") val cost: String
)
