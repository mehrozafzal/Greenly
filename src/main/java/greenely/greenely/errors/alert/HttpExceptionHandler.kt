package greenely.greenely.errors.alert

import android.app.Activity
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import greenely.greenely.R
import greenely.greenely.api.models.ErrorMessage
import greenely.greenely.errors.ErrorHandler
import retrofit2.HttpException


class HttpExceptionHandler(
        private val activity: Activity,
        var next: ErrorHandler? = null
) : ErrorHandler {
    private data class TitleBody(val title: String, val body: String)

    override fun handleError(error: Throwable) {
        if (error is HttpException) {
            try{
                val (title, body) = extractTitleBodyFromError(error)
                showErrorDialog(title, body)
            }catch (e: Exception){
                showErrorDialog(unexpectedErrorTitle, unexpectedErrorBody)
            }

        } else {
            next?.handleError(error)
        }
    }

    private fun extractTitleBodyFromError(error: HttpException): TitleBody {
        return if (error.code() in 400..499) {
            extractTitleBodyFromResponse(error)
        } else {
            unexpectedTitleBody
        }
    }

    private fun extractTitleBodyFromResponse(error: HttpException): TitleBody {
        val errorMessage = ErrorMessage.fromError(error)
        val title = errorMessage.titleOrUnexpected()
        val body = errorMessage.bodyOrUnexpected()

        return TitleBody(title, body)
    }

    private val unexpectedTitleBody: TitleBody
        get() = TitleBody(unexpectedErrorTitle, unexpectedErrorBody)

    private fun ErrorMessage?.titleOrUnexpected(): String =
            this?.title ?: unexpectedErrorTitle

    private fun ErrorMessage?.bodyOrUnexpected(): String =
            this?.description ?: unexpectedErrorBody

    private val unexpectedErrorTitle: String
        get() = activity.getString(R.string.unexpected_error_title)

    private val unexpectedErrorBody: String
        get() = activity.getString(R.string.unexpected_error_body)

    private fun showErrorDialog(title: String, body: String) {
        AlertDialog.Builder(activity, R.style.AlertDialogTheme)
                .setTitle(title)
                .setMessage(body)
                .setPositiveButton(R.string.okay) { dialog: DialogInterface, _: Int ->
                    dialog.dismiss()
                }
                .create()
                .show()
    }
}
