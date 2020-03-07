package greenely.greenely.competefriend.models.addFriend

import javax.annotation.Generated
import com.google.gson.annotations.SerializedName

data class AddFriendResponse(

	@field:SerializedName("created_at")
	val createdAt: Int? = null,

	@field:SerializedName("friend_alias")
	val friendAlias: String? = null,

	@field:SerializedName("id")
	val id: String? = null
)