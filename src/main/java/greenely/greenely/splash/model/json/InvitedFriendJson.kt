package greenely.greenely.splash.model.json

import com.squareup.moshi.Json

data class InvitedFriendJson(
        @field:Json(name = "friend_alias")
        val friend_alias: String? = null
)
