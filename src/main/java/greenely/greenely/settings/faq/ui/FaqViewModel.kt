package greenely.greenely.settings.faq.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import greenely.greenely.OpenClassOnDebug
import greenely.greenely.settings.faq.data.FaqRepo
import greenely.greenely.settings.faq.ui.events.Event
import greenely.greenely.settings.faq.ui.models.FaqItem
import greenely.greenely.utils.SingleLiveEvent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

@OpenClassOnDebug
class FaqViewModel @Inject constructor(private val repo: FaqRepo) : ViewModel() {
    private val _items = MutableLiveData<List<FaqItem>>()
    val items: LiveData<List<FaqItem>>
        get() = _items

    private val _events = SingleLiveEvent<Event>()
    val events: LiveData<Event>
        get() = _events

    private val disposables = CompositeDisposable()


    fun fetch() {
        disposables.add(
                repo.getFaqItems()
                        .subscribeBy(
                                onNext = {
                                    _items.value = it
                                }
                        )
        )
    }

    fun exit() {
        _events.value = Event.Exit
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}

