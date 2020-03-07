package greenely.greenely.errors.alert

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import greenely.greenely.R
import greenely.greenely.errors.ErrorHandler
import greenely.greenely.extensions.asNetworkError
import java.io.IOException

class IOExceptionHandler(
        private val activity: Activity,
        var next: ErrorHandler? = null
) : ErrorHandler {
    override fun handleError(error: Throwable) {
        if (error is IOException) {
            AlertDialog.Builder(activity, R.style.AlertDialogTheme)
                    .asNetworkError().create().show()
        } else {
            next?.handleError(error)
        }
    }
}
