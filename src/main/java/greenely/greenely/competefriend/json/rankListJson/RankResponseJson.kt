package greenely.greenely.competefriend.json.rankListJson

import com.squareup.moshi.Json

data class RankResponseJson(

        @field:Json(name = "title")
        val title: String? = null,

        @field:Json(name = "resolution")
        val resolution: String? = null,

        @field:Json(name = "friends")
        val friends: List<FriendsItemJson?>? = null
)