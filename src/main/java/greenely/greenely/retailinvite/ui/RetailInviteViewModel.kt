package greenely.greenely.retailinvite.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.os.Handler
import greenely.greenely.retail.data.RetailRepo
import greenely.greenely.retailinvite.models.ReferralInviteResponseModel
import greenely.greenely.retail.ui.events.Event
import greenely.greenely.retailinvite.data.RetailInviteRepo
import greenely.greenely.utils.SingleLiveEvent
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class RetailInviteViewModel @Inject constructor(
        private val repo: RetailInviteRepo
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

    val referralInviteResponseModel= MutableLiveData<ReferralInviteResponseModel>()


    fun fetchRetailReferral() :MutableLiveData<ReferralInviteResponseModel> {
        repo.getRetailReferral()
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
                            referralInviteResponseModel.value =it
                        },
                        onError = {
                            handler.removeCallbacksAndMessages(null)
                            _events.value = Event.VisibilityOfViewUIEvent(true)
                            _events.value = Event.LoaderUIEvent(false)
                            _errors.value = it
                            it.printStackTrace()
                        }
                )

        return referralInviteResponseModel
    }



}