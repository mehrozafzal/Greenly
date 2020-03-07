package greenely.greenely.utils.validation

import android.app.Application
import android.os.Build
import android.telephony.PhoneNumberUtils
import greenely.greenely.OpenClassOnDebug
import greenely.greenely.R
import javax.inject.Inject

@OpenClassOnDebug
class PhoneNumberValidator @Inject constructor(private val application: Application) {
    fun validate(value: String): String? {
        return if (value.isEmpty() || value.isBlank()) {
            application.getString(R.string.phone_number_required)
        } else if (value.length != 10) {
            application.getString(R.string.phone_number_must_be_10_digits)
        } else if (!isValidPhoneNumber(value)) {
            application.getString(R.string.invalid_phone_number)
        } else if (!value.startsWith("07")) {
            application.getString(R.string.invalid_phone_number_start_with_07)
        } else {
            null
        }
    }

    private fun isValidPhoneNumber(value: String): Boolean {
        val formatted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            PhoneNumberUtils.formatNumber(value, "SE")
        } else {
            @Suppress("DEPRECATION")
            PhoneNumberUtils.formatNumber(value)
        }

        return formatted != null
    }
}
