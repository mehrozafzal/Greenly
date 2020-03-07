package greenely.greenely.errors.snackbar

import com.google.android.material.snackbar.Snackbar
import greenely.greenely.R
import greenely.greenely.errors.ErrorHandler
import greenely.greenely.errors.HasSnackbarView
import java.io.IOException

class IOExceptionHandler(
        private val hasSnackbarView: HasSnackbarView,
        var next: ErrorHandler? = null
) : ErrorHandler {
    override fun handleError(error: Throwable) {
        if (error is IOException) {
            Snackbar.make(
                    hasSnackbarView.snackbarView,
                    R.string.network_error_body,
                    Snackbar.LENGTH_LONG
            ).show()
        } else {
            next?.handleError(error)
        }
    }
}