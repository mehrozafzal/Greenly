package greenely.greenely.solaranalysis.ui.householdinfo.events

import android.util.Log
import greenely.greenely.errors.ErrorHandler

internal class ShowErrorHandler(
        private val errorHandler: ErrorHandler,
        var next: EventHandler? = null
) : EventHandler {
    override fun handleEvent(event: Event) {
        if (event is Event.ShowError) {
            Log.e("Error", "Error", event.error)
            errorHandler.handleError(event.error)
        } else {
            next?.handleEvent(event)
        }
    }
}
