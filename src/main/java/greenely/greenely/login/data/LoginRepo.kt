package greenely.greenely.login.data

import greenely.greenely.OpenClassOnDebug
import greenely.greenely.api.GreenelyApi
import greenely.greenely.login.model.LoginData
import greenely.greenely.models.AuthenticationInfo
import greenely.greenely.store.UserStore
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Implementation of a [LoginRepo] using the api as the model layer.
 */
@OpenClassOnDebug
@Singleton
class LoginRepo @Inject constructor(
        private val api: GreenelyApi,
        private val userStore: UserStore
) {

    fun login(loginData: LoginData): Observable<AuthenticationInfo> =
            api.login(loginData)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .doOnNext { userStore.token = it.jwt }
                    .map { it.data }
                    .doOnNext { userStore.userId = it.userId }
}

