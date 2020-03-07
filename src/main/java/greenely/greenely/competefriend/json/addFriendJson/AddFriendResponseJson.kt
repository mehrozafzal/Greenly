package greenely.greenely.competefriend.json.addFriendJson


import com.squareup.moshi.Json

data class AddFriendResponseJson(

        @field:Json(name = "created_at")
        val createdAt: Int? = null,

        @field:Json(name = "friend_alias")

        val friendAlias: String? = null,

        @field:Json(name = "id")
        val id: String? = null
)