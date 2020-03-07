package greenely.greenely.errors.alert

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import greenely.greenely.R
import greenely.greenely.errors.ErrorHandler
import greenely.greenely.extensions.asUnexpectedError

class AnyExceptionHandler(
        private val activity: Activity
) : ErrorHandler {
    override fun handleError(error: Throwable) {
        AlertDialog.Builder(activity, R.style.AlertDialogTheme)
                .asUnexpectedError().create().show()
    }
}
