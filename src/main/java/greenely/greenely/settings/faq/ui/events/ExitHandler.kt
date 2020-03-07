package greenely.greenely.settings.faq.ui.events

import greenely.greenely.settings.faq.ui.FaqActivity

class ExitHandler(
        private val activity: FaqActivity,
        var next: EventHandler? = null
) : EventHandler {
    override fun handleEvent(event: Event) {
        if (event is Event.Exit) {
            activity.finish()
        } else {
            next?.handleEvent(event)
        }
    }
}

