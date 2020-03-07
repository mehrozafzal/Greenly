package greenely.greenely.home.ui.events

import android.view.View

internal class LoadingHandler(
        private val loader: View,
        private val scroll: View,
        var next: EventHandler? = null
) : EventHandler {
    override fun handleEvent(event: Event) {
        if (event is Event.LoaderUIEvent) {
            if (event.shouldShow) {
                scroll.visibility = View.INVISIBLE
                loader.visibility = View.VISIBLE
            } else {
                loader.visibility = View.GONE
                scroll.visibility = View.VISIBLE
            }
        } else {
            next?.handleEvent(event)
        }
    }
}