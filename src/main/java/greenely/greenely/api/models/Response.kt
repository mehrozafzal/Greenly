package greenely.greenely.api.models

import com.squareup.moshi.Json

/**
 * Standard API response.
 */
data class Response<out T>(
        @field:Json(name = "data") val data: T,
        @field:Json(name = "jwt") val jwt: String
)