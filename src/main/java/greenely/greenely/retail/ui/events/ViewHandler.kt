package greenely.greenely.retail.ui.events

import android.view.View

internal class ViewHandler(
        private val container: View,
        var next: EventHandler? = null
) : EventHandler {
    override fun handleEvent(event: Event) {
        if (event is Event.VisibilityOfViewUIEvent) {
            if (event.shouldShow) {
                container.visibility = View.VISIBLE
            } else {
                container.visibility = View.INVISIBLE
            }
        } else {
            next?.handleEvent(event)
        }
    }
}
