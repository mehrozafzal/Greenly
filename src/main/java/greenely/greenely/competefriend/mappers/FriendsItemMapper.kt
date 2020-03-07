package greenely.greenely.competefriend.mappers

import greenely.greenely.competefriend.models.rankList.FriendsItem
import greenely.greenely.competefriend.json.rankListJson.FriendsItemJson
import javax.inject.Inject

class FriendsItemMapper @Inject constructor() {

    fun fromFriendsItemJson(json: FriendsItemJson?): FriendsItem {
        return FriendsItem(
                avatarColor = json?.avatarColor,
                avatar_url = json?.avatarUrl,
                friendId = json?.friendId,
                friendAlias = json?.friendAlias,
                ranking = json?.ranking,
                message = json?.message,
                measurement = json?.measurement,
                facilityState = json?.facilityState
        )
    }
}
