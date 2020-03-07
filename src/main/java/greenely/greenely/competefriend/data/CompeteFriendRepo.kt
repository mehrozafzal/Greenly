package greenely.greenely.competefriend.data

import greenely.greenely.OpenClassOnDebug
import greenely.greenely.api.GreenelyApi
import greenely.greenely.competefriend.json.addFriendJson.AddFriendResponseJson
import greenely.greenely.competefriend.json.inviteUserJson.InviteUserResponseJson
import greenely.greenely.competefriend.json.rankListJson.RankResponseJson
import greenely.greenely.competefriend.models.addFriend.AddFriendRequest
import greenely.greenely.competefriend.models.addFriend.AddFriendResponse
import greenely.greenely.forgotpassword.ForgotPasswordRequest
import greenely.greenely.store.UserStore
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@OpenClassOnDebug
class CompeteFriendRepo @Inject constructor(
        private val api: GreenelyApi,
        private val userStore: UserStore) {

    fun getRankedUserListResponse(resolution: String, rankingMethod: String): Observable<RankResponseJson> =
            api.getRankedUserList("JWT ${userStore.token}", resolution, rankingMethod)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext { userStore.token = it.jwt }
                    .map {
                        it.data
                    }

    fun getInviteUserResponse(): Observable<InviteUserResponseJson> =
            api.getInviteUser("JWT ${userStore.token}")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext { userStore.token = it.jwt }
                    .map {
                        it.data
                    }

    fun getAddFriendToListResponse(invitationID: String): Observable<AddFriendResponseJson> =
            api.addFriendToList("JWT ${userStore.token}", invitationID)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext { userStore.token = it.jwt }
                    .map {
                        it.data
                    }

    fun deleteFriend(friendID: String): Completable =
            api.deleteFriend("JWT ${userStore.token}", friendID)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
}