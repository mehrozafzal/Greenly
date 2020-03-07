package greenely.greenely.feed.models

import com.squareup.moshi.Json
import org.joda.time.DateTime

class MonthlyDataPointJson(@field:Json(name = "timestamp") val timestamp: Long,
                           @field:Json(name = "usage") val usage: Int?,
                           @field:Json(name = "temperature") val temperature: Int?,
                           @field:Json(name = "nord_pool_spot_price") val poolSpotPrice: Int?

) {
    private val date
        get() = DateTime(timestamp * 1000L)


}