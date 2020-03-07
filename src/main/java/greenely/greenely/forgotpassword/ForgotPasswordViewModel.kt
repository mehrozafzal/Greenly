package greenely.greenely.forgotpassword

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.databinding.ObservableField
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import greenely.greenely.R
import greenely.greenely.extensions.notify
import greenely.greenely.utils.NonNullObservableField
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

/**
 * Types of events that might occur.
 */
enum class ForgotPasswordEvent {
    /**
     * Close the view.
     */
    FINISH,
    /**
     * Show success dialog.
     */
    SHOW_SUCCESS,
    /**
     * Show help dialog.
     */
    SHOW_HELP
}

/**
 * View model for [ForgotPasswordActivity].

 * @author Anton Holmberg
 */
class ForgotPasswordViewModel @Inject constructor(
        val repo: ForgotPasswordRepo,
        private val inputValidator: InputValidation
) : ViewModel() {

    /**
     * The provided email.
     */
    val email = NonNullObservableField<String>("")

    /**
     * If the view is loading.
     */
    val loading = MutableLiveData<Boolean>()

    /**
     * If an error occured.
     */
    val error = MutableLiveData<Throwable>()

    val inputError = ObservableField<String>("")

    /**
     * Stream of events that might occur.
     */
    val events = MutableLiveData<ForgotPasswordEvent>()

    val validationTextWatcher: TextWatcher = ValidateOnTextChangeListener()

    /**
     * Send reset password email to the provided [email].
     */
    fun sendEmail() {
        inputValidator.validateEmail(email.get()).let {
            inputError.set(it)
            if (it.isNullOrBlank()) {
                loading.value = true
                repo.sendEmail(email.get())
                        .doOnTerminate { loading.value = false }
                        .subscribeBy(
                                onComplete = {
                                    events.notify(ForgotPasswordEvent.SHOW_SUCCESS)
                                },
                                onError = {
                                    error.notify(it)
                                }
                        )
            }
        }
    }

    /**
     * When a menu item is clicked.
     */
    fun onMenuItemClicked(id: Int) =
            when (id) {
                R.id.help -> {
                    events.notify(ForgotPasswordEvent.SHOW_HELP)
                    true
                }
                else -> false
            }

    /**
     * When the back button is pressed.
     */
    fun onBackPressed() {
        events.notify(ForgotPasswordEvent.FINISH)
    }

    /**
     * Handle editor actions.
     */
    fun handleEditorAction(id: Int) = when (id) {
        EditorInfo.IME_ACTION_DONE -> {
            sendEmail()
            true
        }
        else -> false
    }

    inner class ValidateOnTextChangeListener : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if (!inputError.get().isNullOrEmpty()) {
                inputError.set(inputValidator.validateEmail(email.get()))
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
