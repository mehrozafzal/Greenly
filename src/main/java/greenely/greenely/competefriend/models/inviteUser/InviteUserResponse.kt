package greenely.greenely.competefriend.models.inviteUser

import javax.annotation.Generated
import com.google.gson.annotations.SerializedName

data class InviteUserResponse(

	@field:SerializedName("expires_at")
	val expiresAt: Int? = null,

	@field:SerializedName("invitation_link")
	val invitationLink: String? = null,

	@field:SerializedName("created_at")
	val createdAt: Int? = null,

	@field:SerializedName("id")
	val id: String? = null
)