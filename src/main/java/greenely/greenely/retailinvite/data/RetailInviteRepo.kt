package greenely.greenely.retailinvite.data

import greenely.greenely.api.GreenelyApi
import greenely.greenely.retailinvite.models.ReferralInviteResponseModel
import greenely.greenely.store.UserStore
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class RetailInviteRepo @Inject constructor(
        private val api: GreenelyApi,
        private val userStore: UserStore
) {

    fun getRetailReferral(): Observable<ReferralInviteResponseModel> =
            api.getRetailReferral("JWT ${userStore.token}")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext { userStore.token = it.jwt }
                    .map { it.data }
}