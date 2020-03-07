package greenely.greenely.profile.ui

import androidx.lifecycle.*
import greenely.greenely.OpenClassOnDebug
import greenely.greenely.models.Resource
import greenely.greenely.profile.data.ProfileRepo
import greenely.greenely.profile.json.GetProfileResponse
import greenely.greenely.profile.json.SaveProfileRequest
import greenely.greenely.profile.json.SaveProfileResponse
import greenely.greenely.utils.SingleLiveEvent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject


/**
 * Events that might occur in settings
 */
enum class ProfileEvent {
    /**
     * When the password is changed.
     */
    PASSWORD_CHANGED
}

/**
 * Events to be sent to the Ui.
 */
sealed class ProfileUiEvent {
    /**
     * When the password has been changed.
     */
    object PasswordChanged : ProfileUiEvent()

    /**
     * When an error should be shown.
     *
     * @property error The error the occured.
     */
    data class ShowError(val error: Throwable) : ProfileUiEvent()
}


/**
 * View model for settings
 */
@OpenClassOnDebug
class UpdateProfileViewModel @Inject constructor(
        val repo: ProfileRepo) : ViewModel() {
    private val loadingInfo = MutableLiveData<Boolean>()
    private val loadingHouseHold = MutableLiveData<Boolean>()
    private val loadingChangePassword = MutableLiveData<Boolean>()
    val profileSaveResponseLiveData = SingleLiveEvent<Resource<SaveProfileResponse>>()
    val profileGetResponseLiveData = SingleLiveEvent<Resource<GetProfileResponse>>()
    private val disposables = CompositeDisposable()


    private val events = SingleLiveEvent<ProfileUiEvent>()


    /**
     * Get a stream of events.
     */
    fun getEvents(): LiveData<ProfileUiEvent> = events


    /**
     * If teh settings are loading.
     */
    fun isLoadingSettingsInfo(): LiveData<Boolean> = loadingInfo

    /**
     * If the household is loading.
     */
    fun isLoadingHousehold(): LiveData<Boolean> = loadingHouseHold

    /**
     * If change password is loading.
     */
    fun isLoadingChangePassword(): LiveData<Boolean> = loadingChangePassword


    fun getProfileSaveResponse(requestSave: SaveProfileRequest) {
        disposables.add(
                repo.getSaveProfileResponse(requestSave)
                        .doOnSubscribe {
                            profileSaveResponseLiveData.value = Resource.Loading()
                        }
                        .subscribeBy(
                                onNext = {
                                    profileSaveResponseLiveData.value = Resource.Success(it)
                                },
                                onError = {
                                    profileSaveResponseLiveData.value = Resource.Error(it)
                                }
                        ))
    }

    fun getProfileSaveResponse(firstName: String, lastName: String) {
        disposables.add(
                repo.getSaveProfileResponse(firstName, lastName)
                        .doOnSubscribe {
                            profileSaveResponseLiveData.value = Resource.Loading()
                        }
                        .subscribeBy(
                                onNext = {
                                    profileSaveResponseLiveData.value = Resource.Success(it)
                                },
                                onError = {
                                    profileSaveResponseLiveData.value = Resource.Error(it)
                                }
                        ))
    }

    fun getProfileResponse() {
        disposables.add(
                repo.getProfileResponse()
                        .doOnSubscribe {
                            profileGetResponseLiveData.value = Resource.Loading()
                        }
                        .subscribeBy(
                                onNext = {
                                    profileGetResponseLiveData.value = Resource.Success(it)
                                },
                                onError = {
                                    profileGetResponseLiveData.value = Resource.Error(it)
                                }
                        ))
    }

    override fun onCleared() {
        disposables.dispose()
        super.onCleared()
    }

}

