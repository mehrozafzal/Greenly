package greenely.greenely.feed.models

import com.squareup.moshi.Json

data class FeedCountResponse(@field:Json(name = "count") val count: Int)

