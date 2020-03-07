package greenely.greenely.extensions

import android.app.Dialog
import android.content.Context
import androidx.appcompat.app.AlertDialog
import android.view.View
import android.view.inputmethod.InputMethodManager
import greenely.greenely.R
import greenely.greenely.api.models.ErrorMessage


/**
 * Configure an [AlertDialog] with a given [ErrorMessage]
 *
 * @param message The [ErrorMessage].
 */
fun AlertDialog.Builder.setErrorMessage(message: ErrorMessage): AlertDialog.Builder = this.apply {
    setTitle(message.title)
    setMessage(message.description)
    setPositiveButton(R.string.okay) { dialog, _ ->
        dialog.dismiss()
    }
}

/**
 * Configure an [AlertDialog] as a network error.
 */
fun AlertDialog.Builder.asNetworkError(): AlertDialog.Builder = this.apply {
    setTitle(context.getString(R.string.network_error_title))
    setMessage(context.getString(R.string.network_error_body))
    setPositiveButton(
            context.getString(R.string.okay)
    ) { dialog, _ ->
        dialog.dismiss()
    }
}

/**
 * Configure an [AlertDialog] as an unexpected error.
 */
fun AlertDialog.Builder.asUnexpectedError(): AlertDialog.Builder = this.apply {
    setTitle(context.getString(R.string.unexpected_error_title))
    setMessage(context.getString(R.string.unexpected_error_body))
    setPositiveButton(
            context.getString(R.string.okay)
    ) { dialog, _ ->
        dialog.dismiss()
    }
}



fun Dialog.hideKeyboard(view: View? = this.window?.currentFocus) {
    val inputManager = view?.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    inputManager?.hideSoftInputFromWindow(view.windowToken, 0)
}

