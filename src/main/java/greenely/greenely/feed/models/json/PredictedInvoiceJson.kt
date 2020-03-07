package greenely.greenely.feed.models.json


import com.squareup.moshi.Json


data class PredictedInvoiceJson(
        @field:Json(name = "context_date")
        val contextDate: String,
        @field:Json(name = "created")
        val created: Int,
        @field:Json(name = "extra")
        val extra: Extra,
        @field:Json(name = "new_entry")
        val newEntry: Boolean,
        @field:Json(name = "text")
        val text: String,
        @field:Json(name = "title")
        val title: String
) {
    data class Extra(
            @field:Json(name = "prediction_in_kr")
            val predictionInKr: Float,
            @field:Json(name = "subtext")
            val subtext: String
    )
}
