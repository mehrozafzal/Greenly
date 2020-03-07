package greenely.greenely.guidance.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import greenely.greenely.OpenClassOnDebug
import greenely.greenely.guidance.data.GuidanceRepo
import greenely.greenely.guidance.mappers.GuidanceContentMapper
import greenely.greenely.guidance.models.GuidanceContent
import greenely.greenely.utils.SingleLiveEvent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

@OpenClassOnDebug
class GuidanceViewModel @Inject constructor(
        private val repo: GuidanceRepo,
        private val mapper: GuidanceContentMapper
) : ViewModel() {

    private val _errors = SingleLiveEvent<Throwable>()
    val errors: LiveData<Throwable>
        get() {
            return _errors
        }

    private val _guidanceContent = MutableLiveData<GuidanceContent>()

    val guidanceContent: LiveData<GuidanceContent>
        get() {
            if (_guidanceContent.value == null) fetchGuidanceContent()
            return _guidanceContent
        }

    private val disposables = CompositeDisposable()

     fun fetchGuidanceContent() {
        disposables += repo.getGuidanceResponse()
                .map {
                    mapper.fromJson(it)
                }
                .subscribeBy(
                        onNext = {
                            _guidanceContent.value = it
                        },
                        onError = {
                            it.printStackTrace()
                            _errors.value = it
                        }
                )
    }

    override fun onCleared() {
        disposables.dispose()
        super.onCleared()
    }
}

