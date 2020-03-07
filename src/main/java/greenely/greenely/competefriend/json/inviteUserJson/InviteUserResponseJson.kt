package greenely.greenely.competefriend.json.inviteUserJson

import com.squareup.moshi.Json

data class InviteUserResponseJson(

        @field:Json(name = "expires_at")
        val expiresAt: Int? = null,

        @field:Json(name = "invitation_link")
        val invitationLink: String? = null,

        @field:Json(name = "created_at")
        val createdAt: Int? = null,

        @field:Json(name = "id")
        val id: String? = null
)