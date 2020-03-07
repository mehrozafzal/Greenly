package greenely.greenely.setuphousehold.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import greenely.greenely.models.AuthenticationInfo
import greenely.greenely.setuphousehold.data.HouseholdRepo
import greenely.greenely.setuphousehold.mappers.HouseholdConfigMapper
import greenely.greenely.setuphousehold.mappers.HouseholdRequestMapper
import greenely.greenely.setuphousehold.models.HouseholdConfig
import greenely.greenely.setuphousehold.models.HouseholdInputOptions
import greenely.greenely.setuphousehold.models.HouseholdRequest
import greenely.greenely.setuphousehold.models.json.HouseholdRequestJsonModel
import greenely.greenely.setuphousehold.ui.events.Event
import greenely.greenely.utils.NonNullObservableField
import greenely.greenely.utils.SingleLiveEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SetupHouseholdViewModel @Inject constructor(

        private val repo: HouseholdRepo,
        private val householdConfigMapper: HouseholdConfigMapper,
        private val householdRequestMapper: HouseholdRequestMapper

) : ViewModel() {

    private val _errors = SingleLiveEvent<Throwable>()
    val errors: LiveData<Throwable>
        get() = _errors

    private val _events = SingleLiveEvent<Event>()
    val events: LiveData<Event>
        get() = _events

    private val _disruptiveLoading = SingleLiveEvent<Boolean?>()
    val disruptiveLoading: LiveData<Boolean?>
        get() = _disruptiveLoading

    private val _householdConfigurationOptions = MutableLiveData<HouseholdConfig>()
    val householdConfigurationOptions: LiveData<HouseholdConfig>
        get() {
            if (_householdConfigurationOptions.value == null) getHouseholdConfigurationOptions()
            return _householdConfigurationOptions
        }

    private val _householdResponse = MutableLiveData<AuthenticationInfo>()
    val householdResponse: LiveData<AuthenticationInfo>
        get() = _householdResponse

    private val _updateHouseholdResponse = MutableLiveData<HouseholdRequestJsonModel>()
    val updateHouseholdResponse: LiveData<HouseholdRequestJsonModel>
        get() = _updateHouseholdResponse

    var householdRequest = HouseholdRequest()
    val municipality = NonNullObservableField<String>("")
    var heatingTypesList: ArrayList<HouseholdInputOptions> = ArrayList()
    var shouldReOnboard: Boolean = false

    private val _householdParameters = MutableLiveData<HouseholdRequestJsonModel>()
    val householdParameters: LiveData<HouseholdRequestJsonModel>
        get() {
            if (_householdParameters.value == null) getHouseholdParameters()
            return _householdParameters
        }

    fun getHouseholdConfigurationOptions() {
        repo.getHouseholdConfig()
                .doOnSubscribe { _events.value = Event.ShowLoader(true) }
                .map {
                    householdConfigMapper.fromHouseholdConfigJson(it)
                }
                .subscribeBy(
                        onNext = {
                            _events.value = Event.ShowLoader(false)
                            _householdConfigurationOptions.value = it
                        },
                        onError = {
                            _events.value = Event.ShowLoader(false)
                            _errors.value = it
                        }
                )
    }

    fun getHouseholdParameters() {
        repo.getHouseholdParameters()
                .doOnSubscribe { _events.value = Event.ShowLoader(true) }
                .subscribeBy(
                        onNext = {
                            _events.value = Event.ShowLoader(false)
                            _householdParameters.value = it
                        },
                        onError = {
                            _events.value = Event.ShowLoader(false)
                            _errors.value = it
                        }
                )
    }

    fun sendHouseholdInfo(delay: Boolean = true) {
        val request = householdRequestMapper.toHouseholdRequestJson(filterRequest(householdRequest))

        val householdObserver = if (delay) {
            repo.setHouseholdConfig(request)
                    .delay(2, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
        } else repo.setHouseholdConfig(request)

        householdObserver.doOnSubscribe { _disruptiveLoading.value = true }
                .subscribeBy(
                        onNext = {
                            _householdResponse.value = it
                            _disruptiveLoading.value = false
                        },
                        onError = {
                            _disruptiveLoading.value = false
                            _errors.value = it
                        }
                )
    }

    fun setMunicipalityId() {
        _householdConfigurationOptions.value?.municipalities?.let {
            val option = it.findLast { it.name.toLowerCase().trim() == municipality.get().toLowerCase().trim() }
            householdRequest.municipalityId.set(option?.id)
        }
    }

    fun abortSetup() {
        _events.value = Event.Abort
    }

    private fun filterRequest(householdRequest: HouseholdRequest): HouseholdRequest {
        if (householdRequest.facilityTypeId.get() == SetupHouseholdActivity.FACILITY_TYPE_APARTMENT) {
            householdRequest.quaternaryHeatingTypeId.set(null)
            householdRequest.tertiaryHeatingTypeId.set(null)
            householdRequest.secondaryHeatingTypeId.set(null)
            householdRequest.heatingTypeId.set(null)
            householdRequest.constructionYearId.set(null)
            householdRequest.electricCarCountId.set(null)
        }
        return householdRequest
    }

    fun updateHouseHoldInfo(delay: Boolean = true) {
        val request = householdRequestMapper.toHouseholdRequestJson(householdRequest)
        val householdObserver = if (delay) {
            repo.updateHouseholdParameters(request)
                    .delay(2, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
        } else repo.updateHouseholdParameters(request)

        householdObserver.doOnSubscribe { _disruptiveLoading.value = true }
                .subscribeBy(
                        onNext = {
                            _updateHouseholdResponse.value = it
                            _disruptiveLoading.value = false
                        },
                        onError = {
                            _disruptiveLoading.value = false
                            _errors.value = it
                        }
                )
    }

}
