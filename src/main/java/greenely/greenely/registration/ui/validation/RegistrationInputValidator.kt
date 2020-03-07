package greenely.greenely.registration.ui.validation

import android.app.Application
import android.util.Patterns
import greenely.greenely.OpenClassOnDebug
import greenely.greenely.R
import greenely.greenely.registration.ui.models.RegistrationErrorModel
import greenely.greenely.registration.ui.models.RegistrationInputModel
import javax.inject.Inject

@OpenClassOnDebug
class RegistrationInputValidator @Inject constructor(private val application: Application) {
    fun validate(input: RegistrationInputModel): RegistrationErrorModel {
        return RegistrationErrorModel(
                validateEmail(input.email.get()) ?: "",
                validatePassword(input.password.get()) ?: ""
        )
    }

    private fun validateEmail(email: String): String? =
            if (email.isEmpty()) {
                application.getString(R.string.email_empty_error_message)
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                application.getString(R.string.invalid_email_error_message)
            } else {
                null
            }

    private fun validatePassword(password: String): String? =
            if (password.isEmpty()) {
                application.getString(R.string.password_empty_error_message)
            } else {
                null
            }

}

