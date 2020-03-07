package greenely.greenely.solaranalysis.models

import com.squareup.moshi.Json

data class AnalysisResponseJson(
        @field:Json(name = "id") val id: String,
        @field:Json(name = "version") val version: Int,
        @field:Json(name = "created") val date: String,
        @field:Json(name = "payload") val payload: AnalysisJson
)
