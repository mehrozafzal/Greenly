package greenely.greenely.guidance.data

import greenely.greenely.OpenClassOnDebug
import greenely.greenely.api.GreenelyApi
import greenely.greenely.guidance.models.json.GuidanceContentJson
import greenely.greenely.store.UserStore
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
@OpenClassOnDebug
class GuidanceRepo @Inject constructor(
        private val api: GreenelyApi,
        private val userStore: UserStore
) {

    fun getGuidanceResponse(): Observable<GuidanceContentJson> =
            api.getGuidance("JWT ${userStore.token}")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext { userStore.token = it.jwt }
                    .map { it.data }
}





