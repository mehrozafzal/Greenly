package greenely.greenely.profile.json

import com.squareup.moshi.Json

data class SaveProfileRequest(

        @field:Json(name = "last_name")
        var lastName: String? = null,

        @field:Json(name = "avatar")
        var avatar: String? = null,

        @field:Json(name = "first_name")
        var firstName: String? = null
)