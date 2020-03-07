package greenely.greenely.retail.models

import com.squareup.moshi.Json

data class InvoiceJson(
        val month: String,
        val state: String,
        @field:Json(name = "is_paid") val isPaid: Boolean,
        @field:Json(name = "pdf_url") val pdfUrl: String,
        val cost: Int
)
