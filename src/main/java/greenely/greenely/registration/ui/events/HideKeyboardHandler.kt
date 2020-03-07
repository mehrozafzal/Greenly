package greenely.greenely.registration.ui.events

import greenely.greenely.extensions.hideKeyboard
import greenely.greenely.registration.ui.RegistrationActivity

class HideKeyboardHandler(
        private val activity: RegistrationActivity,
        var next: EventHandler? = null
) : EventHandler {
    override fun handleEvent(event: Event) {
        if (event is Event.HideKeyboard) {
            activity.hideKeyboard()
        } else {
            next?.handleEvent(event)
        }
    }
}

