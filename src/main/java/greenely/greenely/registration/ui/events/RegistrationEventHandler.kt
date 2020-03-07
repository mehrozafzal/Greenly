package greenely.greenely.registration.ui.events

import greenely.greenely.registration.ui.RegistrationActivity
import javax.inject.Inject

class RegistrationEventHandler @Inject constructor(activity: RegistrationActivity) : EventHandler {

    private val eventHandler by lazy {
        val hideKeyboardHandler = HideKeyboardHandler(activity)

        hideKeyboardHandler
    }

    override fun handleEvent(event: Event) {
        eventHandler.handleEvent(event)
    }
}

