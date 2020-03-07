package greenely.greenely.feed.models.json

import com.squareup.moshi.Json

data class TipJson(
        @field:Json(name = "created") val timestamp: Long,
        @field:Json(name = "title") val title: String,
        @field:Json(name = "text") val body: String,
        @field:Json(name = "new_entry") var isNewEntry: Boolean
)

