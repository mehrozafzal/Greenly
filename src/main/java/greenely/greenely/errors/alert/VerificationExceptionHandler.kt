package greenely.greenely.errors.alert

import android.app.Activity
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import greenely.greenely.R
import greenely.greenely.errors.ErrorHandler
import greenely.greenely.errors.exceptions.VerificationException

class VerificationExceptionHandler(
        private val activity: Activity,
        var next: ErrorHandler? = null,
        private val builder: AlertDialog.Builder = AlertDialog.Builder(activity, R.style.AlertDialogTheme)
) : ErrorHandler {
    override fun handleError(error: Throwable) {
        if (error is VerificationException) {
            builder.apply {
                setTitle(R.string.invalid_input)
                setMessage(error.message)
                setPositiveButton(R.string.okay) { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                }
                show()
            }
        } else {
            next?.handleError(error)
        }
    }
}

