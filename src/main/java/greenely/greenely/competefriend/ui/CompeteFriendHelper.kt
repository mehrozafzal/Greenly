package greenely.greenely.competefriend.ui

import greenely.greenely.competefriend.json.rankListJson.FriendsItemJson

interface CompeteFriendHelper {

    fun onShareBtnClicked()
    fun onItemClicked(listPosition: Int, friendsItemJson: FriendsItemJson)
    fun onSignPoaClicked()
}