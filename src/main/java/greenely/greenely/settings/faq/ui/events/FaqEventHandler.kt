package greenely.greenely.settings.faq.ui.events

import greenely.greenely.settings.faq.ui.FaqActivity
import javax.inject.Inject

class FaqEventHandler @Inject constructor(activity: FaqActivity) : EventHandler {
    private val eventHandler by lazy {
        ExitHandler(activity)
    }

    override fun handleEvent(event: Event) {
        eventHandler.handleEvent(event)
    }
}

