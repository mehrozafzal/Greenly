package greenely.greenely.retail.data

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import greenely.greenely.api.GreenelyApi
import greenely.greenely.retail.models.*
import greenely.greenely.retailinvite.models.ReferralInviteResponseModel
import greenely.greenely.retailonboarding.models.RetailPriceSummaryResponse
import greenely.greenely.retailonboarding.models.VerifyPromoCodeRequest
import greenely.greenely.retailonboarding.models.VerifyPromoCodeResponse
import greenely.greenely.store.UserStore
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class RetailRepo @Inject constructor(
        private val api: GreenelyApi,
        private val userStore: UserStore
) {

    fun getRetailOverviewResponse(): Observable<RetailOverViewJson> =
            api.getRetailOverview("JWT ${userStore.token}")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext { userStore.token = it.jwt }
                    .map { it.data }

    fun getRetailInvoicesResponse(): Observable<List<Invoice>> =
            api.getRetailInvoices("JWT ${userStore.token}")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext { userStore.token = it.jwt }
                    .map { it.data }

    fun getRetailState(): Observable<RetailStateResponseModel> =
            api.getRetailState("JWT ${userStore.token}")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext { userStore.token = it.jwt }
                    .map { it.data }



}

enum class CustomerState {
    EMPTY,
    WAITING,
    FAILED,
    COMPLETED,
    OPERATIONAL,
    TERMINATED
}

class CustomerStateAdapter {
    @FromJson
    fun fromJson(json: String): CustomerState = when (json) {
        "EMPTY" -> CustomerState.EMPTY
        "WAITING" -> CustomerState.WAITING
        "FAILED" -> CustomerState.FAILED
        "COMPLETED" -> CustomerState.COMPLETED
        "OPERATIONAL" -> CustomerState.OPERATIONAL
        "TERMINATED" -> CustomerState.TERMINATED
        else -> null
    }!!

    @ToJson
    fun toJson(@Suppress("UNUSED_PARAMETER") state: CustomerState): Int = throw UnsupportedOperationException()
}
