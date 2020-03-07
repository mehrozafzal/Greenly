package greenely.greenely.splash.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import greenely.greenely.OpenClassOnDebug
import greenely.greenely.accountsetup.models.AccountSetupNext
import greenely.greenely.models.Resource
import greenely.greenely.splash.data.SplashRepo
import greenely.greenely.welcome.model.json.InvitedFriendJson
import greenely.greenely.splash.ui.navigation.SplashRoute
import greenely.greenely.store.UserStore
import greenely.greenely.tracking.Tracker
import greenely.greenely.utils.SingleLiveEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * View model for [SplashActivity].

 * @author Anton Holmberg
 */
@OpenClassOnDebug
class SplashViewModel @Inject constructor(
        private val userStore: UserStore,
        val repo: SplashRepo
) : ViewModel() {

    /**
     * If the splash screen should be visible.
     */

    private val _finishSplashDuration = MutableLiveData<Boolean>()
    val finishSplashDuration: LiveData<Boolean>
        get() = _finishSplashDuration

    private val _openFirebaseLink = MutableLiveData<Boolean>()
    val openFirebaseLink: LiveData<Boolean>
        get() = _openFirebaseLink

    private val _accountSetupNext = SingleLiveEvent<AccountSetupNext>()
    val accountSetupNext: LiveData<AccountSetupNext>
        get() = _accountSetupNext

    private val _setupNext = SingleLiveEvent<AccountSetupNext>()
    val setupNext: LiveData<AccountSetupNext>
        get() = _setupNext

    private val _navigate = SingleLiveEvent<SplashRoute>()
    val navigate: LiveData<SplashRoute>
        get() = _navigate

    private val _identification = MutableLiveData<Tracker.UserIdentifier>()
    val identification: LiveData<Tracker.UserIdentifier>
        get() = _identification

    private val disposables = CompositeDisposable()

    val inviteFriendLiveData = MutableLiveData<Resource<InvitedFriendJson>>()


    /**
     * Try to authenticate with a previously stored access token.
     */
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

    fun checkLogin() {
        val observer =
                repo.checkAuth()
                        .observeOn(AndroidSchedulers.mainThread())

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
                                _setupNext.value = it.accountSetupNext
                            } else {
                                _openFirebaseLink.value = true

                            }
                        },
                        onError = {
                            _openFirebaseLink.value = true
                        }
                )
        )
    }

    fun getInvitedFriend(invitationID: String) {
        disposables.add(
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

    /**
     * When the login button is clicked.
     */
    fun onClickLogin() {
        _navigate.value = SplashRoute.LOGIN
    }

    /**
     * When the registration button is clicked.
     */
    fun onClickRegister() {
        _navigate.value = SplashRoute.REGISTER
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    fun checkFirstOpenFlag(): Boolean {
        return repo.checkFirstOpenFlag()
    }

    fun setOpenedFlag() {
        repo.setOpenedFlag()
    }

    fun setDeepLinkScreenName(deepLinkScreenName: String) {
        userStore.deepLinkScreenName = deepLinkScreenName
    }

    fun resetUserId() {
        userStore.userId = null
    }


    fun getDeepLinkScreenName(): String? {
        return userStore.deepLinkScreenName
    }
}
