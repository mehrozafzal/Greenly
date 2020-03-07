package greenely.greenely.errors.snackbar

import android.content.Context
import com.google.android.material.snackbar.Snackbar
import greenely.greenely.R
import greenely.greenely.api.models.ErrorMessage
import greenely.greenely.errors.ErrorHandler
import greenely.greenely.errors.HasSnackbarView
import retrofit2.HttpException

class HttpExceptionHandler(
        private val hasSnackbarView: HasSnackbarView,
        var next: ErrorHandler? = null
) : ErrorHandler {

    override fun handleError(error: Throwable) {
        if (error is HttpException) {
            val body = extractBodyFromError(error)
            showErrorSnackbar(body)
        } else {
            next?.handleError(error)
        }
    }

    private fun extractBodyFromError(error: HttpException): String {
        return if (error.code() in 400..499) {
            extractBodyFromResponse(error)
        } else {
            unexpectedErrorBody
        }
    }

    private fun extractBodyFromResponse(error: HttpException): String {
        val errorMessage = ErrorMessage.fromError(error)
        val body = errorMessage.bodyOrUnexpected()

        return body
    }

    private fun ErrorMessage?.bodyOrUnexpected(): String =
            this?.description ?: unexpectedErrorBody

    private val unexpectedErrorBody: String
        get() = context.getString(R.string.unexpected_error_body)

    private val context: Context
        get() = hasSnackbarView.snackbarView.context

    private fun showErrorSnackbar(body: String) {
        Snackbar.make(hasSnackbarView.snackbarView, body, Snackbar.LENGTH_LONG).show()
    }
}