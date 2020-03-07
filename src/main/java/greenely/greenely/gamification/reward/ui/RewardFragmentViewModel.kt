package greenely.greenely.gamification.reward.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import greenely.greenely.utils.SingleLiveEvent
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class RewardFragmentViewModel @Inject constructor() : ViewModel() {

    private val _errors = SingleLiveEvent<Throwable>()
    val errors: LiveData<Throwable>
        get() {
            return _errors
        }
    /**
     * If an error occured.
     */
    val error = MutableLiveData<Throwable>()
    private val disposables = CompositeDisposable()


    override fun onCleared() {
        disposables.dispose()
        super.onCleared()
    }
}