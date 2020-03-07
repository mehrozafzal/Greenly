package greenely.greenely.feed.models.json

import com.squareup.moshi.Json
import org.joda.time.DateTime

data class DataPointJson(
        @field:Json(name = "timestamp") val timestamp: Long,
        @field:Json(name = "usage") val usage: Int?,
        @field:Json(name = "temperature") val temperature: Int?,
        @field:Json(name = "nord_pool_spot_price") val poolSpotPrice: Int?

) {
    private val date
        get() = DateTime(timestamp * 1000L)

    fun asPair(): Pair<DateTime, Int> = date to (usage ?: 0)


}
