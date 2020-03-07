package greenely.greenely.setuphousehold.ui.events

import android.app.Activity
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import greenely.greenely.R

internal class AbortHandler(
        private val activity: Activity, var next: EventHandler? = null
) : EventHandler {
    override fun handleEvent(event: Event) {
        if (event is Event.Abort) {
            AlertDialog.Builder(activity, R.style.AlertDialogTheme)
                    .setTitle(activity.getString(R.string.are_you_sure_abort_title))
                    .setMessage(activity.getString(R.string.are_you_sure_abort_message))
                    .setPositiveButton(R.string.no) { dialog: DialogInterface, _ ->
                        dialog.dismiss()
                    }
                    .setNegativeButton(R.string.yes) { _, _: Int ->
                        activity.finish()
                    }
                    .create()
                    .show()

        } else {
            next?.handleEvent(event)
        }
    }
}