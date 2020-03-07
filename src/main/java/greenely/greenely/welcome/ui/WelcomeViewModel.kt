package greenely.greenely.welcome.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.databinding.Observable
import android.text.Editable
import android.text.TextWatcher
import greenely.greenely.OpenClassOnDebug
import greenely.greenely.accountsetup.models.AccountSetupNext
import greenely.greenely.models.AuthenticationInfo
import greenely.greenely.models.Resource
import greenely.greenely.registration.data.RegistrationRepo
import greenely.greenely.registration.mappers.RegistrationRequestMapper
import greenely.greenely.registration.ui.events.Event
import greenely.greenely.registration.ui.models.RegistrationErrorModel
import greenely.greenely.registration.ui.models.RegistrationInputModel
import greenely.greenely.registration.ui.validation.RegistrationInputValidator
import greenely.greenely.splash.data.SplashRepo
import greenely.greenely.splash.ui.navigation.SplashRoute
import greenely.greenely.store.UserStore
import greenely.greenely.tracking.Tracker
import greenely.greenely.utils.NonNullObservableField
import greenely.greenely.utils.SingleLiveEvent
import greenely.greenely.welcome.data.WelcomeRepo
import greenely.greenely.welcome.model.json.InvitedFriendJson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@OpenClassOnDebug
class WelcomeViewModel @Inject constructor(private val userStore: UserStore,
                                           val repo: WelcomeRepo) : ViewModel() {

    val inviteFriendLiveData = MutableLiveData<Resource<InvitedFriendJson>>()
    private val _finishSplashDuration = MutableLiveData<Boolean>()
    val finishSplashDuration: LiveData<Boolean>
        get() = _finishSplashDuration

    private val _accountSetupNext = SingleLiveEvent<AccountSetupNext>()
    val accountSetupNext: LiveData<AccountSetupNext>
        get() = _accountSetupNext

    private val _navigate = SingleLiveEvent<SplashRoute>()
    val navigate: LiveData<SplashRoute>
        get() = _navigate

    private val _identification = MutableLiveData<Tracker.UserIdentifier>()
    val identification: LiveData<Tracker.UserIdentifier>
        get() = _identification

    private val disposables = CompositeDisposable()

    private val _events = SingleLiveEvent<Event>()
    val events: LiveData<Event>
        get() = _events

    private var disposable = CompositeDisposable()

    fun onBackPressed() {
        _events.value = Event.Exit
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }


    fun getInvitedFriend(invitationID: String) {
        disposable.add(
                repo.invitedFriendResponse(invitationID)
                        /* .map {
                             mapper.fromRankResponseJson(it)
                         }*/
                        .doOnSubscribe {
                            inviteFriendLiveData.value = Resource.Loading()
                        }
                        .subscribeBy(
                                onNext = {
                                    inviteFriendLiveData.value = Resource.Success(it)
                                },
                                onError = {
                                    inviteFriendLiveData.value = Resource.Error(it)
                                }
                        ))
    }

    fun autoLogin(delay: Boolean = true) {
        val observer = if (delay) {
            repo.checkAuth()
                    .delay(1, TimeUnit.SECONDS, true)
                    .observeOn(AndroidSchedulers.mainThread())
        } else {
            repo.checkAuth()
        }

        disposables.add(
                observer.subscribeBy(
                        onNext = {
                            _identification.value = Tracker.UserIdentifier(
                                    it.userId,
                                    it.intercom?.userHash,
                                    it.intercom?.email,
                                    it.intercomProperties
                            )
                            if (it.accountSetupNext == AccountSetupNext.NO_NEXT_STEP) {
                                _accountSetupNext.value = it.accountSetupNext
                            } else {
                                _finishSplashDuration.value = true
                            }
                        },
                        onError = {
                            _finishSplashDuration.value = true
                        }
                )
        )
    }


}
