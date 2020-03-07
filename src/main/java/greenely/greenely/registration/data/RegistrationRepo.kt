package greenely.greenely.registration.data

import greenely.greenely.OpenClassOnDebug
import greenely.greenely.api.GreenelyApi
import greenely.greenely.models.AuthenticationInfo
import greenely.greenely.registration.data.models.RegistrationRequest
import greenely.greenely.store.UserStore
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton


@OpenClassOnDebug
@Singleton
class RegistrationRepo @Inject constructor(
        private val api: GreenelyApi,
        private val userStore: UserStore
) {

    fun register(registrationRequest: RegistrationRequest, invitationID: String?): Observable<AuthenticationInfo> =
            api.register(registrationRequest, invitationID)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext { userStore.token = it.jwt }
                    .map { it.data }
                    .doOnNext {
                        userStore.userId = it.userId
                    }
}