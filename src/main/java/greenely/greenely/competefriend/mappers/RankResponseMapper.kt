package greenely.greenely.competefriend.mappers

import greenely.greenely.competefriend.json.rankListJson.RankResponseJson
import greenely.greenely.competefriend.models.rankList.RankResponse
import javax.inject.Inject

class RankResponseMapper @Inject constructor(private val friendsItemMapper: FriendsItemMapper) {
    fun fromRankListJson(json: RankResponseJson?): RankResponse {
        return RankResponse(
                title = json?.title,
                resolution = json?.resolution,
                friends = json?.friends?.map {
                    friendsItemMapper.fromFriendsItemJson(it)
                }
        )
    }
}






