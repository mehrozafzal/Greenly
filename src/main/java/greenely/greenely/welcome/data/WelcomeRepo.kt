package greenely.greenely.welcome.data

import greenely.greenely.OpenClassOnDebug
import greenely.greenely.api.GreenelyApi
import greenely.greenely.models.AuthenticationInfo
import greenely.greenely.welcome.model.json.InvitedFriendJson
import greenely.greenely.store.UserStore
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@OpenClassOnDebug
@Singleton
class WelcomeRepo @Inject constructor(
        private val api: GreenelyApi,
        private val userStore: UserStore
) {
    fun invitedFriendResponse(invitationID: String): Observable<InvitedFriendJson> =
            api.getInvitedFriend(invitationID)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext { userStore.token = it.jwt }
                    .map { it.data }

    fun checkFirstOpenFlag(): Boolean {
        return userStore.firstOpenFlag ?: false
    }

    fun setOpenedFlag() {
        userStore.firstOpenFlag = true
    }

    fun checkAuth(): Observable<AuthenticationInfo> =
            api.checkAuth("JWT ${userStore.token}")
                    .subscribeOn(Schedulers.io())
                    .doOnNext { userStore.token = it.jwt }
                    .map { it.data }
}

