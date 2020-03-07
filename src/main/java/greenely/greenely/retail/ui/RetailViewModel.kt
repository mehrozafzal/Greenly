package greenely.greenely.retail.ui

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.content.Intent
import android.os.Handler
import android.view.View
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import greenely.greenely.OpenClassOnDebug
import greenely.greenely.main.ui.MainActivity
import greenely.greenely.retail.util.RetailStateHandler
import greenely.greenely.retail.data.RetailOverviewFactory
import greenely.greenely.retail.data.RetailRepo
import greenely.greenely.retail.models.Invoice
import greenely.greenely.retail.models.RetailOverview
import greenely.greenely.retail.ui.events.ClickEvents
import greenely.greenely.retail.ui.events.Event
import greenely.greenely.retailonboarding.models.RetailOnboardingState
import greenely.greenely.retailonboarding.ui.RetailOnboardingActivity
import greenely.greenely.store.UserStore
import greenely.greenely.utils.SingleLiveEvent
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

@OpenClassOnDebug
class RetailViewModel @Inject constructor(
        private val userStore: UserStore,
        private val repo: RetailRepo,
        private val retailOverviewFactory: RetailOverviewFactory,
        private val retailStateHandler: RetailStateHandler
) : ViewModel() {

    private val handler = Handler()
    private val _events = SingleLiveEvent<Event>()
    val events: LiveData<Event>
        get() = _events

    private val _errors = SingleLiveEvent<Throwable>()
    val errors: LiveData<Throwable>
        get() {
            return _errors
        }

    private val _clickEvents = SingleLiveEvent<ClickEvents>()
    val clickEvents: LiveData<ClickEvents>
        get() = _clickEvents

    private val retailOnboardingState = SingleLiveEvent<RetailOnboardingState>()
    fun fetchRetailOnboardingState(): LiveData<RetailOnboardingState> {
        return retailOnboardingState
    }

    private val _retailOverview = MutableLiveData<RetailOverview>()
    val retailOverview: LiveData<RetailOverview>
        get() {
            if (_retailOverview.value == null) fetchRetailOverview()
            return _retailOverview
        }

//    private val _retailPriceSummary= MutableLiveData<RetailPriceSummaryResponse>()

//    val retailPriceSummary: LiveData<RetailPriceSummaryResponse>
//        get() {
//            if (_retailPriceSummary.value == null) fetchPriceSummary(null)
//            return _retailPriceSummary
//        }


     val discount = SingleLiveEvent<Float>()

    val OnboardingInterestVisibility = MutableLiveData<Int>()




    private val _retailInvoices = MutableLiveData<List<Invoice>>()
    val retailInvoices: LiveData<List<Invoice>>
        get() {
            if (_retailInvoices.value == null) fetchRetailInvoices()
            return _retailInvoices
        }

    private fun fetchRetailOverview() {
        repo.getRetailOverviewResponse()
                .doOnSubscribe {
                    _events.value = Event.VisibilityOfViewUIEvent(false)
                    handler.postDelayed({
                        _events.value = Event.LoaderUIEvent(true)
                    }, 500)
                }
                .subscribeBy(
                        onNext = {
                            handler.removeCallbacksAndMessages(null)
                            _events.value = Event.VisibilityOfViewUIEvent(true)
                            _events.value = Event.LoaderUIEvent(false)
                            _retailOverview.value =
                                    retailOverviewFactory.createRetailOverviewModel(it)
                        },
                        onError = {
                            it.printStackTrace()
                            handler.removeCallbacksAndMessages(null)
                            _events.value = Event.VisibilityOfViewUIEvent(true)
                            _events.value = Event.LoaderUIEvent(false)
                            _errors.value = it
                        }
                )
    }

//     fun fetchPriceSummary(promoCode:String?) {
//        repo.getPriceSummary(promoCode)
//                .doOnSubscribe {
//                    _events.value = Event.VisibilityOfViewUIEvent(false)
//                    handler.postDelayed({
//                        _events.value = Event.LoaderUIEvent(true)
//                    }, 500)
//                }
//                .subscribeBy(
//                        onNext = {
//                            handler.removeCallbacksAndMessages(null)
//                            _events.value = Event.VisibilityOfViewUIEvent(true)
//                            _events.value = Event.LoaderUIEvent(false)
//                            _retailPriceSummary.value =it
//                        },
//                        onError = {
//                            handler.removeCallbacksAndMessages(null)
//                            _events.value = Event.VisibilityOfViewUIEvent(true)
//                            _events.value = Event.LoaderUIEvent(false)
//                            _errors.value = it
//                        }
//                )
//    }

    private fun fetchRetailInvoices() {
        repo.getRetailInvoicesResponse()
                .doOnSubscribe {
                    _events.value = Event.VisibilityOfViewUIEvent(false)
                    handler.postDelayed({
                        _events.value = Event.LoaderUIEvent(true)
                    }, 500)
                }
                .subscribeBy(
                        onNext = {
                            handler.removeCallbacksAndMessages(null)
                            _events.value = Event.VisibilityOfViewUIEvent(true)
                            _events.value = Event.LoaderUIEvent(false)
                            _retailInvoices.value = it
                        },
                        onError = {
                            handler.removeCallbacksAndMessages(null)
                            _events.value = Event.VisibilityOfViewUIEvent(true)
                            _events.value = Event.LoaderUIEvent(false)
                            _errors.value = it
                        }
                )
    }

    fun onActivityResult(requestCode: Int, responseCode: Int, data: Intent?) {
        if (requestCode == MainActivity.SIGN_POA_REQUEST && responseCode == Activity.RESULT_OK) {
            _retailOverview.let {
                fetchRetailOverview()
            }
        } else if (requestCode == RetailOnboardingActivity.CLOSE_ONBOARDING_ACTIVITY_REQUEST_CODE
                && responseCode == Activity.RESULT_OK) {
            fetchRetailState()
            retailOnboardingState.value = RetailOnboardingState.RETAIL_ONBOARDING_COMPLETED
        }
    }

    fun addNewInterestedUser(userId: Int) {
        val storedInterestedUsersList: ArrayList<Int> = ArrayList()
        storedInterestedUsersList.addAll(getStoredInterestedUsersList())
        storedInterestedUsersList.add(userId)
        val gson = Gson()
        val json = gson.toJson(storedInterestedUsersList)
        userStore.retailInterestedUsersList = json
    }

    fun getStoredInterestedUsersList(): ArrayList<Int> {
        val gson = Gson()
        val json = userStore.retailInterestedUsersList
        val type = object : TypeToken<List<Int>>() {}.type
        return gson.fromJson(json, type)
    }

    fun getCurrentUserID(): Int? {
        return userStore.userId
    }

    fun resetAppSessionCountForRetailPromotion() {
         userStore.appSessionCount=3
    }

    fun fetchRetailState(){
        retailStateHandler.refreshRetailState()
    }

    fun setOnClick(onClickEvent:ClickEvents) {
        _clickEvents.value=onClickEvent
    }

     fun initOnboardingInterestVisibility(){
        if (getStoredInterestedUsersList().contains(getCurrentUserID())) {
            OnboardingInterestVisibility.value=View.INVISIBLE
        } else {
            OnboardingInterestVisibility.value=View.VISIBLE
        }
    }

    fun onClickSendRetailInterest() {
        if (!getStoredInterestedUsersList().contains(getCurrentUserID())) {
                addNewInterestedUser(getCurrentUserID()!!)
                resetAppSessionCountForRetailPromotion()
            }
        OnboardingInterestVisibility.value=View.INVISIBLE
    }




}
