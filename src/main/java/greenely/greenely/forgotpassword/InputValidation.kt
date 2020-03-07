package greenely.greenely.forgotpassword

import android.app.Application
import android.util.Patterns
import greenely.greenely.OpenClassOnDebug
import greenely.greenely.R
import javax.inject.Inject

@OpenClassOnDebug
class InputValidation @Inject constructor(private val application: Application) {

     fun validateEmail(email: String): String? =
            if (email.isEmpty() || email.isBlank()) {
                application.getString(R.string.email_empty_error_message)
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                application.getString(R.string.invalid_email_error_message)
            } else {
                null
            }

}
