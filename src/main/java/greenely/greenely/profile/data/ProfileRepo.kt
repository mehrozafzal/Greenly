package greenely.greenely.profile.data

import greenely.greenely.OpenClassOnDebug
import greenely.greenely.api.GreenelyApi
import greenely.greenely.profile.json.GetProfileResponse
import greenely.greenely.profile.json.SaveProfileRequest
import greenely.greenely.profile.json.SaveProfileResponse
import greenely.greenely.store.UserStore
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@OpenClassOnDebug
class ProfileRepo @Inject constructor(
        private val api: GreenelyApi,
        private val userStore: UserStore) {

    fun getSaveProfileResponse(requestSave: SaveProfileRequest): Observable<SaveProfileResponse> =
            api.getSaveProfileResponse("JWT ${userStore.token}", requestSave)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext { userStore.token = it.jwt }
                    .map {
                        it.data
                    }

    fun getProfileResponse(): Observable<GetProfileResponse> =
            api.getProfileResponse("JWT ${userStore.token}")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext { userStore.token = it.jwt }
                    .map {
                        it.data
                    }

    fun getSaveProfileResponse(firstName: String, lastName: String): Observable<SaveProfileResponse> =
            api.getSaveProfileWithoutImageResponse("JWT ${userStore.token}", firstName, lastName)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext { userStore.token = it.jwt }
                    .map {
                        it.data
                    }
}