package greenely.greenely.profile.json

import com.squareup.moshi.Json

data class SaveProfileResponse(

        @field:Json(name = "avatar_url")
        val avatarUrl: String? = null,

        @field:Json(name = "avatar_filetype")
        val avatarFiletype: String? = null,

        @field:Json(name = "last_name")
        val lastName: String? = null,

        @field:Json(name = "first_name")
        val firstName: String? = null
)