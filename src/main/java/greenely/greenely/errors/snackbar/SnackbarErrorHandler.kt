package greenely.greenely.errors.snackbar

import com.google.android.material.snackbar.Snackbar
import greenely.greenely.R
import greenely.greenely.errors.ErrorHandler
import greenely.greenely.errors.HasSnackbarView

class AnyExceptionHandler(
        private val hasSnackbarView: HasSnackbarView
) : ErrorHandler {
    override fun handleError(error: Throwable) {
        Snackbar.make(
                hasSnackbarView.snackbarView,
                R.string.unexpected_error_body,
                Snackbar.LENGTH_LONG
        ).show()
    }
}

