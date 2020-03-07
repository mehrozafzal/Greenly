package greenely.greenely.login.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import greenely.greenely.OpenClassOnDebug
import greenely.greenely.login.data.LoginRepo
import greenely.greenely.login.model.LoginData
import greenely.greenely.login.model.LoginErrorModel
import greenely.greenely.login.ui.events.Event
import greenely.greenely.login.ui.validation.LoginInputValidation
import greenely.greenely.models.AuthenticationInfo
import greenely.greenely.models.Resource
import greenely.greenely.utils.NonNullObservableField
import greenely.greenely.utils.SingleLiveEvent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject


@OpenClassOnDebug
class LoginViewModel @Inject constructor( private val inputValidator: LoginInputValidation, val repo: LoginRepo) : ViewModel() {

    val loginData = LoginData()

    val errors = NonNullObservableField<LoginErrorModel>(LoginErrorModel.noErrors())

    private val _loginResponse = MutableLiveData<Resource<AuthenticationInfo>>()

    val loginResponse: LiveData<Resource<AuthenticationInfo>>
        get() = _loginResponse

    private val _events = SingleLiveEvent<Event>()
    val events: LiveData<Event>
        get() = _events
    private val loginDisposable = CompositeDisposable()

    val validationTextWatcher: TextWatcher = ValidateOnTextChangeListener()

    fun onBackPressed() {
        _events.value = Event.Exit
    }

    fun login() {
        inputValidator.validate(loginData).let {
            errors.set(it)
            if (!it.hasErrors()) {
                loginDisposable.add(
                        repo.login(loginData)
                                .doOnSubscribe { _loginResponse.value = Resource.Loading() }
                                .subscribeBy(
                                        onNext = {
                                            _loginResponse.value = Resource.Success(it)
                                        },
                                        onError = {
                                            _loginResponse.value = Resource.Error(it)
                                        }
                                )
                )
            }
        }
    }

    fun forgotPassword() {
        _events.value = Event.ForgotPassword
    }

    fun handleEditorAction(id: Int) = when (id) {
        EditorInfo.IME_ACTION_DONE -> {
            login()
            true
        }
        else -> false
    }

    override fun onCleared() {
        super.onCleared()
        loginDisposable.clear()
    }

    inner class ValidateOnTextChangeListener : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if (errors.get().hasErrors()) {
                inputValidator.validate(loginData).let {
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
