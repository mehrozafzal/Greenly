package greenely.greenely.competefriend.json.rankListJson

import javax.annotation.Generated
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

data class FriendsItemJson(

        @field:Json(name = "avatar_color")
        val avatarColor: String? = null,

        @field:Json(name = "avatar_url")
        val avatarUrl: String? = null,

        @field:Json(name = "friend_id")
        val friendId: Any? = null,

        @field:Json(name = "friend_alias")
        val friendAlias: String? = null,

        @field:Json(name = "facility_state")
        val facilityState: String? = null,

        @field:Json(name = "ranking")
        val ranking: Int? = null,

        @field:Json(name = "message")
        val message: String? = null,

        @field:Json(name = "measurement")
        val measurement: Any? = null
)