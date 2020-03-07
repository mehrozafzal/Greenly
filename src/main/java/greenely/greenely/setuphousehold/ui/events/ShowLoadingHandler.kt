package greenely.greenely.setuphousehold.ui.events

import android.view.View

internal class ShowLoadingHandler(
        private val loader: View,
        var next: EventHandler? = null
) : EventHandler {
    override fun handleEvent(event: Event) {
        if (event is Event.ShowLoader) {
            if (event.shouldShow) loader.visibility = View.VISIBLE
            else loader.visibility = View.GONE
        } else {
            next?.handleEvent(event)
        }
    }
}