package greenely.greenely.main.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import greenely.greenely.feed.data.FeedRepo
import greenely.greenely.main.events.Event
import greenely.greenely.main.router.Route
import greenely.greenely.models.Resource
import greenely.greenely.profile.json.GetProfileResponse
import greenely.greenely.retail.util.RetailStateHandler
import greenely.greenely.store.UserStore
import greenely.greenely.utils.SingleLiveEvent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class MainViewModel @Inject constructor(
        private val userStore: UserStore,
        private val feedRepo: FeedRepo

) : ViewModel() {

    private val _events = SingleLiveEvent<Event>()
    val events: LiveData<Event> = _events
    val profileGetResponseLiveData = SingleLiveEvent<Resource<GetProfileResponse>>()
    private val disposables = CompositeDisposable()
    private val _hasUnreadFeedItems = MutableLiveData<Boolean>()
    val hasUnreadFeedItems: LiveData<Boolean>
        get() {
            if (_hasUnreadFeedItems.value == null) {
                disposables.add(
                        feedRepo.hasUnreadItems()
                                .subscribeBy(
                                        onNext = {
                                            _hasUnreadFeedItems.value = it
                                        }
                                )
                )
            }

            return _hasUnreadFeedItems
        }

    private val deepLinkRoute = SingleLiveEvent<Route>()
    fun fetchDeepLinkScreenState(): LiveData<Route> {
        when {
            getDeepLinkStateName().equals("home") -> deepLinkRoute.value = Route.HOME
            // getDeepLinkStateName().equals("guidance") -> deepLinkRoute.value = Route.GUIDANCE
            getDeepLinkStateName().equals("history") -> deepLinkRoute.value = Route.HISTORY
            getDeepLinkStateName().equals("feed") -> deepLinkRoute.value = Route.FEED
            getDeepLinkStateName().equals("retail") -> deepLinkRoute.value = Route.RETAIL
            getDeepLinkStateName().equals("compete") -> deepLinkRoute.value = Route.COMPETE_FRIEND
            else -> deepLinkRoute.value = Route.HOME
        }
        return deepLinkRoute
    }

    fun getProfileResponse() {
        disposables.add(
                feedRepo.getProfileResponse()
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

    fun logout() {
        userStore.token = null
        userStore.clearInvitedUserPreference()
        userStore.clearAllTokens()
        userStore.clearAccountsMap()
        _events.value = Event.Logout
    }

    fun onRouteSelected(route: Route) {
        if (route == Route.FEED) {
            _hasUnreadFeedItems.value = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    private fun getDeepLinkStateName(): String? {
        return userStore.deepLinkScreenName
    }


}