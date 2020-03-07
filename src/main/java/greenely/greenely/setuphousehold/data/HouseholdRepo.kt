package greenely.greenely.setuphousehold.data

import greenely.greenely.OpenClassOnDebug
import greenely.greenely.api.GreenelyApi
import greenely.greenely.models.AuthenticationInfo
import greenely.greenely.setuphousehold.models.json.HouseholdConfigJsonModel
import greenely.greenely.setuphousehold.models.json.HouseholdRequestJsonModel
import greenely.greenely.store.UserStore
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@OpenClassOnDebug
class HouseholdRepo @Inject constructor(
        private val api: GreenelyApi,
        private val userStore: UserStore
) {

    fun getHouseholdConfig(): Observable<HouseholdConfigJsonModel> =
            api.getHouseholdConfig("JWT ${userStore.token}")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext { userStore.token = it.jwt }
                    .map { it.data }

    fun setHouseholdConfig(request: HouseholdRequestJsonModel): Observable<AuthenticationInfo> =
            api.postHouseholdData("JWT ${userStore.token}", request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext { userStore.token = it.jwt }
                    .map { it.data }

    fun updateHouseholdParameters(request: HouseholdRequestJsonModel): Observable<HouseholdRequestJsonModel> =
            api.patchHouseholdData("JWT ${userStore.token}", request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext { userStore.token = it.jwt }
                    .map { it.data }

    fun getHouseholdParameters(): Observable<HouseholdRequestJsonModel> =
            api.getHouseholdData("JWT ${userStore.token}")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext { userStore.token = it.jwt }
                    .map { it.data }
}
