package greenely.greenely.retailonboarding.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.os.Handler
import greenely.greenely.retail.data.RetailRepo
import greenely.greenely.retailonboarding.models.RetailPriceSummaryResponse
import greenely.greenely.retail.ui.events.Event
import greenely.greenely.retailonboarding.data.RetailOnboardingRepo
import greenely.greenely.utils.SingleLiveEvent
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class PriceSummaryViewModel @Inject constructor(
        private val repo: RetailOnboardingRepo
) : ViewModel()
{
    private val handler = Handler()
    private val _events = SingleLiveEvent<Event>()
    val events: LiveData<Event>
        get() = _events

    private val _errors = SingleLiveEvent<Throwable>()
    val errors: LiveData<Throwable>
        get() {
            return _errors
        }


    val retailPriceSummary= MutableLiveData<RetailPriceSummaryResponse>()

    var promoCode=SingleLiveEvent<String>()



    fun fetchPriceSummary() :MutableLiveData<RetailPriceSummaryResponse> {
        repo.getPriceSummary(promoCode.value)
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
                            retailPriceSummary.value =it
                        },
                        onError = {
                            handler.removeCallbacksAndMessages(null)
                            _events.value = Event.VisibilityOfViewUIEvent(true)
                            _events.value = Event.LoaderUIEvent(false)
                            _errors.value = it
                        }
                )

        return retailPriceSummary
    }
}