package greenely.greenely.registration.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.databinding.Observable
import android.text.Editable
import android.text.TextWatcher
import greenely.greenely.OpenClassOnDebug
import greenely.greenely.models.AuthenticationInfo
import greenely.greenely.models.Resource
import greenely.greenely.registration.data.RegistrationRepo
import greenely.greenely.registration.mappers.RegistrationRequestMapper
import greenely.greenely.registration.ui.events.Event
import greenely.greenely.registration.ui.models.RegistrationErrorModel
import greenely.greenely.registration.ui.models.RegistrationInputModel
import greenely.greenely.registration.ui.validation.RegistrationInputValidator
import greenely.greenely.utils.NonNullObservableField
import greenely.greenely.utils.SingleLiveEvent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

@OpenClassOnDebug
class RegistrationViewModel @Inject constructor(
        private val repo: RegistrationRepo,
        private val inputValidator: RegistrationInputValidator,
        private val registrationRequestMapper: RegistrationRequestMapper
) : ViewModel() {

    private val _events = SingleLiveEvent<Event>()
    val events: LiveData<Event>
        get() = _events

    val input = RegistrationInputModel().apply {
        setPropertyChangedCallback(getInputChangedCallback())
    }

    val errors = NonNullObservableField<RegistrationErrorModel>(RegistrationErrorModel.noErrors())

    val validationTextWatcher: TextWatcher = ValidateOnTextChangeListener()

    private val _authenticationInfo = MutableLiveData<Resource<AuthenticationInfo>>()
    val authenticationInfo: LiveData<Resource<AuthenticationInfo>>
        get() = _authenticationInfo

    private var disposabled = CompositeDisposable()

    fun onBackPressed() {
        _events.value = Event.Exit
    }

    fun register(invitationID: String?) {
        _events.value = Event.HideKeyboard
        inputValidator.validate(input).let {
            errors.set(it)
            if (!it.hasErrors()) {
                disposabled += repo.register(registrationRequestMapper.fromRegistrationInput(input), invitationID)
                        .doOnSubscribe { _authenticationInfo.value = Resource.Loading() }
                        .subscribeBy(
                                onNext = {
                                    _authenticationInfo.value = Resource.Success(it)
                                },
                                onError = {
                                    _authenticationInfo.value = Resource.Error(it)
                                }
                        )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposabled.clear()
    }

    private fun getInputChangedCallback(): Observable.OnPropertyChangedCallback {
        return object : Observable.OnPropertyChangedCallback() {

            override fun onPropertyChanged(sender: Observable, propertyId: Int) {
                if (errors.get().hasErrors()) {
                    errors.set(inputValidator.validate(input))
                }
            }
        }
    }

    inner class ValidateOnTextChangeListener : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if (errors.get().hasErrors()) {
                inputValidator.validate(input).let {
                    errors.set(it)
                }
            }
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            // Do nothing
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            // Do nothing
        }

    }
}
