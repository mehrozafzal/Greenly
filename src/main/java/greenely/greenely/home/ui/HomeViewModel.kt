package greenely.greenely.home.ui

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.content.Intent
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import android.os.Handler
import android.util.Log
import greenely.greenely.OpenClassOnDebug
import greenely.greenely.R
import greenely.greenely.competefriend.json.addFriendJson.AddFriendResponseJson
import greenely.greenely.home.data.HomeModelFactory
import greenely.greenely.home.data.HomeRepo
import greenely.greenely.home.models.FacilitiesResponse
import greenely.greenely.home.models.HomeModel
import greenely.greenely.home.ui.events.Event
import greenely.greenely.main.router.Route
import greenely.greenely.main.ui.MainActivity
import greenely.greenely.models.Resource
import greenely.greenely.retail.util.RetailStateHandler
import greenely.greenely.utils.SingleLiveEvent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

@OpenClassOnDebug
class HomeViewModel @Inject constructor(
        private val repo: HomeRepo,
        private val homeModelFactory: HomeModelFactory,
        val retailStateHandler: RetailStateHandler
) : ViewModel() {


    private val _events = SingleLiveEvent<Event>()
    private val handler = Handler()

    val events: LiveData<Event>
        get() = _events

    private val _errors = SingleLiveEvent<Throwable?>()
    val errors: LiveData<Throwable?>
        get() = _errors

    private var homeModel: MutableLiveData<Resource<HomeModel>>? = null

    val resolution: ObservableInt = ObservableInt(R.id.months)

    val routeChangeEvent = SingleLiveEvent<Event>()

    private val disposables = CompositeDisposable()

    var displayRetailSnackbar = ObservableBoolean()

    val facilitiesResponse = SingleLiveEvent<Resource<FacilitiesResponse>>()


    fun onClickFetchData() {
        _events.value = Event.StartSignatureNavigationEvent(MainActivity.SIGN_POA_REQUEST)
    }

    fun onActivityResult(requestCode: Int, responseCode: Int, data: Intent?) {
        if (requestCode == MainActivity.SIGN_POA_REQUEST && responseCode == Activity.RESULT_OK) {
            homeModel?.let { fetchHomeModel(it) }
        }
    }

    fun getHomeModel(): LiveData<Resource<HomeModel>> {
        return homeModel.let {
            MutableLiveData<Resource<HomeModel>>().apply {
                homeModel = this
                fetchHomeModel(this)
            }
        }
    }

    fun fetchFacilitiesResponse() {
        disposables.add(
                repo.getFacilities()
                        .doOnSubscribe {
                            facilitiesResponse.value = Resource.Loading()
                        }
                        .subscribeBy(
                                onNext = {
                                    facilitiesResponse.value = Resource.Success(it)
                                },
                                onError = {
                                    facilitiesResponse.value = Resource.Error(it)
                                }
                        ))
    }

    private fun fetchHomeModel(homeModel: MutableLiveData<Resource<HomeModel>>) {
        disposables.clear()
        var disposable = repo.getHome(resolution.get())
                .doOnSubscribe {
                    handler.postDelayed({
                        _events.value = Event.LoaderUIEvent(true)
                    }, 1000)
                }
                .subscribeBy(
                        onNext = {
                            handler.removeCallbacksAndMessages(null)
                            homeModel.value = Resource.Success(homeModelFactory.createHomeModel(it))
                            _events.value = Event.LoaderUIEvent(false)
                        },
                        onError = {
                            handler.removeCallbacksAndMessages(null)
                            _errors.value = it
                            Log.e("Home", "Error", it)
                            _events.value = Event.LoaderUIEvent(false)
                        }
                )

        disposables.add(disposable)
    }


    fun onRetailPromoSnackBarUpgradeClick() {
        routeChangeEvent.value = Event.RouteTo(Route.RETAIL)
    }

    fun refereshRetailState() {
        retailStateHandler.refreshRetailState()
    }

    fun computeState(homeModel: HomeModel) {
        displayRetailSnackbar.set((!homeModel.isMissingPOAState() && !homeModel.isZavanneErrorState() && !homeModel.isNoFriendsWaitingState()))
    }

    fun getDataForResolution(id: Int) {
        when (id) {
            R.id.weeks -> homeModel?.let {
                resolution.set(R.id.weeks)
                fetchHomeModel(it)
            }
            R.id.days -> homeModel?.let {
                resolution.set(R.id.days)
                fetchHomeModel(it)
            }
            R.id.months -> homeModel?.let {
                resolution.set(R.id.months)
                fetchHomeModel(it)
            }
        }

    }
}

