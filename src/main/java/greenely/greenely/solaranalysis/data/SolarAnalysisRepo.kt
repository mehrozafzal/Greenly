package greenely.greenely.solaranalysis.data

import greenely.greenely.OpenClassOnDebug
import greenely.greenely.api.GreenelyApi
import greenely.greenely.solaranalysis.mappers.AnalysisMapper
import greenely.greenely.solaranalysis.mappers.ContactMeRequestMapper
import greenely.greenely.solaranalysis.mappers.HouseholdInfoMapper
import greenely.greenely.solaranalysis.models.Analysis
import greenely.greenely.solaranalysis.models.ContactInfo
import greenely.greenely.solaranalysis.models.HouseholdInfo
import greenely.greenely.store.UserStore
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@OpenClassOnDebug
class SolarAnalysisRepo @Inject constructor(
        private val api: GreenelyApi,
        private val userStore: UserStore,
        private val analysisMapper: AnalysisMapper,
        private val householdInfoMapper: HouseholdInfoMapper,
        private val contactMeRequestMapper: ContactMeRequestMapper
) {
    fun sendHouseholdInfo(householdInfo: HouseholdInfo): Observable<Analysis> {
        val jsonModel = householdInfoMapper.toJsonData(householdInfo)
        return api.postSolarAnalysisHousehold("JWT ${userStore.token}", jsonModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { userStore.token = it.jwt }
                .map { analysisMapper.fromAnalysisJson(it.data.payload, it.data.id) }
    }

    fun validateAddress(householdInfo: HouseholdInfo): Completable {
        val addressValidationRequest = householdInfoMapper.toAddressValidationRequest(householdInfo)
        return api.validateSolarAnalysisAddress("JWT ${userStore.token}", addressValidationRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { userStore.token = it.jwt }
                .ignoreElements()
    }

    fun sendContactInformation(analysis: Analysis, contactInfo: ContactInfo
    ): Completable {
        val contactMeRequest = contactMeRequestMapper.from(analysis, contactInfo)
        return api.postContact("JWT ${userStore.token}", contactMeRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { userStore.token = it.jwt }
                .ignoreElements()
    }
}
