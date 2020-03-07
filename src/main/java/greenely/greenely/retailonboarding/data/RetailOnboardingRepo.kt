package greenely.greenely.retailonboarding.data

import greenely.greenely.OpenClassOnDebug
import greenely.greenely.api.GreenelyApi
import greenely.greenely.retail.models.RetailBankIdProcessJson
import greenely.greenely.retailonboarding.mappers.CustomerInfoMapper
import greenely.greenely.retailonboarding.models.*
import greenely.greenely.signature.data.models.PrefillDataResponseModel
import greenely.greenely.store.UserStore
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@OpenClassOnDebug
class RetailOnboardingRepo @Inject constructor(
        private val api: GreenelyApi,
        private val userStore: UserStore,
        private val customerInfoMapper: CustomerInfoMapper
) {
    fun sendCustomerInfo(customerInfo: CustomerInfoModel): Observable<CustomerConversionResponseJson> {
        val jsonModel = customerInfoMapper.from(customerInfo)
        return api.postNewRetailCustomer("JWT ${userStore.token}", jsonModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { userStore.token = it.jwt }
                .map { it.data }
    }

    fun getBankIdProcessStatus(bankidOrderRef: String?): Observable<RetailBankIdProcessJson> =
            api.getBankIdProcessStatus("JWT ${userStore.token}", bankidOrderRef!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext { userStore.token = it.jwt }
                    .map { it.data }

    fun cancelExistingBankIdProcess(bankidOrderRef: String?): Observable<RetailBankIdProcessJson> =
            api.cancelExistingBankIDProcess("JWT ${userStore.token}", bankidOrderRef!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext { userStore.token = it.jwt }
                    .map { it.data }

    fun preFillFromPersonalNumber(personalNumber: String): Single<PrefillDataResponseModel> {
        return api.fetchSignaturePreFillData("JWT ${userStore.token}", personalNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { userStore.token = it.jwt }
                .map { it.data }
                .singleOrError()
    }

    fun getPriceSummary(promoCode:String?): Observable<RetailPriceSummaryResponse> =
            api.getRetailPriceSummary("JWT ${userStore.token}",promoCode)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext { userStore.token = it.jwt }
                    .map { it.data }

    fun verifyPromoCode(promoCode:String?): Observable<VerifyPromoCodeResponse> =
            api.verifyPromoCode("JWT ${userStore.token}", VerifyPromoCodeRequest(promoCode))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext { userStore.token = it.jwt }
                    .map { it.data }
}