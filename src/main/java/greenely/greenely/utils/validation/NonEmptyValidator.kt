package greenely.greenely.utils.validation

import android.app.Application
import greenely.greenely.R
import javax.inject.Inject

class NonEmptyValidator @Inject constructor(private val application: Application) {
    fun validate(value: String): String? {
        return if (value.isEmpty() || value.isBlank()) {
            application.getString(R.string.can_not_be_empty)
        } else {
            null
        }
    }
}
