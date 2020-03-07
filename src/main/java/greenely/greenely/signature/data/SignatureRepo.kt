package greenely.greenely.signature.data

import greenely.greenely.OpenClassOnDebug
import greenely.greenely.api.GreenelyApi
import greenely.greenely.retail.models.RetailBankIdProcessJson
import greenely.greenely.retailonboarding.models.CustomerConversionResponseJson
import greenely.greenely.retailonboarding.models.CustomerInfoModel
import greenely.greenely.signature.data.models.InputValidationModel
import greenely.greenely.signature.data.models.PrefillDataResponseModel
import greenely.greenely.signature.data.models.SignatureRequestModel
import greenely.greenely.signature.data.models.ValidationResponse
import greenely.greenely.store.UserStore
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@OpenClassOnDebug
class SignatureRepo @Inject constructor(
        private val api: GreenelyApi,
        private val userStore: UserStore
) {


    fun sendCustomerInfo(signatureRequestModel: SignatureRequestModel): Observable<CustomerConversionResponseJson> {
        return api.postNewPoaCustomer("JWT ${userStore.token}", signatureRequestModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { userStore.token = it.jwt }
                .map { it.data }
    }

    fun preFillFromPersonalNumber(personalNumber: String): Single<PrefillDataResponseModel> {
        return api.fetchSignaturePreFillData("JWT ${userStore.token}", personalNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { userStore.token = it.jwt }
                .map { it.data }
                .singleOrError()
    }

    fun sendSignature(signatureRequestModel: SignatureRequestModel): Completable {
        return api.sendSignature("JWT ${userStore.token}", signatureRequestModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { userStore.token = it.jwt }
                .ignoreElements()
    }

    fun validateMeterId(inputValidationModel: InputValidationModel): Observable<ValidationResponse> {
        return api.validateInputValues(inputValidationModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { it.data }
    }


    fun getBankIdProcessStatus(bankidOrderRef: String?): Observable<RetailBankIdProcessJson> =
            api.getPOABankIdProcessStatus("JWT ${userStore.token}", bankidOrderRef!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext { userStore.token = it.jwt }
                    .map { it.data.first() }

    fun cancelExistingBankIdProcess(bankidOrderRef: String?): Observable<RetailBankIdProcessJson> =
            api.cancelExistingPoaBankIDProcess("JWT ${userStore.token}", bankidOrderRef!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext { userStore.token = it.jwt }
                    .map { it.data }

}
