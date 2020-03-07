package greenely.greenely.login.ui.validation

import android.app.Application
import android.util.Patterns
import greenely.greenely.OpenClassOnDebug
import greenely.greenely.R
import greenely.greenely.login.model.LoginData
import greenely.greenely.login.model.LoginErrorModel
import javax.inject.Inject

@OpenClassOnDebug
class LoginInputValidation @Inject constructor(private val application: Application) {
    fun validate(input: LoginData): LoginErrorModel  {
        return LoginErrorModel(
                validateEmail(input.email) ?: "",
                validatePassword(input.password) ?: ""
        )
    }

    private fun validateEmail(email: String): String? =
            if (email.isEmpty() || email.isBlank()) {
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