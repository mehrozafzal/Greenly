package greenely.greenely.utils.validation

import android.app.Application
import android.util.Patterns
import greenely.greenely.OpenClassOnDebug
import greenely.greenely.R
import javax.inject.Inject

@OpenClassOnDebug
class EmailValidator @Inject constructor(private val application: Application) {
    fun validate(value: String): String? {
        return if (value.isEmpty() || value.isBlank()) {
            application.getString(R.string.email_empty_error_message)
        } else if (!Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
            application.getString(R.string.invalid_email_error_message)
        } else {
            null
        }
    }
}

